package Domain.transport;

import Auxiliary.Enums.Domain.Status;
import Auxiliary.Enums.Models.LicenseTypeEnum;
import DB.DBManager;
import DB.DBemployeeManager;
import DB.interfaces.transport.IDriverController;
import Domain.Employees.EmployeeManagerCLS;
import Domain.Employees.HR_Lead;
import Models.DBModels.Employees.Employee;
import Models.DBModels.transport.Driver;
import Auxiliary.Helper;

import java.util.ArrayList;

/**
 * The DriversManager class is responsible for managing driver-related operations.
 * It provides methods to add, update, delete, and retrieve drivers from the database.
 */
public class DriversManager {
    private static final IDriverController driverController = DBManager.getInstance().getDriverController();

    /**
     * Adds a new driver to the system.
     *
     * @param firstName   - the first name of the driver
     * @param lastName    - the last name of the driver
     * @param personalId  - the personal ID of the driver
     * @param phoneNumber - the phone number of the driver
     * @param licenseType - the license type of the driver
     * @return true if the driver was added successfully, false otherwise
     */
    public static boolean addDriver(String firstName, String lastName, String personalId, String phoneNumber, String licenseType) {
        Driver d = driverController.getDriveByPersonalId(personalId);
        if (d != null) {
            Helper.showError("Driver with this personal ID already exists");
            return false;
        }
        d = driverController.getDriverByPhoneNumber(phoneNumber);
        if (d != null) {
            Helper.showError("Driver with this phone number already exists");
            return false;
        }
        /*It is possible for multiple people to have the same name - it is not unique - commenting our for now...*/
//        d = driverController.getDriverByFullName(firstName, lastName);
//        if (d != null) {
//            Helper.showError("Driver with this name already exists");
//            return false;
//        }
        d = new Driver(0, firstName, lastName, personalId, phoneNumber, LicenseTypeEnum.stringToEnum(licenseType));
        if (driverController.addDriver(d) != Helper.UndefinedId) {
            return true;
        }
        return false;
    }

    /**
     * Updates a driver's information based on user input.
     * This method updates both the transport system and the employee database.
     *
     * @param some  - the new value for the field to be updated
     * @param id    - the ID of the driver to be updated
     * @param value - the field to be updated (1: first name, 2: last name, 3: personal ID, 4: phone number, 5: license type)
     * @return true if the update was successful in both systems, false otherwise
     */
    public static boolean updateDriver(String some, int id, int value) {
        Driver d = driverController.getDriverById(id);
        if (d == null) {
            Helper.showError("Driver with this ID does not exist");
            return false;
        }

        // Store original personal ID for employee lookup
        String originalPersonalId = d.getPersonalId();

        // Update driver in transport system
        switch (value) {
            case 1:
                d.setFirstName(some);
                break;
            case 2:
                d.setLastName(some);
                break;
            case 3:
                d.setPersonalId(some);
                break;
            case 4:
                d.setPhoneNumber(some);
                break;
            case 5:
                d.setLicenseType(LicenseTypeEnum.stringToEnum(some));
                break;
            default:
                Helper.showError("Invalid choice");
                return false;
        }

        // Update driver in transport database
        if (!driverController.updateDriver(d, id)) {
            Helper.showError("Failed to update driver in transport system");
            return false;
        }

        // Update corresponding employee in employee database
        if (!updateDriverInEmployeeSystem(originalPersonalId, some, value)) {
            Helper.showError("Driver updated in transport system but failed to update in employee database");
            return false;
        }

        System.out.println("Driver updated successfully in both transport and employee systems");
        return true;
    }

    /**
     * Updates a driver's information in the employee system.
     * This is a helper method that updates the corresponding employee record.
     *
     * @param personalId - the personal ID of the driver/employee to update
     * @param newValue   - the new value for the field
     * @param fieldType  - the field to be updated (1: first name, 2: last name, 3: personal ID, 4: phone number, 5: license type)
     * @return true if the employee was updated successfully, false otherwise
     */
    private static boolean updateDriverInEmployeeSystem(String personalId, String newValue, int fieldType) {
        try {
            Employee employee = EmployeeManagerCLS.getEmployeeById(personalId);
            if (employee == null) {
                Helper.showError("Driver exists in transport system but not found in employee database");
                return false;
            }

            // Update employee based on field type
            switch (fieldType) {
                case 1: // First name
                    employee.setName(newValue);
                    break;
                case 2: // Last name
                    // For last name, we need to do nothing
                    break;
                case 3: // Personal ID (Employee ID)
                    employee.setEmployeeID(newValue);
                    break;
                case 4: // Phone number - Employee doesn't have phone field, so we skip this
                    // Phone number is only in transport system, no update needed in employee system
                    return true;
                case 5: // License type - This is transport-specific, no update needed in employee system
                    // License type is only in transport system, no update needed in employee system
                    return true;
                default:
                    return false;
            }

            // Update employee in database
            System.out.println("Updating employee: " + employee.getName() + " (ID: " + employee.getEmployeeID() + ")");
            Status result = EmployeeManagerCLS.updateEmployeeInSystem(employee);
            System.out.println("Employee update result: " + result);
            return result == Status.Success;
        } catch (Exception e) {
            Helper.showError("Failed to update driver in employee system: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a driver from the system.
     * This method deletes the driver from both the transport system and the employee database.
     *
     * @param id - the ID of the driver to be deleted
     * @return true if the driver was deleted successfully from both systems, false otherwise
     */
    public static boolean deleteDriver(int id) {
        Driver d = driverController.getDriverById(id);
        if (d == null) {
            Helper.showError("Driver with this ID does not exist");
            return false;
        }

        // Store personal ID for employee deletion
        String personalId = d.getPersonalId();

        // Delete driver from transport system first
        if (!driverController.deleteDriver(id)) {
            Helper.showError("Failed to delete driver from transport system");
            return false;
        }

        // Delete corresponding employee from employee database
        if (!deleteDriverFromEmployeeSystem(personalId)) {
            Helper.showError("Driver deleted from transport system but failed to delete from employee database");
            return false;
        }

        return true;
    }

    /**
     * Deletes a driver from the employee system.
     * This is a helper method that deletes the corresponding employee record.
     * This method directly calls the employee controller to avoid circular dependency.
     *
     * @param personalId - the personal ID of the driver/employee to delete
     * @return true if the employee was deleted successfully, false otherwise
     */
    private static boolean deleteDriverFromEmployeeSystem(String personalId) {
        try {
            // Check if employee exists
            if (!EmployeeManagerCLS.doesEmployeeExist(personalId)) {
                Helper.showError("Driver exists in transport system but not found in employee database");
                return false;
            }

            // Delete employee directly from database to avoid circular dependency
            // We don't use EmployeeManagerCLS.deleteEmployeeFromSystem because it tries to delete the driver again
            Status result = DBemployeeManager.getInstance().getEmployeeController().removeEmployeeFromDB(personalId);
            return result == Status.Success;
        } catch (Exception e) {
            Helper.showError("Failed to delete driver from employee system: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a driver by their ID.
     *
     * @param id - the ID of the driver to be retrieved
     * @return - the Driver object with the specified ID, or null if not found
     */
    public static Driver getDriverById(int id) {
        Driver d = driverController.getDriverById(id);
        if (d == null) {
            Helper.showError("Driver with this ID does not exist");
            return null;
        }
        return d;
    }

    /**
     * Retrieves all drivers from the system.
     *
     * @return - an ArrayList of Driver objects representing all drivers in the system
     */
    public static ArrayList<Driver> getAllDrivers() {
        ArrayList<Driver> drivers = new ArrayList<>();
        for (Integer id : driverController.getAllIds()) {
            Driver driver = driverController.getDriverById(id);
            if (driver == null) {
                Helper.showError(String.format("Cant get driver with id=%d, it does not exist", id));
            } else {
                drivers.add(driver);
            }
        }
        return drivers;
    }

    /**
     * Retrieves a driver by their personal ID.
     *
     * @param personalId - the personal ID of the driver to be retrieved
     * @return - the Driver object with the specified personal ID, or null if not found
     */
    public static Driver getDriverByPersonalId(String personalId) {
        Driver d = driverController.getDriveByPersonalId(personalId);
        if (d == null) {
            Helper.showError("Driver with this personal ID does not exist");
            return null;
        }
        return d;
    }
}
