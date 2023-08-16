# DBMS-Java

This repository contains the implementation of a light-weight DBMS in Java.


## Overview: A light-weight DBMS

I have built a prototype light-weight DBMS using Java, using the inbuilt modules. The file format for persistent storage is unique and data fault tolerant. This DBMS performs user login, and signup with 2 factor authentication and security with hashing algorithms. It also provides user to create multiple databases and create multiple tables inside of selected database. Table level operations are also supported such as: creating table (DDL), DML- select, insert, update, delete - data from table. Where clause is supported for select, update, and delete operations.
Moreover, the DBMS provides extensive error handling capabilities with meaningful error messages displayed to user just like in MySQL DBMS. This project employs the S.O.L.I.D. and design principle for better code readability, maintainability, and extensibility. 

*Java IDE used*: IntelliJ IDEA 2023.1.2 
*JDK*: JDK 7.0.7 Stable latest long-term support Release [2]
*Java Documentation*: Using Java Docs
*Application Type*: Console based.
Chosen Algorithm for this light-weight DBMS: *MD5*

### Persistent Storage structure design:

This light-weight DBMS stores data into ‘.table’ and ‘.metadata’ files. For instance, the ‘.table’ file contains header information which are the column names. Moreover, it also contains the record data in rows, which contain the actual data of the table. The delimiters are different for both the headers and the data record. 
Header Delimiter: *<#H#>*
Row Delimiter:   *<#D#>*
Additionally, at the end of each line I include another string separator which is an *E*nd *O*f *D*ata record delimiter: *<#EOD#>*
The same is with the metadata file, in which the file structure is such that the first line contains the table name information, followed by each column name and data type information. Table name is delimited by the header delimiter and the columns by the row delimiter. 


### Functionalities of the DBMS: 

#### Authentication Operations:

_Execution flow for authentication functionality_: 

Upon each execution of the DBMS program, it first checks if the AuthenticationRepository, loginInfo.metadata and loginInfo.table exists. The reason for checking this is to verify the integrity, and also if this DBMS is installed in a fresh system, then these artefacts need to be created in order for the DBMS to function. 

*Package Name*: Authentication
*Classes*:

- _AuthHandler_: This is the service layer authentication class. It handles the login, signup functionality. 

- _AuthController_: This is the controller layer authentication class. It calls the service layer suthentication class to get the login and signup status. Its job is also to get the user input.

- _HashAlgorithm_: This class has the business logic for implementing the hashing algorithm.

#### Query Operations supported:

The DBMS offers multiple operations. Here is the description of the operations offered and the extent/ scope of which the functionality is provided:

1. Show databases: 
2. Use database <databasename>:
3. Create database:
4. Create table:
5. Select operation:
6. Insert operation:
7. Update operation:
8. Delete operation:
9. Create ERD Operation:



` Author : Zainuddin Saiyed `

-----
-----
-----
