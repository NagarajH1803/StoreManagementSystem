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
            Properties props = new Properties();
            InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(is);

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(props.getProperty("db.driver"));
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));

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
}
