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
package org.smartfrog.services.hadoop.operations.conf;

import java.rmi.Remote;

/**
 * This is the hadoop configuration. It defines the names of the many, many hadoop options we want to allow people to
 * configure.
 */


public interface HadoopConfiguration extends Remote, ConfigurationAttributes {

    String ATTR_LOAD_DEFAULTS = "conf.load.defaults";
    String ATTR_FILES = "conf.files";
    String ATTR_RESOURCES = "conf.resources";
    String ATTR_READ_EARLY = "conf.read.early";
    String ATTR_DUMP = "conf.dump";
    String ATTR_REQUIRED = "conf.required";
}
