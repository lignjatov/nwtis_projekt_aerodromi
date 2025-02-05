<%@page import="org.foi.nwtis.podaci.Aerodrom"%>
<%@page import="org.foi.nwtis.Konfiguracija"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Svi aerodromi</title>
<%@ include file="../zaglavlje.jsp"%><br />
</head>

<form method="get">
	Aerodrom: <input type="text" name="trazeniAerodrom" /> <br /> Država
	<input type="text" name="trazenaDrzava" /> <br /> <input
		type="submit" value="Trazi" />
</form>
${greska}

<body>
	<table>
		<tr>
			<th>Naziv</th>
			<th>icao</th>
			<th>Država</th>
			<th>Opcije</th>
		</tr>
		<c:forEach var="aerodrom" items="${aerodromi}">
			<tr>

				<td><c:out value="${aerodrom.getNaziv()}" /></td>
				<td><c:out value="${aerodrom.getIcao()}" /></td>
				<td><c:out value="${aerodrom.getDrzava()}" /></td>

				<td><a
					href="${pageContext.servletContext.contextPath}/mvc/aerodromi/${aerodrom.getIcao()}">Detalji</a></td>
				<td><a
					href="${pageContext.servletContext.contextPath}/mvc/aerodromi/svi?spremi=${aerodrom.getIcao()}">Spremi</a></td>
			</tr>
		</c:forEach>
	</table>
	<form method="get">
		<%
		int odBroja = 0;
		int broj = 0;
		if (request.getParameter("odBroja") == null || request.getParameter("broj") == null) {
		  broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
		}

		else {
		  odBroja = Integer.parseInt(request.getParameter("odBroja"));
		  broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
		}
		int sljedeci = odBroja + broj;
		int prethodni = odBroja - broj;
		if (prethodni <= 0) {
		  prethodni = 1;
		}
		String trazeniAerodrom = (String)request.getParameter("trazeniAerodrom");
		String trazenaDrzava = (String)request.getParameter("trazenaDrzava");
		
		if(trazeniAerodrom==null){
		  trazeniAerodrom="";
		}
		if(trazenaDrzava==null){
		  trazenaDrzava="";
		}
		%>
		<input type="submit" value="Prethodna stranica" /> <input
			type="hidden" name="odBroja" value="<%=prethodni%>"> <input
			type="hidden" name="broj" value="<%=broj%>">
			<input type="hidden" name="trazeniAerodrom" value="<%=trazeniAerodrom%>">
			<input type="hidden" name="trazenaDrzava" value="<%=trazenaDrzava %>">
	</form>
	<form method=get>
		<input type="submit" value="Početak" /> <input type="hidden"
			name="odBroja" value="1"> <input type="hidden" name="broj"
			value="<%=broj%>">
			<input type="hidden" name="trazeniAerodrom" value="<%=trazeniAerodrom%>">
		<input type="hidden" name="trazenaDrzava" value="<%=trazenaDrzava %>">
	</form>
	<form method=get>
		<input type="submit" value="Sljedeća stranica" /> 
		<input type="hidden" name="odBroja" value="<%=sljedeci%>"> 
		<input type="hidden" name="broj" value="<%=broj%>">
		<input type="hidden" name="trazeniAerodrom" value="<%=trazeniAerodrom%>">
		<input type="hidden" name="trazenaDrzava" value="<%=trazenaDrzava %>">
	</form>
</body>
</html>