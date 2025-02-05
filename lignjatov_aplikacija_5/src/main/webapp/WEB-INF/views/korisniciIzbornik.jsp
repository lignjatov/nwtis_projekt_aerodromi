<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="zaglavlje.jsp"%><br />
<title>Korisnici</title>
</head>
<body>
<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/korisnici/registracija">Registriraj korisnika</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/korisnici/prijava">Prijava korisnika</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/korisnici/filter">Filtriranje korisnika</a>
	<br />
	<br />
</body>
</html>