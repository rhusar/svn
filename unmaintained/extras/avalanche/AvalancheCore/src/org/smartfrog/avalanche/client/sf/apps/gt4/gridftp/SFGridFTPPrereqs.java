/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Feb 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.gridftp;

import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.CheckPrereqs;
import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.PrereqException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFGridFTPPrereqs extends PrimImpl implements Prim {
	private final String CVERSION = "cVersion";
	private final String TARVENDOR = "tarVendor";
	private final String SEDVENDOR = "sedVendor";
	private final String MAKEVENDOR = "makeVendor";
	private final String SHDTERMINATE = "shouldTerminate";
	
	private String cVersion,tarVendor;
	private String sedVendor, makeVendor;
	private boolean shouldTerminate;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFGridFTPPrereqs() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		cVersion = (String)sfResolve(CVERSION, cVersion, true);
		tarVendor = (String)sfResolve(TARVENDOR, tarVendor, true);
		sedVendor = (String)sfResolve(SEDVENDOR, sedVendor, true);
		makeVendor = (String)sfResolve(MAKEVENDOR, makeVendor, true);
		
		// optional attribute
		shouldTerminate = (boolean)sfResolve(SHDTERMINATE, true, false);
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();		
		CheckPrereqs chk = new CheckPrereqs();
		
		try {
			chk.checkCmd("tar", null, tarVendor);
			chk.checkCmd("sed", null, sedVendor);
			chk.checkCmd("cc", cVersion);
			chk.checkCmd("make", null, makeVendor);
		} catch(IOException ioe) {
			sfLog().err("Exception in checking pre-requisites", ioe);
			throw new SmartFrogException("Exception in checking pre-requisites", 
					ioe);
		} catch (PrereqException pe) {
			sfLog().err(pe);
			throw new SmartFrogException(pe);
		} catch (InterruptedException ie) {
			sfLog().err(ie);
			throw new SmartFrogException(ie);
		}
		
		sfLog().info("All pre-requisites for GT4-GridFTP satisfied");
		
		// terminate synchronously
		TerminationRecord tr = TerminationRecord.normal("Terminating ...", sfCompleteName());
		sfTerminate(tr);		
	}	

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
