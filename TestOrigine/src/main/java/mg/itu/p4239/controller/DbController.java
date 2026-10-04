package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;
import com.passerelle.database.Database;
import com.passerelle.mapping.ModelView;

import java.sql.Connection;

@Controller
public class DbController {

    @Url("/dbtest")
    public ModelView test() {
        ModelView mv = new ModelView("dbtest");

        try (Connection c = Database.getConnection()) {
            mv.addItem("status", "OK");
            mv.addItem("database", c.getCatalog());
            mv.addItem("url", Database.getUrl());
        } catch (Exception e) {
            mv.addItem("status", "ERREUR");
            mv.addItem("error", e.getMessage());
        }
        return mv;
    }
}