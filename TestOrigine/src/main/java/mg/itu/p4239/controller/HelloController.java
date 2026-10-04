package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.ModelView;

@Controller
public class HelloController {

    @Url("/hello")
    public ModelView sayHello() {
        ModelView mv = new ModelView("hello");
        mv.addItem("message", "Bonjour depuis ModelView !");
        return mv;
    }

    @Url("/greet")
    public ModelView greet(String name) {
        ModelView mv = new ModelView("greet");
        mv.addItem("name", name);
        mv.addItem("message", "Salut " + name + " !");
        return mv;
    }

    @Url("/user")
    public ModelView user(String name, int age) {
        ModelView mv = new ModelView("user");
        mv.addItem("name", name);
        mv.addItem("age", age);
        return mv;
    }
}