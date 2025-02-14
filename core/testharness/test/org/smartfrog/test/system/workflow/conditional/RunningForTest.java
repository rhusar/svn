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
package org.smartfrog.test.system.workflow.conditional;

import org.smartfrog.test.DeployingTestBase;

/**
 * tests the RunningFor condition that checks that a componet has been running for a given period of time
 */
public class RunningForTest extends DeployingTestBase {

    protected static final String FILES = "org/smartfrog/test/system/workflow/conditional/";

    public RunningForTest(String s) {
        super(s);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testWaitForRunning() throws Throwable {
        expectSuccessfulTestRun(FILES, "testWaitForRunning");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testRunningForFail() throws Throwable {
        expectSuccessfulTestRun(FILES, "testRunningForFail");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testRunningForPass() throws Throwable {
        expectSuccessfulTestRun(FILES, "testRunningForPass");
    }
}
