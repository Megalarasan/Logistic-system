package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.util.HibernateUtil;
import DB.interfaces.transport.IDriverController;
import Models.DBModels.transport.Driver;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class SqliteDriverController implements IDriverController {
    private static SqliteDriverController instance;

    private SqliteDriverController() {
        // Private constructor to prevent instantiation
    }

    public static SqliteDriverController getInstance() {
        if (instance == null) {
            instance = new SqliteDriverController();
        }
        return instance;
    }

    @Override
    public int addDriver(Driver driver) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(driver);
            session.getTransaction().commit();
            return driver.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add Driver", e);
            return Helper.UndefinedId;
        }
    }

    @Override
    public boolean updateDriver(Driver driver, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Driver existing = session.get(Driver.class, id);
            if (existing == null) return false;
            existing.copyFromOther(driver, session);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to update Driver", e);
            return false;
        }
    }

    @Override
    public boolean deleteDriver(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Driver driver = session.get(Driver.class, id);
            if (driver != null) {
                session.remove(driver);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            Helper.showError("Failed to delete Driver", e);
            return false;
        }
    }

    @Override
    public Driver getDriverById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Driver.class, id);
        } catch (Exception e) {
            Helper.showError("Failed to get Driver by ID", e);
            return null;
        }
    }

    @Override
    public List<Driver> getDriverByFullName(String firstName, String lastName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Driver WHERE firstName = :fn AND lastName = :ln", Driver.class)
                    .setParameter("fn", firstName)
                    .setParameter("ln", lastName).list();
        } catch (Exception e) {
            Helper.showError("Failed to get Driver by full name", e);
            return null;
        }
    }

    @Override
    public Driver getDriveByPersonalId(String personalId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Driver WHERE personalId = :pid", Driver.class)
                    .setParameter("pid", personalId)
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to get Driver by personal ID", e);
            return null;
        }
    }

    @Override
    public Driver getDriverByPhoneNumber(String phoneNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Driver WHERE phoneNumber = :ph", Driver.class)
                    .setParameter("ph", phoneNumber)
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to get Driver by phone number", e);
            return null;
        }
    }

    @Override
    public List<Integer> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var query = session.createQuery("SELECT id FROM Driver", Integer.class);
            return query.list();
        } catch (Exception e) {
            Helper.showError("Failed to get all Driver IDs", e);
            return new ArrayList<>();
        }
    }
}
