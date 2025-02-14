
/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.tools.eclipse.ui.runner;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.model.Util;
import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;

/**
 * all information for SmartFrog external tools:file name, configuration, environment
 */
abstract class ISfRunnerExt
    extends IRunner
{
    //  Default SF Commands ...
    protected final static String CMD_SFDaemon =
        "-Dorg.smartfrog.sfcore.processcompound.sfProcessName="; //$NON-NLS-1$

    protected final static String SfDaemonProcessName = "rootProcess"; //$NON-NLS-1$

    protected final static String SfDaemonDefIniFileProperty =
        "-Dorg.smartfrog.iniFile="; //$NON-NLS-1$

    protected final static String SfDaemonDefSFFileProperty =
        "-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault="; //$NON-NLS-1$

    protected String mSfDaemonDefIniFile =
    	 SmartFrogPreferencePage.getSmartFrogLocation() + ISmartFrogConstants.FILE_SEPARATOR +"bin" + ISmartFrogConstants.FILE_SEPARATOR +"default.ini" ; //$NON-NLS-1$

    protected String mSfDaemonDefSFFile =
    	SmartFrogPreferencePage.getSmartFrogLocation() + ISmartFrogConstants.FILE_SEPARATOR +"bin" + ISmartFrogConstants.FILE_SEPARATOR +"default.sf" ; //$NON-NLS-1$

    //"sfDaemon";
    protected final static String SfDaemonFile =
    	 SmartFrogPreferencePage.getSmartFrogLocation() + ISmartFrogConstants.FILE_SEPARATOR + "bin" +ISmartFrogConstants.FILE_SEPARATOR +"daemon.sf" ; //$NON-NLS-1$

    protected final static String CMD_AddSecurity = "security"; //$NON-NLS-1$
    protected final static String CMD_SFPrase = "sfParse"; //$NON-NLS-1$
    

    protected final static String SfSystemClass = "org.smartfrog.SFSystem"; //$NON-NLS-1$
    protected final static String SfParseClass = "org.smartfrog.SFParse";
   

    protected final static String JAVA =  SmartFrogPreferencePage.getRmiLocation() + ISmartFrogConstants.FILE_SEPARATOR +"java" ; //$NON-NLS-1$
    protected static final String CMD_SFMANAGEMENT_CONSOLE =
        "sfManagementConsole"; //$NON-NLS-1$

    protected static final String CMD_SFPROCESS_START = "smartfrog"; //$NON-NLS-1$

    protected static final String CMD_SFPROCESS_TERMINATE = "sfTerminate"; //$NON-NLS-1$

 /*   protected String mClassPath =
        SmartFrogPreferencePage.getSmartFrogLocation() +
        ISmartFrogConstants.SMARTFROG_LIBS[ 0 ] +
        Util.getClassSeparator() +
        SmartFrogPreferencePage.getSmartFrogLocation() +
        ISmartFrogConstants.SMARTFROG_LIBS[ 1 ] +
        Util.getClassSeparator() +
        SmartFrogPreferencePage.getSmartFrogLocation() +
        ISmartFrogConstants.SMARTFROG_GUI_TOOLS_LIB;
  */


      protected String mClassPath =
        SmartFrogPreferencePage.getSmartFrogLocation() +
        SmartFrogPlugin.getSmartFrogLib()[ 0 ] +
        Util.getClassSeparator() +
        SmartFrogPreferencePage.getSmartFrogLocation() +
       SmartFrogPlugin.getSmartFrogLib()[ 1 ] +
        Util.getClassSeparator() +
        SmartFrogPreferencePage.getSmartFrogLocation() +
        ISmartFrogConstants.SMARTFROG_GUI_TOOLS_LIB;
    public boolean isRunning()
    {
        return ( mProcess != null );
    }


    /** The OS name. */
    private static final String OS_NAME = System.getProperty("os.name");
    /** The OS version. */
    private static final String OS_VERSION = System.getProperty("os.version");
    /** Windows OS Prefix String */
    private static final String WINDOWS_OS_PREFIX = "Windows";
    private static final String WINDOWS_OS_9x_PREFIX = "Windows 9";
    private static final String WINDOWS_OS_95_MINOR = "4.0";
    private static final String WINDOWS_OS_98_MINOR = "4.1";

    /**
     * Matches the JAVA OS String with the strings provided.
     *
     * @param osName    the OS to check, e.g. WINDOWS
     * @param osVersion the version e.g. 4.0
     * @return true if match, false otherwise
     */
    private static boolean checkOS(final String osName, final String osVersion) {

        return !(osName == null || osVersion == null) && OS_NAME.startsWith(osName) && OS_VERSION.startsWith(osVersion);
    }

    /**
     * return the command name in different OS
     *
     * @return the command array
     */
    protected String[] getCommandGeneralArray() {
        String cmds[];

        if (OS_NAME.startsWith(WINDOWS_OS_PREFIX)) {
            // windows OS
            // windows 95/98 ?
            if (checkOS(WINDOWS_OS_9x_PREFIX, WINDOWS_OS_95_MINOR) || checkOS(WINDOWS_OS_9x_PREFIX, WINDOWS_OS_98_MINOR)) {
                cmds = new String[]{"command.exe", "/C"};
            } else {
                // assume all other windows versions have a cmd.exe on the path
                cmds = new String[]{"cmd.exe", "/C"};
            }
        } else {
            cmds = new String[]{"bash"};
        }

        return cmds;
    }
}
