<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Prijava</title>
<%@ include file="../zaglavlje.jsp"%>
</head>
<body>

${stanje}

<form method="get">
	Korisničko ime: <input type="text" name="korisnickoIme"/> <br/>
	Lozinka: <input type="password" name="korisnickaLozinka"/> <br/>
	<input type="submit" value="Unesi"/>
</form>
</body>
</html>