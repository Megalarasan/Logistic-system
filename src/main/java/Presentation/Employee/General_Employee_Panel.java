package Presentation.Employee;

import Auxiliary.Enums.Domain.Status;
import Domain.Employees.UpcomingShiftsManagerCLS;
import Models.DBModels.Employees.ARole;
import Domain.Employees.AvailabilityManager;
import Models.DBModels.Employees.Employee;
import Models.DBModels.Employees.Shift;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class General_Employee_Panel {




    private static void printMenu_GeneralEmployee() {
        System.out.println("\nWelcome to the General Employee Panel!\n");
        System.out.println("Please choose an action (1 to 5):");
        System.out.println("1. Set Upcoming Shifts Availability");
        System.out.println("2. Enter to your Managing Role");
        System.out.println("3. Show Your Personal Information");
        System.out.println("4. Show all Shifts for the next week");
        System.out.println("5. Going back to Login panel");
    }

    public static void moveToPanel_GeneralEmployee(Employee employee) {
        int choice;
        int choice1;
        Scanner scanner = new Scanner(System.in);

        do {
            printMenu_GeneralEmployee();

            if (scanner.hasNextInt())
                choice = scanner.nextInt();
            else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    if (AvailabilityManager.set_Upcoming_Shifts_Availability(employee) == Status.Success) {
                        System.out.println("Your availability for upcoming shifts has been set successfully.");
                    } else {
                        System.out.println("Failed to set your availability. Please try again later.");
                    }
                    break;
                case 2:
                    boolean Manger = false;
                    boolean HRLead = false;
                    List<ARole> rolesList = employee.getRolesList();
                    for (ARole aRole : rolesList) {
                        if (aRole.getRole().equals("Shift Manager")) {
                            Manger = true;
                        } else if (aRole.getRole().equals("HR Lead")) {
                            HRLead = true;
                        }
                    }
                    if (Manger && HRLead) {
                        boolean ValidInput = false;
                        while(!ValidInput){
                            System.out.println("You have both Manager and HR Lead roles.");
                            System.out.println("Please choose one (1 or 2):");
                            System.out.println("1. Manager");
                            System.out.println("2. HR Lead");

                            if (scanner.hasNextInt())
                                choice1 = scanner.nextInt();
                            else {
                                System.out.println("Invalid input. Please enter a number.");
                                scanner.next(); // Clear the invalid input
                                continue;
                            }

                            if (choice1 == 1) {
                                ValidInput = true;
                                System.out.println("Moving to Manger Panel...");
                                Manager_Panel.moveToPanel_Manager(employee);
                            }
                            else if (choice1 == 2) {
                                ValidInput = true;
                                System.out.println("Moving to HR Lead Panel...");
                                HR_Lead_Panel.moveToPanel_HR(employee);
                            }
                            else {
                                System.out.println("Invalid choice. Please select a number from 1 to 2.");
                            }
                        }
                    } else if (Manger) {
                        System.out.println("Moving to Manger Panel...");
                        Manager_Panel.moveToPanel_Manager(employee);
                    } else if (HRLead) {
                        System.out.println("Moving to HR Lead Panel...");
                        HR_Lead_Panel.moveToPanel_HR(employee);
                    }
                    else {
                        System.out.println("You Dont have Managing role.");
                    }
                    break;
                case 3:
                    String info = employee.toString();
                    System.out.println("Your Personal Information: " + info);
                    break;
                case 4:
                    System.out.println("All Shifts for the next week: ");
                    // First, move any past shifts to the past shifts table
                    UpcomingShiftsManagerCLS.moveShiftsToPast();

                    if (!UpcomingShiftsManagerCLS.getUpcomingShifts().isEmpty()) {
                        LocalDate today = LocalDate.now();
                        LocalDate nextWeek = today.plusDays(7);
                        boolean foundUpcomingShifts = false;

                        for (Shift shift : UpcomingShiftsManagerCLS.getUpcomingShifts()) {
                            LocalDate shiftDate = shift.getShiftDate();
                            // Show shifts that are today or in the future AND within the next 7 days
                            if ((shiftDate.isEqual(today) || shiftDate.isAfter(today)) &&
                                (shiftDate.isBefore(nextWeek) || shiftDate.isEqual(nextWeek))) {
                                System.out.println();
                                System.out.println(shift.toString());
                                foundUpcomingShifts = true;
                            }
                        }

                        if (!foundUpcomingShifts) {
                            System.out.println("There are no upcoming shifts in the next week.");
                        }
                    }
                    else {
                        System.out.println("There are no upcoming shifts.");
                    }
                    break;
                case 5:
                    System.out.println("Going back to Login panel...\n");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");

            }

        } while (true);

    }
}
