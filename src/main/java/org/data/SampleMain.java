package org.data;

import org.data.Authentication.HashAlgorithm;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleMain {
    private static String basePath = "src/main/resources/";

    public static void main(String[] args) {

        FileWriter fileHandlerObj = null;
        String username = "me";
        String password = "me2";
        String question = "me3";
        String answer = "me4";
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
            System.out.println("[**ERROR**] ERROR LOCATING TABLE! ERROR MESSAGE: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[**ERROR**] ERROR SAVING RESULT! ERROR MESSAGE: "+e.getMessage());
        }
    }
}