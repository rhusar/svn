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
package org.smartfrog.services.restlet.test.system.testwar;

import org.smartfrog.test.PortChecker;
import org.smartfrog.test.PortCheckingTestBase;

/**
 *
 * Created 30-Nov-2007 16:46:45
 *
 */

public class TestwarTest extends PortCheckingTestBase {
    public static final String PACKAGE="/org/smartfrog/services/restlet/test/system/testwar";

    public TestwarTest(String name) {
        super(name);
    }

    private void addRestletPortCheck() {
        addPortCheck("Restlet server",5050);
    }

    public void testWarDeployed() throws Throwable {
        addRestletPortCheck();
        expectSuccessfulTestRunOrSkip(PACKAGE, "testWarDeployed");
    }

    public void testErrorPage() throws Throwable {
        PortChecker.PortPair restlet = createPortPair("Restlet server", 5050);
        assertFalse("Port open : "+ restlet, restlet.isOpen(500));

        addRestletPortCheck();
        expectSuccessfulTestRunOrSkip(PACKAGE, "testErrorPage");
    }
}