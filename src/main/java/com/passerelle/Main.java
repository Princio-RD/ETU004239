package com.passerelle;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;

public class Main {
    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8081);
        tomcat.getConnector();

        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext("", docBase);

        Tomcat.addServlet(ctx, "frontController", new FrontControllerServlet());
        ctx.addServletMappingDecoded("/*", "frontController");
        System.out.println(" PASSERELLE ACTIVE : http://localhost:8081");
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
