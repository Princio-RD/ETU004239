package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Param;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.ModelView;

@Controller
public class FormController {

    @Url("/form")
    public ModelView showForm() {
        return new ModelView("form");
    }

    @Url(value = "/form/save", method = "POST")
    public ModelView save(
            @Param("nom")   String n,
            @Param("age")   int a,
            @Param("email") String e) {

        ModelView mv = new ModelView("form-result");
        mv.addItem("nom", n);
        mv.addItem("age", a);
        mv.addItem("email", e);
        return mv;
    }
}