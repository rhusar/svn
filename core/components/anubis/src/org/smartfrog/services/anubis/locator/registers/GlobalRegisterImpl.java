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
package org.smartfrog.services.anubis.locator.registers;


import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.smartfrog.services.anubis.locator.Locator;
import org.smartfrog.services.anubis.locator.msg.RegisterMsg;
import org.smartfrog.services.anubis.locator.names.ListenerProxy;
import org.smartfrog.services.anubis.locator.names.ProviderProxy;
import org.smartfrog.services.anubis.locator.util.BlockingQueue;
import org.smartfrog.services.anubis.locator.util.DebugFrame;
import org.smartfrog.services.anubis.locator.util.SetMap;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.views.View;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

public class GlobalRegisterImpl {

    /**
     * RequestServer is required to avoid a potential deadlock
     * between the local and global if they send messages to each
     * other on the local node. Sending a message to the local node
     * results in direct delivery by method call in the same thread.
     * It is possible for a thread that holds the GlobalRegisterImpl monitor
     * to make a call to the LocalRegisterImpl, and vice versa at the same time.
     * So, instead of blocking on a monitor we create a queue of requests for
     * the global and service the queue with a single thread.
     */
    private class RequestServer extends Thread {
        private boolean running = false;
        public RequestServer() { super(); }
        public void run() {
            running = true;
            while( running ) {
                Object obj = requests.get();
                if( obj!= null ) deliver( (RegisterMsg)obj );
            }
        }
        public void terminate() { running = false; }
    }

    private Locator          locator         = null;
    private int              me              = -1;
    private boolean          active          = true;
    private BlockingQueue    requests        = new BlockingQueue();
    private RequestServer    server          = new RequestServer();
    private SetMap           providersByName = new SetMap();
    private SetMap           providersByNode = new SetMap();
    private SetMap           listenersByName = new SetMap();
    private SetMap           listenersByNode = new SetMap();

    private DebugFrame       debug           = null;
    private LogSF            log             = LogFactory.getLog(this.getClass().toString());


    /**
     * Constructor - sets the local
     * @param id
     * @param locator
     */
    public GlobalRegisterImpl(Identity id, Locator locator) {
        this.me             = id.id;
        this.locator        = locator;
    }

    /**
     * Starts the global register server
     */
    public void start() {
        server.setName("Anubis: Global Register Request Server (node " + me + ")");
        server.start();
        updateDebugFrame();
     }

    /**
     * Stop the threads associated with the global register (the request
     * server thread) and deactivate the global.
     */
    public void terminate() {
        server.terminate();
        requests.deactivate();
    }

    /**
     * Set this global register to be active. When active the register will
     * process incoming requests.
     */
    public void activate() {
        if( active ) {
            if( log.isErrorEnabled() )
                log.error(me + " *** Global.activate called when active!!!");
        } else {
            active = true;
            updateDebugFrame();
        }
    }

    /**
     * deactivate: simply drop all info. The new global will rebuild from
     *             the local registers. Any new requests will be dropped
     *             when not active.
     */
    public void deactivate() {
        if( !active )
            return;
        active = false;
        providersByName.clear();
        providersByNode.clear();
        listenersByName.clear();
        listenersByNode.clear();
        updateDebugFrame();
    }



    /**
     * Removes registrations for listeners and providers that are located
     * at a node absent from the view provided.
     *
     * @param view
     */
    public void checkNodes(View view) {

        /**
         * Check providers for nodes that are not in the view
         */
        Iterator piter = providersByNode.keySet().iterator();
        while( piter.hasNext() ) {
            Integer node = (Integer)piter.next();
            if( !view.contains(node.intValue()) ) {
                Iterator iter = providersByNode.getSet(node).iterator();
                while( iter.hasNext() ) {
                    ProviderProxy provider = (ProviderProxy)iter.next();
                    providersByName.remove(provider);
                }
                piter.remove();
            }
        }

        /**
         * Check listeners for nodes that are not in the view
         */
        Iterator liter = listenersByNode.keySet().iterator();
        while( liter.hasNext() ) {
            Integer node = (Integer)liter.next();
            if( !view.contains(node.intValue()) ) {
                Iterator iter = listenersByNode.getSet(node).iterator();
                while( iter.hasNext() ) {
                    ListenerProxy listener = (ListenerProxy)iter.next();
                    listenersByName.remove(listener);
                }
                liter.remove();
            }
        }
    }


    /**
     * When becoming stable there are four cases:
     * - was leader (active) and still leader:           do nothing
     * - was not leader (inactive) and still not leader: do nothing
     * - was leader (active) and now not leader:         deativate
     * - was not leader (inactive) and now leader:       activate
     * @param leader
     */
    public void stable( int leader ) {
        if( active && (leader != me) ) {
            deactivate();
        } else if( !active && (leader == me) ) {
            activate();
        }
    }


    /**
     * When becoming unstable there are four cases:
     * - was leader (active) and still leader:           reset (deactivate+activate)
     * - was leader (active) and not now leader:         deactivate
     * - was not leader (inactive) and now leader:       activate
     * - was not leader (inactive) and still not leader: do nothing
     * @param leader
     */
    public void unstable( int leader ) {

        /**
         * deal with changes in leader (implies changes in global register location)
         */
        if( active && (leader == me) ) {
            deactivate();
            activate();
        } else if( active && (leader != me) ) {
            deactivate();
        } else if( !active && (leader == me) ) {
            activate();
        } else if( !active && (leader != me) ) {
            // do nothing
        }
    }

    /**
     * If this global register is active queue the request in the request
     * queue. If it is not active just return.
     *
     * @param request
     */
    public void deliverRequest(RegisterMsg request) {
        requests.put(request);
    }

    /**
     * call the appropirate method to execute the request. This method is called
     * by the request server after pulling a request off of the requests queue.
     *
     * @param request
     */
    public void deliver(RegisterMsg request) {

        switch( request.type ) {
            case RegisterMsg.Undefined:

                if( log.isErrorEnabled() )
                    log.error(me + " Global received request explicitly declared as type Undefined " + request + " ?!?!?");
                break;

            case RegisterMsg.RegisterProvider:

                registerProvider( (ProviderProxy)request.data );
                updateDebugFrame();
                break;

            case RegisterMsg.DeregisterProvider:

                deregisterProvider( (ProviderProxy)request.data );
                updateDebugFrame();
                break;

            case RegisterMsg.RegisterListener:

                registerListener( (ListenerProxy)request.data );
                updateDebugFrame();
                break;

            case RegisterMsg.DeregisterListener:

                deregisterListener( (ListenerProxy)request.data );
                updateDebugFrame();
                break;

            default:
                if( log.isErrorEnabled() )
                    log.error(me + " Global received unexpected message " + request + " ?!?!?");
        }
    }


    /**
     * registerProvider: Add the new provider to the provider list.
     *                   Inform pending listeners of the location.
     * @param local
     * @param name
     * @return
     * @throws RemoteException
     */
    private void registerProvider(ProviderProxy provider) {

        /**
         * Add to provider info
         */
        providersByNode.put(provider.node, provider);
        providersByName.put(provider.name, provider);

        /**
         * Check for existing listeners and inform the provider of their
         * existance
         */
        Set listeners = listenersByName.getSet(provider.name);
        if( listeners == null )
            return;

        Iterator iter = listeners.iterator();
        while( iter.hasNext() ) {
            ListenerProxy listener = (ListenerProxy)iter.next();
            RegisterMsg msg = RegisterMsg.addListener(listener);
            locator.sendToLocal(msg, provider.node);
        }
    }


    /**
     * deregisterProvider: just remove the entry. No action with
     *                     listeners, the provider is responsible
     *                     for informing them.
     * @param local
     * @param name
     * @return
     * @throws RemoteException
     */
    private void deregisterProvider(ProviderProxy provider) {

        /**
         * Remove from provider info
         */
        providersByNode.remove(provider.node, provider);
        providersByName.remove(provider.name, provider);
    }


    /**
     * registerListener: if there is no provider then add to the pending
     *                   listeners. If there is a provider simply return
     *                   its location and do not add to pending listeners
     *                   (as its not pending!!)
     * @param local
     * @param name
     * @return
     * @throws RemoteException
     */
    private void registerListener(ListenerProxy listener) {

        /**
         * Add to listener info
         */
        listenersByNode.put(listener.node, listener);
        listenersByName.put(listener.name, listener);

        /**
         * Check for existing providers and inform them of the new
         * listener
         */
        Set providers = providersByName.getSet(listener.name);
        if( providers == null )
            return;

        Iterator iter = providers.iterator();
        while( iter.hasNext() ) {
            ProviderProxy provider = (ProviderProxy)iter.next();
            RegisterMsg msg = RegisterMsg.addListener(listener);
            locator.sendToLocal(msg, provider.node);
        }
    }


    /**
     * deregisterListener: just remove the entry. No action with
     *                     providers, the listener is responsible
     *                     for informing them.

     * @param local
     * @param name
     * @return
     * @throws RemoteException
     */
    private void deregisterListener(ListenerProxy listener) {

        /**
         * Remove from listener info
         */
        listenersByNode.remove(listener.node, listener);
        listenersByName.remove(listener.name, listener);

    }

    /**
     * List out the contents of the register by node.
     * @return  String
     */
    public synchronized String toString() {
        if( !active )
            return "** NOT ACTIVE **\n" +
                   "** NOT ACTIVE **\n" +
                   "** NOT ACTIVE **\n";

        String str = "Providers By Node:\n";
        Iterator iter = providersByNode.entrySet().iterator();

        while( iter.hasNext() ) {
            Map.Entry entry = (Map.Entry)iter.next();
            Iterator piter = ((Set)entry.getValue()).iterator();
            while( piter.hasNext() )
                str += "    " + ((ProviderProxy)piter.next()).toString() + "\n";
        }

        str += "\nPending Listeners:\n";
        iter = listenersByNode.entrySet().iterator();
        while( iter.hasNext() ) {
            Map.Entry entry = (Map.Entry)iter.next();
            Iterator piter = ((Set)entry.getValue()).iterator();
            while( piter.hasNext() ) {
                ListenerProxy listener = (ListenerProxy)piter.next();
                str += "    " + listener.toString() + "\n";
            }
        }
        return str;
    }

    public synchronized void showDebugFrame() {
        if(debug == null )
            debug = new DebugFrame("Node " + me + " Global Register Contents:");
        debug.makeVisible(this);
    }
    public synchronized void removeDebugFrame() {
        if( debug != null ) {
            debug.remove();
            debug = null;
        }
    }
    private void updateDebugFrame() {
        if( debug != null )
            debug.update();
    }
}
