<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="../zaglavlje.jsp"%>
<title>Poruke</title>
</head>
<body>
	<br />
	<c:forEach var="poruka" items="${poruke}">
		<c:out value="${poruka}"></c:out> <br/>
	</c:forEach>
	</br>
	<form method="get">
	<input type="submit" name="obrisi" value="obrisi"/>
	</form>
</body>
</html>