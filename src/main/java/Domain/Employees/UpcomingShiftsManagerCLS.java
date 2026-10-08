package Domain.Employees;

import Auxiliary.Enums.Models.AvailabilityStatus;
import DB.DBemployeeManager;
import DB.interfaces.employees.IUpcomingShiftsController;
import Models.DBModels.Employees.Availability;
import Models.DBModels.Employees.Employee;
import Models.DBModels.Employees.Shift;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UpcomingShiftsManagerCLS {

    private static final IUpcomingShiftsController upcomingShiftsController = DBemployeeManager.getInstance().getUpcomingShiftsController();


    public static void addUpcomingShift(Shift shift) {
        upcomingShiftsController.addUpcomingShift(shift);
    }

    public static List<Shift> getUpcomingShifts() {
        return upcomingShiftsController.getUpcomingShifts(); // return copy for safety
    }

    public static void removeUpcomingShift(Shift shift) {
        upcomingShiftsController.removeUpcomingShift(shift);
    }

    public boolean updateShift(Shift updatedShift){
        return upcomingShiftsController.updateShiftInDB(updatedShift);
    }

    public static Shift findShiftById(String shiftId) {
        for (Shift shift : upcomingShiftsController.getUpcomingShifts()) {
            if (shift.getShiftID().equals(shiftId)) {
                return shift;
            }
        }
        return null;
    }

    public static void moveShiftsToPast() {
        upcomingShiftsController.moveShiftsToPast();
    }

    public static void addRequiredRoles(String role, int count) {
        Shift.REQUIRED_ROLES_FOR_SHIFT.put(role, count);
    }

    public static void removeRequiredRole(String role) {
        Shift.REQUIRED_ROLES_FOR_SHIFT.remove(role);
    }

    public static void clearAllRequiredRoles() {
        Shift.REQUIRED_ROLES_FOR_SHIFT.clear();
    }

    public static void addAssignedEmployeeToShift(Employee employee, String role, Shift shift) {
        shift.getAssignedEmployees().put(employee, role);
        shift.getAllAvailableEmployees().remove(employee);
        upcomingShiftsController.updateShiftInDB(shift);
    }


    public static void removeAssignedEmployee(Employee employee, Shift shift) {
        shift.getAssignedEmployees().remove(employee);
        if ( shift.getAllAvailableEmployees().contains(employee)) {
            System.out.println("Employee is already available.");
        } else {
            shift.getAllAvailableEmployees().add(employee);
        }
        upcomingShiftsController.updateShiftInDB(shift);
    }

    public static boolean isEmployeeAvailableForShift(Employee employee, LocalDate shiftDate, String shiftType) {
        if (employee == null) {
            return false;
        }

        Availability availability = employee.getAvailability();
        if (availability == null) {
            return false;
        }

        // First check if the employee is available on this date
        if (!AvailabilityManager.isAvailable(shiftDate, employee)) {
            return false;
        }

        // Then check if the employee is available for this shift type
        AvailabilityStatus status;
        switch (shiftDate.getDayOfWeek()) {
            case SUNDAY -> status = availability.getSunday();
            case MONDAY -> status = availability.getMonday();
            case TUESDAY -> status = availability.getTuesday();
            case WEDNESDAY -> status = availability.getWednesday();
            case THURSDAY -> status = availability.getThursday();
            default -> status = AvailabilityStatus.Unavailable;
        }

        // Check if the employee's availability status matches the shift type
        return switch (status) {
            case Day -> shiftType.equals("Day");
            case Night -> shiftType.equals("Night");
            case Both -> true; // Available for both day and night shifts
            default -> false; // Unavailable or Skip
        };
    }
// for available Roles search in Auxilary package - > sharedFunctions -> DisplayAyavailableRoles
    public static boolean isRoleInShift(Shift shift, String role) {
        if (shift == null || role == null) return false;
        Map<Employee, String> assignedEmployees = shift.getAssignedEmployees();
        for (Employee emp : assignedEmployees.keySet()) {
            if (emp.getRolesList().stream().anyMatch(r -> r.getRole().equals(role))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a shift by its date.
     *
     * @param date - the date of the shift to be retrieved
     * @return - the Shift object with the specified date, or null if not found
     */
    public static Shift getShiftByDate(LocalDate date) {
        for (Shift shift : upcomingShiftsController.getUpcomingShifts()) {
            if (shift.getShiftDate().equals(date)) {
                return shift;
            }
        }
        return null;
    }
}
