<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Employe enregistre</h1>
    <p>Nom : ${emp.nom}</p>
    <p>Age : ${emp.age}</p>
    <p>Salaire : ${emp.salaire}</p>
    <hr/>
    <p>Diplome : ${emp.diplome.libelle}</p>
    <p>Annee : ${emp.diplome.annee}</p>
    <p><a href="<%= request.getContextPath() %>/employe/form">Retour</a></p>
</body>
</html>