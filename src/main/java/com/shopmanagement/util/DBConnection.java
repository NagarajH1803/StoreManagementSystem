package com.shopmanagement.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection manager using HikariCP connection pool.
 * Industry-standard pooling for performance and reliability.
 */
public class DBConnection {

    private static final HikariDataSource dataSource;

    static {
        try {
            // Load db.properties as fallback for local development
            Properties props = new Properties();
            InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            if (is != null) {
                props.load(is);
            }

            // Environment variables take priority (used in production on Render/Docker)
            // Falls back to db.properties values for local development
            String driver = envOrProp("DB_DRIVER", props, "db.driver", "com.mysql.cj.jdbc.Driver");
            String url = envOrProp("DB_URL", props, "db.url", null);
            String username = envOrProp("DB_USERNAME", props, "db.username", null);
            String password = envOrProp("DB_PASSWORD", props, "db.password", null);

            if (url == null || username == null) {
                throw new RuntimeException(
                    "Database not configured. Set DB_URL/DB_USERNAME/DB_PASSWORD environment variables, " +
                    "or create src/main/resources/db.properties (see db.properties.example)."
                );
            }

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password != null ? password : "");

            // Pool configuration
            config.setMinimumIdle(2);
            config.setMaximumPoolSize(10);
            config.setIdleTimeout(300000);       // 5 minutes
            config.setConnectionTimeout(20000);  // 20 seconds
            config.setMaxLifetime(1200000);      // 20 minutes
            config.setLeakDetectionThreshold(60000); // 1 minute

            // Performance settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize HikariCP connection pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // Logged by HikariCP internally
                }
            }
        }
    }

    /**
     * Shutdown the connection pool gracefully (call on app destroy).
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Read a config value: env var first, then properties file, then default.
     */
    private static String envOrProp(String envKey, Properties props, String propKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isEmpty()) {
            return envVal;
        }
        String propVal = props.getProperty(propKey);
        if (propVal != null && !propVal.isEmpty()) {
            return propVal;
        }
        return defaultValue;
    }
}
