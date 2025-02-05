<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="org.foi.nwtis.Konfiguracija"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Dnevnik</title>
<%@ include file="../zaglavlje.jsp" %><br/>
</head>
<body>

<form method="get">
	<input type="submit" name="vrsta" value="AP2">
	<input type="submit" name="vrsta" value="AP4">
	<input type="submit" name="vrsta" value="AP5">
	<input type="hidden" name="odBroja" value="0">
	<input type="hidden" name="broj" value="0">
</form>

<table>
<tr>
<th>Zahtjev</th>
<th>Vrijeme zahtjeva</th>
<th>Vrsta zahtjeva</th>
</tr>
<c:forEach var="zapis" items="${dnevnikRada}">
			<tr>
				<td><c:out value="${zapis.zahtjev()}" /></td>
				<td><c:out value="${zapis.vrijemeZahtjeva()}" /></td>
				<td><c:out value="${zapis.vrsta()}" /></td>
			</tr>
		</c:forEach>
</table>
<form method="get">
	<% 
		int odBroja = 1;
		int broj = 0;
		if(request.getParameter("odBroja")==null || request.getParameter("broj")==null){
		  broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
		}
		
		else{
			odBroja = Integer.parseInt(request.getParameter("odBroja"));
			broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
		}
		int sljedeci = odBroja + broj;
		int prethodni = odBroja-broj;
		if(prethodni<=0){
		  prethodni=1;
		}
		
		String vrsta = request.getParameter("vrsta");
	%>
	<input type="submit" value="Prethodna stranica"/>
	<input type="hidden" name="vrsta" value="<%=vrsta%>">
	<input type="hidden" name="odBroja" value="<%= prethodni%>">
	<input type="hidden" name="broj" value="<%= broj%>">
	</form>
	<form method=get>
	<input type="submit" value="Početak"/>
	<input type="hidden" name="vrsta" value="<%=vrsta%>">
	<input type="hidden" name="odBroja" value="1">
	<input type="hidden" name="broj" value="<%= broj%>">
	</form>
	<form method=get>
	<input type="submit" value="Sljedeća stranica"/>
	<input type="hidden" name="vrsta" value="<%=vrsta%>">
	<input type="hidden" name="odBroja" value="<%= sljedeci%>">
	<input type="hidden" name="broj" value="<%= broj%>">
	</form>
</body>
</html>