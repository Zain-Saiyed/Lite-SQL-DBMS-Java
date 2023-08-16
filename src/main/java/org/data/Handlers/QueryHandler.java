package org.data.Handlers;

import org.data.DataAccessManager.FileStorageAccessManager;
import org.data.DataAccessManager.IFileStorageAccessManager;
import org.data.Entity.PatternMatcher;

import java.util.logging.Logger;

/**
 * Service layer class for handling query level operations only
 */
public class QueryHandler implements IQueryHandler{
    PatternMatcher patternMatcher = new PatternMatcher();
    IFileStorageAccessManager iFileStorageAccessManager = new FileStorageAccessManager();
    private static final Logger LOGGER = Logger.getLogger(QueryHandler.class.getName());

    public String getQueryType(String query)  {
        return patternMatcher.getQueryType(query);
    }

    /**
     * function that calls service layer and returns boolean result if query syntax is well formed or incorrect
     *
     * @param query
     * @param queryType
     * @return
     */
    public boolean verifyWellFormednessQuery(String query, String queryType) {
        return patternMatcher.verifyWellFormednessQuery(query,queryType);
    }

    /**
     * function which returns the String result containing all the name of the database that a user has created in his account
     * @param username
     * @return
     */
    public String showAllDatabases(String username) {
        return iFileStorageAccessManager.showAllDatabases(username);
    }

    /**
     * function for calling data layer to create a new database
     * @param username
     * @param query
     */
    public void createDatabase(String username, String query) {
        boolean status = iFileStorageAccessManager.createUserDataRepository(username);
        if (status) {
            String response = iFileStorageAccessManager.createDatabase(username,query);
            if (response!=null) {
                LOGGER.info("[*SUCCESS*] Database [" +response+ "] created successfully!");
                System.out.println("[*SUCCESS*] Database [" +response+ "] created successfully!");
            }
        }
    }

    /**
     * function for selecting the database for further table level operations to be performed
     * @param username
     * @param query
     * @return
     */
    public String useDatabase(String username, String query) {
        String databaseName = patternMatcher.getDatabaseName(query,patternMatcher.USE_SYNTAX);
        if (iFileStorageAccessManager.isDatabase(username,databaseName)) {
            return databaseName;
        }
        return null;
    }

    /**
     * function for creating a table inside a already selected database in teh user's repository
     * @param username
     * @param databaseName
     * @param query
     * @return
     */
    public boolean createTable(String username, String databaseName, String query) {
        boolean status = iFileStorageAccessManager.createTable(username, databaseName, query);
        return status;
    }

    /**
     * function for inserting data into a table by calling the data layer function
     * @param username
     * @param databaseName
     * @param query
     * @return
     */
    public boolean insertDataIntoTable(String username, String databaseName, String query) {
        boolean status = iFileStorageAccessManager.insertDataIntoTable(username, databaseName, query);
        return status;
    }

    /**
     * function for retrieving the selected data from a table based on users query.
     * @param username
     * @param databaseName
     * @param query
     * @return
     */
    public String selectDataFromTable(String username, String databaseName, String query) {
        return iFileStorageAccessManager.selectDataFromTable(username, databaseName, query);
    }

    /**
     * function that calls the data layer to delete data from a table based on user's query
     * @param username
     * @param databaseName
     * @param query
     */
    public void deleteDataFromTable(String username, String databaseName, String query) {
        String record = iFileStorageAccessManager.deleteDataFromTable(username, databaseName, query);
        if (record!=null) {
            LOGGER.info("[*SUCCESS*] Delete operation successful. Number of row(s) affected: "+record);
            System.out.println("[*SUCCESS*] Delete operation successful. Number of row(s) affected: " + record);
        }
    }

    /**
     * function that calls the data later for updating data from a table based on user's query
     * @param username
     * @param databaseName
     * @param query
     */
    public void updateTableData(String username, String databaseName, String query) {
        String record = iFileStorageAccessManager.updateTableData(username, databaseName, query);
        if (record!=null) {
            LOGGER.info("[*SUCCESS*] Update operation successful. Number of row(s) affected: "+record);
            System.out.println("[*SUCCESS*] Update operation successful. Number of row(s) affected: "+record);
        }
    }

    public String getErd(String username, String databaseName) {
        return iFileStorageAccessManager.getErd(username, databaseName);
    }
}
