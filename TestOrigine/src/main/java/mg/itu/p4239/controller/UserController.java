package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;

@Controller
public class UserController {

    @Url("/users/list")
    public void list() {
    }

    @Url(value = "/users/add", method = "POST")
    public void add() {
    }

    @Url(value = "/users/delete", method = "POST")
    public void delete() {
    }

    // Pas d'annotation -> doit etre ignoree
    public void internalHelper() {
    }
}