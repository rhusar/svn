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
package org.smartfrog.services.anubis.partition.test.mainconsole;


import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.smartfrog.services.anubis.Anubis;
import org.smartfrog.services.anubis.basiccomms.multicasttransport.MulticastAddress;
import org.smartfrog.services.anubis.basiccomms.multicasttransport.MulticastAddressData;
import org.smartfrog.services.anubis.partition.test.colors.ColorAllocator;
import org.smartfrog.services.anubis.partition.util.Config;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.views.BitView;
import org.smartfrog.services.anubis.partition.wire.msg.HeartbeatMsg;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Timer;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

public class Controller
        extends CompoundImpl
        implements Compound {

    private class ExpirationTimer extends Timer {
        ExpirationTimer(long interval) { super(interval); }
        protected void timerTick() { checkNodes(); }
    }

    private long                expirePeriod;
    private long                checkPeriod;
    private MainConsoleFrame    consoleFrame;
    private Snoop               snoop;
    private Identity            id;
    private Map                 nodes              = new HashMap();
    private BitView             globalView         = new BitView();
    private ExpirationTimer     expirationTimer;
    private Reference           componentReference;
    private ColorAllocator      colorAllocator     = new ColorAllocator();

    private AsymetryReportFrame asymetryReport     = null;


    public Controller() throws RemoteException {
        super();
    }

    public void sfDeploy() throws SmartFrogException, RemoteException  {
       try {
        super.sfDeploy();
        componentReference = sfCompleteName();
        id                       = Config.getIdentity(this, "identity");
        expirePeriod             = Config.getLong(this, "expirePeriod");
        checkPeriod              = Config.getLong(this, "checkPeriod");
        MulticastAddress address = ((MulticastAddressData)sfResolve("heartbeatGroup")).getMulticastAddress();
        expirationTimer          = new ExpirationTimer(checkPeriod);
        snoop                    = new Snoop("Anubis: Partition Manager Test Console heartbeat snoop", address, id, this);
        consoleFrame             = new MainConsoleFrame(this);
        consoleFrame.setTitle("Partition Manager Test Controller - " + Anubis.version);
        consoleFrame.initialiseTiming(
                Config.getLong(this, "heartbeatInterval"),
                Config.getLong(this, "heartbeatTimeout"));
       } catch (Exception ex){
            throw (SmartFrogException)SmartFrogException.forward(ex);
       }
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException  {
        super.sfStart();
        snoop.start();
        expirationTimer.start();
        consoleFrame.setVisible(true);
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        snoop.shutdown();
        expirationTimer.stop();
        clearNodes();
        consoleFrame.setVisible(false);
        consoleFrame.dispose();
        consoleFrame = null;
        super.sfTerminateWith(status);
    }

    public synchronized void terminate() {
        sfTerminate(TerminationRecord.normal(
                "Termination from within Partition Manager test console",
                componentReference) );
    }


    public synchronized void deliverHeartbeat(HeartbeatMsg hb) {
        NodeData nodeData = (NodeData)nodes.get(hb.getSender());
        if( nodeData != null ) {
            nodeData.heartbeat(hb);
            return;
        } else {
            nodeData = new NodeData(hb, consoleFrame, colorAllocator, this);
            nodes.put(hb.getSender(), nodeData);
            globalView.add(hb.getSender());
        }
    }


    public synchronized void deliverObject(Object obj, NodeData node) {
        node.deliverObject(obj);
    }


    public synchronized void checkNodes() {
        long timeNow = System.currentTimeMillis();
        long expireTime = timeNow - expirePeriod;
        Iterator iter = nodes.values().iterator();
        while( iter.hasNext() ) {
            NodeData nodeData = (NodeData)iter.next();
            if( nodeData.olderThan(expireTime) ) {
                iter.remove();
                globalView.remove(nodeData.getIdentity());
                nodeData.removeNode();
            }
        }
    }

    public synchronized void clearNodes() {
        Iterator iter = nodes.values().iterator();
        while( iter.hasNext() ) {
            NodeData nodeData = (NodeData)iter.next();
            nodeData.removeNode();
            iter.remove();
        }
        globalView = new BitView();
    }


    public synchronized void disconnectNode(NodeData node) {
        node.disconnected();
    }

    public synchronized void setTiming(long interval, long timeout) {

        /** set local timers **/
        checkPeriod = interval;
        expirePeriod = interval * timeout;
        expirationTimer.setTickDelay(checkPeriod);

        /** set node's timers **/
        Iterator iter = nodes.values().iterator();
        while( iter.hasNext() )
            ((NodeData)iter.next()).setTiming(interval, timeout);
    }


    public synchronized void asymPartition(String nodeStr) {

        StringTokenizer tokens = new StringTokenizer(nodeStr);
        BitView partition = new BitView();
        String token = "";

        if( tokens.countTokens() == 0 )
            return;

        try {
            while( tokens.hasMoreTokens() ) {
                token = tokens.nextToken();
                partition.add(new Integer(token).intValue());
            }
        }
        catch (NumberFormatException ex) {
            consoleFrame.inputError("Unknown: " + token);
            return;
        }

        Iterator iter = nodes.values().iterator();
        while( iter.hasNext() ) {
            NodeData node = (NodeData)iter.next();
            if( partition.contains(node.getIdentity()) )
                node.setIgnoringAsymPartition(globalView, partition);
        }
    }

    public synchronized void symPartition(String nodeStr) {

        StringTokenizer tokens = new StringTokenizer(nodeStr);
        BitView partition = new BitView();
        String token = "";

        if( tokens.countTokens() == 0 )
            return;

        try {
            while( tokens.hasMoreTokens() ) {
                token = tokens.nextToken();
                partition.add(new Integer(token).intValue());
            }
        }
        catch (NumberFormatException ex) {
            consoleFrame.inputError("Unknown: " + token);
            return;
        }

        Iterator iter = nodes.values().iterator();
        while( iter.hasNext() ) {
            ((NodeData)iter.next()).setIgnoringSymPartition(globalView, partition);
        }
    }

    public synchronized void clearPartitions() {
        Iterator iter = nodes.values().iterator();
        while( iter.hasNext() )
            ((NodeData)iter.next()).setIgnoring(new BitView());
    }

    public synchronized void showAsymetryReport() {
        if( asymetryReport == null )
            asymetryReport = new AsymetryReportFrame(this, nodes, id);
        else
            asymetryReport.recalculate(nodes);
    }

    public synchronized void removeAsymetryReport() {
        asymetryReport = null;
    }


    public String nodesToString() {
        String str = "Nodes Table\n===========\n";
        Iterator iter = nodes.entrySet().iterator();
        while( iter.hasNext() ) {
            Map.Entry entry = (Map.Entry)iter.next();
            str += entry.getKey() + "--->" + entry.getValue();
        }
        return str;
    }


}
