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
package org.smartfrog.services.cloudfarmer.client.web.exceptions;

import org.smartfrog.sfcore.common.SmartFrogException;


/**
 * Create this when the farmer is not live and you've asked for a farmer-live-only operation
 */

public class FarmerNotLiveException extends SmartFrogException {
    public static final String ERROR_NOT_LIVE = "The cluster farmer is not live";

    public FarmerNotLiveException() {
        super(ERROR_NOT_LIVE);
    }

    public FarmerNotLiveException(String message) {
        super(message);
    }
}
