package org.data;

import org.data.Authentication.AuthHandler;
import org.data.Controller.AuthController;
import org.data.Controller.QueryController;
import org.data.Handlers.MetaDataHandler;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Main function: starting entry point of program
 */
public class Main {
    static Scanner input = new Scanner(System.in);
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Main function. This has logic of not terminating program and navigating flow to authentication or query operations
     * @param args
     */
    public static void main(String[] args) {
        AuthController authController = new AuthController();
        MetaDataHandler metaDataHandler = new MetaDataHandler();
        // Verify authentication repository integrity
        try {
            metaDataHandler.verifyAuthenticationMetaData();
        } catch (IOException e) {
            System.out.println("[*ERROR*] Authentication Repository creation failed! Unable to start DBMS.");
            LOGGER.severe("[*ERROR*] Authentication Repository creation failed! Unable to start DBMS.");
            System.exit(1);
        }
        QueryController queryController;
        boolean Status = false;
        while(true) {
            // authentication
            Status = mainAuthentication(authController);
//            Status = true;
            while (Status) {
                String username = authController.getUsername();
                queryController = new QueryController(username);
                System.out.println("\n\n-------------------------");
                System.out.println("---- Welcome to DBMS ----");
                System.out.println("-------------------------");
                System.out.println("username: "+username);
                System.out.println("\nPlease enter your query prompt below:");
                while (true)
                    queryController.queryPrompt();
            }
        }
    }

    /**
     * Function for dealing with Authentication UI prompts
     * @param authController
     * @return boolean: true if login or signup success else false
     */
    public static boolean mainAuthentication(AuthController authController) {
        System.out.println("---------------------------");
        System.out.println("Welcome:\n[1] LOGIN\n[2] SIGNUP");
        System.out.print("Choose: ");
        String choice = input.nextLine();
        boolean Status;
        if (choice.equals("1")) {
            // LOGIN
            Status = authController.login();
            // If login Successful
            if (Status) {
                System.out.println("...Login Successful...");
                return true;
            }
            // else Login failed
            else {
                System.out.println("...Login Failed! Please try again!");
                return false;
            }
        }
        else if (choice.equals("2")) {
            // SIGN UP
            Status = authController.signup();
            // If signup is Successful
            if (Status) {
                System.out.println("...Signup Successful...");
                return true;
            }
            // else Signup failed
            else {
                System.out.println("...Signup Failed! Please try again!");
                return false;
            }
        }
        else {
            System.out.println("INVALID SELECTION! Please choose a option between [1/2]. Please try again.\n");
            LOGGER.severe("INVALID SELECTION! Please choose a option between [1/2]. Please try again.\n");
            return false;
        }
    }
}