<%@ page language="java" import="java.util.*" isELIgnored="false" pageEncoding="UTF8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
  <head>
    <base href="<%=basePath%>">
    <title> </title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  </head>  
  <body>
	<a href="${pageContext.request.contextPath}/download?filename=表格.xls">下载</a>
	<a href="${pageContext.request.contextPath }/preview?filename=表格.xls">预览</a>
	<a href="${pageContext.request.contextPath }/wordTemplate?name=叶俊希">学生信息</a>
  </body>
</html>