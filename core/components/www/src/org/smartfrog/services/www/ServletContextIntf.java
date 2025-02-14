/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Generic servlet context
 * created 17-Jun-2004 11:40:11
 */


public interface ServletContextIntf extends ApplicationServerContext {
    /**
     * {@value}
     */
    String ATTR_RESOURCE_BASE = "resourceBase";

    /**
     * {@value}
     */
    String ATTR_RESOURCE_PACKAGE = "resourcePackage";


    /**
     * host ip address. The IPAddr is returned for ease of use on networks where
     * DNS is not there
     */
    String ATTR_HOST_ADDRESS = "ipaddr";

    /**
     * Initial options for the context
     */
    String ATTR_OPTIONS = "options";

    /**
     * Add a mime mapping
     *
     * @param extension extension to map (no '.')
     * @param mimeType  mimetype to generate
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException for RMI/Networking problems
     */
    public void addMimeMapping(String extension, String mimeType) throws RemoteException, SmartFrogException;


    /**
     * Remove a mime mapping for an extension
     *
     * @param extension extension to unmap
     * @return true if the unmapping was successful
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException for RMI/Networking problems
     */
    public boolean removeMimeMapping(String extension) throws RemoteException, SmartFrogException;

    /**
     * add a servlet
     *
     * @param servletDeclaration component declaring the servlet
     * @return the delegate that implements the servlet binding
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException for RMI/Networking problems
     */
    public ServletContextComponentDelegate addServlet(ServletComponent servletDeclaration) throws RemoteException, SmartFrogException;

    /**
     * add a filter
     *
     * @param servletDeclaration component declaring the servlet
     * @return the delegate that implements the servlet binding
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException    for RMI/Networking problems
     */
    public ServletContextComponentDelegate addFilter(FilterComponent declaration)
            throws RemoteException, SmartFrogException;


}
