<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registriraj korisnika</title>
<%@ include file="../zaglavlje.jsp"%>
</head>
<body>
<form method="post">
	Ime: <input type="text" name="ime"/> <br/>
	Prezime: <input type="text" name="prezime"/> <br/>
	Korisničko ime: <input type="text" name="korisnickoIme"/> <br/>
	Lozinka: <input type="password" name="korisnickaLozinka"/> <br/>
	Email adresa: <input type="text" name="korisnickiEmail"/> <br/>
	<input type="submit" value="Unesi"/>
</form>
</body>
</html>