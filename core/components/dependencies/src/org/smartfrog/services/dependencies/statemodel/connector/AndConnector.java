/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.dependencies.statemodel.connector;

import java.rmi.RemoteException;

import org.smartfrog.SFSystem;
import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

/**
 */
 public class AndConnector extends Connector {

   public AndConnector() throws RemoteException {}

   public boolean isEnabled() throws RemoteException, SmartFrogRuntimeException {
       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1]);
      boolean existsCheck = false;
      boolean result=true;
      for (DependencyValidation dep : dependencies){
          existsCheck = true;
          if (!dep.isEnabled()) {
              result=false;
              break;
          }
      }

      //If result is true, then ok subject to existence check, otherwise ok.
      if (result && exists){
           result = existsCheck;
      }

       if (sfLog().isDebugEnabled())
           sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");

      //Either way, subsequently toggle result based on not
      return (not? !result: result);
   }
}
