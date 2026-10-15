<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<body>
    <h1>Resultat upload</h1>
    <c:forEach var="entry" items="${filemap}">
        <h3>Cle : ${entry.key}</h3>
        <p>Nom : ${entry.value.name}</p>
        <p>Type : ${entry.value.contentType}</p>
        <p>Taille : ${entry.value.size} octets</p>
        <p>Sous-dossier : ${entry.value.resolveSubDir()}</p>
        <p>Sauvegarde : ${saved[entry.key]}</p>
    </c:forEach>
</body>
</html>