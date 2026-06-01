package com.shopmanagement.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/shop_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String pass = "nagu";
        
        System.out.println("Attempting to connect to the database...");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                System.out.println("SUCCESS: Connected to the database!");
                
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SHOW TABLES");
                    System.out.println("\nTables in database:");
                    boolean hasTables = false;
                    while (rs.next()) {
                        System.out.println("- " + rs.getString(1));
                        hasTables = true;
                    }
                    if (!hasTables) {
                        System.out.println("  (No tables found. Did you run the schema script?)");
                    }
                    
                    if (hasTables) {
                        ResultSet rsUsers = stmt.executeQuery("SELECT count(*) FROM users");
                        if (rsUsers.next()) {
                            System.out.println("\nNumber of users: " + rsUsers.getInt(1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("\nFAILED to connect: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
