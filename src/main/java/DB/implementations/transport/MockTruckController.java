package DB.implementations.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ITruckController;
import Models.DBModels.transport.Truck;

import java.util.ArrayList;
import java.util.List;

public class MockTruckController implements ITruckController {
    private static MockTruckController instance = null;
    private final ArrayList<Truck> trucks;
    private int currentId = 1;

    private MockTruckController() {
        // Private constructor to prevent instantiation
        trucks = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of MockTruckController.
     *
     * @return The singleton instance of MockTruckController.
     */
    public static MockTruckController getInstance() {
        if (instance == null) {
            instance = new MockTruckController();
        }
        return instance;
    }

    @Override
    public int addTruck(Truck truck) {
        if (truck == null) {
            return Helper.UndefinedId;
        }
        truck.setId(currentId++);
        trucks.add(truck);
        return truck.getId();
    }

    @Override
    public boolean updateTruck(Truck truck, int id) {
        if (truck == null || id <= 0) {
            return false;
        }
        for (int i = 0; i < trucks.size(); i++) {
            if (trucks.get(i).getId() == id) {
                trucks.set(i, truck);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteTruck(int id) {
        if (id <= 0) {
            return false;
        }
        for (int i = 0; i < trucks.size(); i++) {
            if (trucks.get(i).getId() == id) {
                trucks.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public Truck getTruckById(int id) {
        if (id <= 0) {
            return null;
        }
        for (Truck truck : trucks) {
            if (truck.getId() == id) {
                return truck;
            }
        }
        return null;
    }

    @Override
    public Truck getTruckByPlate(String plate) {
        if (plate == null || plate.isEmpty()) {
            return null;
        }
        for (Truck truck : trucks) {
            if (truck.getPlate().equalsIgnoreCase(plate)) {
                return truck;
            }
        }
        return null;
    }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        for (Truck truck : trucks) {
            ids.add(truck.getId());
        }
        return ids;
    }
}
