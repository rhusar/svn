/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.java.libraries;

import org.smartfrog.test.DeployingTestBase;

/**
 * Test Ivy library work
 */

public class IvyLibraryTest extends DeployingTestBase {
    public static final String FILES = "org/smartfrog/test/system/java/libraries/";

    public IvyLibraryTest(String name) {
        super(name);
    }


    public void testLocalSmartFrog() throws Throwable {
        expectSuccessfulTestRun(FILES, "testLocalSmartFrog");
    }
    
    public void testMissingLocalArtifactFails() throws Throwable {
        expectSuccessfulTestRun(FILES, "testMissingLocalArtifactFails");
    }
    
}
