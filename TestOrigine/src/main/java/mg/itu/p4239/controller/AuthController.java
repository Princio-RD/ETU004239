package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Param;
import com.passerelle.annotation.Public;
import com.passerelle.annotation.Role;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.ModelView;
import com.passerelle.session.Session;

@Controller
public class AuthController {

    // 1. Accessible sans etre connecte
    @Url("/login/form")
    @Public
    public ModelView form() {
        return new ModelView("login");
    }

    // 2. Traitement du login (Public)
    @Url(value = "/login", method = "POST")
    @Public
    public ModelView login(@Param("username") String username,
                           @Param("password") String password,
                           Session session) {

        // Simulation de la BDD
        if ("admin".equals(username) && "admin".equals(password)) {
            session.add("user", username);
            session.add("role", "ADMIN");
            return new ModelView("dashboard");
        } 
        else if ("user".equals(username) && "user".equals(password)) {
            session.add("user", username);
            session.add("role", "USER");
            return new ModelView("dashboard");
        }
        
        ModelView mv = new ModelView("login");
        mv.addItem("error", "Identifiants incorrects");
        return mv;
    }

    // 3. Accessible a tous les connectes (pas de @Role)
    @Url("/profile")
    public ModelView profile(Session session) {
        ModelView mv = new ModelView("profile");
        mv.addItem("user", session.get("user"));
        mv.addItem("role", session.get("role"));
        return mv;
    }

    // 4. Accessible UNIQUEMENT aux ADMIN (Annotation @Role)
    @Url("/admin")
    @Role("ADMIN")
    public ModelView admin(Session session) {
        ModelView mv = new ModelView("admin");
        mv.addItem("user", session.get("user"));
        return mv;
    }

    // 5. Deconnexion
    @Url("/logout")
    public ModelView logout(Session session) {
        session.clear();
        return new ModelView("login");
    }
}