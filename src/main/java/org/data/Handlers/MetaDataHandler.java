package org.data.Handlers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data layer class for handling all metadata operations
 */
public class MetaDataHandler implements IMetaDataHandler {
    private static String basePath = "src/main/resources/";
    private static String metaDataRepositoryName = "metaDataRepository";
    private static String extension = ".metadata";
    private static final Logger LOGGER = Logger.getLogger(MetaDataHandler.class.getName());

    /**
     * Constructor verifying if metaData Repository and its contents are correctly present, else create them.
     */
    public MetaDataHandler() {
        if (!Files.exists(Path.of(basePath+metaDataRepositoryName))) {
            new File(basePath + metaDataRepositoryName).mkdirs();
            LOGGER.info("[*success*] MetaData repository created successfully!");
            System.out.println("[*success*] MetaData repository created successfully!");
        }
    }

    /**
     * function to verify if the authentication metadata file and table file are present. if not found then create them for integrity and is necessary for teh authentication functionality
     * @throws IOException
     */
    public  void verifyAuthenticationMetaData() throws IOException {
        // If metadata of loginTable doesn't exist then create it.
        if (!Files.exists(Path.of(basePath+"AuthenticationRepository"+"/"+"loginInfo"+extension))){
            FileWriter loginMetaData = new FileWriter(basePath+"AuthenticationRepository"+"/"+"loginInfo"+extension);
            loginMetaData.write("tableName<#H#>loginInfo<#EOD#>\n" +
                                    "username<#D#>text<#EOD#>\n" +
                                    "password<#D#>text<#EOD#>\n" +
                                    "question<#D#>text<#EOD#>\n" +
                                    "answer<#D#>text<#EOD#>\n");
            loginMetaData.close();
            LOGGER.info("[*success*] Authentication metadata created successfully!");
            System.out.println("[*success*] Authentication metadata created successfully!");
        }
        // If loginTable doesn't exist then create it.
        if (!Files.exists(Path.of(basePath+"AuthenticationRepository"+"/"+"loginInfo"+".table"))){
            FileWriter loginMetaData = new FileWriter(basePath+"AuthenticationRepository"+"/"+"loginInfo"+".table");
            loginMetaData.write("username<#H#>password<#H#>question<#H#>answer<#EOD#>");
            loginMetaData.close();
            LOGGER.info("[*success*] Authentication table created successfully!");
            System.out.println("[*success*] Authentication table created successfully!");
        }
    }

    /**
     * function for saving the metadata of a newly created table. This hapens when a new table is created,
     *
     * @param username
     * @param databaseName
     * @param tableName
     * @param allColumnNames
     * @param allColumnDataTypes
     * @param allColumnConstraints
     * @param PKColumns
     * @param FKColumns
     * @return
     */
    public  boolean saveTableMetaData (String username, String databaseName, String tableName, List<String> allColumnNames, List<String> allColumnDataTypes, List<String> allColumnConstraints, List<String> PKColumns, List<String> FKColumns) {
        tableName += extension;
        System.out.println("tableName = " + tableName);
        // Check if table already exists
        if (Files.exists(Path.of(basePath+metaDataRepositoryName+"/"+username+"/"+databaseName+"/"+tableName))) {
            return false;
        }
        // Create user's main folder for storing metadata
        if (!Files.exists(Path.of(basePath+metaDataRepositoryName+"/"+username))) {
            boolean status = new File(basePath+metaDataRepositoryName+"/"+username).mkdirs();
        }
        // Create a database folder for storing metadata for user's database
        if (!Files.exists(Path.of(basePath+metaDataRepositoryName+"/"+username+"/"+databaseName))) {
            boolean status = new File(basePath+metaDataRepositoryName+"/"+username+"/"+databaseName).mkdirs();
        }
        // save the metadata file for the new table
        FileWriter metadataTable = null;
        try {
            metadataTable = new FileWriter(basePath+metaDataRepositoryName+"/"+username+"/"+databaseName+"/"+tableName);
            String result="tableName<#H#>"+tableName+"<#EOD#>\n";
            for (int i = 0; i < allColumnNames.size(); i++) {
                result += allColumnNames.get(i)+"<#D#>"+allColumnDataTypes.get(i)+"<#D#>"+allColumnConstraints.get(i)+"<#EOD#>\n";
            }
            if (PKColumns.size()>0) {
                result += "PK<#D#>"+PKColumns.get(0)+"\n";
            }
            if (FKColumns.size()>0) {
                for (int i = 0; i < FKColumns.size(); i++) {
                    result += "FK<#D#>"+FKColumns.get(0)+"\n";
                }
            }
            metadataTable.write(result);
            metadataTable.close();
            System.out.println("[*SUCCESS*] metadata success.");

        } catch (IOException e) {
            LOGGER.info("[*error*] Unable to create table metadata.");
            System.out.println("[*error*] Unable to create table metadata.");
            return false;
        }
        return true;
    }

    /**
     * function to retreive all the column names from a table file.
     * @param username
     * @param databaseName
     * @param tableName
     * @return
     */
    public  List<String> getAllColumnNames(String username,String databaseName,String tableName) {
        BufferedReader metadataTable = null;
        try {
            metadataTable = new BufferedReader(new FileReader(basePath+metaDataRepositoryName+"/"+username+"/"+databaseName+"/"+tableName+extension));
            String metadataRow;
            List<String> result = new ArrayList<String>();
            while((metadataRow = metadataTable.readLine()) != null)
            {
                // Traverse all record rows only
                if (!metadataRow.contains(".metadata")){
                    result.add(metadataRow.replaceAll("<#EOD#>","").split("<#D#>")[0]);
                }
            }
            metadataTable.close();
            return result;
        }
        catch (FileNotFoundException e) {
            LOGGER.severe("[*ERROR*] Table ("+tableName+") not present in database ("+databaseName+"). Message: "+e.getMessage());
            System.out.println("[*ERROR*] Table ("+tableName+") not present in database ("+databaseName+"). Message: "+e.getMessage());
            return null;
        }
        catch (IOException e) {
            LOGGER.severe("[*ERROR*] Error while reading table ("+tableName+") in database ("+databaseName+"). Message: "+e.getMessage());
            System.out.println("[*ERROR*] Error while reading table ("+tableName+") in database ("+databaseName+"). Message: "+e.getMessage());
            return null;
        }
    }

    /**
     * function to get raw metadata file
     * @param username
     * @param databaseName
     * @param tableName
     * @return
     */
    public  String getRawTableMetadata(String username, String databaseName, String tableName) {
        try {
            String result = Files.readString(Path.of(basePath + "metaDataRepository" + "/" + username + "/" + databaseName + "/" + tableName + extension));;
            return result;
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
