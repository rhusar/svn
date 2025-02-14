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
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.Location;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;

import java.lang.reflect.InvocationTargetException;

/**
 * an exception type with special handling of Ant BuildExceptions, and the ability to recognise and unwrap invocation
 * target exceptions, handling any nested BuildException appropriately. Created 31-Oct-2007 13:47:02
 */

public class SmartFrogAntBuildException extends SmartFrogRuntimeException {

    private Location location = Location.UNKNOWN_LOCATION;
    private int exitStatus;
    private boolean hasExitStatus;
    private boolean buildInterrupted;

    /**
     * Constructs a SmartFrogRuntimeException with message.
     *
     * @param message exception message
     */
    public SmartFrogAntBuildException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogRuntimeException with cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogAntBuildException(Throwable cause) {
        super(cause);
        maybeBind(cause);
    }

    /**
     * Constructs a SmartFrogRuntimeException with cause. Also initializes the exception context with component
     * details.
     *
     * @param cause    exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogAntBuildException(Throwable cause, Prim sfObject) {
        super(cause, sfObject);
        maybeBind(cause);
    }

    /**
     * Constructs a SmartFrogRuntimeException with message and cause.
     *
     * @param message exception message
     * @param cause   exception causing this exception
     */
    public SmartFrogAntBuildException(String message, Throwable cause) {
        super(message, cause);
        maybeBind(cause);
    }

    /**
     * Constructs a SmartFrogRuntimeException with message. Also initializes the exception context with component
     * details.
     *
     * @param message  message
     * @param sfObject component that encountered exception
     */
    public SmartFrogAntBuildException(String message, Prim sfObject) {
        super(message, sfObject);
    }

    /**
     * Constructs a SmartFrogRuntimeException with message and cause. Also initializes  the exception context with
     * component details.
     *
     * @param message  message
     * @param cause    exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogAntBuildException(String message, Throwable cause, Prim sfObject) {
        super(message, cause, sfObject);
        maybeBind(cause);
    }

    /**
     * build from a BuildException; the location is extracted.
     *
     * @param source source exception.
     */
    public SmartFrogAntBuildException(BuildException source) {
        super(source.toString(), source);
        //inherit the location
        bind(source);
    }

    private void maybeBind(Throwable cause) {
        if (cause instanceof BuildException) {
            bind((BuildException) cause);
        }
    }

    /**
     * Bind to a build exception; looking for specific subclasses and extracting more information in these situations.
     *
     * @param source source exception
     */
    private void bind(BuildException source) {
        location = source.getLocation();
        if (source instanceof ExitStatusException) {
            ExitStatusException ese = (ExitStatusException) source;
            exitStatus = ese.getStatus();
            hasExitStatus = true;
        } else if (source instanceof BuildInterruptedException) {
            buildInterrupted = true;
        }
    }


    /**
     * Look inside the invocation exception, and extract any underlying fault. If it is a BuildException, handle it
     * specially.
     *
     * @param te the thrown exception
     */
    public SmartFrogAntBuildException(InvocationTargetException te) {
        super(te.getCause() != null ? te.getCause().getMessage() : te.getMessage(),
              te.getCause() != null ? te.getCause() : te);
        Throwable rootCause = te.getCause();
        if (rootCause != null) {
            maybeBind(rootCause);
        }
    }

    /**
     * Get the location of the exception; the build file and line
     *
     * @return the location, or {@link Location#UNKNOWN_LOCATION}
     */
    public Location getLocation() {
        return location;
    }


    /**
     * If initiated from an exit status exception, this will include the exit code
     *
     * @return the exit status. This is only valid if {@link #hasExitStatus()} is true
     */
    public int getExitStatus() {
        return exitStatus;
    }

    /**
     * Test for the class having an exit status
     *
     * @return true if the exit status value was provided by an ExitStatusException.
     */
    public boolean hasExitStatus() {
        return hasExitStatus;
    }

    /**
     * Is the exception from the build being interrupted
     *
     * @return true if this exception was built from a BuildInterruptedEvent
     */
    public boolean isBuildInterrupted() {
        return buildInterrupted;
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     * @return SmartFrogException that is a SmartFrogAntBuildException
     */
    public static SmartFrogAntBuildException forward(Throwable thr) {
        return forward(thr.toString(), thr);
    }


    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param message message
     * @param thr     throwable object to be forwarded
     * @return Throwable that is a SmartFrogAntBuildException
     */
    public static SmartFrogAntBuildException forward(String message, Throwable thr) {
        if (thr instanceof SmartFrogAntBuildException) {
            if (message != null) {
                ((SmartFrogAntBuildException) thr).add("msg: ", message);
            }
            return (SmartFrogAntBuildException) thr;
        }
        if (thr instanceof BuildException) {
            return new SmartFrogAntBuildException((BuildException) thr);
        }
        if (thr instanceof InvocationTargetException) {
            InvocationTargetException ite = (InvocationTargetException) thr;
            if (ite.getCause() instanceof BuildException) {
                return new SmartFrogAntBuildException((BuildException) ite.getCause());
            } else {
                return new SmartFrogAntBuildException(ite);
            }
        } else {
            return new SmartFrogAntBuildException(message, thr);
        }
    }
}


