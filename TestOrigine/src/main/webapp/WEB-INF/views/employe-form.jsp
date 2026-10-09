<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Employe avec diplome</h1>
    <form action="<%= request.getContextPath() %>/employe/save" method="post">
        <h3>Employe</h3>
        <p>Nom : <input type="text" name="nom"/></p>
        <p>Age : <input type="number" name="age"/></p>
        <p>Salaire : <input type="text" name="salaire"/></p>

        <h3>Diplome</h3>
        <p>Libelle : <input type="text" name="diplome.libelle"/></p>
        <p>Annee : <input type="number" name="diplome.annee"/></p>

        <p><button type="submit">Enregistrer</button></p>
    </form>
</body>
</html>