package mg.itu.p4239.annotation.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.GetMapping;
import mg.itu.p4239.annotation.repository.EmployeRepository;
import mg.itu.p4239.annotation.entity.Employe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.List;
import java.io.PrintWriter;

@Controller
public class EmployeeController {

    @GetMapping(value = "/liste")
    public void liste(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("    <title>Liste des Employés</title>");
            out.println("    <style>");
            out.println("        body { font-family: Arial, sans-serif; margin: 30px; }");
            out.println("        h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }");
            out.println("        ul { list-style: none; padding: 0; }");
            out.println("        li { padding: 10px; margin: 5px 0; background: #ecf0f1; border-radius: 5px; border-left: 4px solid #3498db; }");
            out.println("        li:hover { background: #d5dbdb; }");
            out.println("        .status { color: #7f8c8d; font-style: italic; margin-top: 20px; }");
            out.println("        .error { color: #e74c3c; font-weight: bold; }");
            out.println("        .success { color: #27ae60; font-weight: bold; }");
            out.println("    </style>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <h1>📋 Liste des Employés</h1>");
            
            Connection conn = (Connection) request.getServletContext().getAttribute("dbConnection");
            
            if (conn != null) {
                try {
                    EmployeRepository repo = new EmployeRepository(conn);
                    List<Employe> employes = repo.findAll();
                    
                    if (employes != null && !employes.isEmpty()) {
                        out.println("    <ul>");
                        for (Employe e : employes) {
                            out.println("        <li><strong>ID:</strong> " + e.getId() + " - <strong>Nom:</strong> " + e.getNom() + "</li>");
                        }
                        out.println("    </ul>");
                        out.println("    <p class='status success'>✅ Status DB: Connected - " + employes.size() + " employé(s) trouvé(s)</p>");
                    } else {
                        out.println("    <p class='status'>Aucun employé trouvé dans la base de données</p>");
                        out.println("    <p class='status'>Status DB: Connected</p>");
                    }
                } catch (Exception e) {
                    out.println("    <p class='error'>❌ Erreur lors de la requête: " + e.getMessage() + "</p>");
                }
            } else {
                out.println("    <p class='error'>❌ Status DB: Non connecté</p>");
                out.println("    <p>Vérifiez les paramètres de connexion à la base de données.</p>");
            }
            
            out.println("</body>");
            out.println("</html>");
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}