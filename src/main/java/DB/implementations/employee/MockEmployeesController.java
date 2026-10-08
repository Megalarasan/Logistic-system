package DB.implementations.employee;

import Auxiliary.Enums.Domain.Status;
import DB.interfaces.employees.IEmployeeController;
import Models.DBModels.Employees.Employee;

import java.util.*;

public class MockEmployeesController implements IEmployeeController {
    private static MockEmployeesController instance;

    private final Map<String, Employee> employees = new HashMap<>();

    // Singleton pattern
    public static MockEmployeesController getInstance() {
        if (instance == null) {
            instance = new MockEmployeesController();
        }
        return instance;
    }

    private MockEmployeesController() {
    }

    @Override
    public Status addEmployeeToDB(Employee employee) {
        if (employee == null || employee.getEmployeeID() == null || employees.containsKey(employee.getEmployeeID()))
            return Status.Failure;
        employees.put(employee.getEmployeeID(), employee);
        return Status.Success;
    }

    @Override
    public Status updateEmployee(Employee updatedEmployee) {
        if (updatedEmployee == null || updatedEmployee.getEmployeeID() == null ||
                !employees.containsKey(updatedEmployee.getEmployeeID())) {
            return Status.Failure;
        }
        employees.put(updatedEmployee.getEmployeeID(), updatedEmployee);
        return Status.Success;
    }

    @Override
    public Status removeEmployeeFromDB(String id) {
        if (id == null || !employees.containsKey(id)) return Status.Failure;
        employees.remove(id);
        return Status.Success;
    }

    @Override
    public Employee findEmployeeById(String id) {
        if (id == null) return null;
        return employees.get(id);
    }

    @Override
    public List<String> getAllIds() {
        return new ArrayList<>(employees.keySet());
    }

    // Optionally: helper to load mock data
    public void loadMockEmployees(List<Employee> mockList) {
        for (Employee e : mockList) {
            employees.put(e.getEmployeeID(), e);
        }
    }
}