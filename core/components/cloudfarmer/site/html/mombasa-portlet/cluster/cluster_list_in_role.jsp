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
Total number of hosts in role <bean:write name="role" /> : <bean:write name="hostcount"/>
</div>
<table cellpadding="0" cellspacing="0">
 <tbody>
  <tr class="header">
    <th>Hostname</th>
    <th class="padding"></th>
    <th>State</th>
    <th class="padding"></th>
    <th>Application</th>
    <th class="padding"></th>
    <th>Role</th>
    <th class="padding"></th>
    <th>Description</th>
  </tr>
  <logic:iterate id="host"
                 name="hosts"
                 property="list"
                 type="org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance">
    <tr>
      <td>
        <html:link action="/mombasa-portlet/cluster/viewhost"
            paramId="hostid" paramName="host" paramProperty="id">
          <bean:write name="host" property="hostname"/>
        </html:link>
      </td>
      <td class="padding"></td>
      <td><bean:write name="host" property="state"/></td>
      <td class="padding"></td>
      <td><bean:write name="host" property="application"/></td>
      <td class="padding"></td>
      <td>
        <logic:notEmpty name="host" property="role">
          <bean:write name="host" property="role"/>
        </logic:notEmpty>
      </td>
      <td class="padding"></td>
      <td>
        <logic:notEmpty name="host" property="application">
          <logic:notEmpty name="host" property="application.description">
            <bean:write name="host" property="application.description"/>
          </logic:notEmpty>
        </logic:notEmpty>
      </td>
    </tr>
  </logic:iterate>
  <tr>
    <td class="action">
      <html:link action="/mombasa-portlet/cluster/create_role_instance"
                 paramId="role" paramName="role" >
        Add
        <bean:write name="role" />
      </html:link>
    </td>
  </tr>
  <tr>
    <td class="action">
      <html:link action="/mombasa-portlet/cluster/delete_all_in_role"
                 paramId="role" paramName="role" >
        Delete all machines in the role 
        <bean:write name="role" />
      </html:link>
    </td>
  </tr>  
 </tbody>
</table>




