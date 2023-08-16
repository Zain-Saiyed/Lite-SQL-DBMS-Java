package org.data.Controller;

import org.data.Authentication.AuthHandler;
import org.data.DataAccessManager.FileStorageAccessManager;
import org.data.DataAccessManager.IFileStorageAccessManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Authentication controller class handling UI operation for authentication
 */
public class AuthController {

    static AuthHandler authHandler;
    private String basePath = "src/main/resources/";
    String username = null;

    static IFileStorageAccessManager ifileStorageAccessManager = new FileStorageAccessManager();

    public String getUsername() {
        return username;
    }

    /**
     * constructor: it verifies if the Authentication Repository directory exists
     */
    public AuthController() {
        this.authHandler = new AuthHandler();
        if (!Files.exists(Path.of(basePath+"AuthenticationRepository"))) {
            new File(basePath+"AuthenticationRepository").mkdirs();
        }
    }

    /**
     * Controller function for handling login operation by accepting prompt from user
     * @return
     */
    public boolean login(){
        Scanner input = new Scanner(System.in);
        System.out.println("-----------------");
        System.out.println("-- LOGIN --------");
        System.out.println("-----------------");
        System.out.print("Username: ");
        String username = input.nextLine();
//        String username = "user1";
        System.out.print("Password: ");
        String password = input.nextLine();
//        String password = "pass1";
        // Strip white spaces
        username = username.strip();
        password = password.strip();
        // verify password
        boolean loginStatus = authHandler.login(username,password);
        // If password is verified
        if (loginStatus) {
            // Get security Question
            String question = ifileStorageAccessManager.getSecurityQuestionAnswer(username,"question");
            System.out.println("Question: "+question);
            System.out.print("Answer: ");
            String answer = input.nextLine();
//            String answer = "white";
            answer = answer.strip().toLowerCase();
            loginStatus = authHandler.verifySecurityQuestion(username,answer);
            if (loginStatus) this.username = username;
            return loginStatus;
        }
        return false;
    }

    /**
     * Controller function for handling signup operation by accepting prompt from user
     * @return
     */
    public boolean signup() {
        Scanner input = new Scanner(System.in);
        System.out.println("-------------------------------------");
        System.out.println("-- SIGNUP ---------------------------");
        System.out.println("-------------------------------------\n");
        System.out.println("Please Enter the following details: ");
        System.out.print("Username: ");
        String username = input.nextLine();
//        String username = "user2";
        System.out.print("Password: ");
        String password = input.nextLine();
//        String password = "pass2";
        System.out.print("Security Question: ");
        String question = input.nextLine();
//        String question = "What is H2O?";
        System.out.print("Security Answer: ");
        String answer = input.nextLine();
//        String answer = "Water";
        // Strip white spaces
        username = username.strip();
        password = password.strip();
        question = question.strip();
        answer = answer.strip().toLowerCase();
        boolean signupStatus = authHandler.signUp(username,password,question,answer);
        if (signupStatus) this.username = username;
        return signupStatus;
    }
}
