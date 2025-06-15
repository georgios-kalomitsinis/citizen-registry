package first_exercise_bonus;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class Application {
    private static Registry registry;

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        registry = new Registry();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean exit = false;
            while (!exit) {
                System.out.println("\nCitizen Registry Management System:");
                System.out.println("1. Add Citizen");
                System.out.println("2. Delete Citizen");
                System.out.println("3. Update Citizen");
                System.out.println("4. Search Citizen(s)");
                System.out.println("5. Print All Citizens");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                
                String input = scanner.nextLine();

                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a number between 1 and 6.");
                    continue;
                }
                String id; 

                switch (choice) {
                    case 1:
                        addCitizen(scanner);
                        break;
                    case 2:
                    	System.out.print("\nEnter the ID of the citizen to delete: ");
                        id = scanner.nextLine();
                        registry.deleteCitizen(id);
                        break;
                    case 3:
                        System.out.print("\nEnter the ID of the citizen to update: ");
                        id = scanner.nextLine();
                        if (!registry.isValidInput("id", id)) {
                            System.out.println("Error: Invalid ID. Update aborted.");
                            break;
                        }

                        System.out.print("Enter the new Tax ID (9 characters, or leave empty): ");
                        String newTaxId = scanner.nextLine();
                        if (!newTaxId.isEmpty() && newTaxId.length() != 9) {
                            System.out.println("Error: Tax ID must be exactly 9 characters or left empty. Update aborted.");
                            break;
                        }

                        System.out.print("Enter the new Address (or leave empty): ");
                        String newAddress = scanner.nextLine();

                        if (registry.updateCitizen(id, newTaxId, newAddress)) {
                            System.out.println("Citizen updated successfully.");
                        } else {
                            System.out.println("Failed to update citizen. Please check the details and try again.");
                        }
                        break;
                    case 4:
                        System.out.println("\nEnter search criteria - you can specify one or more from the list below:");
                        System.out.println("• ID\n• FirstName\n• LastName\n• Gender\n• BirthDate\n• TaxID\n• Address");
                        Map<String, String> criteriaMap = new HashMap<>();

                        boolean returnToMenu = false;

                        while (true) {
                            System.out.print("Enter the field to search by (or type 'Enter' to finish): ");
                            String field = scanner.nextLine();
                            if (field.isEmpty()) {
                                break;
                            }

                            if (!registry.isValidCriteria(field)) {
                                System.out.println("\nInvalid field: '" + field + "'. Returning to the main menu.");
                                returnToMenu = true;
                                break;
                            }

                            System.out.print("Enter the value for '" + field + "': ");
                            String value = scanner.nextLine();

                            if ((field.equalsIgnoreCase("firstName") || field.equalsIgnoreCase("lastName")) && value.trim().isEmpty()) {
                                System.out.println("\nError: '" + field + "' cannot be empty. Returning to the main menu.");
                                returnToMenu = true;
                                break;
                            }

                            if (field.equalsIgnoreCase("taxId")) {
                                if (!value.isEmpty() && value.length() != 9) {
                                    System.out.println("\nError: 'taxId' must be exactly 9 characters or left empty. Returning to the main menu.");
                                    returnToMenu = true;
                                    break;
                                }
                            }

                            if (field.equalsIgnoreCase("birthDate")) {
                                if (!registry.isValidDate(value)) {
                                    System.out.println("\nInvalid value for 'birthDate': '" + value + "'. Please use the format DD-MM-YYYY.");
                                    returnToMenu = true;
                                    break;
                                }
                            } else if (!registry.isValidInput(field, value)) {
                                System.out.println("\nInvalid value for '" + field + "': '" + value + "'. Returning to the main menu.");
                                returnToMenu = true;
                                break;
                            }

                            criteriaMap.put(field, value);
                        }

                        if (returnToMenu) {
                            break;
                        }

                        if (criteriaMap.isEmpty()) {
                            System.out.println("No valid search criteria provided. Returning to the main menu.");
                            break;
                        }

                        List<Citizen> foundCitizens = registry.searchCitizens(criteriaMap);

                        if (!foundCitizens.isEmpty()) {
                            System.out.println("Found citizens:");
                            for (Citizen citizen : foundCitizens) {
                                System.out.println(citizen);
                            }
                        } else {
                            System.out.println("No citizens found matching the criteria.");
                        }
                        break;
                    case 5:
                    	registry.printAllCitizens();
                        break;
                    case 6:
                        exit = true;
                        System.out.println("Exiting application...");
                        break;
                    default:
                        System.out.println("Invalid option. Please choose again.");
                }
            }
        }
    }

    private static void addCitizen(Scanner scanner) {
        System.out.println("\nEnter Citizen Details:");
        System.out.print("ID (8 characters): ");
        String id = scanner.nextLine();

        if (!registry.isValidInput("id", id)) {
            return;
        }

        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        if (firstName.trim().isEmpty()) {
            System.out.println("Error: First Name is mandatory and cannot be empty.");
            return;
        }

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        if (lastName.trim().isEmpty()) {
            System.out.println("Error: Last Name is mandatory and cannot be empty.");
            return;
        }

        System.out.print("Gender (Male/Female): ");
        String gender = scanner.nextLine();
        if (!registry.isValidInput("gender", gender)) {
            return;
        }

        System.out.print("Birth Date (day-month-year [DD-MM-YYYY]): ");
        String birthDate = scanner.nextLine();
        if (!registry.isValidDate(birthDate)) {
            return;
        }

        System.out.print("Tax ID (9 characters): ");
        String taxId = scanner.nextLine();
        if (!registry.isValidInput("taxId", taxId)) {
            return;
        }

        System.out.print("Address: ");
        String address = scanner.nextLine();

        Citizen citizen = new Citizen(id, firstName, lastName, gender, birthDate, taxId, address);

        if (registry.addCitizen(citizen)) {
            System.out.println("Citizen added successfully!!!");
        } else {
            System.out.println("Failed to add citizen.");
        }
    }

}