package com.badgesystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/badgeverificationsystem?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    
    // Replace "YOUR_MYSQL_PASSWORD" with your actual MySQL/Workbench root password
    private static final String PASSWORD = "12345678"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}