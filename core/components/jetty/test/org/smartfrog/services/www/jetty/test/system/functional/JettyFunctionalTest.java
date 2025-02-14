/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */

package org.smartfrog.services.www.jetty.test.system.functional;

import org.smartfrog.services.www.jetty.test.system.JettyTestBase;
import org.smartfrog.sfcore.prim.Prim;

import java.io.File;

public class JettyFunctionalTest extends JettyTestBase {

    public JettyFunctionalTest(String name) {
        super(name);
    }


    public void NotestCaseTCP21() throws Throwable {
        application = deployExpectingSuccess(FUNCTIONAL_FILES + "tcp21.sf", "tcp21");
        assertNotNull(application);
        Prim server = (Prim) application.sfResolve("server");
        String jettyhome = server.sfResolve("jettyhome", (String) null, true);
        String filename = jettyhome.concat(
                                                  File.separator + "demo"
                                                  + File.separator + "webapps"
                                                  + File.separator + "root"
                                                  + File.separator + "index.html");
        File file = new File(filename);
        File jettyfile = new File(jettyhome);

        assertTrue("Not found" + file, file.exists());
        assertTrue("Not found" + jettyfile, jettyfile.exists());
        assertFalse("Should not be a directory " + file, file.isDirectory());
        assertTrue("Should be a directory " + jettyfile,
                   jettyfile.isDirectory());
    }

    public void testTcp19() throws Throwable {
        addPortCheck("Jetty 1", TEST_JETTY_PORT_1);
        expectSuccessfulTestRun(FUNCTIONAL_FILES, "tcp19test");
    }

}
