package DB.implementations.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.IDriverController;
import Models.DBModels.transport.Driver;

import java.util.ArrayList;
import java.util.List;

public class MockDriverController implements IDriverController {
    private static MockDriverController instance = null;
    private final ArrayList<Driver> drivers;
    private int currentId = 1;

    private MockDriverController() {
        // Private constructor to prevent instantiation
        drivers = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of MockDriverController.
     *
     * @return The singleton instance of MockDriverController.
     */
    public static MockDriverController getInstance() {
        if (instance == null) {
            instance = new MockDriverController();
        }
        return instance;
    }

    @Override
    public int addDriver(Driver driver) {
        if (driver == null) {
            return Helper.UndefinedId;
        }
        driver.setId(currentId++);
        drivers.add(driver);
        return driver.getId();
    }

    @Override
    public boolean updateDriver(Driver driver, int id) {
        if (driver == null || id <= 0) {
            return false;
        }
        for (int i = 0; i < drivers.size(); i++) {
            if (drivers.get(i).getId() == id) {
                drivers.set(i, driver);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteDriver(int id) {
        if (id <= 0) {
            return false;
        }
        for (int i = 0; i < drivers.size(); i++) {
            if (drivers.get(i).getId() == id) {
                drivers.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public Driver getDriverById(int id) {
        if (id <= 0) {
            return null;
        }
        for (Driver driver : drivers) {
            if (driver.getId() == id) {
                return driver;
            }
        }
        return null;
    }

    @Override
    public List<Driver> getDriverByFullName(String firstName, String lastName) {
        if (firstName == null || lastName == null || firstName.isEmpty() || lastName.isEmpty()) {
            return null;
        }
        for (Driver driver : drivers) {
            if (driver.getFirstName().equalsIgnoreCase(firstName) && driver.getLastName().equalsIgnoreCase(lastName)) {
                return List.of(driver);
            }
        }
        return null;
    }

    @Override
    public Driver getDriveByPersonalId(String personalId) {
        if (personalId == null || personalId.isEmpty()) {
            return null;
        }
        for (Driver driver : drivers) {
            if (driver.getPersonalId().equalsIgnoreCase(personalId)) {
                return driver;
            }
        }
        return null;
    }

    @Override
    public Driver getDriverByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        for (Driver driver : drivers) {
            if (driver.getPhoneNumber().equalsIgnoreCase(phoneNumber)) {
                return driver;
            }
        }
        return null;
    }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        for (Driver driver : drivers) {
            ids.add(driver.getId());
        }
        return ids;
    }
}
