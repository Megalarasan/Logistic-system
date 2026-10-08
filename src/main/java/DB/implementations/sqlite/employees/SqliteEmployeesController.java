package DB.implementations.sqlite.employees;
import Auxiliary.Enums.Domain.Status;
import Auxiliary.Helper;
import DB.interfaces.employees.IEmployeeController;
import DB.util.HibernateUtil;
import Models.DBModels.Employees.Employee;
import Models.DBModels.Employees.ARole;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class SqliteEmployeesController implements IEmployeeController {
    private static SqliteEmployeesController instance;

    public static SqliteEmployeesController getInstance() {
        if (instance == null) {
            instance = new SqliteEmployeesController();
        }
        return instance;
    }

    public Status addEmployeeToDB(Employee employee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Reset IDs for all related entities to avoid conflicts from IdGenerator
            // Reset employee ID to let Hibernate assign proper ID
            employee.setId(0);

            // Handle roles - reset IDs and persist each role
            if (employee.getRolesList() != null) {
                for (ARole role : employee.getRolesList()) {
                    if (role != null) {
                        // Reset the ID to 0 to let Hibernate assign a proper unique ID
                        role.setId(0);
                        // Persist the role
                        session.persist(role);
                    }
                }
            }

            // Handle bank account - reset ID
            if (employee.getBankInfo() != null) {
                employee.getBankInfo().setId(0);
                session.persist(employee.getBankInfo());
            }

            // Handle contract - reset ID
            if (employee.getEmployeeCondition() != null) {
                employee.getEmployeeCondition().setId(0);
                session.persist(employee.getEmployeeCondition());
            }

            // Handle availability - reset ID
            if (employee.getAvailability() != null) {
                employee.getAvailability().setId(0);
                session.persist(employee.getAvailability());
            }

            // Flush to ensure all related entities are saved first
            session.flush();

            // Now persist the employee
            session.persist(employee);
            session.getTransaction().commit();
            return Status.Success;
        } catch (Exception e) {
            Helper.showError("Failed to add Employee", e);
            return Status.Failure;
        }
    }

    public Status updateEmployee(Employee updatedEmployee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First get the current employee from the database
            Employee existingEmployee = session.get(Employee.class, updatedEmployee.getId());
            if (existingEmployee == null) {
                session.getTransaction().rollback();
                return Status.Failure;
            }

            // Handle roles - simple approach: each employee gets their own role instances
            if (updatedEmployee.getRolesList() != null) {
                for (ARole role : updatedEmployee.getRolesList()) {
                    if (role != null) {
                        // Reset the ID to 0 to let Hibernate assign a proper unique ID
                        // This avoids conflicts from IdGenerator
                        role.setId(0);
                        // Persist the role - each employee gets their own role instance
                        session.persist(role);
                    }
                }
                // Flush to ensure all new roles are saved
                session.flush();
            }

            // Copy data from updated employee to existing one
            existingEmployee.copyFromOther(updatedEmployee, session);

            // Save the updated entity
            session.merge(existingEmployee);
            session.getTransaction().commit();
            return Status.Success;
        } catch (Exception e) {
            Helper.showError("Failed to update Employee", e);
            return Status.Failure;
        }
    }





    // This method is used to remove an employee by their ID
    public Status removeEmployeeFromDB(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            // Use HQL query to find employee by employeeID field, not primary key
            Employee employee = session.createQuery("FROM Employee WHERE employeeID = :empId", Employee.class)
                    .setParameter("empId", id)
                    .uniqueResult();
            if (employee != null) {
                session.remove(employee);
                session.getTransaction().commit();
                return Status.Success;
            }
            return Status.Failure;
        } catch (Exception e) {
            Helper.showError("Failed to delete Employee", e);
            return Status.Failure;
        }
    }

    // This method is used to find an employee by their ID
    public Employee findEmployeeById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Use HQL query to find employee by employeeID field, not primary key
            return session.createQuery("FROM Employee WHERE employeeID = :empId", Employee.class)
                    .setParameter("empId", id)
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to get employee by ID", e);
            return null;
        }
    }

    @Override
    public List<String> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<String> ids = session.createQuery("SELECT employeeID FROM Employee", String.class).getResultList();
            return ids;
        } catch (Exception e) {
            Helper.showError("Failed to get all employees IDs", e);
            return new ArrayList<>();
        }
    }

}
