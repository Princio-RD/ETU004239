<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Dashboard</h1>
    <p>Bienvenue !</p>
    <p><a href="<%= request.getContextPath() %>/profile">Voir mon profil</a></p>
    <p><a href="<%= request.getContextPath() %>/logout">Se deconnecter</a></p>
</body>
</html>