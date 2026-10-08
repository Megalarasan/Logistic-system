package Presentation.transport;

import Auxiliary.Enums.Domain.CreateNewTransportResultEnum;
import Auxiliary.Enums.Models.TransportStatusEnum;
import Auxiliary.Enums.Models.visitSiteEnum;
import Auxiliary.Helper;
import Domain.stock.StockManager;
import Domain.transport.*;
import Models.DBModels.transport.*;
import Models.DTOs.StockItem;
import Models.DTOs.transport.CreateNewTransportResult;
import Models.DTOs.transport.CreateNewTransportRoutesResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransportHandler {


    /**
     * This method is used to get the date and time for transport from the user.
     *
     * @return A list of LocalDateTime objects representing the start and end date and time of transport.
     */
    private static List<LocalDateTime> getDateForTransport() {
        List<LocalDateTime> localDateTimes = new ArrayList<>();
        Helper.println(String.format("Please enter the planned *start* date and time of transport (%s):",
                Helper.DefaultDateTimeFormat));
        String dateTimeStr = Helper.getUserInputString();
        LocalDateTime dateTime = Helper.stringToDateTime(dateTimeStr);
        while (dateTime == null) {
            Helper.print("Invalid date and time format. Please try again: ");
            dateTimeStr = Helper.getUserInputString();
            dateTime = Helper.stringToDateTime(dateTimeStr);
        }
        localDateTimes.add(dateTime);
        Helper.println(String.format("Please enter the planned *end* date and time of transport (%s):",
                Helper.DefaultDateTimeFormat));
        dateTimeStr = Helper.getUserInputString();
        dateTime = Helper.stringToDateTime(dateTimeStr);
        while (dateTime == null) {
            Helper.print("Invalid date and time format. Please try again: ");
            dateTimeStr = Helper.getUserInputString();
            dateTime = Helper.stringToDateTime(dateTimeStr);
        }
        localDateTimes.add(dateTime);
        return localDateTimes;
    }


    /**
     * This method is used to get the branch ID for transport based on user input.
     *
     * @param userInput        The user input string.
     * @param existingBranches The list of existing branches.
     * @return The branch ID if valid, otherwise undefined ID.
     */
    private static int getBranchIdForTransport(String userInput, List<Branch> existingBranches) {
        int branchId = Helper.UndefinedId;
        try {
            branchId = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            Helper.println("Invalid input. Please enter a valid branch ID or 'continue'.");
            return Helper.UndefinedId;
        }
        if (!Helper.isValidId(existingBranches, branchId)) {
            Helper.println("Invalid branch ID. Please try again.");
            return Helper.UndefinedId;
        }
        int finalBranchId = branchId;
        Branch selectedBranch = existingBranches.stream().filter(branch -> branch.getId() == finalBranchId)
                .findFirst().orElse(null);
        if (selectedBranch == null) {
            Helper.println("Branch not found. Please try again.");
            return Helper.UndefinedId;
        }
        Helper.println("You have selected branch: " + selectedBranch.getSiteName());
        return branchId;
    }


    /**
     * This method is used to get the stock items selected by the user for transport to a specific branch
     *
     * @param branchId The ID of the branch.
     * @return A list of selected stock items for transport.
     */
    private static ArrayList<StockItem> getStockItemsUserSelectionOfBranchForTransport(int branchId) {
        String userInput;
        ArrayList<StockItem> selectedItems = new ArrayList<>();
        do {
            Helper.print("Please enter the search string for stock items or 'continue' to proceed: ");
            userInput = Helper.getUserInputString();
            while (userInput == null || userInput.trim().isEmpty()) {
                Helper.print("Invalid search string. Please try again: ");
                userInput = Helper.getUserInputString();
            }
            if (userInput.equalsIgnoreCase("continue")) {
                break;
            }
            ArrayList<StockItem> stockItems = StockManager.mockSearchAndReturnRelevantStockItems(userInput, branchId);
            for (StockItem stockItem : stockItems) {
                Helper.println(String.format("Item ID: %d, Item Name: %s, Amount: %.2f",
                        stockItem.getStockItemId(), stockItem.getItemName(), stockItem.getAmount()));
            }
            Helper.print("Please select the relevant item you want to add to the transport by its ID," +
                    " or write 0 to skip this item: ");
            int itemId = Helper.getUserInputInteger();
            while (itemId == Helper.UndefinedUserInput) {
                Helper.print("Invalid item ID. Please try again: ");
                itemId = Helper.getUserInputInteger();
            }
            if (itemId == 0) {
                Helper.println("Skipping this item.");
            } else {
                int finalItemId = itemId;
                StockItem selectedItem = stockItems.stream().filter(item -> item.getStockItemId() == finalItemId)
                        .findFirst().orElse(null);
                if (selectedItem != null) {
                    if (selectedItems.stream().anyMatch(item -> item.getStockItemId() == selectedItem.getStockItemId())) {
                        Helper.println("Item already selected. Please choose a different item.");
                    } else {
                        Helper.println("You have selected item: " + selectedItem.getItemName());
                        Helper.println("The current amount at the branch is: " + selectedItem.getAmount());
                        Helper.println("Please enter the amount you want to add to the transport: ");
                        double amount = Helper.getUserInputDouble();
                        while (amount <= 0) {
                            Helper.print("Invalid amount. Please enter a valid amount: ");
                            amount = Helper.getUserInputDouble();
                        }
                        selectedItem.setAmount(amount);
                        selectedItems.add(selectedItem);
                    }
                } else {
                    Helper.println("Invalid item ID. Please try again.");
                }
            }
        }
        while (userInput.trim().isEmpty() || !userInput.equalsIgnoreCase("continue"));
        return selectedItems;
    }


    /**
     * This method is used to perform the actual transport creation process.
     *
     * @param plannedStartDateTime   The planned start date and time of transport.
     * @param plannedEndDateTime     The planned end date and time of transport.
     * @param createdTransportRoutes The list of created transport routes.
     * @return True if the transport creation was successful, false otherwise.
     */
    private static boolean performActualTransportCreation(LocalDateTime plannedStartDateTime,
                                                          LocalDateTime plannedEndDateTime,
                                                          List<TransportRoute> createdTransportRoutes) {
        if (createdTransportRoutes == null || createdTransportRoutes.isEmpty()) {
            Helper.println("No transport routes created. Cannot proceed with transport creation.");
            return false;
        }
        List<Truck> trucks = TransportManager.getAvailableTrucksByDate(plannedStartDateTime, plannedEndDateTime);
        boolean truckAndDriverSelectionSuccess = trucks.isEmpty();
        if (truckAndDriverSelectionSuccess) {
            Helper.println("No trucks available for the selected date and time.");
            //todo add support to edit the time right here and then search again!
            return false;
        }
        List<DeliveryFileItem> deliveryFileItems = new ArrayList<>();
        for (TransportRoute route : createdTransportRoutes) {
            if (route != null) {
                DeliveryFile deliveryFile = route.getDeliveryFile();
                if (deliveryFile != null) {
                    for (DeliveryFileItem item : deliveryFile.getDeliveryFileItems()) {
                        if (item != null) {
                            deliveryFileItems.add(item);
                        }
                    }
                }
            }
        }
        double sumOfItemsWeight = 0;
        for (DeliveryFileItem deliveryFileItem : deliveryFileItems) {
            StockItem stock = StockManager.getStockItemById(deliveryFileItem.getStockItemId());
            if (stock == null) {
                Helper.println("Stock item not found. Please try again.");
                return false;
            }
            if (stock.getWeight() > 0) {
                sumOfItemsWeight += stock.getWeight() * deliveryFileItem.getAmount();
            } else {
                Helper.println("Invalid weight for stock item. Please try again.");
                return false;
            }
        }
        if (sumOfItemsWeight == 0) {
            Helper.println("No items selected for transport. Cannot proceed with transport creation.");
            return false;
        }
        for (Truck truck : trucks) {
            if (truck.getMaxWeight() - truck.getNetWeight() < sumOfItemsWeight) {
                trucks.remove(truck);
            }
        }
        if (trucks.isEmpty()) {
            Helper.println("No trucks available for the selected date and time.");
            return false;
        }
        do {
            Helper.println(String.format("Select a truck for the transport (Only available trucks are visible for the" +
                            " date %s-%s):", plannedStartDateTime.format(Helper.DefaultDateTimeFormatter),
                    plannedEndDateTime.format(Helper.DefaultDateTimeFormatter)));
            trucks.forEach(truck -> Helper.println(truck.toPrettyString()));
            Helper.print("Please enter the truck ID: ");
            int truckId = Helper.getUserInputInteger();
            while (truckId == Helper.UndefinedUserInput || !Helper.isValidId(trucks, truckId)) {
                Helper.print("Invalid truck ID. Please try again: ");
                truckId = Helper.getUserInputInteger();
            }
            Truck selectedTruck = TrucksManager.getTruckById(truckId);
            if (selectedTruck == null) {
                Helper.println("Truck not found. Please try again.");
                continue;
            } else {
                Helper.println("You have selected truck: " + selectedTruck.toPrettyString());
            }

            List<Driver> drivers = TransportManager.getAvailableDriversByDateAndLicense(plannedStartDateTime,
                    plannedEndDateTime, selectedTruck.getRequiredLicenseType());
            if (drivers.isEmpty()) {
                Helper.println("No drivers available for the selected date and time and license type of %s"
                        .formatted(selectedTruck.getRequiredLicenseType()));
                break;
            }
            Helper.println(String.format("Select a driver for the transport" +
                    "(Only available drivers with suitable license are visible for the" +
                    " date %s-%s):", plannedStartDateTime, plannedEndDateTime));
            drivers.forEach(driver -> Helper.println(driver.toPrettyString()));
            Helper.print("Please enter the driver ID: ");
            int driverId = Helper.getUserInputInteger();
            while (driverId == Helper.UndefinedUserInput || !Helper.isValidId(drivers, driverId)) {
                Helper.print("Invalid driver ID. Please try again: ");
                driverId = Helper.getUserInputInteger();
            }
            Driver selectedDriver = DriversManager.getDriverById(driverId);
            if (selectedDriver == null) {
                Helper.println("Driver not found. Please try again.");
            } else {
                Helper.println("You have selected driver: " + selectedDriver.toPrettyString());
                Helper.println("Generating transport...");
                CreateNewTransportResult result = TransportManager.createTransport(plannedStartDateTime, plannedEndDateTime,
                        selectedTruck, selectedDriver, createdTransportRoutes);
                boolean stopHandlingStocker = false;
                do {
                    if (result.getResultEnum() == CreateNewTransportResultEnum.StockerUnavailable) {
                        Helper.println(String.format("no available stocker at the planned end time %s for branch %s",
                                plannedEndDateTime,
                                createdTransportRoutes.getFirst().getDestinationSite().toString()));
                        Helper.print("Enter a different date and time for the transport?: ");
                        String answer = Helper.getUserInputString();
                        while (answer == null || answer.isEmpty() ||
                                (!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("no"))) {
                            Helper.print("Invalid input. Please enter 'yes' or 'no': ");
                            answer = Helper.getUserInputString();
                        }
                        if (answer.equalsIgnoreCase("yes")) {
                            plannedStartDateTime = null;
                            plannedEndDateTime = null;
                            do {
                                Helper.println("Please enter the new date and time for the transport:");
                                List<LocalDateTime> newDateTimes = getDateForTransport();
                                if (newDateTimes.isEmpty() || newDateTimes.size() < 2) {
                                    Helper.println("Invalid date and time. Please try again.");
                                    continue;
                                }
                                plannedStartDateTime = newDateTimes.get(0);
                                plannedEndDateTime = newDateTimes.get(1);
                            }
                            while (plannedStartDateTime == null || plannedEndDateTime == null);
                        } else {
                            Helper.println("Transport creation canceled.");
                            TransportManager.deleteTransportRoutes(createdTransportRoutes);
                            return false;
                        }
                        result = TransportManager.createTransport(plannedStartDateTime, plannedEndDateTime,
                                selectedTruck, selectedDriver, createdTransportRoutes);
                    } else {
                        stopHandlingStocker = true;
                    }
                } while (!stopHandlingStocker);
                if (result.getResultEnum() == CreateNewTransportResultEnum.Success) {
                    Transport createdTransport = TransportManager.getTransportById(result.getCreatedTransportId());
                    if (createdTransport == null) {
                        Helper.println("Failed to create transport.");
                        return false;
                    }
                    Helper.println("You have created transport: " + createdTransport.toPrettyString());
                } else {
                    Helper.println("Failed to create transport. Please try again.");
                    TransportManager.deleteTransportRoutes(createdTransportRoutes);
                    return false;
                }
                truckAndDriverSelectionSuccess = true;
            }
        } while (!truckAndDriverSelectionSuccess);
        return truckAndDriverSelectionSuccess;
    }


    /**
     * This method is the main routine for creating transport.
     * It handles the selection of branches, stock items,
     * and the creation of transport routes.
     */
    private static void createTransportMainRoutine() {
        List<TransportRoute> createdTransportRoutes = new ArrayList<>();
        Helper.println("Transport Creation:");
        List<LocalDateTime> dateTimes = getDateForTransport();
        if (dateTimes.isEmpty() || dateTimes.size() < 2) {
            Helper.println("Invalid date and time. Please try again.");
            return;
        }
        HelperForCreateTransportRoutes(createdTransportRoutes);
        if (createdTransportRoutes.isEmpty()) {
            Helper.println("No transport routes created. Cannot proceed with transport creation.");
            return;
        }
        if (performActualTransportCreation(dateTimes.get(0), dateTimes.get(1), createdTransportRoutes)) {
            Helper.println("Transport created successfully.");
        } else {
            Helper.println("Failed to create transport.");
        }
    }

    /**
     * This method is used to create a new transport route.
     * It handles the selection of a branch and stock items,
     * and the creation of transport routes.
     */
    private static void HelperForCreateTransportRoutes(List<TransportRoute> createdTransportRoutes) {
        ArrayList<Branch> branches = BranchesManager.getExistingBranches();
        if (branches.isEmpty()) {
            Helper.println("No branches available. Please create a branch first.");
            return;
        }
        String userInput;
        for (Branch branch : branches) {
            Helper.println(String.format("Branch ID: %d, Branch Name: %s", branch.getId(), branch.getSiteName()));
        }
        Helper.print("Select the branch to create transport for or write 'cancel' to cancel this operation: ");
        userInput = Helper.getUserInputString();
        if (userInput == null || userInput.equalsIgnoreCase("cancel")) {
            Helper.println("Transport creation canceled.");
        } else {
            int branchId = getBranchIdForTransport(userInput, branches);
            if (branchId == Helper.UndefinedId) {
                Helper.println("Invalid branch ID. Please try again.");
                return;
            }

            List<StockItem> selectedItems = getStockItemsUserSelectionOfBranchForTransport(branchId);
            Helper.println("The items you selected for this transport are:");
            for (StockItem selectedItem : selectedItems) {
                Helper.println(String.format("Item ID: %d, Item Name: %s, Amount: %.2f",
                        selectedItem.getStockItemId(), selectedItem.getItemName(), selectedItem.getAmount()));
            }
            Helper.println("Generating routes from suppliers to branch...");
            CreateNewTransportRoutesResult routesCreationResult =
                    TransportManager.createNewTransportRoutes(branchId, selectedItems);
            switch (routesCreationResult.getResultEnum()) {
                case Success -> {
                    Helper.println("Transport routes created successfully for branch " + branchId);
                    createdTransportRoutes.addAll(routesCreationResult.getCreatedTransportRoutes());
                }
                case SomeItemsUnprovided -> {
                    Helper.println("Some items could not be provided by the suppliers for this branch");
                    Helper.println("Please check the following items:");
                    for (StockItem unprovidedItem : routesCreationResult.getUnprovidedItems()) {
                        Helper.println(String.format("Item ID: %d, Item Name: %s, Amount: %.2f",
                                unprovidedItem.getStockItemId(),
                                unprovidedItem.getItemName(), unprovidedItem.getAmount()));
                    }
                    Helper.print("Would you like to add this branch with the unprovided items to the transport? " +
                            "(yes/no): ");
                    String proceedInput = Helper.getUserInputString();
                    while (proceedInput == null
                            || (!proceedInput.equalsIgnoreCase("yes")
                            && !proceedInput.equalsIgnoreCase("no"))) {
                        Helper.print("Invalid input. Please enter 'yes' or 'no': ");
                        proceedInput = Helper.getUserInputString();
                    }
                    if (proceedInput.equalsIgnoreCase("yes")) {
                        Helper.println("Transport routes created successfully for branch " + branchId);
                        createdTransportRoutes.addAll(routesCreationResult.getCreatedTransportRoutes());
                    } else {
                        Helper.println("Branch will not be added to the transport.");
                        List<TransportRoute> routes = routesCreationResult.getCreatedTransportRoutes();
                        if (routes != null && !routes.isEmpty()) {
                            if (!TransportManager.deleteTransportRoutes(routes)) {
                                Helper.println("Failed to delete created routes.");
                            } else {
                                Helper.println("Created routes deleted successfully.");
                            }
                        }
                    }
                }
                case UNDEFINED -> {
                    Helper.println("Failed to create transport routes for branch " + branchId);
                    List<TransportRoute> routesIds = routesCreationResult.getCreatedTransportRoutes();
                    if (routesIds != null && !routesIds.isEmpty()) {
                        TransportManager.deleteTransportRoutes(routesIds);
                        Helper.println("Deleting created routes...");
                    }
                }
            }
        }
    }

    /**
     * This method is used to delete transport by its ID.
     */
    private static void deleteTransportById() {
        Helper.print("Please enter the transport ID to delete: ");
        int transportId = Helper.getUserInputInteger();
        while (transportId == Helper.UndefinedUserInput) {
            Helper.print("Invalid transport ID. Please try again: ");
            transportId = Helper.getUserInputInteger();
        }
        Transport t = TransportManager.getTransportById(transportId);
        if (t == null) {
            Helper.println("Transport not found. Please try again.");
            return;
        }
        Helper.print("Are you sure you want to delete this transport?:\n" + t.toPrettyString() + "\n(yes/no): ");
        String confirmation = Helper.getUserInputString();
        while (confirmation == null || (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no"))) {
            Helper.print("Invalid input. Please enter 'yes' or 'no': ");
            confirmation = Helper.getUserInputString();
        }
        if (confirmation.equalsIgnoreCase("no")) {
            Helper.println("Transport deletion canceled.");
            return;
        }
        if (TransportManager.deleteTransportById(transportId)) {
            Helper.println("Transport with ID " + transportId + " deleted successfully.");
        } else {
            Helper.println("Failed to delete transport with ID " + transportId + ".");
        }
    }


    /**
     * This method is used to show all transports.
     *
     * @return A list of all transports.
     */
    private static List<Transport> showAllTransports() {
        List<Transport> transports = TransportManager.getAllTransports();
        if (transports.isEmpty()) {
            Helper.println("No transports available.");
            return null;
        }
        Helper.println("All Transports:");
        for (Transport transport : transports) {
            Helper.println(String.format("Transport ID: %d, on date: %s", transport.getId(),
                    transport.getPlannedStartDateTime().format(Helper.DefaultDateTimeFormatter)));
        }
        return transports;
    }


    /**
     * This method is used to show specific detailed transport by its ID.
     */
    private static void showSpecificDetailedTransport(Transport transport) {
        Helper.println("Showing specific detailed transport by ID");
        List<Transport> lst = showAllTransports();
        if (lst == null || lst.isEmpty()) {
            return;
        }
        if (transport == null) {
            Helper.print("Please enter the transport ID: ");
            int transportId = Helper.getUserInputInteger();
            while (transportId == Helper.UndefinedUserInput || !Helper.isValidId(lst, transportId)) {
                Helper.print("Invalid transport ID. Please try again: ");
                transportId = Helper.getUserInputInteger();
            }
            transport = TransportManager.getTransportById(transportId);
            if (transport == null) {
                Helper.println("Transport not found. Please try again.");
                return;
            }
        }

        Truck truck = TrucksManager.getTruckById(transport.getTruckId());
        if (truck == null) {
            Helper.println("Truck not found. Please try again.");
            return;
        }
        Driver driver = DriversManager.getDriverById(transport.getDriverId());
        if (driver == null) {
            Helper.println("Driver not found. Please try again.");
            return;
        }

        Helper.println("\nYou have selected transport:\n" + transport.toPrettyString());
        Helper.println("Truck details:\n" + truck.toPrettyString());
        Helper.println("Driver details:\n" + driver.toPrettyString());
        Helper.println("Displaying routes:");
        for (TransportRoute route : transport.getTransportRoutes()) {
            Helper.println("--------------------------------");
            Helper.println(TransportManager.getDetailedTransportRouteString(route));
            Helper.println("--------------------------------");
        }
        Helper.println("-End of transport details-");
    }

    /**
     * This method is used to start the transport.
     */
    private static void startTransport() {
        Helper.print("Please enter the transport ID to start: ");
        int transportId = Helper.getUserInputInteger();
        while (transportId == Helper.UndefinedUserInput) {
            Helper.print("Invalid transport ID. Please try again: ");
            transportId = Helper.getUserInputInteger();
        }
        Transport transport = TransportManager.getTransportById(transportId);
        if (transport == null) {
            Helper.println("Transport not found. Please try again.");
            return;
        }
        Helper.print("Enter the start date and time of the transport (%s): "
                .formatted(Helper.DefaultDateTimeFormat));
        String dateTimeStr = Helper.getUserInputString();
        LocalDateTime startDateTime = Helper.stringToDateTime(dateTimeStr);
        while (startDateTime == null) {
            Helper.print("Invalid date and time format. Please try again: ");
            dateTimeStr = Helper.getUserInputString();
            startDateTime = Helper.stringToDateTime(dateTimeStr);
        }
        if (!TransportManager.StartTransport(transport, startDateTime)) {
            Helper.println("Failed to start transport.");
            return;
        }
    }

    /**
     * This method is used to visit the source of the transport.
     */
    private static void visitSource() {
        for (Transport transport : TransportManager.getAllTransports()) {
            Helper.println("Transport ID: " + transport.getId() + ", Status: " + transport.getStatus());
        }
        Helper.print("Please enter the transport ID to visit source: ");
        int transportId = Helper.getUserInputInteger();
        while (transportId == Helper.UndefinedUserInput) {
            Helper.print("Invalid transport ID. Please try again: ");
            transportId = Helper.getUserInputInteger();
        }
        Transport transport = TransportManager.getTransportById(transportId);
        if (transport == null) {
            Helper.println("Transport not found. Please try again.");
            return;
        }
        if (transport.getStatus() == TransportStatusEnum.DONE || transport.getStatus() == TransportStatusEnum.DONE_WITH_ISSUES) {
            Helper.println("Transport is already done. Cannot visit destination.");
            return;
        }
        if (transport.getStatus() == TransportStatusEnum.STARTED) {
            Helper.println("Relevant sources to visit: ");
            List<TransportRoute> routes = transport.getTransportRoutes();
            int count = 0;
            TransportRoute selectedRoute = null;
            for (TransportRoute transportRoute : routes) {
                if (transportRoute == null) {
                    Helper.println("Transport route not found. Please try again.");
                    return;
                }
                if (transportRoute.getStatus() == TransportStatusEnum.SOURCE_COMPLETED_WITH_ISSUES
                        || transportRoute.getStatus() == TransportStatusEnum.SOURCE_COMPLETED
                        || transportRoute.getStatus() == TransportStatusEnum.DESTINATION_COMPLETED) {
                    if (transportRoute.getStatus() == TransportStatusEnum.SOURCE_COMPLETED_WITH_ISSUES
                            || transportRoute.getStatus() == TransportStatusEnum.SOURCE_COMPLETED) {
                        count++;
                    }
                    continue;
                }
                APhysicalSite sourceSite = transportRoute.getSourceSite();
                if (sourceSite == null) {
                    Helper.println("Source not found. Please try again.");
                    return;
                }
                Helper.println("Source ID: " + sourceSite.getId() + ", Address: " + transportRoute.getSourceSite());
            }
            if (count == routes.size()){
                Helper.println("All sources have been visited. Cannot visit source.");
                return;
            }
            Helper.println("Enter the ID of the source you want to visit: ");
            boolean flag = false;
            do {
                int sourceID = Helper.getUserInputInteger();
                if (sourceID <= Helper.UndefinedId) {
                    Helper.println("Invalid input. Please try again.");
                    continue;
                }
                for (TransportRoute transportRoute : routes) {
                    if (transportRoute == null) {
                        Helper.println("Transport route not found. Please try again.");
                        return;
                    }
                    APhysicalSite src = transportRoute.getSourceSite();
                    if (src != null && src.getId() == sourceID) {
                        Helper.println("Visiting source: " + src.toPrettyString());
                        selectedRoute = transportRoute;
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
                Helper.println("Source not found. Please try again.");
            } while (true);
            visitSiteEnum status = TransportManager.visitSource(transport, selectedRoute);
            if (status == visitSiteEnum.OVERLOAD) {
                boolean check = false;
                do {
                    Helper.println("Overload detected. Cannot visit source. The total weight exceeds the truck's capacity.");
                    Helper.println("Choose a different truck (1) or choose which items to discard (2).");
                    String answer = Helper.getUserInputString();
                    do {
                        if (answer == null || answer.isEmpty() ||
                                (!answer.equals("1") && !answer.equals("2"))) {
                            Helper.println("Invalid input. Please choose 1 or 2.");
                            answer = Helper.getUserInputString();
                        }
                    } while (!answer.equals("1") && !answer.equals("2"));
                    if (answer.equals("1")) {
                        List<Truck> availableTrucks = new ArrayList<>();
                        if (!check) {
                            availableTrucks = TransportManager.getAvailableTrucksByDate(transport.getPlannedStartDateTime(),
                                    transport.getPlannedEndDateTime());
                        }
                        if (availableTrucks.isEmpty()) {
                            Helper.println("No available trucks found.");
                            if (check) {
                                Helper.println("Please try again later when trucks will be available.");
                            }
                            return;
                        }
                        Helper.println("Available trucks:");
                        for (Truck truck : availableTrucks) {
                            Helper.println("Truck ID: " + truck.getId() + ", total weight: " + (truck.getMaxWeight() - truck.getNetWeight()));
                        }
                        Helper.print("Please enter the truck ID: ");
                        int truckId = Helper.getUserInputInteger();
                        while (truckId == Helper.UndefinedUserInput) {
                            Helper.print("Invalid truck ID. Please try again: ");
                            truckId = Helper.getUserInputInteger();
                        }
                        boolean isValidTruckId = false;
                        for (Truck truck : availableTrucks) {
                            if (truck.getId() == truckId) {
                                isValidTruckId = true;
                                break;
                            }
                        }
                        if (!isValidTruckId) {
                            Helper.println("Invalid truck ID. Please try again.");
                            continue;
                        }
                        if (!TransportManager.AttachIssueToExistingTransport(transport, 1, selectedRoute, truckId, null, 0)) {
                            Helper.println("Failed to update transport issue. Please try again.");
                            return;
                        }
                        Helper.println("Truck updated successfully.");
                        if (TransportManager.visitSource(transport, selectedRoute) == visitSiteEnum.SUCCESS) {
                            Helper.println("Successfully visited source.");
                            return;
                        } else {
                            Helper.println("Try again. The truck weight is still too high. Please choose a different truck.");
                            check = true;
                            availableTrucks.remove(truckId);
                        }
                    } else {
                        Helper.println("Choose item Id to modify the amount of the item to discard.");
                        DeliveryFile deliveryFile = selectedRoute.getDeliveryFile();
                        if (deliveryFile == null) {
                            Helper.println("Delivery file not found. Please try again.");
                            return;
                        }
                        List<StockItem> stockItems = new ArrayList<>();
                        for (DeliveryFileItem item : deliveryFile.getDeliveryFileItems()) {
                            if (item != null) {
                                StockItem stockItem = StockManager.getStockItemById(item.getStockItemId());
                                if (stockItem == null) {
                                    Helper.println("Stock item not found. Please try again.");
                                    return;
                                }
                                stockItems.add(stockItem);
                            }
                        }
                        int copy = TransportManager.createDeliveryFile(stockItems);
                        DeliveryFile copyDeliveryFile = TransportManager.getDeliveryFileById(copy);
                        if (copyDeliveryFile == null) {
                            Helper.println("Failed to create delivery file copy. Please try again.");
                            return;
                        }
                        for (DeliveryFileItem item : copyDeliveryFile.getDeliveryFileItems()) {
                            if (item != null) {
                                StockItem stockItem = StockManager.getStockItemById(item.getStockItemId());
                                if (stockItem == null) {
                                    Helper.println("Stock item not found. Please try again.");
                                    return;
                                }
                                Helper.println("Item ID: " + item.getId() + ", Amount: " + item.getAmount() + ", single weight: " +
                                        stockItem.getWeight() + ", total weight: " + (stockItem.getWeight() * item.getAmount()));
                            }
                        }
                        List<Integer> discardedItems = Helper.retrieveIdsListFromInput(copyDeliveryFile.getDeliveryFileItems());
                        if (discardedItems.isEmpty()) {
                            Helper.println("No items selected for discard. Please try again.");
                            continue;
                        }
                        for (DeliveryFileItem item : copyDeliveryFile.getDeliveryFileItems()) {
                            if (item != null && discardedItems.contains(item.getId())) {
                                StockItem stockItem = StockManager.getStockItemById(item.getStockItemId());
                                assert stockItem != null;
                                Helper.println("Item ID: " + item.getId() + ", Amount: " + item.getAmount() + ", single weight: " +
                                        stockItem.getWeight() + ", total weight: " + (stockItem.getWeight() * item.getAmount()));
                                Helper.print("Please enter the new amount for this item: ");
                                double newAmount = Helper.getUserInputDouble();
                                while (newAmount <= 0) {
                                    Helper.print("Invalid amount. Please try again: ");
                                    newAmount = Helper.getUserInputDouble();
                                }
                                item.setAmount(newAmount);
                                Helper.println("Item amount updated successfully.");
                            }
                        }
                        selectedRoute.setDeliveryFile(copyDeliveryFile);
                        if (TransportManager.visitSource(transport, selectedRoute) == visitSiteEnum.SUCCESS) {
                            Helper.println("Successfully visited source.");
                        } else {
                            Helper.println("Failed to visit source. Try again.");
                        }
                        if (!TransportManager.AttachIssueToExistingTransport(transport, 2, selectedRoute, 0, deliveryFile, 0)) {
                            Helper.println("Failed to update transport issue. Please try again.");
                            return;
                        } else {
                            Helper.println("Transport issue updated successfully.");
                            return;
                        }
                    }
                } while (true);
            } else {
                if (status == visitSiteEnum.SUCCESS) {
                    Helper.println("Successfully visited source.");
                } else {
                    Helper.println("Failed to visit source. Try again.");
                }
                return;
            }
        }
        Helper.println("Failed to visit source. Transport not started or source not found.");
        return;
    }

    private static void visitDestination() {
        for (Transport transport : TransportManager.getAllTransports()) {
            Helper.println("Transport ID: " + transport.getId() + ", Status: " + transport.getStatus());
        }
        Helper.print("Please enter the transport ID to visit source: ");
        int transportId = Helper.getUserInputInteger();
        while (transportId == Helper.UndefinedUserInput) {
            Helper.print("Invalid transport ID. Please try again: ");
            transportId = Helper.getUserInputInteger();
        }
        Transport transport = TransportManager.getTransportById(transportId);
        if (transport == null) {
            Helper.println("Transport not found. Please try again.");
            return;
        }
        if (transport.getStatus() == TransportStatusEnum.DONE || transport.getStatus() == TransportStatusEnum.DONE_WITH_ISSUES) {
            Helper.println("Transport is already done. Cannot visit destination.");
            return;
        }
        if (transport.getStatus() == TransportStatusEnum.STARTED || transport.getStatus() == TransportStatusEnum.CONTINUED_WITH_ISSUES) {
            Helper.println("Relevant destinations to visit: ");
            List<TransportRoute> routes = transport.getTransportRoutes();
            List<TransportRoute> selectedRoute = new ArrayList<>();
            for (TransportRoute transportRoute : routes) {
                if (transportRoute == null) {
                    Helper.println("Transport route not found. Please try again.");
                    continue;
                }
                if (transportRoute.getStatus() == TransportStatusEnum.DESTINATION_COMPLETED_WITH_ISSUES
                        || transportRoute.getStatus() == TransportStatusEnum.PENDING
                        || transportRoute.getStatus() == TransportStatusEnum.DESTINATION_COMPLETED) {
                    continue;
                }
                APhysicalSite destinationSite = transportRoute.getDestinationSite();
                if (destinationSite == null) {
                    Helper.showError("Destination not found. Please try again.");
                    return;
                }
                Helper.println("Destination ID: " + destinationSite.getId() + ", Address: " + destinationSite.getSiteAddress());
            }
            Helper.println("Enter the ID of the destination you want to visit: ");
            boolean flag = false;
            do {
                int destinationID = Helper.getUserInputInteger();
                if (destinationID <= Helper.UndefinedId) {
                    Helper.println("Invalid input. Please try again.");
                    continue;
                }
                for (TransportRoute tr : routes) {
                    if (tr == null) {
                        Helper.showError("Transport route not found. Please try again.");
                        continue;
                    }
                    APhysicalSite destination = tr.getDestinationSite();
                    if (destination != null && destination.getId() == destinationID) {
                        Helper.println("Visiting destination: " + destination.toPrettyString());
                        selectedRoute.add(tr);
                        flag = true;
                    }
                }
                if (flag) {
                    break;
                }
                Helper.println("Destination not found. Please try again.");
            } while (true);
            if (TransportManager.visitDestination(transport, selectedRoute) == visitSiteEnum.SUCCESS) {
                if (TransportManager.checkIfTransportCompleted(transport)) {
                    Helper.println("Successfully visited destination.");
                    Helper.println("Transport is completed successfully.");
                } else {
                    Helper.println("Successfully visited destination.");
                    Helper.println("Transport is not completed yet.");
                }
            } else {
                Helper.println("Failed to visit destination. Try again.");
            }
            return;
        } else {
            Helper.println("Transport is not started yet. Please start the transport first.");
            return;
        }
    }

    /**
     * This method is used to edit an unstarted transport.
     */
    private static void editUnstartedTransport() {
        Helper.print("Please enter the transport ID to edit: ");
        int transportId = Helper.getUserInputInteger();
        while (transportId == Helper.UndefinedUserInput) {
            Helper.print("Invalid transport ID. Please try again: ");
            transportId = Helper.getUserInputInteger();
        }
        Transport transport = TransportManager.getTransportById(transportId);
        if (transport == null) {
            Helper.println("Transport not found. Please try again.");
            return;
        }
        if (transport.getStatus() != TransportStatusEnum.PENDING) {
            Helper.println("Transport is already started or canceled. Cannot edit.");
            return;
        }
        do {
            Helper.println("Choose the field to edit:");
            Helper.println("1. Planned date and time");
            Helper.println("2. Transport status (cancel)");
            Helper.println("3. Truck ID");
            Helper.println("4. Driver ID");
            Helper.println("5. Transport route IDs");
            Helper.println("6. Back to main menu");
            int choice = Helper.getUserInputInteger();
            switch (choice) {
                case 1 -> {
                    do {
                        if (transport.getStatus() != TransportStatusEnum.PENDING) {
                            Helper.println("Transport is already started. Cannot edit planned date and time.");
                            break;
                        } else {
                            Helper.print("Enter the new planned date and time (%s): "
                                    .formatted(Helper.DefaultDateTimeFormat));
                            String dateTimeStr = Helper.getUserInputString();
                            LocalDateTime plannedDateTime = Helper.stringToDateTime(dateTimeStr);
                            while (plannedDateTime == null) {
                                Helper.print("Invalid date and time format. Please try again: ");
                                dateTimeStr = Helper.getUserInputString();
                                plannedDateTime = Helper.stringToDateTime(dateTimeStr);
                            }
                            if (TransportManager.setNewPlannedDateTime(transport, plannedDateTime)) {
                                Helper.println("Planned date and time updated successfully.");
                                Helper.println("");
                                break;
                            } else {
                                Helper.println("Failed to update planned date and time. Try again.");
                            }
                        }
                    } while (true);
                }
                case 2 -> {
                    Helper.print("Are you sure you want to cancel this transport? (yes/no): ");
                    String confirmation = Helper.getUserInputString();
                    while (confirmation == null || (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("no"))) {
                        Helper.print("Invalid input. Please enter 'yes' or 'no': ");
                        confirmation = Helper.getUserInputString();
                    }
                    if (confirmation.equalsIgnoreCase("yes")) {
                        if (TransportManager.cancelTransport(transport)) {
                            Helper.println("Transport canceled successfully.");
                        } else {
                            Helper.println("Failed to cancel transport. Try again.");
                        }
                    } else {
                        Helper.println("Transport cancellation canceled.");
                    }
                    break;
                }
                case 3 -> {
                    Helper.print("Enter the new truck ID: ");
                    int truckId = Helper.getUserInputInteger();
                    while (truckId == Helper.UndefinedUserInput) {
                        Helper.print("Invalid truck ID. Please try again: ");
                        truckId = Helper.getUserInputInteger();
                    }
                    Truck selectedTruck = TrucksManager.getTruckById(truckId);
                    if (selectedTruck == null) {
                        Helper.println("Truck not found. Please try again.");
                        continue;
                    }
                    if (TransportManager.setNewTruck(transport, selectedTruck)) {
                        Helper.println("Truck ID updated successfully.");
                    } else {
                        Helper.println("Failed to update truck ID. Try again.");
                    }
                    break;
                }
                case 4 -> {
                    Helper.print("Enter the new driver ID: ");
                    int driverId = Helper.getUserInputInteger();
                    while (driverId == Helper.UndefinedUserInput) {
                        Helper.print("Invalid driver ID. Please try again: ");
                        driverId = Helper.getUserInputInteger();
                    }
                    Driver selectedDriver = DriversManager.getDriverById(driverId);
                    if (selectedDriver == null) {
                        Helper.println("Driver not found. Please try again.");
                        continue;
                    }
                    if (TransportManager.setNewDriver(transport, selectedDriver)) {
                        Helper.println("Driver ID updated successfully.");
                        Helper.println("");
                    } else {
                        Helper.println("Failed to update driver ID. Try again.");
                    }
                    break;
                }
                case 5 -> {
                    Helper.println("Choose to add or edit or delete transport route IDs:");
                    Helper.println("1. Add transport routes ID");
                    //Helper.println("2. Edit transport route ID");
                    Helper.println("2. Delete transport route ID");
                    String userInput = Helper.getUserInputString();
                    while (userInput == null || userInput.isEmpty() || !userInput.matches("[1-3]")) {
                        Helper.println("Invalid input. Please choose 1 or 2.");
                        userInput = Helper.getUserInputString();
                    }
                    switch (userInput) {
                        case "1" -> {
                            List<TransportRoute> createdTransportRoutesIds = new ArrayList<>();
                            HelperForCreateTransportRoutes(createdTransportRoutesIds);
                            if (createdTransportRoutesIds.isEmpty()) {
                                Helper.println("No transport routes created. Cannot proceed with adding new transport routes.");
                                return;
                            }
                            transport.getTransportRoutes().addAll(createdTransportRoutesIds);
                            //Took time to think.
                        }
//                        case "2" -> {
//                            // TODO NTH Edit transport route ID
//                        }
                        case "2" -> {
                            Helper.println("Enter the transport route ID to delete: ");
                            int transportRouteIdToDelete = Helper.getUserInputInteger();
                            while (transportRouteIdToDelete == Helper.UndefinedUserInput) {
                                Helper.println("Invalid transport route ID. Please try again: ");
                                transportRouteIdToDelete = Helper.getUserInputInteger();
                            }
                            if (TransportManager.deleteTransportRoute(transport, transportRouteIdToDelete)) {
                                Helper.println("Transport route ID deleted successfully.");
                            } else {
                                Helper.println("Failed to delete transport route ID. Try again.");
                            }
                        }
                        default -> {
                            Helper.println("Invalid input. Please choose 1 or 2.");
                        }
                    }
                }
                case 6 -> {
                    Helper.println("Exiting edit menu.");
                    return;
                }
                default -> Helper.println("Invalid choice. Please try again.");
            }


        } while (true);
    }

    /**
     * This method is used to print the transport drives for the driver.
     */
    private static void printTransportDrivesForDriver() {
        Helper.print("Please enter the transport ID: ");
        int transportId = Helper.getUserInputInteger();
        while (transportId == Helper.UndefinedUserInput) {
            Helper.print("Invalid transport ID. Please try again: ");
            transportId = Helper.getUserInputInteger();
        }
        Transport transport = TransportManager.getTransportById(transportId);
        if (transport == null) {
            Helper.println("Transport not found. Please try again.");
            return;
        }
        List<TransportRoute> routes = transport.getTransportRoutes();
        if (routes == null || routes.isEmpty()) {
            Helper.println("No transport routes found for this transport.");
            return;
        }
        int count = 0;
        for (TransportRoute route : routes) {
            if (route == null) {
                Helper.println("Transport route not found. Please try again.");
                routes.remove(null);
                continue;
            }
            if (route.getStatus() != TransportStatusEnum.PENDING) {
                routes.remove(route);
            }
            if (route.getStatus() == TransportStatusEnum.SOURCE_COMPLETED || route.getStatus() == TransportStatusEnum.SOURCE_COMPLETED_WITH_ISSUES) {
                count++;
            }
        }
        if (count == routes.size()) {
            Helper.println("All sources are completed. Please continue to the destination.");
            return;
        }
        if (routes.isEmpty()) {
            Helper.println("No transport routes found for this transport.");
            return;
        }
        Helper.println("Transport routes for driver:");
        for (TransportRoute route : routes) {
            Helper.println("Route ID: " + route.getId() + ", Source: " + route.getSourceSite().getSiteAddress() +
                    ", Destination: " + route.getDestinationSite().getSiteAddress() + ", contact name source: " + route.getSourceContactName() +
                    ", contact phone source: " + route.getSourceContactPhone() + " ,contact name destination: " + route.getDestinationContactName() +
                    ", contact phone destination: " + route.getDestinationContactPhone());
            Helper.println("");
            Helper.println("Delivery file items:");
            DeliveryFile deliveryFile = route.getDeliveryFile();
            if (deliveryFile != null) {
                for (DeliveryFileItem item : deliveryFile.getDeliveryFileItems()) {
                    Helper.println("Item" + item.getStockItemId() + " ,Amount: " + item.getAmount() + " ,Picked up: " + item.getAmountPickedUp());
                }
            } else {
                Helper.println("No delivery file items found.");
            }
            Helper.println("--------------------------------");
        }
    }

    /**
     * This method is used to handle the transport management menu.
     */
    public static void handleTransports() {
        boolean exit = false;
        while (!exit) {
            int choice;
            Helper.println("\nManage Transports:");
            Helper.println("1. Show all transports");
            Helper.println("2. Show specific detailed transport by ID");
            Helper.println("3. Print transport drives for driver - by Transport ID");
            Helper.println("4. Delete transport by ID");
            Helper.println("5. Create transport");
            Helper.println("6. Start transport");
            Helper.println("7. Visit source");
            Helper.println("8. Visit destination");
            Helper.println("9. Edit Unstarted Transport");
            Helper.println("10. Back to main menu");

            do {
                Helper.print("Please choose an option: ");
                choice = Helper.getUserInputInteger();
            } while (choice == Helper.UndefinedUserInput);

            switch (choice) {
                case 1:
                    showAllTransports();
                    break;
                case 2:
                    showSpecificDetailedTransport(null);
                    break;
                case 3:
                    printTransportDrivesForDriver();
                    break;
                case 4:
                    deleteTransportById();
                    break;
                case 5:
                    createTransportMainRoutine();
                    break;
                case 6:
                    startTransport();
                    break;
                case 7:
                    visitSource();
                    break;
                case 8:
                    visitDestination();
                    break;
                case 9:
                    editUnstartedTransport();
                    break;
                case 10:
                    exit = true;
                    break;
                default:
                    Helper.println("Invalid choice. Please try again.");
            }
        }
    }
}
