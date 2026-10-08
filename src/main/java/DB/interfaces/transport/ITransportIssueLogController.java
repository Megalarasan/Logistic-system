package DB.interfaces.transport;

import Models.DBModels.transport.TransportIssueLog;

public interface ITransportIssueLogController extends IController<TransportIssueLog> {
    /**
     * Adds a transport issue log to the database.
     *
     * @param transportIssueLog The transport issue log to add.
     * @return The ID of the added transport issue log, or -1 if the operation failed.
     */
    int addTransportIssueLog(TransportIssueLog transportIssueLog);

    /**
     * Updates a transport issue log in the database.
     *
     * @param transportIssueLog The transport issue log to update.
     */
    boolean updateTransportIssueLog(TransportIssueLog transportIssueLog, int id);

    /**
     * Deletes a transport issue log from the database.
     *
     * @param id The ID of the transport issue log to delete.
     */
    boolean deleteTransportIssueLog(int id);

    /**
     * Retrieves a transport issue log from the database by its ID.
     *
     * @param id The ID of the transport issue log to retrieve.
     * @return The transport issue log with the specified ID, or null if not found.
     */
    TransportIssueLog getTransportIssueLogById(int id);
}
