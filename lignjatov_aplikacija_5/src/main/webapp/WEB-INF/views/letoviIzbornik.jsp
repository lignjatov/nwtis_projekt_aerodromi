<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="zaglavlje.jsp"%><br />
<title>Letovi</title>
</head>
<body>
<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/letovi/spremljeni">Spremljeni letovi</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/letovi/dan">Letovi dana</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/letovi/os">Letovi otvorenog neba</a>
	<br />
	<br />
</body>
</html>