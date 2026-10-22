<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Profil</h1>
    <p>Utilisateur : ${user}</p>
    <p>Role : ${role}</p>
    <p><a href="<%= request.getContextPath() %>/logout">Se deconnecter</a></p>
</body>
</html>