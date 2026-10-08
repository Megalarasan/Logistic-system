package DB.implementations.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ITransportAreaController;
import Models.DBModels.transport.TransportArea;

import java.util.ArrayList;
import java.util.List;

public class MockTransportAreaController implements ITransportAreaController {
    private static MockTransportAreaController instance = null;
    private final ArrayList<TransportArea> transportAreas;
    private static int idCounter = 1; // Static counter for unique IDs

    private MockTransportAreaController() {
        // Private constructor to prevent instantiation
        this.transportAreas = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of MockTransportAreaController.
     *
     * @return The singleton instance of MockTransportAreaController.
     */
    public static MockTransportAreaController getInstance() {
        if (instance == null) {
            instance = new MockTransportAreaController();
        }
        return instance;
    }

    @Override
    public int addTransportArea(TransportArea transportArea) {
        if (transportArea == null) {
            return Helper.UndefinedId;
        }
        // Assign a unique ID to the transport area
        transportArea.setId(idCounter++);
        transportAreas.add(transportArea);
        return transportArea.getId(); // Return the assigned ID
    }

    @Override
    public TransportArea getTransportAreaById(int id) {
        for (TransportArea area : transportAreas) {
            if (area.getId() == id) {
                return area; // Found the transport area
            }
        }
        return null; // Not found
    }

    @Override
    public boolean updateTransportArea(TransportArea transportArea, int id) {
        if (transportArea == null || id <= 0) {
            return false;
        }
        for (int i = 0; i < transportAreas.size(); i++) {
            if (transportAreas.get(i).getId() == id) {
                transportAreas.set(i, transportArea);
                return true; // Successfully updated
            }
        }
        return false; // Not found
    }

    @Override
    public boolean deleteTransportArea(int id) {
        for (int i = 0; i < transportAreas.size(); i++) {
            if (transportAreas.get(i).getId() == id) {
                transportAreas.remove(i);
                return true; // Successfully deleted
            }
        }
        return false; // Not found
    }

    @Override
    public TransportArea getTransportAreaByAreaName(String areaName) {
        if (areaName == null || areaName.isEmpty()) {
            return null; // Invalid area name
        }
        for (TransportArea area : transportAreas) {
            if (area.getAreaName().equalsIgnoreCase(areaName)) {
                return area; // Found the transport area
            }
        }
        return null; // Not found
    }

    @Override
    public List<TransportArea> getAllTransportAreasByBranchId(int branchId) {
        return transportAreas.stream()
                .filter(area -> area.getBranches().contains(branchId))
                .toList(); // Filter transport areas by branch ID
    }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        for (TransportArea area : transportAreas) {
            ids.add(area.getId()); // Collect all IDs
        }
        return ids; // Return the list of IDs
    }
}
