package Domain.transport;

import Auxiliary.Enums.Domain.CreateNewTransportResultEnum;
import Auxiliary.Enums.Domain.CreateNewTransportRouteResultEnum;
import Auxiliary.Enums.Models.LicenseTypeEnum;
import Auxiliary.Enums.Models.TransportStatusEnum;
import Auxiliary.Enums.Models.visitSiteEnum;
import Auxiliary.Helper;
import DB.DBManager;
import DB.interfaces.transport.IDeliveryFileController;
import DB.interfaces.transport.ITransportController;
import DB.interfaces.transport.ITransportIssueLogController;
import DB.interfaces.transport.ITransportRouteController;
import Domain.stock.StockManager;
import Domain.suppliers.SuppliersManager;
import Models.DBModels.Employees.ARole;
import Models.DBModels.Employees.Employee;
import Models.DBModels.Employees.Shift;
import Models.DBModels.suppliers.Supplier;
import Models.DBModels.transport.*;
import Models.DTOs.ItemsAtSupplier;
import Models.DTOs.StockItem;
import Models.DTOs.transport.CreateNewTransportResult;
import Models.DTOs.transport.CreateNewTransportRoutesResult;
import Domain.Employees.UpcomingShiftsManagerCLS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The TransportManager class is responsible for managing transport-related operations.
 */
public class TransportManager {
    private static final ITransportController transportController = DBManager.getInstance().getTransportController();
    private static final IDeliveryFileController deliveryFileController = DBManager.getInstance().getDeliveryFileController();
    private static final ITransportRouteController transportRouteController = DBManager.getInstance().getTransportRouteController();
    private static final ITransportIssueLogController transportIssueLogController = DBManager.getInstance().getTransportIssueLogController();

    /**
     * This method is used to create a delivery file.
     *
     * @param requiredItems - the list of required items
     * @return - the ID of the created delivery file, or Helper.UndefinedId if failed
     */
    public static int createDeliveryFile(List<StockItem> requiredItems) {
        if (requiredItems == null || requiredItems.isEmpty()) {
            Helper.showError("No items to create a delivery file.");
            return Helper.UndefinedId;
        }
        List<DeliveryFileItem> addedItems = new ArrayList<>();
        for (StockItem item : requiredItems) {
            if (item.getAmount() <= 0) {
                Helper.showError("Invalid amount for item: " + item.getItemName());
            } else {
                addedItems.add(new DeliveryFileItem(Helper.UndefinedId, item.getStockItemId(), item.getAmount(), 0));
            }
        }
        return deliveryFileController.addDeliveryFile(new DeliveryFile(Helper.UndefinedId, addedItems));
    }


    /**
     * This method is used to delete a delivery file and its items.
     *
     * @param deliveryFileId - the ID of the delivery file to be deleted
     * @return - true if the delivery file was deleted successfully, false otherwise
     */
    private static boolean deleteDeliveryFile(int deliveryFileId) {
        if (deliveryFileId <= 0) {
            Helper.showError("Cant delete delivery file with id <= 0");
            return false;
        }
        DeliveryFile deliveryFile = deliveryFileController.getDeliveryFileById(deliveryFileId);
        if (deliveryFile == null) {
            Helper.showError("Cant delete delivery file with id=" + deliveryFileId + ", it does not exist");
            return false;
        }
        return deliveryFileController.deleteDeliveryFile(deliveryFileId);
    }

    /**
     * This method is used to create a transport route.
     *
     * @param branchId   - the ID of the branch - assumes ID is correct!!!
     * @param supplierId - the ID of the supplier - assumes ID is correct!!!
     * @param items      - the list of items to be delivered
     * @return - the ID of the created transport route, or Helper.UndefinedId if failed
     */
    public static int createTransportRoute(int branchId, int supplierId, List<StockItem> items) {
        if (items == null || items.isEmpty()) {
            Helper.showError("No items to create a transport route.");
            return Helper.UndefinedId;
        }
        int deliveryFileId = createDeliveryFile(items);
        if (deliveryFileId <= Helper.UndefinedId) {
            Helper.showError("Failed to create delivery file for transport route.");
            return Helper.UndefinedId;
        }
        Branch b = BranchesManager.getBranchById(branchId);
        if (b == null) {
            Helper.showError("Invalid branch ID: " + branchId);
            return Helper.UndefinedId;
        }
        Supplier s = SuppliersManager.getSupplierById(supplierId);
        if (s == null) {
            Helper.showError("Invalid supplier ID: " + supplierId);
            return Helper.UndefinedId;
        }
        TransportRoute transportRoute = new TransportRoute(Helper.UndefinedId, TransportStatusEnum.PENDING,
                deliveryFileController.getDeliveryFileById(deliveryFileId),
                s, b, SuppliersManager.getCurrentContactNameOfSupplier(supplierId),
                SuppliersManager.getCurrentContactPhoneOfSupplier(supplierId),
                BranchesManager.getCurrentContactNameOfBranch(branchId),
                BranchesManager.getCurrentContactPhoneOfBranch(branchId), 0);
        return transportRouteController.addTransportRoute(transportRoute);
    }

    /**
     * This method is used to create new transport routes for the given branch and required items.
     *
     * @param branchId      - the ID of the branch
     * @param requiredItems - the list of required items
     * @return - a list of created transport route IDs
     */
    public static CreateNewTransportRoutesResult createNewTransportRoutes(int branchId, List<StockItem> requiredItems) {
        List<TransportRoute> routes = new ArrayList<>();
        List<StockItem> items = new ArrayList<>(requiredItems);
        Map<Integer, List<StockItem>> suppliersAndItems = new HashMap<>();
        List<TransportArea> transportAreas = DBManager.getInstance().getTransportAreaController().getAllTransportAreasByBranchId(branchId);
        if (transportAreas != null && !transportAreas.isEmpty()) {
            Set<Supplier> suppliers = new HashSet<>();
            for (TransportArea area : transportAreas) {
                suppliers.addAll(area.getSuppliers());
            }
            if (!suppliers.isEmpty()) {
                for (Supplier s : suppliers) {
                    List<ItemsAtSupplier> itemsAtSupplier = SuppliersManager.mockCanSupplierProvideRequestedItems(s.getId(), items);
                    if (itemsAtSupplier.isEmpty()) {
                        // No items can be provided by this supplier
                        continue;
                    }
                    List<StockItem> providedItems = new ArrayList<>();
                    for (ItemsAtSupplier itemAS : itemsAtSupplier) {
                        items.stream().filter(item -> item.getStockItemId() == itemAS.getStockItemId()).findFirst().ifPresent(foundItem -> {
                            providedItems.add(foundItem);
                            items.remove(foundItem);
                        });
                    }
                    if (!suppliersAndItems.containsKey(s.getId())) {
                        suppliersAndItems.put(s.getId(), providedItems);
                    } else {
                        suppliersAndItems.get(s.getId()).addAll(providedItems);
                    }
                }
                if (items != null && !items.isEmpty()) {
                    //todo handle general search for unprovided items...
                }
            } else {
                // todo handle general search...
            }
        } else {
            // todo handle general search...
        }

        boolean someItemsUnprovided = !items.isEmpty();
        for (Map.Entry<Integer, List<StockItem>> entry : suppliersAndItems.entrySet()) {
            int supplierId = entry.getKey();
            List<StockItem> itemsAtSupplier = entry.getValue();
            int routeId = createTransportRoute(branchId, supplierId, itemsAtSupplier);
            if (routeId > Helper.UndefinedId) {
                TransportRoute createdRoute = transportRouteController.getTransportRouteById(routeId);
                if (createdRoute == null) {
                    Helper.showError("Failed to create transport route with ID: " + routeId);
                    continue;
                }
                routes.add(createdRoute);
            } else {
                Helper.showError("Failed to create transport route for supplier: " + supplierId);
            }
        }
        return new CreateNewTransportRoutesResult(routes,
                someItemsUnprovided ? CreateNewTransportRouteResultEnum.SomeItemsUnprovided :
                        CreateNewTransportRouteResultEnum.Success,
                items);
    }


    /**
     * This method is used to delete transport routes.
     *
     * @param routes - the list of transport routes to be deleted
     * @return - true if the transport routes were deleted successfully, false otherwise
     */
    public static boolean deleteTransportRoutes(List<TransportRoute> routes) {
        if (routes == null || routes.isEmpty()) {
            Helper.showError("Cant delete transport routes, they do not exist");
            return false;
        } else {
            for (TransportRoute route : routes) {
                if (route == null) {
                    Helper.showError("Cant delete transport route it does not exist");
                    continue;
                }
                if (!transportRouteController.deleteTransportRoute(route.getId())) {
                    Helper.showError("Cant delete transport with id=" + route.getId() + ", failed to delete transport route");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method is used to delete transport by ID.
     *
     * @param transportId - the ID of the transport to be deleted
     * @return - true if the transport was deleted successfully, false otherwise
     */
    public static boolean deleteTransportById(int transportId) {
        if (transportId <= 0) {
            Helper.showError("Cant delete transport with id <= 0");
            return false;
        }
        Transport transport = transportController.getTransportById(transportId);
        if (transport == null) {
            Helper.showError("Cant delete transport with id=" + transportId + ", it does not exist");
            return false;
        }
        for (TransportRoute route : transport.getTransportRoutes()) {
            if (route == null) {
                Helper.showError("Cant delete transport route it does not exist");
                continue;
            }
            if (!transportRouteController.deleteTransportRoute(route.getId())) {
                Helper.showError("Cant delete transport with id=" + transportId + ", failed to delete transport route");
                return false;
            }
        }
        if (!transportController.deleteTransport(transportId)) {
            Helper.showError("Cant delete transport with id=" + transportId + ", failed to delete transport");
            return false;
        }
        return true;
    }


    /**
     * Method return ONLY the available trucks for the given dates.
     * Available trucks are those that are not assigned to any transport on the given dates.
     *
     * @param targetPlannedStartDateTime - the date to check for available trucks
     * @param targetPlannedEndDateTime   - the date to check for available trucks
     * @return - a list of available trucks
     */
    public static List<Truck> getAvailableTrucksByDate(LocalDateTime targetPlannedStartDateTime,
                                                       LocalDateTime targetPlannedEndDateTime) {
        List<Truck> availableTrucks = TrucksManager.getAllTrucks();
        List<Transport> transports = getAllTransports();

        // Find all truck IDs that are already assigned to transport on this date frame
        Set<Integer> busyTruckIds = transports.stream()
                .filter(t -> {
                    TransportStatusEnum status = t.getStatus();
                    boolean isActive = status == TransportStatusEnum.PENDING || status == TransportStatusEnum.STARTED;
                    boolean currentStartInBetweenFrame = Helper.isDateTimeInBetween(t.getPlannedStartDateTime(),
                            targetPlannedStartDateTime, targetPlannedEndDateTime);
                    boolean currentEndInBetweenFrame = Helper.isDateTimeInBetween(t.getPlannedEndDateTime(),
                            targetPlannedStartDateTime, targetPlannedEndDateTime);

                    return isActive && (currentStartInBetweenFrame || currentEndInBetweenFrame ||
                            (t.getPlannedStartDateTime().isBefore(targetPlannedStartDateTime) &&
                                    /*This is an edge case if the entire trucks frame is longer than the target planned start+end*/
                                    t.getPlannedEndDateTime().isAfter(targetPlannedEndDateTime)));
                })
                .map(Transport::getTruckId)
                .collect(Collectors.toSet());

        // Filter out busy trucks
        return availableTrucks.stream()
                .filter(truck -> !busyTruckIds.contains(truck.getId()))
                .collect(Collectors.toList());
    }


    /**
     * Method return ONLY the available drivers for the given date.
     * Available drivers are those that are not assigned to any shifts on the given dates.
     *
     * @param targetStartDateTime - the date to check for available drivers
     * @param targetEndDateTime   - the date to check for available drivers
     * @return - a list of available drivers
     */
    public static List<Driver> getAvailableDriversByDateAndLicense(LocalDateTime targetStartDateTime,
                                                                   LocalDateTime targetEndDateTime,
                                                                   LicenseTypeEnum truckLicenseType) {
        List<Driver> availableDriversWithProperLicense = DriversManager.getAllDrivers().stream()
                .filter(driver -> Helper.isLicenseTypeValidPerTruck(driver.getLicenseType(), truckLicenseType))
                .toList();
        if (availableDriversWithProperLicense.isEmpty()) {
            Helper.showError("No drivers with the required license type found.");
            return new ArrayList<>();
        }
        Shift shift = UpcomingShiftsManagerCLS.getShiftByDate(targetStartDateTime.toLocalDate());
        if (shift == null) {
            Helper.showError("No shift found for the given date.");
            return new ArrayList<>();
        }
        if(!shift.getAssignedEmployees().containsValue("Driver")) { //TODO assigned employees may not be empty but still have no drivers - oki doki
            Helper.showError("No available drivers for the given shift.");
            return new ArrayList<>();
        }
        Map<Employee, String> availableEmployees = shift.getAssignedEmployees();
        List<Driver> availableDrivers = new ArrayList<>();
        for (Employee employee : availableEmployees.keySet()) {
            if (employee.getRolesList().stream().anyMatch(role -> role.getRole().equals("Driver"))) {
                if (availableDriversWithProperLicense.stream()
                        .anyMatch(driver -> driver.getPersonalId().equals(employee.getEmployeeID()))) {
                    Driver d = availableDriversWithProperLicense.stream().anyMatch(driver -> driver.getPersonalId().equals(employee.getEmployeeID()))
                            ? availableDriversWithProperLicense.stream().filter(driver -> driver.getPersonalId().equals(employee.getEmployeeID())).findFirst().orElse(null)
                            : null;
                    availableDrivers.add(d);
                }

            }
        }
        return /*TODO @ethan link with employees module and check for each specific driver that is not blocked due to a shift by start and end -DONE*/
                availableDrivers;
    }


    /**
     * This method is used to create transport.
     *
     * @param plannedStartDateTime - the planned start date and time for the transport
     * @param plannedEndDateTime   - the planned end date and time for the transport
     * @param truck                - the truck to be used for the transport
     * @param driver               - the driver for the transport
     * @param transportRoutes      - the list of transport routes for the transport
     * @return - the ID of the created transport, or Helper.UndefinedId if failed
     */
    public static CreateNewTransportResult createTransport(LocalDateTime plannedStartDateTime, LocalDateTime plannedEndDateTime, Truck truck, Driver driver, List<TransportRoute> transportRoutes) {
        if (plannedStartDateTime == null || truck == null || driver == null || transportRoutes == null
                || transportRoutes.isEmpty()) {
            Helper.showError("Invalid input for creating transport.");
            return new CreateNewTransportResult(Helper.UndefinedId,
                    CreateNewTransportResultEnum.InvalidInput);
        }
        //todo @ethan link with employees by branch name(extracted from the routes) and the plannedEndDateTime!
        Shift shift = UpcomingShiftsManagerCLS.getShiftByDate(plannedEndDateTime.toLocalDate());
        if (shift == null) {
            Helper.showError("No shift found for the planned end date.");
            return new CreateNewTransportResult(Helper.UndefinedId,
                    CreateNewTransportResultEnum.StockerUnavailable);
        }
        List<Employee> stockers = shift.getAssignedEmployees().containsValue("Stocker") ?
                shift.getAssignedEmployees().keySet().stream()
                        .filter(employee -> employee.getRolesList().stream()
                                .anyMatch(role -> role.getRole().equals("Stocker")))
                        .toList() : new ArrayList<>();
        if (stockers.isEmpty()) {
            Helper.println("No stocker available at the planned end time.");
            return new CreateNewTransportResult(Helper.UndefinedId,
                    CreateNewTransportResultEnum.StockerUnavailable);
        }
        Transport transport = new Transport(Helper.UndefinedId, plannedStartDateTime, plannedEndDateTime,
                TransportStatusEnum.PENDING, truck, driver, transportRoutes, null, null,
                null);

        int transportId = transportController.addTransport(transport);
        if (transportId <= Helper.UndefinedId) {
            Helper.showError("Failed to create transport.");
            if (deleteTransportRoutes(transportRoutes)) {
                Helper.println("Deleted successfully the transport routes");
            } else {
                Helper.showError("Failed to delete transport routes");
            }
            return new CreateNewTransportResult(Helper.UndefinedId,
                    CreateNewTransportResultEnum.TransportCreationFailed);
        } else {
            for (TransportRoute route : transportRoutes) {
                if (route != null) {
                    APhysicalSite srcSite = route.getSourceSite();
                    DeliveryFile deliveryFile = route.getDeliveryFile();
                    if (srcSite != null) {
                        if (srcSite instanceof Supplier supplier) {
                            List<DeliveryFileItem> deliveryFileItems = deliveryFile.getDeliveryFileItems();
                            List<StockItem> items = new ArrayList<>();
                            List<ItemsAtSupplier> itemsAtSupplier =
                                    SuppliersManager.mockGetSupplierItemsByStockItems(supplier.getId(),
                                            StockManager.mockGetStockItemsByIds(
                                                    deliveryFileItems.stream()
                                                            .map(DeliveryFileItem::getStockItemId)
                                                            .collect(Collectors.toList())));
                            if (!SuppliersManager.mockReserveItemsOfSupplier(supplier.getId(), itemsAtSupplier)) {
                                Helper.showError("Failed to reserve items for supplier: " + supplier.getId());
                            }
                        }
                    }

                }
            }
        }
        return new CreateNewTransportResult(transportId,
                CreateNewTransportResultEnum.Success);
    }


    /**
     * This method is used to get transport by ID.
     *
     * @return - the transport object, or null if not found
     */
    public static Transport getTransportById(int transportId) {
        if (transportId <= Helper.UndefinedId) {
            Helper.showError("Invalid transport ID.");
            return null;
        }
        Transport transport = transportController.getTransportById(transportId);
        if (transport == null) {
            Helper.showError("Transport not found.");
        }
        return transport;
    }

    /**
     * This method is used to get all transports.
     *
     * @return - a list of all transports
     */
    public static List<Transport> getAllTransports() {
        ArrayList<Transport> transports = new ArrayList<>();
        for (Integer id : transportController.getAllIds()) {
            Transport transport = transportController.getTransportById(id);
            if (transport == null) {
                Helper.showError(String.format("Cant get transport with id=%d, it does not exist", id));
            } else {
                transports.add(transport);
            }
        }
        return transports;
    }


    /**
     * This method is used to get a detailed string representation of a delivery file.
     *
     * @param deliveryFile - the delivery file object
     * @return - a detailed string representation of the delivery file
     */
    private static String getDetailedDeliveryFileString(DeliveryFile deliveryFile) {
        if (deliveryFile == null) {
            Helper.showError("Cant get delivery file, it does not exist");
            return null;
        }
        List<DeliveryFileItem> deliveryFileItems = deliveryFile.getDeliveryFileItems();
        if (deliveryFileItems == null || deliveryFileItems.isEmpty()) {
            Helper.showError("cant get delivery file items with id=" + deliveryFile.getId() + ", they do not exist");
            return null;
        }
        List<StockItem> items = StockManager.mockGetStockItemsByIds(deliveryFileItems.stream()
                .map(DeliveryFileItem::getStockItemId)
                .collect(Collectors.toList()));
        if (items.isEmpty()) {
            Helper.showError("cant get delivery file items with id=" + deliveryFile.getId() + ", they do not exist");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Delivery File ID: ").append(deliveryFile.getId()).append("\n");
        sb.append("Items:");
        for (DeliveryFileItem item : deliveryFileItems) {
            items.stream().filter(i -> i.getStockItemId() == item.getStockItemId())
                    .findFirst()
                    .ifPresent(stockItem -> sb.append("\n - ")
                            .append(stockItem.getItemName())
                            .append(": Amount - ")
                            .append(item.getAmount())
                            .append(" Amount Picked Up - ")
                            .append(item.getAmountPickedUp()));
        }
        return sb.toString();
    }


    /**
     * This method is used to get a detailed string representation of a transport route.
     *
     * @param route - the route object
     * @return - a detailed string representation of the transport route
     */
    public static String getDetailedTransportRouteString(TransportRoute route) {
        if (route == null) {
            Helper.showError("Cant get transport route it does not exist");
            return null;
        }
        String deleiveryFileDetailedString = getDetailedDeliveryFileString(route.getDeliveryFile());

        APhysicalSite sourceSite = route.getSourceSite();
        if (sourceSite == null) {
            Helper.showError("Cant get transport route with id=" + route.getId() + ", it does not exist");
            return null;
        }
        APhysicalSite destinationSite = route.getDestinationSite();
        if (destinationSite == null) {
            Helper.showError("Cant get transport route with id=" + route.getId() + ", it does not exist");
            return null;
        }

        return "Transport Route ID: " + route.getId() + "\n" +
                "Status: " + route.getStatus() + "\n" +
                "Source Site: " + sourceSite.toPrettyString() + "\n" +
                "Destination Site: " + destinationSite.toPrettyString() + "\n" +
                "Delivery File: \n" +
                deleiveryFileDetailedString;
    }

//    public static TransportRoute getTransportRouteById(int routeId) {
//        if (routeId <= Helper.UndefinedId) {
//            Helper.showError("Cant get transport route with id <= 0");
//            return null;
//        }
//        TransportRoute route = transportRouteController.getTransportRouteById(routeId);
//        if (route == null) {
//            Helper.showError("Cant get transport route with id=" + routeId + ", it does not exist");
//            return null;
//        }
//        return route;
//    }
//
//    public static DeliveryFile getDeliveryFileById(int deliveryFileId) {
//        if (deliveryFileId <= Helper.UndefinedId) {
//            Helper.showError("Cant get delivery file with id <= 0");
//            return null;
//        }
//        DeliveryFile deliveryFile = deliveryFileController.getDeliveryFileById(deliveryFileId);
//        if (deliveryFile == null) {
//            Helper.showError("Cant get delivery file with id=" + deliveryFileId + ", it does not exist");
//            return null;
//        }
//        return deliveryFile;
//    }

    public static boolean StartTransport(Transport transport, LocalDateTime startDateTime) {
        if (transport.getStatus() == TransportStatusEnum.PENDING) {
            LocalDateTime plannedDateTime = transport.getPlannedStartDateTime();
            if (plannedDateTime == null) {
                Helper.println("Planned date and time is null. Cannot start transport.");
                return false;
            }
            Helper.print("Are you sure you want to start the transport? (yes/no) ");
            String answer = Helper.getUserInputString();
            if (answer == null || answer.isEmpty()) {
                Helper.println("Invalid input. Transport start cancelled.");
                return false;
            }
            if (!answer.equalsIgnoreCase("yes")) {
                Helper.println("Transport start cancelled.");
                return false;
            }
            transport.setStatus(TransportStatusEnum.STARTED);
            transport.setActualStartDatetime(startDateTime);
            Helper.println("Transport started successfully.");
            transportController.updateTransport(transport, transport.getId());
            return true;
        } else {
            Helper.println("Transport is already started or completed. Cannot start again.");
            return false;
        }
    }

    /**
     * This method is used to visit the source site of a transport route.
     *
     * @param transport     - the transport object
     * @param selectedRoute - the selected transport route
     * @return - an enum indicating the result of the visit
     */
    public static visitSiteEnum visitSource(Transport transport, TransportRoute selectedRoute) {
        DeliveryFile deliveryFile = selectedRoute.getDeliveryFile();
        if (deliveryFile == null) {
            Helper.println("Delivery file not found.");
            return visitSiteEnum.FAILED;
        }
        List<DeliveryFileItem> deliveryFileItems = deliveryFile.getDeliveryFileItems();
        if (deliveryFileItems == null || deliveryFileItems.isEmpty()) {
            Helper.println("No items in the delivery file.");
            return visitSiteEnum.FAILED;
        }
        Truck truck = TrucksManager.getTruckById(transport.getTruckId());
        if (truck == null) {
            Helper.println("Truck not found.");
            return visitSiteEnum.FAILED;
        }
        double totalWeight = 0;
        for (DeliveryFileItem item : deliveryFileItems) {
            StockItem stockItem = StockManager.getStockItemById(item.getStockItemId());
            if (stockItem == null) {
                Helper.println("Stock item not found.");
                return visitSiteEnum.FAILED;
            }
            totalWeight += stockItem.getWeight() * item.getAmount();
        }
        if (totalWeight > truck.getMaxWeight() - truck.getNetWeight()) {
            return visitSiteEnum.OVERLOAD;
        }
        if (selectedRoute.getStatus() != TransportStatusEnum.SOURCE_COMPLETED_WITH_ISSUES) {
            selectedRoute.setRecordedWeightAtSite(totalWeight);
            selectedRoute.setStatus(TransportStatusEnum.SOURCE_COMPLETED);
        }
        if (selectedRoute.getStatus() != TransportStatusEnum.SOURCE_COMPLETED) {
            selectedRoute.setRecordedWeightAtSite(totalWeight);
            selectedRoute.setStatus(TransportStatusEnum.SOURCE_COMPLETED_WITH_ISSUES);
        }
        for (DeliveryFileItem item : deliveryFileItems) {
            selectedRoute.getDeliveryFile().getDeliveryFileItems()
                    .stream()
                    .filter(i -> i.getStockItemId() == item.getStockItemId())
                    .findFirst()
                    .ifPresent(i -> {
                        i.setAmountPickedUp(item.getAmount());
                    });
        }
        transportRouteController.updateTransportRoute(selectedRoute, selectedRoute.getId());
        transportController.updateTransport(transport, transport.getId());
        return visitSiteEnum.SUCCESS;
    }

    /**
     * This method is used to visit the destination site.
     *
     * @param transport     - the transport object
     * @param selectedRoute - the selected transport routes
     * @return - an enum indicating the result of the visit
     */
    public static visitSiteEnum visitDestination(Transport transport, List<TransportRoute> selectedRoute) {
        if (selectedRoute == null || selectedRoute.isEmpty()) {
            Helper.println("Selected route is null or empty.");
            return visitSiteEnum.FAILED;
        }
        for (TransportRoute route : selectedRoute) {
            DeliveryFile deliveryFile = deliveryFileController.getDeliveryFileById(route.getDeliveryFile().getId());
            if (deliveryFile == null) {
                Helper.println("Delivery file not found.");
                return visitSiteEnum.FAILED;
            }
            List<DeliveryFileItem> deliveryFileItems = deliveryFile.getDeliveryFileItems();
            if (deliveryFileItems == null || deliveryFileItems.isEmpty()) {
                Helper.println("No items in the delivery file.");
                return visitSiteEnum.FAILED;
            }
            if (route.getStatus() != TransportStatusEnum.DESTINATION_COMPLETED_WITH_ISSUES) {
                route.setStatus(TransportStatusEnum.DESTINATION_COMPLETED);
            }
            if (route.getStatus() != TransportStatusEnum.DESTINATION_COMPLETED) {
                route.setStatus(TransportStatusEnum.DESTINATION_COMPLETED_WITH_ISSUES);
            }
            transportRouteController.updateTransportRoute(route, route.getId());
        }
        transportController.updateTransport(transport, transport.getId());
        return visitSiteEnum.SUCCESS;
    }

    /**
     * This method is used to attach an issue to an existing transport.
     *
     * @param transport     - the transport object
     * @param code          - the code indicating the type of issue
     * @param selectedRoute - the selected transport route
     * @param truckId       - the ID of the truck
     * @param deliveryFile  - the delivery file object
     * @param codeSite      - the code indicating the site type
     * @return - true if the update was successful, false otherwise
     */
    public static boolean AttachIssueToExistingTransport(Transport transport, int code, TransportRoute selectedRoute, int truckId, DeliveryFile deliveryFile, int codeSite) {
        if (selectedRoute == null || transport == null) {
            Helper.println("Selected route is null.");
            return false;
        } else {
            if (codeSite == 0) {
                if (code == 1) {
                    Truck newTruck = TrucksManager.getTruckById(truckId);
                    if (newTruck == null) {
                        Helper.showError("Truck with id " + truckId + " not found.");
                        return false;
                    }
                    TransportIssueLog transportIssueLog = new TransportIssueLog(Helper.UndefinedId, transport.getTruck(),
                            newTruck, selectedRoute, null);
                    int addedLogId = transportIssueLogController.addTransportIssueLog(transportIssueLog);
                    transportIssueLog = transportIssueLogController.getTransportIssueLogById(addedLogId);
                    if (transportIssueLog == null) {
                        Helper.showError("Failed to create transport issue log.");
                        return false;
                    }
                    transport.getTransportIssueLogs().add(transportIssueLog);
                    transport.setTruck(newTruck);
                }
                if (code == 2) {
                    if (deliveryFile == null) {
                        Helper.showError("Delivery file not found.");
                        return false;
                    }
                    TransportIssueLog transportIssueLog = new TransportIssueLog(Helper.UndefinedId, transport.getTruck(),
                            null, selectedRoute, deliveryFile);
                    int addedLogId = transportIssueLogController.addTransportIssueLog(transportIssueLog);
                    transportIssueLog = transportIssueLogController.getTransportIssueLogById(addedLogId);
                    if (transportIssueLog == null) {
                        Helper.showError("Failed to create transport issue log.");
                        return false;
                    }
                    transport.getTransportIssueLogs().add(transportIssueLog);
                }
                selectedRoute.setStatus(TransportStatusEnum.SOURCE_COMPLETED_WITH_ISSUES);
                transport.setStatus(TransportStatusEnum.CONTINUED_WITH_ISSUES);
                transportRouteController.updateTransportRoute(selectedRoute, selectedRoute.getId());
                transportController.updateTransport(transport, transport.getId());
                return true;
            }
//            if (codeSite == 1) {
//                /* Optional for now */
//                return true;
//            }
        }
        return false;
    }

    /**
     * This method is used to check if the transport is completed.
     *
     * @param transport - the transport object
     * @return - true if the transport is completed, false otherwise
     */
    public static boolean checkIfTransportCompleted(Transport transport) {
        boolean isCompleted = true;
        for (TransportRoute route : transport.getTransportRoutes()) {
            if (route == null) {
                Helper.println("Transport route not found.");
                isCompleted = false;
                break;
            }
            if (route.getStatus() != TransportStatusEnum.DESTINATION_COMPLETED) {
                isCompleted = false;
                transportController.updateTransport(transport, transport.getId());
                break;
            }
        }
        if (isCompleted) {
            if (transport.getStatus() == TransportStatusEnum.CONTINUED_WITH_ISSUES) {
                transport.setStatus(TransportStatusEnum.DONE_WITH_ISSUES);
                transportController.updateTransport(transport, transport.getId());
            } else {
                transport.setStatus(TransportStatusEnum.DONE);
                transportController.updateTransport(transport, transport.getId());
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is used to set a new planned date and time for transport.
     *
     * @param transport          - the transport object
     * @param newPlannedDateTime - the new planned date and time
     * @return - true if the planned date and time were set successfully, false otherwise
     */
    public static boolean setNewPlannedDateTime(Transport transport, LocalDateTime newPlannedDateTime) {
        if (newPlannedDateTime == null) {
            Helper.println("New planned date and time cannot be null.");
            return false;
        }
        transport.setPlannedStartDateTime(newPlannedDateTime);
        transportController.updateTransport(transport, transport.getId());
        return true;
    }

    /**
     * This method is used to cancel transport.
     *
     * @param transport - the transport object
     * @return - true if the transport was canceled successfully, false otherwise
     */
    public static boolean cancelTransport(Transport transport) {
        if (transport.getStatus() == TransportStatusEnum.STARTED) {
            Helper.println("Transport is already started. Cannot cancel.");
            return false;
        }
        if (transport.getStatus() == TransportStatusEnum.DONE) {
            Helper.println("Transport is already done. Cannot cancel.");
            return false;
        }
        if (transport.getStatus() == TransportStatusEnum.DONE_WITH_ISSUES) {
            Helper.println("Transport is already done with issues. Cannot cancel.");
            return false;
        }
        transport.setStatus(TransportStatusEnum.CANCELED);
        transportController.updateTransport(transport, transport.getId());
        return true;
    }

    /**
     * This method is used to set a new truck for transport.
     *
     * @param transport - the transport object
     * @param truck     - the new truck
     * @return - true if the truck ID was set successfully, false otherwise
     */
    public static boolean setNewTruck(Transport transport, Truck truck) {
        if (truck == null) {
            Helper.println("Invalid truck");
            return false;
        }
        transport.setTruck(truck);
        transportController.updateTransport(transport, transport.getId());
        Helper.println("Truck ID set successfully.");
        return true;
    }

    /**
     * This method is used to set a new driver for transport.
     *
     * @param transport - the transport object
     * @param driver    - the new driver
     * @return - true if the driver ID was set successfully, false otherwise
     */
    public static boolean setNewDriver(Transport transport, Driver driver) {
        if (driver == null) {
            Helper.println("Driver not found or not available.");
            return false;
        }
        transport.setDriver(driver);
        transportController.updateTransport(transport, transport.getId());
        Helper.println("Driver ID set successfully.");
        return true;
    }

//    /**
//     * This method is used to edit a transport route.
//     *
//     * @param transport - the transport object
//     * @param routeId   - the ID of the transport route
//     * @return - true if the transport route was edited successfully, false otherwise
//     */
//    public static boolean editTransportRoute(Transport transport, int routeId) {
//        return true; //TODO
//    }

    public static boolean deleteTransportRoute(Transport transport, int transportRouteIdToDelete) {
        if (transport == null || transportRouteIdToDelete <= Helper.UndefinedId) {
            Helper.showError("Cant delete transport with id <= 0");
            return false;
        }
        List<TransportRoute> routes = transport.getTransportRoutes();
        if (routes == null || routes.isEmpty()) {
            Helper.showError("Cant delete transport route with id=" + transportRouteIdToDelete + ", it does not exist");
            return false;
        }
        if (routes.stream().noneMatch(r -> r.getId() == transportRouteIdToDelete)) {
            Helper.showError("Cant delete transport route with id=" + transportRouteIdToDelete + ", it does not exist");
            return false;
        }
        TransportRoute transportRoute = transportRouteController.getTransportRouteById(transportRouteIdToDelete);
        if (transportRoute == null) {
            Helper.showError("Error retrieving transport route with id=" + transportRouteIdToDelete);
            return false;
        }
        if (!transportRouteController.deleteTransportRoute(transportRouteIdToDelete)) {
            Helper.showError("Cant delete transport route with id=" + transportRouteIdToDelete + ", failed to delete transport route");
            return false;
        }
        if (!transport.getTransportRoutes().removeIf(r -> r.getId() == transportRouteIdToDelete)) {
            Helper.showError("Cant delete transport route with id=" + transportRouteIdToDelete + ", failed to delete transport route");
            return false;
        }
        return true;
    }

    /**
     * This method is used to get a driver by ID.
     *
     * @param driverId - the ID of the driver
     * @return - the driver object, or null if not found
     */
    public static Driver getDriverById(int driverId) {
        if (driverId <= Helper.UndefinedId) {
            Helper.showError("Cant get driver with id <= 0");
            return null;
        }
        Driver driver = DriversManager.getDriverById(driverId);
        if (driver == null) {
            Helper.showError("Cant get driver with id=" + driverId + ", it does not exist");
            return null;
        }
        return driver;
    }

    /**
     * This method is used to get a delivery file by ID.
     *
     * @param copy - the ID of the delivery file
     * @return - the delivery file object, or null if not found
     */
    public static DeliveryFile getDeliveryFileById(int copy) {
        if (copy <= Helper.UndefinedId) {
            Helper.showError("Cant get delivery file with id <= 0");
            return null;
        }
        DeliveryFile deliveryFile = deliveryFileController.getDeliveryFileById(copy);
        if (deliveryFile == null) {
            Helper.showError("Cant get delivery file with id=" + copy + ", it does not exist");
            return null;
        }
        return deliveryFile;
    }
}
