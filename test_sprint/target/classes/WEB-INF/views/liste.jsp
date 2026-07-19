<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, mg.itu.p4239.annotation.entity.Employe" %>
<html>
<head>
    <title>Liste des Employés</title>
</head>
<body>
    <h1>Liste des Employés</h1>
    <p>Status DB: ${dbStatus}</p>
    <ul>
        <%
            List<Employe> employes = (List<Employe>) request.getAttribute("employes");
            if (employes != null && !employes.isEmpty()) {
                for (Employe e : employes) {
        %>
            <li><%= e.getId() %> - <%= e.getNom() %></li>
        <%
                }
            } else {
        %>
            <li>Aucun employé trouvé</li>
        <%
            }
        %>
    </ul>
</body>
</html>