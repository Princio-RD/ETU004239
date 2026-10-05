package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.ModelView;
import mg.itu.p4239.model.Employe;

@Controller
public class EmployeController {

    @Url("/employe/form")
    public ModelView form() {
        return new ModelView("employe-form");
    }

    @Url(value = "/employe/save", method = "POST")
    public ModelView save(Employe emp) {
        ModelView mv = new ModelView("employe-result");
        mv.addItem("emp", emp);
        return mv;
    }
}