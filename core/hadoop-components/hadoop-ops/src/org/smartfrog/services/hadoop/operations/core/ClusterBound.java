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
package org.smartfrog.services.hadoop.operations.core;

import java.rmi.Remote;

/**
 * This interface declares that component is bound to a cluster. It still has the right to override those settings; the
 * cluster is the initial template that may be dynamically built up from the core Hadoop XML files
 */


public interface ClusterBound extends Remote {

    /**
     * {@value}
     */
    String ATTR_CLUSTER = "cluster";
    String ATTR_CLUSTER_REQUIRED = "clusterRequired";
}
