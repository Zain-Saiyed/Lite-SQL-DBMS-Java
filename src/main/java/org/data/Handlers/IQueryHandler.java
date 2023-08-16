package org.data.Handlers;

/**
 * Service layer interface for handling query level operations only
 */
public interface IQueryHandler {

    public String getQueryType(String query) ;

    public boolean verifyWellFormednessQuery(String query, String queryType);
    public String showAllDatabases(String username);
    public void createDatabase(String username, String query);
    public String useDatabase(String username, String query);

    public boolean createTable(String username, String databaseName, String query);

    public boolean insertDataIntoTable(String username, String databaseName, String query);
    public String selectDataFromTable(String username, String databaseName, String query);

    public void deleteDataFromTable(String username, String databaseName, String query);

    public void updateTableData(String username, String databaseName, String query);

    public String getErd(String username, String databaseName);

}
