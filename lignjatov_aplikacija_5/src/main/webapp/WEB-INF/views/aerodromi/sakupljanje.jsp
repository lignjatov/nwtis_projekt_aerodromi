<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../zaglavlje.jsp"%><br />
<meta charset="UTF-8">
<title>Sakupljanje</title>
</head>
<body>
${greska}
	<table>
		<tr>
			<th>Icao</th>
			<th>Status</th>
		</tr>
		<c:forEach var="aerodrom" items="${aerodromi}">
			<tr>
				<td><c:out value="${aerodrom.getNaziv()}" /></td>
				<td><c:out value="${aerodrom.isAktivan()}" /></td>
				<td><a
					href="${pageContext.servletContext.contextPath}/mvc/aerodromi/sakupljanje?promjeniStatus=${aerodrom.isAktivan()}&icao=${aerodrom.getNaziv()}">
					<c:if test="${aerodrom.isAktivan()}">Pauza</c:if>
					<c:if test="${!aerodrom.isAktivan()}">Aktiviraj</c:if>
					</a></td>
			</tr>
		</c:forEach>
	</table>
	
	<br>
	<form method="get">
		<%
		int odBroja = 1;
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
		%>
		<input type="submit" value="Prethodna stranica" /> <input
			type="hidden" name="odBroja" value="<%=prethodni%>"> <input
			type="hidden" name="broj" value="<%=broj%>">
	</form>
	<form method=get>
		<input type="submit" value="Početak" /> <input type="hidden"
			name="odBroja" value="1"> <input type="hidden" name="broj"
			value="<%=broj%>">
	</form>
	<form method=get>
		<input type="submit" value="Sljedeća stranica" /> 
		<input type="hidden" name="odBroja" value="<%=sljedeci%>"> 
		<input type="hidden" name="broj" value="<%=broj%>">
	</form>
	<br>
	<div id="podaci"></div>
	<div id="brojAerodroma"></div>
	<script>
	var wsocket;
	function connect() {
		wsocket = new WebSocket("ws://localhost:8080/lignjatov_aplikacija_4/info");
		wsocket.onmessage = onMessage;
	}
	function onMessage(evt) {
		console.log(evt.data);
		document.getElementById("podaci").innerHTML="Trenutno vrijeme: "+evt.data.split(";")[0];
		document.getElementById("brojAerodroma").innerHTML=" Broj aerodroma za sakupljanje: "+ evt.data.split(";")[2];
	}
	
	function posaljiPoruku(){
		wsocket.send("dohvati");
		
	}
	
	
	window.addEventListener("load", connect, false);
	setTimeout(posaljiPoruku,500);
	
	</script>	
</body>
</html>