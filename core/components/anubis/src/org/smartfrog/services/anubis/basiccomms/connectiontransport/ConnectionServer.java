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
package org.smartfrog.services.anubis.basiccomms.connectiontransport;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;


/**
 * Server of connections.
 *
 * ConnectionServer is a socket listener server that receives connection
 * requests on a given address, accepts them and creates an endpoint
 * object to handle the connection. The endpoint is created using a
 * ConnectionFactory interface.
 */
public class ConnectionServer extends Thread {
    
            private     ServerSocketChannel     listenSocket;
            private     ConnectionFactory       connectionFactory;
volatile    private     boolean                 open;
            private     LogSF                   log = LogFactory.getLog(this.getClass().toString());

    /**
     * Default constructor is initually unusable.
     * Constructor - sets a null listening socket and a null connection
     * factory (i.e. an unusable server).
     */
    public ConnectionServer() {

        super();

        listenSocket      = null;
        connectionFactory = null;
        open              = false;
    }



    /**
     * Constructor - creates a listening socket on the default ip address
     * returned for the given host name (should be this host). Initially the
     * default connection factory is assumed.
     */
    public ConnectionServer(String threadName, String hostName) throws IOException {
        super(threadName);
        constructServer(hostName, 0);
    }


    /**
     *
     * Constructor - creates a listening socket on the default ip address
     * returned for the given host name (should be this host), using the
     * given port. Initially the default connection factory is assumed.
     */
    public ConnectionServer(String threadName, String hostName, int port) throws IOException {
        super(threadName);
        constructServer(hostName, port);
    }


    /**
     * Constructor - creates a listening socket on the default ip address
     * returned for the given host name (should be this host), using the
     * given port. Initially the default connection factory is assumed.
     */
    public ConnectionServer(String threadName, InetAddress addr, int port) throws IOException {
        super(threadName);
        constructServer(addr, port);
    }

    /**
     * Constructor helper - creates a listening socket on the default ip address
     * returned for the given host name (should be this host), using the
     * given port. Initially the default connection factory is assumed.
     */
    private void constructServer(String hostName, int port) throws IOException {
        constructServer(InetAddress.getByName(hostName), port);
    }

    /**
     * Constructor helper - creates a listening socket on the default ip address
     * returned for the given InetAddress (should be this host), using the
     * given port. Initially the default connection factory is assumed.
     */

    private void constructServer(InetAddress inetAddress, int port) throws IOException {

        connectionFactory = new DefaultConnectionFactory();

        try {

            if( log.isDebugEnabled() ) {
                log.debug("Binding blocking connection server to port: " + port);
            }
            
            listenSocket = ServerSocketChannel.open();
            listenSocket.configureBlocking(true);
            listenSocket.socket().bind(new InetSocketAddress(inetAddress, port));

        } catch(IOException ioex) {
            
            if( log.isDebugEnabled() ) {
                log.debug("Failed to create server socket: ", ioex);
            }

           listenSocket = null;
           open  = false;
           throw ioex;

        }

        open = true;

    }



    /**
     * set the connection factory to the one specified as a parameter
     */
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }



    /**
     * return the address being used by this ConnectionServer.
     */
    public ConnectionAddress getAddress() {

        if( listenSocket == null )
            return null;
        else
            return new ConnectionAddress( listenSocket.socket().getInetAddress(),
                                          listenSocket.socket().getLocalPort() );
    }


    public void shutdown() {
        open = false;
        try {
            listenSocket.close();
        } catch( IOException ioex ) {}
    }


    /**
     * blocking connection accept loop - creates a connection endpoint
     * in response to connection requests.
     */
    public void run() {

        while( open ) {

            try {
                SocketChannel channel = listenSocket.accept();
                connectionFactory.createConnection(channel);
            } catch(Exception ex) {
                if( open ) {
                    if( log.isInfoEnabled() )
                        log.info(ex);
                }
            }

        }

    }


    /**
     * returns an string representing the status of this thread
     */
    public String getThreadStatusString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.getName()).append(" ............................ ").setLength(30);
        buffer.append(super.isAlive() ? ".. is Alive " : ".. is Dead ");
        buffer.append(open ? ".. running ....." : ".. terminated ..");
        buffer.append( " address = " ).append(getAddress().toString());
        return buffer.toString();
  }
}

