package org.data.Controller;

import org.data.Handlers.IQueryHandler;
import org.data.Handlers.QueryHandler;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Controller class for handling query operations
 */
public class QueryController {
    String username;
    String databaseName;
    IQueryHandler queryHandler;
    private static final Logger LOGGER = Logger.getLogger(QueryController.class.getName());
    Scanner input = new Scanner(System.in);
    public QueryController(String username) {
        this.username = username;
        this.queryHandler = new QueryHandler();
    }

    /**
     * Main query prompt function for accepting user's query input and handling different operation by calling service layer
     */
    public void queryPrompt() {
        System.out.print("\n\n["+username+"] > ");
        String query = input.nextLine();
        query = query.strip();
//        System.out.println("query = " + query);
        String queryType = queryHandler.getQueryType(query.toLowerCase());
//        System.out.print("queryType = " + queryType);
        if (queryType != null) {
            boolean wellFormedStatus = queryHandler.verifyWellFormednessQuery(query.toLowerCase(),queryType);
//            System.out.print(" | wellFormedStatus = " + wellFormedStatus);
            if (wellFormedStatus) {
                switch (queryType) {
                    case "create database" -> {
                        query = query.toLowerCase();
                        queryHandler.createDatabase(username, query);
                    }
                    case "create table" -> {
                        query = query.toLowerCase();
                        if (this.databaseName != null) {
                            boolean responseStatus = queryHandler.createTable(username, this.databaseName, query);
                            if (responseStatus) {
                                LOGGER.info("[*SUCCESS*] Table created successfully!");
                                System.out.println("[*SUCCESS*] Table created successfully!");
                            }
                        } else {
                            LOGGER.severe("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                            System.out.println("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                        }
                    }
                    case "use" -> {
                        query = query.toLowerCase();
                        this.databaseName = queryHandler.useDatabase(username, query);
                        if (this.databaseName == null) {
                            LOGGER.severe("[*ERROR*] Database not found!");
                            System.out.println("[*ERROR*] Database not found!");
                        } else {
                            LOGGER.info("[*SUCCESS*] Using database : " + this.databaseName);
                            System.out.println("[*SUCCESS*] Using database : " + this.databaseName);
                        }
                    }
                    case "select" -> {
                        if (this.databaseName != null) {
                            System.out.println(queryHandler.selectDataFromTable(username, this.databaseName, query));
                        } else {
                            LOGGER.severe("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                            System.out.println("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                        }
                    }
                    case "insert" -> {
                        if (this.databaseName != null) {
                            boolean responseStatus = queryHandler.insertDataIntoTable(username, this.databaseName, query);
                            if (responseStatus) {
                                LOGGER.info("[*SUCCESS*] Data inserted successfully!");
                                System.out.println("[*SUCCESS*] Data inserted successfully!");
                            }
                        } else {
                            LOGGER.severe("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                            System.out.println("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                        }
                    }
                    case "update" -> {
                        if (this.databaseName != null) {
                            queryHandler.updateTableData(username, this.databaseName, query);
                        } else {
                            LOGGER.severe("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                            System.out.println("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                        }
                    }
                    case "delete" -> {
                        if (this.databaseName != null) {
                            queryHandler.deleteDataFromTable(username, this.databaseName, query);
                        } else {
                            LOGGER.severe("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                            System.out.println("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                        }
                    }
                    case "show" -> {
                        System.out.println("---------------------");
                        System.out.println("List of All Databases");
                        System.out.println("---------------------");
                        System.out.println(queryHandler.showAllDatabases(username));
                    }
                    case "create erd" -> {
                        if (this.databaseName != null) {
                            System.out.println("---------------------");
                            System.out.println("         ERD         ");
                            System.out.println("---------------------");
                            System.out.println(queryHandler.getErd(username,databaseName));
                        } else {
                            LOGGER.severe("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                            System.out.println("[*ERROR*] Error Code: 1046:ER_NO_DB_ERROR. No database selected. Select the default DB to be used by using 'USE' keyword.\n");
                        }
                    }
                }
            } else {
//                LOGGER.severe("\n[*ERROR*] SYNTAX ERROR IN QUERY. Please Enter a Well-formed Query. Query operation attempted = "+queryType);
                System.out.println("[*ERROR*] SYNTAX ERROR IN QUERY. Please Enter a Well-formed Query. Query operation attempted = "+queryType);
            }
        }
        else {
//            LOGGER.severe("\n[*ERROR*] INVALID QUERY. Please Enter a valid Query.");
            System.out.println("\n[*ERROR*] INVALID QUERY. Please Enter a valid Query.");
        }
    }
}
