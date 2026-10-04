package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;

@Controller
public class HelloController {

    @Url("/hello")
    public void sayHello() {
    }

    @Url(value = "/login", method = "POST")
    public void login() {
    }

    @Url("/about")
    public void about() {
    }
}