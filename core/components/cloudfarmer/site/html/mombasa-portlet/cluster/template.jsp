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
<jsp:include page="/html/mombasa-portlet/header.jsp"/>
<tiles:useAttribute id="content" name="content" classname="java.lang.String" ignore="true"/>
<tiles:useAttribute id="title" name="title" classname="java.lang.String" ignore="true"/>
<html>
<head>
  <jsp:include page="/html/mombasa-portlet/html-head.jsp" flush="true"/>
  <title><%= title %>
  </title>
</head>
<body>
  <h2><%= title %></h2>
  <logic:messagesPresent>
    <span class="portlet-msg-error">
    <html:errors/>
    </span>
  </logic:messagesPresent>
  <div>
    <jsp:include page='<%= content %>' flush="true"/>
  </div>
  <div>
    <jsp:include page="/html/mombasa-portlet/cluster/cluster_nav.jsp" flush="true"/>
  </div>
</body>

</html>