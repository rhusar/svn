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


package org.smartfrog.services.ant.test.system;

import org.smartfrog.services.ant.Ant;
import org.smartfrog.services.ant.AntRuntime;
import org.smartfrog.sfcore.annotations.Description;
import org.smartfrog.sfcore.annotations.SkippedTest;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;

/**
 * JUnit test class for test cases related to Ant
 */
public class AntComplexPropertiesTest
        extends DeployingTestBase {

    private static final String FILES = "/org/smartfrog/services/ant/test/system/";

    public AntComplexPropertiesTest(String s) {
        super(s);
    }

    @Description("Execute a long sequence of operations")
    public void testProperties() throws Throwable {
        expectSuccessfulTestRun(FILES, "testProperties");
    }


    @Description("JVM properties can be set/got")
    @SkippedTest("fails to resolve")
    public void testPropertiesAdvanced() throws Throwable {
        expectSuccessfulTestRun(FILES,  "testPropertiesAdvanced");
/*
        Prim antprim;
        //antprim = application.sfResolve("",(Prim)null, true);
        antprim = application;
        Ant ant = (Ant) antprim;
        Prim runtime = antprim.sfResolve(Ant.ATTR_RUNTIME, (Prim) null, true);
        String message = runtime.sfResolveHere("java.version").toString();
        assertTrue("missing text from " + message, message.contains("1."));
        message = runtime.sfResolveHere("pathtext").toString();
        assertTrue("missing text from " + message, message.contains("path="));
        assertFalse("unexpanded text in " + message, message.contains("${path}"));
        assertFalse("unexpanded text in " + message, message.contains("${env."));
*/
    }


}

