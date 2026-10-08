package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.ITruckController;
import DB.util.HibernateUtil;
import Models.DBModels.transport.Truck;
import org.hibernate.Session;

import java.util.List;

public class SqliteTruckController implements ITruckController {
    private static SqliteTruckController instance = null;

    private SqliteTruckController() {
    }

    public static SqliteTruckController getInstance() {
        if (instance == null) {
            instance = new SqliteTruckController();
        }
        return instance;
    }

    @Override
    public int addTruck(Truck truck) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(truck);
            session.getTransaction().commit();
            return truck.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add truck", e);
            return Helper.UndefinedId;
        }
    }

    @Override
    public boolean updateTruck(Truck truck, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Truck existingTruck = session.get(Truck.class, id);
            if (existingTruck != null) {
                existingTruck.copyFromOther(truck, session);
                session.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            Helper.showError("Failed to update truck", e);
        }
        return false;
    }

    @Override
    public boolean deleteTruck(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Truck truck = session.get(Truck.class, id);
            if (truck != null) {
                session.remove(truck);
                session.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            Helper.showError("Failed to delete truck", e);
        }
        return false;
    }

    @Override
    public Truck getTruckById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Truck.class, id);
        } catch (Exception e) {
            Helper.showError("Failed to get truck by ID", e);
            return null;
        }
    }

    @Override
    public Truck getTruckByPlate(String plate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Truck WHERE plate = :plate", Truck.class)
                    .setParameter("plate", plate)
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to get truck by plate", e);
            return null;
        }
    }

    @Override
    public List<Integer> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT id FROM Truck", Integer.class).list();
        } catch (Exception e) {
            Helper.showError("Failed to get all truck IDs", e);
            return null;
        }
    }
}
