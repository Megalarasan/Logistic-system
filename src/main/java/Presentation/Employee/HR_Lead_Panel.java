package Presentation.Employee;

import Auxiliary.*;
import Auxiliary.Enums.Domain.Status;
import Auxiliary.Enums.Models.AvailabilityStatus;
import Domain.Employees.*;
import Models.DBModels.Employees.*;
import Auxiliary.sharedFunctions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HR_Lead_Panel {

    private static void printMenu_HR() {
        System.out.println("\nWelcome to the HR Panel!\n");
        System.out.println("Please choose an action (1,2,3,4,5,6 or 7):");
        System.out.println("1. Terminate Employee from System");
        System.out.println("2. Add New Employee to System");
        System.out.println("3. Set Shift Hours");
        System.out.println("4. Update Required Roles to Shift");
        System.out.println("5. Set All Required Shift Roles");
        System.out.println("6. Update Employee's information");
        System.out.println("7. Going back to General Employee panel");
    }

    public static void moveToPanel_HR(Employee HRLead) {
        int choice;
        do {
            printMenu_HR();
            Scanner scanner = new Scanner(System.in);

            if (scanner.hasNextInt())
                choice = scanner.nextInt();
            else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
                continue;
            }
            scanner.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    Status termStatus = terminateEmployeeFromSystemUI(HRLead);
                    System.out.println(termStatus == Status.Success ? "Employee terminated successfully." : "Failed to terminate employee.");
                    break;
                case 2:
                    Status addStatus = addEmployeeToSystemUI();
                    System.out.println(addStatus == Status.Success ? "Employee added successfully." : "Failed to add employee.");
                    break;
                case 3:
                    Status shiftStatus = setShiftHoursUI();
                    System.out.println(shiftStatus == Status.Success ? "Shift hours set successfully." : "Failed to set shift hours.");
                    break;
                case 4:
                    Status updRolesStatus = updateRequiredRolesToShiftUI();
                    System.out.println(updRolesStatus == Status.Success ? "Required roles updated successfully." : "Failed to update required roles.");
                    break;
                case 5:
                    Status setRolesStatus = setRequiredRolesToShiftUI();
                    System.out.println(setRolesStatus == Status.Success ? "Required roles set successfully." : "Failed to set required roles.");
                    break;
                case 6:
                    Status updateEmpStatus = updateEmployeeInformationUI();
                    System.out.println(updateEmpStatus == Status.Success ? "Employee information updated successfully." : "Failed to update employee information.");
                    break;
                case 7:
                    System.out.println("Going back to General Employee panel");
                    return; // Exit the panel
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }

    // --- Each UI action now gathers input and delegates to domain ---

    private static Status terminateEmployeeFromSystemUI(Employee HRLead) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter the ID of the Employee to terminate: ");
        String employeeID = scanner.nextLine();
        if (employeeID.equals(HRLead.getEmployeeID())) {
            System.out.println("You cannot terminate yourself.");
            return Status.Failure;
        }
        return HR_Lead.Terminate_Employee_from_System(employeeID);
    }

    private static Status addEmployeeToSystemUI() {
        Scanner scanner = new Scanner(System.in);
        String employeeID = promptForEmployeeID(scanner);
        String employeeName = promptForEmployeeName(scanner);
        double employeeSalary = promptForSalary(scanner);

        int choice;
        do {
            System.out.println("Please choose Employee's role number:\n" +
                    "1. Manager\n2. HR Lead\n3. Cashier\n4. Cleaner\n5. Stocker\n6. Security");
            choice = sharedFunctions.getValidIntChoice(scanner, 1, 6);
        } while (choice < 1 || choice > 6);

        ARole role = sharedFunctions.promptForRole(choice);
        List<ARole> rolesList = new ArrayList<>();
        rolesList.add(role);

        BankAccount employeeAccount = promptForBankAccount(scanner);
        Contract employeeContract = promptForContract(scanner);
        LocalDate recruitment_Date = LocalDate.now();

        if (employeeAccount == null || employeeContract == null) {
            return Status.Failure;
        }

        return HR_Lead.Add_Employee_to_System(employeeName, employeeSalary, employeeID, recruitment_Date, rolesList, employeeAccount, employeeContract);
    }

    private static Status setShiftHoursUI() {
        Scanner scanner = new Scanner(System.in);
        String shiftType = sharedFunctions.getShiftTypeFromUser(scanner);
        System.out.println("Start Time: ");
        LocalTime startTime = sharedFunctions.parseTimeFromUser(scanner);
        System.out.println("End Time: ");
        LocalTime endTime = sharedFunctions.parseTimeFromUser(scanner);
        return HR_Lead.Set_Shift_Hours(shiftType, startTime, endTime);
    }

    private static Status setRequiredRolesToShiftUI() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("This will clear ALL existing required roles and set new roles as specified.");

        // Clear all existing required roles first
        HR_Lead.clearAllRequiredRoles();
        System.out.println("All previous required roles have been cleared.");

        boolean continueLoop = true;
        boolean firstRole = true;

        do {
            if (firstRole) {
                System.out.println("Please choose the first role you want to set for shifts:");
                firstRole = false;
            } else {
                System.out.println("Please choose the next role you want to add to shifts:");
            }

            sharedFunctions.DisplayAvailableRoles(1);
            int choice = sharedFunctions.getValidIntChoice(scanner, 1, 7);

            String role = sharedFunctions.promptForRole(choice).getRole();
            scanner.nextLine();
            int amount = promptForPositiveInt(scanner, "Please Enter the wanted amount of this role: ");

            if (amount == 0) {
                System.out.println("You cannot set the amount to 0 for this role. Skipping this role.");
            } else {
                // Add the specified role
                HR_Lead.Update_Required_Role_to_Shift(1, amount, role);
                System.out.println("Added required role: " + role + " with amount: " + amount);
            }

            // Ask if user wants to add more roles
            String answer;
            do {
                System.out.println("Do you want to add another role? (yes/no)");
                answer = scanner.nextLine().trim().toLowerCase();
                if (answer.equals("yes")) {
                    continueLoop = true;
                } else if (answer.equals("no")) {
                    continueLoop = false;
                } else {
                    System.out.println("Invalid answer. Please enter 'yes' or 'no'.");
                }
            } while (!answer.equals("yes") && !answer.equals("no"));

        } while (continueLoop);

        System.out.println("Finished setting required roles for shifts.");
        return Status.Success;
    }

    private static Status updateRequiredRolesToShiftUI() {
        Scanner scanner = new Scanner(System.in);
        int action;
        do {
            System.out.println("which action do you want to do? (1 OR 2)\n" +
                    "1. Add new role OR Update the amount of a role\n*You can increase or decrease the number of employees in the role.*\n" +
                    "2. Remove existing role");
            action = sharedFunctions.getValidIntChoice(scanner, 1, 2);
        } while (action != 1 && action != 2);

        sharedFunctions.DisplayAvailableRoles(1);
        int choice = sharedFunctions.getValidIntChoice(scanner, 1, 7);
        String role = sharedFunctions.promptForRole(choice).getRole();

        scanner.nextLine();
        int amount = (action == 1) ? promptForPositiveInt(scanner, "Please Enter the wanted amount of this role: ") : 0;
        HR_Lead.Update_Required_Role_to_Shift(action, amount, role);

        return Status.Success;
    }

    private static Status updateEmployeeInformationUI() {
        Scanner scanner = new Scanner(System.in);
        boolean found;
        String employeeID;
        do{
            System.out.println("Please enter Employee's ID:");

            employeeID = scanner.nextLine().trim();
            if (!EmployeeManagerCLS.doesEmployeeExist(employeeID)) {
                System.out.println("Employee not found. Please check the ID and try again.");
                found = false;
            }
            else
                found = true;
        } while (!found);

        int choice;
        do {
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Salary");
            System.out.println("2. Add Role");
            System.out.println("3. Availability");
            System.out.println("4. Go back");
            choice = sharedFunctions.getValidIntChoice(scanner, 1, 4);

            switch (choice) {
                case 1: // Update salary
                    double newSalary = promptForSalary(scanner);
                    Status status1 = HR_Lead.Update_Employee_Information(employeeID, newSalary, null, null, null, null);
                    if (status1 == Status.Success) {
                        System.out.println("Salary updated successfully.");
                    } else {
                        System.out.println("Failed to update salary.");
                    }
                    return status1;

                case 2: // Add role
                    sharedFunctions.DisplayAvailableRoles();
                    int roleChoice = sharedFunctions.getValidIntChoice(scanner, 1, 6);
                    ARole newRole = sharedFunctions.promptForRole(roleChoice);
                    Status status2 = HR_Lead.Update_Employee_Information(employeeID, null, newRole, null, null, null);
                    if (status2 == Status.Success) {
                        System.out.println("Role added successfully.");
                    } else if (status2 == Status.EmployeeNotFound) {
                        System.out.println("Failed to add role. Employee not found.");
                    } else {
                        System.out.println("Driver role can only be added during recruitment.");
                    }
                    return status2;

                case 3: // Update availability
                    Availability newAvailability = promptForAvailability(scanner);
                    Status status3 = HR_Lead.Update_Employee_Information(employeeID, null, null, null, newAvailability, null);
                    if (status3 == Status.Success) {
                        System.out.println("Availability updated successfully.");
                    } else {
                        System.out.println("Failed to update availability.");
                    }
                    return status3;

                case 4:
                    System.out.println("No changes made.");
                    return Status.Failure;
            }
        } while (true);
    }

    // --- Input helper methods (unchanged) ---

    private static String promptForEmployeeID(Scanner scanner) {
        String employeeID;
        do {
            System.out.println("Please enter Employee's ID:");
            employeeID = scanner.nextLine().trim();

            if (employeeID.isEmpty()) {
                System.out.println("Employee ID cannot be empty. Please try again.");
                continue;
            }
            if (EmployeeManagerCLS.doesEmployeeExist(employeeID)) {
                System.out.println("Employee ID already exists. Please enter a different ID.");
                continue;
            }
            break; // Input is valid
        } while (true);

        return employeeID;
    }

    private static String promptForEmployeeName(Scanner scanner) {
        System.out.println("Please Enter Employee's name: ");
        String name;
        while (true) {
            name = scanner.next().trim();
            if (name.isEmpty()) {
                System.out.println("Invalid input. Name cannot be empty. Please try again.");
            } else if (!name.matches("[a-zA-Z]+")) {
                System.out.println("Invalid input. Name must contain only alphabetic characters. Please try again.");
            } else {
                break;
            }
        }
        return name;
    }


    private static double promptForSalary(Scanner scanner) {
        double salary;
        scanner.nextLine();
        while (true) {
            System.out.print("Please enter Employee's salary: ");
            String input = scanner.nextLine().trim();

            try {
                salary = Double.parseDouble(input);

                if (salary <= 0) {
                    System.out.println("Salary must be a positive number greater than zero. Please try again.");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value (e.g., 35.5).");
            }
        }
        return salary;
    }

    private static BankAccount promptForBankAccount(Scanner scanner) {
        String bankName, accountNumber, branch;

        System.out.println("Please enter Employee's bank information:");
        scanner.nextLine();
        // Bank name
        while (true) {
            System.out.print("Bank name: ");
            bankName = scanner.nextLine().trim();
            if (bankName.isEmpty()) {
                System.out.println("Bank name cannot be empty. Please try again.");
            } else {
                break;
            }
        }

        // Account number
        while (true) {
            System.out.print("Bank account number: ");
            accountNumber = scanner.nextLine().trim();
            if (accountNumber.isEmpty()) {
                System.out.println("Account number cannot be empty. Please try again.");
                continue;
            }
            if (!accountNumber.matches("\\d+")) {
                System.out.println("Account number must be numeric. Please try again.");
                continue;
            }
            break;
        }

        // Branch number
        while (true) {
            System.out.print("Bank branch number: ");
            branch = scanner.nextLine().trim();
            if (branch.isEmpty()) {
                System.out.println("Branch number cannot be empty. Please try again.");
                continue;
            }
            if (!branch.matches("\\d+")) {
                System.out.println("Branch number must be numeric. Please try again.");
                continue;
            }
            break;
        }

        return new BankAccount(bankName, accountNumber, branch);
    }

    private static Contract promptForContract(Scanner scanner) {
        System.out.println("Please enter Employee's contract information:");
        int pension;
        while (true) {
            pension = promptForPositiveInt(scanner, "Pension Percentage (0–100): ");
            if (pension < 0 || pension > 100) {
                System.out.println("Pension percentage must be between 0 and 100.");
            } else {
                break;
            }
        }

        int sickDays = promptForPositiveInt(scanner, "Sick days number: ");
        int vacation = promptForPositiveInt(scanner, "Vacation days number: ");
        int minHours = promptForPositiveInt(scanner, "Min monthly hours: ");

        return new Contract(IdGenerator.getNextIdContract(), pension, sickDays, vacation, minHours);
    }

    private static int promptForPositiveInt(Scanner scanner, String message) {
        int value;
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                value = Integer.parseInt(input);
                if (value < 0) {
                    System.out.println("Value must be 0 or more. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
        return value;
    }

    private static Availability promptForAvailability(Scanner scanner) {
        System.out.println("Please set availability for each day of the week:");
        System.out.println("Options: 1 = Day, 2 = Night, 3 = Both, 4 = Unavailable");
        AvailabilityStatus sunday = promptForDayAvailability(scanner, "Sunday");
        AvailabilityStatus monday = promptForDayAvailability(scanner, "Monday");
        AvailabilityStatus tuesday = promptForDayAvailability(scanner, "Tuesday");
        AvailabilityStatus wednesday = promptForDayAvailability(scanner, "Wednesday");
        AvailabilityStatus thursday = promptForDayAvailability(scanner, "Thursday");
        return new Availability(sunday, monday, tuesday, wednesday, thursday);
    }

    private static AvailabilityStatus promptForDayAvailability(Scanner scanner, String day) {
        int choice;
        do {
            System.out.println("Enter availability for " + day + " (1-4):");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear invalid input
                choice = 0;
                continue;
            }
            switch (choice) {
                case 1:
                    return AvailabilityStatus.Day;
                case 2:
                    return AvailabilityStatus.Night;
                case 3:
                    return AvailabilityStatus.Both;
                case 4:
                    return AvailabilityStatus.Unavailable;
                default:
                    System.out.println("Invalid choice. Please select a number from 1 to 4.");
            }
        } while (true);
    }

    public static Status Add_Driver_to_System(String firstName, String personalId) {
        Scanner scanner = new Scanner(System.in);
        double employeeSalary = promptForSalary(scanner);


        ARole role = sharedFunctions.promptForRole(7);
        List<ARole> rolesList = new ArrayList<>();
        rolesList.add(role);

        BankAccount employeeAccount = promptForBankAccount(scanner);
        Contract employeeContract = promptForContract(scanner);
        LocalDate recruitment_Date = LocalDate.now();

        if (employeeAccount == null || employeeContract == null) {
            return Status.Failure;
        }
        return HR_Lead.Add_Employee_to_System(firstName, employeeSalary, personalId, recruitment_Date, rolesList, employeeAccount, employeeContract);
    }
}