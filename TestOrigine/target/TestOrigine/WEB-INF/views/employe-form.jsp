<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Sprint 9-b : Binding objet avec objet imbrique</h1>
    <form action="<%= request.getContextPath() %>/employe/save" method="post">
        <h3>Employe</h3>
        <p>Nom : <input type="text" name="emp.nom"/></p>
        <p>Age : <input type="number" name="emp.age"/></p>
        <p>Salaire : <input type="text" name="emp.salaire"/></p>

        <h3>Diplome</h3>
        <p>Libelle : <input type="text" name="emp.diplome.libelle"/></p>
        <p>Annee : <input type="number" name="emp.diplome.annee"/></p>

        <p><button type="submit">Enregistrer</button></p>
    </form>
</body>
</html>