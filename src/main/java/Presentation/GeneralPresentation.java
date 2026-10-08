package Presentation;

import Auxiliary.Enums.Domain.CreateNewTransportResultEnum;
import Auxiliary.Helper;
import DB.implementations.MockFactory.MockShiftsData;
import DB.util.HibernateUtil;
import Domain.stock.StockManager;
import Domain.suppliers.SuppliersManager;
import Domain.transport.*;
import Models.DBModels.BaseModel;
import Models.DBModels.transport.Branch;
import Models.DBModels.transport.Driver;
import Models.DBModels.transport.TransportRoute;
import Models.DBModels.transport.Truck;
import Models.DTOs.StockItem;
import Models.DTOs.transport.CreateNewTransportResult;
import Models.DTOs.transport.CreateNewTransportRoutesResult;
import Presentation.Employee.System_Login_Panel;
import Presentation.transport.TransportPresentation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GeneralPresentation class handles the main menu and user input for the Transport+Employee Management System.
 * It provides options to manage the transport system or the employee system.
 */
public class GeneralPresentation {
    private static boolean mockDataLoaded = false;


    /**
     * Main method to run the GeneralPresentation.
     * It displays the main menu and handles user input.
     */
    public static void runner() {
        Helper.print("""
                Load mock data?
                [Warning!!! This will wipe(erase) the current DB, the point of the mock data is to give you a minimal working fresh DB]
                (y/n):\s""");
        String loadMockData = Helper.getUserInputString();
        while (loadMockData == null || loadMockData.isEmpty() || (
                !loadMockData.equalsIgnoreCase("y") && !loadMockData.equalsIgnoreCase("n"))) {
            Helper.print("Please enter 'y' or 'n': ");
            loadMockData = Helper.getUserInputString();
        }
        if (loadMockData.equalsIgnoreCase("y")) {
            Helper.println("Loading mock data...");
            if (!handleTempMockDataRuntimeLoad()) {
                Helper.println("Failed to load mock data. Exiting.");
                return;
            }
        }
        else
        {Helper.println("Skipping mock data loading.");}

        int choice;
        do {
            Helper.println("Welcome to the Transport+Employee Management System!");
            Helper.println("Please choose an option:");
            Helper.println("1. Transport system");
            Helper.println("2. Employee system");
            Helper.println("3. Exit");
            Helper.print("Your choice: ");
            choice = Helper.getUserInputInteger();
            handleChoice(choice);
        } while (choice != 3);
    }

    private static void handleChoice(int choice) {
        switch (choice) {
            case 1:
                TransportPresentation.TransportPresentationHandler();
                break;
            case 2:
                System_Login_Panel.printLoginPanel();
                break;
            case 3:
                Helper.println("Exiting the system. Goodbye!");
                break;
            default:
                Helper.println("Invalid choice. Please try again.");
                break;
        }
    }


    /**
     * Loads temporary mock data for testing purposes.
     * This method is used to populate the system with some initial data.
     *
     * @return true if the data was loaded successfully, false otherwise
     */
    public static boolean handleTempMockDataRuntimeLoad() {
        if (mockDataLoaded) {
            Helper.println("Temporary mock data has already been loaded.");
            return false;
        }
        // we should delete any existing dbs if the point is to test mock data
         HibernateUtil.deleteDatabaseFiles();

        BranchesManager.addBranch("Branch1", "Demo Address 1", "DEMO-PHONE-001");
        BranchesManager.addBranch("Branch2", "Demo Address 2", "DEMO-PHONE-002");
        BranchesManager.addBranch("Branch3", "Demo Address 3", "DEMO-PHONE-003");


        DriversManager.addDriver("John", "Doe", "EMP007", "DEMO-PHONE-007", "C");
        DriversManager.addDriver("Sam", "Wilson", "EMP008", "DEMO-PHONE-008", "B");
        DriversManager.addDriver("Jane", "Smith", "EMP009", "DEMO-PHONE-009", "C1");

        TrucksManager.addTruck("DEMO-TRUCK-001", 3500, 5000, "C1", "Toyota");
        TrucksManager.addTruck("DEMO-TRUCK-002", 1000, 3000, "C", "Ford");
        TrucksManager.addTruck("DEMO-TRUCK-003", 4500, 7000, "C1", "Mercedes");

        System_Login_Panel.EmployeesPresentationHandler();

        SuppliersManager.addSupplier("Supplier1", "Demo Address 4", "DEMO-PHONE-004");
        SuppliersManager.addSupplier("Supplier2", "Demo Address 5", "DEMO-PHONE-005");
        SuppliersManager.addSupplier("Supplier3", "Demo Address 6", "DEMO-PHONE-006");

        TransportAreasManager.addNewTransportArea("Area1",
                BranchesManager.getExistingBranches().subList(0, BranchesManager.getExistingBranches().size()).stream().map(BaseModel::getId).toList(),
                SuppliersManager.getSuppliers().subList(0, BranchesManager.getExistingBranches().size()).stream().map(BaseModel::getId).toList());

        TransportAreasManager.addNewTransportArea("Area2",
                BranchesManager.getExistingBranches().subList(0, BranchesManager.getExistingBranches().size() - 1).stream().map(BaseModel::getId).toList(),
                SuppliersManager.getSuppliers().subList(0, BranchesManager.getExistingBranches().size() - 1).stream().map(BaseModel::getId).toList());

        Branch firstBranch = BranchesManager.getBranchByName("Branch1");
        assert firstBranch != null;
        List<StockItem> selectedItems = StockManager.mockSearchAndReturnRelevantStockItems("", firstBranch.getId());
        LocalDateTime plannedStartDateTime = LocalDateTime.now().plusDays(3);
        plannedStartDateTime = Auxiliary.Helper.getValidDateTimeForMock(plannedStartDateTime);

        LocalDateTime plannedEndDateTime = plannedStartDateTime.plusHours(2);

        CreateNewTransportRoutesResult routesCreationResult =
                TransportManager.createNewTransportRoutes(firstBranch.getId(), selectedItems);
        List<TransportRoute> createdTransportRoutesIds = new ArrayList<>(routesCreationResult.getCreatedTransportRoutes());
        List<Truck> trucks = TransportManager.getAvailableTrucksByDate(plannedStartDateTime, plannedEndDateTime);
        Truck selectedTruck = null;
        Driver selectedDriver = null;
        for (Truck truck : trucks) {
            List<Driver> drivers = TransportManager.getAvailableDriversByDateAndLicense(plannedStartDateTime, plannedEndDateTime,
                    truck.getRequiredLicenseType());
            if (drivers != null && !drivers.isEmpty()) {
                selectedTruck = truck;
                selectedDriver = drivers.getFirst();
                break;
            } else {
                Helper.showError("No available drivers found for truck with license type: " + truck.getRequiredLicenseType());
                return false;
            }
        }
        if (selectedTruck == null || selectedDriver == null) {
            Helper.showError("No available trucks or drivers found.");
            return false;
        }
        CreateNewTransportResult createdTransportResult = TransportManager.createTransport(plannedStartDateTime, plannedEndDateTime,
                selectedTruck, selectedDriver, createdTransportRoutesIds);

        if (createdTransportResult.getResultEnum() == CreateNewTransportResultEnum.Success &&
                createdTransportResult.getCreatedTransportId() > 0) {
            mockDataLoaded = true;
            return true;
        }
        return false;
    }
}
