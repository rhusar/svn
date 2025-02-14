package org.smartfrog.services.cloudfarmer.api;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.rmi.Remote;

/**
 *  This interface is primarily for SF components; its a factory
 * interface to provide host-specific deployment services
 */
public interface NodeDeploymentServiceFactory extends Remote {

    /**
     * Create a service that can deploy to this node
     * @param node the node to work with
     * @return a service interface
     * @throws IOException Network and other IO problems
     * @throws SmartFrogException SmartFrog problems
     */
    public NodeDeploymentService createInstance(ClusterNode node) throws IOException, SmartFrogException;

    /**
     * Get some diagnostics text from a component
     * @return text
     * @throws IOException Network and other IO problems
     * @throws SmartFrogException SmartFrog problems
     */
    public String getDiagnosticsText() throws IOException, SmartFrogException;

    /**
     * Test for node deployment being supported <i>at all</i>
     * @return true if Node deployment instances can be created, false otherwise
     * @throws IOException Network and other IO problems
     * @throws SmartFrogException SmartFrog problems
     */
    public boolean isNodeDeploymentSupported() throws IOException, SmartFrogException;
}
