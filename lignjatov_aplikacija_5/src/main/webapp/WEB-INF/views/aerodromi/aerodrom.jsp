<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="../zaglavlje.jsp" %>
<title>${aerodrom.icao} </title>
</head>
<body>
<br/>
icao:${aerodrom.icao} 
Naziv:${aerodrom.naziv}
Država:${aerodrom.drzava} <br>
Oblaci: ${meteo.getCloudsName()} <br/>
Oblaci vrijednost: ${meteo.getCloudsValue()}
<hr>
Vlažnost zraka: ${meteo.getHumidityValue()}${meteo.getHumidityUnit()}
<hr>
Pritisak: ${meteo.getPressureValue()} ${meteo.getPressureUnit()}
<hr>
Maksimalna temperatura: ${meteo.getTemperatureMax()} ${meteo.getTemperatureUnit()}<br/>
Minimalna temperatura: ${meteo.getTemperatureMin()} ${meteo.getTemperatureUnit()}<br/>
Trenutna temperatura: ${meteo.getTemperatureValue()} ${meteo.getTemperatureUnit()}<br/>
<hr>
</body>
</html>