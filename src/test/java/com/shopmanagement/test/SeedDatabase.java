package com.shopmanagement.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.io.File;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Random;

public class SeedDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/shop_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "nagu";
    
    // Simple SHA-256 equivalent for seeding (matches MySQL SHA2(str, 256))
    // We will just let MySQL do the hashing for the user insert.

    public static void main(String[] args) {
        System.out.println("Starting Database Seeding...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                
                // 1. Insert Shopkeeper User
                System.out.println("Inserting shopkeeper user...");
                String insertUser = "INSERT IGNORE INTO users (username, password, full_name, email, role) VALUES (?, SHA2(?, 256), ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
                    pstmt.setString(1, "shopkeeper1");
                    pstmt.setString(2, "shop123");
                    pstmt.setString(3, "John Shopkeeper");
                    pstmt.setString(4, "john@example.com");
                    pstmt.setString(5, "SHOPKEEPER");
                    pstmt.executeUpdate();
                }

                // 2. Insert Categories
                System.out.println("Inserting categories...");
                String[] categories = {"Electronics", "Clothing", "Groceries", "Home & Kitchen", "Sports"};
                String insertCategory = "INSERT IGNORE INTO categories (id, name) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertCategory)) {
                    for (int i = 0; i < categories.length; i++) {
                        pstmt.setInt(1, i + 1);
                        pstmt.setString(2, categories[i]);
                        pstmt.executeUpdate();
                    }
                }

                // 3. Generate Dummy Images
                System.out.println("Generating dummy product images...");
                File imgDir = new File("src/main/webapp/assets/img");
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }
                
                Random rand = new Random();
                for (int i = 1; i <= 10; i++) {
                    File imgFile = new File(imgDir, "prod" + i + ".jpg");
                    if (!imgFile.exists()) {
                        BufferedImage img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = img.createGraphics();
                        // Random pastel color
                        g2d.setColor(new Color(rand.nextFloat() * 0.5f + 0.5f, rand.nextFloat() * 0.5f + 0.5f, rand.nextFloat() * 0.5f + 0.5f));
                        g2d.fillRect(0, 0, 300, 300);
                        g2d.setColor(Color.DARK_GRAY);
                        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
                        g2d.drawString("Product Image " + i, 50, 150);
                        g2d.dispose();
                        ImageIO.write(img, "jpg", imgFile);
                    }
                }

                // 4. Insert 100 Products
                System.out.println("Inserting 100 products...");
                String insertProduct = "INSERT INTO products (name, description, price, stock, category_id, image_url) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertProduct)) {
                    String[] adjectives = {"Premium", "Wireless", "Smart", "Eco-friendly", "Durable", "Portable", "Luxury", "Compact"};
                    String[][] itemNames = {
                        {"Smartphone", "Laptop", "Headphones", "Tablet", "Monitor"}, // Electronics
                        {"T-Shirt", "Jeans", "Jacket", "Sneakers", "Cap"},           // Clothing
                        {"Organic Coffee", "Green Tea", "Almonds", "Honey", "Oats"}, // Groceries
                        {"Blender", "Toaster", "Knife Set", "Pan", "Mug"},           // Home
                        {"Yoga Mat", "Dumbbells", "Tennis Racket", "Football", "Jump Rope"} // Sports
                    };

                    int count = 0;
                    for (int i = 1; i <= 100; i++) {
                        int catIdx = rand.nextInt(5); // 0 to 4
                        int itemIdx = rand.nextInt(5); // 0 to 4
                        String adj = adjectives[rand.nextInt(adjectives.length)];
                        String baseName = itemNames[catIdx][itemIdx];
                        
                        String pName = adj + " " + baseName + " " + (1000 + i);
                        String pDesc = "This is a high-quality " + baseName.toLowerCase() + " featuring the latest " + adj.toLowerCase() + " design. Perfect for everyday use and highly recommended by professionals. Limited stock available!";
                        double pPrice = 10.0 + (rand.nextDouble() * 490.0); // 10 to 500
                        int pStock = rand.nextInt(100) + 5; // 5 to 104
                        int pCatId = catIdx + 1;
                        int imgNum = rand.nextInt(10) + 1; // 1 to 10
                        String pImageUrl = "assets/img/prod" + imgNum + ".jpg";

                        pstmt.setString(1, pName);
                        pstmt.setString(2, pDesc);
                        pstmt.setDouble(3, pPrice);
                        pstmt.setInt(4, pStock);
                        pstmt.setInt(5, pCatId);
                        pstmt.setString(6, pImageUrl);
                        pstmt.addBatch();
                        
                        count++;
                        if (count % 20 == 0) {
                            pstmt.executeBatch();
                        }
                    }
                    pstmt.executeBatch(); // Execute remaining
                }

                System.out.println("Seeding completed successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
