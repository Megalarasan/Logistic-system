package Presentation.Employee;

import Auxiliary.Enums.Domain.Status;
import Auxiliary.IdGenerator;
import Domain.Employees.*;
import Domain.transport.BranchesManager;
import Models.DBModels.Employees.*;
import Models.DBModels.transport.Branch;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import Auxiliary.sharedFunctions;

public class Manager_Panel {

    private static void printMenu_Manager() {
        System.out.println("\nWelcome to the Manager Panel!\n");
        System.out.println("Please choose an action (1,2,3,4,5 or 6):");
        System.out.println("1. create New Shift");
        System.out.println("2. Remove Employee from Shift");
        System.out.println("3. Add Employee to Shift");
        System.out.println("4. Promote Employee");
        System.out.println("5. Use cancel card");
        System.out.println("6. Go back to General Employee panel");
    }

    public static void moveToPanel_Manager(Employee employee) {
        Scanner scanner = new Scanner(System.in);
        do {
            printMenu_Manager();
            int choice = sharedFunctions.getValidIntChoice(scanner, 1, 6);

            switch (choice) {
                case 1 -> createNewShift(employee);
                case 2 -> removeEmployeeFromShift(employee);
                case 3 -> addEmployeeToShift();
                case 4 -> promoteEmployee();
                case 5 -> useCancelCard(employee);
                case 6 -> {
                    System.out.println("Going back to General Employee panel");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }while (true);
    }

    private static void useCancelCard(Employee manager) {
        EmployeeManagerCLS.use_cancel_card();
    }


    //case 1 in switch case printMenu_Manager
    public static void createNewShift(Employee manager) {
        Scanner scanner = new Scanner(System.in);
        LocalDate shiftDate = sharedFunctions.parseDate(scanner, "Please enter your start date (dd/MM/yyyy):");

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.SUNDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.THURSDAY); // THURSDAY

        if (shiftDate.isBefore(today)) {
            System.out.println("Shift date cannot be in the past.");
            return;
        }

        String shiftType = sharedFunctions.getShiftTypeFromUser(scanner);

        // Get branch from user
        Branch branch = getShiftBranchFromUser(scanner);
        if (branch == null) {
            System.out.println("No branch selected. Shift creation cancelled.");
            return;
        }

        Map<Employee, String> assignedEmployeesToShift = new HashMap<>();
        Set<Employee> allAvailableEmployees = new HashSet<>();

        for (Employee emp : EmployeeManagerCLS.getAllEmployees()) {
            if (AvailabilityManager.isAvailable(shiftDate,emp)) {
                allAvailableEmployees.add(emp);
            }
        }

        assignEmployeesToShiftRoles(scanner, allAvailableEmployees, assignedEmployeesToShift);

        if (assignedEmployeesToShift.isEmpty()) {
            System.out.println("No employees assigned to the shift. Shift creation cancelled.");
            return;
        }

        if (Shift_Manager.createShift(
                IdGenerator.getNextIdShift(),
                shiftDate,
                shiftType,
                manager,
                assignedEmployeesToShift,
                allAvailableEmployees,
                branch
        ) == Status.Success) {
            System.out.println("Shift created successfully.");
        } else {
            System.out.println("Failed to create shift.");
        }
    }


    // main method to assign employees to shift roles
    // it will iterate over all roles and take input from the user
    // to select employees for each role
    private static void assignEmployeesToShiftRoles(Scanner scanner, Set<Employee> allAvailableEmployees, Map<Employee, String> assignedEmployeesToShift) {
        for (Map.Entry<String, Integer> entry : Shift.getRequiredRoles().entrySet()) {
            String role = entry.getKey();
            int requiredCount = entry.getValue();
            Set<Employee> availableForRole = filterAvailableEmployeesByRole(allAvailableEmployees, role);
            selectEmployeesForRole(scanner, role, requiredCount, availableForRole, assignedEmployeesToShift);
        }
    }

    // iterate over all employees and check if the employee has corresponding role
    // if yes add to the list
    private static Set<Employee> filterAvailableEmployeesByRole(Set<Employee> allEmployees, String role) {
        Set<Employee> result = new HashSet<>();
        for (Employee emp : allEmployees) {
            if (emp.getRolesList().stream().anyMatch(r -> r.getRole().equals(role))) {
                result.add(emp);
            }
        }
        return result;
    }

    // This method will be used to select employees for a specific role
    // It will display the available employees and allow the user to select them
    private static void selectEmployeesForRole(
            Scanner scanner,
            String role,
            int requiredCount,
            Set<Employee> availableEmployees,
            Map<Employee, String> assignedEmployees
    ) {
        int selectedCount = 0;
        System.out.println("\nFor " + role + " needed: " + requiredCount + " employees");

        if (availableEmployees.isEmpty()) {
            System.out.println("No available employees for the role: " + role);
            System.out.println("You should recruit employees for this role.");
            return; // No point in proceeding
        }

        // We'll use a List to enable index-based access for selection
        List<Employee> availableList = new ArrayList<>(availableEmployees);

        while (selectedCount < requiredCount && !availableList.isEmpty()) {
            printAvailableEmployeesWithIndex(availableList);
            System.out.println("0. Skip assigning remaining employees for this role");
            System.out.println("Please select the corresponding number to the employee you wish to add.");
            System.out.println("Example: 1. Employee ID: 12345 - John Doe -> select 1");
            System.out.print("Enter your choice: ");

            int choice = sharedFunctions.getValidIntChoice(scanner, 0, availableList.size());

            if (choice == 0) {
                System.out.println("Skipping remaining selections for role: " + role);
                break;
            }
            // subtract 1 from choice to get the index (starts from 0)
            Employee selectedEmployee = availableList.get(choice - 1);

            if (assignedEmployees.containsKey(selectedEmployee)) {
                System.out.println("Employee already assigned. Choose a different one.");
                continue;
            }

            assignedEmployees.put(selectedEmployee, role);
            availableEmployees.remove(selectedEmployee); // Remove from original set
            availableList.remove(selectedEmployee);      // Remove from list too
            selectedCount++;

            if (selectedCount >= requiredCount) {
                System.out.println("\nYou have selected enough employees for the role: " + role);
            } else {
                System.out.println("\nYou need to select " + (requiredCount - selectedCount) + " more employees for the role: " + role);
            }
        }
    }

    // Use a List parameter to print with index:
    private static void printAvailableEmployeesWithIndex(List<Employee> employees) {
        System.out.println("Available employees:");
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            System.out.println((i + 1) + ". Employee ID: " + emp.getEmployeeID() + " - " + emp.getName());
        }
    }
    private static Branch getShiftBranchFromUser(Scanner scanner) {
        // Display all available branches first
        System.out.println("Available branches:");
        ArrayList<Branch> branches = BranchesManager.getExistingBranches();

        if (branches.isEmpty()) {
            System.out.println("No branches available. Please create a branch first.");
            return null;
        }

        for (int i = 0; i < branches.size(); i++) {
            Branch branch = branches.get(i);
            System.out.println((i + 1) + ". Branch ID: " + branch.getId() + " - Branch Name: " + branch.getSiteName());
        }

        // Ask user to select a branch
        System.out.println("Please enter the number of the branch you want to select:");
        int choice = sharedFunctions.getValidIntChoice(scanner, 1, branches.size());

        // Get the selected branch
        Branch selectedBranch = branches.get(choice - 1);

        if (selectedBranch == null) {
            System.out.println("Error retrieving branch. Please try again.");
            return getShiftBranchFromUser(scanner);
        }

        System.out.println("Selected branch: " + selectedBranch.getSiteName());
        return selectedBranch;
    }

    // This method will print the available employees with their index
    // employees are selected to print based on the role
    // it will be used in the selectEmployeesForRole method
    private static void printAvailableEmployeesWithIndex(Set<Employee> employees) {
        System.out.println("Available employees:");
        int i = 1;
        for (Employee emp : employees) {
            System.out.println(i + ". Employee ID: " + emp.getEmployeeID() + " - " + emp.getName());
            i++;
        }
    }



    //case 2 in switch case printMenu_Manager
    private static void removeEmployeeFromShift(Employee employee) {
        Scanner scanner = new Scanner(System.in);
        String employeeId;
        Employee toRemove;

        try {
            System.out.println("Please enter the ID of the employee you want to remove from the shift:");
            employeeId = scanner.nextLine();
            toRemove = EmployeeManagerCLS.getEmployeeById(employeeId);


            if (toRemove == null) {
                System.out.println("Employee with ID " + employeeId + " not found.");
                return;
            } else if (employee.getEmployeeID().equals(toRemove.getEmployeeID())) {
                System.out.println("you cant remove yourself");
                return;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while searching for the employee.");
            return;
        }

        int shiftsFound = 0;
        String shiftId = null;

        // First, move any past shifts to the past shifts table
        UpcomingShiftsManagerCLS.moveShiftsToPast();

        for (Shift shift : UpcomingShiftsManagerCLS.getUpcomingShifts()) {
            if (shift.getAssignedEmployees().keySet().stream().anyMatch(emp -> emp.getEmployeeID().equals(employeeId))) {
                System.out.println(shift.getShiftID() + " - " + shift.getShiftDate() + " - " + shift.getShiftType());
                shiftsFound++;
                shiftId = shift.getShiftID();
            }
        }

        if (shiftsFound == 0) {
            System.out.println("Employee with ID " + employeeId + " not found in any shifts.");
            return;
        }

        if (shiftsFound == 1) {
            Shift shift = UpcomingShiftsManagerCLS.findShiftById(shiftId);
            if (shift != null) {
                UpcomingShiftsManagerCLS.removeAssignedEmployee(toRemove,shift);
                System.out.println("Employee with ID " + employeeId + " has been removed from the shift.");
            }
        } else {
            System.out.println("Multiple shifts found for employee with ID " + employeeId + ". Please specify the shift ID");
            while (true) {
                System.out.println("Valid shift ID example (uppercase): 'SHIFT-1' \n Enter shift ID:");
                String shiftIdToRemoveFrom = scanner.nextLine();

                Shift shift = UpcomingShiftsManagerCLS.findShiftById(shiftIdToRemoveFrom);
                if (shift != null) {
                    if (Shift_Manager.removeEmployeeFromShift(toRemove, shift) == Status.Success) {
                        System.out.println("Employee with ID " + employeeId + " has been removed from the shift.");
                    } else {
                        System.out.println("Failed to remove employee from the shift.");
                    }
                    break;
                } else {
                    System.out.println("Inavlid input - Please Enter a valid shift ID.");
                }
            }
        }
    }

    //case 3 in switch case printMenu_Manager
    public static void addEmployeeToShift() {
        Scanner scanner = new Scanner(System.in);

        // First, move any past shifts to the past shifts table
        UpcomingShiftsManagerCLS.moveShiftsToPast();

        System.out.println("Enter the Employee ID you want to assign to a shift:");
        String empId = scanner.nextLine().trim();
        Employee employeeToAssign = EmployeeManagerCLS.getEmployeeById(empId);

        if (employeeToAssign == null) {
            System.out.println("Employee with ID " + empId + " does not exist.");
            return;
        }

        System.out.println("Enter the Shift ID to assign the employee to:");
        String shiftId = scanner.nextLine().trim();
        Shift shift = UpcomingShiftsManagerCLS.findShiftById(shiftId);

        if (shift == null) {
            System.out.println("Shift with ID " + shiftId + " does not exist.");
            return;
        }

        // Check if employee is available for the shift date
        if (!AvailabilityManager.isAvailable(shift.getShiftDate(), employeeToAssign)) {
            System.out.println("Employee " + employeeToAssign.getName() + " is not available for this shift date.");
            return;
        }
        if (!shift.getAllAvailableEmployees().contains(employeeToAssign) &&
                shift.getAssignedEmployees().containsKey(employeeToAssign)) {
            System.out.println("Employee " + employeeToAssign.getName() + " is already assigned to this shift.");
            return;
        }

        // Display available roles and ask user to select one
        System.out.println("Employee " + employeeToAssign.getName() + " has the following roles:");
        for (int i = 0; i < employeeToAssign.getRolesList().size(); i++) {
            ARole role = employeeToAssign.getRolesList().get(i);
            System.out.printf(role.getRole() + ", ");
        }
        System.out.println();
        System.out.println();
        System.out.println("Based on the Employee's roles");
        System.out.println("Select the corresponding number to the desired Role to add:");
        sharedFunctions.DisplayAvailableRoles(1);

        int choice = sharedFunctions.getValidIntChoice(scanner, 1, 7);
        scanner.nextLine(); // Clear the buffer

        // Get the selected role
        ARole selectedRole = sharedFunctions.promptForRole(choice);
        String roleName = selectedRole.getRole();

        // Check if employee has the selected role
        boolean hasSelectedRole = false;
        for (ARole role : employeeToAssign.getRolesList()) {
            if (role.getRole().equals(roleName)) {
                hasSelectedRole = true;
                break;
            }
        }

        if (!hasSelectedRole) {
            System.out.println("Employee " + employeeToAssign.getName() + " does not have the role " + roleName);
            return;
        }

        if (Shift_Manager.addEmployeeToShift(employeeToAssign, shift, roleName) == Status.Success) {
            System.out.println("Employee " + employeeToAssign.getName() + " added to shift " + shiftId + " with role " + roleName);
        } else {
            System.out.println("Failed to add employee to the shift.");
        }
    }

    //case 4 in switch case printMenu_Manager
    private static void promoteEmployee() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the Employee ID you want to promote:");
        String empId = scanner.nextLine().trim();
        Employee employeeToPromote = EmployeeManagerCLS.getEmployeeById(empId);

        if (employeeToPromote == null) {
            System.out.println("Employee with ID " + empId + " does not exist.");
            return;
        }
        if (employeeToPromote.getRolesList().stream().anyMatch(r -> r instanceof Shift_Manager  ||r instanceof HR_Lead)) {
            System.out.println("Cannot promote to equal or higher roles.");
            return;
        }

        System.out.println("Choose a role to promote the employee to:");
        sharedFunctions.DisplayAvailableRoles();

        int choice = sharedFunctions.getValidIntChoice(scanner, 1, 6);
        scanner.nextLine(); // Clear the buffer

        ARole roleToAdd;
        String roleName;

        // Use switch case to handle role selection
        roleToAdd = sharedFunctions.promptForRole(choice);
        roleName = roleToAdd.getRole();


        if (Shift_Manager.promote_Employee(employeeToPromote, roleToAdd) == Status.Success){
            System.out.println("Employee " + employeeToPromote.getName() + " promoted to " + roleName);
        } else {
            System.out.println("Employee " + employeeToPromote.getName() + " already has the role " + roleName);
        }
    }
}
