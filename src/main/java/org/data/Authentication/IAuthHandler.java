package org.data.Authentication;


/**
 * Service Layer Interface for handling login and signup operations.
 */
public interface IAuthHandler {

    public boolean login(String username, String password);

    public boolean verifySecurityQuestion(String username,String answer);
    public boolean signUp(String username, String password, String question, String answer);

}
