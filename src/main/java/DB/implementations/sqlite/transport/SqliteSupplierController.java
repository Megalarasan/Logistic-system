package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ISupplierController;
import DB.util.HibernateUtil;
import Models.DBModels.suppliers.Supplier;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class SqliteSupplierController implements ISupplierController {
    private static SqliteSupplierController instance;

    public static SqliteSupplierController getInstance() {
        if (instance == null) {
            instance = new SqliteSupplierController();
        }
        return instance;
    }

    private SqliteSupplierController() {
    }

    @Override
    public int addSupplier(Supplier supplier) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(supplier);
            session.getTransaction().commit();
            return supplier.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add %s".formatted(Supplier.class), e);
            return Helper.UndefinedId;
        }
    }

    @Override
    public boolean updateSupplier(Supplier supplier, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Supplier existing = session.get(Supplier.class, id);
            if (existing == null) return false;
            existing.copyFromOther(supplier, session);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to add %s".formatted(Supplier.class), e);
            return false;
        }
    }

    @Override
    public boolean deleteSupplier(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Supplier supplier = session.get(Supplier.class, id);
            if (supplier == null) return false;
            session.remove(supplier);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to add %s".formatted(Supplier.class), e);
            return false;
        }
    }

    @Override
    public Supplier getSupplierById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Supplier.class, id);
        } catch (Exception e) {
            Helper.showError("Failed to get Supplier", e);
            return null;
        }
    }

    @Override
    public Supplier getSupplierByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Supplier WHERE siteName = :name", Supplier.class)
                    .setParameter("name", name)
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to get Supplier by name", e);
            return null;
        }
    }

    @Override
    public ArrayList<Supplier> getSuppliersByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (ArrayList<Supplier>) session.createQuery("FROM Supplier WHERE id IN (:ids)", Supplier.class)
                    .setParameterList("ids", ids)
                    .list();
        } catch (Exception e) {
            Helper.showError("Failed to get Suppliers by IDs", e);
            return null;
        }
    }

    @Override
    public List<Integer> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT id FROM Supplier", Integer.class).list();
        } catch (Exception e) {
            Helper.showError("Failed to get all Supplier IDs", e);
            return new ArrayList<>();
        }
    }
}
