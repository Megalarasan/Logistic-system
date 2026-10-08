package Presentation.transport;

import Auxiliary.Enums.Domain.Status;
import Domain.Employees.HR_Lead;
import Domain.transport.DriversManager;
import Models.DBModels.transport.Driver;
import Auxiliary.Helper;
import Presentation.Employee.HR_Lead_Panel;

import java.util.ArrayList;

public class DriversHandler {
    private static boolean addDriver() {
        String firstName = null;
        String lastName = null;
        String personalId = null;
        String phoneNumber = null;
        String licenseType = null;

        try {
            Helper.println("Driver creation:");
            do {
                Helper.print("Enter Driver first name: ");
                firstName = Helper.getUserInputString();
            } while (firstName == null || firstName.isEmpty());
            do {
                Helper.print("Enter Driver last name: ");
                lastName = Helper.getUserInputString();
            } while (lastName == null || lastName.isEmpty());
            do {
                Helper.print("Enter Driver personal ID: ");
                personalId = Helper.getUserInputString();
            } while (personalId == null || personalId.isEmpty());
            do {
                Helper.print("Enter Driver phone number: ");
                phoneNumber = Helper.getUserInputString();
            } while (phoneNumber == null || phoneNumber.isEmpty());
            do {
                Helper.print("Enter Driver license type: ");
                licenseType = Helper.getUserInputString();
            } while (licenseType == null || licenseType.isEmpty());
        } catch (Exception e) {
            Helper.println("Error: " + e.getMessage());
            return false;
        }
        boolean f = DriversManager.addDriver(firstName, lastName, personalId, phoneNumber, licenseType);
        if (!f) {
            Helper.showError("Error creating new driver.");
            return false;
        }
        if (HR_Lead_Panel.Add_Driver_to_System(firstName, personalId) == Status.Success) {
            ;//TODO @Ethan link with employees module, it is here.
            return true;
        } else {
            return false;
        }
    }

    private static boolean updateDriver() {
        String someString;
        int id = Helper.UndefinedUserInput;
        do {
            Helper.println("Enter Driver ID to update: ");
            id = Helper.getUserInputInteger();
        } while (id == Helper.UndefinedUserInput);
        int choice;
        try {
            Helper.println("Choose attribute to update:");
            Helper.println("1. First Name");
            Helper.println("2. Last Name");
            Helper.println("3. Personal ID");
            Helper.println("4. Phone Number");
            Helper.println("5. License Type");
            Helper.println("6. Back to Main Menu");
            someString = null;
            do {
                choice = Helper.getUserInputInteger();
                switch (choice) {
                    case 1:
                        Helper.print("Enter new first name: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 2:
                        Helper.print("Enter new last name: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 3:
                        Helper.print("Enter new personal ID: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 4:
                        Helper.print("Enter new phone number: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 5:
                        Helper.print("Enter new license type: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 6:
                        return false;
                    default:
                        Helper.println("Invalid choice, please try again.");
                        break;
                }
            } while (choice == Helper.UndefinedUserInput);
        } catch (Exception e) {
            Helper.showError("Error: " + e.getMessage());
            return false;
        }
        boolean f = DriversManager.updateDriver(someString, id, choice);
        if (!f) {
            Helper.showError("Error updating driver.");
            return false;
        }
        return true;
    }

    private static boolean deleteDriver() {
        int id = Helper.UndefinedUserInput;
        do {
            Helper.print("Enter Driver ID to delete: ");
            id = Helper.getUserInputInteger();
        } while (id == Helper.UndefinedUserInput);
        boolean f = DriversManager.deleteDriver(id);
        if (!f) {
            Helper.showError("Driver with this ID does not exist");
            return false;
        }
        return true;
    }

    private static void printDriverById() {
        int id = Helper.UndefinedUserInput;
        do {
            Helper.print("Enter Driver ID to view: ");
            id = Helper.getUserInputInteger();
        } while (id == Helper.UndefinedUserInput);
        Driver d = DriversManager.getDriverById(id);
        if (d == null) {
            Helper.showError("No driver found with ID: " + id);
            return;
        }
        Helper.println(d.toString());
    }

    private static void viewAllDrivers() {
        // Implement method to view all drivers
        // This could be a simple print of all drivers in the system
        Helper.println("List of all drivers:");
        // Assuming DriversManager has a method to get all drivers
        ArrayList<Driver> drivers = DriversManager.getAllDrivers();
        if (drivers == null || drivers.isEmpty()) {
            Helper.println("No drivers found.");
            return;
        }
        for (Driver driver : DriversManager.getAllDrivers()) {
            Helper.println("ID: " + Integer.toString(driver.getId()) + ", First name:  " + driver.getFirstName() + ", Last name: " + driver.getLastName());
        }
    }

    public static void handleDrivers() {
        boolean exit = false;
        while (!exit) {
            int choice;
            Helper.println("Manage Drivers:");
            Helper.println("1. Add Driver");
            Helper.println("2. Update Driver");
            Helper.println("3. Delete Driver");
            Helper.println("4. View Driver by ID");
            Helper.println("5. View Existing Drivers");
            Helper.println("6. Back to Main Menu");

            do {
                Helper.print("Please choose an option: ");
                choice = Helper.getUserInputInteger();
            } while (choice == Helper.UndefinedUserInput);
            switch (choice) {
                case 1:
                    addDriver();
                    break;
                case 2:
                    updateDriver();
                    break;
                case 3:
                    deleteDriver();
                    break;
                case 4:
                    printDriverById();
                    break;
                case 5:
                    viewAllDrivers();
                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    Helper.println("Invalid choice, please try again.");
            }
        }
    }
}