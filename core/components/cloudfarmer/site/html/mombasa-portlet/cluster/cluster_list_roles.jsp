<%--
/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

For more information: www.smartfrog.org

*/
--%>
<%@ include file="/html/mombasa-portlet/cluster/init.jsp" %>

<tiles:useAttribute id="text" name="text" classname="java.lang.String" ignore="true"/>
<div>
<%= text %>
</div>
<div>
</div>
<table cellpadding="0" cellspacing="0">
 <tbody>
    
  <tr class="header">
    <th>Role</th>
    <th class="padding"></th>
    <th>Description</th>
    <th class="padding"></th>
    <th>Size</th>
    <th class="padding"></th>
    <th>Recommended</th>
    <th class="padding"></th>
    <th>Add</th>
  </tr>
  <logic:iterate id="role"
                 name="cluster.controller"
                 property="roles"
                 type="org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo">
    <tr>
      <td>
         <html:link action="/mombasa-portlet/cluster/listInRole"
            paramId="role" paramName="role" paramProperty="name">
           <bean:write name="role" property="name"/>
         </html:link>
      </td>
      <td class="padding"></td>
      <td><bean:write name="role" property="description"/></td>
      <td class="padding"></td>
      <td><bean:write name="role" property="roleSize"/></td>
      <td class="padding"></td>
      <td><bean:write name="role" property="recommendedSize"/></td>
      <td class="padding"></td>
      <td class="action">
         <html:link action="/mombasa-portlet/cluster/create_role_instance"
            paramId="role" paramName="role" paramProperty="name">
           Add
           <bean:write name="role" property="name"/>
         </html:link>
      </td>
    </tr>
  </logic:iterate>
 </tbody>
</table>
<div>

</div>

