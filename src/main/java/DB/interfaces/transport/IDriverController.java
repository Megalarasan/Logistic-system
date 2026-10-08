package DB.interfaces.transport;

import Models.DBModels.transport.Driver;

import java.util.List;

public interface IDriverController extends IController<Driver> {
    /**
     * Adds a new driver to the database.
     *
     * @param driver The driver to be added.
     * @return id of the added driver if successful, -1 otherwise.
     */
    int addDriver(Driver driver);

    /**
     * Updates an existing driver in the database.
     *
     * @param driver The driver to be updated.
     * @return true if the driver was updated successfully, false otherwise.
     */
    boolean updateDriver(Driver driver, int id);

    /**
     * Deletes a driver from the database.
     *
     * @param id The ID of the driver to be deleted.
     * @return true if the driver was deleted successfully, false otherwise.
     */
    boolean deleteDriver(int id);

    /**
     * Retrieves a driver from the database by its ID.
     *
     * @param id The ID of the driver to be retrieved.
     * @return The driver with the specified ID, or null if not found.
     */
    Driver getDriverById(int id);

    /**
     * Retrieves a driver from the database by its full name.
     *
     * @param firstName The first name of the driver to be retrieved.
     * @param lastName  The last name of the driver to be retrieved.
     * @return The driver with the specified full name, or null if not found.
     */
    List<Driver> getDriverByFullName(String firstName, String lastName);

    /**
     * Retrieves a driver from the database by its personal ID.
     *
     * @param personalId The personal ID of the driver to be retrieved.
     * @return The driver with the specified personal ID, or null if not found.
     */
    Driver getDriveByPersonalId(String personalId);

    /**
     * Retrieves a driver from the database by its phone number.
     *
     * @param phoneNumber The phone number of the driver to be retrieved.
     * @return The driver with the specified phone number, or null if not found.
     */
    Driver getDriverByPhoneNumber(String phoneNumber);
}
