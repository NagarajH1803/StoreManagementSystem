package com.shopmanagement.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class SeedRealDatabase {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/shop_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "nagu";

    public static void main(String[] args) {
        System.out.println("Starting Real Database Seeding...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                
                // Truncate tables to replace old data
                System.out.println("Clearing old data...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                    stmt.execute("TRUNCATE TABLE order_items");
                    stmt.execute("TRUNCATE TABLE orders");
                    stmt.execute("TRUNCATE TABLE products");
                    stmt.execute("TRUNCATE TABLE categories");
                    stmt.execute("TRUNCATE TABLE users");
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
                }

                // 1. Insert Admin and Shopkeeper
                System.out.println("Inserting users...");
                String insertUser = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, SHA2(?, 256), ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, "admin123");
                    pstmt.setString(3, "System Administrator");
                    pstmt.setString(4, "admin@shop.com");
                    pstmt.setString(5, "MANAGER");
                    pstmt.executeUpdate();

                    pstmt.setString(1, "shopkeeper1");
                    pstmt.setString(2, "shop123");
                    pstmt.setString(3, "John Shopkeeper");
                    pstmt.setString(4, "john@shop.com");
                    pstmt.setString(5, "SHOPKEEPER");
                    pstmt.executeUpdate();
                }

                // 2. Insert Categories
                System.out.println("Inserting categories...");
                String[] categories = {"Electronics", "Clothing", "Groceries", "Home & Kitchen", "Sports & Outdoors"};
                String insertCategory = "INSERT INTO categories (id, name) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertCategory)) {
                    for (int i = 0; i < categories.length; i++) {
                        pstmt.setInt(1, i + 1);
                        pstmt.setString(2, categories[i]);
                        pstmt.executeUpdate();
                    }
                }

                // 3. Create Image Directory
                File imgDir = new File("src/main/webapp/assets/img/products");
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }

                // 4. Products Data (Category ID, Name, Price, Keyword, Description)
                Object[][] productsData = {
                    // ELECTRONICS (Category 1)
                    {1, "Apple iPhone 15 Pro", 129900.0, "iphone", "Latest Apple flagship with titanium body, A17 Pro chip, and advanced 48MP camera system."},
                    {1, "Samsung Galaxy S24 Ultra", 134900.0, "smartphone", "Premium Android device featuring AI capabilities, S-Pen support, and a brilliant AMOLED display."},
                    {1, "MacBook Air M3", 114900.0, "macbook", "Incredibly thin and light laptop powered by the M3 chip, offering up to 18 hours of battery life."},
                    {1, "Dell XPS 15", 145000.0, "laptop", "High-performance Windows laptop with a stunning 4K OLED display and powerful Intel Core i7 processor."},
                    {1, "Sony WH-1000XM5", 29990.0, "headphones", "Industry-leading noise canceling wireless headphones with unmatched audio quality and 30-hour battery."},
                    {1, "Apple AirPods Pro (2nd Gen)", 24900.0, "airpods", "True wireless earbuds with active noise cancellation, spatial audio, and MagSafe charging case."},
                    {1, "Samsung 65-inch 4K Smart TV", 85000.0, "tv", "Crystal clear 4K UHD resolution TV with smart features, built-in Alexa, and a sleek bezel-less design."},
                    {1, "LG OLED Evo C3 55-inch", 139990.0, "oled", "Premium OLED TV delivering perfect blacks, intense colors, and infinite contrast for a cinematic experience."},
                    {1, "Nintendo Switch OLED", 34990.0, "nintendo", "Versatile gaming console with a vibrant 7-inch OLED screen, enhanced audio, and flexible kickstand."},
                    {1, "PlayStation 5 Console", 49990.0, "playstation", "Next-gen gaming console offering lightning-fast loading, haptic feedback, and breathtaking 4K graphics."},
                    {1, "Xbox Series X", 49990.0, "xbox", "The fastest, most powerful Xbox ever. Play thousands of titles from four generations of consoles."},
                    {1, "GoPro HERO12 Black", 37990.0, "gopro", "Rugged, waterproof action camera with 5.3K video, HDR, and improved HyperSmooth 6.0 stabilization."},
                    {1, "Canon EOS R50", 64990.0, "camera", "Compact mirrorless camera perfect for content creators, featuring 4K video and advanced autofocus."},
                    {1, "Logitech MX Master 3S", 9995.0, "mouse", "Advanced wireless mouse with quiet clicks, ultra-fast scrolling, and ergonomic design for productivity."},
                    {1, "Keychron K2 Mechanical Keyboard", 8500.0, "keyboard", "Wireless mechanical keyboard with tactile switches, RGB backlight, and a compact 84-key layout."},
                    {1, "iPad Air (5th Gen)", 54900.0, "ipad", "Versatile tablet powered by the Apple M1 chip, featuring a 10.9-inch Liquid Retina display."},
                    {1, "Amazon Kindle Paperwhite", 13999.0, "kindle", "E-reader with a 6.8-inch display, adjustable warm light, and up to 10 weeks of battery life."},
                    {1, "Bose SoundLink Flex", 13500.0, "speaker", "Portable Bluetooth speaker providing crisp, clear sound. Waterproof and dustproof for outdoor use."},
                    {1, "Samsung Galaxy Watch 6", 25999.0, "smartwatch", "Advanced smartwatch with health tracking, sleep monitoring, and a larger, brighter display."},
                    {1, "Apple Watch Series 9", 41900.0, "applewatch", "Smarter, brighter, and more powerful watch featuring the new S9 chip and Double Tap gesture."},

                    // CLOTHING (Category 2)
                    {2, "Levi's 501 Original Fit Jeans", 3599.0, "jeans", "The iconic straight fit jeans with a signature button fly. A classic staple for any wardrobe."},
                    {2, "Nike Air Force 1 '07", 7495.0, "sneakers", "Classic everyday sneakers featuring a crisp leather upper, bold details, and comfortable cushioning."},
                    {2, "Adidas Ultraboost Light", 16999.0, "runningshoes", "High-performance running shoes featuring the lightest BOOST midsole yet for epic energy return."},
                    {2, "Polo Ralph Lauren T-Shirt", 4500.0, "tshirt", "Classic crewneck cotton t-shirt featuring the signature embroidered pony logo on the chest."},
                    {2, "North Face Nuptse Jacket", 22000.0, "jacket", "Iconic down-insulated puffer jacket providing incredible warmth and a retro 90s style."},
                    {2, "Calvin Klein Underwear Set", 2999.0, "underwear", "Comfortable cotton stretch underwear featuring the classic Calvin Klein logo waistband."},
                    {2, "H&M Slim Fit Chinos", 1999.0, "chinos", "Versatile slim-fit trousers made from a soft cotton blend, perfect for casual or smart-casual wear."},
                    {2, "Zara Faux Leather Biker Jacket", 5990.0, "leatherjacket", "Edgy biker jacket with asymmetrical zip fastening, metal details, and a tailored fit."},
                    {2, "Tommy Hilfiger Classic Hoodie", 6500.0, "hoodie", "Comfortable fleece hoodie featuring a drawstring hood, kangaroo pocket, and embroidered flag logo."},
                    {2, "Vans Old Skool Sneakers", 5500.0, "vans", "Classic skate shoe featuring the iconic side stripe, durable canvas upper, and waffle rubber outsoles."},
                    {2, "Under Armour Tech Polo", 3200.0, "polo", "Lightweight, breathable polo shirt made with sweat-wicking fabric for active days."},
                    {2, "Puma Suede Classic", 5999.0, "puma", "Timeless suede sneakers that have been a streetwear staple since the 1980s."},
                    {2, "Uniqlo Ultra Light Down Vest", 3990.0, "vest", "Incredibly light and warm down vest that packs compactly into an included pouch."},
                    {2, "Ray-Ban Aviator Classic", 8500.0, "sunglasses", "Iconic teardrop-shaped sunglasses originally designed for U.S. aviators in 1937."},
                    {2, "Fossil Leather Chronograph Watch", 12500.0, "watch", "Elegant men's watch featuring a genuine leather strap and a precise chronograph dial."},
                    {2, "Gucci GG Marmont Belt", 35000.0, "belt", "Luxury black leather belt featuring the signature Double G buckle in antique brass hardware."},
                    {2, "Patagonia Fleece Pullover", 9500.0, "fleece", "Warm and comfortable fleece sweater made from 100% recycled polyester."},
                    {2, "Reebok Club C 85", 6500.0, "reebok", "Vintage-inspired court shoes with a soft leather upper and minimalist design."},
                    {2, "Casio G-Shock Classic", 7995.0, "gshock", "Rugged, shock-resistant digital watch featuring a stopwatch, countdown timer, and 200m water resistance."},
                    {2, "Timberland Premium 6-Inch Boot", 14990.0, "boots", "The original waterproof work boot, crafted with premium leather and seam-sealed construction."},

                    // GROCERIES (Category 3)
                    {3, "Nescafe Classic Coffee 200g", 350.0, "coffee", "100% pure instant coffee offering a rich taste and unmistakable aroma to start your day."},
                    {3, "Twinings Earl Grey Tea 50 bags", 450.0, "tea", "A classic English afternoon tea flavored with the citrusy notes of bergamot."},
                    {3, "Nutella Hazelnut Spread 750g", 750.0, "nutella", "Delicious chocolate hazelnut spread, perfect for toast, pancakes, and baking."},
                    {3, "Kellogg's Corn Flakes 875g", 320.0, "cereal", "Crispy, golden flakes of corn for a healthy and nutritious breakfast."},
                    {3, "Quaker Oats 1kg", 200.0, "oats", "100% natural whole grain oats, rich in fiber and a great source of energy."},
                    {3, "Maggi 2-Minute Noodles (12 Pack)", 168.0, "noodles", "The classic instant noodle snack that's ready in just 2 minutes. Loved by everyone."},
                    {3, "Heinz Tomato Ketchup 1kg", 180.0, "ketchup", "Thick and rich tomato ketchup made from sweet, juicy, ripe tomatoes."},
                    {3, "Kikkoman Soy Sauce 500ml", 250.0, "soysauce", "Naturally brewed soy sauce that brings authentic Japanese flavor to your dishes."},
                    {3, "Oreo Original Sandwich Cookies", 80.0, "oreo", "Chocolate wafers filled with a sweet vanilla creme. Milk's favorite cookie."},
                    {3, "Doritos Nacho Cheese 150g", 50.0, "doritos", "Crunchy tortilla chips with a bold and tangy nacho cheese flavor."},
                    {3, "Coca-Cola Can 300ml (Pack of 6)", 240.0, "cocacola", "The refreshing, classic cola beverage served best chilled."},
                    {3, "Red Bull Energy Drink 250ml", 125.0, "redbull", "Vitalizes body and mind. The perfect energy boost for work, sports, or studying."},
                    {3, "Hershey's Chocolate Syrup 680g", 230.0, "syrup", "Rich chocolate syrup perfect for making chocolate milk, pouring over ice cream, or baking."},
                    {3, "Skippy Creamy Peanut Butter 462g", 350.0, "peanutbutter", "Smooth and creamy peanut butter made from high-quality roasted peanuts."},
                    {3, "Barilla Spaghetti Pasta 500g", 150.0, "pasta", "Premium Italian durum wheat semolina pasta that cooks to a perfect al dente."},
                    {3, "Amul Pure Ghee 1L", 550.0, "ghee", "Pure clarified butter with a rich aroma and granular texture, essential for Indian cooking."},
                    {3, "India Gate Basmati Rice 5kg", 600.0, "rice", "Premium long-grain basmati rice with exceptional aroma and taste."},
                    {3, "Tata Salt 1kg", 25.0, "salt", "Vacuum evaporated iodized salt ensuring purity and the right iodine content."},
                    {3, "Saffola Gold Cooking Oil 5L", 1150.0, "oil", "Healthy blended cooking oil formulated to help keep your heart healthy."},
                    {3, "Cadbury Dairy Milk Silk 150g", 175.0, "chocolate", "Smooth, creamy, and incredibly melt-in-your-mouth milk chocolate."},

                    // HOME & KITCHEN (Category 4)
                    {4, "Philips Air Fryer XL", 8999.0, "airfryer", "Fry, bake, grill, and roast your favorite foods with up to 90% less fat using rapid air technology."},
                    {4, "Dyson V15 Detect Vacuum", 65900.0, "vacuum", "Powerful cordless vacuum with laser illumination to reveal microscopic dust on hard floors."},
                    {4, "Instant Pot Duo 7-in-1", 7999.0, "instantpot", "Versatile multi-cooker acting as a pressure cooker, slow cooker, rice cooker, steamer, and more."},
                    {4, "KitchenAid Stand Mixer", 45000.0, "mixer", "Iconic artisan stand mixer with a 5-quart stainless steel bowl and 10 speed settings."},
                    {4, "Nespresso Vertuo Plus", 14999.0, "coffeemaker", "Convenient coffee and espresso machine utilizing Centrifusion technology for a perfect crema."},
                    {4, "Roomba j7+ Robot Vacuum", 59900.0, "robotvacuum", "Smart robot vacuum that avoids obstacles like pet waste and empties itself automatically."},
                    {4, "Ninja Professional Blender", 8500.0, "blender", "High-powered blender crushing ice and tough ingredients effortlessly for smoothies and shakes."},
                    {4, "Le Creuset Dutch Oven 5.5 Qt", 32000.0, "dutchoven", "Premium enameled cast iron oven perfect for slow-cooking, roasting, and baking."},
                    {4, "IKEA BILLY Bookcase", 3500.0, "bookcase", "The classic, versatile bookcase that fits seamlessly into any room decor."},
                    {4, "Philips Hue Starter Kit", 12500.0, "smartbulb", "Smart LED lighting system with 16 million colors, controllable via smartphone or voice assistants."},
                    {4, "Waterpik Aquarius Water Flosser", 6500.0, "waterflosser", "Advanced oral irrigator offering superior plaque removal and improved gum health."},
                    {4, "Cuisinart 14-Cup Food Processor", 18500.0, "foodprocessor", "Large capacity food processor ideal for slicing, dicing, chopping, and kneading dough."},
                    {4, "Brita Water Filter Pitcher", 2500.0, "waterfilter", "Everyday water pitcher that reduces chlorine taste and odor for cleaner, great-tasting water."},
                    {4, "Lodge Cast Iron Skillet 10.25-inch", 2200.0, "skillet", "Pre-seasoned cast iron skillet offering excellent heat retention for searing and frying."},
                    {4, "Tupperware Storage Container Set", 1800.0, "tupperware", "Airtight, durable plastic containers designed to keep food fresh for longer."},
                    {4, "Bedsure Fleece Blanket", 1500.0, "blanket", "Incredibly soft, warm, and lightweight microfiber fleece blanket for the couch or bed."},
                    {4, "YETI Rambler 20 oz Tumbler", 3500.0, "tumbler", "Double-wall vacuum insulated stainless steel tumbler keeping drinks hot or cold for hours."},
                    {4, "Ember Temperature Control Mug", 12999.0, "mug", "Smart mug that allows you to set an exact drinking temperature and keeps it there."},
                    {4, "Zinus 12 Inch Green Tea Memory Foam Mattress", 25000.0, "mattress", "Comfortable memory foam mattress infused with green tea for freshness and support."},
                    {4, "OXO Good Grips Pop Containers", 4500.0, "containers", "Stackable, space-efficient storage containers with a unique push-button airtight seal."},

                    // SPORTS & OUTDOORS (Category 5)
                    {5, "Yonex Astrox 99 Badminton Racket", 12500.0, "badminton", "Head-heavy power racket designed for advanced players seeking steep, devastating smashes."},
                    {5, "Spalding NBA Official Basketball", 3500.0, "basketball", "Premium composite leather basketball providing excellent grip and official NBA size and weight."},
                    {5, "Fitbit Charge 6 Fitness Tracker", 14999.0, "fitbit", "Advanced health and fitness tracker with built-in GPS, heart rate monitoring, and ECG app."},
                    {5, "Coleman Sundome 4-Person Tent", 6500.0, "tent", "Easy-to-setup camping tent featuring WeatherTec system to keep you dry in rain."},
                    {5, "Hydro Flask Standard Mouth Water Bottle", 3200.0, "waterbottle", "Insulated stainless steel water bottle keeping beverages cold up to 24 hours or hot up to 12."},
                    {5, "Bowflex SelectTech 552 Dumbbells", 35000.0, "dumbbells", "Adjustable dumbbells replacing 15 sets of weights, adjustable from 5 to 52.5 lbs each."},
                    {5, "Manduka PRO Yoga Mat", 9500.0, "yogamat", "Ultra-dense, spacious yoga mat providing unmatched comfort, cushioning, and joint protection."},
                    {5, "Garmin Forerunner 265", 45990.0, "garmin", "Premium GPS running smartwatch with a bright AMOLED display and advanced training metrics."},
                    {5, "Osprey Atmos AG 65 Backpack", 25000.0, "backpack", "Highly ventilated and comfortable backpacking pack with Anti-Gravity suspension system."},
                    {5, "Titleist Pro V1 Golf Balls (Dozen)", 4500.0, "golfballs", "The #1 ball in golf, offering total performance, penetrating flight, and soft feel."},
                    {5, "Speedo Vanquisher 2.0 Swim Goggles", 1800.0, "goggles", "Low-profile, mirrored swimming goggles offering UV protection and anti-fog lenses."},
                    {5, "Decathlon Rockrider ST100 Mountain Bike", 14999.0, "bike", "Versatile mountain bike featuring a lightweight aluminum frame, 21 speeds, and front suspension."},
                    {5, "Everlast Pro Style Boxing Gloves", 2500.0, "boxinggloves", "Durable synthetic leather training gloves with secure hook-and-loop wrist straps."},
                    {5, "Nivia Antrix Football", 800.0, "football", "High-quality, durable football ideal for training and recreational play on all surfaces."},
                    {5, "Babolat Pure Drive Tennis Racket", 18500.0, "tennis", "Iconic tennis racket offering a perfect blend of power, spin, and maneuverability."},
                    {5, "CamelBak M.U.L.E. Hydration Pack", 8500.0, "hydration", "Narrow-gauge hydration backpack perfect for mountain biking and hiking, includes a 3L reservoir."},
                    {5, "Black Diamond Spot 400 Headlamp", 3500.0, "headlamp", "Compact, waterproof headlamp outputting 400 lumens, ideal for camping and night runs."},
                    {5, "Kookaburra Kahuna Cricket Bat", 15000.0, "cricketbat", "Premium English willow cricket bat featuring a powerful profile and thick edges."},
                    {5, "Theragun Prime Massage Gun", 25000.0, "massagegun", "Quiet and powerful percussive therapy device to relieve muscle soreness and tension."},
                    {5, "Giro Register MIPS Bike Helmet", 4500.0, "helmet", "Comfortable, adjustable cycling helmet featuring integrated MIPS technology for enhanced safety."}
                };

                String insertProduct = "INSERT INTO products (name, description, price, stock, category_id, image_url) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertProduct)) {
                    Random rand = new Random();
                    int count = 0;
                    
                    System.out.println("Downloading images and inserting products (this may take a minute)...");
                    for (Object[] pd : productsData) {
                        int catId = (int) pd[0];
                        String name = (String) pd[1];
                        double price = (double) pd[2];
                        String keyword = (String) pd[3];
                        String desc = (String) pd[4];
                        int stock = rand.nextInt(50) + 10; // 10 to 59
                        
                        // Download Image
                        String filename = "prod_" + count + "_" + keyword + ".jpg";
                        String imagePath = "assets/img/products/" + filename;
                        File targetFile = new File("src/main/webapp/" + imagePath);
                        
                        try {
                            // Using ?lock= to get different images for the same keyword if repeated
                            String imgUrlStr = "https://loremflickr.com/400/400/" + keyword + "?lock=" + count;
                            URL url = new URL(imgUrlStr);
                            try (InputStream in = url.openStream()) {
                                Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (Exception e) {
                            System.err.println("Warning: Could not download image for " + name + ". Using placeholder.");
                            // If download fails, we will just use a static placeholder path (might not render if file missing)
                        }

                        pstmt.setString(1, name);
                        pstmt.setString(2, desc);
                        pstmt.setDouble(3, price);
                        pstmt.setInt(4, stock);
                        pstmt.setInt(5, catId);
                        pstmt.setString(6, imagePath);
                        pstmt.executeUpdate();
                        
                        count++;
                        if (count % 10 == 0) System.out.println("Processed " + count + " / 100 products...");
                    }
                }

                System.out.println("Real World Seeding completed successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
