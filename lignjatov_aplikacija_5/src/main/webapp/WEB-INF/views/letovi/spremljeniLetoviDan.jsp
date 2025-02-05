<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Svi letovi</title>
<%@ include file="../zaglavlje.jsp"%><br />
</head>
<body>

${greska}

	<form method="get">
		Icao: <input type="text" name="icao" /> <br /> Datum(dd.mm.yyyy): <input
			type="text" name="dan" /> <br /> <input type="submit"
			value="Dohvati letove" />
	</form>

	<table>
		<tr>
			<th>Ishodište</th>
			<th>Odredište</th>
			<th>Callsign</th>
			<th>Prvo viđen</th>
			<th>Zadnje viđen</th>
		</tr>
		<c:forEach var="let" items="${letovi}">
			<tr>
				<td><c:out value="${let.getEstDepartureAirport()}" /></td>
				<td><c:out value="${let.getEstArrivalAirport()}" /></td>
				<td><c:out value="${let.getCallsign()}" /></td>
				<td><c:out value="${let.getFirstSeen()}" /></td>
				<td><c:out value="${let.getLastSeen()}" /></td>

				<td>
					<form method="post"
						action="${pageContext.servletContext.contextPath}/mvc/letovi/spremi">
						<input type="hidden" name="icao24" value="${let.getIcao24()}">
						<input type="hidden" name="firstSeen"
							value="${let.getFirstSeen()}"> <input type="hidden"
							name="estDepartureAirport"
							value="${let.getEstDepartureAirport()}"> <input
							type="hidden" name="lastSeen" value="${let.getLastSeen()}">
						<input type="hidden" name="estArrivalAirport"
							value="${let.getEstArrivalAirport()}"> <input
							type="hidden" name="callSign" value="${let.getCallsign()}">
						<input type="hidden" name="estDepartureAirportHorizDistance"
							value="${let.getEstDepartureAirportHorizDistance()}"> <input
							type="hidden" name="estDepartureAirportVertDistance"
							value="${let.getEstDepartureAirportVertDistance()}"> <input
							type="hidden" name="estArrivalAirportHorizDistance"
							value="${let.getEstArrivalAirportHorizDistance()}"> <input
							type="hidden" name="estArrivalAirportVertDistance"
							value="${let.getEstArrivalAirportVertDistance()}"> <input
							type="hidden" name="departureAirportCandidatesCount"
							value="${let.getDepartureAirportCandidatesCount()}"> <input
							type="hidden" name="arrivalAirportCandidatesCount"
							value="${let.getArrivalAirportCandidatesCount()}"> <input
							type="submit" value="Spremi">
					</form>
				</td>
			</tr>
		</c:forEach>
	</table>

	<%
	int odBroja = 1;
	int broj = 0;
	if (request.getParameter("odBroja") == null || request.getParameter("broj") == null) {
	  broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
	  odBroja = 1;
	} else {
	  odBroja = Integer.parseInt(request.getParameter("odBroja"));
	  broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
	}
	int sljedeci = odBroja + broj;
	int prethodni = odBroja - broj;
	if (prethodni <= 0) {
	  prethodni = 1;
	}
	%>
	<form method="get">
		<input type="submit" value="Prethodna stranica" /> <input
			type="hidden" name="odBroja" value="<%=prethodni%>"> <input
			type="hidden" name="broj" value="<%=broj%>"> <input
			type="hidden" name="icao" value="<%=request.getParameter("icao")%>">
		<input type="hidden" name="dan"
			value="<%=request.getParameter("dan")%>">
	</form>
	<form method=get>
		<input type="submit" value="Početna stranica" /> <input type="hidden"
			name="odBroja" value="1"> <input type="hidden" name="broj"
			value="<%=broj%>"> <input type="hidden" name="icao"
			value="<%=request.getParameter("icao")%>"> <input
			type="hidden" name="dan" value="<%=request.getParameter("dan")%>">
	</form>
	<form method=get>
		<input type="submit" value="Sljedeća stranica" /> <input
			type="hidden" name="odBroja" value="<%=sljedeci%>"> <input
			type="hidden" name="broj" value="<%=broj%>"><input
			type="hidden" name="icao" value="<%=request.getParameter("icao")%>">
		<input type="hidden" name="dan"
			value="<%=request.getParameter("dan")%>">
	</form>


</body>
</html>