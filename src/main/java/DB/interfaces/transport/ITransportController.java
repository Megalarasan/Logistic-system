package DB.interfaces.transport;

import Models.DBModels.transport.Transport;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransportController extends IController<Transport> {
    /**
     * Adds new transport to the database.
     *
     * @param transport The transport to add.
     * @return The ID of the added transport, or Helper.UndefinedId if the operation failed.
     */
    int addTransport(Transport transport);

    /**
     * Updates existing transport in the database.
     *
     * @param transport The transport with updated information.
     * @param id        The ID of the transport to update.
     * @return true if the transport was updated successfully, false otherwise.
     */
    boolean updateTransport(Transport transport, int id);

    /**
     * Deletes transport from the database.
     *
     * @param id The ID of the transport to delete.
     * @return true if the transport was deleted successfully, false otherwise.
     */
    boolean deleteTransport(int id);

    /**
     * Retrieves transport by its ID.
     *
     * @param id The ID of the transport to retrieve.
     * @return The transport with the specified ID, or null if not found.
     */
    Transport getTransportById(int id);

    /**
     * Retrieves transports by its truck ID.
     *
     * @param id The ID of the truck to retrieve transports for.
     * @return A list of transports associated with the specified truck ID.
     */
    List<Transport> getTransportsByTruckId(int id);

    /**
     * Retrieves transports by its driver ID.
     *
     * @param id The ID of the driver to retrieve transports for.
     * @return A list of transports associated with the specified driver ID.
     */
    List<Transport> getTransportsByDriverId(int id);

    /**
     * Retrieves transports by its date range.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of transports within the specified date range.
     */
    List<Transport> getTransportsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
