package DB.implementations.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.IDeliveryFileController;
import Models.DBModels.transport.DeliveryFile;
import Models.DBModels.transport.DeliveryFileItem;

import java.util.ArrayList;
import java.util.List;

public class MockDeliveryFileController implements IDeliveryFileController {
    private final ArrayList<DeliveryFile> files;
    private int currentId = 1;
    private static MockDeliveryFileController instance = null;

    private MockDeliveryFileController() {
        this.files = new ArrayList<>();
    }

    public static MockDeliveryFileController getInstance() {
        if (instance == null) {
            instance = new MockDeliveryFileController();
        }
        return instance;
    }

    @Override
    public int addDeliveryFile(DeliveryFile deliveryFile) {
        if (deliveryFile == null) {
            return Helper.UndefinedId;
        }
        deliveryFile.setId(currentId++);
        files.add(deliveryFile);
        return deliveryFile.getId();
    }

    @Override
    public boolean addItemsToDeliveryFile(int deliveryFileId, List<DeliveryFileItem> items) {
        for (DeliveryFile deliveryFile : files) {
            if (deliveryFile.getId() == deliveryFileId) {
                for (var item : items) {
                    item.setDeliveryFile(deliveryFile);
                    deliveryFile.getDeliveryFileItems().add(item);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeItemsFromDeliveryFile(int deliveryFileId, List<DeliveryFileItem> items) {
        for (DeliveryFile deliveryFile : files) {
            if (deliveryFile.getId() == deliveryFileId) {
                for (var item : items) {
                    item.setDeliveryFile(deliveryFile);
                    deliveryFile.getDeliveryFileItems().remove(item);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteDeliveryFile(int id) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getId() == id) {
                files.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public DeliveryFile getDeliveryFileById(int id) {
        for (DeliveryFile deliveryFile : files) {
            if (deliveryFile.getId() == id) {
                return deliveryFile;
            }
        }
        return null;
    }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        for (DeliveryFile deliveryFile : files) {
            ids.add(deliveryFile.getId());
        }
        return ids;
    }
}
