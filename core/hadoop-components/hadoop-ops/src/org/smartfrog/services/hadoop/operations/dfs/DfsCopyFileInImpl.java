/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.operations.dfs;

import org.apache.hadoop.fs.Path;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.operations.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.operations.utils.DfsUtils;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.rmi.RemoteException;

/**
 * Component to copy a file into DFS Created 17-Jun-2008 15:06:23
 */

@SuppressWarnings("ProhibitedExceptionDeclared")
public class DfsCopyFileInImpl extends DfsOperationImpl implements DfsCopyOperation {

    public DfsCopyFileInImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        startWorkerThread();
    }

    /**
     * do the work
     *
     * @param fileSystem the filesystem; this is closed afterwards
     * @param conf       the configuration driving this operation
     * @throws Exception on any failure
     */
    @Override
    protected void performDfsOperation(org.apache.hadoop.fs.FileSystem fileSystem,
                                       ManagedConfiguration conf)
            throws Exception {
        Path dest = resolveDfsPath(ATTR_DEST);
        File source = FileSystem.lookupAbsoluteFile(this,
                ATTR_SOURCE,
                null,
                null,
                true,
                null);
        boolean overwrite = sfResolve(ATTR_OVERWRITE, false, true);
        DfsUtils.copyLocalFileIn(fileSystem, source, dest, overwrite);
    }

}
