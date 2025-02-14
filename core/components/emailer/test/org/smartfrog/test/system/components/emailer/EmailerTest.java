/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test.system.components.emailer;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;

/**
 * JUnit test class for test cases related to "emailer" component
 */
public class EmailerTest
    extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/emailer/";

    public EmailerTest(String s) {
        super(s);
    }

    public void testCaseTCN64() throws Exception {
        deployExpectingException(FILES+"tcn64.sf",
                                 "tcn64",
                                 EXCEPTION_LIFECYCLE,
                                 "sfStart",
                                 EXCEPTION_SMARTFROG,
                                 "Unknown SMTP host: no-such-hostname");
    }

    public void testCaseTCN65() throws Exception {
        deployExpectingException(FILES+"tcn65.sf",
                                 "tcn65",
                                 EXCEPTION_DEPLOYMENT,
                                 null,
                                 EXCEPTION_LINKRESOLUTION,
                                 "error in schema: non-optional attribute 'to' is missing");
    }

    public void testCaseTCN66() throws Exception {
        deployExpectingException(FILES+"tcn66.sf",
                                 "tcn66",
                                 EXCEPTION_DEPLOYMENT,
                                 null,
                                 EXCEPTION_LINKRESOLUTION,
                                 "error in schema: non-optional attribute 'from' is missing");
    }

    public void testCaseTCN67() throws Exception {
        deployExpectingException(FILES+"tcn67.sf",
                                 "tcn67",
                                 EXCEPTION_DEPLOYMENT,
                                 null,
                                 EXCEPTION_LINKRESOLUTION,
                                 "error in schema: non-optional attribute 'smtpHost' is missing");
    }
}

