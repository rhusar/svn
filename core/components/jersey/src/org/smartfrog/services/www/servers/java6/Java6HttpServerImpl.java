/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.www.servers.java6;

import com.sun.net.httpserver.HttpServer;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

/**
 * Component that represents a Java 6 server
 */
public class Java6HttpServerImpl extends PrimImpl {

    private HttpServer server;
    public static final String ATTR_PORT = "port";
    public static final String ATTR_HOST = "host";
    public static final String ATTR_PROTOCOL = "protocol";
    public static final String ATTR_FACTORY_CLASS = "factoryClass";
    private static final String E_NO_START = "Could not start the server on ";

    public Java6HttpServerImpl() throws RemoteException {
    }

    @Override
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        int port = sfResolve(ATTR_PORT, 0, true);
        String host = sfResolve(ATTR_HOST, "", true);
        String protocol = sfResolve(ATTR_PROTOCOL, "", true);
        String serverURL = protocol + "://" + host + ":" + port + "/";
        sfLog().info("Starting Java6 HTTP server on " + serverURL);
        String factoryClassName = sfResolve(ATTR_FACTORY_CLASS, "", true);
        ComponentHelper helper = new ComponentHelper(this);
        Class factoryClass = helper.loadClass(factoryClassName);
        Method createMethod = null;
        try {
            createMethod = factoryClass.getMethod("create", String.class);
        } catch (NoSuchMethodException e) {
            throw new SmartFrogDeploymentException("No method 'create(String)' in class " + factoryClassName, e);
        }
        try {
            Object result = createMethod.invoke(null, serverURL);
            server = (HttpServer) result;
            if (server == null) {
                throw new SmartFrogDeploymentException("Null server from " + factoryClassName);
            }
            server.start();
        } catch (InvocationTargetException e) {
            Throwable thrown = e;
            if (e.getCause() != null) {
                thrown = e.getCause();
            }
            throw new SmartFrogDeploymentException(E_NO_START
                    + serverURL + ": " + thrown,
                    thrown);

        } catch (IllegalAccessException e) {
            throw new SmartFrogDeploymentException(E_NO_START
                    + serverURL + ": " + e,
                    e);
        } catch (ClassCastException e) {
            throw new SmartFrogDeploymentException(E_NO_START
                    + serverURL + ": " + e,
                    e);
        }
    }

    @Override
    protected void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }
}
