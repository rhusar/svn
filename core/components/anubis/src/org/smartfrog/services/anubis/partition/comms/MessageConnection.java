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
package org.smartfrog.services.anubis.partition.comms;



import java.util.LinkedList;

import org.smartfrog.services.anubis.partition.protocols.heartbeat.HeartbeatProtocol;
import org.smartfrog.services.anubis.partition.protocols.heartbeat.HeartbeatProtocolAdapter;
import org.smartfrog.services.anubis.partition.protocols.leader.Candidate;
import org.smartfrog.services.anubis.partition.protocols.partitionmanager.ConnectionSet;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.wire.Wire;
import org.smartfrog.services.anubis.partition.wire.WireMsg;
import org.smartfrog.services.anubis.partition.wire.msg.Close;
import org.smartfrog.services.anubis.partition.wire.msg.Heartbeat;
import org.smartfrog.services.anubis.partition.wire.msg.HeartbeatMsg;
import org.smartfrog.services.anubis.partition.wire.msg.MessageMsg;
import org.smartfrog.services.anubis.partition.wire.msg.TimedMsg;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;


public class MessageConnection
        extends HeartbeatProtocolAdapter
        implements Connection, HeartbeatProtocol, Candidate {

    /**
     * Implementations
     */
    private IOConnection connectionImpl = null;
    private IOConnection closingImpl = null;

    private Identity                me                = null;
    private ConnectionSet           connectionSet     = null;
    private LinkedList              msgQ              = new LinkedList();
    private boolean                 disconnectPending = false;
    private boolean                 terminated        = false;

    private boolean                 ignoring          = false;

    private LogSF                   log               = LogFactory.getLog(this.getClass().toString());


    /**
     * Constructor used to create a MessageConnection when the implementation
     * is yet to be built. If this end is initiating then the implementation
     * should be constructed asynchronously by MessageConnectionInititor using
     * its own thread - done to avoid blocking the connectionSet. If this end
     * is not initiating then this object will wait around until the
     * MessageConnectionServer implements its implementation in response to a
     * connection request from the other end.
     *
     * @param id - the identity of this node
     * @param cs - the connection set
     * @param can - the heartbeat connection (used to get details of the connection)
     */
    public MessageConnection(Identity id, ConnectionSet cs, HeartbeatProtocol hbp, Candidate can) {
        super(hbp, can);
        me                = id;
        connectionSet     = cs;
    }

    /**
     * Over-ride the receiveHeartbeat() method. Message connections ignore
     * heartbeats received out of band (i.e. delivered from the multicast
     * heartbeat). Only heartbeats received in-band on their own connection counts.
     *
     * @param hb - the heartbeat
     * @return - always false (not valid)
     */
    public boolean receiveHeartbeat(Heartbeat hb) { return false; }

    /**
     * Connection interface - to terminate (kill as opposed to close) the connection.
     * There will be no callback to closing() as a result of terminating the transport.
     */
    public void terminate() {
        super.terminate();
        if( connectionImpl != null )
            connectionImpl.terminate();
        msgQ.clear();
        terminated = true;
    }

    /**
     * closing() is used by the transport to indicate that it is closing. If the
     * transport detects an error condition it will close itself and call this
     * method.
     */
    public void closing() {
        super.terminate();
        connectionSet.removeConnection(this);
        msgQ.clear();
    }

    /**
     * General deliver method - any messaage received by the transport will
     * be delivered using this method. The message will be a valid TimedMsg!
     *
     * @param msg - the message
     */
    public void deliver(TimedMsg msg) {

        if( msg instanceof HeartbeatMsg ) {

            HeartbeatMsg hbmsg = (HeartbeatMsg)msg;

            /**
             * pass off to heartbeat protocol
             */
            super.receiveHeartbeat(hbmsg);

            /**
             * do the checks specific to a heartbeat connection.
             */
            if( connectionSet.thisEndInitiatesConnectionsTo( getSender() ) ) {
                checkInitiatingClose(hbmsg);
            } else {
                checkRespondingClose(hbmsg);
            }

        } else if( msg instanceof MessageMsg ) {

            MessageMsg mmsg = (MessageMsg)msg;

            /**
             * The heartbeat protocol is extended to advance the time if any
             * message arrives. This is because receiving any message counts
             * as indication that the other end is still alive
             */
           if( mmsg.getTime() > super.getTime() ) {
                super.setTime(mmsg.getTime());
            }
            connectionSet.receiveObject(mmsg.getMessageObject(),
                                        mmsg.getSender(),
                                        mmsg.getTime());

        } else if( msg == null ) {

            if( log.isErrorEnabled() )
                log.error(me + "connection transport delivered null message from " + this.getSender() );

        } else {

          if( log.isErrorEnabled() )
              log.error(me + "connection transport delivered unknown message type from " + this.getSender()
                 + " message=" + msg );
        }
    }


    private void checkInitiatingClose(HeartbeatMsg msg) {
        // System.out.println(me + " initiator close check on link to " + getSender() );
        /**
         * If the connection is already closing check for the returned
         * close heartbeat - note that normal messages and heartbeats
         * are accepted up to the actual return close message.
         */
        if( closingImpl != null ) {
            if(msg instanceof Close) {
                closingImpl.terminate();
                closingImpl = null;

                /**
                 * if we have new messages or either end wants the connection
                 * back - reinitiate the connection by re-creating the
                 * implementation.
                 */
                if( !msgQ.isEmpty() ||
                    msg.getMsgLinks().contains(me.id) ||
                    connectionSet.wantsMsgLinkTo( getSender() ) ) {

                    // System.out.println(me + " connection closed but re-opening ");
                    // if( !msgQ.isEmpty() ) System.out.println(me + "  -- the message queue is not empty");
                    // if( msg.getMsgLinks().get(me.id) ) System.out.println(me + "  -- the other end appears to want the connection");
                    // if( connectionSet.wantsMsgLinkTo( getSender() ) ) System.out.println(me + "  -- this end appears to want the connection");

                    connectionSet.getConnectionServer().initiateConnection(me, this, connectionSet.getHeartbeatMsg());
                }

                /**
                 * If the connection is to stay closed replace it with a
                 * heartbeat connection in the connection set.
                 */
                else {
                    // System.out.println(me + " connection fully closed - converting back to heartbeat connection");
                    connectionSet.convertToHeartbeatConnection(this);
                }
            } else {
                // System.out.println(me + " connection closing but not a CloseMsg heartbeat");
            }
        }

        /**
         * If the connection is not already closing but it is time to close
         * (i.e. neither end wants the connection) initiate a close - but not if we
         * have some messages to send!!!! (if there are messages hang around
         * until they are sent - clears up case of rapid connect, send, disconnect)
         */
        else if( !msg.getMsgLinks().contains(me.id) &&
                 !connectionSet.wantsMsgLinkTo( getSender() ) &&
                 msgQ.isEmpty() ) {
            // System.out.println(me + " entering close on connection to " +  getSender() );
            closingImpl = connectionImpl;
            connectionImpl = null;
            try {

                closingImpl.send( connectionSet.getHeartbeatMsg().toClose());

            } catch (Exception ex) {

                if( log.isErrorEnabled() )
                    log.error( me + "failed to marshall close message - not sent to " + getSender(), ex );

            }
        }
    }


    private void checkRespondingClose(HeartbeatMsg msg) {
        // System.out.println(me + " responder close check on link to " +  getSender() );
        /**
         * If we have a close message then immediately drop the connection.
         * We need to tell the connection impl to be silent - this means don't
         * tell me when you terminate. This is because the initiating end will
         * eventually shutdown the link and the impl will close - when that happens
         * we don't want it cause this messageConnection to terminate too!
         */
        if(msg instanceof Close) {
            // System.out.println(me + " received CloseMsg - closing connection to " +  getSender() );
            sendMsg( connectionSet.getHeartbeatMsg().toClose() );
            connectionImpl.silent();
            connectionImpl = null;
            if( !connectionSet.wantsMsgLinkTo( getSender() ) ) {
                // System.out.println(me + " converting back to heartbeat connection");
                connectionSet.convertToHeartbeatConnection(this);
            }
        }
    }

    /**
     * sendObject() creates a message to transport an object and calls
     * sendMsg() to send it. It also time-stamps the message. The object must
     * be serializable.
     *
     * @param obj - the object to transport
     */
    public void sendObject(Object obj) {
        MessageMsg msg = new MessageMsg(me, obj);
        msg.setTime(System.currentTimeMillis());
        sendMsg(msg);
    }




    public void sendMsg(TimedMsg msg) {
        
        if (msg == null) {
            if (log.isErrorEnabled())
                log.error(me + " sendBytes(WireMsg) called with null parameter ",
                          new Exception());
            return;
        }
        
        
        synchronized (msgQ) {
            /**
             * If the connection has not been constructed, buffer the message.
             * FIX ME: This is a hack for the moment there is no flow control to
             *         deal with too many messages!
             */
            if (connectionImpl == null) {
                msgQ.addLast(msg);
                return;
            }

            /**
             * If the connection has been terminated then just return. In time the
             * User will be notified that the connection, and therefore the
             * remote node, has terminated.
             */
            if( !connectionImpl.connected() )
                return;


            /**
             * If the connectionImpl exists and it is connected then just send on it.
             */
            connectionImpl.send(msg);
        }
    }


    /**
     * Inform the messageConnection that an implementation of the connection
     * has been created. When a new connection is completed any outstanding
     * messages should be sent. If there is a disconnect pending then
     * immediately inform the connectionSet to disconnect.
     *
     * @param impl
     * @return  boolean
     */
    public boolean assignImpl(IOConnection impl) {

        synchronized(msgQ) {
            if( terminated )
                return false;

            if( connectionImpl != null ) {
                if( log.isErrorEnabled() )
                    log.error(me + " attempt to assign a new implementation when one exists", new Exception());
                return false;
            }

            connectionImpl = impl;
            connectionImpl.setIgnoring(ignoring); // indicate if it should ignore messages

            while( !msgQ.isEmpty() )
                connectionImpl.send((TimedMsg)msgQ.removeFirst());

            if(disconnectPending)
                connectionSet.disconnect( getSender() );
        }

        return true;
    }

    /**
     * Instruct the messageConnection to disconnect. If there are no messages
     * waiting to be sent then this can be done immediately (by informing
     * the connectionSet of the disconnect - it is actually done via a check
     * on heartbeat delivery, not now). If there are messages pending it
     * implies that the connection has not been created yet and there are
     * messages to send, so we still want to connect even if the application
     * layer no longer wants to send. In this case that there is a disconnect
     * pending but don't tell the connectionSet to disconnect yet.
     */
    public void disconnect() {
        synchronized(msgQ) {
            disconnectPending = true;

            if( msgQ.isEmpty() )
                connectionSet.disconnect( getSender() );
        }
    }


    /**
     * Indicate to the MessageConnection that a connect has been called.
     * The purpose is to cancel a pending disconnect.
     */
    public void connect() {
        disconnectPending = false;
    }

    /**
     * for testing - can set the connection to ignore messages -
     * they will be received, but just dropped
     * @param ignoring
     */
    public void setIgnoring(boolean ignoring) {
        synchronized(msgQ) {
            this.ignoring = ignoring;
            if(connectionImpl != null )
                connectionImpl.setIgnoring(ignoring);
        }
    }

    public void logClose(String reason, Throwable throwable) {

         if( log.isDebugEnabled() )
             log.debug(me + " message connection transport for " + this.getSender() + " shutdown:" + reason, throwable);

    }



}
