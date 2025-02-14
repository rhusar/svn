package org.smartfrog.services.groovy.install.download

import java.rmi.Remote
import java.rmi.RemoteException
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.prim.Prim

/**
 * Interface for accessing a remote data source
 */
interface ISource extends Prim, Remote {

    /**
     * Copy all files from remoteURL into local temp directory
     * @return True if successful
     */
    String ATTR_DEST_DIR = "destDir"
    String ATTR_SOURCE = "source"

    boolean retrieve() throws RemoteException, SmartFrogException
}
