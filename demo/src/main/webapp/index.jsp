<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<html>
<body>
<h2>Hello World!</h2>
<a href="${pageContext.request.contextPath}/exportNull">测试导出空表</a>
<a href="${pageContext.request.contextPath}/exportUser">测试导出用户表</a>
<a href="${pageContext.request.contextPath}/hello">测试跳转</a>
<a href="${pageContext.request.contextPath}/result">测试带数据跳转</a>
<a href="${pageContext.request.contextPath}/show">测试json返回</a>
</body>
</html>
