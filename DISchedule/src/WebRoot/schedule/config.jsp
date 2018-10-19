<%@page import="com.taobao.pamirs.schedule.zk.ZKManager"%>
<%@page import="java.util.Properties"%>
<%@page import="com.taobao.pamirs.schedule.ConsoleManager"%>
<%@page import="com.taobao.pamirs.schedule.strategy.ScheduleStrategy"%>
<%@ page contentType="text/html; charset=utf-8" %>
<%
ConsoleManager.initial();
Properties  p = ConsoleManager.loadConfig();
String error = request.getParameter("error");
%>
<html>
<head>
<STYLE type=text/css>

TH{color:#5371BA;font-weight:bold;font-size:12px;background-color:#E4EFF1;display:block;}
TD{font-size:12px;}

</STYLE>
</head>
<body>
<h1>基础信息配置</h1>
<%if(error != null){%>
<div id ="error" style="font-size:18px;color:red">错误信息：<%=error%>,如果能够进入“管理页面”，请忽视该信息</div>

<%}%>

<form id="configForm" method="get" name="configForm" action="configDeal.jsp">
<table>
<tr>
  <td>Zookeeper地址:</td>
  <td><input type="text" name="zkConnectString"  value="<%=p.getProperty(ZKManager.keys.zkConnectString.toString())%>" style="width:300"></td>
  <td>格式: IP地址：端口</td>
</tr>
<tr>
  <td>Zookeeper超时:</td>
  <td><input type="text" name="zkSessionTimeout" value="<%=p.getProperty(ZKManager.keys.zkSessionTimeout.toString())%>" style="width:300"></td>
  <td>单位毫秒</td>
</tr>
<tr>
  <td>Zookeeper根目录：</td>
  <td><input type="text" name="rootPath" value="<%=p.getProperty(ZKManager.keys.rootPath.toString())%>" style="width:300"></td>
  <td>例如：/taobao-pamirs-schedule/huijin,，可以是一级目录，也可以是多级目录，注意不同调度域间不能有父子关系<br/>
      通过切换此属性来实现多个调度域的管理
  </td>
</tr>
<tr>
  <td>Zookeeper用户：</td>
  <td><input type="text" name="userName" value="<%=p.getProperty(ZKManager.keys.userName.toString())%>" style="width:300"></td>
  <td></td>
</tr>
<tr>
  <td>Zookeeper密码：</td>
  <td><input type="text" name="password" value="<%=p.getProperty(ZKManager.keys.password.toString())%>" style="width:300" ></td>
  <td>首次用来生成zookeeper上该文件夹的读写权限</td>
</tr>
</table>
<br/>
<!-- <input type="button" value="保存" onclick="save();" style="width:100px" > -->
<a href="index.jsp?manager=true">管理主页...</a>
<br/><br/>
<b><pre>
在工程目录下必需有一个application.properties的文件，该文件至少有2个键,
    1.system.name，即本工程所在的系统中,如
        system.name=pay,
        system.name=luxury
    2.tbSchedule.config.file ,真正指向的配置文件 值如 tbSchedule.config.file=file:/data/config/pay-config.xml,
	     a.windows系统上，file:/data/config/pay-config.xml 会指向 tomcat所有盘的根目录对应，如tomcat在盘G上，
	            则 file:/data/config/pay-config.xml会被指向file:/G:/data/config/pay-config.xml，
	     b.在linux,mac即指向目录.
	     c.在 /data/config/pay-config.xml 文件中，通过组合查找配置项，如 \${systemName}.scheduleManagerFactory.zkConfig.zkConnectString,
		     可以获得以下键，再查找到值，如果没有，将取默认值。
		     如 pay.scheduleManagerFactory.zkConfig.zkConnectString
		          pay.scheduleManagerFactory.zkConfig.rootPath
		          pay.scheduleManagerFactory.zkConfig.userName
		          pay.scheduleManagerFactory.zkConfig.password
		          pay.scheduleManagerFactory.zkConfig.zkSessionTimeout
		          pay.scheduleManagerFactory.zkConfig.isCheckParentPath
	     d.如果启动没成功，请配置好，再重启本控制台
</pre>
</b>
</form>

</body>
</html>

<script>
function save(){
    document.getElementById("configForm").submit();
}
</script>