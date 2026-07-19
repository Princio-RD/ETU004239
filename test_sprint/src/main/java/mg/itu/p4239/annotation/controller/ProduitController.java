package mg.itu.p4239.annotation.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;

@Controller
public class ProduitController {
    @Url("/liste-produits")
    public String lister() {
        return "Liste des produits";
    }
}