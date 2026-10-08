package DB.implementations.sqlite.transport;

import Auxiliary.Helper;
import DB.interfaces.transport.IBranchController;
import DB.util.HibernateUtil;
import Models.DBModels.transport.Branch;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class SqliteBranchController implements IBranchController {
    private static SqliteBranchController instance;

    public static SqliteBranchController getInstance() {
        if (instance == null) {
            instance = new SqliteBranchController();
        }
        return instance;
    }

    @Override
    public int addBranch(Branch branch) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(branch);
            //session.flush(); // Force the ID generation before commit
            session.getTransaction().commit();
            return branch.getId();
        } catch (Exception e) {
            Helper.showError("Failed to add Branch", e);
            return Helper.UndefinedId;
        }
    }

    @Override
    public boolean updateBranch(Branch branch, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Branch existing = session.get(Branch.class, id);
            if (existing == null) return false;
            existing.copyFromOther(branch, session);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Helper.showError("Failed to update Branch", e);
            return false;
        }
    }

    @Override
    public boolean deleteBranch(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Branch branch = session.get(Branch.class, id);
            if (branch != null) {
                session.remove(branch);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            Helper.showError("Failed to delete Branch", e);
            return false;
        }
    }

    @Override
    public Branch getBranchById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Branch.class, id);
        } catch (Exception e) {
            Helper.showError("Failed to get Branch by ID", e);
            return null;
        }
    }

    @Override
    public Branch getBranchByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Branch WHERE siteName = :name", Branch.class)
                    .setParameter("name", name)
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to get Branch by name", e);
            return null;
        }
    }

    @Override
    public Branch getBranchByAddress(String address) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Branch WHERE siteAddress = :address", Branch.class)
                    .setParameter("address", address)
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to get Branch by address", e);
            return null;
        }
    }

    @Override
    public Branch getBranchByPhoneNumber(String phoneNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Branch WHERE sitePhone = :phone", Branch.class)
                    .setParameter("phone", phoneNumber)
                    .uniqueResult();
        } catch (Exception e) {
            Helper.showError("Failed to get Branch by phone number", e);
            return null;
        }
    }

    @Override
    public List<Branch> getBranchesByIds(List<Integer> ids) {

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Branch WHERE id IN :ids", Branch.class)
                    .setParameterList("ids", ids)
                    .list();
        } catch (Exception e) {
            Helper.showError("Failed to get Branches by IDs", e);
            return null;
        }
    }

    @Override
    public List<Integer> getAllIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT id FROM Branch", Integer.class).list();
        } catch (Exception e) {
            Helper.showError("Failed to get all Branch IDs", e);
            return new ArrayList<>();
        }
    }
}
