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
package org.smartfrog.services.hadoop.components.cluster;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Created 11-Aug-2008 16:30:48
 */

public class IsWorkerCountGood extends IsHadoopServiceLive {

    private int minCount;

    public IsWorkerCountGood() throws RemoteException {
    }


    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        minCount = sfResolve("minCount", 0, true);
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    @Override
    public boolean evaluate() throws RemoteException, SmartFrogException {
        if(!super.evaluate()) {
            return false;
        }
        int workerCount = ((ClusterManager) getService()).getLiveWorkerCount();
        return evalOrFail(workerCount >= minCount, "worker count is "+ workerCount);
    }
}
