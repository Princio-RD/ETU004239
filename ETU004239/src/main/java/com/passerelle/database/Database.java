package com.passerelle.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    public static void init(String driver, String url, String user, String password)
            throws ClassNotFoundException {
        Database.driver = driver;
        Database.url = url;
        Database.user = user;
        Database.password = password;

        Class.forName(driver);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static String getDriver()   { return driver; }
    public static String getUrl()      { return url; }
    public static String getUser()     { return user; }
    public static String getPassword() { return password; }
}