package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ITransportIssueLogController;
import DB.util.HibernateUtil;
import Models.DBModels.transport.TransportIssueLog;
import org.hibernate.Session;

import java.util.List;

public class SqliteTransportIssueLogController implements ITransportIssueLogController {
    private static SqliteTransportIssueLogController instance = null;

    private SqliteTransportIssueLogController() {
    }

    public static SqliteTransportIssueLogController getInstance() {
        if (instance == null) {
            instance = new SqliteTransportIssueLogController();
        }
        return instance;
    }

    @Override
    public int addTransportIssueLog(TransportIssueLog transportIssueLog) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(transportIssueLog);
            session.getTransaction().commit();
            return transportIssueLog.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add transport issue log", e);
            return Helper.UndefinedId;
        }
    }

    @Override
    public boolean updateTransportIssueLog(TransportIssueLog transportIssueLog, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            TransportIssueLog existingLog = session.get(TransportIssueLog.class, id);
            if (existingLog != null) {
                existingLog.copyFromOther(transportIssueLog, session);
                session.getTransaction().commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Helper.showError("Failed to update transport issue log", e);
            return false;
        }
    }

    @Override
    public boolean deleteTransportIssueLog(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            TransportIssueLog transportIssueLog = session.get(TransportIssueLog.class, id);
            if (transportIssueLog != null) {
                session.remove(transportIssueLog);
                session.getTransaction().commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Helper.showError("Failed to delete transport issue log", e);
            return false;
        }
    }

    @Override
    public TransportIssueLog getTransportIssueLogById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TransportIssueLog.class, id);
        } catch (Exception e) {
            Helper.showError("Failed to get transport issue log by ID", e);
            return null;
        }
    }

    @Override
    public List<Integer> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT id FROM TransportIssueLog", Integer.class).getResultList();
        } catch (Exception e) {
            Helper.showError("Failed to get all transport issue log IDs", e);
            return null;
        }
    }
}
