package Presentation.transport;

import Auxiliary.Enums.Domain.UpdateTransportAreaEnum;
import Auxiliary.Helper;
import Domain.suppliers.SuppliersManager;
import Models.DBModels.suppliers.Supplier;
import Models.DBModels.transport.Branch;
import Models.DBModels.transport.TransportArea;
import Domain.transport.TransportAreasManager;

import java.util.ArrayList;

public class TransportAreaHandler {

    //temp method to show suppliers list
    private static ArrayList<Supplier> showSuppliersList() {
        ArrayList<Supplier> suppliers = SuppliersManager.getSuppliers();
        Helper.println("Existing Suppliers:");
        if (suppliers != null && !suppliers.isEmpty()) {
            for (Supplier s : suppliers) {
                Helper.println("ID: " + s.getId() + ", Name: " + s.getSiteName());
            }
            return suppliers;
        } else {
            Helper.println("No suppliers available.");
        }
        return null;
    }


    /**
     * This method is used to add a new transport area.
     * It prompts the user to enter the name of the transport area and the IDs of the suppliers and branches.
     * If the input is valid, it creates a new transport area and adds it to the system.
     */
    private static void addTransportArea() {
        String areaName = "";
        ArrayList<Integer> suppliersIds = new ArrayList<>();//get user input!
        ArrayList<Integer> branchesIds = new ArrayList<>();
        try {
            Helper.println("Transport Area creation:");
            do {
                Helper.print("Enter Transport Area name: ");
                areaName = Helper.getUserInputString();
            } while (areaName == null || areaName.isEmpty());
            ArrayList<Branch> b = BranchesHandler.viewAllBranches();
            if (b != null && !b.isEmpty()) {
                while (branchesIds.isEmpty()) {
                    Helper.println("Select branches for the transport area:");
                    branchesIds = Helper.retrieveIdsListFromInput(b);
                }
            }

            ArrayList<Supplier> suppliers = showSuppliersList();
            if (suppliers != null && !suppliers.isEmpty()) {
                while (suppliersIds.isEmpty()) {
                    Helper.println("Select suppliers for the transport area:");
                    suppliersIds = Helper.retrieveIdsListFromInput(suppliers);
                }
            }
        } catch (Exception e) {
            Helper.showError("Error: " + e.getMessage());
        }
        if (TransportAreasManager.addNewTransportArea(areaName, branchesIds, suppliersIds)) {
            Helper.println("Transport area added successfully.");
        } else {
            Helper.println("Failed to add transport area.");
        }
    }


    private static ArrayList<TransportArea> printAllTransportAreas() {
        ArrayList<TransportArea> allAreas = TransportAreasManager.getAllTransportAreas();
        if (allAreas == null || allAreas.isEmpty()) {
            Helper.println("No transport areas available.");
            return null;
        }
        Helper.println("All Transport Areas:");
        for (TransportArea area : allAreas) {
            Helper.println("ID: " + area.getId() + ", Name: " + area.getAreaName());
        }
        return allAreas;
    }

    /**
     * This method is used to print a transport area by its ID.
     * It prompts the user to enter the ID of the transport area they want to view.
     * If the ID is valid, it retrieves and prints the transport area details.
     */
    private static void printTransportAreaById() {
        try {
            Helper.println("Enter the transport area you want to view:");
            int id = Helper.getUserInputInteger();
            TransportArea area = TransportAreasManager.getTransportAreaById(id);
            if (area != null) {
                Helper.println(area.toString());
            } else {
                Helper.println("No transport area found with the given ID.");
            }
        } catch (Exception e) {
            Helper.showError("Error: " + e.getMessage(), e);
        }
    }

    /**
     * This method is used to update a transport area.
     * It prompts the user to enter the ID of the transport area they want to update,
     * and then allows them to choose which attribute to update.
     * Depending on the user's choice, it updates the transport area accordingly.
     */
    private static void updateTransportArea() {
        try {
            Helper.println("Transport Area update:");
            Helper.println("Which transport area do you want to update?");
            ArrayList<TransportArea> areas = printAllTransportAreas();
            if (areas != null && !areas.isEmpty()) {
                Helper.print("Enter the ID: ");
                int id = Helper.getUserInputInteger();
                while (!Helper.isValidId(areas, id)) {
                    Helper.println("Invalid ID. Please enter a valid transport area ID that exists in the system.");
                    id = Helper.getUserInputInteger();
                }
                Helper.println("You have selected transport area with ID: " + id);
                TransportArea area = TransportAreasManager.getTransportAreaById(id);
                if (area != null) {
                    Helper.println(area.toString());
                }
                Helper.println("What would you like to update?");
                for (UpdateTransportAreaEnum e : UpdateTransportAreaEnum.values()) {
                    if (e != UpdateTransportAreaEnum.UNDEFINED) {
                        Helper.println(e.getValue() + ". " + e.getStringLabel());
                    }
                }
                Helper.print("Enter your choice: ");
                int choice = Helper.getUserInputInteger();
                UpdateTransportAreaEnum updateChoice = UpdateTransportAreaEnum.intToEnum(choice);
                while (updateChoice == UpdateTransportAreaEnum.UNDEFINED) {
                    Helper.print("Invalid choice. Please enter a valid option: ");
                    choice = Helper.getUserInputInteger();
                    updateChoice = UpdateTransportAreaEnum.intToEnum(choice);
                }

                switch (updateChoice) {
                    case Update_Name -> {
                        Helper.print("Enter new name: ");
                        String newName = Helper.getUserInputString();
                        if (newName != null && !newName.isEmpty()) {
                            if (TransportAreasManager.updateTransportArea(updateChoice, id, newName, null, null)) {
                                Helper.println("Transport area name updated successfully.");
                            } else {
                                Helper.println("Failed to update transport area name.");
                            }
                        } else {
                            Helper.println("Invalid name. Update failed.");
                        }
                    }
                    case Add_Suppliers -> {
                        Helper.println("Choose suppliers you want to add: ");
                        ArrayList<Supplier> suppliers = showSuppliersList();
                        if (suppliers != null && !suppliers.isEmpty()) {
                            ArrayList<Integer> lst = Helper.retrieveIdsListFromInput(suppliers);
                            if (!lst.isEmpty() && TransportAreasManager.updateTransportArea(updateChoice, id, null, lst, null)) {
                                Helper.println("Suppliers added successfully.");
                            } else {
                                Helper.println("Failed to add suppliers.");
                            }
                        } else {
                            Helper.showError("No suppliers available to add.");
                        }
                    }
                    case Remove_Suppliers -> {
                        Helper.println("Choose suppliers you want to remove: ");
                        ArrayList<Supplier> suppliers = showSuppliersList();
                        if (suppliers != null && !suppliers.isEmpty()) {
                            ArrayList<Integer> lst = Helper.retrieveIdsListFromInput(suppliers);
                            if (!lst.isEmpty() && TransportAreasManager.updateTransportArea(updateChoice, id, null, lst, null)) {
                                Helper.println("Suppliers removed successfully.");
                            } else {
                                Helper.println("Failed to remove suppliers.");
                            }
                        } else {
                            Helper.showError("No suppliers available to remove.");
                        }
                    }
                    case Add_Branches -> {
                        Helper.println("Choose branches you want to add: ");
                        ArrayList<Branch> branches = BranchesHandler.viewAllBranches();
                        if (branches != null && !branches.isEmpty()) {
                            ArrayList<Integer> lst = Helper.retrieveIdsListFromInput(branches);
                            if (!lst.isEmpty() && TransportAreasManager.updateTransportArea(updateChoice, id, null, null, lst)) {
                                Helper.println("Branches added successfully.");
                            } else {
                                Helper.println("Failed to add branches.");
                            }
                        } else {
                            Helper.showError("No branches available to add.");
                        }
                    }
                    case Remove_Branches -> {
                        Helper.println("Choose branches you want to remove: ");
                        ArrayList<Branch> branches = BranchesHandler.viewAllBranches();
                        if (branches != null && !branches.isEmpty()) {
                            ArrayList<Integer> lst = Helper.retrieveIdsListFromInput(branches);
                            if (!lst.isEmpty() && TransportAreasManager.updateTransportArea(updateChoice, id, null, null, lst)) {
                                Helper.println("Branches removed successfully.");
                            } else {
                                Helper.println("Failed to remove branches.");
                            }
                        } else {
                            Helper.showError("No branches available to remove.");
                        }
                    }
                    default -> {
                        Helper.println("Invalid choice. No updates made.");
                    }
                }
            }
        } catch (Exception e) {
            Helper.showError("Error: " + e.getMessage(), e);
        }
    }

    /*
     * This method is used to delete a transport area.
     * It prompts the user to enter the ID of the transport area they want to delete.
     * If the ID is valid, it deletes the transport area and prints a success message.
     */
    private static boolean deleteTransportArea() {
        try {
            Helper.print("Enter the ID of the transport area you want to delete: ");
            int id = Helper.getUserInputInteger();
            boolean result = TransportAreasManager.removeTransportArea(id);
            if (result) {
                Helper.println("Transport area deleted successfully.");
                return true;
            } else {
                Helper.println("Failed to delete transport area.");
            }
        } catch (Exception e) {
            Helper.showError("Error: " + e.getMessage(), e);
        }
        return false;
    }

    /*
     * This method is used to handle the transport area menu.
     * It displays the menu options and prompts the user to select an action.
     * Based on the user's choice, it calls the appropriate method to perform the action.
     */
    public static void HandleTransportAreaMenu() {
        boolean exit = false;
        while (!exit) {
            int choice;
            Helper.println("Transport Management Menu:");
            Helper.println("1. Add Transport Area");
            Helper.println("2. Update Transport Area");
            Helper.println("3. Delete Transport Area");
            Helper.println("4. Print Transport area by ID");
            Helper.println("5. Print All Transport Areas");
            Helper.println("6. Back to Main Menu");

            do {
                Helper.print("Please choose an option: ");
                choice = Helper.getUserInputInteger();
            } while (choice == Helper.UndefinedUserInput);

            switch (choice) {
                case 1:
                    addTransportArea();
                    break;
                case 2:
                    updateTransportArea();
                    break;
                case 3:
                    deleteTransportArea();
                    break;
                case 4:
                    printTransportAreaById();
                    break;
                case 5:
                    printAllTransportAreas();
                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    Helper.showError("Invalid choice. Please try again.");
            }
        }
    }
}
