package Domain.Employees;

import Models.DBModels.BaseModel;
import Models.DBModels.Employees.ARole;
import Models.DBModels.Employees.Employee;
import Models.DBModels.Employees.Shift;
import Auxiliary.Enums.Domain.Status;
import Models.DBModels.transport.Branch;
import org.hibernate.Session;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@DiscriminatorValue("SHIFT_MANAGER")
public class Shift_Manager extends ARole {
    public Shift_Manager() {
        super();
    }

    public Shift_Manager(int id) {
        super(id, Shift_Manager.class.getName());
        this.setRoleName("Shift Manager");
    }

    public static Status createShift(int id, LocalDate shiftDate, String shiftType,
                                     Employee shiftManager, Map<Employee, String> assignedEmployees,
                                     Set<Employee> allAvailableEmployees, Branch branch){
        Shift newShift = new Shift(id, shiftDate, shiftType, shiftManager, assignedEmployees, allAvailableEmployees, branch);
        UpcomingShiftsManagerCLS.addUpcomingShift(newShift);
        return Status.Success;
    }

    public static Status removeEmployeeFromShift(Employee employeeToRemove , Shift shift) {
        UpcomingShiftsManagerCLS.removeAssignedEmployee(employeeToRemove, shift);
        return Status.Success;
    }


    public static Status addEmployeeToShift(Employee employeeToAdd, Shift shift, String role) {
        UpcomingShiftsManagerCLS.addAssignedEmployeeToShift(employeeToAdd, role, shift);
        return Status.Success;
    }


    public static Status promote_Employee(Employee employeeToPromote, ARole newRole) {
        // Check if the employee already has the new role
        for (ARole role : employeeToPromote.getRolesList()) {
            if (role.getRole().equals(newRole.getRole())) {
                System.out.println("Employee already has this role.");
                return Status.Failure;
            }
        }
        return EmployeeManagerCLS.addNewRoleToEmployee(newRole,employeeToPromote);
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        return false;
    }
}
