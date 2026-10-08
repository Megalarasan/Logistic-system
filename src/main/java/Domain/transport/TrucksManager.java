package Domain.transport;

import Auxiliary.Enums.Models.LicenseTypeEnum;
import Models.DBModels.transport.Truck;
import DB.DBManager;
import DB.interfaces.transport.ITruckController;
import Auxiliary.Helper;

import java.util.ArrayList;

/**
 * TrucksManager is a class that manages the trucks in the system.
 * It provides methods to add, update, delete, and retrieve trucks.
 */
public class TrucksManager {
    private static final ITruckController truckController = DBManager.getInstance().getTruckController();

    public static boolean addTruck(String plate, double netWeight, double maxWeight, String requiredLicenseType, String model) {
        Truck t = truckController.getTruckByPlate(plate);
        if (t != null) {
            Helper.showError("Truck with this plate already exists");
            return false;
        }
        t = new Truck(0, plate, netWeight, maxWeight, LicenseTypeEnum.stringToEnum(requiredLicenseType), model);
        return truckController.addTruck(t) > 0;
    }

    /**
     * Updates a truck's information based on user input.
     *
     * @param some  - a string that is not used in the method
     * @param id    - the ID of the truck to be updated
     * @param value - the field to be updated (1: plate, 2: net weight, 3: max weight, 4: required license type, 5: model)
     * @return true if the update was successful, false otherwise
     */
    public static boolean updateTruck(String some, int id, int value) {
        Truck t = truckController.getTruckById(id);
        if (t == null) {
            Helper.showError("Truck with this ID does not exist");
            return false;
        }
        switch (value) {
            case 1:
                t.setPlate(some);
                break;
            case 2:
                t.setNetWeight(Integer.parseInt(some));
                break;
            case 3:
                t.setMaxWeight(Integer.parseInt(some));
                break;
            case 4:
                t.setRequiredLicenseType(LicenseTypeEnum.stringToEnum(some));
                break;
            case 5:
                t.setModel(some);
                break;
            default:
                Helper.showError("Invalid choice");
                return false;
        }
        return truckController.updateTruck(t, id);
    }

    /**
     * Deletes a truck from the system.
     *
     * @param id - the ID of the truck to be deleted
     * @return true if the truck was deleted successfully, false otherwise
     */
    public static boolean deleteTruck(int id) {
        Truck t = truckController.getTruckById(id);
        if (t == null) {
            Helper.showError("Truck with this ID does not exist");
            return false;
        }
        return truckController.deleteTruck(id);
    }

    /**
     * Retrieves the entire list of trucks from the database.
     *
     * @return - an ArrayList of Truck objects representing all trucks in the system
     */
    public static ArrayList<Truck> getAllTrucks() {
        ArrayList<Truck> trucks = new ArrayList<>();
        for (Integer id : truckController.getAllIds()) {
            Truck truck = truckController.getTruckById(id);
            if (truck == null) {
                Helper.showError(String.format("Cant get transport area with id=%d, it does not exist", id));
            } else {
                trucks.add(truck);
            }
        }
        return trucks;
    }

    /**
     * Retrieves a truck by its ID.
     *
     * @param id - the ID of the truck to be retrieved
     * @return - the Truck object with the specified ID, or null if not found
     */
    public static Truck getTruckById(int id) {
        Truck t = truckController.getTruckById(id);
        if (t == null) {
            Helper.showError("Truck with this ID does not exist");
            return null;
        }
        return t;
    }

    /**
     * Retrieves a truck by its plate number.
     *
     * @param plate - the plate number of the truck to be retrieved
     * @return - the Truck object with the specified plate number, or null if not found
     */
    public static Truck getTruckByPlate(String plate) {
        Truck t = truckController.getTruckByPlate(plate);
        if (t == null) {
            Helper.showError("Truck with this plate does not exist");
            return null;
        }
        return t;
    }
}
