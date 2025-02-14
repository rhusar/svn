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
package org.smartfrog.services.ant;

import org.apache.tools.ant.BuildException;

/**
 * An exception to indicate that the build was halted; this can be raised inside Ant classes and will
 * be processed as a BuildException.
 * <p/>
 * Created 02-Nov-2007 13:00:18
 *
 */

public class BuildInterruptedException extends BuildException {


    /**
     * Constructs a build exception with no descriptive information.
     */
    public BuildInterruptedException() {
    }

    /**
     * Constructs an exception with the given descriptive message.
     *
     * @param message A description of or information about the exception.
     *            Should not be <code>null</code>.
     */
    public BuildInterruptedException(String message) {
        super(message);
    }
}
