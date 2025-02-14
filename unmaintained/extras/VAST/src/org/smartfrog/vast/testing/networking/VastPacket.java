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

package org.smartfrog.vast.testing.networking;

import org.smartfrog.vast.testing.networking.messages.VastMessage;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * A vast packet.
 */
public class VastPacket implements Serializable {
	private VastMessage vastMessage;
	private int Clock;
	private InetAddress Target;

	public VastPacket(int clock, VastMessage vastMessage) {
		Clock = clock;
		this.vastMessage = vastMessage;
	}

	public int getClock() {
		return Clock;
	}

	public void setClock(int clock) {
		Clock = clock;
	}

	public VastMessage getMessage() {
		return vastMessage;
	}

	public void setMessage(VastMessage vastMessage) {
		this.vastMessage = vastMessage;
	}

	public InetAddress getTarget() {
		return Target;
	}

	public void setTarget(InetAddress target) {
		Target = target;
	}
}
