/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.vast.helper;

import java.util.ArrayList;

/**
 * A helper class to setup a test node to run the vast test runner.
 */
public interface Helper {
    /**
     * Retrieves the names of the network interface cards.
     * @return The list of names.
     */
    public ArrayList<String> retrieveNICNames();

    /**
     * Sets the network address for a given NIC.
     * @param inNICName The name of the NIC.
     * @param inIP The IP address to set.
     * @param inMask The according network mask of the ip address.
     */
    public void setNetworkAddress(String inNICName, String inIP, String inMask);

	/**
	 * Sets the default gateway for the network.
	 * @param inGatewayAddress The gateway address.
	 * @param inNICName The name of the NIC.
	 */
	public void setDefaultGateway(String inGatewayAddress, String inNICName);

	/**
	 * Sets the hostname of this machine.
	 * @param inName The hostname.
	 */
	public void setHostname(String inName);

	/**
	 * Adds a DNS entry.
	 * @param inName The name of the host.
	 * @param inIP The ip of the host.
	 */
	public void addDNSEntry(String inName, String inIP);

	/**
	 * Shuts down a network interface card with the given ip.
	 * @param inNICName Name of the NIC.
	 * @param inIP The ip.
	 */
	public void cutNetworkConnection(String inNICName, String inIP);
}
