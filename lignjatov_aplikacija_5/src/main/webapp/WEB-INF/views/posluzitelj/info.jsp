<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Upravljanje poslužiteljem</title>
<%@ include file="../zaglavlje.jsp" %>
</head>
<body>

FORM
<form method="get">
<input type="submit" name="komanda" value="INIT">
<input type="submit" name="komanda" value="KRAJ">
<br/>
<input type="submit" name="komanda" value="STATUS">
<input type="submit" name="komanda" value="PAUZA">
<input type="submit" name="komanda" value="INFO DA"> 
<input type="submit" name="komanda" value="INFO NE">
</form>
${odgovorPosluzitelja}


</body>
</html>