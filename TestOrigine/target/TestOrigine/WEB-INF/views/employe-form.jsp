<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Nouvel employe</h1>
    <form action="<%= request.getContextPath() %>/employe/save" method="post">
        <p>Nom : <input type="text" name="nom"/></p>
        <p>Age : <input type="number" name="age"/></p>
        <p>Salaire : <input type="text" name="salaire"/></p>
        <p><button type="submit">Enregistrer</button></p>
    </form>
</body>
</html>