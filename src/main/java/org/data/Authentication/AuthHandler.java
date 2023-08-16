package org.data.Authentication;

import org.data.DataAccessManager.FileStorageAccessManager;
import org.data.DataAccessManager.IFileStorageAccessManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Service Layer Class for handling login and signup operations.
 */
public class AuthHandler implements IAuthHandler{
    private String basePath = "src/main/resources/";
    private String repositoryName = "AuthenticationRepository";
    private String authTableName = "loginInfo.table";
    private static final Logger LOGGER = Logger.getLogger(AuthHandler.class.getName());

    IFileStorageAccessManager ifileStorageAccessManager;
    HashAlgorithm hashAlgorithm;

    /**
     * Constructor verifying if Authentication Repository and its contents are correctly present, else creating them.
     */
    public AuthHandler() {
        // instantiate hash algorithm handler
        hashAlgorithm = new HashAlgorithm();
        // dependency injection
        ifileStorageAccessManager = new FileStorageAccessManager();
        boolean status = false;
        // if authentication repository folder doesn't exist then create it
        if (!Files.exists(Path.of(basePath+repositoryName))){
            status = new File(basePath+repositoryName).mkdirs();
            LOGGER.info("[*success*] Authentication repository created successfully!");
            System.out.println("[*success*] Authentication repository created successfully!");

            FileWriter loginTable = null;
            try {
                loginTable = new FileWriter(basePath+repositoryName+"/"+authTableName);
                loginTable.write("username<#H#>password<#EOD#>\n");
                loginTable.write("user1<#D#>a722c63db8ec8625af6cf71cb8c2d939<#EOD#>\n");
                loginTable.close();
            } catch (IOException e) {
                LOGGER.severe("[*error*] Unable to create Autentication table.");
                System.out.println("[*error*] Unable to create Autentication table.");
            }
            if (status) {
                LOGGER.info("[*success*] Authentication table created successfully!");
                System.out.println("[*success*] Authentication table created successfully!");
            }
        }
        // if authentication repository folder exists but the table doesn't then create it
        else if (!Files.exists(Path.of(basePath + repositoryName + "/" + authTableName))){
            File loginTable = new File(basePath+repositoryName+"/"+authTableName);
            try {
                status = loginTable.createNewFile();
            } catch (IOException e) {
                LOGGER.severe("[*error*] Unable to create Autentication table.");
                System.out.println("[*error*] Unable to create Autentication table.");
            }
            if (status) {
                LOGGER.info("[*success*] Authentication table created successfully!");
                System.out.println("[*success*] Authentication table created successfully!");
            }
        }
    }

    /**
     * Service Layer function for generating user's entered password's hash value and performing logging operation
     * @param username
     * @param password
     * @return true if login success else false
     */
    public boolean login(String username, String password) {

        String hashedPassword = hashAlgorithm.generateSecureHashPassword(password);
        if (hashedPassword.equals(ifileStorageAccessManager.getPassword(username))) {
            return true;
        }
        return false;
    }

    /**
     * function to verify the answer of the security question.
     * @param username
     * @param answer
     * @return true if user's answer is correct else false
     */
    public boolean verifySecurityQuestion(String username,String answer) {
        if (answer.equals(ifileStorageAccessManager.getSecurityQuestionAnswer(username,"answer"))) {
            return true;
        }
        return false;
    }

    /**
     * Service layer function to perform signup operation by calling data layer function
     * @param username
     * @param password
     * @param question
     * @param answer
     * @return true if signup success, else false
     */
    public boolean signUp(String username, String password, String question, String answer) {
        return ifileStorageAccessManager.signUpUserDetails(username, password, question, answer);
    }

}