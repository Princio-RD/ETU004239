<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="mg.itu.p4239.annotation.entity.Employe" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Examen - Liste des Employés</title>
</head>
<body>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nom de l'employé</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Employe> employes = (List<Employe>) request.getAttribute("listeEmployes");
                if (employes != null && !employes.isEmpty()) {
                    for (Employe emp : employes) {
            %>
                        <tr>
                            <td><%= emp.getId() %></td>
                            <td><%= emp.getNom() %></td>
                        </tr>
            <%
                    }
                } else {
            %>
                    <tr>
                        <td colspan="2" style="text-align: center; color: #777;">Aucune donnée disponible.</td>
                    </tr>
            <%
                }
            %>
        </tbody>
    </table>

</body>
</html>