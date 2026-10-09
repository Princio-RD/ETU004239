package mg.itu.p4239.controller;

import com.passerelle.annotation.Param;
import com.passerelle.annotation.RestApi;
import com.passerelle.annotation.Url;

import java.util.HashMap;
import java.util.Map;

@RestApi
public class ApiController {

    @Url("/api/hello")
    public Map<String, Object> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Bonjour");
        data.put("status", "OK");
        data.put("code", 200);
        return data;
    }

    @Url("/api/echo")
    public String echo(@Param("msg") String msg) {
        return "Echo : " + msg;
    }
}