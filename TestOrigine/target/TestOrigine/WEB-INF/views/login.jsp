<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Connexion</h1>
    <p style="color:red;">${error}</p>
    <form action="<%= request.getContextPath() %>/login" method="post">
        <p>Username : <input type="text" name="username"/></p>
        <p>Password : <input type="password" name="password"/></p>
        <p><button type="submit">Se connecter</button></p>
    </form>
</body>
</html>