<%@ page isErrorPage="false" %>
<%@ include file="/html/mombasa-portlet/common/patcherrorpages.jsp" %>
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


<%
  /*
  * This is the error reporting page. If an exception is attached in the attribute "org.apache.struts.action.EXCEPTION", it gets displayed
  * If an errorMessage is attached, it is printed
  */
%>

<h2>Error: <bean:write name="errorMessage"/></h2>

<div>
<jsp:include page="/html/mombasa-portlet/cluster/cluster_nav.jsp" flush="true"/>
</div>

<%@ include file="/html/mombasa-portlet/common/printStrutsException.jsp" %>
<%@ include file="/html/mombasa-portlet/common/printMessages.jsp" %>


