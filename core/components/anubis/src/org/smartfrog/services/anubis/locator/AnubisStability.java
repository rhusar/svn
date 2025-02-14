/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.locator;

import org.smartfrog.services.anubis.locator.util.ActiveTimeQueue;
import org.smartfrog.services.anubis.locator.util.TimeoutErrorLogger;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

abstract public class AnubisStability {

    private ActiveTimeQueue timers = null;
    protected LogSF log = LogFactory.getLog(this.getClass().toString());
    private boolean lastWasStable = true;
    private long    lastTimeRef   = -1;

    public void setTimerQueue(ActiveTimeQueue t) {
        timers = t;
    }

    public void notifyStability(boolean isStable, long timeRef) {

        /**
         * if the stability has changed notify the user
         */
        if( isStable != lastWasStable ) {
            lastWasStable = isStable;
            lastTimeRef = timeRef;
            safeStability(isStable, timeRef);
            return;
        }

        /**
         * if the stability has not changed and is unstable there
         * is nothing new to tell the user
         */
        if( !isStable ) {
            return;
        }

        /**
         * if the stability has not changed but is stable and the time
         * reference has changed, tell the user
         */
        if (lastTimeRef != timeRef) {
            lastWasStable = isStable;
            lastTimeRef = timeRef;
            safeStability(isStable, timeRef);
            return;
        }
    }

    public void safeStability(boolean isStable, long timeRef) {

            long   timein  = System.currentTimeMillis();
            long   timeout = 0;

            TimeoutErrorLogger timeoutErrorLogger = new TimeoutErrorLogger(log,
                    "User API Upcall took >200ms in stability(s,t) where s=" + isStable + ", t=" + timeRef);
            timers.add(timeoutErrorLogger, (timein+200) );
            try {
                stability(isStable, timeRef);
            } catch (Throwable ex) {
                if( log.isFatalEnabled() )
                    log.fatal("User API Upcall threw Throwable in stability(s,t) where s=" + isStable + ", t=" + timeRef, ex);
            }
            timeout = System.currentTimeMillis();
            timers.remove(timeoutErrorLogger);
            if( log.isTraceEnabled() )
                log.trace("User API Upcall took " + (timeout - timein) + "ms in stability(s,t) where s=" + isStable + ", t=" + timeRef);

    }

    abstract public void stability(boolean isStabile, long timeRef);
    
    public synchronized boolean isStable() {
    	return lastWasStable;
    }
    
    public synchronized long timeReference() {
    	return lastTimeRef;
    }

    public String toString() {
        return "[Stability notification interface]";
    }
}
