package Tests.transport;

import Auxiliary.Enums.Models.LicenseTypeEnum;
import Auxiliary.Enums.Models.TransportStatusEnum;
import Auxiliary.Helper;
import DB.DBManager;
import Models.DBModels.suppliers.Supplier;
import Models.DBModels.transport.*;
import Tests.losPollosAnnotations.UseTestingDatabase;
import Tests.losPollosAnnotations.UseTestingDatabaseExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(UseTestingDatabaseExtension.class)
@TestMethodOrder(MethodOrderer.Random.class)
public class DBTests {

    @UseTestingDatabase
    @Test
    public void testCreateDriver() {
        _testCreateDriver(false);
    }

    private int _testCreateDriver(boolean quitIfExisting) {
        Driver driver = DBManager.getInstance().getDriverController().getDriveByPersonalId("TEST-PERSON-001");
        if (driver != null) {
            if (quitIfExisting) {
                return driver.getId();
            }
            DBManager.getInstance().getDriverController().deleteDriver(driver.getId());
        }
        driver = new Driver(0, "John", "Doe", "TEST-PERSON-001", "TEST-PHONE-001",
                LicenseTypeEnum.B);
        int addedId = DBManager.getInstance().getDriverController().addDriver(driver);
        Driver retrievedDriver = DBManager.getInstance().getDriverController().getDriverById(addedId);
        assertNotNull(retrievedDriver);
        assertEquals("John", retrievedDriver.getFirstName());
        assertEquals("Doe", retrievedDriver.getLastName());
        assertEquals("TEST-PHONE-001", retrievedDriver.getPhoneNumber());
        assertEquals("TEST-PERSON-001", retrievedDriver.getPersonalId());
        assertEquals(LicenseTypeEnum.B, retrievedDriver.getLicenseType());
        return addedId;
    }

    @UseTestingDatabase
    @Test
    public void testUpdateDriver() {
        Driver driver = DBManager.getInstance().getDriverController().getDriveByPersonalId("TEST-PERSON-002");
        if (driver != null) {
            DBManager.getInstance().getDriverController().deleteDriver(driver.getId());
        }
        driver = DBManager.getInstance().getDriverController().getDriveByPersonalId("TEST-PERSON-001");
        if (driver != null) {
            DBManager.getInstance().getDriverController().deleteDriver(driver.getId());
        }
        driver = new Driver(0, "John", "Doe", "TEST-PERSON-001", "TEST-PHONE-001",
                LicenseTypeEnum.B);
        int addedId = DBManager.getInstance().getDriverController().addDriver(driver);
        Driver retrievedDriver = DBManager.getInstance().getDriverController().getDriverById(addedId);
        assertNotNull(retrievedDriver);

        retrievedDriver.setFirstName("Updated");
        retrievedDriver.setLastName("Driver");
        retrievedDriver.setPhoneNumber("TEST-PHONE-002");
        retrievedDriver.setPersonalId("TEST-PERSON-002");
        boolean result = DBManager.getInstance().getDriverController().updateDriver(retrievedDriver, addedId);
        assertTrue(result);
        assertEquals("Updated", retrievedDriver.getFirstName());
        assertEquals("Driver", retrievedDriver.getLastName());
        assertEquals("TEST-PERSON-002", retrievedDriver.getPersonalId());
        assertEquals("TEST-PHONE-002", retrievedDriver.getPhoneNumber());

    }

    @UseTestingDatabase
    @Test
    public void testDeleteDriver() {
        Driver driver = DBManager.getInstance().getDriverController().getDriveByPersonalId("TEST-PERSON-001");
        if (driver != null) {
            DBManager.getInstance().getDriverController().deleteDriver(driver.getId());
        }
        driver = new Driver(0, "John", "Doe", "TEST-PERSON-001", "TEST-PHONE-001",
                LicenseTypeEnum.B);
        int addedId = DBManager.getInstance().getDriverController().addDriver(driver);
        Driver retrievedDriver = DBManager.getInstance().getDriverController().getDriverById(addedId);
        assertNotNull(retrievedDriver);

        boolean result = DBManager.getInstance().getDriverController().deleteDriver(addedId);
        assertTrue(result);
        retrievedDriver = DBManager.getInstance().getDriverController().getDriverById(addedId);
        assertNull(retrievedDriver);
    }

    /**
     * Some tests rely on this test! make sure that at least 3 items are added to the delivery file!!!
     */
    @UseTestingDatabase
    @Test
    public void testCreateDeliveryFile() {
        DeliveryFileItem item1 = new DeliveryFileItem(0, 101, 5.0, 0.0);
        DeliveryFileItem item2 = new DeliveryFileItem(0, 102, 7.0, 0.0);
        DeliveryFileItem item3 = new DeliveryFileItem(0, 200, 13.0, 0.0);
        int addedId = DBManager.getInstance().getDeliveryFileController().addDeliveryFile(new DeliveryFile(0,
                List.of(item1, item2, item3)));
        DeliveryFile deliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(addedId);
        assertNotNull(deliveryFile);
        List<DeliveryFileItem> items = deliveryFile.getDeliveryFileItems();
        assertNotNull(items);
        assertNotNull(items.get(0));
        assertNotNull(items.get(1));
        assertNotNull(items.get(2));
        assertEquals(5.0, items.get(0).getAmount());
        assertEquals(7.0, items.get(1).getAmount());
        assertEquals(13.0, items.get(2).getAmount());
    }

    @UseTestingDatabase
    @Test
    public void testAddItemsToDeliveryFile() {
        DeliveryFileItem item1 = new DeliveryFileItem(0, 101, 5.0, 0.0);
        DeliveryFileItem item2 = new DeliveryFileItem(0, 102, 7.0, 0.0);
        int addedId = DBManager.getInstance().getDeliveryFileController().addDeliveryFile(new DeliveryFile(0,
                List.of(item1, item2)));
        DeliveryFile deliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(addedId);
        assertNotNull(deliveryFile);

        DeliveryFileItem item3 = new DeliveryFileItem(0, 102, 15.2, 0.0);
        boolean result = DBManager.getInstance().getDeliveryFileController().addItemsToDeliveryFile(addedId,
                List.of(item3));
        assertTrue(result);
        deliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(addedId);
        assertNotNull(deliveryFile);
        List<DeliveryFileItem> items = deliveryFile.getDeliveryFileItems();
        assertNotNull(items);
        assertEquals(3, items.size());
        assertEquals(5.0, items.get(0).getAmount());
        assertEquals(7.0, items.get(1).getAmount());
        assertEquals(15.2, items.get(2).getAmount());
    }

    @UseTestingDatabase
    @Test
    public void testRemoveItemsToDeliveryFile() {
        DeliveryFileItem item1 = new DeliveryFileItem(0, 101, 5.0, 0.0);
        DeliveryFileItem item2 = new DeliveryFileItem(0, 102, 7.0, 0.0);
        int addedId = DBManager.getInstance().getDeliveryFileController().addDeliveryFile(new DeliveryFile(0,
                List.of(item1, item2)));
        DeliveryFile deliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(addedId);
        assertNotNull(deliveryFile);
        assertEquals(2, deliveryFile.getDeliveryFileItems().size());

        boolean result = DBManager.getInstance().getDeliveryFileController().removeItemsFromDeliveryFile(addedId,
                List.of(item1));
        assertTrue(result);
        deliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(addedId);
        assertNotNull(deliveryFile);
        List<DeliveryFileItem> items = deliveryFile.getDeliveryFileItems();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(7.0, items.get(0).getAmount());
    }

    @UseTestingDatabase
    @Test
    public void testDeleteDeliveryFile() {
        DeliveryFileItem item1 = new DeliveryFileItem(0, 101, 5.0, 0.0);
        DeliveryFileItem item2 = new DeliveryFileItem(0, 102, 7.0, 0.0);
        int addedId = DBManager.getInstance().getDeliveryFileController().addDeliveryFile(new DeliveryFile(0,
                List.of(item1, item2)));
        DeliveryFile deliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(addedId);
        assertNotNull(deliveryFile);
        boolean result = DBManager.getInstance().getDeliveryFileController().deleteDeliveryFile(addedId);
        assertTrue(result);
        deliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(addedId);
        assertNull(deliveryFile);
    }

    @UseTestingDatabase
    @Test
    public void testCreateBranch() {
        _testCreateBranch(false);
    }

    private int _testCreateBranch(boolean quitIfExisting) {
        Branch branch = DBManager.getInstance().getBranchController().getBranchByName("Branch 1");
        if (branch != null) {
            if (quitIfExisting) {
                return branch.getId();
            }
            DBManager.getInstance().getBranchController().deleteBranch(branch.getId());
        }
        branch = new Branch(0, "Branch 1", "Test Address 1", "TEST-PHONE-101");
        int addedId = DBManager.getInstance().getBranchController().addBranch(branch);
        Branch retrievedBranch = DBManager.getInstance().getBranchController().getBranchById(addedId);
        assertNotNull(retrievedBranch);

        assertEquals("Branch 1", retrievedBranch.getSiteName());
        assertEquals("Test Address 1", retrievedBranch.getSiteAddress());
        assertEquals("TEST-PHONE-101", retrievedBranch.getSitePhone());
        return addedId;
    }


    @UseTestingDatabase
    @Test
    public void testUpdateBranch() {
        Branch branch = DBManager.getInstance().getBranchController().getBranchByName("Updated Branch");
        if (branch != null) {
            DBManager.getInstance().getBranchController().deleteBranch(branch.getId());
        }
        branch = new Branch(0, "Branch 2", "Test Address 2", "TEST-PHONE-102");
        int addedId = DBManager.getInstance().getBranchController().addBranch(branch);
        Branch retrievedBranch = DBManager.getInstance().getBranchController().getBranchById(addedId);
        assertNotNull(retrievedBranch);

        retrievedBranch.setSiteName("Updated Branch");
        retrievedBranch.setSitePhone("TEST-PHONE-103");
        boolean result = DBManager.getInstance().getBranchController().updateBranch(retrievedBranch, addedId);
        assertTrue(result);
        assertEquals("Updated Branch", retrievedBranch.getSiteName());
        assertEquals("Test Address 2", retrievedBranch.getSiteAddress());
        assertEquals("TEST-PHONE-103", retrievedBranch.getSitePhone());
    }

    @UseTestingDatabase
    @Test
    public void testDeleteBranch() {
        Branch branch = DBManager.getInstance().getBranchController().getBranchByName("Branch 3");
        if (branch != null) {
            DBManager.getInstance().getBranchController().deleteBranch(branch.getId());
        }
        branch = new Branch(0, "Branch 3", "Test Address 3", "TEST-PHONE-104");
        int addedId = DBManager.getInstance().getBranchController().addBranch(branch);
        Branch retrievedBranch = DBManager.getInstance().getBranchController().getBranchById(addedId);
        assertNotNull(retrievedBranch);

        boolean result = DBManager.getInstance().getBranchController().deleteBranch(addedId);
        assertTrue(result);
        retrievedBranch = DBManager.getInstance().getBranchController().getBranchById(addedId);
        assertNull(retrievedBranch);
    }

    @UseTestingDatabase
    @Test
    public void testCreateSupplier() {
        _testCreateSupplier(false);
    }

    private int _testCreateSupplier(boolean quitIfExisting) {
        Supplier supplier = DBManager.getInstance().getSupplierController().getSupplierByName("Supplier 1");
        if (supplier != null) {
            if (quitIfExisting) {
                return supplier.getId();
            }
            DBManager.getInstance().getSupplierController().deleteSupplier(supplier.getId());
        }
        supplier = new Supplier(0, "Supplier 1", "Test Address 4", "TEST-PHONE-201");
        int addedId = DBManager.getInstance().getSupplierController().addSupplier(supplier);
        Supplier retrievedSupplier = DBManager.getInstance().getSupplierController().getSupplierById(addedId);
        assertNotNull(retrievedSupplier);
        assertEquals("Supplier 1", retrievedSupplier.getSiteName());
        assertEquals("Test Address 4", retrievedSupplier.getSiteAddress());
        assertEquals("TEST-PHONE-201", retrievedSupplier.getSitePhone());
        return addedId;
    }


    @UseTestingDatabase
    @Test
    public void testUpdateSupplier() {
        Supplier supplier = DBManager.getInstance().getSupplierController().getSupplierByName("Updated Supplier");
        if (supplier != null) {
            DBManager.getInstance().getSupplierController().deleteSupplier(supplier.getId());
        }
        supplier = new Supplier(0, "Supplier 2", "Test Address 5", "TEST-PHONE-202");
        int addedId = DBManager.getInstance().getSupplierController().addSupplier(supplier);
        Supplier retrievedSupplier = DBManager.getInstance().getSupplierController().getSupplierById(addedId);
        assertNotNull(retrievedSupplier);

        retrievedSupplier.setSiteName("Updated Supplier");
        retrievedSupplier.setSiteAddress("Test Address 6");
        retrievedSupplier.setSitePhone("TEST-PHONE-203");
        boolean result = DBManager.getInstance().getSupplierController().updateSupplier(retrievedSupplier, addedId);
        assertTrue(result);
        assertEquals("Updated Supplier", retrievedSupplier.getSiteName());
        assertEquals("Test Address 6", retrievedSupplier.getSiteAddress());
        assertEquals("TEST-PHONE-203", retrievedSupplier.getSitePhone());
    }

    @UseTestingDatabase
    @Test
    public void testDeleteSupplier() {
        Supplier supplier = DBManager.getInstance().getSupplierController().getSupplierByName("Supplier 3");
        if (supplier != null) {
            DBManager.getInstance().getSupplierController().deleteSupplier(supplier.getId());
        }
        supplier = new Supplier(0, "Supplier 3", "delete address 1", "555-054-78974");
        int addedId = DBManager.getInstance().getSupplierController().addSupplier(supplier);
        Supplier retrievedSupplier = DBManager.getInstance().getSupplierController().getSupplierById(addedId);
        assertNotNull(retrievedSupplier);

        boolean result = DBManager.getInstance().getSupplierController().deleteSupplier(addedId);
        assertTrue(result);
        retrievedSupplier = DBManager.getInstance().getSupplierController().getSupplierById(addedId);
        assertNull(retrievedSupplier);
    }

    @UseTestingDatabase
    @Test
    public void testCreateTransportRoute() {
        _testCreateTransportRoute();
    }

    private int _testCreateTransportRoute() {
        //We assume that these function leave a correct creation of branch, supplier, delivery file
        // so that a new delivery file is created, but an ALREADY EXISTING branch and supplier are used
        _testCreateBranch(true);
        _testCreateSupplier(true);
        testCreateDeliveryFile();

        List<Integer> deliveryFileIds = DBManager.getInstance().getDeliveryFileController().getAllIds();
        assertFalse(deliveryFileIds.isEmpty(), "No delivery files found");
        int deliveryFileId = deliveryFileIds.getLast();// we use a one to one, so we take the last one(must be newly created)!

        List<Integer> branchIds = DBManager.getInstance().getBranchController().getAllIds();
        assertFalse(branchIds.isEmpty(), "No branches found");
        int branchId = branchIds.getFirst();

        List<Integer> supplierIds = DBManager.getInstance().getSupplierController().getAllIds();
        assertFalse(supplierIds.isEmpty(), "No suppliers found");
        int supplierId = supplierIds.getFirst();

        DeliveryFile deliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(deliveryFileId);
        assertNotNull(deliveryFile, "Delivery file not found");
        Branch branch = DBManager.getInstance().getBranchController().getBranchById(branchId);
        assertNotNull(branch, "Branch not found");
        Supplier supplier = DBManager.getInstance().getSupplierController().getSupplierById(supplierId);
        assertNotNull(supplier, "Supplier not found");

        // Create a new transport route
        int addedId = DBManager.getInstance().getTransportRouteController().addTransportRoute(
                new TransportRoute(0, TransportStatusEnum.PENDING, deliveryFile, supplier, branch,
                        "John Doe", "555-1234", "Jane Smith",
                        "555-5678", 0.0));
        assertTrue(addedId > 0, "Failed to add transport route");
        TransportRoute transportRoute = DBManager.getInstance().getTransportRouteController().getTransportRouteById(addedId);
        assertNotNull(transportRoute, "Transport route not found");
        assertEquals(TransportStatusEnum.PENDING, transportRoute.getStatus(), "Transport route status mismatch");
        assertEquals(deliveryFileId, transportRoute.getDeliveryFile().getId(), "Delivery file ID mismatch");
        assertEquals(supplierId, transportRoute.getSourceSite().getId(), "Source branch ID mismatch");
        assertEquals(branchId, transportRoute.getDestinationSite().getId(), "Destination supplier ID mismatch");
        assertEquals("John Doe", transportRoute.getSourceContactName(), "Source contact name mismatch");
        assertEquals("555-1234", transportRoute.getSourceContactPhone(), "Source contact phone mismatch");
        assertEquals("Jane Smith", transportRoute.getDestinationContactName(), "Destination contact name mismatch");
        assertEquals("555-5678", transportRoute.getDestinationContactPhone(), "Destination contact phone mismatch");
        assertEquals(0.0, transportRoute.getRecordedWeightAtSite(), "Recorded weight mismatch");
        assertEquals(supplier.getSiteName(), transportRoute.getSourceSite().getSiteName(), "Source site name mismatch");
        assertEquals(branch.getSiteName(), transportRoute.getDestinationSite().getSiteName(), "Destination site name mismatch");
        return addedId;
    }

    @UseTestingDatabase
    @Test
    public void testUpdateTransportRoute() {
        // first we must create a route
        int transportRouteId = _testCreateTransportRoute();

        TransportRoute transportRoute = DBManager.getInstance().getTransportRouteController().getTransportRouteById(transportRouteId);
        assertNotNull(transportRoute, "Transport route not found");
        transportRoute.setStatus(TransportStatusEnum.STARTED);
        transportRoute.setSourceContactName("Updated John Doe");
        transportRoute.setSourceContactPhone("555-9876");
        transportRoute.setDestinationContactName("Updated Jane Smith");
        transportRoute.setDestinationContactPhone("555-6543");
        boolean result = DBManager.getInstance().getTransportRouteController().updateTransportRoute(transportRoute, transportRouteId);
        assertTrue(result, "Failed to update transport route");
        TransportRoute updatedTransportRoute = DBManager.getInstance().getTransportRouteController().getTransportRouteById(transportRouteId);
        assertNotNull(updatedTransportRoute, "Updated transport route not found");
        assertEquals(TransportStatusEnum.STARTED, updatedTransportRoute.getStatus(), "Transport route status mismatch");
        assertEquals("Updated John Doe", updatedTransportRoute.getSourceContactName(), "Source contact name mismatch");
        assertEquals("555-9876", updatedTransportRoute.getSourceContactPhone(), "Source contact phone mismatch");
        assertEquals("Updated Jane Smith", updatedTransportRoute.getDestinationContactName(), "Destination contact name mismatch");
        assertEquals("555-6543", updatedTransportRoute.getDestinationContactPhone(), "Destination contact phone mismatch");
    }

    @UseTestingDatabase
    @Test
    public void testDeleteTransportRoute() {
        // first we must create a route
        int transportRouteId = _testCreateTransportRoute();

        TransportRoute transportRoute = DBManager.getInstance().getTransportRouteController().getTransportRouteById(transportRouteId);
        assertNotNull(transportRoute, "Transport route not found");
        boolean result = DBManager.getInstance().getTransportRouteController().deleteTransportRoute(transportRouteId);
        assertTrue(result, "Failed to delete transport route");
        transportRoute = DBManager.getInstance().getTransportRouteController().getTransportRouteById(transportRouteId);
        assertNull(transportRoute, "Transport route still exists after deletion");
    }

    @UseTestingDatabase
    @Test
    public void testCreateTransportArea() {
        _testCreateTransportArea(false);
    }

    private int _testCreateTransportArea(boolean quitIfExists) {
        TransportArea area = DBManager.getInstance().getTransportAreaController().getTransportAreaByAreaName("Area1");
        if (area != null) {
            if (quitIfExists) {
                return area.getId();
            }
            DBManager.getInstance().getTransportAreaController().deleteTransportArea(area.getId());
        }

        // first we must create a branch and a supplier
        int branchId = _testCreateBranch(true);
        int supplierId = _testCreateSupplier(true);
        Branch existingBranch = DBManager.getInstance().getBranchController().getBranchById(branchId);
        assertNotNull(existingBranch, "Branch not found");
        Supplier existingSupplier = DBManager.getInstance().getSupplierController().getSupplierById(supplierId);
        assertNotNull(existingSupplier, "Supplier not found");

        area = new TransportArea(0, "Area1", List.of(existingSupplier), List.of(existingBranch));
        int createdAreaId = DBManager.getInstance().getTransportAreaController().addTransportArea(area);
        assertTrue(createdAreaId > 0, "Failed to create transport area");
        TransportArea createdArea = DBManager.getInstance().getTransportAreaController().getTransportAreaById(createdAreaId);
        assertNotNull(createdArea, "Created transport area not found");
        assertEquals("Area1", createdArea.getAreaName(), "Transport area name mismatch");
        assertEquals(1, createdArea.getSuppliers().size(), "Transport area suppliers size mismatch");
        assertEquals(1, createdArea.getBranches().size(), "Transport area branches size mismatch");
        assertEquals(existingSupplier.getId(), createdArea.getSuppliers().getFirst().getId(),
                "Transport area supplier ID mismatch");
        assertEquals(existingBranch.getId(), createdArea.getBranches().getFirst().getId(),
                "Transport area branch ID mismatch");
        return createdAreaId;
    }

    @UseTestingDatabase
    @Test
    public void testUpdateTransportArea() {
        TransportArea transportArea = DBManager.getInstance().getTransportAreaController().getTransportAreaByAreaName("Updated Area");
        if (transportArea != null) {
            DBManager.getInstance().getTransportAreaController().deleteTransportArea(transportArea.getId());
        }
        // first we must create a route
        int transportAreaId = _testCreateTransportArea(true);

        transportArea = DBManager.getInstance().getTransportAreaController().getTransportAreaById(transportAreaId);
        assertNotNull(transportArea, "Transport area not found");
        transportArea.setAreaName("Updated Area");
        boolean result = DBManager.getInstance().getTransportAreaController().updateTransportArea(transportArea, transportAreaId);
        assertTrue(result, "Failed to update transport area");
        TransportArea updatedTransportArea = DBManager.getInstance().getTransportAreaController().getTransportAreaById(transportAreaId);
        assertNotNull(updatedTransportArea, "Updated transport area not found");
        assertEquals("Updated Area", updatedTransportArea.getAreaName(), "Transport area name mismatch");
    }

    @UseTestingDatabase
    @Test
    public void testDeleteTransportArea() {
        // first we must create a route
        int transportAreaId = _testCreateTransportArea(true);

        TransportArea transportArea = DBManager.getInstance().getTransportAreaController().getTransportAreaById(transportAreaId);
        assertNotNull(transportArea, "Transport area not found");
        boolean result = DBManager.getInstance().getTransportAreaController().deleteTransportArea(transportAreaId);
        assertTrue(result, "Failed to delete transport area");
        transportArea = DBManager.getInstance().getTransportAreaController().getTransportAreaById(transportAreaId);
        assertNull(transportArea, "Transport area still exists after deletion");
    }

    @UseTestingDatabase
    @Test
    public void testCreateTruck() {
        _testCreateTruck(false, false);
    }

    private int _testCreateTruck(boolean quitIfExists, boolean randomPlate) {
        String plate = randomPlate ? "Truck1%s".formatted(Helper.getRandomString(5)) : "Truck1";
        Truck truck = DBManager.getInstance().getTruckController().getTruckByPlate(plate);
        if (truck != null) {
            if (quitIfExists) {
                return truck.getId();
            }
            DBManager.getInstance().getTruckController().deleteTruck(truck.getId());
        }
        truck = new Truck(0, plate, 1000.0, 2000.0, LicenseTypeEnum.C, "Model X");
        int addedId = DBManager.getInstance().getTruckController().addTruck(truck);
        Truck retrievedTruck = DBManager.getInstance().getTruckController().getTruckById(addedId);
        assertNotNull(retrievedTruck);
        assertEquals(plate, retrievedTruck.getPlate());
        assertEquals(1000.0, retrievedTruck.getNetWeight());
        assertEquals(2000.0, retrievedTruck.getMaxWeight());
        assertEquals(LicenseTypeEnum.C, retrievedTruck.getRequiredLicenseType());
        return addedId;
    }

    @UseTestingDatabase
    @Test
    public void testUpdateTruck() {
        Truck truck = DBManager.getInstance().getTruckController().getTruckByPlate("XYZ789");
        if (truck != null) {
            DBManager.getInstance().getTruckController().deleteTruck(truck.getId());
        }
        truck = DBManager.getInstance().getTruckController().getTruckByPlate("UpdatedXYZ789");
        if (truck != null) {
            DBManager.getInstance().getTruckController().deleteTruck(truck.getId());
        }
        truck = new Truck(0, "XYZ789", 1500.0, 2500.0, LicenseTypeEnum.B, "Model Y");
        int addedId = DBManager.getInstance().getTruckController().addTruck(truck);
        Truck retrievedTruck = DBManager.getInstance().getTruckController().getTruckById(addedId);
        assertNotNull(retrievedTruck);

        retrievedTruck.setPlate("UpdatedXYZ789");
        retrievedTruck.setNetWeight(1600.0);
        retrievedTruck.setMaxWeight(2600.0);
        retrievedTruck.setRequiredLicenseType(LicenseTypeEnum.CPlusE);
        boolean result = DBManager.getInstance().getTruckController().updateTruck(retrievedTruck, addedId);
        assertTrue(result);
        assertEquals("UpdatedXYZ789", retrievedTruck.getPlate());
        assertEquals(1600.0, retrievedTruck.getNetWeight());
        assertEquals(2600.0, retrievedTruck.getMaxWeight());
        assertEquals(LicenseTypeEnum.CPlusE, retrievedTruck.getRequiredLicenseType());
    }

    @UseTestingDatabase
    @Test
    public void testDeleteTruck() {
        Truck truck = DBManager.getInstance().getTruckController().getTruckByPlate("DeleteXYZ789");
        if (truck != null) {
            DBManager.getInstance().getTruckController().deleteTruck(truck.getId());
        }
        truck = new Truck(0, "DeleteXYZ789", 1500.0, 2500.0, LicenseTypeEnum.B, "Model Y");
        int addedId = DBManager.getInstance().getTruckController().addTruck(truck);
        Truck retrievedTruck = DBManager.getInstance().getTruckController().getTruckById(addedId);
        assertNotNull(retrievedTruck);

        boolean result = DBManager.getInstance().getTruckController().deleteTruck(addedId);
        assertTrue(result);
        retrievedTruck = DBManager.getInstance().getTruckController().getTruckById(addedId);
        assertNull(retrievedTruck);
    }

    @UseTestingDatabase
    @Test
    public void testCreateTransportIssueLog() {
        _testCreateTransportIssueLog();
    }

    private int _testCreateTransportIssueLog() {
        // first we must create a route
        int transportRouteId = _testCreateTransportRoute();
        int createdTruckId = _testCreateTruck(true, false);
        Truck truck = DBManager.getInstance().getTruckController().getTruckById(createdTruckId);
        assertNotNull(truck, "Truck not found");
        TransportRoute transportRoute = DBManager.getInstance().getTransportRouteController().getTransportRouteById(transportRouteId);
        assertNotNull(transportRoute, "Transport route not found");
        List<DeliveryFileItem> items = transportRoute.getDeliveryFile().getDeliveryFileItems();

        //simulate unloading some items:
        assertTrue(items.size() >= 3, "Delivery file items size mismatch!!");

        // we must create new file items!
        List<DeliveryFileItem> unloadedItems = Stream.of(items.get(0), items.get(1)).map(DeliveryFileItem::new).toList();
        DeliveryFile unloadedDeliveryFile = new DeliveryFile(0, unloadedItems);
        int addedDeliveryFile = DBManager.getInstance().getDeliveryFileController().addDeliveryFile(unloadedDeliveryFile);
        assertTrue(addedDeliveryFile > 0, "Failed to add delivery file");
        unloadedDeliveryFile = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(addedDeliveryFile);
        assertNotNull(unloadedDeliveryFile, "Unloaded delivery file not found");

        TransportIssueLog issueLog = new TransportIssueLog(0, truck, null, transportRoute, unloadedDeliveryFile);
        int addedId = DBManager.getInstance().getTransportIssueLogController().addTransportIssueLog(issueLog);
        assertTrue(addedId > 0, "Failed to add transport issue log");
        TransportIssueLog retrievedIssueLog = DBManager.getInstance().getTransportIssueLogController().getTransportIssueLogById(addedId);
        assertNotNull(retrievedIssueLog, "Transport issue log not found");
        assertEquals(truck.getId(), retrievedIssueLog.getCurrentTruck().getId(), "Current truck ID mismatch");
        assertEquals(transportRoute.getId(), retrievedIssueLog.getRelatedTransportRoute().getId(),
                "Related transport route ID mismatch");
        assertEquals(unloadedDeliveryFile.getId(), retrievedIssueLog.getDeliveryFileOfUnloadedItems().getId(),
                "Delivery file ID mismatch");
        assertEquals(2, retrievedIssueLog.getDeliveryFileOfUnloadedItems().getDeliveryFileItems().size(),
                "Delivery file items size mismatch");
        for (DeliveryFileItem item : retrievedIssueLog.getDeliveryFileOfUnloadedItems().getDeliveryFileItems()) {
            assertTrue(unloadedItems.stream().anyMatch(i -> i.getId() == item.getId()
                            && i.getAmount() == item.getAmount() && i.getStockItemId() == item.getStockItemId()),
                    "Unloaded item ID mismatch");
        }
        return addedId;
    }

    @UseTestingDatabase
    @Test
    public void testDeleteTransportIssueLog() {
        int transportIssueLogId = _testCreateTransportIssueLog();

        TransportIssueLog issueLog = DBManager.getInstance().getTransportIssueLogController().getTransportIssueLogById(transportIssueLogId);
        assertNotNull(issueLog, "Transport issue log not found");
        boolean result = DBManager.getInstance().getTransportIssueLogController().deleteTransportIssueLog(transportIssueLogId);
        assertTrue(result, "Failed to delete transport issue log");
        issueLog = DBManager.getInstance().getTransportIssueLogController().getTransportIssueLogById(transportIssueLogId);
        assertNull(issueLog, "Transport issue log still exists after deletion");
    }

    @UseTestingDatabase
    @Test
    public void testUpdateTransportIssueLog() {
        Truck newTruck = DBManager.getInstance().getTruckController().getTruckByPlate("tempNewTruck");
        if (newTruck != null) {
            DBManager.getInstance().getTruckController().deleteTruck(newTruck.getId());
        }

        int transportIssueLogId = _testCreateTransportIssueLog();

        TransportIssueLog issueLog = DBManager.getInstance().getTransportIssueLogController().getTransportIssueLogById(transportIssueLogId);
        assertNotNull(issueLog, "Transport issue log not found");

        newTruck = new Truck(0, "tempNewTruck", 1200.0, 2200.0, LicenseTypeEnum.C, "Model Y");
        int newTruckId = DBManager.getInstance().getTruckController().addTruck(newTruck);
        assertTrue(newTruckId > 0, "Failed to add new truck");
        newTruck = DBManager.getInstance().getTruckController().getTruckById(newTruckId);
        assertNotNull(newTruck, "New truck not found");

        issueLog.setNewTruck(newTruck);
        boolean result = DBManager.getInstance().getTransportIssueLogController().updateTransportIssueLog(issueLog, transportIssueLogId);
        assertTrue(result, "Failed to update transport issue log");
        TransportIssueLog updatedIssueLog = DBManager.getInstance().getTransportIssueLogController().getTransportIssueLogById(transportIssueLogId);
        assertNotNull(updatedIssueLog, "Updated transport issue log not found");
        assertEquals(newTruck.getId(), updatedIssueLog.getNewTruck().getId(), "New truck ID mismatch");
    }

    @UseTestingDatabase
    @Test
    public void testCreateNewTransport() {
        _testCreateNewTransport();
    }

    private int _testCreateNewTransport() {
        LocalDateTime plannedStartDateTime = Helper.stringToDateTime("2023-10-01 10:00");
        LocalDateTime plannedEndDateTime = Helper.stringToDateTime("2023-10-01 16:00");
        int createdRouteOne = _testCreateTransportRoute();
        int createdRouteTwo = _testCreateTransportRoute();
        int createdTruck = _testCreateTruck(true, false);
        int createdDriver = _testCreateDriver(true);

        TransportRoute transportRouteOne = DBManager.getInstance().getTransportRouteController().getTransportRouteById(createdRouteOne);
        assertNotNull(transportRouteOne, "Transport route one not found");
        TransportRoute transportRouteTwo = DBManager.getInstance().getTransportRouteController().getTransportRouteById(createdRouteTwo);
        assertNotNull(transportRouteTwo, "Transport route two not found");
        Truck truck = DBManager.getInstance().getTruckController().getTruckById(createdTruck);
        assertNotNull(truck, "Truck not found");
        Driver driver = DBManager.getInstance().getDriverController().getDriverById(createdDriver);
        assertNotNull(driver, "Driver not found");

        Transport transport = new Transport(0, plannedStartDateTime, plannedEndDateTime, TransportStatusEnum.PENDING, truck, driver,
                List.of(transportRouteOne, transportRouteTwo), null, null, null);
        int addedId = DBManager.getInstance().getTransportController().addTransport(transport);
        assertTrue(addedId > 0, "Failed to add transport");
        assertNotNull(DBManager.getInstance().getTransportController().getTransportById(addedId),
                "Transport not found");
        Transport retrievedTransport = DBManager.getInstance().getTransportController().getTransportById(addedId);
        assertNotNull(retrievedTransport, "Retrieved transport not found");
        assertEquals(plannedStartDateTime, retrievedTransport.getPlannedStartDateTime(), "Planned date time mismatch");
        assertEquals(plannedEndDateTime, retrievedTransport.getPlannedEndDateTime(), "Planned end date time mismatch");
        assertEquals(TransportStatusEnum.PENDING, retrievedTransport.getStatus(), "Transport status mismatch");
        assertEquals(truck.getId(), retrievedTransport.getTruck().getId(), "Truck ID mismatch");
        assertEquals(driver.getId(), retrievedTransport.getDriver().getId(), "Driver ID mismatch");
        assertEquals(2, retrievedTransport.getTransportRoutes().size(), "Transport routes size mismatch");
        assertEquals(transportRouteOne.getId(), retrievedTransport.getTransportRoutes().getFirst().getId(),
                "Transport route one ID mismatch");
        assertEquals(transportRouteTwo.getId(), retrievedTransport.getTransportRoutes().getLast().getId(),
                "Transport route two ID mismatch");
        return addedId;
    }

    @UseTestingDatabase
    @Test
    public void testEditTransport() {
        int createdId = _testCreateNewTransport();
        Transport transport = DBManager.getInstance().getTransportController().getTransportById(createdId);
        assertNotNull(transport, "Transport not found");
        int createdNewRouteId = _testCreateTransportRoute();
        TransportRoute newRoute = DBManager.getInstance().getTransportRouteController().getTransportRouteById(createdNewRouteId);
        assertNotNull(newRoute, "New transport route not found");
        int createdLogId = _testCreateTransportIssueLog();
        TransportIssueLog issueLog = DBManager.getInstance().getTransportIssueLogController().getTransportIssueLogById(createdLogId);
        assertNotNull(issueLog, "Transport issue log not found");
        issueLog.setRelatedTransportRoute(newRoute);
        boolean result = DBManager.getInstance().getTransportIssueLogController().updateTransportIssueLog(issueLog, createdLogId);
        assertTrue(result, "Failed to update transport issue log");
        TransportIssueLog updatedIssueLog = DBManager.getInstance().getTransportIssueLogController().getTransportIssueLogById(createdLogId);
        assertNotNull(updatedIssueLog, "Updated transport issue log not found");
        assertEquals(newRoute.getId(), updatedIssueLog.getRelatedTransportRoute().getId(),
                "Updated transport route ID mismatch");
        int createdNewTruckResult = _testCreateTruck(false, true);
        Truck newTruck = DBManager.getInstance().getTruckController().getTruckById(createdNewTruckResult);
        assertNotNull(newTruck, "New truck not found");

        LocalDateTime newPlannedDateTime = Helper.stringToDateTime("2023-10-02 12:00");
        transport.setPlannedStartDateTime(newPlannedDateTime);
        transport.setStatus(TransportStatusEnum.STARTED);
        transport.getTransportRoutes().add(newRoute);
        transport.setTransportIssueLogs(List.of(updatedIssueLog));
        transport.setTruck(newTruck);

        result = DBManager.getInstance().getTransportController().updateTransport(transport, createdId);
        assertTrue(result, "Failed to update transport");
        Transport updatedTransport = DBManager.getInstance().getTransportController().getTransportById(createdId);
        assertNotNull(updatedTransport, "Updated transport not found");
        assertEquals(newPlannedDateTime, updatedTransport.getPlannedStartDateTime(), "Planned date time mismatch");
        assertEquals(TransportStatusEnum.STARTED, updatedTransport.getStatus(), "Transport status mismatch");
        assertEquals(3, updatedTransport.getTransportRoutes().size(), "Transport routes size mismatch");
        assertEquals(newRoute.getId(), updatedTransport.getTransportRoutes().getLast().getId(),
                "New transport route ID mismatch");
        assertEquals(1, updatedTransport.getTransportIssueLogs().size(), "Transport issue logs size mismatch");
        assertEquals(updatedIssueLog.getId(), updatedTransport.getTransportIssueLogs().getFirst().getId(),
                "Updated transport issue log ID mismatch");
    }

    @UseTestingDatabase
    @Test
    public void testDeleteTransport() {
        int createdId = _testCreateNewTransport();
        Transport transport = DBManager.getInstance().getTransportController().getTransportById(createdId);
        assertNotNull(transport, "Transport not found");

        assertFalse(transport.getTransportRoutes().isEmpty());
        for (TransportRoute route : transport.getTransportRoutes()) {
            assertNotNull(route, "Transport route not found");
            int deliveryFileId = route.getDeliveryFile().getId();
            // we can simply delete the route and due to the one-to-one relation, the delivery file will also be deleted
            assertTrue(DBManager.getInstance().getTransportRouteController().deleteTransportRoute(route.getId()),
                    "Failed to delete transport route");
            TransportRoute r = DBManager.getInstance().getTransportRouteController().getTransportRouteById(route.getId());
            assertNull(r, "Transport route still exists after deletion");
            DeliveryFile df = DBManager.getInstance().getDeliveryFileController().getDeliveryFileById(deliveryFileId);
            assertNull(df, "Delivery file still exists after deletion");
        } //NOTE: in real time - we should also do the same for the transport issue logs

        boolean result = DBManager.getInstance().getTransportController().deleteTransport(createdId);
        assertTrue(result, "Failed to delete transport");
        transport = DBManager.getInstance().getTransportController().getTransportById(createdId);
        assertNull(transport, "Transport still exists after deletion");
    }
}
