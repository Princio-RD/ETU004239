<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Dashboard</h1>
    <p>Bienvenue !</p>
    <p><a href="<%= request.getContextPath() %>/profile">Mon Profil</a></p>
    <p><a href="<%= request.getContextPath() %>/admin">Espace Admin (si autorise)</a></p>
    <p><a href="<%= request.getContextPath() %>/logout">Deconnexion</a></p>
</body>
</html>