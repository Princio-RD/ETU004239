package mg.itu;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class Main {
    public static void main(String[] args) throws LifecycleException {
        System.out.println("Démarrage de l'application");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8082);
        tomcat.getConnector();

        // Créer un répertoire temporaire pour les ressources web
        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir = new File(".");
        }
        
        String docBase = webappDir.getAbsolutePath();
        Context ctx = tomcat.addContext("", docBase);

        // Ajouter un répertoire de ressources supplémentaire
        File webInfDir = new File(docBase, "WEB-INF");
        if (!webInfDir.exists()) {
            webInfDir.mkdirs();
            new File(webInfDir, "views").mkdirs();
        }

        ctx.addParameter("packageToScan", "mg.itu.p4239.annotation.controller");
        ctx.addParameter("db_url", "jdbc:postgresql://localhost:5432/testdb");
        ctx.addParameter("db_user", "postgres");
        ctx.addParameter("db_password", "Pr20071010");
        ctx.addParameter("view_prefix", "/WEB-INF/views/");
        ctx.addParameter("view_suffix", ".jsp");

        ctx.addApplicationListener("com.passerelle.listener.FrameworkListener");

        Tomcat.addServlet(ctx, "FrontController", new com.passerelle.controller.FrontControllerServlet());
        ctx.addServletMappingDecoded("/*", "FrontController");

        tomcat.start();
        System.out.println("http://localhost:8082");
        tomcat.getServer().await();
    }
}