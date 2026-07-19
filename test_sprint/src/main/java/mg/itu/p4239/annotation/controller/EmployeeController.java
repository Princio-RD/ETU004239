package mg.itu.p4239.annotation.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.Map;
import mg.itu.p4239.annotation.entity.Employe;
import mg.itu.p4239.annotation.repository.EmployeRepository;

@Controller
public class EmployeeController {

    @GetMapping("/employes")
    public String lister(HttpServletRequest request) {
        // 1. Extraction du conteneur d'instances (Le "ctx" du tableau)
        @SuppressWarnings("unchecked")
        Map<Class<?>, Object> ctx = (Map<Class<?>, Object>) request.getServletContext().getAttribute("controllerInstances");

        // 2. Récupération de la DataSource (ctx.getBean)
        Connection conn = (Connection) ctx.get(Connection.class);

        // 3. Passage de la connexion au Repository
        EmployeRepository repo = new EmployeRepository(conn);

        // 4. Injection de la liste brute d'employés dans la requête
        request.setAttribute("listeEmployes", repo.findAll());

        // 5. Envoi vers le fichier de rendu JSP
        return "/vues/employes.jsp";
    }
}