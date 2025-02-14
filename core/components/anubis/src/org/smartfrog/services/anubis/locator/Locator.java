/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.anubis.locator;


import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.smartfrog.services.anubis.Anubis;
import org.smartfrog.services.anubis.locator.msg.RegisterMsg;
import org.smartfrog.services.anubis.locator.registers.GlobalRegisterImpl;
import org.smartfrog.services.anubis.locator.registers.LocalRegisterImpl;
import org.smartfrog.services.anubis.locator.registers.StabilityQueue;
import org.smartfrog.services.anubis.locator.util.ActiveTimeQueue;
import org.smartfrog.services.anubis.partition.Partition;
import org.smartfrog.services.anubis.partition.PartitionNotification;
import org.smartfrog.services.anubis.partition.comms.MessageConnection;
import org.smartfrog.services.anubis.partition.util.Config;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.views.View;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class Locator
        extends PrimImpl
        implements Prim, PartitionNotification, AnubisLocator {

    private class InstanceGenerator {
        private long counter = 0;
        public String instance() { return myId.id + "/" + myId.epoch + "/" + Long.toString(counter++); }
    }

    private Partition          partition         = null;
    public  GlobalRegisterImpl global            = null;    // public for debug
    public  LocalRegisterImpl  local             = null;    // public for debug
    private Map                links             = new HashMap();
    private Identity           myId              = null;
    public  Integer            me                = null;    // public for debug
    private Integer            leader            = null;
    private boolean            stable            = false;
    private ActiveTimeQueue    timers            = null;    // public for debug
    public  ThreadLocal        callingThread     = new ThreadLocal();
    private long               maxTransDelay;
    private InstanceGenerator  instanceGenerator = new InstanceGenerator();
    private Random             random;
    private LogSF              log               = sfLog();

    private StabilityQueue     stabilityQueue    = new StabilityQueue() {
        public void doit(View v, int l) { partitionNotificationImpl(v, l); }
    };

    /**
     * Constructor
     */
    public Locator() throws RemoteException { super(); }

    /**
     * Prim interface
     */
    public void sfDeploy() throws SmartFrogException, RemoteException  {
        try {
            super.sfDeploy();

            partition         = (Partition)sfResolve("partitionManager");
            myId              = (Identity)Config.getIdentity((Prim)partition, "identity");
            me                = new Integer(myId.id);
            long hbtimeout    = sfResolve("heartbeatTimeout", (long)0, true);
            long hbinterval   = sfResolve("heartbeatInterval", (long)0, true);
            maxTransDelay     = hbtimeout * hbinterval;
            timers            = new ActiveTimeQueue("Anubis: Locator timers (node " + me + ")");
            global            = new GlobalRegisterImpl(myId, this);
            local             = new LocalRegisterImpl(myId, this);
            random            = new Random(System.currentTimeMillis() + 1966*me.longValue());

            global.start();
            local.start();
            timers.start();
            stabilityQueue.start();
            partition.register(this);
        }
        catch (Exception ex) {
            if( log != null )
                log.error("failed to deploy", ex);
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }
    public void sfStart() throws SmartFrogException, RemoteException  {
        try {
            super.sfStart();
            if( log.isInfoEnabled() )
                log.info("Locator started - " + Anubis.version);
        }
        catch (Exception ex) {
            if( log.isErrorEnabled() )
                log.error("failed to start", ex);
        }
    }
    public void sfTerminateWith(TerminationRecord status) {
        if( log.isInfoEnabled() )
            log.info("Terminating Locator");
        stabilityQueue.terminate();
        global.terminate();
        local.terminate();
        timers.terminate();
        log = null;
        super.sfTerminateWith(status);
    }

    /**
     * AnubisLocator interface
     *
     */
    public void registerProvider(AnubisProvider provider) {
        /**
         * set the time of registration and create an instance (UID) for this
         * provider
         */
        provider.setAnubisData(this, System.currentTimeMillis(), instanceGenerator.instance());
        local.registerProvider(provider);
    }
    public void deregisterProvider(AnubisProvider provider) {
        local.deregisterProvider(provider);
    }
    public void registerListener(AnubisListener listener) {
        listener.setTimerQueue(timers);
        local.registerListener(listener);
    }
    public void deregisterListener(AnubisListener listener) {
        local.deregisterListener(listener);
        // don't set timers to null in listener
    }

    public void newProviderValue(AnubisProvider provider) {
        local.newProviderValue(provider);
    }
    /**
     * registerStability registers an interface for stability notifications.
     * This interface is called to inform the user when the local partition
     * becomes stable or unstable.
     */
    public void registerStability(AnubisStability stability) {
        stability.setTimerQueue(timers);
        local.registerStability(stability);
    }
    /**
     * deregisterStability deregisters a stability notification iterface.
     */
    public void deregisterStability(AnubisStability stability) {
        local.deregisterStability(stability);
    }

    /**
     * This is a temporary fix for a deadlock bug. The implementation of links
     * maintenance (calls to send(), clearConnections() and dropBrokenConnections())
     * is brain dead in that it can cause a deadlock because upcoming notifications
     * from the partitionManager may deadlock with downward send() invocations from
     * the registers. Making partitionNotification() asynchronous by queueing
     * the requests will avoid the deadlock. Later the implementation of
     * request servicing and link maintenance should be examined.
     * @param view
     * @param leader
     */
    public void partitionNotification(View view, int leader) {
        stabilityQueue.put(view, leader);
    }
    /**
     * PartitionNotification interface
     */
    public void partitionNotificationImpl(View view, int leader) {

        /**
         * Keep record of the current stability and which node is leader. The
         * leader holds the active global register.
         */
        this.stable = view.isStable();
        this.leader = new Integer(leader);

        /**
         * The view will only be the same or larger when a stable report
         * is received. If it was stable already it may have jumped to
         * a larger view that has become stable. If it was unstable, then again
         * the new one will be the same or larger. Accordingly, we can not lose
         * any nodes in becoming stable, however, a different node may have
         * over as leader - so the global may have moved. So......
         *
         * ....if the global has changed we need to start using the new one.
         */
        if( view.isStable() ) {

            /**
             * No point in putting jitter in at the global because it
             * doesn't send messages at stability.
             */
            global.stable( leader );

            /**
             * Introduce jitter to prevent the nodes all hitting the leader
             * at the same time. This should really have a max delay
             * proportional to the heartbeat interval.
             */
            try { wait((long)random.nextInt(1000)); }
            catch(Exception ex) { }
            local.stable( leader, view.getTimeStamp() );
        }

        /**
         * If a view is unstable it may be the same size as previously (in the
         * case that a node has been added) or it may be smaller (in the case
         * that a node has been removed). If the view shrinks all nodes will
         * become unstable. Accordingly, some providers may have become
         * inaccessible (absent) if they are on nodes that have vanished. Also
         * the global register may have been lost. So....
         *
         * ....check if the global has changed and check for lost providers.
         */
        else {
            global.unstable( leader );
            local.unstable(view);
            dropBrokenConnections(view);
        }
    }


    /**
     * PartitionNotification interface.
     * delivery of a message from the partition transport
     *
     * @param obj - the message
     * @param sender - the sending node
     * @param time - the time sent
     */
    public void objectNotification(Object obj, int sender, long time) {

        /**
         * As a sanity check see that it is a valid message type.
         * Then pass to the approriate register.
         */
        if( obj instanceof RegisterMsg )
            deliverRequest( (RegisterMsg)obj );
        else if( log.isErrorEnabled() )
            log.error("Locator received un-recognised message " +
                      obj + " in objectNotificaiton");
    }

    /**
     * Deliver a request to the locator.
     *
     * @param msg
     */
    private void deliverRequest(RegisterMsg msg) {
        if( msg.register == RegisterMsg.GlobalRegister )
            global.deliverRequest(msg);
        else
            local.deliverRequest(msg);
    }

    /**
     * The locator manages connections between nodes. Connections are created
     * on demand in the send method. If the recipient drops out of the partition
     * during the connect() call we will get a null connection - in that case
     * just do nothing.
     *
     * @param obj
     * @param node
     */
    private void send(Object obj, Integer node) {
        synchronized( links ) {
            MessageConnection con = (MessageConnection)links.get(node);
            if( con == null ) {
                con = partition.connect(node.intValue());
                if( con == null ) return;
                links.put(node, con);
            }
            con.sendObject(obj);
        }
    }


    /**
     * Remove all connections managed by the locator.
     */
    private void clearConnections() {
        synchronized( links ) {
            Iterator iter = links.values().iterator();
            while( iter.hasNext() )
                ((MessageConnection)iter.next()).disconnect();
            links.clear();
        }
    }

    /**
     * Drop connections to nodes that are not in the view - they will
     * be broken.
     *
     * @param v
     */
    private void dropBrokenConnections(View v) {
        synchronized( links ) {
            Iterator iter = links.entrySet().iterator();
            while( iter.hasNext() ) {
                Map.Entry entry = (Map.Entry)iter.next();
                Integer node = (Integer)entry.getKey();
                if( !v.contains( node.intValue() ) ) {
                    ((MessageConnection)entry.getValue()).disconnect();
                    iter.remove();
                }
            }
        }
    }

    /**
     * Handle deliver of messages to the global register. If the partition is
     * not stable the message will be dropped. Access to the global register
     * is suspended during periods of instability because it may move. This
     * constraint will be relaxed in a later version. If the leader is this
     * node then this nodes global register is the active one, so bypass the
     * comms and deliver directly to the global register.
     *
     * @param msg
     */
    public void sendToGlobal(RegisterMsg msg) {
        if( stable ) {
            if( leader.equals(me) ) {

                global.deliverRequest(msg);

            } else {

                send(msg, leader);

            }
        } else {

            if( log.isTraceEnabled() )
                log.info("Due to instability I am _NOT_ Sending " + msg + " to global register");

        }
    }


    /**
     * Handle delivery of messages to local registers. Messages are delivered
     * to peer local registers at any time, regardless of partition stability.
     * If the target node is this one then bypass the communications and
     * deliver directly to the local register.
     *
     * @param msg
     * @param node
     */
    public void sendToLocal(RegisterMsg msg, Integer node) {

         if( node.equals(me) ) {

            local.deliverRequest(msg);

        } else {

            send(msg, node);
        }
    }

    public ActiveTimeQueue getTimeQueue() {
        return timers;
    }

    public long getmaxDelay() {
        return maxTransDelay;
    }

}
