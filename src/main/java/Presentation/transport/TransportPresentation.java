package Presentation.transport;

import Auxiliary.Helper;

public class TransportPresentation {

    /**
     * Prints the instructions for the user.
     * This method displays the available options to the user.
     */
    private static void printInstructions() {
        Helper.println("Welcome to the Transport Management System!");
        Helper.println("Please choose an option:");
        Helper.println("1. Manage Transport Areas");
        Helper.println("2. Manage Transports");
        Helper.println("3. Manage Trucks");
        Helper.println("4. Manage Drivers");
        Helper.println("5. Manage Branches");
        Helper.println("6. Exit");
    }

    /**
     * Handles user input and calls the appropriate method based on the choice.
     *
     * @param choice the user's choice
     * @return true if the user wants to continue, false if they want to exit
     */
    private static boolean handleUserInput(int choice) {
        switch (choice) {
            case 1:
                TransportAreaHandler.HandleTransportAreaMenu();
                break;
            case 2:
                TransportHandler.handleTransports();
                break;
            case 3:
                TrucksHandler.HandleTrucks();
                break;
            case 4:
                DriversHandler.handleDrivers();
                break;
            case 5:
                BranchesHandler.handleBranches();
                break;
            case 6:
                Helper.println("Exiting the system. Goodbye!");
                return false;
            default:
                Helper.println("Invalid choice. Please try again.");
                break;
        }
        return true;
    }


    /**
     * Main method to handle the transport presentation layer.
     * This method runs in a loop until the user chooses to exit.
     */
    public static void TransportPresentationHandler() {

        boolean exit = false;
        while (!exit) {
            printInstructions();
            Helper.print("Enter your choice: ");
            try {
                int choice = Helper.getUserInputInteger();
                exit = !handleUserInput(choice);
            } catch (Exception e) {
                Helper.println("An error occurred: " + e.getMessage());
            }
        }
    }
}
