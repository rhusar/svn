/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.download;

import org.smartfrog.test.SmartFrogTestBase;

/**
 * created 15-Apr-2005 14:16:30
 */

public class DownloadTest extends SmartFrogTestBase {


    public static final String FILES = "org/smartfrog/test/system/download/";


    public DownloadTest(String name) {
        super(name);
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testNoSuchHostname() throws Throwable {
        Throwable t = deployExpectingException(FILES + "testNoSuchHostname.sf",
                "testNoSuchHostname",
                EXCEPTION_LIFECYCLE, null,
                null, null);
        /*
        Throwable innermost=t.getCause().getCause();
        assertFaultCauseAndTextContains(innermost,
                "UnknownHostException",
                null,
                "");
                */
    }
}
