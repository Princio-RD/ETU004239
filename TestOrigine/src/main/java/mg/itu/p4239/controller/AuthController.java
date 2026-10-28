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

    // --- PUBLIC ---
    @Url("/login/form")
    @Public
    public ModelView form() {
        return new ModelView("login");
    }

    @Url(value = "/login", method = "POST")
    @Public
    public ModelView login(@Param("username") String username,
                           @Param("password") String password,
                           Session session) {
        if ("admin".equals(username) && "admin".equals(password)) {
            session.add("user", username);
            session.add("role", "ADMIN"); // Le rôle est mis dans la session
            return new ModelView("dashboard");
        } else if ("user".equals(username) && "user".equals(password)) {
            session.add("user", username);
            session.add("role", "USER");
            return new ModelView("dashboard");
        }
        ModelView mv = new ModelView("login");
        mv.addItem("error", "Identifiants incorrects");
        return mv;
    }

    // --- CONNECTE OBLIGATOIRE (role par defaut du web-fragment sera utilise si @Role vide) ---
    @Url("/profile")
    @Role // Utilise "security.default.role" (USER)
    public ModelView profile(Session session) {
        ModelView mv = new ModelView("profile");
        mv.addItem("user", session.get("user"));
        mv.addItem("role", session.get("role"));
        return mv;
    }

    // --- ROLE SPECIFIQUE ---
    @Url("/admin")
    @Role("ADMIN") // Exige explicitement ADMIN
    public ModelView admin(Session session) {
        ModelView mv = new ModelView("admin");
        mv.addItem("user", session.get("user"));
        return mv;
    }

    @Url("/logout")
    public ModelView logout(Session session) {
        session.clear();
        return new ModelView("login");
    }
}