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
package org.smartfrog.test.unit.sfcore;


import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * created 16-Apr-2004 11:36:32
 */

public class SchemaTest extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/unit/sfcore/";


    public SchemaTest(String name) {
        super(name);
    }

    public void testSchemaVector() throws SmartFrogException {
        Phases phases=parseLocalFile(FILES+"testSchemaVector.sf");
        assertNotNull(phases);
    }

    public void testSchemaOptionalVector() throws SmartFrogException {
        Phases phases = parseLocalFile(FILES + "testSchemaOptionalVector.sf");
        assertNotNull(phases);
    }
}
