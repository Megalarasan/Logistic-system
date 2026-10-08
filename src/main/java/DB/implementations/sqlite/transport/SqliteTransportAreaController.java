package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ITransportAreaController;
import DB.util.HibernateUtil;
import Models.DBModels.transport.TransportArea;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.util.List;

public class SqliteTransportAreaController implements ITransportAreaController {
    private static SqliteTransportAreaController instance;

    private SqliteTransportAreaController() {
    }

    public static SqliteTransportAreaController getInstance() {
        if (instance == null) {
            instance = new SqliteTransportAreaController();
        }
        return instance;
    }

    @Override
    public int addTransportArea(TransportArea transportArea) {
        if (transportArea == null) {
            return Helper.UndefinedId;
        }
        if (transportArea.getSuppliers().isEmpty() || transportArea.getBranches().isEmpty()) {
            throw new IllegalArgumentException("A TransportArea must have at least one supplier and one branch.");
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(transportArea);
            //session.flush(); // Force the ID generation before commit
            session.getTransaction().commit();
            return transportArea.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add transport route", e);
            return Helper.UndefinedId;
        }
    }

    private void initializeTransportArea(TransportArea transportArea) {
        if (transportArea != null) {
            Hibernate.initialize(transportArea.getSuppliers());
            Hibernate.initialize(transportArea.getBranches());
        }
    }

    @Override
    public TransportArea getTransportAreaById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            TransportArea transportArea = session.get(TransportArea.class, id);
            initializeTransportArea(transportArea);
            session.getTransaction().commit();
            return transportArea;
        } catch (Exception e) {
            Helper.showError("Failed to get transport area by ID", e);
            return null;
        }
    }

    @Override
    public boolean updateTransportArea(TransportArea transportArea, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            TransportArea existing = session.get(TransportArea.class, id);
            if (existing == null) return false;
            existing.copyFromOther(transportArea, session);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to update transport area", e);
            return false;
        }
    }

    @Override
    public boolean deleteTransportArea(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            TransportArea transportArea = session.get(TransportArea.class, id);
            if (transportArea == null) return false;
            session.remove(transportArea);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to delete transport area", e);
            return false;
        }
    }

    @Override
    public TransportArea getTransportAreaByAreaName(String areaName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            List<TransportArea> transportAreas = session.createQuery("FROM TransportArea WHERE areaName = :areaName",
                    TransportArea.class).setParameter("areaName", areaName).getResultList();
            session.getTransaction().commit();
            TransportArea area = transportAreas.isEmpty() ? null : transportAreas.getFirst();
            initializeTransportArea(area);
            return area;
        } catch (Exception e) {
            Helper.showError("Failed to get transport area by area name", e);
            return null;
        }
    }

    @Override
    public List<TransportArea> getAllTransportAreasByBranchId(int branchId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            List<TransportArea> transportAreas = session.createQuery("SELECT ta FROM TransportArea ta " +
                            "JOIN ta.branches b WHERE b.id = :branchId", TransportArea.class)
                    .setParameter("branchId", branchId).getResultList();

            for (TransportArea transportArea : transportAreas) {
                initializeTransportArea(transportArea);
            }

            session.getTransaction().commit();
            return transportAreas;
        } catch (Exception e) {
            Helper.showError("Failed to get all transport areas by branch ID", e);
            return null;
        }
    }

    @Override
    public List<Integer> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            List<Integer> ids = session.createQuery("SELECT id FROM TransportArea", Integer.class).getResultList();
            session.getTransaction().commit();
            return ids;
        } catch (Exception e) {
            Helper.showError("Failed to get all transport area IDs", e);
            return null;
        }
    }
}
