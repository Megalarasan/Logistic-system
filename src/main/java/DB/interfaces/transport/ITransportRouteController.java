package DB.interfaces.transport;

import Models.DBModels.transport.TransportRoute;

import java.util.ArrayList;

public interface ITransportRouteController extends IController<TransportRoute> {

    /**
     * Adds a new transport route to the database.
     *
     * @param transportRoute The transport route to add.
     * @return The ID of the added transport route, or Helper.UndefinedId if the operation failed.
     */
    int addTransportRoute(TransportRoute transportRoute);

    /**
     * Updates existing transport route in the database.
     *
     * @param transportRoute The transport route with updated information.
     * @param id             The ID of the transport route to update.
     * @return true if the transport route was updated successfully, false otherwise.
     */
    boolean updateTransportRoute(TransportRoute transportRoute, int id);

    /**
     * Deletes transport route from the database.
     *
     * @param id The ID of the transport route to delete.
     * @return true if the transport route was deleted successfully, false otherwise.
     */
    boolean deleteTransportRoute(int id);

    /**
     * Retrieves transport route by its ID.
     *
     * @param id The ID of the transport route to retrieve.
     * @return The transport route with the specified ID, or null if not found.
     */
    TransportRoute getTransportRouteById(int id);

    /**
     * Retrieves transport routes by their IDs.
     *
     * @param ids The list of IDs of the transport routes to retrieve.
     * @return A list of transport routes with the specified IDs, or an empty list if none found.
     */
    ArrayList<TransportRoute> getTransportRoutesByIds(ArrayList<Integer> ids);

}
