<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Espace Administrateur</h1>
    <p>Bienvenue ${user}</p>
    <p><a href="<%= request.getContextPath() %>/logout">Se deconnecter</a></p>
</body>
</html>