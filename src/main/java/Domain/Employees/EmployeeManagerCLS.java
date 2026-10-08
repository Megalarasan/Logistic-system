package Domain.Employees;


import Auxiliary.Enums.Domain.Status;
import Auxiliary.Helper;
import DB.DBemployeeManager;
import DB.interfaces.employees.IEmployeeController;
import Domain.transport.DriversManager;
import Models.DBModels.Employees.*;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManagerCLS {

    private static final IEmployeeController employeeController = DBemployeeManager.getInstance().getEmployeeController();

    public static Status addEmployeeToSystem(Employee employee) {
        return employeeController.addEmployeeToDB(employee);
    }

    public static boolean doesEmployeeExist(String employeeID) {
        Employee emp = getEmployeeById(employeeID);
        if (emp != null)
            return true;
        return false;
    }

    public static Employee getEmployeeById(String employeeID) {
        Employee employee = employeeController.findEmployeeById(employeeID);
        return employee;
    }

    public static Status deleteEmployeeFromSystem(String employeeID) {
        if (!doesEmployeeExist(employeeID)) {
            Helper.showError(String.format("Cant delete employee with id=%s, it does not exist", employeeID));
            return Status.Failure;
        }

        boolean hasDriverRole = getEmployeeById(employeeID).getRolesList().stream().anyMatch(role -> role instanceof Driver);
        if (hasDriverRole) {
            String driverPersonal_id = getEmployeeById(employeeID).getEmployeeID();
            Models.DBModels.transport.Driver driverToRemove = DriversManager.getDriverByPersonalId(driverPersonal_id);
            if (DriversManager.deleteDriver(driverToRemove.getId())) {
                return employeeController.removeEmployeeFromDB(employeeID);
            }
            return Status.Failure;
        } else {
            return employeeController.removeEmployeeFromDB(employeeID);
        }
    }
    public static Status updateEmployeeInSystem(Employee updatedEmployee) {
        return employeeController.updateEmployee(updatedEmployee);
    }


    public static Status addNewRoleToEmployee(ARole newRole , Employee employee) {
        // Check if employee already has this role
        for (ARole existingRole : employee.getRolesList()) {
            if (existingRole.getRole().equals(newRole.getRole())) {
                return Status.Failure;
            }
        }

        // Create a new list to avoid modifying the original list directly
        List<ARole> updatedRoles = new ArrayList<>(employee.getRolesList());
        updatedRoles.add(newRole);
        employee.setAllRoles(updatedRoles);

        return employeeController.updateEmployee(employee);
    }

    public static List<Employee> getAllEmployees() {{
        ArrayList<Employee> employeesList = new ArrayList<>();
        for (String id : employeeController.getAllIds()) {
            Employee employee = employeeController.findEmployeeById(id);
            if (employee == null) {
                Helper.showError(String.format("Cant get employee with id=%s, it does not exist", id));
            } else {
                employeesList.add(employee);
            }
        }
        return employeesList;
        }
    }

    public static void use_cancel_card() {
        Helper.println("Cancel card used");
    }

}
