package mg.itu;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        System.out.println(" Démarrage de l'application");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8082);
        tomcat.getConnector();

        String docBase = new File("src/main/webapp").getAbsolutePath();
        
        Context ctx = tomcat.addContext("", docBase);

        ctx.addParameter("packageToScan", "mg.itu.p4239.annotation.controller");
        ctx.addApplicationListener("com.passerelle.listener.FrameworkListener");

        Tomcat.addServlet(ctx, "FrontController", new com.passerelle.controller.FrontControllerServlet());
        ctx.addServletMappingDecoded("/*", "FrontController");

        tomcat.start();
        System.out.println("🌐 http://localhost:8082");

        tomcat.getServer().await();
    }
}