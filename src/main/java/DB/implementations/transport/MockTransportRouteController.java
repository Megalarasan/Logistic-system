package DB.implementations.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ITransportRouteController;
import Models.DBModels.transport.TransportRoute;

import java.util.ArrayList;
import java.util.List;

public class MockTransportRouteController implements ITransportRouteController {
    private static MockTransportRouteController instance = null;
    private final ArrayList<TransportRoute> transportRoutes;
    private int currentId = 1;

    private MockTransportRouteController() {
        // Private constructor to prevent instantiation
        transportRoutes = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of MockTransportRouteController.
     *
     * @return The singleton instance of MockTransportRouteController.
     */
    public static MockTransportRouteController getInstance() {
        if (instance == null) {
            instance = new MockTransportRouteController();
        }
        return instance;
    }

    @Override
    public int addTransportRoute(TransportRoute transportRoute) {
        if (transportRoute == null) {
            return Helper.UndefinedId;
        }
        transportRoute.setId(currentId++);
        transportRoutes.add(transportRoute);
        return transportRoute.getId();
    }

    @Override
    public boolean updateTransportRoute(TransportRoute transportRoute, int id) {
        if (transportRoute == null || id <= 0) {
            return false;
        }
        for (int i = 0; i < transportRoutes.size(); i++) {
            if (transportRoutes.get(i).getId() == id) {
                transportRoutes.set(i, transportRoute);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteTransportRoute(int id) {
        if (id <= 0) {
            return false;
        }
        for (int i = 0; i < transportRoutes.size(); i++) {
            if (transportRoutes.get(i).getId() == id) {
                transportRoutes.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public TransportRoute getTransportRouteById(int id) {
        if (id <= 0) {
            return null;
        }
        for (TransportRoute transportRoute : transportRoutes) {
            if (transportRoute.getId() == id) {
                return transportRoute;
            }
        }
        return null;
    }

    @Override
    public ArrayList<TransportRoute> getTransportRoutesByIds(ArrayList<Integer> ids) {
        ArrayList<TransportRoute> result = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        for (Integer id : ids) {
            TransportRoute transportRoute = getTransportRouteById(id);
            if (transportRoute != null) {
                result.add(transportRoute);
            }
        }
        return result;
    }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        for (TransportRoute transportRoute : transportRoutes) {
            ids.add(transportRoute.getId());
        }
        return ids;
    }
}
