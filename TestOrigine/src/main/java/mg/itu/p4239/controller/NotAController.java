package mg.itu.p4239.controller;

import com.passerelle.annotation.Url;

public class NotAController {

    @Url("/should-be-ignored")
    public void method() {
    }
}