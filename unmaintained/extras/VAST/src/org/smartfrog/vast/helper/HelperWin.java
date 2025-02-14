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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

public class HelperWin implements Helper {
	private PrintStream out;

	public HelperWin(PrintStream out) {
		this.out = out;
	}

	public ArrayList<String> retrieveNICNames() {
		ArrayList<String> names = new ArrayList<String>();

		try {
			// execute the ifconfig command
			Process ps = Runtime.getRuntime().exec("ipconfig -all");

			// read the output and the error stream
			// doing this asynchronously prevents blocking
			// of the process
			ArrayList<String> outBuffer = new ArrayList<String>();
			ArrayList<String> errBuffer = new ArrayList<String>();
			ReaderThread rtOut = new ReaderThread(ps.getInputStream(), outBuffer);
			ReaderThread rtErr = new ReaderThread(ps.getErrorStream(), errBuffer);
			rtOut.start();
			rtErr.start();

			// wait for ifconfig to finish
			ps.waitFor();

			// wait for the output reader to finish
			while (rtOut.getState() != Thread.State.TERMINATED)
				Thread.sleep(100);

			// parse the output
			Pattern pattern = Pattern.compile("^Ethernet\\sadapter\\s([\\w\\s]+)\\:$");
			for (String strLine : outBuffer)
			{
				out.println(strLine);
				Matcher matcher = pattern.matcher(strLine);
				if (matcher.matches()) {
					names.add(matcher.group(1));
				}
			}

		} catch (Exception e) {
			e.printStackTrace(out);
		}

		return names;
	}

	public void setNetworkAddress(String inNICName, String inIP, String inMask) {
		// check addresses
		if (Validator.isValidIP(inIP) && Validator.isValidSubnetMask(inMask)) {
			try {
				Process ps = Runtime.getRuntime().exec(String.format("netsh int ip add address \"%s\" %s %s", inNICName, inIP, inMask));

				// just for blocking prevention
				ArrayList<String> outBuffer = new ArrayList<String>();
				ArrayList<String> errBuffer = new ArrayList<String>();
				ReaderThread rtOut = new ReaderThread(ps.getInputStream(), outBuffer);
				ReaderThread rtErr = new ReaderThread(ps.getErrorStream(), errBuffer);
				rtOut.start();
				rtErr.start();

				ps.waitFor();
			} catch (Exception e) {
				e.printStackTrace(out);
			}
		}
	}

	public void setDefaultGateway(String inGatewayAddress, String inNICName) {
		if (Validator.isValidIP(inGatewayAddress)) {
			try {
                Process ps = Runtime.getRuntime().exec(String.format("netsh interface ip set address name=\"%s\" gateway=%s gwmetric=1", inNICName, inGatewayAddress));

				// just for blocking prevention
				ArrayList<String> outBuffer = new ArrayList<String>();
				ArrayList<String> errBuffer = new ArrayList<String>();
				ReaderThread rtOut = new ReaderThread(ps.getInputStream(), outBuffer);
				ReaderThread rtErr = new ReaderThread(ps.getErrorStream(), errBuffer);
				rtOut.start();
				rtErr.start();

				ps.waitFor();
            } catch (Exception e) {
                e.printStackTrace(out);
            }
		}
	}

	public void setHostname(String inName) {
		out.println("setHostname not supported on command line under windows.");
	}

	public void addDNSEntry(String inName, String inIP) {
		if (Validator.isValidIP(inIP)) {
			try {
                Process ps = Runtime.getRuntime().exec(String.format("netsh interface ip add dns %s %s", inName, inIP));

				// just for blocking prevention
				ArrayList<String> outBuffer = new ArrayList<String>();
				ArrayList<String> errBuffer = new ArrayList<String>();
				ReaderThread rtOut = new ReaderThread(ps.getInputStream(), outBuffer);
				ReaderThread rtErr = new ReaderThread(ps.getErrorStream(), errBuffer);
				rtOut.start();
				rtErr.start();

				ps.waitFor();
            } catch (Exception e) {
                e.printStackTrace(out);
            }
		}
	}

	public void cutNetworkConnection(String inNICName, String inIP) {
		if (Validator.isValidIP(inIP)) {
			try {
				Process ps = Runtime.getRuntime().exec(String.format("netsh int ip delete address \"%s\" %s", inNICName, inIP));

				// just for blocking prevention
				ArrayList<String> outBuffer = new ArrayList<String>();
				ArrayList<String> errBuffer = new ArrayList<String>();
				ReaderThread rtOut = new ReaderThread(ps.getInputStream(), outBuffer);
				ReaderThread rtErr = new ReaderThread(ps.getErrorStream(), errBuffer);
				rtOut.start();
				rtErr.start();
	
				ps.waitFor();
			} catch (Exception e) {
				e.printStackTrace(out);
			}
		}
	}
}
