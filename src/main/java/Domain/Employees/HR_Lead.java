package Domain.Employees;

import Auxiliary.Enums.Models.AvailabilityStatus;
import Auxiliary.IdGenerator;
import Models.DBModels.BaseModel;
import Models.DBModels.Employees.*;
import Auxiliary.Enums.Domain.Status;
import DB.interfaces.employees.IEmployeeController;
import DB.implementations.sqlite.employees.SqliteEmployeesController;
import org.hibernate.Session;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@DiscriminatorValue("HR_LEAD")
public class HR_Lead extends ARole {
    public HR_Lead() {
        super();
    }

    public HR_Lead(int id) {
        super(id, HR_Lead.class.getName());
        this.setRoleName("HR Lead");
    }

    public static Status Terminate_Employee_from_System(String employeeID) {
        return EmployeeManagerCLS.deleteEmployeeFromSystem(employeeID);
    }

    public static Status Add_Employee_to_System(String employeeName, double employeeSalary, String employeeID, LocalDate recruitment_Date, List<ARole> rolesList, BankAccount employeeAccount, Contract employeeContract) {
        Availability employeeAvailability = new Availability(
                AvailabilityStatus.Unavailable,
                AvailabilityStatus.Unavailable,
                AvailabilityStatus.Unavailable,
                AvailabilityStatus.Unavailable,
                AvailabilityStatus.Unavailable);

        Employee employee = new Employee(
                IdGenerator.getNextIdEmployee(),
                employeeName,
                employeeSalary,
                employeeID,
                recruitment_Date,
                rolesList,
                employeeAccount,
                employeeContract,
                employeeAvailability
        );

        return EmployeeManagerCLS.addEmployeeToSystem(employee);
    }

    public static Status Set_Shift_Hours(String shiftType, LocalTime startHour, LocalTime endHour) {
        if (shiftType.equals("Day")) {
            Shift.setStartHourDay(startHour);
            Shift.setEndHourDay(endHour);
        } else {
            Shift.setStartHourNight(startHour);
            Shift.setEndHourNight(endHour);
        }
        return Status.Success;
    }

    public static void Update_Required_Role_to_Shift(int action, int roleAmount, String roleName) {
        if (action == 1) {
            UpcomingShiftsManagerCLS.addRequiredRoles(roleName, roleAmount);
        } else if (action == 2) {
            UpcomingShiftsManagerCLS.removeRequiredRole(roleName);
        }
    }

    public static void clearAllRequiredRoles() {
        UpcomingShiftsManagerCLS.clearAllRequiredRoles();
    }

    /**
     * Update any subset of an employee's information, as directed by UI or business logic.
     * @param employeeID The employee to update
     * @param newSalary If not null, sets salary
     * @param newRole If not null, adds this role (does not remove any existing roles)
     * @param newBankInfo If not null, replaces bank info
     * @param newAvailability If not null, replaces availability
     * @param newContract If not null, replaces contract
     * @return Status.Success if update succeeded, error otherwise.
     */
    public static Status Update_Employee_Information(
            String employeeID,
            Double newSalary,        // Nullable
            ARole newRole,           // Nullable
            BankAccount newBankInfo, // Nullable
            Availability newAvailability, // Nullable
            Contract newContract     // Nullable
    ) {
        // --- Ideally inject the controller; for now, use singleton (OK for this scale) ---
        IEmployeeController controller = SqliteEmployeesController.getInstance();

        // 1. Fetch the current employee
        Employee employee = controller.findEmployeeById(employeeID);
        if (employee == null) {
            return Status.EmployeeNotFound;
        }
        boolean updated = false;

        // 2. Apply all changes to the in-memory object
        if (newSalary != null) {
            employee.setSalary(newSalary);
            updated = true;
        }
        if (newRole != null) {
            boolean hasDriverRole = employee.getRolesList().stream().anyMatch(role -> role instanceof Driver);
            if (hasDriverRole) {return Status.Failure;} // Cannot add a new role if the employee is already a Driver
            EmployeeManagerCLS.addNewRoleToEmployee(newRole, employee);
            updated = true;
        }
        if (newAvailability != null) {
            employee.setAvailability(newAvailability);
            updated = true;
        }

        if (!updated) return Status.Failure;

        // 3. Persist the changes
        return controller.updateEmployee(employee);
    }


    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        return false;
    }
}