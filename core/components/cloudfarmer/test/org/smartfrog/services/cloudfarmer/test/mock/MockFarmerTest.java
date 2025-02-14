/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.test.mock;

import org.smartfrog.test.DeployingTestBase;

/**
 * Created 30-Nov-2007 16:46:45
 */

public class MockFarmerTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/mock/";

    public MockFarmerTest(String name) {
        super(name);
    }

    public void testFarmCreate() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmCreate");
    }

    public void testFarmCreateBounded() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmCreateBounded");
    }

    public void testFarmCreateBoundedbyRole() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmCreateBoundedbyRole");
    }

    public void testFarmCreateNoRole() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmCreateNoRole");
    }

    public void testFarmCreateNoRoom() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmCreateNoRoom");
    }

    public void testFarmHasRoles() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmHasRoles");
    }

    public void testFarmLacksRole() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmLacksRole");
    }

    public void testFarmRemembersDeployments() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmRemembersDeployments");
    }
}