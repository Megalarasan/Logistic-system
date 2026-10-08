package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ITransportRouteController;
import DB.util.HibernateUtil;
import Models.DBModels.transport.TransportRoute;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class SqliteTransportRouteController implements ITransportRouteController {
    private static SqliteTransportRouteController instance;

    public static SqliteTransportRouteController getInstance() {
        if (instance == null) {
            instance = new SqliteTransportRouteController();
        }
        return instance;
    }

    private SqliteTransportRouteController() {
    }

    @Override
    public int addTransportRoute(TransportRoute transportRoute) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(transportRoute);
            session.getTransaction().commit();
            return transportRoute.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add transport route", e);
            return Helper.UndefinedId;
        }
    }

    @Override
    public boolean updateTransportRoute(TransportRoute transportRoute, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            TransportRoute existing = session.get(TransportRoute.class, id);
            if (existing == null) return false;
            existing.copyFromOther(transportRoute, session);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to update transport route", e);
            return false;
        }
    }

    @Override
    public boolean deleteTransportRoute(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            TransportRoute transportRoute = session.get(TransportRoute.class, id);
            if (transportRoute == null) return false;
            session.remove(transportRoute);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to delete transport route", e);
            return false;
        }
    }

    @Override
    public TransportRoute getTransportRouteById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TransportRoute.class, id);
        } catch (Exception e) {
            Helper.showError("Failed to get transport route by ID", e);
            return null;
        }
    }

    @Override
    public ArrayList<TransportRoute> getTransportRoutesByIds(ArrayList<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ArrayList<TransportRoute> transportRoutes = new ArrayList<>();
            for (Integer id : ids) {
                TransportRoute transportRoute = session.get(TransportRoute.class, id);
                if (transportRoute != null) {
                    transportRoutes.add(transportRoute);
                }
            }
            return transportRoutes;
        } catch (Exception e) {
            Helper.showError("Failed to get transport routes by IDs", e);
            return null;
        }
    }

    @Override
    public List<Integer> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT id FROM TransportRoute", Integer.class).list();
        } catch (Exception e) {
            Helper.showError("Failed to get all transport route IDs", e);
            return new ArrayList<>();
        }
    }
}
