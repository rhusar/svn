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



import java.io.IOException;
import java.io.Serializable;

import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogImplAsyncWrapper;
import org.smartfrog.sfcore.logging.LogSF;


public class ValueData implements Serializable {
    static  private Object  noMarshall   = "state could not be marshalled";
    static  private Object  noUnmarshall = "state could not be unmarshalled";
    static  private LogSF   log          = new LogImplAsyncWrapper( LogFactory.getLog(ValueData.class.getClass().toString()) );
            private boolean marshalled;
            private Object  value;

    private ValueData(boolean marshalled, Object value) {
        this.marshalled   = marshalled;
        this.value        = value;
    }

    static public ValueData newMarshalledValue(Object value) {
        try {
            return new ValueData(true, new java.rmi.MarshalledObject(value) );
        } catch (IOException ex) {
            if( log.isErrorEnabled() )
                log.error("While creating a marshalled ValueData, failed to marshall the value: " + value, ex);
            return new ValueData(false, noMarshall);
        }
    }

    static public ValueData newValue(Object value) {
        return new ValueData(false, value);
    }

    static public ValueData nullValue() {
        return new ValueData(false, null);
    }

    public Object getValue() {
        if( marshalled ) {
            try {
                return ((java.rmi.MarshalledObject)value).get();
            } catch (ClassNotFoundException ex) {
                if( log.isErrorEnabled() )
                    log.error("Attempt to unmarshall a DataValue value in a JVM that does not have access to that class", ex);
                return noUnmarshall;
            } catch (IOException ex) {
                if( log.isErrorEnabled() )
                    log.error("Failed to unmarshall a DataValue value", ex);
                return noUnmarshall;
            }
        } else {
            return value;
        }
    }

    public String toString() {
        if( marshalled )
            try {
                return "Marshalled[ " + ( (java.rmi.MarshalledObject) value).get() + " ]";
            }
            catch (ClassNotFoundException ex) {
                return "Marshalled[ class not known here ]";
            }
            catch (IOException ex) {
                return "Marshalled[ IOException when unmarshalling ]";
            }
        else
            return getValue().toString();
    }
}
