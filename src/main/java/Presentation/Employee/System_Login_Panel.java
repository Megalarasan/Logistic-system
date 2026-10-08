package Presentation.Employee;

import Auxiliary.sharedFunctions;
import Domain.Employees.EmployeeManagerCLS;
import Models.DBModels.Employees.Employee;

// Following imports are only for mock data upload-----
import DB.implementations.MockFactory.MockEmployeeData;
import DB.implementations.MockFactory.MockShiftsData;
import Models.DBModels.Employees.Shift;
//-----------------------
import java.util.List;
import java.util.Scanner;

public class System_Login_Panel {

    public static void printLoginPanel() {
        Scanner scanner = new Scanner(System.in);  // Create Scanner object
        int choice =0;

        while (choice !=2) {
            System.out.println("\nWelcome to the Employees Management System!\n");
            System.out.println("You are on Login panel!");

            boolean continueLoop;

            Employee enteredEmployee = null;
            do {
                continueLoop = false;
                System.out.println("\nPlease choose an action (1 or 2):");
                System.out.println("1. Enter to your Employee Role");
                System.out.println("2. Exit System");

                choice = sharedFunctions.getValidIntChoice(scanner, 1, 2);

                switch (choice) {
                    case 1:
                        scanner.nextLine(); // Clear the buffer
                        System.out.println("Please Enter Your ID: ");
                        String employeeID = scanner.nextLine();// Read user input
                        enteredEmployee = EmployeeManagerCLS.getEmployeeById(employeeID);
                        if (enteredEmployee == null) {
                            System.out.println("Invalid Employee ID - There is no employee with the following ID.\nPlease try again.");
                            continueLoop = true;
                        }
                        break;
                    case 2:
                        System.out.println("Exiting the system...");
                        continueLoop = false;
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        continueLoop = true;
                }
            } while (continueLoop);

            General_Employee_Panel.moveToPanel_GeneralEmployee(enteredEmployee);
        }
    }


     public static void EmployeesPresentationHandler() {
                try {
                    // Get a new session to ensure schema is created
                    try (org.hibernate.Session session = DB.util.HibernateUtil.getSessionFactory().openSession()) {
                        // Begin transaction for adding employees
                        session.beginTransaction();

                        // Create and add mock employees
                        List<Employee> employees = MockEmployeeData.createMockEmployees();
                        for (Employee emp : employees) {
                            try {
                                session.merge(emp);
                            } catch (Exception e) {
                                System.out.println("Error adding employee " + emp.getEmployeeID() + ": " + e.getMessage());
                            }
                        }
                        // Commit the transaction
                        session.getTransaction().commit();

                        // Begin new transaction for shifts
                        session.beginTransaction();

                        // Fetch the persisted employees from database to use in shifts
                        List<Employee> persistedEmployees = session.createQuery("FROM Employee", Employee.class).getResultList();

                        // Create and add mock shifts using persisted employees
                        List<Shift> shifts = MockShiftsData.createUpcomingShiftsMockDataWithEmployees(persistedEmployees);

                        for (Shift shift : shifts) {
                            try {
                                // Use merge instead of persist to handle detached entities
                                session.merge(shift);
                            } catch (Exception e) {
                                System.out.println("Error adding shift " + shift.getShiftID() + ": " + e.getMessage());
                            }
                        }
                        // Commit the transaction
                        session.getTransaction().commit();

                    }

                } catch (Exception e) {
                    System.out.println("Error in mock data process: " + e.getMessage());
                    e.printStackTrace();

                }
        }
    }
