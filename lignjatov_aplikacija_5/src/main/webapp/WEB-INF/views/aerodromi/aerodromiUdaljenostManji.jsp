<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="../zaglavlje.jsp" %><br/>
<title>Manji od dva</title>
</head>
<body>
	<form method="get">
		Aerodrom: <input name="icao" maxlength="10" size="10" /><br />
		Država: <input name="drzava" maxlength="10" size="10" /><br />
		Kilometri: <input name="km" maxlength="10" size="10" /><br />
		<input type="submit" value=" Potraži udaljenosti " />
	</form>
Letovi manji od ${manjiOd}km
<br/>

	<table>
		<tr>
			<th>icao</th>
			<th>država</th>
			<th>km</th>
		</tr>
		<c:forEach var="aerodrom" items="${aerodromi}">
			<tr>
				<td><c:out value="${aerodrom.icao()}" /></td>
				<td><c:out value="${aerodrom.drzava()}" /></td>	
				<td><c:out value="${aerodrom.km()}" /></td>
			</tr>
		</c:forEach>
	</table>


</body>
</html>