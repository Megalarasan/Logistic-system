package Presentation.transport;

import Auxiliary.Enums.Domain.UpdateBranchEnum;
import Domain.transport.BranchesManager;
import Models.DBModels.transport.Branch;
import Auxiliary.Helper;

import java.util.ArrayList;

public class BranchesHandler {

    /**
     * This method is used to add a new branch.
     */
    private static void addBranch() {
        String name = null;
        String address = null;
        String phone = null;
        try {
            Helper.println("Branch creation:");
            do {
                Helper.print("Enter Branch name: ");
                name = Helper.getUserInputString();
            } while (name == null || name.isEmpty());
            do {
                Helper.print("Enter Branch address: ");
                address = Helper.getUserInputString();
            } while (address == null || address.isEmpty());
            do {
                Helper.print("Enter Branch phone: ");
                phone = Helper.getUserInputString();
            } while (phone == null || phone.isEmpty());
        } catch (Exception e) {
            Helper.println("Error: " + e.getMessage());
        }
        if (!BranchesManager.addBranch(name, address, phone)) {
            Helper.showError("Error adding branch");
        }
    }

    /**
     * This method is used to view all existing branches.
     * It retrieves the list of branches from the BranchesManager and prints their details.
     *
     * @return the list of branches if found and printed, otherwise null
     */
    public static ArrayList<Branch> viewAllBranches() {
        try {
            ArrayList<Branch> lst = BranchesManager.getExistingBranches();
            if (lst == null || lst.isEmpty()) {
                Helper.println("No branches found.");
                return null;
            }
            Helper.println("Existing Branches:");
            for (Branch branch : lst) {
                Helper.println("Branch ID: " + branch.getId() + ", Name: " + branch.getSiteName());
            }
            return lst;
        } catch (Exception e) {
            Helper.showError(e.getMessage(), Helper.getStringTraceOfException(e));
        }
        return null;
    }

    /**
     * This method is used to get a branch by its ID.
     *
     * @return the branch object if found, otherwise null
     */
    private static Branch getBranchById() {
        int id;
        do {
            Helper.print("Enter Branch ID: ");
            id = Helper.getUserInputInteger();
        } while (id == Helper.UndefinedUserInput);
        Branch b = BranchesManager.getBranchById(id);
        if (b == null) {
            Helper.println("No branch found with ID: " + id);
            return null;
        }
        return b;
    }

    /**
     * This method is used to print a branch by its ID.
     */
    private static void printBranchById() {
        try {
            Branch b = getBranchById();
            if (b != null) {
                Helper.println(b.toString());
            }
        } catch (Exception e) {
            Helper.showError(e.getMessage(), Helper.getStringTraceOfException(e));
        }
    }

    /**
     * This method is used to update a branch.
     * It prompts the user to select a branch and then choose which attribute to update.
     */
    private static void updateBranch() {
        try {
            Branch b = getBranchById();
            if (b == null) {
                return;
            }
            Helper.println(b.toString());
            UpdateBranchEnum choice;
            do {
                Helper.println("What would you like to update?");
                Helper.println(String.format("%s. %s", UpdateBranchEnum.Name.getValue(), UpdateBranchEnum.Name.name()));
                Helper.println(String.format("%s. %s", UpdateBranchEnum.Address.getValue(), UpdateBranchEnum.Address.name()));
                Helper.println(String.format("%s. %s", UpdateBranchEnum.Phone.getValue(), UpdateBranchEnum.Phone.name()));
                Helper.print("Enter your choice: ");
                choice = UpdateBranchEnum.intToEnum(Helper.getUserInputInteger());
            } while (choice == UpdateBranchEnum.UNDEFINED);
            String newValue;
            do {
                Helper.print(String.format("Enter new value for %s: ", choice.name()));
                newValue = Helper.getUserInputString();
            } while (newValue == null || newValue.isEmpty());

            if (!BranchesManager.updateBranch(b.getId(), choice == UpdateBranchEnum.Name ? newValue : b.getSiteName(),
                    choice == UpdateBranchEnum.Address ? newValue : b.getSiteAddress(),
                    choice == UpdateBranchEnum.Phone ? newValue : b.getSitePhone())) {
                Helper.println(String.format("Error updating branch with id %d", b.getId()));
            }

        } catch (Exception e) {
            Helper.showError(e.getMessage(), Helper.getStringTraceOfException(e));
        }
    }

    /**
     * This method is used to delete a branch.
     * It prompts the user to confirm the deletion before proceeding.
     */
    private static void deleteBranch() {
        try {
            Branch b = getBranchById();
            if (b != null) {
                Helper.println("Branch with ID: " + b.getId() + " and name: " + b.getSiteName() + "\nwill be deleted. Are you sure? (y/n): ");
                String choice = Helper.getUserInputString();
                while (choice == null || choice.isEmpty() ||
                        (!choice.equalsIgnoreCase("y") && !choice.equalsIgnoreCase("n"))) {
                    Helper.print("Invalid choice. Please enter 'y' or 'n': ");
                    choice = Helper.getUserInputString();
                }
                if (choice.equalsIgnoreCase("y")) {
                    if (!BranchesManager.deleteBranch(b.getId())) {
                        Helper.println("Error deleting branch");
                    } else {
                        Helper.println("Branch deleted successfully");
                    }
                } else {
                    Helper.println("Branch deletion cancelled.");
                }
            }
        } catch (Exception e) {
            Helper.showError(e.getMessage(), Helper.getStringTraceOfException(e));
        }
    }

    /**
     * This method is used to handle the branches management menu.
     * It provides options to add, update, delete, view branches, and exit the menu.
     * It runs in a loop until the user chooses to exit.
     */
    public static void handleBranches() {
        boolean exit = false;
        while (!exit) {
            int choice;
            Helper.println("Manage Branches:");
            Helper.println("1. Add Branch");
            Helper.println("2. Update Branch");
            Helper.println("3. Delete Branch");
            Helper.println("4. View Branch by ID");
            Helper.println("5. View Existing Branches");
            Helper.println("6. Back to Main Menu");
            do {
                Helper.print("Please choose an option: ");
                choice = Helper.getUserInputInteger();
            } while (choice == Helper.UndefinedUserInput);

            switch (choice) {
                case 1:
                    addBranch();
                    break;
                case 2:
                    updateBranch();
                    break;
                case 3:
                    deleteBranch();
                    break;
                case 4:
                    printBranchById();
                    break;
                case 5:
                    viewAllBranches();
                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    Helper.println("Invalid choice. Please try again.");
            }
        }
    }
}
