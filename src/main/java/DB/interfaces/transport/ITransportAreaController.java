package DB.interfaces.transport;

import Models.DBModels.transport.TransportArea;

import java.util.List;

public interface ITransportAreaController extends IController<TransportArea> {
    /**
     * Adds a transport area to the database.
     *
     * @param transportArea The transport area to add.
     * @return The ID of the added transport area, or -1 if the addition failed.
     */
    int addTransportArea(TransportArea transportArea);

    /**
     * Retrieves a transport area from the database by its ID.
     *
     * @param id The ID of the transport area to retrieve.
     * @return The transport area with the specified ID, or null if not found.
     */
    TransportArea getTransportAreaById(int id);

    /**
     * Updates a transport area in the database.
     *
     * @param transportArea The transport area to updated object.
     * @param id            The ID of the transport area to update.
     * @return True if the transport area was updated successfully, false otherwise.
     */
    boolean updateTransportArea(TransportArea transportArea, int id);

    /**
     * Deletes a transport area from the database by its ID.
     *
     * @param id The ID of the transport area to delete.
     * @return True if the transport area was deleted successfully, false otherwise.
     */
    boolean deleteTransportArea(int id);

    /**
     * Retrieves a transport area from the database by its name.
     *
     * @param areaName The name of the transport area to retrieve.
     * @return The transport area with the specified name, or null if not found.
     */
    TransportArea getTransportAreaByAreaName(String areaName);


    /**
     * Retrieves all transport areas associated with a specific branch ID.
     *
     * @param branchId The ID of the branch to retrieve transport areas for.
     * @return A list of transport areas associated with the specified branch ID.
     */
    List<TransportArea> getAllTransportAreasByBranchId(int branchId);
}
