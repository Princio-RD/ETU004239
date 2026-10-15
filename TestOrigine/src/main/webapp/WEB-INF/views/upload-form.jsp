<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Upload de fichiers</h1>
    <form action="<%= request.getContextPath() %>/upload/save"
          method="post"
          enctype="multipart/form-data">
        <p>Photo (image) : <input type="file" name="photo" accept="image/*"/></p>
        <p>CSV : <input type="file" name="data" accept=".csv"/></p>
        <p>PDF : <input type="file" name="doc" accept=".pdf"/></p>
        <p>Autre : <input type="file" name="other"/></p>
        <p><button type="submit">Envoyer</button></p>
    </form>
</body>
</html>