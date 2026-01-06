package ss.sqlsanitation;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public final class AESEncryption
{
    private static Cipher cipher;

    //static variable cipher initialization
    static
    {
        try
        {
            //set up the cipher for AES
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey key = keyGen.generateKey();

            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }catch (Exception e)
        {
            System.out.println("Something wrong with initial");
        }
        System.out.println("static initial");
    }

    /**
     * Encrypts an input string with the 128-bit AES cipher, and returns a string of binary digits representing the
     * ciphertext.
     * @param plainText The string to be encrypted
     * @return The cipher text, in a binary form string
     */
    public static String encryptAES(String plainText)
    {
        try
        {
            //encrypt plain text with AES cipher
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            //now must convert in back into a string
            StringBuilder encryptedString = new StringBuilder();

            //go through each byte in the ciphertext...
            for (byte b: encryptedBytes)
            {
                encryptedString.append(String.format("%02x", b));
            }

            //return the encrypted plain text in string form
            return encryptedString.toString();
        }catch (Exception e) //if encryption fails, return blank string
        {
            return "";
        }
    }

}
