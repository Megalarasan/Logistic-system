package Domain.transport;

import Auxiliary.Enums.Domain.UpdateTransportAreaEnum;
import Auxiliary.Helper;
import DB.DBManager;
import DB.interfaces.transport.ITransportAreaController;
import Domain.suppliers.SuppliersManager;
import Models.DBModels.suppliers.Supplier;
import Models.DBModels.transport.Branch;
import Models.DBModels.transport.TransportArea;

import java.util.ArrayList;
import java.util.List;

public class TransportAreasManager {
    private static final ITransportAreaController transportAreaController = DBManager.getInstance()
            .getTransportAreaController();

    /**
     * This method is used to add a new transport area.
     *
     * @param areaName     - the name of the transport area
     * @param branchesIDs  - the IDs of the branches in the transport area - assumes IDs are correct
     * @param suppliersIDs - the IDs of the suppliers in the transport area - assumes IDs are correct
     * @return - true if the transport area was added successfully, false otherwise
     */
    public static boolean addNewTransportArea(String areaName, List<Integer> branchesIDs,
                                              List<Integer> suppliersIDs) {
        TransportArea area = transportAreaController.getTransportAreaByAreaName(areaName);
        if (area != null) {
            Helper.showError(String.format("Transport area with this name={%s} already exists", areaName));
            return false;
        }
        if (branchesIDs == null || branchesIDs.isEmpty()) {
            Helper.showError("Cant add transport area with empty branches IDs");
            return false;
        }
        if (suppliersIDs == null || suppliersIDs.isEmpty()) {
            Helper.showError("Cant add transport area with empty suppliers IDs");
            return false;
        }
        List<Branch> branches = BranchesManager.getBranchesByIds(branchesIDs);
        List<Supplier> suppliers = SuppliersManager.getSuppliersByIds(suppliersIDs);
        area = new TransportArea(Helper.UndefinedId, areaName, suppliers, branches);
        return transportAreaController.addTransportArea(area) > 0;
    }

    /**
     * This method is used to get a transport area by its ID.
     *
     * @param id - the ID of the transport area
     * @return - the transport area object if found, otherwise null
     */
    public static boolean removeTransportArea(int id) {
        if (id <= 0) {
            Helper.showError("Cant delete transport area with id <= 0");
            return false;
        }
        return transportAreaController.deleteTransportArea(id);
    }

    /**
     * This method is used to get a transport area by its ID.
     *
     * @param id - the ID of the transport area
     * @return - the transport area object if found, otherwise null
     */
    public static TransportArea getTransportAreaById(int id) {
        return transportAreaController.getTransportAreaById(id);
    }

    /**
     * Get all the existing transports areas.
     *
     * @return - a list of all transport areas
     */
    public static ArrayList<TransportArea> getAllTransportAreas() {
        ArrayList<TransportArea> areas = new ArrayList<>();
        for (Integer id : transportAreaController.getAllIds()) {
            TransportArea area = transportAreaController.getTransportAreaById(id);
            if (area == null) {
                Helper.showError(String.format("Cant get transport area with id=%d, it does not exist", id));
            } else {
                areas.add(area);
            }
        }
        return areas;
    }


    /**
     * This method is used to update a transport area.
     *
     * @param updateType   - the type of update to perform
     * @param id           - the ID of the transport area to update
     * @param AreaName     - must not be null or empty if updateType is Update_Name
     * @param suppliersIDs - must not be null or empty if updateType is Add_Suppliers or Remove_Suppliers
     * @param branchesIDs  - must not be null or empty if updateType is Add_Branches or Remove_Branches
     * @return - true if the transport area was updated successfully, false otherwise
     */
    public static boolean updateTransportArea(UpdateTransportAreaEnum updateType, int id, String AreaName,
                                              List<Integer> suppliersIDs, List<Integer> branchesIDs) {
        if (id <= 0) {
            Helper.showError("Cant update transport area with id <= 0");
            return false;
        }
        if ((AreaName == null || AreaName.isEmpty()) && updateType == UpdateTransportAreaEnum.Update_Name) {
            Helper.showError("Cant update transport area with empty name when updating name");
            return false;
        } else if (AreaName != null && !AreaName.isEmpty() && updateType == UpdateTransportAreaEnum.Update_Name) {
            // Check if the area name already exists for some other area
            TransportArea existingArea = transportAreaController.getTransportAreaByAreaName(AreaName);
            if (existingArea != null && existingArea.getId() != id) {
                Helper.showError(String.format("Cant update transport area with id=%d," +
                        " area name=%s already exists", id, AreaName));
                return false;
            }
        }
        if ((suppliersIDs == null || suppliersIDs.isEmpty()) && (updateType == UpdateTransportAreaEnum.Add_Suppliers
                || updateType == UpdateTransportAreaEnum.Remove_Suppliers)) {
            Helper.showError("Cant update transport area with empty suppliers IDs when updating suppliers");
            return false;
        }
        if ((branchesIDs == null || branchesIDs.isEmpty()) && (updateType == UpdateTransportAreaEnum.Add_Branches
                || updateType == UpdateTransportAreaEnum.Remove_Branches)) {
            Helper.showError("Cant update transport area with empty branches IDs when updating branches");
            return false;
        }
        TransportArea area = transportAreaController.getTransportAreaById(id);
        if (area == null) {
            Helper.showError(String.format("Cant update transport area with id=%d, it does not exist", id));
            return false;
        }
        switch (updateType) {
            case Update_Name -> area.setAreaName(AreaName);
            case Add_Suppliers -> {
                List<Supplier> addLst = new ArrayList<>(area.getSuppliers());
                for (int supplierID : suppliersIDs) {
                    if (addLst.stream().noneMatch(supplier -> supplier.getId() == supplierID)) {
                        Supplier supplierToAdd = SuppliersManager.getSupplierById(supplierID);
                        if (supplierToAdd == null) {
                            Helper.showError(String.format("Cant add supplier with id=%d, it does not exist", supplierID));
                        } else {
                            addLst.add(supplierToAdd);
                        }
                    }
                }
                area.setSuppliers(addLst);
            }
            case Remove_Suppliers -> {
                List<Supplier> removeLst = new ArrayList<>(area.getSuppliers());
                for (int supplierID : suppliersIDs) {
                    removeLst.stream().filter(supplier -> supplier.getId() == supplierID)
                            .findFirst().ifPresent(removeLst::remove);
                }
                if (removeLst.isEmpty()) {
                    Helper.showError("Cant remove all suppliers from transport area, there must be at least one supplier.");
                    return false;
                }
                area.setSuppliers(removeLst);
            }
            case Add_Branches -> {
                List<Branch> addLst = new ArrayList<>(area.getBranches());
                for (int branchID : branchesIDs) {
                    if (addLst.stream().noneMatch(branch -> branch.getId() == branchID)) {
                        Branch branchToAdd = BranchesManager.getBranchById(branchID);
                        if (branchToAdd == null) {
                            Helper.showError(String.format("Cant add branch with id=%d, it does not exist", branchID));
                        } else {
                            addLst.add(branchToAdd);
                        }
                    }
                }
                area.setBranches(addLst);
            }
            case Remove_Branches -> {
                List<Branch> removeLst = new ArrayList<>(area.getBranches());
                for (int branchID : branchesIDs) {
                    removeLst.stream().filter(branch -> branch.getId() == branchID)
                            .findFirst().ifPresent(removeLst::remove);
                }
                if (removeLst.isEmpty()) {
                    Helper.showError("Cant remove all branches from transport area, there must be at least one branch.");
                    return false;
                }
                area.setBranches(removeLst);
            }
            default -> {
                Helper.showError("Cant update transport area with undefined update type");
                return false;
            }
        }
        return transportAreaController.updateTransportArea(area, id);
    }

    /**
     * This method is used to get all transport areas by branch ID.
     *
     * @param branchId - the ID of the branch
     * @return - a list of transport areas associated with the branch ID
     */
    public static List<TransportArea> getTransportAreasByBranchId(int branchId) {
        return transportAreaController.getAllTransportAreasByBranchId(branchId);
    }
}
