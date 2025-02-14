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
package org.smartfrog.services.cloudfarmer.client.web.actions.cluster;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.BadParameterException;
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.AttributeNames;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.rmi.ConnectException;
import java.util.Enumeration;
import java.util.Map;
import java.io.IOException;

/**
 * Created 02-Oct-2009 12:57:12
 */

public abstract class AbstractStrutsAction extends Action implements ClusterRequestAttributes {
    protected static final Log LOG = LogFactory.getLog("org.smartfrog.services.cloudfarmer.client.web.action");
    protected Log log = LogFactory.getLog("org.smartfrog.services.cloudfarmer.client.web."
            + getActionName());

    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the name of the action
     */
    protected String getActionName() {
        return "AbstractStrutsAction";
    }

    /**
     * Handle by the success handler
     * @param mapping mapping object
     * @return the success handler to use
     */
    protected ActionForward success(ActionMapping mapping) {
        return mapping.findForward(ACTION_SUCCESS);
    }

    /**
     * Handle a failure by looking for the failure action
     * @param request incoming request
     * @param mapping mapping object
     * @param message error message
     * @return the next action handler
     */
    protected ActionForward failure(HttpServletRequest request, ActionMapping mapping, String message) {
        return forwardErrorAction(request, mapping, AttributeNames.ACTION_FAILURE, message, null, null
        );
    }
    

    /**
     * Report a failure
     *
     * @param request request
     * @param mapping mapping handler
     * @param message the message to include
     * @param thrown  what was thrown
     * @return the next handler
     */
    protected ActionForward failure(HttpServletRequest request, ActionMapping mapping, String message,
                                    Throwable thrown) {
        return forwardErrorAction(request, mapping, AttributeNames.ACTION_FAILURE, message, thrown.toString(), thrown);
    }

    /**
     * Report a failure to connect to a cluster controller
     *
     * @param request request
     * @param mapping mapping handler
     * @param message the message to include
     * @param thrown  what was thrown
     * @return the next handler
     */
    protected ActionForward bindFailure(HttpServletRequest request, ActionMapping mapping, String message,
                                        Throwable thrown) {
        String cause = "";
        if (thrown instanceof ConnectException) {
            cause = "SmartFrog is not running at the target URL, or is not reachable";
        } else if (thrown instanceof SmartFrogResolutionException) {
            cause = "A cluster farmer component is not running at the target URL";
        } else if (thrown instanceof java.net.UnknownHostException) {
            cause = "The hostname of the cluster manager is not known. Check the URL, and the hosts tables/DNS";
        }
        return forwardErrorAction(request, mapping, AttributeNames.ACTION_BIND_FAILURE, message, cause, thrown);
    }

    /**
     * Set error attributes and forward the action
     * @param request request
     * @param mapping mapping
     * @param actionName action name
     * @param message error message
     * @param cause optional cause string
     * @param thrown optional exception
     * @return the forwarded action
     */
    protected ActionForward forwardErrorAction(HttpServletRequest request, ActionMapping mapping, String actionName,
                                               String message, String cause, Throwable thrown) {
        if (thrown != null) {
            log.error(message, thrown);
            request.setAttribute(ATTR_THROWN, thrown);
        } else {
            log.error(message);
        }
        if (cause != null) {
            request.setAttribute(ATTR_ERROR_CAUSE, cause);
            log.error(cause);
        }
        request.setAttribute(ATTR_ERROR_MESSAGE, message);
        return mapping.findForward(actionName);
    }

    /**
     * Log an unimplemented operation
     *
     * @param request request
     * @param mapping mapping handle
     * @return the next handler
     */
    protected ActionForward unimplemented(HttpServletRequest request, ActionMapping mapping) {
        return failure(request, mapping, "unimplemented");
    }

    /**
     * Convert a param to an attribute; return the value (which may be null)
     *
     * @param request       request name
     * @param parameter     name to retrieve on
     * @param attributeName name to set for the attribute
     * @param required      trigger an exception if the parameter is missing
     * @return the value or null if not found and required==false
     * @throws BadParameterException the parameter is bad or missing
     */
    protected static String parameterToAttribute(HttpServletRequest request,
                                                 String parameter,
                                                 String attributeName,
                                                 boolean required) throws BadParameterException {
        String val = request.getParameter(parameter);
        if (val != null) {
            request.setAttribute(attributeName, val);
        } else {
            if (required) {
                throw new BadParameterException(new ActionMessage("error.missingParameter", parameter).toString());
            }
        }
        return val;
    }


    protected String listParameters(HttpServletRequest request) {
        Map map = request.getParameterMap();
        StringBuilder buffer = new StringBuilder();
        for (Object key:map.keySet()) {
            buffer.append(key);
            buffer.append('=');
            Object value = request.getParameter(key.toString());
            buffer.append(value);
            buffer.append('\n');
        }
        return buffer.toString();
    }

    protected String listAttributes(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        Enumeration names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            Object value = request.getAttribute(key);
            buffer.append(key);
            buffer.append('=');
            buffer.append(value);
            buffer.append('\n');
        }
        return buffer.toString();
    }

    protected void logParameters(HttpServletRequest request) {
        log.info("Request Parameters: \n"+listParameters(request));
        log.info("Request Attributes: \n" + listAttributes(request));
        
    }

    /**
    * Convert a param to an attribute; return the trimmed value which must not be empty 
    *
    * @param request       request name
    * @param parameter     name to retrieve on
    * @param attributeName name to set for the attribute
    * @return the value or null if not found and required==false
    * @throws BadParameterException the parameter is bad or missing
    */
    protected static String parameterToNonEmptyStringAttribute(HttpServletRequest request,
                                                      String parameter,
                                                      String attributeName) throws BadParameterException {
        String val = request.getParameter(parameter);
        if (val != null) {
            val = val.trim();
        } else {
            val = "";
        }
        if (!val.isEmpty()) {
            request.setAttribute(attributeName, val);
        } else {
            throw new BadParameterException(new ActionMessage("error.missingParameter", parameter).toString());
        }
        return val;
    }

    protected boolean isEmptyOrNull(String s) {
        return s==null || s.isEmpty();
    }

    /**
    * Retrieve attribute state, may integrate with persistent configuration mechanisms
    * @param request http request
    * @param key     key to retrieve on
    * @return the value
    */
    protected static Object getAttributeFromRequestState(HttpServletRequest request, String key) {
        Object o = request.getAttribute(key);
        if (o == null) {
            o = request.getSession().getAttribute(key);
        }
        return o;
    }

    protected static void setAttribute(HttpServletRequest request, String key, Object value) {
        request.setAttribute(key, value);
        request.getSession().setAttribute(key, value);
    }

    protected static void removeAttribute(HttpServletRequest request, String key) {
        request.removeAttribute(key);
        request.getSession().removeAttribute(key);
    }

    /**
     * Get an option from the servlet context, extracted from the request
     * @param request request
     * @param key key to look for
     * @param defval default value
     * @return the value
     */
    protected Object getContextOption(HttpServletRequest request, String key, Object defval) {
        ServletContext context = request.getSession().getServletContext();
        Object o = context.getAttribute(key);
        return o == null ? defval : o;
    }

    /**
    * Add the {@link #ATTR_CLUSTER_HAS_MASTER} boolean, and, if that is true the {@link #ATTR_CLUSTER_MASTER} and
    * {@link #ATTR_CLUSTER_MASTER_HOSTNAME} attributes,
    *
    * @param request    request to manipulate
    * @param controller cluster controller
    */
    protected void addMasterAttributes(HttpServletRequest request, ClusterController controller) {
        request.setAttribute(ATTR_CLUSTER_CONTROLLER, controller);
        HostInstance master = controller.getMaster();
        boolean hasMaster = master != null;
        request.setAttribute(ATTR_CLUSTER_HAS_MASTER, hasMaster);
        if (hasMaster) {
            request.setAttribute(ATTR_CLUSTER_MASTER, master);
            request.setAttribute(ATTR_CLUSTER_MASTER_HOSTNAME, master.getHostname());
        } else {
            request.setAttribute(ATTR_CLUSTER_MASTER_HOSTNAME, "(no master node)");
        }
    }

    /**
     * Get the Remote daemon of a request
     * @param request the request
     * @return any daemon attribute or null
     */
    public static RemoteDaemon getRemoteDaemon(HttpServletRequest request) {
        return (RemoteDaemon) request.getAttribute(AttributeNames.REMOTE_DAEMON);
    }


    /**
     * Get the Remote daemon of a request
     * @param request the request
     * @param required is the daemon required
     * @return any daemon attribute or null
     * @throws BadParameterException if it isn't there and it is required
     */
    public static String bindRemoteDaemon(HttpServletRequest request, boolean required)
            throws BadParameterException {
        return parameterToAttribute(request, AttributeNames.REMOTE_DAEMON_URL, AttributeNames.REMOTE_DAEMON_URL, required);
    }

    /**
     * bind to a local or remote daemon
     * @param request the request
     * @return the daemon
     * @throws SmartFrogException SF problems
     * @throws IOException IO problems
     */
    public static RemoteDaemon bindToRemoteDaemon(HttpServletRequest request) throws SmartFrogException, IOException {
        RemoteDaemon server = getRemoteDaemon(request);
        if (server == null) {
            String url = bindRemoteDaemon(request, false);
            if (url == null) {
                url = "http://localhost";
                setAttribute(request, AttributeNames.REMOTE_DAEMON_URL, url);
            }
            LOG.info("binding to remote server at " + url);
            try {
                server = new RemoteDaemon(url);
                server.bindOnDemand();
            } catch (IOException e) {
                LOG.error("Failed to bind to " + server, e);
                throw new IOException("Failed to connect to " + server + ":" + e, e);
            } catch (SmartFrogException e) {
                LOG.error("Failed to bind to " + server + " " + e, e);
                throw e;
            }
        }
        return server;
    }
}
