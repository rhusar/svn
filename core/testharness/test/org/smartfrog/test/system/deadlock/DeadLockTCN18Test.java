/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.deadlock;

import org.smartfrog.test.SmartFrogTestBase;

/**
 * JUnit test class for test cases related to life cycle method "sfDeploy"
 */
public class DeadLockTCN18Test extends SmartFrogTestBase {

    private static final String FILES="org/smartfrog/test/system/deadlock/";

    public DeadLockTCN18Test(String s) {
        super(s);
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testCaseTCN18() throws Throwable {
        deployExpectingException(FILES+"tcn18.sf",
                "tcn18",
                "SmartFrogLifecycleException",
                "sfStart",
                "SmartFrogResolutionException",
                "cyclic reference");
    }

  
}
