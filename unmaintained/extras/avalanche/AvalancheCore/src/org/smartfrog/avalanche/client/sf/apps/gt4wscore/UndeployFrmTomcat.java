/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4wscore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UndeployFrmTomcat {
	private String tomcatDir = null;
	private String version = null;
	
	private static Log log = LogFactory.getLog(UndeployFrmTomcat.class);

	/**
	 * 
	 */
	public UndeployFrmTomcat(String dir, String ver) {
		super();
		// TODO Auto-generated constructor stub
		
		tomcatDir = new String(dir);
		tomcatDir = tomcatDir.replace('\\', File.separatorChar);
		tomcatDir = tomcatDir.replace('/', File.separatorChar);
		
		version = new String(ver);		
	}
	
	public void undeployFrmTomcat(String webAppName)
			throws TomcatConfigException {
		File tomcatDirectory = new File(tomcatDir);
		if (!tomcatDirectory.exists()) {
			log.error("The directory " + tomcatDir + " does not exist.");
			log.error("Tomcat is not installed in the directory " + tomcatDir);
			throw new TomcatConfigException("Tomcat is not installed in the directory " + tomcatDir);
		}
		String dirName = tomcatDir + File.separatorChar + 
				"webapps" + File.separatorChar + webAppName;
		
		File dir = new File(dirName);
		if (!dir.exists()) {
			log.error("The directory " + dirName + " does not exist.");
			log.error("The webapp name " + webAppName + " is wrong or " + 
					"it is not deployed");
			throw new TomcatConfigException("The webapp name " + webAppName +
					" is wrong or it is not deployed");
		}
		
		try {
			log.debug("Deleting the web application " +
				webAppName + " from Tomcat...");
			DiskUtils.forceDelete(dir);			
		} catch(IOException ioe) {
			log.error("Error in deleting directory " + dirName);
			log.error("Failed to undeploy " + webAppName);
			throw new TomcatConfigException(ioe.toString());
		} 
		log.info("Successfully undeployed " + webAppName);
	}
	
	public void cleanXMLFiles() throws TomcatConfigException {
		String serverxml = new String(tomcatDir.concat("/conf/server.xml"));
		serverxml = serverxml.replace('\\', File.separatorChar);
		serverxml = serverxml.replace('/', File.separatorChar);

		EditXML xmlHelper = null;
		Element connector = null;
		Element valve = null;
		try {
			xmlHelper = new EditXML(serverxml);
			
			if (version.startsWith("4.1")) {
				connector = xmlHelper.getElementByTagNameAttrName("Connector", "className",
						"org.apache.catalina.connector.http.HttpConnector");
				xmlHelper.removeNode(connector);
				
				valve = xmlHelper.getElementByTagNameAttrName("Valve", "className",
				"org.globus.tomcat.catalina.valves.HTTPSValve");
				xmlHelper.removeNode(valve);								
			}
			else if (version.startsWith("5.")) {
				connector = xmlHelper.getElementByTagNameAttrName("Connector", 
						"className", "org.globus.tomcat.coyote.net.HTTPSConnector");
				xmlHelper.removeNode(connector);
							
				valve = xmlHelper.getElementByTagNameAttrName("Valve", "className",
						"org.globus.tomcat.coyote.valves.HTTPSValve");
				xmlHelper.removeNode(valve);
			}
			else {
				log.error("The version " + version + " is not supported");
				throw new TomcatConfigException("The version " + version + 
						" is not supported");
			}			
		} catch (ParserConfigurationException pce) {
			throw new TomcatConfigException(pce.toString());
		} catch (IOException ioe) {
			throw new TomcatConfigException(ioe.toString());
		} catch (SAXException se) {
			throw new TomcatConfigException(se.toString());
		} finally {
			try {
				xmlHelper.commitChanges();								
			}catch (FileNotFoundException fnf) {
				log.error(fnf);	
				throw new TomcatConfigException(fnf);
			}catch (TransformerException te) {
				log.error(te);				
				throw new TomcatConfigException(te);
			}	
		}
	}

}
