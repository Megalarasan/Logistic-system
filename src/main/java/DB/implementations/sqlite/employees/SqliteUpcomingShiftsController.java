package DB.implementations.sqlite.employees;

import Auxiliary.Helper;
import DB.interfaces.employees.IPastShiftsController;
import DB.interfaces.employees.IUpcomingShiftsController;
import DB.util.HibernateUtil;
import Models.DBModels.Employees.Shift;
import Models.DBModels.transport.Branch;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SqliteUpcomingShiftsController implements IUpcomingShiftsController {
    private static SqliteUpcomingShiftsController instance;
    private final IPastShiftsController pastShiftsController;

    private SqliteUpcomingShiftsController() {
        // Private constructor for singleton pattern
        this.pastShiftsController = SqlitePastShiftsController.getInstance();
    }

    public static SqliteUpcomingShiftsController getInstance() {
        if (instance == null) {
            instance = new SqliteUpcomingShiftsController();
        }
        return instance;
    }

    @Override
    public boolean addUpcomingShift(Shift shift) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            // Use merge instead of persist to handle detached entities
            session.merge(shift);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to add upcoming shift", e);
            return false;
        }
    }

    @Override
    public boolean removeUpcomingShift(Shift shift) {
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
            Helper.showError("Failed to remove upcoming shift", e);
            return false;
        }
    }

    public boolean updateShiftInDB(Shift updatedShift) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(updatedShift); // Update the shift in the database
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to update shift", e);
            return false;
        }
    }

    @Override
    public Shift findShiftById(String shiftId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT s FROM Shift s " +
                            "LEFT JOIN FETCH s.assignedEmployees " +
                            "LEFT JOIN FETCH s.allAvailableEmployees " +
                            "WHERE s.shiftID = :id",
                            Shift.class
                            ).setParameter("id", shiftId).uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to find upcoming shift by ID", e);
            return null;
        }
    }

    @Override
    public List<Shift> getUpcomingShifts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                            SELECT DISTINCT s
                            FROM Shift s
                            LEFT JOIN FETCH s.assignedEmployees
                            LEFT JOIN FETCH s.requiredRoles
                            LEFT JOIN FETCH s.allAvailableEmployees
                            WHERE s.isPast = false
                            """, Shift.class).list();
        } catch (Exception e) {
            Helper.showError("Failed to get upcoming shifts", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Shift findShiftByDateTimeAndBranch(LocalDate date, LocalTime time, Branch branch) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                                        SELECT s
                                        FROM Shift s
                                        LEFT JOIN FETCH s.assignedEmployees
                                        LEFT JOIN FETCH s.requiredRoles
                                        WHERE s.isPast = false AND s.shiftDate = :date
                                        AND :time BETWEEN s.startShiftTime AND s.endShiftTime
                                        AND s.branch.id = :branchId
                                    """, Shift.class)
                    .setParameter("date", date)
                    .setParameter("time", time)
                    .setParameter("branchId", branch.getId())
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to find shift by date, time, and branch", e);
            return null;
        }
    }

    @Override
    public int moveShiftsToPast() {
        LocalDate today = LocalDate.now();
        int count = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            List<Shift> shiftsToMove = session.createQuery("""
    SELECT DISTINCT s
    FROM Shift s
    LEFT JOIN FETCH s.assignedEmployees
    LEFT JOIN FETCH s.requiredRoles
    WHERE s.isPast = false AND s.shiftDate < :today
""", Shift.class)
                    .setParameter("today", today)
                    .list();

            for (Shift shift : shiftsToMove) {
                shift.setPast(true);
                // Update the shift in the current session
                session.merge(shift);
                count++;
            }

            session.getTransaction().commit();
            return count;
        } catch (Exception e) {
            Helper.showError("Failed to move shifts to past", e);
            return 0;
        }
    }

    @Override
    public List<String> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT shiftID FROM Shift WHERE isPast = false", String.class).list();
        } catch (Exception e) {
            Helper.showError("Failed to get all upcoming shift IDs", e);
            return new ArrayList<>();
        }
    }
}
