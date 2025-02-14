/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Feb 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.smartfrog.avalanche.server.ftp;

import org.smartfrog.avalanche.server.RepositoryConfig;
import org.smartfrog.avalanche.server.modules.Repository;
import org.smartfrog.avalanche.server.modules.RepositoryConfigException;
import org.smartfrog.avalanche.server.modules.RepositoryFactory;

/**
 * @author sanjay
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class FTPRepositoryFactory extends RepositoryFactory {
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.RepositoryFactory#getRepository(org.smartfrog.avalanche.repository.RepositoryConfig)
	 */
	public Repository getRepository(RepositoryConfig config)
			throws RepositoryConfigException {
		Repository rep = new FTPRepository(config);
		return rep;
	}
}
