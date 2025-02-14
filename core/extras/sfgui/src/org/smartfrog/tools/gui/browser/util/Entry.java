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


package org.smartfrog.tools.gui.browser.util;


/*
 * BrowserEntry.java
 *
 */

import java.util.*;



/**
 *  Description of the Interface
 *
 *@author     julgui
 *@created    12 December 2001
 */
public interface Entry {
   /**
    *  Description of the Field
    */
   public static String CRLF = "\r\n";

   /**
    *  Description of the Method
    *
    *@param  DN  Description of Parameter
    *@return     Description of the Returned Value
    */
   public boolean remove(String DN);

   /**
    *  Description of the Method
    *
    *@param  DN     Description of Parameter
    *@param  value  Description of Parameter
    *@return        Description of the Returned Value
    */
   public boolean add(String DN, Object value);

   /**
    *  Gets the leaf attribute of the Entry object
    *
    *@return    The leaf value
    */
   public boolean isLeaf();

   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public String toString();

   /**
    *  Gets the name attribute of the Entry object
    *
    *@return    The name value
    */
   public String getName();

   /**
    *  Gets the parentDN attribute of the Entry object
    *
    *@return    The parentDN value
    */
   public String getParentDN();

   /**
    *  Gets the dN attribute of the Entry object
    *
    *@return    The dN value
    */
   public String getDN();

   /**
    *  Gets the rDN attribute of the Entry object
    *
    *@return    The rDN value
    */
   public String getRDN();

   /**
    *  Gets the childrenCount attribute of the Entry object
    *
    *@return    The childrenCount value
    */
   public int getChildrenCount();
   //public Object getChildren();
   //public Object getAttributes();
}
