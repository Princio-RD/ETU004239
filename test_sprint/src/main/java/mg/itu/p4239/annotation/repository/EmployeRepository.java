package mg.itu.p4239.annotation.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mg.itu.p4239.annotation.entity.Employe;

public class EmployeRepository {
    private Connection connection;

    public EmployeRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Employe> findAll() {
        List<Employe> liste = new ArrayList<>();
        if (connection == null) return liste;

        String sql = "SELECT * FROM employe";
        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                liste.add(new Employe(rs.getInt("id"), rs.getString("nom")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return liste;
    }
}