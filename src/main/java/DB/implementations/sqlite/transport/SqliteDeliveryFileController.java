package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.IDeliveryFileController;
import DB.util.HibernateUtil;
import Models.DBModels.transport.DeliveryFile;
import Models.DBModels.transport.DeliveryFileItem;

import java.util.ArrayList;
import java.util.List;

public class SqliteDeliveryFileController implements IDeliveryFileController {
    private static SqliteDeliveryFileController instance = null;

    private SqliteDeliveryFileController() {
    }

    public static SqliteDeliveryFileController getInstance() {
        if (instance == null) {
            instance = new SqliteDeliveryFileController();
        }
        return instance;
    }

    @Override
    public int addDeliveryFile(DeliveryFile deliveryFile) {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(deliveryFile);
            session.getTransaction().commit();
            return deliveryFile.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add DeliveryFile", e);
            return Helper.UndefinedId;
        }
    }

    @Override
    public boolean addItemsToDeliveryFile(int deliveryFileId, List<DeliveryFileItem> items) {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            DeliveryFile deliveryFile = session.get(DeliveryFile.class, deliveryFileId);
            if (deliveryFile != null) {
                for (var item : items) {
                    item.setDeliveryFile(deliveryFile);
                    session.persist(item);
                }
                session.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            Helper.showError("Failed to add items to DeliveryFile", e);
        }
        return false;
    }

    @Override
    public boolean removeItemsFromDeliveryFile(int deliveryFileId, List<DeliveryFileItem> items) {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            DeliveryFile deliveryFile = session.get(DeliveryFile.class, deliveryFileId);
            if (deliveryFile != null) {
                List<DeliveryFileItem> attachedItems = deliveryFile.getDeliveryFileItems();
                for (var item : items) {
                    attachedItems.stream()
                            .filter(existing -> existing.getId() == item.getId())
                            .findFirst().ifPresent(attachedItems::remove);
                }

                session.getTransaction().commit(); // orphanRemoval deletes them from DB
                return true;
            }
        } catch (Exception e) {
            Helper.showError("Failed to remove items from DeliveryFile", e);
        }
        return false;
    }

    @Override
    public boolean deleteDeliveryFile(int id) {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            DeliveryFile deliveryFile = session.get(DeliveryFile.class, id);
            if (deliveryFile != null) {
                session.remove(deliveryFile);
                session.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            Helper.showError("Failed to delete DeliveryFile", e);
        }
        return false;
    }

    @Override
    public DeliveryFile getDeliveryFileById(int id) {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(DeliveryFile.class, id);
        } catch (Exception e) {
            Helper.showError("Failed to get DeliveryFile by ID", e);
            return null;
        }
    }
    @Override
    public List<Integer> getAllIds() {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            var query = session.createQuery("SELECT id FROM DeliveryFile", Integer.class);
            return query.getResultList();
        } catch (Exception e) {
            Helper.showError("Failed to get all DeliveryFile IDs", e);
            return new ArrayList<>();
        }
    }
}
