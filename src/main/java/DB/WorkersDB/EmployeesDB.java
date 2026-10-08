package DB.WorkersDB;

import Models.DBModels.Employees.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeesDB {
    private static final List<Employee> mockEmployeeDB = new ArrayList<>();

    public static void addEmployeeToDB(Employee employee) {
        mockEmployeeDB.add(employee);
    }


    public static List<Employee> getMockEmployeeDB() {
        return new ArrayList<>(mockEmployeeDB);  // return copy for safety
    }

    // This method is used to remove an employee by their ID
    public static void removeEmployeeByIdFromDB(String id) {
        mockEmployeeDB.removeIf(e -> e.getEmployeeID().equals(id));
    }

    // This method is used to find an employee by their ID
    public static Employee findEmployeeById(String id) {
        for (Employee employee : mockEmployeeDB) {
            if (employee.getEmployeeID().trim().equals(id.trim())) {
                return employee;
            }
        }
        return null;
    }

    //for future use and testing
    public static void clear() {
        mockEmployeeDB.clear();
    }
}
