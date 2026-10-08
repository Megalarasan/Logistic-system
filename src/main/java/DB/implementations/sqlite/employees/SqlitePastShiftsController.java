package DB.implementations.sqlite.employees;

import Auxiliary.Helper;
import DB.interfaces.employees.IPastShiftsController;
import DB.util.HibernateUtil;
import Models.DBModels.Employees.Shift;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqlitePastShiftsController implements IPastShiftsController {
    private static SqlitePastShiftsController instance;

    private SqlitePastShiftsController() {
        // Private constructor for singleton pattern
    }

    public static SqlitePastShiftsController getInstance() {
        if (instance == null) {
            instance = new SqlitePastShiftsController();
        }
        return instance;
    }

    @Override
    public boolean addPastShift(Shift shift) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            // Use merge instead of persist to handle detached entities
            session.merge(shift);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to add past shift", e);
            return false;
        }
    }

    @Override
    public boolean removePastShift(Shift shift) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Shift existingShift = session.get(Shift.class, shift.getId());
            if (existingShift != null) {
                session.remove(existingShift);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            Helper.showError("Failed to remove past shift", e);
            return false;
        }
    }

    @Override
    public Shift findShiftById(String shiftId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Shift.class, shiftId);
        } catch (Exception e) {
            Helper.showError("Failed to find past shift by ID", e);
            return null;
        }
    }

    @Override
    public List<Shift> getPastShifts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Shift WHERE isPast = true", Shift.class).list();
        } catch (Exception e) {
            Helper.showError("Failed to get past shifts", e);
            return new ArrayList<>();
        }
    }

    @Override
    public int removeShiftsOlderThan(LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            int count = session.createQuery("DELETE FROM Shift WHERE isPast = true AND shiftDate < :date")
                    .setParameter("date", date)
                    .executeUpdate();
            session.getTransaction().commit();
            return count;
        } catch (Exception e) {
            Helper.showError("Failed to remove old past shifts", e);
            return 0;
        }
    }

    @Override
    public void removeOlderThen7YearsShifts() {
        LocalDate today = LocalDate.now();
        LocalDate sevenYearsAgo = today.minusYears(7);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            int removedCount = session.createQuery("DELETE FROM Shift WHERE isPast = true AND shiftDate < :date")
                    .setParameter("date", sevenYearsAgo)
                    .executeUpdate();
            session.getTransaction().commit();
            System.out.println("Removed " + removedCount + " old past shifts.");
        } catch (Exception e) {
            Helper.showError("Failed to remove old past shifts", e);
        }
    }

    @Override
    public List<String> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT shiftID FROM Shift WHERE isPast = true", String.class).list();
        } catch (Exception e) {
            Helper.showError("Failed to get all past shift IDs", e);
            return new ArrayList<>();
        }
    }
}
