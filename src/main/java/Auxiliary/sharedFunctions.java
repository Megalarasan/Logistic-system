package Auxiliary;


import Domain.Employees.*;
import Models.DBModels.Employees.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.time.LocalTime;

public class sharedFunctions {

    public static LocalDate parseDate(Scanner scanner, String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter date in format dd/MM/yyyy:");
            }
        }
    }

    public static LocalTime parseTimeFromUser(Scanner scanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        while (true) {
            System.out.println("Enter time in format HH:mm ,example: \"09:45\"");
            String input = scanner.nextLine().trim();
            try {
                return LocalTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please try again.");
            }
        }
    }

    public static String getShiftTypeFromUser(Scanner scanner) {
        System.out.println("Please enter the shift type (Day or Night):");
        String shiftType = scanner.nextLine().trim();

        while (!isValidShiftType(shiftType)) {
            System.out.println("Invalid input. Please enter either 'Day' or 'Night':");
            shiftType = scanner.nextLine().trim();
        }
        if (shiftType.equalsIgnoreCase("Day")) {
            return "Day";
        } else {
            return "Night";
        }
    }

    private static boolean isValidShiftType(String shiftType) {
        return shiftType.equalsIgnoreCase("Day") || shiftType.equalsIgnoreCase("Night");
    }

    public static ARole promptForRole(int choice) {
        ARole role = null;
        while (role == null) {
            switch (choice) {
                case 1:
                    role = new Shift_Manager(IdGenerator.getNextIdManager());
                    break;
                case 2:
                    role = new HR_Lead(IdGenerator.getNextIdHR());
                    break;
                case 3:
                    role = new Cashier(IdGenerator.getNextIdCashier());
                    break;
                case 4:
                    role = new Cleaner(IdGenerator.getNextIdCleaner());
                    break;
                case 5:
                    role = new Stocker(IdGenerator.getNextIdStocker());
                    break;
                case 6:
                    role = new Security(IdGenerator.getNextIdSecurity());
                    break;
                case 7:
                    role = new Driver(IdGenerator.getNextIdDriver());
                    break;
                default:
                    System.out.println("Invalid choice. Promotion cancelled.");
                    return null;
            }
        }
        return role;
    }

    public static void DisplayAvailableRoles() { //calling DisplayAvailableRoles() will use preset = 0 by default.
        DisplayAvailableRoles(0);
    }

    public static void DisplayAvailableRoles(int preset) {
        if (preset == 0) { //regular roles
            System.out.println("1. Shift Manager");
            System.out.println("2. HR Lead");
            System.out.println("3. Cashier");
            System.out.println("4. Cleaner");
            System.out.println("5. Stocker");
            System.out.println("6. Security");
        }
        if (preset == 1) { // with the added driver role
            System.out.println("1. Shift Manager");
            System.out.println("2. HR Lead");
            System.out.println("3. Cashier");
            System.out.println("4. Cleaner");
            System.out.println("5. Stocker");
            System.out.println("6. Security");
            System.out.println("7. Driver");
        }
    }

    public static int getValidIntChoice(Scanner scanner, int from, int to) {
        while (true) {
            try {
                int choice = scanner.nextInt();
                if (choice >= from && choice <= to) {
                    return choice;
                } else {
                    System.out.printf("Invalid choice. Please enter a number between %d and %d.%n", from, to);
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // clear invalid input
            }
        }
    }
}