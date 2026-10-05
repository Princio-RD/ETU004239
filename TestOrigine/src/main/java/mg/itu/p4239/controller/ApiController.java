package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.RestApi;
import com.passerelle.annotation.Url;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ApiController {

    @Url("/api/hello")
    @RestApi
    public Map<String, Object> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Bonjour");
        data.put("status", "OK");
        data.put("code", 200);
        return data;
    }

    @Url("/api/user")
    @RestApi
    public User user() {
        return new User("Alice", 25);
    }

    @Url("/api/echo")
    @RestApi
    public String echo(String msg) {
        return "Echo : " + msg;
    }
	
	@Url("/api/test")
	public String test(){
		return "test";
	}
	
}