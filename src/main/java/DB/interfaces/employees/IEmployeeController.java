package DB.interfaces.employees;

import Auxiliary.Enums.Domain.Status;
import DB.interfaces.transport.IController;
import Models.DBModels.Employees.Employee;

public interface IEmployeeController extends IEmpController<Employee> {
    public Status addEmployeeToDB(Employee employee);
    public Status updateEmployee(Employee updatedEmployee);
    public Status removeEmployeeFromDB(String id);
    public Employee findEmployeeById(String id);
}
