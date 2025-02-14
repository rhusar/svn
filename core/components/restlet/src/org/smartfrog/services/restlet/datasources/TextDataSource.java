/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.restlet.datasources;

import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * A source of text data
 */
public class TextDataSource extends AbstractRestletDataSource implements RestletDataSource {

    private static final String ATTR_TEXT = "text";
    private String text;

    public TextDataSource() throws RemoteException {
    }


    /**
     * load our content type
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        text = sfResolve(ATTR_TEXT,"",true);
    }

    /**
     * Load the representation. This is not remote, because Representations
     * don't serialize
     *
     * @return the representation
     *
     * @throws SmartFrogException failure to load
     * @throws RemoteException network trouble
     */
    public Representation loadRepresentation()
            throws SmartFrogException, RemoteException {
        return new StringRepresentation(text,getMediaType());
    }
}
