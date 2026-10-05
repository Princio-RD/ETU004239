package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.ModelView;

@Controller
public class FormController {

    @Url("/form")
    public ModelView showForm() {
        return new ModelView("form");
    }

    @Url(value = "/form/save", method = "POST")
    public ModelView save(String nom, int age, String email) {
        ModelView mv = new ModelView("form-result");
        mv.addItem("nom", nom);
        mv.addItem("age", age);
        mv.addItem("email", email);
        return mv;
    }
}