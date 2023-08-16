package org.data.Authentication;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class for handling hash algorithm operations
 */
public class HashAlgorithm {
    /**
     * function for generating the password's md5 hash value and returning it
     * @param password
     * @return
     */
    public String generateSecureHashPassword(String password) {
        // Get the md5 hashing function
        MessageDigest hashingFunction = null;
        try {
            hashingFunction = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        // Perform passsword hashing using the above selected hashing function
        byte[] byteHashedPassword = hashingFunction.digest(password.getBytes());
        // Generate a string builder to store the final hexadecimal string of the md5 hashed password
        StringBuilder byteToHexadecimalString = new StringBuilder();
        // Iterate overall the hash bytes
        for (byte b : byteHashedPassword) {
            // convert the byte to a hexadecimal string
            String byteToHexString = Integer.toHexString(b & 0xFF);
            // check the length of the hexadecimal string, if its only one character then appending 0 (ensuring each hash byte has two characters in the hexadecimal output)
            if (byteToHexString.length() == 1) {
                byteToHexadecimalString.append('0');
            }
            // Add the converted decimal string to string builder
            byteToHexadecimalString.append(byteToHexString);
        }
        // return the hashed result
        return byteToHexadecimalString.toString();
    }
}
