/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jetty.contexts.delegates;

import org.mortbay.jetty.webapp.WebAppContext;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.jetty.JettyImpl;
import org.smartfrog.services.jetty.contexts.JettyWebApplicationContext;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.rmi.RemoteException;

/**
 * This represents a web application that has been deployed on a jetty system
 */
public class DelegateWebApplicationContext extends DelegateApplicationContext
        implements JettyWebApplicationContext {

    private Reference contextPathRef = new Reference(ATTR_CONTEXT_PATH);
    private Reference requestIdRef = new Reference(ATTR_REQUEST_ID);

    private String contextPath = null;
    private String webApp = null;
    private final Prim owner;
    private boolean requestId = false;


    private WebAppContext application;
    /**
     * a log
     */
    private final Log log;

    /**
     * 
     * @param server the server to deploy into
     * @param owner the Prim that owns the webapp context
     */
    public DelegateWebApplicationContext(JettyImpl server, Prim owner) {
        super(server, null);
        this.owner = owner;
        log = LogFactory.getOwnerLog(owner);
    }

    /**
     * at deploy time, do everything except starting the component
     *
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems
     */
    @Override
    public void deploy()
            throws SmartFrogException, RemoteException {
        super.deploy();
        webApp = FileSystem.lookupAbsolutePath(owner,
                        ATTR_FILE,
                        null,
                        null,
                        true,
                        null);
        //sanity check
        File webappFile = new File(webApp);
        if (!webappFile.exists()) {
            throw new SmartFrogDeploymentException(ERROR_FILE_NOT_FOUND +
                    webappFile);
        }
        //request ID
        requestId = owner.sfResolve(requestIdRef, requestId, false);

        contextPath = owner.sfResolve(contextPathRef,
                (String) null,
                true);

        if (!contextPath.startsWith("/")) {
            log.warn("Fixing up the context path " + contextPath + " by adding a leading \"/\"");
            contextPath = "/" + contextPath;
        }

        owner.sfReplaceAttribute(ATTR_ABSOLUTE_PATH, contextPath);
        application = new WebAppContext(webApp, contextPath);

        setContext(application);
        DelegateHelper.setOwnerAttribute(application.getServletContext(), owner);
    }


}
