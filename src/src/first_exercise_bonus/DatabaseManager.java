package first_exercise_bonus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseManager {
    private static String DATABASE_URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static boolean initialized = false; 

    public static void initializeDatabase() {
        if (initialized) return;        
        
        loadDatabaseCredentials();
        
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String createDatabaseSQL = """
                    IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'app')
                    BEGIN
                        CREATE DATABASE app;
                    END
                """;
                
                stmt.executeUpdate(createDatabaseSQL);
                System.out.println("Database `app` created if it didn't exist!");
            } catch (SQLException e) {
                System.out.println("Error creating database: " + e.getMessage());
                return;
            }

            String appDatabaseURL = DATABASE_URL + "databaseName=app;";
            try (Connection conn = DriverManager.getConnection(appDatabaseURL, USERNAME, PASSWORD);
                 Statement stmt = conn.createStatement()) {

                String createTableSQL = """
                    IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Citizens' AND xtype='U')
                    CREATE TABLE Citizens (
                        id VARCHAR(8) PRIMARY KEY,
                        firstName NVARCHAR(50) NOT NULL,
                        lastName NVARCHAR(50) NOT NULL,
                        gender NVARCHAR(6) NOT NULL,
                        birthDate DATE NOT NULL,
                        taxId VARCHAR(9) UNIQUE NULL,
                        address NVARCHAR(100) NULL,
                        CONSTRAINT check_id_length CHECK (len(id)=8),
                        CONSTRAINT check_taxId_length CHECK (len(taxId)=9 OR len(taxId) IS NULL OR len(taxId)=0),
                        CONSTRAINT check_gender_value CHECK (lower(gender) IN ('male', 'female'))
                    );

                    IF NOT EXISTS (
                        SELECT * FROM sys.indexes WHERE name = 'Citizens_TaxId' AND object_id = OBJECT_ID('Citizens'))
                    BEGIN
                        CREATE UNIQUE INDEX Citizens_TaxId ON Citizens(taxId)
                        WHERE taxId IS NOT NULL AND taxId <> '';
                    END;
                """;
                stmt.executeUpdate(createTableSQL);
                System.out.println("Citizens table created in the `app` database if it did not exist!");
            } catch (SQLException e) {
                System.out.println("Error initializing database: " + e.getMessage());
            }

            initialized = true;

        } catch (ClassNotFoundException e) {
            System.out.println("SQL Server JDBC Driver not found: " + e.getMessage());
        }
    }

    
    private static void loadDatabaseCredentials() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            
            
            DATABASE_URL = properties.getProperty("db.url");
            USERNAME = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");

        } catch (IOException e) {
            System.out.println("Error loading configuration file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
