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
--%><%@ include file="/html/mombasa-portlet/cluster/init.jsp" %>
<div class="separator"></div>
<table class="navigation" >
  <tbody>
    <tr class="navigation">
      <td>
        <html:link styleClass="navigation"
            action="/mombasa-portlet/cluster/list" styleId="listHosts">List Hosts</html:link>
      </td>
      <td>
        <html:link styleClass="navigation"
            action="/mombasa-portlet/cluster/listRoles" styleId="listRoles">List Roles</html:link>
      </td>
      <td>
        <html:link styleClass="navigation"
            action="/mombasa-portlet/cluster/hadoop" styleId="add">Hadoop Actions</html:link>
      </td>
      <td>
        <html:link styleClass="navigation"
            action="/mombasa-portlet/cluster/admin" styleId="admin">Administration</html:link>
      </td>
    </tr>
  </tbody>
</table>


