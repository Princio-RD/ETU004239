package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;

@Controller
public class HelloController {

    @Url("/hello")
    public void sayHello() {
        System.out.println(">>> sayHello()");
    }

    @Url("/greet")
    public void greet(String name) {
        System.out.println(">>> greet(" + name + ")");
    }

    @Url("/add")
    public void add(int a, int b) {
        System.out.println(">>> add(" + a + ", " + b + ") = " + (a + b));
    }

    @Url("/user")
    public void user(String name, int age, boolean active) {
        System.out.println(">>> user : " + name + ", " + age + ", " + active);
    }

    @Url("/price")
    public void price(double amount) {
        System.out.println(">>> price = " + amount);
    }
}