package org.data.DataAccessManager;

import org.data.Authentication.AuthHandler;
import org.data.Authentication.HashAlgorithm;
import org.data.Entity.PatternMatcher;
import org.data.Handlers.IMetaDataHandler;
import org.data.Handlers.MetaDataHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data layer class for handling all the data file level access operations
 */
public class FileStorageAccessManager implements IFileStorageAccessManager {

    private String basePath = "src/main/resources/";
    String printDelimeter = " ---- ";
    private static final Logger LOGGER = Logger.getLogger(FileStorageAccessManager.class.getName());

    PatternMatcher patternMatcher = new PatternMatcher();
    IMetaDataHandler iMetaDataHandler ;
    /**
     * Constructor: Check if Data Repository exists else create it.
     */
    public FileStorageAccessManager() {
        if (!Files.exists(Path.of(basePath+"DataRepository"))) {
            new File(basePath+"DataRepository").mkdirs();
        }
        iMetaDataHandler = new MetaDataHandler();
    }

    /**
     * function to retrieve the password from the loginInfo table based on the username
     * @param username
     * @return
     */
    @Override
    public String getPassword(String username) {
        String repositoryName = "AuthenticationRepository";
        String authTableName = "loginInfo.table";
        String password = null;
        try {
            BufferedReader tableReader = new BufferedReader(new FileReader(basePath+repositoryName+"/"+authTableName));
            String rowRecord = tableReader.readLine();
            while (rowRecord != null) {
                if (this.patternMatcher.findUsername(rowRecord,username)) {
                    password = rowRecord.split("<#EOD#>")[0].split("<#D#>")[1].strip();
                    break;
                }
                // else get next row in table
                rowRecord = tableReader.readLine();
            }
            // get password from table record
            tableReader.close();
        } catch (FileNotFoundException e) {
            LOGGER.severe("[*error*] Login table not found!");
            System.out.println("[*error*] Login table not found!");
            return null;
        } catch (IOException e) {
            LOGGER.severe("[*error*] Unable to read Login table!");
            System.out.println("[*error*] Unable to read Login table!");
            return null;
        }
        return password;
    }

    /**
     * retrieve the user's security question or answer based on the username and type
     * @param username
     * @param type
     * @return
     */
    @Override
    public String getSecurityQuestionAnswer(String username, String type) {
        String repositoryName = "AuthenticationRepository";
        String authTableName = "loginInfo.table";
        String result = null;
        try {
            BufferedReader tableReader = new BufferedReader(new FileReader(basePath+repositoryName+"/"+authTableName));
            String rowRecord = tableReader.readLine();
            while (rowRecord != null) {
                if (this.patternMatcher.findUsername(rowRecord,username)) {
                    if (type == "question")
                        result = rowRecord.replaceAll("<#EOD#>","").split("<#D#>")[2].strip();
                    else if (type == "answer")
                        result = rowRecord.replaceAll("<#EOD#>","").split("<#D#>")[3].strip();
                    break;
                }
                // else get next row in table
                rowRecord = tableReader.readLine();
            }
            // get password from table record
            tableReader.close();
        } catch (FileNotFoundException e) {
            LOGGER.severe("[*error*] Login table not found!");
            System.out.println("[*error*] Login table not found!");
            return null;
        } catch (IOException e) {
            LOGGER.severe("[*error*] Unable to read Login table!");
            System.out.println("[*error*] Unable to read Login table!");
            return null;
        }
        return result;
    }

    /**
     * function for signing up the use by saving the user's credentials into the loginTable
     * @param username
     * @param password
     * @param question
     * @param answer
     * @return
     */
    @Override
    public boolean signUpUserDetails(String username, String password, String question, String answer) {
        FileWriter fileHandlerObj = null;
        System.out.println("username = " + username);
        System.out.println("password = " + password);
        System.out.println("question = " + question);
        System.out.println("answer = " + answer);
        String repositoryName = "AuthenticationRepository";
        String authTableName = "loginInfo.table";
        HashAlgorithm hashAlgorithm = new HashAlgorithm();
        String hashedPassword = hashAlgorithm.generateSecureHashPassword(password);
        try (BufferedWriter filehandlerObjWriter = new BufferedWriter(new FileWriter(basePath + repositoryName +"/"+ authTableName, true))){
            filehandlerObjWriter.write(username+"<#D#>"+hashedPassword+"<#D#>"+question+"<#D#>"+answer+"<#EOD#>\n");
            filehandlerObjWriter.close();
        } catch (FileNotFoundException e) {
            LOGGER.severe("[**ERROR**] ERROR LOCATING TABLE! ERROR MESSAGE: " + e.getMessage());
            System.out.println("[**ERROR**] ERROR LOCATING TABLE! ERROR MESSAGE: " + e.getMessage());
            return false;
        } catch (IOException e) {
            LOGGER.severe("[**ERROR**] ERROR SAVING RESULT! ERROR MESSAGE: "+e.getMessage());
            System.out.println("[**ERROR**] ERROR SAVING RESULT! ERROR MESSAGE: "+e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * function to retrieve all the database names
     * @param username
     * @return
     */
    @Override
    public String showAllDatabases(String username) {
        if (!Files.exists(Path.of(basePath+"DataRepository"+"/"+username))) {
            new File(basePath+"DataRepository"+"/"+username).mkdirs();
        }
        File dataRepositoryDir = new File(basePath+"DataRepository/"+username);
        String allDatabaseNames = String.join("\n",dataRepositoryDir.list());
        return allDatabaseNames;
    }

    /**
     * function for creating/initialising the user's data repository in the data repository
     * @param username
     * @return
     */
    public boolean createUserDataRepository(String username) {
        // if username's respository doesn't exist then create it
        if (!Files.exists(Path.of(basePath + "DataRepository/" + username))) {
            boolean status = new File(basePath + "DataRepository/" + username).mkdirs();
            return status;
        }
        else
            return true;
    }

    /**
     * function for creating the database inside the user's data repository
     * @param username
     * @param query
     * @return
     */
    @Override
    public String createDatabase(String username, String query) {
        String databaseName = patternMatcher.getDatabaseName(query,patternMatcher.CREATE_DATABASE_SYNTAX);
        if (!Files.exists(Path.of(basePath + "DataRepository/" + username + "/" + databaseName))) {
            boolean status = this.createUserDataRepository(username + "/" + databaseName);
            return databaseName;
        } else {
            LOGGER.severe("[*ERROR*] Unable to Create Database. Database already exists!");
            System.out.println("[*ERROR*] Unable to Create Database. Database already exists!");
            return null;
        }
    }

    /**
     * Check if Data base exists in the user's data repository based on databaseName
     * @param username
     * @param databaseName
     * @return
     */
    @Override
    public boolean isDatabase(String username, String databaseName) {
        return Files.exists(Path.of(basePath + "DataRepository/" + username + "/" + databaseName));
    }

    /**
     * Check if table exists in the user's database based on tableName
     * @param username
     * @param databaseName
     * @param tableName
     * @return
     */
    @Override
    public boolean isTable(String username, String databaseName, String tableName) {
        return Files.exists(Path.of(basePath + "DataRepository/" + username + "/" + databaseName+"/"+tableName+".table"));
    }

    /**
     * function for performing create table operation
     * @param username
     * @param databaseName
     * @param tableName
     * @param allColumnNames
     * @return
     */
    private boolean createTableDefinition(String username, String databaseName, String tableName, List<String> allColumnNames){
        // save the metadata file for the new table
        FileWriter tableFile = null;
        try {
            tableFile = new FileWriter(basePath+"DataRepository"+"/"+username+"/"+databaseName+"/"+tableName);
            // Adding header into the table file
            tableFile.write(String.join("<#H#>",allColumnNames)+"<#EOD#>\n");
            tableFile.close();
        } catch (IOException e) {
            LOGGER.severe("[*error*] Unable to create table.");
            System.out.println("[*error*] Unable to create table.");
            return false;
        }
        return true;
    }

    /**
     * function for performing the insert data operation
     * @param username
     * @param databaseName
     * @param tableName
     * @param allValuesToInsert
     * @return
     */
    private boolean insertDataIntoTable(String username, String databaseName, String tableName, List<String> allValuesToInsert){
        // open the table file in append mode and insert the data
        try (BufferedWriter filehandlerObjWriter = new BufferedWriter(new FileWriter(basePath + "DataRepository" +"/"+ username+"/"+databaseName+"/"+tableName, true))){
            String result = "";
            for (int i = 0; i < allValuesToInsert.size(); i++) {
                result += String.join("<#D#>",allValuesToInsert.get(i).trim().replaceAll("'","").split(","));
                result += "<#EOD#>\n";
            }
            // Append data to table file
            filehandlerObjWriter.write(result);
        } catch (FileNotFoundException e) {
            LOGGER.severe("[**ERROR**] ERROR LOCATING TABLE! ERROR MESSAGE: " + e.getMessage());
            System.out.println("[**ERROR**] ERROR LOCATING TABLE! ERROR MESSAGE: " + e.getMessage());
            return false;
        } catch (IOException e) {
            LOGGER.severe("[**ERROR**] ERROR INSERTING RECORD INTO TABLE! ERROR MESSAGE: "+e.getMessage());
            System.out.println("[**ERROR**] ERROR INSERTING RECORD INTO TABLE! ERROR MESSAGE: "+e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * function for handling create table operation
     * @param username
     * @param databaseName
     * @param query
     * @return
     */
    @Override
    public boolean createTable(String username, String databaseName, String query) {
        List <String> allColumnNames = new ArrayList<String>();
        List <String> allColumnDataTypes = new ArrayList<String>();
        List <String> allColumnConstraints = new ArrayList<String>();
        List <String> PKColumns = new ArrayList<String>();
        List <String> FKColumns = new ArrayList<String>();

        // Get column definition from query
        Matcher tableMatcher = patternMatcher.getCreateTableStructure(query, patternMatcher.CREATE_TABLE_SYNTAX);
        String tableName = tableMatcher.group(1);
        String[] columnDefinition = tableMatcher.group(2).split(",");

        // Get all column names and its datatype
        for (int i = 0; i < columnDefinition.length; i++) {
            // Add pk column if exist
            if (columnDefinition[i].contains("primary key")) {
                Pattern pattern = Pattern.compile("\\((.*?)\\)");
                Matcher matcher = pattern.matcher(columnDefinition[i]);
                matcher.find();
                PKColumns.add(matcher.group(1));
            }
            // Add forign key column if exist
            else if (columnDefinition[i].contains("foreign key")) {
                Pattern pattern = Pattern.compile("\\((.*?)\\) references (.*?)\\((.*?)\\)");
                Matcher matcher = pattern.matcher(columnDefinition[i]);
                matcher.find();
                // colname-refTable.colname
                String colName = matcher.group(1) + "-" + matcher.group(2) + "." + matcher.group(3);
                FKColumns.add(colName);
            }
            else {
                String[] singleColumn = columnDefinition[i].strip().split(" ");

                allColumnNames.add(singleColumn[0].strip());
                allColumnDataTypes.add(singleColumn[1].strip());
                String result ="";
                // Add anything after col data type
                if (singleColumn.length > 2){
                    // join with space rest of the constrant after column data type
                    String intermediateResult = String.join(" ", Arrays.copyOfRange(singleColumn, 2, singleColumn.length));
                    allColumnConstraints.add(intermediateResult);
                }
                // if no constraint then adding null
                else {
                    allColumnConstraints.add(null);
                }
            }
        }
        System.out.println("FKColumns = " + FKColumns);
        System.out.println("PKColumns = " + PKColumns);
        // Save meta data in metadata repository
        boolean status = iMetaDataHandler.saveTableMetaData(username, databaseName, tableName, allColumnNames, allColumnDataTypes,allColumnConstraints,PKColumns,FKColumns);
        // if metadata created successfully then create table file
        if (status) {
            // Create a database folder for storing metadata for user's database
            tableName += ".table";
            // Create the table file
            this.createTableDefinition(username,databaseName,tableName,allColumnNames);
        }else {
            LOGGER.severe("[*ERROR*] Table already exists.");
            System.out.println("[*ERROR*] Table already exists.");
            return false;
        }
        return true;
    }

    /**
     * function for inserting data into the selected table from the user's query
     * @param username
     * @param databaseName
     * @param query
     * @return
     */
    @Override
    public boolean insertDataIntoTable(String username, String databaseName, String query) {
        // String manipulation to maintain data integrity and correctness, avoiding data loss
        String finalQuery = query.substring(0, query.toLowerCase().indexOf("values")).toLowerCase() + "values" + query.substring(query.toLowerCase().indexOf("values") + 6);

        Matcher queryMatcher = patternMatcher.getQueryStructure(finalQuery, patternMatcher.INSERT_SYNTAX);
        // get the table name in which data is to be inserted
        String tableName = queryMatcher.group(1);
        // Get the Column Names if any and its values
        String values,columnNames;
        if (queryMatcher.groupCount()==3) {
            columnNames = queryMatcher.group(2);
            values = queryMatcher.group(3);
        }
        else {
            values = queryMatcher.group(2);
        }
        List<String> allValuesToInsert = new ArrayList<>();
//        queryMatcher = patternMatcher.getQueryStructure(query,"\\([^()]*\\)");
        Pattern pattern = Pattern.compile("\\([^()]*\\)");
        queryMatcher = pattern.matcher(values);
        while (queryMatcher.find()) {
            String value = queryMatcher.group().replaceAll("[()]", "").trim();
            allValuesToInsert.add(value);
        }
        // return boolean insertion status to service layer
        return this.insertDataIntoTable(username,databaseName,tableName+".table",allValuesToInsert);
    }

    /**
     * function for performing select operation. It retrieves the data from a table based on the user's query.
     * @param username
     * @param databaseName
     * @param query
     * @return
     */
    @Override
    public String selectDataFromTable(String username, String databaseName, String query) {
        // where clause Column Name verification???
        Matcher queryMatcher = patternMatcher.getQueryStructure(query, patternMatcher.SELECT_SYNTAX);
        String columns = queryMatcher.group(1).strip();
        String tableName = queryMatcher.group(2);
        if (!this.isTable(username,databaseName,tableName)) {
            LOGGER.severe("[*ERROR*] Error Code: 1146:ER_NO_SUCH_TABLE, Table '"+databaseName+"."+tableName+"' doesn't exist");
            System.out.println("[*ERROR*] Error Code: 1146:ER_NO_SUCH_TABLE, Table '"+databaseName+"."+tableName+"' doesn't exist");
            return null;
        }
        else {
            List<String> allColumnNames = iMetaDataHandler.getAllColumnNames(username,databaseName,tableName);
//            System.out.println("allColumnNames = " + allColumnNames);
            String result = "";
//            System.out.println("columns = " + columns);
            result += "=====================\n";
            result += "  "+tableName;
            result += "\n=====================\n";
            String intermediateTableResultset = "";
            // get all columns
            if (columns.equals("*")) {
                result += String.join(printDelimeter,allColumnNames);
                result += "\n---------------------\n";
                try {
                    BufferedReader tableReader = new BufferedReader(new FileReader(basePath + "DataRepository" + "/" + username + "/" + databaseName + "/" + tableName + ".table"));
                    String record;
                    while((record = tableReader.readLine()) != null)
                    {
                        if (record.contains("<#D#>")) {
                            intermediateTableResultset += record.replaceAll("<#EOD#>", "").replaceAll("<#D#>", printDelimeter);
                            intermediateTableResultset += "\n";
                        }
                    }
                    tableReader.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            // Select only specific columns
            else {
                // separate out column names
                String[] columnsToSelect = columns.split(",");
                for (int i = 0; i < columnsToSelect.length; i++)
                    columnsToSelect[i] = columnsToSelect[i].trim();
                boolean[] columnSelectStatus = new boolean[allColumnNames.size()];
                for (int i = 0; i < columnsToSelect.length; i++) {
                    columnSelectStatus[allColumnNames.indexOf(columnsToSelect[i].strip())] = true;
                }
                result += String.join(printDelimeter,columnsToSelect);
                result += "\n---------------------\n";
                try {
                    BufferedReader tableReader = new BufferedReader(new FileReader(basePath + "DataRepository" + "/" + username + "/" + databaseName + "/" + tableName + ".table"));
                    String record;
                    while((record = tableReader.readLine()) != null)
                    {
                        if (record.contains("<#D#>")) {
                            List<String> intermediateResult = new ArrayList<String>();
                            String[] splitedRecord = record.replaceAll("<#EOD#>", "").split("<#D#>");
                            // Select only those record who column user selected
                            for (int i = 0; i < splitedRecord.length; i++) {
                                if ( columnSelectStatus[i] )
                                    intermediateResult.add(splitedRecord[i]);
                            }
                            intermediateTableResultset += String.join( printDelimeter,intermediateResult);
                            intermediateTableResultset += "\n";
                        }
                    }
                    tableReader.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Process Where clause in query if any
            String columnWhereCondition = queryMatcher.group(3);
            String operatorWhereCondition = queryMatcher.group(4);
            String valueWhereCondition = queryMatcher.group(5);

            if (columnWhereCondition != null || operatorWhereCondition != null || valueWhereCondition != null) {
                columnWhereCondition = columnWhereCondition.strip();
                operatorWhereCondition = operatorWhereCondition.strip();
                valueWhereCondition = valueWhereCondition.strip();
                valueWhereCondition = valueWhereCondition.replaceAll("'","");
                valueWhereCondition = valueWhereCondition.replaceAll("\"","");
                intermediateTableResultset = this.processWhereCondition(intermediateTableResultset, allColumnNames, columns, columnWhereCondition, operatorWhereCondition, valueWhereCondition, printDelimeter);
            }
            // Append select result set to final result
            result += intermediateTableResultset;
            result += "---------------------\n";
            return result;
        }
    }

    /**
     * function for performing delete operation. It deletes the records based on the user's query.
     * @param username
     * @param databaseName
     * @param query
     * @return
     */
    @Override
    public String deleteDataFromTable(String username, String databaseName, String query) {
        Matcher queryMatcher = patternMatcher.getQueryStructure(query, patternMatcher.DELETE_SYNTAX);
        String tableName = queryMatcher.group(1);
        String columnWhereCondition = queryMatcher.group(2);
        String operatorWhereCondition = queryMatcher.group(3);
        String valueWhereCondition = queryMatcher.group(4);
        if (!this.isTable(username,databaseName,tableName)) {
            LOGGER.severe("[*ERROR*] Error Code: 1146:ER_NO_SUCH_TABLE, Table '"+databaseName+"."+tableName+"' doesn't exist");
            System.out.println("[*ERROR*] Error Code: 1146:ER_NO_SUCH_TABLE, Table '"+databaseName+"."+tableName+"' doesn't exist");
            return null;
        }
        else {
            int record = 0;
            if (columnWhereCondition != null || operatorWhereCondition != null || valueWhereCondition != null) {

                columnWhereCondition = columnWhereCondition.strip();
                operatorWhereCondition = operatorWhereCondition.strip();
                valueWhereCondition = valueWhereCondition.strip();
                valueWhereCondition = valueWhereCondition.replaceAll("'","");
                valueWhereCondition = valueWhereCondition.replaceAll("\"","");
                String intermediateTableResultset = "";

                // get all column names from metadata file
                List<String> allColumnNames = iMetaDataHandler.getAllColumnNames(username,databaseName,tableName);
                int colIndex = allColumnNames.indexOf(columnWhereCondition);

                // read the entire raw table file
                String initialTableResultset = this.getRawAllTableData(username, databaseName, tableName);

                // Check operator condition
                // 1. Check if 'equals'
                if (operatorWhereCondition.equals("=")) {
                    for (String row : initialTableResultset.split("\n")) {
                        row = row.strip();
                        // Append the header in the file
                        if (row.contains("<#H#>"))
                            intermediateTableResultset += (row + "\n");
                        // Check if the value at column is equal to the value given by user
//                        System.out.println("--------------------");
//                        System.out.println("record : "+row);
//                        System.out.println("col value : " + row.replaceAll("<#EOD#>", "").split("<#D#>")[colIndex].strip());
//                        System.out.println("boolean value : " + !row.replaceAll("<#EOD#>", "").split("<#D#>")[colIndex].strip().equals(valueWhereCondition));
                        else if (!row.replaceAll("<#EOD#>", "").split("<#D#>")[colIndex].strip().equals(valueWhereCondition)) {
                            intermediateTableResultset += (row + "\n");
                        }
                        else
                            record += 1;
                    }
                    // Write Data into table temp file
                    boolean status = writeDataIntoTempFile(username, databaseName, tableName, intermediateTableResultset);
                    if (!status)
                        return null;
                    // Perform table / file level delete operation
                    status = this.performDeleteOperationOnTableFile(username, databaseName, tableName);
                    if (status)
                        return String.valueOf(record);
                    else
                        return null;
                }
                else {
                    LOGGER.severe("[*ERROR*] Operator "+operatorWhereCondition+" not supported yet. Currently only equals operator is supported.\nDELETE OPERATION ABORTED!\n");
                    System.out.println("[*ERROR*] Operator "+operatorWhereCondition+" not supported yet. Currently only equals operator is supported.\nDELETE OPERATION ABORTED!\n");
                    return null;
                }
            }
            // Delete all rows from table file
            else {
                String tableData = this.getRawAllTableData(username , databaseName, tableName);
                for (String row : tableData.split("\n")) {
                    if (row.contains("<#H#>"))
                        // Write header into temp file
                        writeDataIntoTempFile(username,databaseName,tableName,row+"\n");
                    else
                        record += 1;
                }
                // Perform table / file level delete operation
                boolean status = this.performDeleteOperationOnTableFile(username, databaseName, tableName);
                if (status)
                    return String.valueOf(record);
                else
                    return null;
            }
        }
    }

    /**
     * function to perform the file level delete operation by deleting the old table file and then renaming the temporary table file
     * @param username
     * @param databaseName
     * @param tableName
     * @return
     */
    private boolean performDeleteOperationOnTableFile(String username, String databaseName, String tableName) {
        // Delete existing table
        File delTable = new File(basePath + "DataRepository" + "/" + username + "/" + databaseName + "/" + tableName + ".table");
        if (!delTable.delete()) {
            LOGGER.severe("[*ERROR*] Delete operation failed. Error while deleting table file.");
            System.out.println("[*ERROR*] Delete operation failed. Error while deleting table file.");
            return false;
        }

        // Rename the temp table file as the main table file
        // Reference : https://www.inf.unibz.it/~calvanese/teaching/05-06-ip/lecture-notes/uni09/node12.html#:~:text=To%20rename%20a%20file%2C%20we,to%20give%20to%20the%20file.&text=The%20file%20oldname.,txt.
        File tempTableFile = new File(basePath + "DataRepository" + "/" + username + "/" + databaseName + "/" + tableName + ".table_temp");
        File mainTableFile = new File(basePath + "DataRepository" + "/" + username + "/" + databaseName + "/" + tableName + ".table");
//                    // rename file
        if (tempTableFile.renameTo(mainTableFile)) {
            // success
            return true;
        } else {
            LOGGER.severe("[*ERROR*] Delete operation failed. Error while deleting table file.");
            System.out.println("[*ERROR*] Delete operation failed. Error while deleting table file.");
            return false;
        }
    }

    /**
     * function for writing data into a temporary table file
     * @param username
     * @param databaseName
     * @param tableName
     * @param data
     * @return
     */
    private boolean writeDataIntoTempFile(String username, String databaseName, String tableName, String data) {
        BufferedWriter tempTable = null;
        try {
            tempTable = new BufferedWriter(new FileWriter(basePath + "DataRepository" + "/" + username + "/" + databaseName + "/" + tableName + ".table_temp"));
            tempTable.write(data);
            tempTable.close();
        } catch (FileNotFoundException e) {
            LOGGER.severe("[*ERROR*] Delete operation failed. Table file not found.");
            System.out.println("[*ERROR*] Delete operation failed. Table file not found.");
            return false;
        } catch (IOException e) {
            LOGGER.severe("[*ERROR*] Delete operation failed. Error while creating temp Table file.");
            System.out.println("[*ERROR*] Delete operation failed. Error while creating temp Table file.");
            return false;
        }
        return true;
    }

    /**
     * fucntion to retrieve the raw table data from a table file
     * @param username
     * @param databaseName
     * @param tableName
     * @return
     */
    private String getRawAllTableData(String username, String databaseName, String tableName) {
        try {
            String result = Files.readString(Path.of(basePath + "DataRepository" + "/" + username + "/" + databaseName + "/" + tableName + ".table"));;
            return result;
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * function to perform where clause operation on the select operation.
     * @param result
     * @param allColumnNames
     * @param columns
     * @param columnWhereCondition
     * @param operatorWhereCondition
     * @param valueWhereCondition
     * @param printDelimeter
     * @return
     */
    private String processWhereCondition(String result, List<String> allColumnNames, String columns, String columnWhereCondition, String operatorWhereCondition, String valueWhereCondition, String printDelimeter) {
//        System.out.println("columnWhereCondition = " + columnWhereCondition);
//        System.out.println("operatorWhereCondition = " + operatorWhereCondition);
//        System.out.println("valueWhereCondition = " + valueWhereCondition);
        // check if column exists else wrong column in where clause
        String intermediateTableResultset;
        if (allColumnNames.contains(columnWhereCondition)) {
            int colIndex = allColumnNames.indexOf(columnWhereCondition);
            intermediateTableResultset = "";
            if (columns.equals("*")) {
                // Check operator condition
                // 1. Check if 'equals'
                if (operatorWhereCondition.equals("=")) {
                    String [] rows = result.split("\n");
                    for (int i = 0; i < rows.length; i++) {
//                        System.out.println(rows[i].split(printDelimeter)[colIndex].strip()+"------"+valueWhereCondition);
                        if (rows[i].split(printDelimeter)[colIndex].strip().equals(valueWhereCondition)) {
                            intermediateTableResultset += (rows[i]+"\n");
                        }
                    }
                }
                else {
                    LOGGER.severe("[*ERROR*] Operator "+operatorWhereCondition+" not supported yet. Currently only equals operator is supported.\nHere is the result without any condition appleid in the 'Where' clause: \n");
                    System.out.println("[*ERROR*] Operator "+operatorWhereCondition+" not supported yet. Currently only equals operator is supported.\nHere is the result without any condition appleid in the 'Where' clause: \n");
                    return result;
                }
            }
        }
        else {
            LOGGER.severe("Error Code: 1054. Unknown column '" + columnWhereCondition + "' in 'where clause'\n");
            System.out.println("Error Code: 1054. Unknown column '" + columnWhereCondition + "' in 'where clause'\n");
            return null;
            }
        return intermediateTableResultset;
    }

    /**
     * performs the update query operation on the user's query
     * @param username
     * @param databaseName
     * @param query
     * @return
     */
    @Override
    public String updateTableData(String username, String databaseName, String query) {
//        String finalQuery = query.replaceAll("(?i)(?<=\\b)(update|set|where)(?=\\b)", String::toLowerCase);
        String finalQuery = query;
        Matcher queryMatcher = patternMatcher.getQueryStructure(finalQuery, patternMatcher.UPDATE_SYNTAX);
        // get the table name in which data is to be inserted
        String tableName = queryMatcher.group(1);
        String setCondition = queryMatcher.group(2);
        String columnNameWhereCondition = queryMatcher.group(3);
        String valueWhereCondition = queryMatcher.group(4);
        valueWhereCondition = valueWhereCondition.replaceAll("'","").replaceAll("\"","");
        // Get the Column Names if any and its values
        String values;
        List<String> allColumnNames = iMetaDataHandler.getAllColumnNames(username,databaseName,tableName);

        String columnNameSetCondition = setCondition.split("=")[0].strip();
        String valueSetCondition = setCondition.split("=")[1].strip();
        valueSetCondition = valueSetCondition.replaceAll("'","").replaceAll("\"","");

        String intermediateTableResultset = "";
        // Get Table data
        String initialTableData = this.getRawAllTableData(username, databaseName, tableName);
        // Get column index of the column name in Set clause
        int colIndexSetCondition = allColumnNames.indexOf(columnNameSetCondition);
        // Get column index of the column name in where clause
        int colIndexWhereCondition = allColumnNames.indexOf(columnNameWhereCondition);
        int record=0;
        boolean whereFlag = false;
        // true if where clause present else false
        if (columnNameWhereCondition!=null || valueWhereCondition!=null) {
            whereFlag = true;
        }
        // Iterate over all the row in the table
        for (String row : initialTableData.split("\n")) {
            row = row.strip();
            // if row contains data
            if (row.contains("<#D#>")) {
                String[] splitRowRecord = row.replaceAll("<#EOD#>", "").split("<#D#>");
                // if where clause not present in query then update row column value based on that column
                if (!whereFlag) {
                    splitRowRecord[colIndexSetCondition] = valueSetCondition;
                    record += 1;
                }
                else {
//                    System.out.println("---------------------------------------------------------");
//                    System.out.println("1. "+splitRowRecord[colIndexWhereCondition].strip());
//                    System.out.println("2. "+valueWhereCondition);
                    // If the where clause condition matches then make changes to row
                    if (colIndexWhereCondition != -1) {
                        if (splitRowRecord[colIndexWhereCondition].strip().equals(valueWhereCondition)) {
                            splitRowRecord[colIndexSetCondition] = valueSetCondition;
                            record += 1;
                        }
                    }
                    else {
                        System.out.println("[*ERROR*] Column ("+columnNameWhereCondition+") specified in where clause not found in table: "+tableName);
                        return null; 
                    }
                }
                intermediateTableResultset += (String.join("<#D#>",splitRowRecord)+"<#EOD#>"+"\n");
            }
            // Append the header in the file
            else if (row.contains("<#H#>")) {
                intermediateTableResultset += (row + "\n");
            }
        }
        // Write Data into table temp file
        boolean status = writeDataIntoTempFile(username, databaseName, tableName, intermediateTableResultset);
        if (!status)
            return null;
        // Perform table / file level delete operation to update the new table file
        status = this.performDeleteOperationOnTableFile(username, databaseName, tableName);
        if (status)
            return String.valueOf(record);
        else
            return null;
    }

    /**
     * function to get erd by reading metadata of all files in user's database
     * @param username
     * @param databaseName
     * @return
     */
    @Override
    public String getErd(String username, String databaseName) {
        String result="";
        String[] allTableNames = this.showAllTables(username,databaseName).split("\n");
        for (int i = 0; i < allTableNames.length; i++) {
            String fileName = allTableNames[i].replaceAll(".table","");
            String[] rawTableMetadata = iMetaDataHandler.getRawTableMetadata(username, databaseName, fileName).split("\n");
            for (String row: rawTableMetadata) {
                if (row.split("<#D#>")[0].equals("FK")) {
                    result += fileName +" [N] --------------- [1] "+row.split("<#D#>")[1].split("-")[1].split("\\.")[0]+" (many-to-one realtionship)\n";
                }
            }
        }
        return result;
    }

    /**
     * function to get all tables in a database
     * @param username
     * @param databaseName
     * @return
     */
    public String showAllTables(String username,String databaseName) {
        File tableDataRepositoryDir = new File(basePath+"DataRepository/"+username+"/"+databaseName);
        String allTableNames = String.join("\n",tableDataRepositoryDir.list());
        return allTableNames;
    }
}