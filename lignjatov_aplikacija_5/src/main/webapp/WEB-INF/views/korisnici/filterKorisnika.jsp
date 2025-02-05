<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="../zaglavlje.jsp" %>
<title>Filter</title>
</head>
<body>
<script>
	var wsocket;
	function connect() {
		wsocket = new WebSocket("ws://localhost:8080/lignjatov_aplikacija_4/info");
		wsocket.onmessage = onMessage;
	}
	function onMessage(evt) {
		console.log(evt.data);

		document.getElementById("podaci").innerHTML="Trenutno vrijeme: "+evt.data.split(";")[0];
		document.getElementById("brojKorisnika").innerHTML="Ukupni broj korisnika: "+ evt.data.split(";")[1];
	}
	
	function posaljiPoruku(){
		wsocket.send("dohvati");
	}
	window.addEventListener("load", connect, false);
	setTimeout(posaljiPoruku,500);
	</script>

	${prijavljeniKorisnik}
	<form method="get">
		Ime: <input type="text" name="trazenoIme" /> <br /> Prezime: <input
			type="password" name="trazenoPrezime" /> <br /> <input type="submit"
			value="Trazi" />
	</form>

	<table>
		<tr>
			<th>Ime</th>
			<th>Prezime</th>
			<th>Korisničko ime</th>
			<th>Lozinka</th>
		</tr>
		<c:forEach var="korisnik" items="${korisnici}">
			<tr>
				<td><c:out value="${korisnik.getIme()}" /></td>
				<td><c:out value="${korisnik.getPrezime()}" /></td>
				<td><c:out value="${korisnik.getKorisnickoIme()}" /></td>
				<td><c:out value="${korisnik.getLozinka()}" /></td>
			</tr>
		</c:forEach>
	</table>
	<div id="podaci"></div> <br>
	<div id="brojKorisnika"></div>
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
		String trazenoIme = (String)request.getParameter("trazenoIme");
		String trazenoPrezime = (String)request.getParameter("trazenoPrezime");
		
		if(trazenoIme==null){
		  trazenoIme="";
		}
		if(trazenoPrezime==null){
		  trazenoPrezime="";
		}
		%>
		<form method="get">
		<input type="submit" value="Prethodna stranica" /> <input
			type="hidden" name="odBroja" value="<%=prethodni%>"> <input
			type="hidden" name="broj" value="<%=broj%>">
			<input type="hidden" name="trazenoIme" value="<%=trazenoIme%>">
			<input type="hidden" name="trazenoPrezime" value="<%=trazenoPrezime %>">
	</form>
	<form method="get">
		<input type="submit" value="Početak" /> <input type="hidden"
			name="odBroja" value="1"> <input type="hidden" name="broj"
			value="<%=broj%>">
			<input type="hidden" name="trazenoIme" value="<%=trazenoIme%>">
		<input type="hidden" name="trazenoPrezime" value="<%=trazenoPrezime %>">
	</form>
	<form method="get">
		<input type="submit" value="Sljedeća stranica" /> 
		<input type="hidden" name="odBroja" value="<%=sljedeci%>"> 
		<input type="hidden" name="broj" value="<%=broj%>">
		<input type="hidden" name="trazenoIme" value="<%=trazenoIme%>">
		<input type="hidden" name="trazenoPrezime" value="<%=trazenoPrezime %>">
	</form>
	

</body>
</html>