package first_exercise_bonus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.text.ParseException;




public class Registry {
	private static String DATABASE_URL;
    private static String USERNAME;
    private static String PASSWORD;

    public Registry() {
    	loadDatabaseCredentials();
        DatabaseManager.initializeDatabase();
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

    public Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    public boolean addCitizen(Citizen citizen) {
        String sql = "INSERT INTO Citizens (id, firstName, lastName, gender, birthDate, taxId, address) VALUES (?, ?, ?, ?, CONVERT(DATE, ?, 105), ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, citizen.getId());
            pstmt.setString(2, citizen.getFirstName());
            pstmt.setString(3, citizen.getLastName());
            pstmt.setString(4, citizen.getGender());
            pstmt.setString(5, citizen.getBirthDate());
            pstmt.setString(6, citizen.getTaxId());
            pstmt.setString(7, citizen.getAddress());
            
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error adding citizen: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCitizen(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.out.println("Invalid ID. Please provide a valid 8-character ID.");
            return false;
        }

        String sql = "DELETE FROM Citizens WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Citizen with ID '" + id + "' was successfully deleted.");
                return true;
            } else {
                System.out.println("No citizen found with ID '" + id + "'.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error deleting citizen: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCitizen(String id, String taxId, String address) {
        if (id == null || id.trim().isEmpty() || id.length() != 8) {
            System.out.println("Error: Invalid ID. Please ensure it has exactly 8 characters.");
            return false;
        }

        if (!taxId.isEmpty() && taxId.length() != 9) {
            System.out.println("Error: Tax ID must be exactly 9 characters or left empty.");
            return false;
        }

        String sql = """
            UPDATE Citizens
            SET 
                taxId = CASE WHEN ? in ('', NULL) THEN '' ELSE ? END, 
                address = ?
            WHERE id = ?
        """;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, taxId);
            pstmt.setString(2, taxId);
            pstmt.setString(3, address);
            pstmt.setString(4, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Citizen with ID '" + id + "' was updated successfully.");
                return true;
            } else {
                System.out.println("Error: No citizen found with the provided ID.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error updating citizen: " + e.getMessage());
            return false;
        }
    }


    public boolean isValidCriteria(String criteria) {
    	criteria = criteria.toLowerCase();
        List<String> validCriteria = Arrays.asList("id", "firstname", "lastname", "gender", "birthdate", "taxid", "address");
        return validCriteria.contains(criteria.toLowerCase());
    }

    public boolean isValidInput(String criteria, String value) {
        switch (criteria) {
            case "id":
                if (value.length() != 8) {
                    System.out.println("\nError: 'ID' parsed value should have a length of 8. You entered: '" + value +"' with length "+ value.length()+".");
                    return false;
                }
                return true;
            case "gender":
                if (!value.equalsIgnoreCase("male") && !value.equalsIgnoreCase("female")) {
                    System.out.println("\nError: 'Gender' parsed value should be either 'male' or 'female'. You entered: '" + value +"'.");
                    return false;
                }
                return true;
            case "taxId":
                if (value.length() != 9 & value.length() != 0) {
                    System.out.println("\nError: 'TaxId' parsed value should have a length of 9. You entered: '" + value +"' with length "+ value.length()+".");
                    return false;
                }
                return true;
            default:
                return true;
        }
    }
    
    public boolean isValidDate(String birthDate) {
        if (birthDate == null || birthDate.trim().isEmpty()) {
            System.out.println("\nError: 'BirthDate' cannot be null or empty.");
            return false;
        }

        String dateFormat = "dd-MM-yyyy";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sdf.setLenient(false);

            sdf.parse(birthDate);
            return true;
        } catch (ParseException e) {
            System.out.println("\nError: 'BirthDate' must be in the format DD-MM-YYYY. You entered: '" + birthDate + "'.");
            return false;
        }
    }

    public List<Citizen> searchCitizens(Map<String, String> criteriaMap) {
        StringBuilder sql = new StringBuilder("SELECT id, firstName, lastName, gender, format(birthDate, 'dd-MM-yyyy') as birthDate, taxId, address FROM Citizens WHERE ");
        List<String> conditions = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : criteriaMap.entrySet()) {
            String field = entry.getKey().toLowerCase();
            String value = entry.getValue();

            if (isValidCriteria(field)) {
                if (isValidInput(field, value)) {
                    conditions.add(field + " = ?");
                    values.add(value);
                } else {
                    System.out.println("\nInvalid value for '" + field + "': '" + value + "'. \nPlease check the criteria values.");
                    return new ArrayList<>();
                }
            } else {
                System.out.println("Invalid field: " + field);
                return new ArrayList<>();
            }
        }

        if (conditions.isEmpty()) {
            System.out.println("\nNo valid search criteria-values provided.");
            return new ArrayList<>();
        }

        sql.append(String.join(" AND ", conditions));

        List<Citizen> citizens = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (String value : values) {
                pstmt.setString(index++, value);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Citizen citizen = new Citizen(
                    rs.getString("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("gender"),
                    rs.getString("birthDate"),
                    rs.getString("taxId"),
                    rs.getString("address")
                );
                citizens.add(citizen);
            }
        } catch (SQLException e) {
            System.out.println("Error searching citizens: " + e.getMessage());
        }

        return citizens;
    }
    
    public void printAllCitizens() {
        String query = "SELECT id, firstName, lastName, gender, format(birthDate, 'dd-MM-yyyy') as birthDate, taxId, address FROM Citizens";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No records found.");
                return;
            }

            System.out.printf("%-10s | %-15s | %-15s | %-10s | %-12s | %-10s | %-20s%n",
                    "ID", "First Name", "Last Name", "Gender", "Birth Date", "Tax ID", "Address");
            System.out.println("---------------------------------------------------------------------------------------------");

            while (rs.next()) {
                String id = rs.getString("id");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String gender = rs.getString("gender");
                String birthDate = rs.getString("birthDate");
                String taxId = rs.getString("taxId");
                String address = rs.getString("address");

                System.out.printf("%-10s | %-15s | %-15s | %-10s | %-12s | %-10s | %-20s%n",
                        id, firstName, lastName, gender, birthDate, taxId != null ? taxId : "N/A", address != null ? address : "N/A");
            }

            System.out.println("---------------------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("Error retrieving records: " + e.getMessage());
        }
    }
}
