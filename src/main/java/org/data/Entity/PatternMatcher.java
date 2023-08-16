package org.data.Entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for handling all the regular expression operations and returning the matched group to the caller functions
 */
public class PatternMatcher {

    public static String USE_SYNTAX = "use\s+([A-Za-z]+);?" ;
    public static String CREATE_DATABASE_SYNTAX = "create\s+database\s+([A-Za-z]+);?";
//    public static String CREATE_TABLE_SYNTAX = "create\\s+table\\s+([A-Za-z\\_]+)\\s*\\(\\s*((?:[A-Za-z0-9]+\\s+[A-Za-z\\(0-9\\)]+,?\\s*)+)\\);?";
    public String CREATE_TABLE_SYNTAX = "create\\s+table\\s+([A-Za-z\\\\_]+)\\s*\\(\\s*((?:[A-Za-z0-9]+\\s+[A-Za-z\\\\(0-9\\\\)]+\\s*,?\\s*)+)\\);?";
    public String SELECT_SYNTAX = "select\\s+((?:\\*)|(?:[A-Za-z0-9]+\\s*(?:,\\s*[A-Za-z0-9]+\\s*)*))\\s+from\\s+([A-Za-z]+)(?:\\s+where\\s+([A-Za-z0-9]+)\\s*([=<>]+)\\s*(.*))?;";
    public String INSERT_SYNTAX = "insert\\s+into\\s+([A-Za-z]+)(?:\\s*\\(([^)]+)\\))?\\s*values\\s*(.*);";
    public String UPDATE_SYNTAX = "update\\s+([A-Za-z]+)\\s+set\\s+((?:[A-Za-z]+\\s*=\\s*(?:'[^']*'|\\d+(?:\\.\\d+)?)\\s*,?\\s*)+)(?:\\s+where\\s+([A-Za-z0-9]+)\\s*=\\s*('(?:\\\\'|[^'])+'|\\d+(?:\\.\\d+)?)\\s*)?;";
    public String DELETE_SYNTAX = "delete\\s+from\\s+([A-Za-z]+)(?:\\s+where\\s+([A-Za-z0-9]+)\\s*([=<>]+)\\s*('(?:\\\\'|[^'])+'|\\d+(?:\\.\\d+)?))?\\s*;";

    /**
     * function for returning boolean value if username exists in teh login table
     * @param row
     * @param username
     * @return
     */
    public static boolean findUsername(String row, String username) {
        Pattern pattern = Pattern.compile(username);
        Matcher matcher = pattern.matcher(row);
        boolean matchFound = matcher.find();
        return matchFound;
    }

    /**
     * function for returning boolean value if the query regex matches the query type in teh user's query input
     * @param query
     * @param queryType
     * @return boolean true if query pattern found else false
     */
    public static boolean findQueryTypeInQuery(String query, String queryType) {
        Pattern pattern = Pattern.compile(queryType+"\s+");
        Matcher matcher = pattern.matcher(query);
        return matcher.find();
    }

    /**
     * function to return the type of query by calling the helper function findQueryTypeInQuery
     * @param query
     * @return
     */
    public static String getQueryType(String query) {
        // create database
        String queryType = "create database";
        if (findQueryTypeInQuery(query,"create\\s+database"))
            return queryType;
        // create erd
        queryType = "create erd";
        if (findQueryTypeInQuery(query+" ","create\s+erd"))
            return queryType;

        // use or select a database
        queryType = "create table";
        if (findQueryTypeInQuery(query,"create\\s+table"))
            return queryType;

        // use or select a database
        queryType = "use";
        if (findQueryTypeInQuery(query,queryType))
            return queryType;

        // show databases;
        queryType = "show database";
        if (findQueryTypeInQuery(query,queryType))
            return queryType;

        // select a table
        queryType = "select";
        if (findQueryTypeInQuery(query,queryType))
            return queryType;

        // insert into table
        queryType = "insert";
        if (findQueryTypeInQuery(query,"insert\s+into"))
            return queryType;

        // update data in table
        queryType = "update";
        if (findQueryTypeInQuery(query,queryType))
            return queryType;

        // delete data in table
        queryType = "delete";
        if (findQueryTypeInQuery(query,queryType))
            return queryType;

        // Invalid query
        return null;
    }

    /**
     * helper function which returns true or false if the query is well formed against the query regualr expression
     * @param query
     * @param condition
     * @return true if the user's query syntax is correct else returns false
     */
    public static boolean checkWellformInQuery(String query, String condition) {
        Pattern pattern = Pattern.compile(condition);
        Matcher matcher = pattern.matcher(query);
        return matcher.find();
    }

    /**
     * Query parser function: return the boolean result of query syntax is coreect or wrong by calling the helper function checkWellformInQuery
     * @param query
     * @param queryType
     * @return
     */
    public boolean verifyWellFormednessQuery(String query, String queryType) {
        if(queryType == "create database") {
            return checkWellformInQuery(query,CREATE_DATABASE_SYNTAX);
        } else if (queryType == "create table") {
            return checkWellformInQuery(query,CREATE_TABLE_SYNTAX);
        } else if (queryType == "use") {
            return checkWellformInQuery(query,USE_SYNTAX);
        } else if (queryType == "show") {
            return checkWellformInQuery(query,"show\s+databases\s*;?");
        } else if (queryType == "select") {
            return checkWellformInQuery(query,SELECT_SYNTAX);
        } else if (queryType == "insert") {
            return checkWellformInQuery(query,INSERT_SYNTAX);
        } else if (queryType == "update") {
            return checkWellformInQuery(query,UPDATE_SYNTAX);
        } else if (queryType == "delete") {
            return checkWellformInQuery(query,DELETE_SYNTAX);
        } else if (queryType == "create erd") {
            return checkWellformInQuery(query,"create\s+erd\s*;?");
        } else {
            return false;
        }
    }

    /**
     * function to match the synatx to retrieve the database name from the user's query
     * @param query
     * @param SYNTAX
     * @return
     */
    public static String getDatabaseName(String query,String SYNTAX) {
        Pattern pattern = Pattern.compile(SYNTAX);
        Matcher matcher = pattern.matcher(query);
        if (matcher.find())
            return matcher.group(1);
        else
            return null;
    }

    /**
     * function to retrieve the groups form teh create table query using teh regular expression
     * @param query
     * @param condition
     * @return
     */
    public Matcher getCreateTableStructure(String query, String condition) {
        Pattern pattern = Pattern.compile(condition);
        Matcher matcher = pattern.matcher(query);
        matcher.find();
        return matcher;
    }

    /**
     * function to retreive the query structure from the query matching the condition
     * @param query
     * @param condition
     * @return
     */
    public Matcher getQueryStructure(String query, String condition) {
        Pattern pattern = Pattern.compile(condition);
        Matcher matcher = pattern.matcher(query);
        matcher.find();
        return matcher;
    }
}
