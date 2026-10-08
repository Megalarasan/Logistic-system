package DB.implementations.MockFactory;

import Auxiliary.IdGenerator;
import Auxiliary.Enums.Models.AvailabilityStatus;
import Domain.transport.DriversManager;
import Models.DBModels.Employees.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Domain.Employees.*;

public class MockEmployeeData {

    public static List<Employee> createMockEmployees() {
        List<Employee> employees = new ArrayList<>();
        ArrayList<Models.DBModels.transport.Driver> drivers = DriversManager.getAllDrivers();

        // --- Mock Bank Accounts ---
        BankAccount mockBank1 = new BankAccount("Demo Bank A", "DEMO-ACCOUNT-001", "DEMO-BRANCH-001");
        BankAccount mockBank2 = new BankAccount("Demo Bank B", "DEMO-ACCOUNT-002", "DEMO-BRANCH-002");
        BankAccount mockBank3 = new BankAccount("Demo Bank C", "DEMO-ACCOUNT-003", "DEMO-BRANCH-003");
        BankAccount mockBank4 = new BankAccount("Demo Bank D", "DEMO-ACCOUNT-004", "DEMO-BRANCH-004");
        BankAccount mockBank5 = new BankAccount("Demo Bank E", "DEMO-ACCOUNT-005", "DEMO-BRANCH-005");
        BankAccount mockBank6 = new BankAccount("Demo Bank F", "DEMO-ACCOUNT-006", "DEMO-BRANCH-006");

        // --- Mock Contracts ---
        Contract fullTimeContract = new Contract(0,7, 14, 21, 160);
        Contract partTimeContract = new Contract(0,5, 10, 14, 80);
        Contract flexibleContract = new Contract(0, 6, 12, 18, 120);

        // --- Mock Availabilities ---
        Availability mockAvailability1 = new Availability(
                AvailabilityStatus.Day, AvailabilityStatus.Day, AvailabilityStatus.Day,
                AvailabilityStatus.Night, AvailabilityStatus.Unavailable
        );

        Availability mockAvailability2 = new Availability(
                AvailabilityStatus.Day, AvailabilityStatus.Night, AvailabilityStatus.Night,
                AvailabilityStatus.Day, AvailabilityStatus.Day
        );

        Availability mockAvailability3 = new Availability(
                AvailabilityStatus.Day, AvailabilityStatus.Night, AvailabilityStatus.Day,
                AvailabilityStatus.Night, AvailabilityStatus.Day
        );

        Availability mockAvailability4 = new Availability(
                AvailabilityStatus.Day, AvailabilityStatus.Day, AvailabilityStatus.Day,
                AvailabilityStatus.Day, AvailabilityStatus.Night
        );

        Availability mockAvailability5 = new Availability(
                AvailabilityStatus.Both, AvailabilityStatus.Both, AvailabilityStatus.Both,
                AvailabilityStatus.Both, AvailabilityStatus.Both
        );

        // --- Mock Roles ---
        ARole manager1 = new Shift_Manager(0);
        ARole hrLead1 = new HR_Lead(0);
        ARole cleaner1 = new Cleaner(0);
        ARole security1 = new Security(0);
        ARole stocker1 = new Stocker(0);

        // --- Employees ---
        // Employee 1: Alice
        List<ARole> rolesList1 = new ArrayList<>(List.of(stocker1));
        Employee emp1 = new Employee(
                0, "Alice", 32.5, "EMP001",
                LocalDate.of(2022, 3, 15),
                rolesList1,
                mockBank1,
                fullTimeContract,
                mockAvailability1
        );
        employees.add(emp1);

        // Employee 2: Bob
        List<ARole> rolesList2 = new ArrayList<>();
        rolesList2.add(manager1);
        rolesList2.add(stocker1);
        Employee emp2 = new Employee(
                0, "Bob", 47.1, "EMP002",
                LocalDate.of(2023, 1, 10),
                rolesList2,
                mockBank2,
                partTimeContract,
                mockAvailability2
        );
        employees.add(emp2);

        // Employee 3: Charlie
        List<ARole> rolesList3 = new ArrayList<>();
        rolesList3.add(hrLead1);
        Employee emp3 = new Employee(
                0, "Charlie", 120.2, "EMP003",
                LocalDate.of(2021, 5, 20),
                rolesList3,
                mockBank3,
                flexibleContract,
                mockAvailability3
        );
        employees.add(emp3);

        // Employee 4: David
        List<ARole> rolesList4 = new ArrayList<>();
        rolesList4.add(stocker1);
        Employee emp4 = new Employee(
                0, "David", 35.0, "EMP004",
                LocalDate.of(2023, 6, 1),
                rolesList4,
                mockBank4,
                partTimeContract,
                mockAvailability4
        );
        employees.add(emp4);

        // Employee 5: Eve
        List<ARole> rolesList5 = new ArrayList<>();
        rolesList5.add(stocker1);
        rolesList5.add(cleaner1);
        Employee emp5 = new Employee(
                0, "Eve", 33.5, "EMP005",
                LocalDate.of(2023, 7, 15),
                rolesList5,
                mockBank5,
                flexibleContract,
                mockAvailability5
        );
        employees.add(emp5);

        // Employee 6: Inna
        List<ARole> rolesList6 = new ArrayList<>();
        rolesList6.add(stocker1);
        rolesList6.add(security1);
        Employee emp6 = new Employee(
                0, "Inna", 33, "EMP006",
                LocalDate.of(2024, 7, 10),
                rolesList6,
                mockBank6,
                flexibleContract,
                mockAvailability5
        );
        employees.add(emp6);

        // Employee 7: John
        List<ARole> rolesList7 = new ArrayList<>();
        rolesList7.add(new Driver(0));
        String personalId = drivers.get(0).getPersonalId();
        Employee emp7 = new Employee(
                0, "John", 33, personalId,
                LocalDate.of(2024, 7, 10),
                rolesList7,
                mockBank6,
                flexibleContract,
                mockAvailability5
        );
        employees.add(emp7);

        // Employee 8: Jane
        List<ARole> rolesList8 = new ArrayList<>();
        rolesList8.add(new Driver(0));
        String personalId2 = drivers.get(1).getPersonalId();
        String name2 = drivers.get(1).getFirstName();
        Employee emp8 = new Employee(
                0, name2, 33, personalId2,
                LocalDate.of(2024, 7, 10),
                rolesList8,
                mockBank6,
                flexibleContract,
                mockAvailability5
        );
        employees.add(emp8);

        // Employee 9: Sam
        List<ARole> rolesList9 = new ArrayList<>();
        rolesList9.add(new Driver(0));
        String personalId3 = drivers.get(2).getPersonalId();
        String name3 = drivers.get(2).getFirstName();
        Employee emp9 = new Employee(
                0, name3, 33, personalId3,
                LocalDate.of(2024, 7, 10),
                rolesList9,
                mockBank6,
                flexibleContract,
                mockAvailability5
        );
        employees.add(emp9);

        return employees;
    }
}
