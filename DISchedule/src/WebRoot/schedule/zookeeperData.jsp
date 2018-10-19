<%@page import="java.io.StringWriter"%>
<%@page import="com.taobao.pamirs.schedule.ConsoleManager"%>
<%@ page contentType="text/html; charset=utf-8" %>
<%
if(ConsoleManager.isInitial() == false){
		response.sendRedirect("config.jsp");
}
%>
<html>
<body style="font-size:12px;">
<%
  StringWriter writer = new StringWriter();
  ConsoleManager.getScheduleStrategyManager().printTree(
  ConsoleManager.getScheduleStrategyManager().getRootPath(),writer,"<br/>");
%>
<pre>
<%=writer.getBuffer().toString()%>
</pre>
</body>
</html>
