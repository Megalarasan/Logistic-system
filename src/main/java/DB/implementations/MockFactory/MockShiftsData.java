package DB.implementations.MockFactory;

import Auxiliary.IdGenerator;
import DB.util.HibernateUtil;
import Domain.Employees.AvailabilityManager;
import Domain.Employees.UpcomingShiftsManagerCLS;
import Domain.transport.BranchesManager;
import Models.DBModels.Employees.Employee;
import Models.DBModels.Employees.Shift;
import Models.DBModels.transport.Branch;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.time.LocalDate;
import java.util.*;


public class MockShiftsData {

    public static List<Shift> createUpcomingShiftsMockData() {
        List<Shift> shifts = new ArrayList<>();
        List<Employee> employees = MockEmployeeData.createMockEmployees();
        Map<String, Employee> employeeMap = new HashMap<>();
        for (Employee e : employees) {
            employeeMap.put(e.getEmployeeID(), e);
        }
        return createShiftsWithEmployeeMap(employeeMap);
    }

    public static List<Shift> createUpcomingShiftsMockDataWithEmployees(List<Employee> persistedEmployees) {
        Map<String, Employee> employeeMap = new HashMap<>();
        for (Employee e : persistedEmployees) {
            employeeMap.put(e.getEmployeeID(), e);
        }
        return createShiftsWithEmployeeMap(employeeMap);
    }

    private static List<Shift> createShiftsWithEmployeeMap(Map<String, Employee> employeeMap) {
        List<Shift> shifts = new ArrayList<>();

        List<Branch> branches = BranchesManager.getExistingBranches();
        if (branches.isEmpty()) {
            System.err.println("No branches found after creation. Cannot proceed with mock data creation.");
            return shifts;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(branches.size());

        // --- PAST SHIFT: 3 years ago ---
        {
            Set<Employee> available = new HashSet<>();
            Map<Employee, String> assigned = new HashMap<>();
            LocalDate date = LocalDate.now().minusYears(3).withMonth(5).withDayOfMonth(15);
            String type = "Day";

            Employee emp001 = employeeMap.get("EMP001");
            Employee emp002 = employeeMap.get("EMP002");
            if (emp001 != null) available.add(emp001);
            if (emp002 != null) available.add(emp002);

            assigned.put(emp001, getFirstRoleOrUnknown(emp001));
            assigned.put(emp002, getFirstRoleOrUnknown(emp002));
            available.remove(emp001);
            available.remove(emp002);

            shifts.add(new Shift(0, date, type, emp001, assigned, available, branches.get(randomIndex)));
        }

        // --- PAST SHIFT: 8 years ago ---
        {
            Set<Employee> available = new HashSet<>();
            Map<Employee, String> assigned = new HashMap<>();
            LocalDate date = LocalDate.now().minusYears(8).withMonth(8).withDayOfMonth(12);
            String type = "Night";

            Employee emp003 = employeeMap.get("EMP003");
            Employee emp004 = employeeMap.get("EMP004");
            if (emp003 != null) available.add(emp003);
            if (emp004 != null) available.add(emp004);

            assigned.put(emp003, getFirstRoleOrUnknown(emp003));
            assigned.put(emp004, getFirstRoleOrUnknown(emp004));
            available.remove(emp003);
            available.remove(emp004);
            randomIndex = ThreadLocalRandom.current().nextInt(branches.size());
            shifts.add(new Shift(
                    0, date, type, emp003, assigned, available, branches.get(randomIndex)
            ));
        }

        // --- FUTURE SHIFT 1: Tomorrow (matches plannedStartDateTime) ---
        {
            Set<Employee> available = new HashSet<>();
            Map<Employee, String> assigned = new HashMap<>();
            LocalDate date = LocalDate.now().plusDays(3); // matches plannedStartDateTime
            date = Auxiliary.Helper.getValidDateForMock(date);
            String type = "Day";

            Employee emp002 = employeeMap.get("EMP002");
            Employee emp003 = employeeMap.get("EMP003");
            Employee emp005 = employeeMap.get("EMP005");
            Employee emp007 = employeeMap.get("EMP007"); // John (Driver) - ensures this shift has a Driver

            if (isEmployeeAvailableForShift(emp002, date, type)) available.add(emp002);
            if (isEmployeeAvailableForShift(emp003, date, type)) available.add(emp003);
            if (isEmployeeAvailableForShift(emp005, date, type)) available.add(emp005);
            if (isEmployeeAvailableForShift(emp007, date, type)) available.add(emp007);

            String role002 = getFirstRoleOrUnknown(emp002);
            String role005 = getFirstRoleOrUnknown(emp005);
            String role007 = getFirstRoleOrUnknown(emp007);

            if (isEmployeeAvailableForShift(emp002, date, type)) {
                assigned.put(emp002, role002);
                available.remove(emp002);
            }

            if (isEmployeeAvailableForShift(emp005, date, type)) {
                assigned.put(emp005, role005);
                available.remove(emp005);
            }

            if (isEmployeeAvailableForShift(emp007, date, type)) {
                assigned.put(emp007, role007);
                available.remove(emp007);
            }
            randomIndex = ThreadLocalRandom.current().nextInt(branches.size());
            shifts.add(new Shift(0, date, type, emp002, assigned, available, branches.get(randomIndex)));
        }

        // --- FUTURE SHIFT 2: Day after tomorrow with new employees ---
        {
            Set<Employee> available = new HashSet<>();
            Map<Employee, String> assigned = new HashMap<>();
            LocalDate date = LocalDate.now().plusDays(2);
            date = Auxiliary.Helper.getValidDateForMock(date);
            String type = "Night";

            Employee emp002 = employeeMap.get("EMP002");
            Employee emp004 = employeeMap.get("EMP004");
            Employee emp008 = employeeMap.get("EMP008"); // Jane (Driver) - ensures this shift has a Driver

            if (isEmployeeAvailableForShift(emp002, date, type)) available.add(emp002);
            if (isEmployeeAvailableForShift(emp004, date, type)) available.add(emp004);
            if (isEmployeeAvailableForShift(emp008, date, type)) available.add(emp008);

            String role002 = getFirstRoleOrUnknown(emp002);
            String role004 = getFirstRoleOrUnknown(emp004);
            String role008 = getFirstRoleOrUnknown(emp008);

            if (isEmployeeAvailableForShift(emp002, date, type)) {
                assigned.put(emp002, role002);
                available.remove(emp002);
            }
            if (isEmployeeAvailableForShift(emp004, date, type)) {
                assigned.put(emp004, role004);
                available.remove(emp004);
            }
            if (isEmployeeAvailableForShift(emp008, date, type)) {
                assigned.put(emp008, role008);
                available.remove(emp008);
            }
            randomIndex = ThreadLocalRandom.current().nextInt(branches.size());
            shifts.add(new Shift(
                    0, date, type, emp002, assigned, available, branches.get(randomIndex)));
        }

        // --- FUTURE SHIFT 3: Next week with all new employees ---
        {
            Set<Employee> available = new HashSet<>();
            Map<Employee, String> assigned = new HashMap<>();
            LocalDate date = LocalDate.now().plusDays(6);
            date = Auxiliary.Helper.getValidDateForMock(date);
            String type = "Day";

            Employee emp001 = employeeMap.get("EMP001");
            Employee emp002 = employeeMap.get("EMP002");
            //Employee emp007 = employeeMap.get("EMP007"); // John (Driver)
            //Employee emp008 = employeeMap.get("EMP008"); // Jane (Driver)
            Employee emp009 = employeeMap.get("EMP009"); // Sam (Driver) - all 3 new Drivers in one shift

            if (isEmployeeAvailableForShift(emp001, date, type)) available.add(emp001);
            if (isEmployeeAvailableForShift(emp002, date, type)) available.add(emp002);
//            if (isEmployeeAvailableForShift(emp007, date, type)) available.add(emp007);
//            if (isEmployeeAvailableForShift(emp008, date, type)) available.add(emp008);
            if (isEmployeeAvailableForShift(emp009, date, type)) available.add(emp009);

            String role001 = getFirstRoleOrUnknown(emp001);
            String role002 = getFirstRoleOrUnknown(emp002);
//            String role007 = getFirstRoleOrUnknown(emp007);
//            String role008 = getFirstRoleOrUnknown(emp008);
            String role009 = getFirstRoleOrUnknown(emp009);

            if (isEmployeeAvailableForShift(emp002, date, type)) {
                assigned.put(emp002, role002);
                available.remove(emp002);
            }
            if (isEmployeeAvailableForShift(emp001, date, type)) {
                assigned.put(emp001, role001);
                available.remove(emp001);
            }
//            if (isEmployeeAvailableForShift(emp007, date, type)) {
//                assigned.put(emp007, role007);
//                available.remove(emp007);
//            }
//            if (isEmployeeAvailableForShift(emp008, date, type)) {
//                assigned.put(emp008, role008);
//                available.remove(emp008);
//            }
            if (isEmployeeAvailableForShift(emp009, date, type)) {
                assigned.put(emp009, role009);
                available.remove(emp009);
            }
            randomIndex = ThreadLocalRandom.current().nextInt(branches.size());
            shifts.add(new Shift(
                    0, date, type, emp002, assigned, available, branches.get(randomIndex)));
        }

        return shifts;
    }

    private static boolean isEmployeeAvailableForShift(Employee employee, LocalDate date, String shiftType) {
        if (employee == null) return false;
        // Use the AvailabilityManager.isAvailable method which takes LocalDate and Employee
        return AvailabilityManager.isAvailable(date, employee);
    }

    private static String getFirstRoleOrUnknown(Employee employee) {
        if (employee == null || employee.getRolesList().isEmpty()) {
            return "Unknown";
        }
        return employee.getRolesList().get(0).getRole();
    }
}
