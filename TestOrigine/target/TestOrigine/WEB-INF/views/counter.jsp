<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
    <h1>Compteur de clics</h1>
    
    <p>
        Valeur actuelle : <b>${count}</b>
    </p>
    
    <p>
        <a href="<%= request.getContextPath() %>/counter/add" style="font-size: 20px; padding: 10px; background: #eee; text-decoration: none;">
            Cliquez ici pour +1
        </a>
    </p>

    <hr/>
    
    <p>
        Votre ID de session : ${sessionId}
    </p>
</body>
</html>