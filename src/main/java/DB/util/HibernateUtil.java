package DB.util;

import Auxiliary.GlobalConfig;
import Auxiliary.Helper;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.logging.*;
import java.lang.management.ManagementFactory;

public class HibernateUtil {
    private static SessionFactory sessionFactory = null;

    /**
     * Function to invalidate the current session factory.
     */
    public static void invalidateSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    /**
     * Function to build the session factory.
     *
     * @return SessionFactory
     */
    private static SessionFactory buildSessionFactory() {
        if (!ManagementFactory.getRuntimeMXBean().getInputArguments()
                .toString().contains("-agentlib:jdwp")) {
            Logger hibernateLogger = Logger.getLogger("org.hibernate");
            hibernateLogger.setLevel(Level.OFF);

            for (Handler handler : hibernateLogger.getHandlers()) {
                handler.setLevel(Level.OFF);
            }
        }

        try {
            // Use programmatic configuration to avoid XML parsing issues
            Configuration configuration = new Configuration();

            // Database settings
            boolean isTestMode = GlobalConfig.getDatabaseConfigUrl().contains("test");
            String dbUrl = isTestMode ? "jdbc:sqlite:losPollosHermanosTestingOnly.db" : "jdbc:sqlite:losPollosHermanos.db";

            configuration.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
            configuration.setProperty("hibernate.connection.url", dbUrl);
            configuration.setProperty("dialect", "org.hibernate.community.dialect.SQLiteDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");

            // Debugging settings
            configuration.setProperty("hibernate.show_sql", "false"); // can be later changed from "false" to Globalconfig.logsql()
            configuration.setProperty("hibernate.format_sql", isTestMode ? "true" : "false");
            configuration.setProperty("hibernate.generate_statistics", "false");
            configuration.setProperty("hibernate.use_sql_comments", "false");

            // Enable parameter value logging for debugging (optional)
            if (isTestMode || GlobalConfig.logSql().equals("true")) {
                configuration.setProperty("org.hibernate.SQL", "DEBUG");
                configuration.setProperty("org.hibernate.type.descriptor.sql.BasicBinder", "TRACE");
            }

            // CRITICAL: Disable all bytecode enhancement to prevent the ClassCastException
            configuration.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
            configuration.setProperty("hibernate.bytecode.provider", "none");
            configuration.setProperty("hibernate.enhance.enableLazyInitialization", "false");
            configuration.setProperty("hibernate.enhance.enableDirtyTracking", "false");
            configuration.setProperty("hibernate.enhance.enableAssociationManagement", "false");

            // Add all entity classes
            addEntityClasses(configuration);

            return configuration.buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Failed to create SessionFactory: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }


    private static void addEntityClasses(Configuration configuration) {
        // Transport entities
        configuration.addAnnotatedClass(Models.DBModels.transport.Driver.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.Branch.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.DeliveryFileItem.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.DeliveryFile.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.APhysicalSite.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.TransportRoute.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.TransportArea.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.Truck.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.TransportIssueLog.class);
        configuration.addAnnotatedClass(Models.DBModels.transport.Transport.class);
        configuration.addAnnotatedClass(Models.DBModels.suppliers.Supplier.class);

        // Employee entities
        configuration.addAnnotatedClass(Models.DBModels.Employees.Employee.class);
        configuration.addAnnotatedClass(Models.DBModels.Employees.Shift.class);
        configuration.addAnnotatedClass(Models.DBModels.Employees.Availability.class);
        configuration.addAnnotatedClass(Models.DBModels.Employees.ARole.class);
        configuration.addAnnotatedClass(Models.DBModels.Employees.Contract.class);
        configuration.addAnnotatedClass(Models.DBModels.Employees.BankAccount.class);

        // Employee Role entities
        configuration.addAnnotatedClass(Domain.Employees.Cashier.class);
        configuration.addAnnotatedClass(Domain.Employees.Shift_Manager.class);
        configuration.addAnnotatedClass(Domain.Employees.HR_Lead.class);
        configuration.addAnnotatedClass(Domain.Employees.Cleaner.class);
        configuration.addAnnotatedClass(Domain.Employees.Security.class);
        configuration.addAnnotatedClass(Domain.Employees.Stocker.class);
        configuration.addAnnotatedClass(Domain.Employees.Driver.class);
    }

    /**
     * Function to get the session factory.
     *
     * @return SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            sessionFactory = buildSessionFactory();
        }
        if (sessionFactory.isClosed()) {
            System.err.println("Failed to create SessionFactory");
            throw new ExceptionInInitializerError();
        }
        return sessionFactory;
    }


    /**
     * Function to delete the database files.
     *
     * @return true if the files were deleted successfully, false otherwise
     */
    public static boolean deleteDatabaseFiles() {
        SessionFactory _sessionFactory = getSessionFactory();
        if (_sessionFactory != null) {
            _sessionFactory.close();
            sessionFactory = null;
        }
        invalidateSessionFactory();
        return deleteDatabaseFiles(GlobalConfig.DATABASE_FILE_NAME) &&
                deleteDatabaseFiles(GlobalConfig.DATABASE_TESTING_FILE_NAME);
    }

    /**
     * Function to delete the database files.
     *
     * @param dbFilePath the path to the database file
     * @return true if the file was deleted successfully, false otherwise
     */
    private static boolean deleteDatabaseFiles(String dbFilePath) {
        try {
            File dbFile = new File(dbFilePath);
            if (dbFile.exists()) {
                return dbFile.delete();
            }
        } catch (Exception e) {
            Helper.showError("Error deleting database file: " + e);
        }
        return false;
    }
}
