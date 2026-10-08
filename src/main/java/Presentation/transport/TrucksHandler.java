package Presentation.transport;

//mport Domain.transport.DriversManager;
import Domain.transport.TrucksManager;
import Auxiliary.Helper;
import Models.DBModels.transport.Truck;

import java.util.ArrayList;

/**
 * TrucksHandler class is responsible for handling truck-related operations.
 * It provides methods to add, update, delete, and print truck information.
 */
public class TrucksHandler {
    private static boolean addTruck() {
        String plate;
        double netWeight;
        double maxWeight;
        String requiredLicenseType;
        String model;
        try {
            Helper.println("Truck creation:");
            do {
                Helper.print("Enter Truck plate: ");
                plate = Helper.getUserInputString();
            } while (plate == null || plate.isEmpty());
            do {
                Helper.print("Enter Truck net weight: ");
                netWeight = Helper.getUserInputDouble();
            } while (netWeight <= 0);
            do {
                Helper.print("Enter Truck max weight: ");
                maxWeight = Helper.getUserInputDouble();
            } while (maxWeight <= 0);
            do {
                Helper.print("Enter Truck required license type: ");
                requiredLicenseType = Helper.getUserInputString();
            } while (requiredLicenseType == null || requiredLicenseType.isEmpty());
            do {
                Helper.print("Enter Truck model: ");
                model = Helper.getUserInputString();
            } while (model == null || model.isEmpty());
        } catch (Exception e) {
            Helper.showError("Error: " + e.getMessage());
            return false;
        }
        boolean f = TrucksManager.addTruck(plate, netWeight, maxWeight, requiredLicenseType, model);
        if (!f) {
            Helper.showError("Truck with this plate already exists");
            return false;
        }
        return true;
    }

    /**
     * Updates a truck's information based on user input.
     *
     * @return true if the update was successful, false otherwise
     */
    private static boolean updateTruck() {
        String someString;
        int id = Helper.UndefinedUserInput;
        do {
            Helper.println("Enter Truck ID to update: ");
            id = Helper.getUserInputInteger();
        } while (id == Helper.UndefinedUserInput);
        int choice;
        try {
            Helper.println("Choose attribute to update:");
            Helper.println("1. Plate");
            Helper.println("2. Net Weight");
            Helper.println("3. Max Weight");
            Helper.println("4. Required License Type");
            Helper.println("5. Model");
            Helper.println("6. Back to Main Menu");
            someString = null;
            do {
                choice = Helper.getUserInputInteger();
                switch (choice) {
                    case 1:
                        Helper.print("Enter new plate: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 2:
                        Helper.print("Enter new net weight: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 3:
                        Helper.print("Enter new max weight: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 4:
                        Helper.print("Enter new required license type: ");
                        someString = Helper.getUserInputString();
                        break;
                    case 5:
                        Helper.print("Enter new model: ");
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
        boolean f = TrucksManager.updateTruck(someString, id, choice);
        if (!f) {
            Helper.showError("Error updating truck");
            return false;
        }
        return true;
    }

    /**
     * Deletes a truck based on user input.
     *
     * @return true if the deletion was successful, false otherwise
     */
    private static boolean deleteTruck() {
        int id = Helper.UndefinedUserInput;
        do {
            Helper.print("Enter Truck ID to delete: ");
            id = Helper.getUserInputInteger();
        } while (id == Helper.UndefinedUserInput);
        boolean f = TrucksManager.deleteTruck(id);
        if (!f) {
            Helper.showError("Error deleting truck");
            return false;
        }
        return true;
    }

    /**
     * Prints the details of a truck based on user input.
     */
    private static void printTruckById() {
        int id = Helper.UndefinedUserInput;
        do {
            Helper.println("Enter Truck ID to print: ");
            id = Helper.getUserInputInteger();
        } while (id == Helper.UndefinedUserInput);
        Truck t = TrucksManager.getTruckById(id);
        if (t == null) {
            Helper.showError("Truck with this ID does not exist");
            return;
        }
        Helper.println(t.toString());
    }

    /**
     * Prints the details of all trucks.
     */
    private static void printAllTrucks() {
        ArrayList<Truck> trucks = TrucksManager.getAllTrucks();
        if (trucks == null || trucks.isEmpty()) {
            Helper.showError("No trucks found");
            return;
        }
        for (Truck t : trucks) {
            Helper.println("ID: " + Integer.toString(t.getId()) + ", Plate: " + t.getPlate());
        }
    }

    /**
     * Handles the truck management menu and user interactions.
     */
    public static void HandleTrucks(){
        int choice;
        boolean exit = false;
        while (!exit) {
            Helper.println("Truck Management Menu:");
            Helper.println("1. Add Truck");
            Helper.println("2. Update Truck");
            Helper.println("3. Delete Truck");
            Helper.println("4. Print Truck by ID");
            Helper.println("5. Print All Trucks");
            Helper.println("6. Back to Main Menu");

            do {
                Helper.print("Please choose an option: ");
                choice = Helper.getUserInputInteger();
            } while (choice == Helper.UndefinedUserInput);

            switch (choice) {
                case 1:
                    addTruck();
                    break;
                case 2:
                    updateTruck();
                    break;
                case 3:
                    deleteTruck();
                    break;
                case 4:
                    printTruckById();
                    break;
                case 5:
                    printAllTrucks();
                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    Helper.showError("Invalid choice, please try again.");
                    break;
            }
        }
    }
}
