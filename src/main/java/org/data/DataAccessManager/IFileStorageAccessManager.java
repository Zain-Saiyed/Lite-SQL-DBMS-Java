package org.data.DataAccessManager;

/**
 * Interface of Data layer which defines the generic behavior handling all the data file level access operations
 */
public interface IFileStorageAccessManager {

    public String getPassword(String username);

    public String getSecurityQuestionAnswer(String username, String type);

    public boolean signUpUserDetails(String username, String password, String question, String answer);

    public String showAllDatabases(String username);

    public boolean createUserDataRepository(String username);

    public String createDatabase(String username, String query);

    boolean isDatabase(String username, String databaseName);

    boolean isTable(String username, String databaseName, String tableName);

    boolean createTable(String username, String databaseName, String query);

    boolean insertDataIntoTable(String username, String databaseName, String query);

    String selectDataFromTable(String username, String databaseName, String query);

    String deleteDataFromTable(String username, String databaseName, String query);

    String updateTableData(String username, String databaseName, String query);

    String getErd(String username, String databaseName);
}
