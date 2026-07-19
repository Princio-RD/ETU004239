package mg.itu.p4239.annotation.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.GetMapping;
import com.passerelle.annotation.PostMapping;
import com.passerelle.annotation.Url;

@Controller
public class UserController {
    @GetMapping("/profil")
    public String afficherProfil() {
        System.out.println(" GET /profil exécuté");
        return "GET /profil - Affichage du profil utilisateur";
    }

    @GetMapping("/profil")
    public String mettreAJourProfil() {
        System.out.println(" POST /profil exécuté");
        return "POST /profil - Profil mis à jour avec succès";
    }
}