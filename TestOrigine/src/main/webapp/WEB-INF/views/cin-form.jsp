<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Upload CIN (recto + verso)</h1>
    <form action="<%= request.getContextPath() %>/cin/save"
          method="post"
          enctype="multipart/form-data">
        <p>Recto : <input type="file" name="cin.recto" accept="image/*"/></p>
        <p>Verso : <input type="file" name="cin.verso" accept="image/*"/></p>
        <p><button type="submit">Envoyer</button></p>
    </form>
</body>
</html>