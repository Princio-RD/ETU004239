<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Sprint 9-a : Parametres individuels</h1>
    <form action="<%= request.getContextPath() %>/form/save" method="post">
        <p>Nom : <input type="text" name="nom"/></p>
        <p>Age : <input type="number" name="age"/></p>
        <p>Email : <input type="email" name="email"/></p>
        <p><button type="submit">Envoyer</button></p>
    </form>
</body>
</html>