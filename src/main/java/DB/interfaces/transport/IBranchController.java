package DB.interfaces.transport;

import Models.DBModels.transport.Branch;

import java.util.List;

public interface IBranchController extends IController<Branch> {
    /**
     * Adds a new branch to the database.
     *
     * @param branch The branch to be added.
     * @return branch id if added successfully -1 otherwise
     */
    int addBranch(Branch branch);

    /**
     * Updates an existing branch in the database.
     *
     * @param branch The updated branch structure.
     * @param id     The ID of the branch to be updated.
     * @return true if the branch was updated successfully, false otherwise.
     */
    boolean updateBranch(Branch branch, int id);

    /**
     * Deletes a branch from the database.
     *
     * @param id The ID of the branch to be deleted.
     * @return true if the branch was deleted successfully, false otherwise.
     */
    boolean deleteBranch(int id);

    /**
     * Retrieves a branch from the database by its ID.
     *
     * @param id The ID of the branch to be retrieved.
     * @return The branch with the specified ID, or null if not found.
     */
    Branch getBranchById(int id);

    /**
     * Retrieves a branch from the database by its name.
     *
     * @param name The name of the branch to be retrieved.
     * @return The branch with the specified name, or null if not found.
     */
    Branch getBranchByName(String name);

    /**
     * Retrieves a branch from the database by its address.
     *
     * @param address The address of the branch to be retrieved.
     * @return The branch with the specified address, or null if not found.
     */
    Branch getBranchByAddress(String address);

    /**
     * Retrieves a branch from the database by its phone number.
     *
     * @param phoneNumber The phone number of the branch to be retrieved.
     * @return The branch with the specified phone number, or null if not found.
     */
    Branch getBranchByPhoneNumber(String phoneNumber);

    /**
     * Retrieves branches from the database by their IDs.
     *
     * @param ids The list of IDs of the branches to be retrieved.
     * @return A list of branches with the specified IDs, or an empty list if none found.
     */
    List<Branch> getBranchesByIds(List<Integer> ids);

}
