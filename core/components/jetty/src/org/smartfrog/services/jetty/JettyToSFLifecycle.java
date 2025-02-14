/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.jetty;

import org.mortbay.component.LifeCycle;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Liveness;

import java.rmi.RemoteException;

/**
 * Something to bridge ping, start and stop operations to jetty.
 */

public class JettyToSFLifecycle<LIFECYCLE extends LifeCycle> implements Liveness, LifeCycle.Listener {

    private String name;
    private LIFECYCLE lifecycle;
    private volatile boolean started = false;
    private final String fullName;
    private static final Log LOG = LogFactory.getLog(JettyToSFLifecycle.class);

    /**
     * Error string raised in liveness checks. {@value}
     */
    public static final String LIVENESS_ERROR_NOT_STARTED = " is not active";
    public static final String LIVENESS_ERROR_NOT_RUNNING = " is not running";
    public static final String LIVENESS_ERROR_FAILED = " has failed";

    public JettyToSFLifecycle(String name, LIFECYCLE lifecycle) {
        this.name = name;
        this.lifecycle = lifecycle;
        fullName = name + ":" + lifecycle;
    }


    public String getName() {
        return name;
    }

    public LIFECYCLE getLifecycle() {
        return lifecycle;
    }
    
    
    

    /**
     * liveness test verifies the server is started
     *
     * @param source caller
     * @throws SmartFrogLivenessException the server is  not started
     * @throws RemoteException network trouble
     */
    @Override
    public synchronized void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        if (lifecycle == null) {
            throw new SmartFrogLivenessException(name + LIVENESS_ERROR_NOT_STARTED);
        }

        if (!started) {
            //the startup hasn't finished yet, so don't react to any pings at all. Yes, this doesn't catch very-slow
            //startups, but it avoids startup race conditions, and we assume that a failing startup will
            //get caught and trigger failures
            return;
        }
        if (lifecycle.isFailed()) {
            throw new SmartFrogLivenessException(name + LIVENESS_ERROR_FAILED);
        }
        if (!lifecycle.isRunning()) {
            throw new SmartFrogLivenessException(name + LIVENESS_ERROR_NOT_RUNNING);
        }
    }

    /**
     * Stop the component; throw anything you want, as we expect this to be caught and logged in the termination logic
     *
     * @throws Exception anything that went wrong
     */
    public synchronized void stop() throws Exception {
        if (lifecycle != null) {
            lifecycle.stop();
            lifecycle = null;
        }
    }

    /**
     * Stop the component by calling {@link #stop()} any exceptions are caught and forwarded as SmartFrogExceptions
     *
     * @throws SmartFrogException anything that went wrong
     */
    public synchronized void wrappedStop() throws SmartFrogException {
        try {
            stop();
        } catch (Exception e) {
            throw SmartFrogException.forward("When stopping " + name, e);
        }
    }

    /**
     * Start the server
     *
     * @throws SmartFrogException if the component failed to start
     */
    public synchronized void start() throws SmartFrogException {
        try {
            if (lifecycle != null) {
                lifecycle.start();
            }
            started = true;
        } catch (Exception e) {
            throw SmartFrogException.forward(e);
        }
    }


    /**
     * Returns a string representation of the object.
     *
     * @return the name and the string value of the embedded lifecycle
     */
    public String toString() {
        return fullName;
    }

    @Override
    public void lifeCycleStarting(final LifeCycle event) {
        if(LOG.isDebugEnabled()) {
            LOG.debug(fullName + " starting");
        }
    }

    @Override
    public void lifeCycleStarted(final LifeCycle event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(fullName + " started");
        }
    }

    @Override
    public void lifeCycleFailure(final LifeCycle event, final Throwable cause) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(fullName + " failed");
        }

    }

    @Override
    public void lifeCycleStopping(final LifeCycle event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(fullName + " stopping");
        }
    }

    @Override
    public void lifeCycleStopped(final LifeCycle event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(fullName + " stopped");
        }
    }
}
