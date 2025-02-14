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
package org.smartfrog.services.anubis.partition.comms.multicast;

import java.rmi.RemoteException;

import org.smartfrog.services.anubis.basiccomms.connectiontransport.ConnectionAddress;
import org.smartfrog.services.anubis.basiccomms.multicasttransport.MulticastAddress;
import org.smartfrog.services.anubis.partition.protocols.heartbeat.HeartbeatReceiver;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.wire.security.WireSecurity;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

public class HeartbeatCommsFactory extends PrimImpl implements Prim {
    
    private WireSecurity wireSecurity = null;
    
    public HeartbeatCommsFactory() throws Exception {
        super();
    }
    
    public void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy();
        wireSecurity = (WireSecurity)sfResolve("security");
    }
    
    public HeartbeatCommsIntf create(MulticastAddress address, HeartbeatReceiver cs, String threadName, Identity id) throws Exception {
        return (HeartbeatCommsIntf)new HeartbeatComms(address, cs, threadName, id, wireSecurity);
    }

    public HeartbeatCommsIntf create(MulticastAddress address, ConnectionAddress inf, HeartbeatReceiver cs, String threadName, Identity id) throws Exception {
        return (HeartbeatCommsIntf)new HeartbeatComms(address, inf, cs, threadName, id, wireSecurity);
    }
}
