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
            String encryptedString = "";

            //go through each byte in the hash...
            for (byte b: encryptedBytes)
            {
                //...and through each bit in each hash...
                for (int i = 0; i < 8; i++)
                {
                    //collect each bit
                    encryptedString = encryptedString + ((b >> (7 - i)) & 1);
                }
            }

            //return the encrypted plain text in string form
            return encryptedString;
        }catch (Exception e)
        {
            return "Something wrong with encryption.";
        }
    }
}
