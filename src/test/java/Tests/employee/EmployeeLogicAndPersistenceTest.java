package Tests.employee;

import DB.DBemployeeManager;
import DB.implementations.MockFactory.MockEmployeeData;
import DB.implementations.MockFactory.MockShiftsData;
import DB.implementations.employee.MockEmployeesController;
import DB.interfaces.employees.IEmployeeController;
import DB.interfaces.employees.IUpcomingShiftsController;
import Models.DBModels.Employees.*;
import Models.DBModels.transport.Branch;
import Auxiliary.Enums.Domain.Status;
import Domain.transport.BranchesManager;
import Domain.transport.DriversManager;
import Domain.Employees.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeLogicAndPersistenceTest {

    // ==== Shared Helper Methods  ====
    private static String getAvailabilityDescription(Auxiliary.Enums.Models.AvailabilityStatus status) {
        switch (status) {
            case Day:
                return "Available during day shift";
            case Night:
                return "Available during night shift";
            case Both:
                return "Available during both shifts";
            case Unavailable:
                return "Not available";
            case Skip:
                return "Skip this day";
            default:
                return "Unknown status";
        }
    }

    // ==== Persistence (DB/Hibernate) Tests ====
    @Nested
    class PersistenceIntegration {

        private IEmployeeController employeeController;
        private IUpcomingShiftsController shiftController;
        private List<Employee> mockEmployees;

        @BeforeAll
        static void globalSetUp() {

            Auxiliary.GlobalConfig.setTestMode(true);
        }

        private static void clearTables() {
            try (org.hibernate.Session session = DB.util.HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.createQuery("DELETE FROM Shift").executeUpdate();
                session.createQuery("DELETE FROM Employee").executeUpdate();
                session.createQuery("DELETE FROM Driver").executeUpdate();
                session.createQuery("DELETE FROM TransportArea").executeUpdate();
                session.createQuery("DELETE FROM Branch").executeUpdate();
                session.getTransaction().commit();
            }
        }

        @BeforeEach
        void setUp() {
            clearTables();
            BranchesManager.addBranch("Main Branch", "1 Main St", "123-456");
            BranchesManager.addBranch("Secondary Branch", "2 Side St", "321-654");
            DriversManager.addDriver("John", "Doe", "EMP007", "DEMO-PHONE-007", "C");
            DriversManager.addDriver("Sam", "Wilson", "EMP008", "DEMO-PHONE-008", "B");
            DriversManager.addDriver("Jane", "Smith", "EMP009", "DEMO-PHONE-009", "C1");

            employeeController = DBemployeeManager.getInstance().getEmployeeController();
            shiftController = DBemployeeManager.getInstance().getUpcomingShiftsController();
            mockEmployees = MockEmployeeData.createMockEmployees();
        }

        @Test
        void testAddEmployeeToDBAndFetch() {
            System.out.println("Running: testAddEmployeeToDBAndFetch");
            Employee emp = mockEmployees.get(0);
            Status status = employeeController.addEmployeeToDB(emp);
            assertEquals(Status.Success, status);
            Employee fetched = employeeController.findEmployeeById(emp.getEmployeeID());
            assertNotNull(fetched);
            assertEquals(emp.getName(), fetched.getName());
        }

        @Test
        void testUpdateEmployeeInDB() {
            System.out.println("Running: testUpdateEmployeeInDB");
            Employee emp = mockEmployees.get(1);
            employeeController.addEmployeeToDB(emp);
            emp.setName("UpdatedName");
            emp.setSalary(123.45);
            Status status = employeeController.updateEmployee(emp);
            assertEquals(Status.Success, status);
            Employee updated = employeeController.findEmployeeById(emp.getEmployeeID());
            assertEquals("UpdatedName", updated.getName());
            assertEquals(123.45, updated.getSalary());
        }

        @Test
        void testRemoveEmployeeFromDB() {
            System.out.println("Running: testRemoveEmployeeFromDB");
            Employee emp = mockEmployees.get(2);
            employeeController.addEmployeeToDB(emp);
            Status status = employeeController.removeEmployeeFromDB(emp.getEmployeeID());
            assertEquals(Status.Success, status);
            assertNull(employeeController.findEmployeeById(emp.getEmployeeID()));
        }

        @Test
        void testAddEmployeeToShiftInDB() {
            System.out.println("Running: testAddEmployeeToShiftInDB");
            // Add employees
            for (Employee e : mockEmployees) {
                employeeController.addEmployeeToDB(e);
            }
            Employee emp = mockEmployees.get(0);
            Employee manager = mockEmployees.get(1);
            Employee persistedEmp = employeeController.findEmployeeById(emp.getEmployeeID());
            Employee persistedManager = employeeController.findEmployeeById(manager.getEmployeeID());
            List<Branch> branches = BranchesManager.getExistingBranches();
            Branch testBranch;
            if (branches.isEmpty()) {
                BranchesManager.addBranch("Test Branch", "123 Test St", "555-0123");
                branches = BranchesManager.getExistingBranches();
            }
            testBranch = branches.get(0);

            // **Using Set instead of List**
            Set<Employee> available = new HashSet<>(List.of(persistedEmp));
            Map<Employee, String> assigned = new HashMap<>();

            Shift shift = new Shift(
                    0,
                    LocalDate.now().plusDays(1),
                    "Day",
                    persistedManager,
                    assigned,
                    available,
                    testBranch
            );
            shiftController.addUpcomingShift(shift);

            // Add employee to shift
            Shift managedShift = shiftController.findShiftById(shift.getShiftID());
            managedShift.getAssignedEmployees().put(persistedEmp, "Stocker");
            managedShift.getAllAvailableEmployees().remove(persistedEmp);
            shiftController.updateShiftInDB(managedShift);

            // Validate
            Shift fetched = shiftController.findShiftById(managedShift.getShiftID());
            assertNotNull(fetched);
            Employee managedEmp = employeeController.findEmployeeById(persistedEmp.getEmployeeID());
            boolean found = fetched.getAssignedEmployees().keySet().stream()
                    .anyMatch(e -> e.getEmployeeID().equals(managedEmp.getEmployeeID()));
            assertTrue(found);
        }

        @Test
        void testRemoveEmployeeFromShiftInDB() {
            System.out.println("Running: testRemoveEmployeeFromShiftInDB");
            // Add employees
            for (Employee e : mockEmployees) {
                employeeController.addEmployeeToDB(e);
            }
            Employee emp = mockEmployees.get(0);
            Employee manager = mockEmployees.get(1);
            Employee persistedEmp = employeeController.findEmployeeById(emp.getEmployeeID());
            Employee persistedManager = employeeController.findEmployeeById(manager.getEmployeeID());
            List<Branch> branches = BranchesManager.getExistingBranches();
            Branch testBranch;
            if (branches.isEmpty()) {
                BranchesManager.addBranch("Test Branch", "123 Test St", "555-0123");
                branches = BranchesManager.getExistingBranches();
            }
            testBranch = branches.get(0);
            Set<Employee> available = new HashSet<>(); // Empty, as the employee is assigned
            Map<Employee, String> assigned = new HashMap<>();
            assigned.put(persistedEmp, "Stocker");
            Shift shift = new Shift(0, LocalDate.now().plusDays(1), "Day", persistedManager, assigned, available, testBranch);
            shiftController.addUpcomingShift(shift);

            // Remove employee from shift
            Shift managedShift = shiftController.findShiftById(shift.getShiftID());
            managedShift.getAssignedEmployees().remove(persistedEmp);
            managedShift.getAllAvailableEmployees().add(persistedEmp);
            shiftController.updateShiftInDB(managedShift);

            // Validate removal
            Shift fetched2 = shiftController.findShiftById(managedShift.getShiftID());
            assertNotNull(fetched2);
            Employee managedEmp = employeeController.findEmployeeById(persistedEmp.getEmployeeID());
            boolean notFound = fetched2.getAssignedEmployees().keySet().stream()
                    .noneMatch(e -> e.getEmployeeID().equals(managedEmp.getEmployeeID()));
            assertTrue(notFound);
        }

        @Test
        void testRemoveShiftFromDB() {
            System.out.println("Running: testRemoveShiftFromDB");
            // Add employees
            for (Employee e : mockEmployees) {
                employeeController.addEmployeeToDB(e);
            }
            Employee emp = mockEmployees.get(0);
            Employee manager = mockEmployees.get(1);
            Employee persistedEmp = employeeController.findEmployeeById(emp.getEmployeeID());
            Employee persistedManager = employeeController.findEmployeeById(manager.getEmployeeID());
            List<Branch> branches = BranchesManager.getExistingBranches();
            Branch testBranch = branches.isEmpty() ? null : branches.get(0);
            Set<Employee> available = new HashSet<>(Set.of(persistedEmp));
            Map<Employee, String> assigned = new HashMap<>();
            Shift shift = new Shift(0, LocalDate.now().plusDays(1), "Day", persistedManager, assigned, available, testBranch);
            shiftController.addUpcomingShift(shift);
            // Remove shift
            Shift managedShift = shiftController.findShiftById(shift.getShiftID());
            boolean removed = shiftController.removeUpcomingShift(managedShift);
            assertTrue(removed);
            Shift fetched = shiftController.findShiftById(managedShift.getShiftID());
            assertNull(fetched);
        }

        @Test
        void testAddShiftToDBAndFetch() {
            System.out.println("Running: testAddShiftToDBAndFetch");
            for (Employee e : mockEmployees) {
                employeeController.addEmployeeToDB(e);
            }
            List<Employee> persistedEmployees = new ArrayList<>();
            for (String id : employeeController.getAllIds()) {
                persistedEmployees.add(employeeController.findEmployeeById(id));
            }
            List<Shift> shifts = MockShiftsData.createUpcomingShiftsMockDataWithEmployees(persistedEmployees);
            Shift shift = shifts.get(0);
            boolean added = shiftController.addUpcomingShift(shift);
            assertTrue(added);
            Shift fetched = shiftController.findShiftById(shift.getShiftID());
            assertNotNull(fetched);
            assertEquals(shift.getShiftDate(), fetched.getShiftDate());
        }

        @Test
        void testAddDriverEmployeeToShift() {
            System.out.println("Running: testAddDriverEmployeeToShift");
            // Add employees
            for (Employee e : mockEmployees) {
                employeeController.addEmployeeToDB(e);
            }
            // Find a driver (assuming at least one mock employee is a driver)
            Employee driver = mockEmployees.stream()
                    .filter(e -> e.getRolesList().stream().anyMatch(r -> r.getRoleName().equalsIgnoreCase("Driver")))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("No driver in mockEmployees"));
            Employee manager = mockEmployees.get(1);
            Employee persistedDriver = employeeController.findEmployeeById(driver.getEmployeeID());
            Employee persistedManager = employeeController.findEmployeeById(manager.getEmployeeID());
            List<Branch> branches = BranchesManager.getExistingBranches();
            Branch testBranch = branches.isEmpty() ? null : branches.get(0);
            Set<Employee> available = new HashSet<>(Set.of(persistedDriver));
            Map<Employee, String> assigned = new HashMap<>();
            Shift shift = new Shift(0, LocalDate.now().plusDays(1), "Day", persistedManager, assigned, available, testBranch);
            shiftController.addUpcomingShift(shift);
            // Add driver to shift
            Shift managedShift = shiftController.findShiftById(shift.getShiftID());
            managedShift.getAssignedEmployees().put(persistedDriver, "Driver");
            managedShift.getAllAvailableEmployees().remove(persistedDriver);
            shiftController.updateShiftInDB(managedShift);
            // Validate
            Shift fetched = shiftController.findShiftById(managedShift.getShiftID());
            assertNotNull(fetched);
            boolean found = fetched.getAssignedEmployees().keySet().stream()
                    .anyMatch(e -> e.getEmployeeID().equals(persistedDriver.getEmployeeID()));
            assertTrue(found);
        }

        @Test
        void testRemoveEmployeeFromShift() {
            System.out.println("Running: testRemoveEmployeeFromShift");
            // Add employees
            for (Employee e : mockEmployees) {
                employeeController.addEmployeeToDB(e);
            }
            Employee emp = mockEmployees.get(0);
            Employee manager = mockEmployees.get(1);
            Employee persistedEmp = employeeController.findEmployeeById(emp.getEmployeeID());
            Employee persistedManager = employeeController.findEmployeeById(manager.getEmployeeID());
            List<Branch> branches = BranchesManager.getExistingBranches();
            Branch testBranch = branches.isEmpty() ? null : branches.get(0);
            Set<Employee> available = new HashSet<>();
            Map<Employee, String> assigned = new HashMap<>();
            assigned.put(persistedEmp, "Stocker");
            Shift shift = new Shift(0, LocalDate.now().plusDays(1), "Day", persistedManager, assigned, available, testBranch);
            shiftController.addUpcomingShift(shift);
            // Remove employee from shift
            Shift managedShift = shiftController.findShiftById(shift.getShiftID());
            managedShift.getAssignedEmployees().remove(persistedEmp);
            managedShift.getAllAvailableEmployees().add(persistedEmp);
            shiftController.updateShiftInDB(managedShift);
            // Validate
            Shift fetched = shiftController.findShiftById(managedShift.getShiftID());
            assertNotNull(fetched);
            boolean notFound = fetched.getAssignedEmployees().keySet().stream()
                    .noneMatch(e -> e.getEmployeeID().equals(persistedEmp.getEmployeeID()));
            assertTrue(notFound);
        }

        @Test
        void testEndToEndEmployeeShiftLifecycle() {
            System.out.println("Running: testEndToEndEmployeeShiftLifecycle");
            // 1. Add a new Driver Employee
            Employee driver = mockEmployees.stream()
                    .filter(e -> e.getRolesList().stream().anyMatch(r -> r.getRoleName().equalsIgnoreCase("Driver")))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("No driver in mockEmployees"));
            Status addStatus = employeeController.addEmployeeToDB(driver);
            assertEquals(Status.Success, addStatus);
            Employee persistedDriver = employeeController.findEmployeeById(driver.getEmployeeID());
            assertNotNull(persistedDriver);

            // 2. Create a new Shift
            Employee manager = mockEmployees.get(1);
            Employee persistedManager = employeeController.findEmployeeById(manager.getEmployeeID());
            List<Branch> branches = BranchesManager.getExistingBranches();
            Branch testBranch = branches.isEmpty() ? null : branches.get(0);
            Set<Employee> available = new HashSet<>(Set.of(persistedDriver));
            Map<Employee, String> assigned = new HashMap<>();
            Shift shift = new Shift(0, LocalDate.now().plusDays(2), "Night", persistedManager, assigned, available, testBranch);
            boolean shiftAdded = shiftController.addUpcomingShift(shift);
            assertTrue(shiftAdded);

            // 3. Assign the Driver to the Shift
            Shift managedShift = shiftController.findShiftById(shift.getShiftID());
            managedShift.getAssignedEmployees().put(persistedDriver, "Driver");
            managedShift.getAllAvailableEmployees().remove(persistedDriver);
            shiftController.updateShiftInDB(managedShift);

            // 4. Fetch and verify assignment
            Shift fetched = shiftController.findShiftById(managedShift.getShiftID());
            assertNotNull(fetched);
            boolean found = fetched.getAssignedEmployees().keySet().stream()
                    .anyMatch(e -> e.getEmployeeID().equals(persistedDriver.getEmployeeID()));
            assertTrue(found);

            // 5. Remove the Driver from the Shift
            managedShift.getAssignedEmployees().remove(persistedDriver);
            managedShift.getAllAvailableEmployees().add(persistedDriver);
            shiftController.updateShiftInDB(managedShift);

            // 6. Fetch and verify removal
            Shift fetched2 = shiftController.findShiftById(managedShift.getShiftID());
            assertNotNull(fetched2);
            boolean notFound = fetched2.getAssignedEmployees().keySet().stream()
                    .noneMatch(e -> e.getEmployeeID().equals(persistedDriver.getEmployeeID()));
            assertTrue(notFound);

            // 7. Remove the Shift and verify deletion
            boolean removed = shiftController.removeUpcomingShift(managedShift);
            assertTrue(removed);
            Shift fetched3 = shiftController.findShiftById(managedShift.getShiftID());
            assertNull(fetched3);
        }

        @AfterEach
        void tearDown() {
            for (String id : new ArrayList<>(employeeController.getAllIds())) {
                employeeController.removeEmployeeFromDB(id);
            }
            for (Shift shift : new ArrayList<>(shiftController.getUpcomingShifts())) {
                shiftController.removeUpcomingShift(shift);
            }
        }

        @AfterAll
        static void globalTearDown() {
            Auxiliary.GlobalConfig.setTestMode(false);
        }
    }

    // ==== (Mock/Logic) Tests ====
    @Nested
    class InMemoryLogicUnit {

        private MockEmployeesController controller;
        private List<Employee> mockEmployees;

        @BeforeAll
        static void globalSetUp() {

            Auxiliary.GlobalConfig.setTestMode(false);
            Presentation.GeneralPresentation.handleTempMockDataRuntimeLoad();
        }

        @BeforeEach
        void setUp() {
            controller = MockEmployeesController.getInstance();
            for (String id : new ArrayList<>(controller.getAllIds())) {
                controller.removeEmployeeFromDB(id);
            }
            mockEmployees = MockEmployeeData.createMockEmployees();
        }

        @Test
        void testAddAllMockEmployees() {
            System.out.println("Running: testAddAllMockEmployees");
            for (Employee e : mockEmployees) {
                Status status = controller.addEmployeeToDB(e);
                assertEquals(Status.Success, status, "Should add employee: " + e.getEmployeeID());
            }
            assertEquals(mockEmployees.size(), controller.getAllIds().size());
        }

        @Test
        void testFetchEmployeeById() {
            System.out.println("Running: testFetchEmployeeById");
            Employee e = mockEmployees.get(0);
            controller.addEmployeeToDB(e);
            Employee fetched = controller.findEmployeeById(e.getEmployeeID());
            assertNotNull(fetched);
            assertEquals(e.getName(), fetched.getName());
        }

        @Test
        void testUpdateEmployeeDetails() {
            System.out.println("Running: testUpdateEmployeeDetails");
            Employee e = mockEmployees.get(1);
            controller.addEmployeeToDB(e);
            e.setName("UpdatedName");
            e.setSalary(99.99);
            Status status = controller.updateEmployee(e);
            assertEquals(Status.Success, status);
            Employee updated = controller.findEmployeeById(e.getEmployeeID());
            assertEquals("UpdatedName", updated.getName());
            assertEquals(99.99, updated.getSalary());
        }

        @Test
        void testRemoveEmployee() {
            System.out.println("Running: testRemoveEmployee");
            Employee e = mockEmployees.get(2);
            controller.addEmployeeToDB(e);
            Status status = controller.removeEmployeeFromDB(e.getEmployeeID());
            assertEquals(Status.Success, status);
            assertNull(controller.findEmployeeById(e.getEmployeeID()));
        }

        @Test
        void testPreventDuplicateEmployeeAddition() {
            System.out.println("Running: testPreventDuplicateEmployeeAddition");
            Employee e = mockEmployees.get(3);
            Status first = controller.addEmployeeToDB(e);
            Status second = controller.addEmployeeToDB(e);
            assertEquals(Status.Success, first);
            assertNotEquals(Status.Success, second);
        }

        @Test
        void testFetchAllEmployeeIds() {
            System.out.println("Running: testFetchAllEmployeeIds");
            for (Employee e : mockEmployees) {
                controller.addEmployeeToDB(e);
            }
            List<String> ids = controller.getAllIds();
            assertEquals(mockEmployees.size(), ids.size());
            for (Employee e : mockEmployees) {
                assertTrue(ids.contains(e.getEmployeeID()));
            }
        }

        @Test
        void testAddEmployeeWithNullIdFails() {
            System.out.println("Running: testAddEmployeeWithNullIdFails");
            Employee e = new Employee(0, "NoId", 10.0, null, LocalDate.now(), new ArrayList<>(), null, null, null);
            Status status = controller.addEmployeeToDB(e);
            assertNotEquals(Status.Success, status);
        }

        @Test
        void testUpdateNonExistentEmployeeFails() {
            System.out.println("Running: testUpdateNonExistentEmployeeFails");
            Employee e = mockEmployees.get(4);
            Status status = controller.updateEmployee(e); // not added yet
            assertNotEquals(Status.Success, status);
        }

        @Test
        void testRemoveNonExistentEmployeeFails() {
            System.out.println("Running: testRemoveNonExistentEmployeeFails");
            Status status = controller.removeEmployeeFromDB("NON_EXISTENT_ID");
            assertNotEquals(Status.Success, status);
        }

        @Test
        void testAddEmployeeWithMultipleRoles() {
            System.out.println("Running: testAddEmployeeWithMultipleRoles");
            List<ARole> roles = new ArrayList<>();
            roles.add(new Stocker(0));
            roles.add(new Cleaner(0));
            Employee e = new Employee(0, "MultiRole", 50.0, "MULTI001", LocalDate.now(), roles, null, null, null);
            Status status = controller.addEmployeeToDB(e);
            assertEquals(Status.Success, status);
            Employee fetched = controller.findEmployeeById("MULTI001");
            assertEquals(2, fetched.getRolesList().size());
        }

        @Test
        void testAddAndFetchEmployeeWithBankContractAvailability() {
            System.out.println("Running: testAddAndFetchEmployeeWithBankContractAvailability");
            BankAccount bank = new BankAccount("TestBank", "TEST-ACCOUNT-001", "TEST-BRANCH-001");
            Contract contract = new Contract(0, 5, 10, 15, 100);
            Availability avail = new Availability(
                    Auxiliary.Enums.Models.AvailabilityStatus.Day,
                    Auxiliary.Enums.Models.AvailabilityStatus.Night,
                    Auxiliary.Enums.Models.AvailabilityStatus.Both,
                    Auxiliary.Enums.Models.AvailabilityStatus.Unavailable,
                    Auxiliary.Enums.Models.AvailabilityStatus.Skip
            );
            Employee e = new Employee(0, "FullData", 77.7, "FULL001", LocalDate.now(), new ArrayList<>(), bank, contract, avail);
            Status status = controller.addEmployeeToDB(e);
            assertEquals(Status.Success, status);
            Employee fetched = controller.findEmployeeById("FULL001");
            assertNotNull(fetched.getBank_info());
            assertNotNull(fetched.getEmployee_condition());
            assertNotNull(fetched.getAvailability());
            assertEquals("TestBank", fetched.getBank_info().getBankName());
            assertEquals(100, fetched.getEmployee_condition().getMinHours());
            assertEquals(Auxiliary.Enums.Models.AvailabilityStatus.Day, fetched.getAvailability().getSunday());
        }

        @Test
        void testEmployeeRoleManagement_Mock() {
            System.out.println("Running: testEmployeeRoleManagement_Mock");
            Employee e = mockEmployees.get(0);
            controller.addEmployeeToDB(e);
            // Add another role
            e.getRolesList().add(new Stocker(0));
            controller.updateEmployee(e);
            Employee updated = controller.findEmployeeById(e.getEmployeeID());
            assertTrue(updated.getRolesList().size() >= 2);
            Set<String> roleNames = new HashSet<>();
            for (ARole role : updated.getRolesList()) {
                roleNames.add(role.getRoleName());
            }
            assertTrue(roleNames.contains("Stocker"));
        }

        @Test
        void testBasicSyntaxAndEnums_Mock() {
            System.out.println("Running: testBasicSyntaxAndEnums_Mock");
            Auxiliary.Enums.Models.AvailabilityStatus unavailable = Auxiliary.Enums.Models.AvailabilityStatus.Unavailable;
            Auxiliary.Enums.Models.AvailabilityStatus day = Auxiliary.Enums.Models.AvailabilityStatus.Day;
            Auxiliary.Enums.Models.AvailabilityStatus night = Auxiliary.Enums.Models.AvailabilityStatus.Night;
            Auxiliary.Enums.Models.AvailabilityStatus both = Auxiliary.Enums.Models.AvailabilityStatus.Both;
            Auxiliary.Enums.Models.AvailabilityStatus skip = Auxiliary.Enums.Models.AvailabilityStatus.Skip;
            assertNotNull(unavailable);
            assertNotNull(day);
            assertNotNull(night);
            assertNotNull(both);
            assertNotNull(skip);
            assertTrue(day == Auxiliary.Enums.Models.AvailabilityStatus.Day);
            assertTrue(day.equals(Auxiliary.Enums.Models.AvailabilityStatus.Day));
            assertFalse(day.equals(night));
            String description = getAvailabilityDescription(day);
            assertEquals("Available during day shift", description);
            System.out.println();
            System.out.println("Setting up Persistence DB Tests");
            System.out.println("==================================");
        }

        @Test
        void testCreateEmployeeWithValidData_Mock() {
            System.out.println("Running: testCreateEmployeeWithValidData_Mock");
            Employee e = new Employee(0, "John Test", 25.0, "EMP_TEST_001", LocalDate.now(), List.of(new Cashier(0)), null, null, null);
            assertNotNull(e);
            assertEquals("John Test", e.getName());
            assertEquals("EMP_TEST_001", e.getEmployeeID());
            assertEquals(25.0, e.getSalary());
            assertNotNull(e.getRolesList());
            assertFalse(e.getRolesList().isEmpty());
            assertTrue(e.getRolesList().get(0) instanceof Cashier);
        }

        @Test
        void testAddEmployeeToSystem_Mock() {
            System.out.println("Running: testAddEmployeeToSystem_Mock");
            Employee e = new Employee(0, "Jane Test", 30.0, "EMP_TEST_002", LocalDate.now(), List.of(new Shift_Manager(0)), null, null, null);
            Status result = controller.addEmployeeToDB(e);
            assertEquals(Status.Success, result);
            Employee retrieved = controller.findEmployeeById(e.getEmployeeID());
            assertNotNull(retrieved);
            assertEquals(e.getEmployeeID(), retrieved.getEmployeeID());
            assertEquals(e.getName(), retrieved.getName());
        }

        @Test
        void testUpdateEmployeeDetails_Mock() {
            System.out.println("Running: testUpdateEmployeeDetails_Mock");
            Employee e = new Employee(0, "John Test", 25.0, "EMP_TEST_001", LocalDate.now(), List.of(new Cashier(0)), null, null, null);
            controller.addEmployeeToDB(e);
            e.setName("John Updated");
            e.setSalary(28.0);
            Status updateResult = controller.updateEmployee(e);
            assertEquals(Status.Success, updateResult);
            Employee updated = controller.findEmployeeById(e.getEmployeeID());
            assertEquals("John Updated", updated.getName());
            assertEquals(28.0, updated.getSalary());
        }

        @Test
        void testHRLeadAddEmployeeToSystem() {
            System.out.println("Running: testHRLeadAddEmployeeToSystem");
            Employee newEmp = new Employee(0, "HRAdd", 55.0, "HRADD001", LocalDate.now(), List.of(new HR_Lead(0)), null, null, null);
            Status status = controller.addEmployeeToDB(newEmp);
            assertEquals(Status.Success, status);
            assertNotNull(controller.findEmployeeById("HRADD001"));
        }

        @Test
        void testHRLeadRemoveEmployeeFromSystem() {
            System.out.println("Running: testHRLeadRemoveEmployeeFromSystem");
            Employee newEmp = new Employee(0, "HRRemove", 55.0, "HRREM001", LocalDate.now(), List.of(new HR_Lead(0)), null, null, null);
            controller.addEmployeeToDB(newEmp);
            Status status = controller.removeEmployeeFromDB("HRREM001");
            assertEquals(Status.Success, status);
            assertNull(controller.findEmployeeById("HRREM001"));
        }

        @Test
        void testAddEmployeeToShiftInMemory() {
            System.out.println("Running: testAddEmployeeToShiftInMemory");
            List<Employee> employees = MockEmployeeData.createMockEmployees();
            Employee emp = employees.get(0); // Alice
            Employee manager = new Employee(0, "Manager", 100.0, "MANAGER001", LocalDate.now(), List.of(new Shift_Manager(0)), null, null, null);
            controller.addEmployeeToDB(emp);
            controller.addEmployeeToDB(manager);
            Set<Employee> available = new HashSet<>(List.of(emp));
            Map<Employee, String> assigned = new HashMap<>();
            Shift shift = new Shift(0, LocalDate.now().plusDays(1), "Day", manager, assigned, available, null);

            // Correct: modify the Shift's own collections
            shift.getAssignedEmployees().put(emp, "Stocker");
            shift.getAllAvailableEmployees().remove(emp);

            assertTrue(shift.getAssignedEmployees().containsKey(emp));
            assertEquals("Stocker", shift.getAssignedEmployees().get(emp));
            assertFalse(shift.getAllAvailableEmployees().contains(emp));
        }

        @Test
        void testRemoveEmployeeFromShiftInMemory() {
            System.out.println("Running: testRemoveEmployeeFromShiftInMemory");
            List<Employee> employees = MockEmployeeData.createMockEmployees();
            Employee emp = employees.get(0); // Alice
            Employee manager = new Employee(0, "Manager", 100.0, "MANAGER002", LocalDate.now(), List.of(new Shift_Manager(0)), null, null, null);
            controller.addEmployeeToDB(emp);
            controller.addEmployeeToDB(manager);
            Map<Employee, String> assigned = new HashMap<>();
            assigned.put(emp, "Stocker");
            Set<Employee> available = new HashSet<>();
            Shift shift = new Shift(0, LocalDate.now().plusDays(1), "Day", manager, assigned, available, null);

            // Correct: modify the Shift's own collections
            shift.getAssignedEmployees().remove(emp);
            shift.getAllAvailableEmployees().add(emp);

            assertFalse(shift.getAssignedEmployees().containsKey(emp));
            assertTrue(shift.getAllAvailableEmployees().contains(emp));
        }

    }
}
