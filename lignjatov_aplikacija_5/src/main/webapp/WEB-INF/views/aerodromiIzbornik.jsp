<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="zaglavlje.jsp"%><br />
<title>Aerodromi</title>
</head>
<body>
<br>
<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/svi">Pregled svih aerodroma</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/sakupljanje">Pregled aerodroma za sakupljanje</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/udaljenosti">Pogled udaljenosti</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/udaljenostiPosluzitelj">Pogled udaljenosti koristeći poslužitelj</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/udaljenostiManjiOdDva">Manje opcije između dva aerodroma</a>
	<br />
	<br />
	<a href="${pageContext.servletContext.contextPath}/mvc/aerodromi/udaljenostiManji">Aerodromi manje udaljenost od traženog</a>
	<br />
	<br />
</body>
</html>