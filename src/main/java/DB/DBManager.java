package DB;

import DB.implementations.sqlite.transport.*;
import DB.interfaces.transport.*;

/**
 * DBManager is a singleton class that manages the database controllers for various entities.
 * It provides access to the controllers for site, delivery file, truck, delivery file item,
 * transport area, driver, supplier, transport, branch, and transport issue log.
 */
public class DBManager {
    static DBManager instance = null;
    private final IDeliveryFileController deliveryFileController;
    private final ITruckController truckController;
    private final ITransportAreaController transportAreaController;
    private final IDriverController driverController;
    private final ISupplierController supplierController;
    private final ITransportController transportController;
    private final IBranchController branchController;
    private final ITransportIssueLogController transportIssueLogController;
    private final ITransportRouteController transportRouteController;

    private DBManager() {
        this.deliveryFileController = SqliteDeliveryFileController.getInstance();
        this.truckController = SqliteTruckController.getInstance();
        this.transportAreaController = SqliteTransportAreaController.getInstance();
        this.driverController = SqliteDriverController.getInstance();
        this.supplierController = SqliteSupplierController.getInstance();
        this.branchController = SqliteBranchController.getInstance();
        this.transportRouteController = SqliteTransportRouteController.getInstance();
        this.transportIssueLogController = SqliteTransportIssueLogController.getInstance();
        this.transportController = SqliteTransportController.getInstance();
    }

    /**
     * Returns the singleton instance of DBManager.
     *
     * @return The singleton instance of DBManager.
     */
    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    /**
     * Returns the delivery file controller.
     *
     * @return The delivery file controller.
     */
    public final IDeliveryFileController getDeliveryFileController() {
        return deliveryFileController;
    }

    /**
     * Returns the truck controller.
     *
     * @return The truck controller.
     */
    public final ITruckController getTruckController() {
        return truckController;
    }

    /**
     * Returns the transport area controller.
     *
     * @return The transport area controller.
     */
    public final ITransportAreaController getTransportAreaController() {
        return transportAreaController;
    }

    /**
     * Returns the driver controller.
     *
     * @return The driver controller.
     */
    public final IDriverController getDriverController() {
        return driverController;
    }

    /**
     * Returns the supplier controller.
     *
     * @return The supplier controller.
     */
    public final ISupplierController getSupplierController() {
        return supplierController;
    }

    /**
     * Returns the transport controller.
     *
     * @return The transport controller.
     */
    public final ITransportController getTransportController() {
        return transportController;
    }

    /**
     * Returns the branch controller.
     *
     * @return The branch controller.
     */
    public final IBranchController getBranchController() {
        return branchController;
    }

    /**
     * Returns the transport issue log controller.
     *
     * @return The transport issue log controller.
     */
    public final ITransportIssueLogController getTransportIssueLogController() {
        return transportIssueLogController;
    }

    /**
     * Returns the transport route controller.
     *
     * @return The transport route controller.
     */
    public final ITransportRouteController getTransportRouteController() {
        return transportRouteController;
    }

}
