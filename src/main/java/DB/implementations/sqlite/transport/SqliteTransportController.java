package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ITransportController;
import DB.util.HibernateUtil;
import Models.DBModels.transport.Transport;
import Models.DBModels.transport.TransportIssueLog;
import Models.DBModels.transport.TransportRoute;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public class SqliteTransportController implements ITransportController {
    private static SqliteTransportController controller;

    private SqliteTransportController() {
    }

    public static SqliteTransportController getInstance() {
        if (controller == null) {
            controller = new SqliteTransportController();
        }
        return controller;
    }

    private void initializeTransport(Transport transport) {
        if (transport != null) {
            Hibernate.initialize(transport.getTransportRoutes());
            Hibernate.initialize(transport.getTransportIssueLogs());
        }
    }

    private void linkListsForTransport(Transport transport, Session session) {
        if (transport.getTransportRoutes() != null) {
            for (TransportRoute route : transport.getTransportRoutes()) {
                route.setTransport(transport);
            }
        }
        if (transport.getTransportIssueLogs() != null) {
            for (TransportIssueLog log : transport.getTransportIssueLogs()) {
                log.setTransport(transport);
            }
        }
    }

    @Override
    public int addTransport(Transport transport) {
        if (transport == null || transport.getTransportRoutes() == null) {
            return Helper.UndefinedId;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            linkListsForTransport(transport, session);
            Transport merged = session.merge(transport);
            session.getTransaction().commit();
            return merged.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add transport route", e);
            return Helper.UndefinedId;
        }
    }

    @Override
    public boolean updateTransport(Transport transport, int id) {
        if (transport == null) {
            return false;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Transport existingTransport = session.get(Transport.class, id);
            if (existingTransport != null) {
                existingTransport.copyFromOther(transport, session);
                session.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            Helper.showError("Failed to update transport", e);
        }
        return false;
    }

    @Override
    public boolean deleteTransport(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Transport transport = session.get(Transport.class, id);
            if (transport != null) {
                session.remove(transport);
                session.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            Helper.showError("Failed to delete transport route", e);
        }
        return false;
    }

    @Override
    public Transport getTransportById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Transport transport = session.get(Transport.class, id);
            initializeTransport(transport);
            session.getTransaction().commit();
            return transport;
        } catch (Exception e) {
            Helper.showError("Failed to get transport route by ID", e);
            return null;
        }
    }

    @Override
    public List<Transport> getTransportsByTruckId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            List<Transport> transports = session.createQuery("FROM Transport WHERE truck.id = :truckId", Transport.class)
                    .setParameter("truckId", id)
                    .getResultList();
            for (Transport transport : transports) {
                initializeTransport(transport);
            }
            session.getTransaction().commit();
            return transports;
        } catch (Exception e) {
            Helper.showError("Failed to get transports by truck ID", e);
            return List.of();
        }
    }

    @Override
    public List<Transport> getTransportsByDriverId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            List<Transport> transports = session.createQuery("FROM Transport WHERE driver.id = :driverId", Transport.class)
                    .setParameter("driverId", id)
                    .getResultList();
            for (Transport transport : transports) {
                initializeTransport(transport);
            }
            session.getTransaction().commit();
            return transports;
        } catch (Exception e) {
            Helper.showError("Failed to get transports by driver ID", e);
            return List.of();
        }
    }

    @Override
    public List<Transport> getTransportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            List<Transport> transports = session.createQuery("FROM Transport WHERE " +
                            "plannedStartDateTime BETWEEN :startDate AND :endDate", Transport.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
            for (Transport transport : transports) {
                initializeTransport(transport);
            }
            session.getTransaction().commit();
            return transports;
        } catch (Exception e) {
            Helper.showError("Failed to get transports by date range", e);
            return List.of();
        }
    }

    @Override
    public List<Integer> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            List<Integer> ids = session.createQuery("SELECT id FROM Transport", Integer.class).getResultList();
            session.getTransaction().commit();
            return ids;
        } catch (Exception e) {
            Helper.showError("Failed to get all transport IDs", e);
            return List.of();
        }
    }
}
