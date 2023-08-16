package org.data.Handlers;

import java.io.IOException;
import java.util.List;

/**
 * Data layer interface for handling all metadata operations
 */
public interface IMetaDataHandler {

    public  void verifyAuthenticationMetaData() throws IOException;
    public  boolean saveTableMetaData (String username, String databaseName, String tableName, List<String> allColumnNames, List<String> allColumnDataTypes, List<String> allColumnConstraints, List<String> PKColumns, List<String> FKColumns);

    public  List<String> getAllColumnNames(String username, String databaseName, String tableName);
    public  String getRawTableMetadata(String username, String databaseName, String tableName);



}
