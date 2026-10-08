package DB.interfaces.transport;

import Models.DBModels.suppliers.Supplier;

import java.util.ArrayList;
import java.util.List;

public interface ISupplierController extends IController<Supplier> {
    /**
     * Adds a supplier to the database.
     *
     * @param supplier The supplier to add.
     * @return The ID of the newly added supplier, or Helper.UndefinedId if the operation failed.
     */
    int addSupplier(Supplier supplier);

    /**
     * Updates a supplier in the database.
     *
     * @param supplier The supplier to update.
     * @return True if the supplier was updated successfully, false otherwise.
     */
    boolean updateSupplier(Supplier supplier, int id);

    /**
     * Deletes a supplier from the database.
     *
     * @param id The ID of the supplier to delete.
     * @return True if the supplier was deleted successfully, false otherwise.
     */
    boolean deleteSupplier(int id);

    /**
     * Retrieves a supplier from the database by ID.
     *
     * @param id The ID of the supplier to retrieve.
     * @return The supplier with the specified ID, or null if not found.
     */
    Supplier getSupplierById(int id);

    /**
     * Retrieves a supplier from the database by name.
     *
     * @param name The name of the supplier to retrieve.
     * @return The supplier with the specified name, or null if not found.
     */
    Supplier getSupplierByName(String name);

    /**
     * Retrieves suppliers from the database by their IDs.
     *
     * @param ids The list of IDs of the suppliers to retrieve.
     * @return A list of suppliers with the specified IDs, or an empty list if none found.
     */
    ArrayList<Supplier> getSuppliersByIds(List<Integer> ids);
}
