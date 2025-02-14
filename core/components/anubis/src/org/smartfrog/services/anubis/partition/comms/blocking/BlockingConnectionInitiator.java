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
package org.smartfrog.services.anubis.partition.comms.blocking;

import org.smartfrog.services.anubis.partition.protocols.partitionmanager.ConnectionSet;
import org.smartfrog.services.anubis.partition.wire.msg.HeartbeatMsg;
import org.smartfrog.services.anubis.partition.wire.security.WireSecurity;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.comms.IOConnection;
import org.smartfrog.services.anubis.partition.comms.MessageConnection;
import java.io.IOException;
import org.smartfrog.services.anubis.partition.wire.WireFormException;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

public class BlockingConnectionInitiator
    extends Thread {
    
    private Identity me = null;
    private ConnectionSet connectionSet = null;
    private MessageConnection connection = null;
    private HeartbeatMsg heartbeat = null;
    private WireSecurity wireSecurity = null;
    private LogSF log = LogFactory.getLog(this.getClass().toString());

    public BlockingConnectionInitiator(Identity id,
                                       MessageConnection con,
                                       ConnectionSet cset,
                                       HeartbeatMsg hb,
                                       WireSecurity sec) throws IOException,
        WireFormException {
        super();
        me = id;
        connection = con;
        connectionSet = cset;
        heartbeat = hb;
        wireSecurity = sec;
    }

    /**
     * make a new connection. The messageConnectionImpl opens an actual
     * connection. This method runs in its own thread so that creating
     * connections is an asynchronous task.
     */
    public void run() {
        
        MessageConnectionImpl impl = new MessageConnectionImpl(
                me, connectionSet, connection.getSenderAddress(), connection, wireSecurity);

        /**
         * If the attempt to establish a connection failed we just give up. In
         * time the MessageConnection will time out.
         */
        if( !impl.connected() ) {
            if( log.isDebugEnabled() ) {
                log.debug("Blocking connection initiator failed to establish a connection");
            }
            return;
        }

        /**
         * Set the order to be the initial message and 
         * send with security
         */
        try {
            heartbeat.setOrder(IOConnection.INITIAL_MSG_ORDER);
            impl.send( wireSecurity.toWireForm(heartbeat) );
        } catch (WireFormException e) {
            if( log.isErrorEnabled() ) {
                log.error(me + " failed to marshall timed message: " + heartbeat, e);
            }
            return;
        }

        /**
         * If the implementation is successfully assigned then start its thread
         * - otherwise call terminate() to shutdown the connection. The impl
         * will not be accepted if the heartbeat protocol has terminated the
         * connection during the time it took to establish it.
         */
        if( connection.assignImpl(impl) )
            impl.start();
        else
            impl.terminate();
    }
}
