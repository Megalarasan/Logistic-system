package DB.implementations.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.IBranchController;
import Models.DBModels.transport.Branch;

import java.util.ArrayList;
import java.util.List;

public class MockBranchController implements IBranchController {
    private static MockBranchController instance = null;
    private final ArrayList<Branch> branches;
    private int currentId = 1;

    private MockBranchController() {
        // Private constructor to prevent instantiation
        branches = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of MockBranchController.
     *
     * @return The singleton instance of MockBranchController.
     */
    public static MockBranchController getInstance() {
        if (instance == null) {
            instance = new MockBranchController();
        }
        return instance;
    }

    @Override
    public int addBranch(Branch branch) {
        if (branch == null) {
            return Helper.UndefinedId;
        }
        branch.setId(currentId++);
        branches.add(branch);
        return branch.getId();
    }

    @Override
    public boolean updateBranch(Branch branch, int id) {
        if (branch == null || id <= 0) {
            return false;
        }
        for (int i = 0; i < branches.size(); i++) {
            if (branches.get(i).getId() == id) {
                branches.set(i, branch);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteBranch(int id) {
        if (id <= 0) {
            return false;
        }
        for (int i = 0; i < branches.size(); i++) {
            if (branches.get(i).getId() == id) {
                branches.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public Branch getBranchById(int id) {
        if (id <= 0) {
            return null;
        }
        for (Branch branch : branches) {
            if (branch.getId() == id) {
                return branch;
            }
        }
        return null;
    }

    @Override
    public Branch getBranchByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (Branch branch : branches) {
            if (branch.getSiteName().equalsIgnoreCase(name)) {
                return branch;
            }
        }
        return null;
    }

    @Override
    public Branch getBranchByAddress(String address) {
        if (address == null || address.isEmpty()) {
            return null;
        }
        for (Branch branch : branches) {
            if (branch.getSiteAddress().equalsIgnoreCase(address)) {
                return branch;
            }
        }
        return null;
    }

    @Override
    public Branch getBranchByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        for (Branch branch : branches) {
            if (branch.getSitePhone().equalsIgnoreCase(phoneNumber)) {
                return branch;
            }
        }
        return null;
    }

    @Override
    public List<Branch> getBranchesByIds(List<Integer> ids) {
        ArrayList<Branch> result = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        for (Integer id : ids) {
            Branch branch = getBranchById(id);
            if (branch != null) {
                result.add(branch);
            }
        }
        return result;
    }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        for (Branch branch : branches) {
            ids.add(branch.getId());
        }
        return ids;
    }
}
