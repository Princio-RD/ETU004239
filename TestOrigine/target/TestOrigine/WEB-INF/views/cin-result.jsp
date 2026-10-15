<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Resultat CIN</h1>
    <p style="color:red;">${error}</p>

    <h3>Recto</h3>
    <p>Nom : ${recto.name}</p>
    <p>Type : ${recto.contentType}</p>
    <p>Taille : ${recto.size} octets</p>
    <img src="${recto.toDataUrl()}" width="300"/>

    <h3>Verso</h3>
    <p>Nom : ${verso.name}</p>
    <p>Type : ${verso.contentType}</p>
    <p>Taille : ${verso.size} octets</p>
    <img src="${verso.toDataUrl()}" width="300"/>
</body>
</html>