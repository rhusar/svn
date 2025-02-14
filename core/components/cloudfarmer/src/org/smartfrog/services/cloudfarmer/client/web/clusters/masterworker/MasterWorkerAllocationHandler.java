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
package org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker;

import org.smartfrog.sfcore.common.LocalSmartFrogDescriptor;
import org.smartfrog.services.cloudfarmer.client.web.clusters.ClusterAllocationHandler;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequest;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Created 11-Jan-2010 16:09:50
 */

public abstract class MasterWorkerAllocationHandler extends ClusterAllocationHandler implements MasterWorkerRoles {
    protected boolean creatingMaster;
    protected HostInstance master;

    protected MasterWorkerAllocationHandler(ClusterController controller) {
        super(controller);
    }

    public boolean isCreatingMaster() {
        return creatingMaster;
    }

    public void setCreatingMaster(boolean creatingMaster) {
        this.creatingMaster = creatingMaster;
    }

    public HostInstance getMaster() {
        return master;
    }

    public void setMaster(HostInstance master) {
        this.master = master;
    }

    /**
     * This is where roles are installed 
     * {@inheritDoc}
     */
    @Override
    public void allocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException {
        String role = request.getRole();
        if (MASTER.equals(role)) {
            masterAllocationRequestSucceeded(request, newhosts);
        } else if (WORKER.equals(role)) {
            List<DeploymentFailure> failures = workerAllocationRequestSucceeded(request, newhosts);
            if (failures.size() > 0) {
                int deployedTo = newhosts.size() - failures.size();
                DeploymentFailure firstFailure = failures.get(0);
                StringBuilder builder = new StringBuilder();
                for (DeploymentFailure df:failures) {
                    builder.append(df.toString());
                    builder.append("\n");
                }
                throw new SmartFrogDeploymentException(
                        "Only deployed to " + deployedTo + " hosts :\n"
                                + builder.toString(),
                        firstFailure.getException());
            }
        }
    }

    /**
     * Bind the system properties then load the application
     * @param resource   resource to load
     * @return a descriptor bonded to the master
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    protected LocalSmartFrogDescriptor loadSFApp(String resource) throws IOException, SmartFrogException {
        ///set the binding values
        bindSystemProperties();
        return parseResource(resource);
    }


    /**
     * Perform any system property setup ready to load the SF application
     */
    protected void bindSystemProperties() {
        System.setProperty(BINDING_MASTER_HOSTNAME, master.getHostname());
    }

    /**
     * Handle a master allocation request by pushing out the master configuration
     *
     * @param request  the request that just succeeded
     * @param newhosts the new hosts
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    protected void masterAllocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException {
        bindMaster(newhosts);
        String resource = getMasterResourceName();
        if (resource != null && !resource.isEmpty()) {
            LocalSmartFrogDescriptor descriptor = loadSFApp(resource);
            deployApplication(master, request.getRole(), descriptor);
        }

    }

    /**
     * Bind to the master node
     * @param newhosts the new host list
     * @return the master
     */
    protected HostInstance bindMaster(HostInstanceList newhosts) {
        master = newhosts.getMaster();
        return master;
    }

    /**
     * Handle a master allocation request by pushing out the worker configuration
     *
     * @param request  the request that just succeeded
     * @param newhosts the new hosts
     * @return a list of exceptions, the size is an implicit count of nodes that failed. 
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    protected List<DeploymentFailure> workerAllocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException {
        //we'd better have a non-null master here
        if (master == null) {
            throw new SmartFrogDeploymentException("Cannot bring up worker nodes without a master node");
        }
        List<DeploymentFailure> failures = new ArrayList<DeploymentFailure>();
        String resource = getWorkerResourceName();
        if (resource != null && !resource.isEmpty()) {
            LocalSmartFrogDescriptor descriptor = loadSFApp(resource);
            for (HostInstance host : newhosts) {
                try {
                    deployApplication(host, request.getRole(), descriptor);
                } catch (SmartFrogException e) {
                    DeploymentFailure fail = new DeploymentFailure(host, e);
                    LOG.error(fail, e);
                    failures.add(fail);
                } catch (IOException e) {
                    DeploymentFailure fail = new DeploymentFailure(host, e);
                    LOG.error(fail, e);
                    failures.add(fail);
                }
            }
        }
        return failures;
    }

    /**
     * Get the resource to load for the master
     * @return the resource or null
     */
    protected abstract String getMasterResourceName();

    /**
     * Get the resource or null
     * @return the resource
     */
    protected abstract String getWorkerResourceName();

    protected static class DeploymentFailure {
        private HostInstance host;
        private Exception exception;

        protected DeploymentFailure(HostInstance host, Exception exception) {
            this.host = host;
            this.exception = exception;
        }

        public HostInstance getHost() {
            return host;
        }

        public Exception getException() {
            return exception;
        }

        @Override
        public String toString() {
            return "Failed to deploy to " + host + " : " + exception;
        }
    }
}
