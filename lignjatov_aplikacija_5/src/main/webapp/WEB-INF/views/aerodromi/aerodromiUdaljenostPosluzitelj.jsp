<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="../zaglavlje.jsp" %><br/>
<%! int ukupnaUdaljenost=0; %>
<title>Udaljenosti između 2 aerodroma</title>
</head>
<body>
	<form method="get">
		Aerodrom od: <input name="icaoOd" maxlength="10" size="10" /><br />
		Aerodrom do: <input name="icaoDo" maxlength="10" size="10" /><br />
		<input type="submit" value=" Potraži udaljenosti " />
	</form>

	<table>
		<tr>
			<th>država</th>
			<th>km</th>
		</tr>
		<c:forEach var="aerodrom" items="${aerodromi}">
			<tr>
				<td><c:out value="${aerodrom.drzava()}" /></td>
				<td><c:out value="${aerodrom.km()}" /></td>
			</tr>
		</c:forEach>
	</table>
	Ukupna udaljenost iznosi:  ${ukupnaUdaljenost}km
</body>
</html>