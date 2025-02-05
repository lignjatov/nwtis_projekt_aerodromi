<%@page import="org.foi.nwtis.Konfiguracija"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

</head>
<body>
<a href="${pageContext.servletContext.contextPath}">Početna stranica</a>

<% 
Konfiguracija konfiguracija = (Konfiguracija)application.getAttribute("konfig");
konfiguracija.dajPostavku("autor.ime");
out.print(konfiguracija.dajPostavku("autor.ime") + " " + konfiguracija.dajPostavku("autor.prezime") + " ");
out.print(konfiguracija.dajPostavku("autor.predmet") + " " + konfiguracija.dajPostavku("aplikacija.godina")+ " ");
out.println(konfiguracija.dajPostavku("aplikacija.verzija"));
out.println();
%>
</body>
</html>