package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Param;
import com.passerelle.annotation.Public;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.ModelView;
import com.passerelle.session.Session;

@Controller
public class AuthController {

    // Accessible sans etre connecte
    @Url("/login/form")
    @Public
    public ModelView form() {
        return new ModelView("login");
    }

    // Traitement du login
    @Url(value = "/login", method = "POST")
    @Public
    public ModelView login(@Param("username") String username,
                           @Param("password") String password,
                           Session session) {

        // Verification basique (a remplacer par BDD)
        if ("admin".equals(username) && "admin".equals(password)) {
            session.add("user", username);
            session.add("role", "ADMIN");
            return new ModelView("dashboard");
        }
        
        ModelView mv = new ModelView("login");
        mv.addItem("error", "Identifiants incorrects");
        return mv;
    }

    // se connecter pour connecter
    @Url("/profile")
    public ModelView profile(Session session) {
        ModelView mv = new ModelView("profile");
        mv.addItem("user", session.get("user"));
        mv.addItem("role", session.get("role"));

        mv.addItem("sessionId",session.get("sessionId"));
        return mv;
    }

    // Deconnexion
    @Url("/logout")
    public ModelView logout(Session session) {
        session.clear();
        return new ModelView("login");
    }
}