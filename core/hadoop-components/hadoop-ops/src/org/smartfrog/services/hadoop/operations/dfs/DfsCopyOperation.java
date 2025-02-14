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

/**
 * Created 17-Jun-2008 15:18:28
 */

public interface DfsCopyOperation extends DfsOperation {
    /**
     * {@value}
     */
    String ATTR_SOURCE = "source";
    /**
     * {@value}
     */
    String ATTR_DEST = "dest";
    /**
     * {@value}
     */
    String ATTR_OVERWRITE = "overwrite";

    /**
     * {@value}
     */
    String ATTR_BLOCKSIZE = "blocksize";

    /**
     * Regexp to use when singleFile==false
     * {@value}
     */
    String ATTR_PATTERN = "pattern";


    /**
     * {@value}
     */
    String ATTR_SOURCEFS = "sourceFS";

    /**
     * URL to the destination filesystem
     * {@value}
     */
    String ATTR_DESTFS = "destFS";

    /**
     * Flag to say delete source files after the copy
     */
    String ATTR_DELETE_SOURCE = "deleteSource";
}



