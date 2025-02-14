/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.creation;

import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

/**
 * created 13-Apr-2006 13:51:02
 * Create a system , then destroy it immediately.
 */

public class Api_13_system_multiple_systems_Test extends StandardTestBase {
    private SystemSession system2;
    private String resID;
    private String resID2;

    public Api_13_system_multiple_systems_Test(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        createSystem(null);
        system2 = getPortal().create(null);
        resID = getSystem().getResourcePropertySingle(CddlmConstants.PROPERTY_MUWS_RESOURCEID).getValue();
        resID2 = system2.getResourcePropertySingle(CddlmConstants.PROPERTY_MUWS_RESOURCEID).getValue();
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
        if (system2 != null) {
            system2.destroy();
        }
    }

    public void testFirstSystemValid() throws Exception {
        assertNotNull(resID);
        assertTrue(resID.length() > 0);
    }

    public void testSystem2Valid() throws Exception {
        assertNotNull(resID2);
        assertTrue(resID2.length() > 0);
    }

    public void testMultipleSystems() throws Exception {
        assertFalse("resources have the same ID", resID2.equals(resID));
    }

    public void testActiveSystemFound() throws Exception {
        boolean found = isSystemInActiveSystems(resID);
        assertTrue("Created system" + resID + " not found in the list of active systems", found);
    }

    public void testActiveSystem2Found() throws Exception {
        boolean found = isSystemInActiveSystems(resID2);
        assertTrue("Created system " + resID2 + "not found in the list of active systems", found);
    }


}
