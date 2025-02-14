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


package org.smartfrog.test.system.dump;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.Dumper;
import org.smartfrog.sfcore.common.DumperCDImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * JUnit test class for test cases related to Subprocess Example
 */
public class SubProcessExampleDumpTest
        extends DeployingTestBase {

    // In this particular case we use the examples without screens
    //private static final String FILES = "org/smartfrog/examples/subprocesses/";
    //#include "org/smartfrog/test/system/deploy/subprocessTestHarness.sf"
    private static final String FILES = "org/smartfrog/test/system/deploy/";
    private static final Log log = LogFactory.getLog(SubProcessExampleDumpTest.class);

    static long timeout = 1 * 31 * 1000L;

    /**
     * Constructor
     * @param s name
     */
    public SubProcessExampleDumpTest(String s) {
        super(s);
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testCaseSubProcessExDump01() throws Throwable {
        System.out.println("\n*********************************************************" +
                           "\n    Testing: testCaseSubProcessExDump01."+
                           "\n*********************************************************");

        application = deployExpectingSuccess(FILES + "subprocessTestHarness.sf", "tcSPEDump01");
        assertNotNull(application);

        String actualSfClass = (String) application.sfResolveHere("sfClass");
        assertEquals("org.smartfrog.sfcore.compound.CompoundImpl", actualSfClass);

        //Some basic check
        Prim sys = (Prim) application.sfResolveHere("system");
        assertEquals("first", sys.sfDeployedProcessName());

        Prim foo = (Prim) sys.sfResolveHere("foo");
        assertEquals("test", foo.sfDeployedProcessName());

        Prim bar = (Prim) foo.sfResolveHere("bar");
        assertEquals("test2", bar.sfDeployedProcessName());

        ComponentDescription cd = application.sfDiagnosticsReport();
        assertNotNull("No Diagnostics report", cd);
        log.info("Diagnostics report: \n" + cd);
        //Testing Dump now
        try {
          StringBuffer message = new StringBuffer();
          message.append ( application.sfCompleteName().toString() );
          message.append ("\n");
          message.append ( dumpState(application));
          System.out.println(message);
          log.info(message);
        } catch (Exception ex){
            System.err.println("Error: "+ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("testCaseSubProcessExDump01 Success.");


    }

	    /**
     * test case
     * @throws Throwable on failure
     */

    public void testCaseSubProcessExDump02() throws Throwable {
            System.out.println("\n*********************************************************" +
                               "\n    Testing: testCaseSubProcessExDump02."+
                               "\n*********************************************************");
        application =null;
        application = deployExpectingSuccess(FILES + "subprocessSimple.sf", "tcSPEDump02");
        assertNotNull(application);

        //Testing Dump now
        try {
          StringBuffer message = new StringBuffer();
          message.append ( application.sfCompleteName().toString() );
          message.append ("\n");
          message.append ( dumpState(application));

          System.out.println(message);
          log.info(message);
        } catch (Exception ex){
            System.err.println("Error: "+ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("testCaseSubProcessExDump02 Success.");
    }
	
	    /**
     * test case
     * @throws Throwable on failure
     */

    public void testCaseSubProcessExDump03() throws Throwable {
            System.out.println("\n*********************************************************" +
                               "\n    Testing: testCaseSubProcessExDump03."+
                               "\n*********************************************************");

        application = deployExpectingSuccess(FILES + "subprocessNo.sf", "tcSPEDump03");
        assertNotNull(application);

        //Testing Dump now
        try {
          StringBuffer message = new StringBuffer();
          message.append ( application.sfCompleteName().toString() );
          message.append ("\n");
          message.append ( dumpState(application));
          System.out.println(message);
          log.info(message);
        } catch (Exception ex){
            System.err.println("Error: "+ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("testCaseSubProcessExDump03 Success.");
    }


/**
     * test case
     * @throws Throwable on failure
     */

    public void testCaseSubProcessExDump04() throws Throwable {

        System.out.println("\n*********************************************************" +
                           "\n    Testing: testCaseSubProcessExDump04. (Should fail, you cannot dump root daemon)"+
                            "\n*********************************************************");

        application = deployExpectingSuccess(FILES + "subprocessTestHarness.sf", "tcSPEDump04");
        assertNotNull(application);

        String actualSfClass = (String) application.sfResolveHere("sfClass");
        assertEquals("org.smartfrog.sfcore.compound.CompoundImpl", actualSfClass);

        //Some basic check
        Prim sys = (Prim) application.sfResolveHere("system");
        assertEquals("first", sys.sfDeployedProcessName());

        Prim foo = (Prim) sys.sfResolveHere("foo");
        assertEquals("test", foo.sfDeployedProcessName());

        Prim bar = (Prim) foo.sfResolveHere("bar");
        assertEquals("test2", bar.sfDeployedProcessName());

        ComponentDescription cd = application.sfDiagnosticsReport();
        assertNotNull("No Diagnostics report", cd);
//        log.info("Diagnostics report: \n" + cd);
//        System.out.println("Diagnostics report: \n" + cd);
        //Testing Dump now with rootProcess (This will fail until loop references are solved)
        Prim root = (Prim) application.sfResolveWithParser("HOST localhost");
        StringBuffer message = new StringBuffer();
        assertNotNull(root);
        boolean success = false;
        String messageEx = null;
        try {
          message.append ( root.sfCompleteName().toString() );
          message.append ("\n");
          message.append ( dumpState(root));
          System.out.println(message);
          log.info(message);

        } catch (Throwable ex){
            messageEx =  ex.getMessage().toString();
            //String messageS =  "Error: "+message+"\n"+messageEx;
            String messageS = " Test successful: It failed to dump description of root daemon when circular having references" + "; "+ messageEx+"\n"+ message;
            //log.info(messageS);
            //ex.printStackTrace();
            System.out.println("\n **** testCaseSubProcessExDump04: Success. **** "+ messageS);
            success = true;
        }
        if (success!=true) {
            String messageS = " Test unsuccessful: It should failed to dump description of root daemon when circular having references" + "; "+ messageEx;
            log.info(messageS);
            throw new Exception (messageS);
        }

    }
	

    /**
     * Cast the parameter toa prim and dump it
     * @param node node to dump
     * @return the dup
     */
    public String dumpState(Object node) throws Exception {
        StringBuffer message = new StringBuffer();
        String name = "error";
        //Only works for Prims.
        if (node instanceof Prim) {
            try {
                Prim objPrim = ((Prim) node);
                message.append("\n*************** State *****************\n");
                Dumper dumper = new DumperCDImpl(objPrim);
                objPrim.sfDumpState(dumper.getDumpVisitor());
				name = (objPrim).sfCompleteName().toString();
                message.append(dumper.toString(timeout));
            } catch (Exception ex) {
                log.error(ex);
                StringWriter sw = new StringWriter();
                PrintWriter pr = new PrintWriter(sw, true);
                ex.printStackTrace(pr);
                pr.close();
                message.append("\n **** Error: \n" + ex.toString() + "\n StackTrace: \n" + sw.toString());
                System.out.println(message);
                fail(message.toString());
                throw ex;
            }
        }
        return ("State for " + name + "\n" + message.toString());

    }

}
