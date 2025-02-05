<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Projekt</title>
<%@ include file="zaglavlje.jsp"%>
</head>
<body>
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/korisnici">Korisnici</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/info/">Posluzitelj</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/poruke">JMS
		poruke</a>
	<br />
	<br />
	<a
		href="${pageContext.servletContext.contextPath}/mvc/aerodromi/pocetakAerodromi">Aerodromi</a>
	<br />
	<br />
	<a
		href="${pageContext.servletContext.contextPath}/mvc/letovi">Letovi</a>
	<br />
	<br />
	<a
		href="${pageContext.servletContext.contextPath}/mvc/dnevnik">Dnevnik</a>
	<br />
</body>
</html>