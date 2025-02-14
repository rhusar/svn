/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.locator.subprocess;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.smartfrog.services.anubis.locator.AnubisListener;
import org.smartfrog.services.anubis.locator.names.ProviderInstance;

public class SPListenerImpl
        extends UnicastRemoteObject
        implements SPListener {

    private AnubisListener listener;

    public SPListenerImpl(AnubisListener listener) throws RemoteException {
        this.listener = listener;
    }

    public void newValue(ProviderInstance i) throws RemoteException {
        listener.newValue(i);
    }
    public void removeValue(ProviderInstance i) throws RemoteException {
        listener.removeValue(i);
    }
    public void removeValue(ProviderInstance i, long time) throws RemoteException {
        listener.removeValue(i, time);
    }

}
