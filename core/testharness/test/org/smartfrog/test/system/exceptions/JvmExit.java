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

package org.smartfrog.test.system.exceptions;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.ExitCodes;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

/**
 * Makes JVM exit during deployment, consequently kills smartfrog daemon 
 * as well.
 */
public class JvmExit extends PrimImpl implements Prim {
    
    /** Default Constructor 
     *
     *@exception  RemoteException if unable to create the object
     */
    public JvmExit() throws RemoteException {
    }

    /**
     * Deploys the component and exits from the JVM.
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException network problems
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        sfLog().warn("Going to exit....");
        ExitCodes.exit(10);
    }
}
