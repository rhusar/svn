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
package org.smartfrog.services.velocity.test.system.transformer;

import org.smartfrog.test.DeployingTestBase;

/**
 * Created 28-Apr-2008 13:49:46
 */

public class TransformerTest extends DeployingTestBase {
    private static final String PACKAGE = "/org/smartfrog/services/velocity/test/system/transformer";

    public TransformerTest(String name) {
        super(name);
    }

    public void testLoad() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testLoad");
    }
    
    public void testGenerate() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testGenerate");
    }
}
