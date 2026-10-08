package Auxiliary;

import DB.util.HibernateUtil;

/**
 * GlobalConfig is a utility class that manages the configuration settings for the application.
 */
public class GlobalConfig {
    private static final String DATABASE_CONFIG_URL = "hibernate.cfg.xml"; // default
    private static final String DATABASE_CONFIG_TESTING_URL = "hibernate-test.cfg.xml"; // testing only db
    private static boolean testMode = false;
    private static final boolean overrideAndShowSql = false;//change this manually to true to show sql queries
    public static final String DATABASE_FILE_NAME = "losPollosHermanos.db"; // default database file name
    public static final String DATABASE_TESTING_FILE_NAME = "losPollosHermanosTestingOnly.db"; // testing only db

    /**
     * Sets the test mode for the application.
     */
    public static void setTestMode(boolean testing) {
        HibernateUtil.invalidateSessionFactory();
        testMode = testing;
    }

    /**
     * Returns the database configuration URL based on the current mode (test or production).
     *
     * @return the database configuration URL
     */
    public static String getDatabaseConfigUrl() {
        return testMode ? DATABASE_CONFIG_TESTING_URL : DATABASE_CONFIG_URL;
    }


    /**
     * Returns the database print sql queries config
     *
     * @return the database print sql queries config
     */
    public static String logSql() {
        return testMode || overrideAndShowSql ? "true" : "false";
    }

}
