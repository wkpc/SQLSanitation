package ss.sqlsanitation;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public final class AESEncryption
{
    private static Cipher cipherE;
    private static Cipher cipherD;

    //static variable cipherE initialization
    static
    {
        try
        {
            //set up the ciphers for AES
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey key = keyGen.generateKey();

            //use 2 ciphers, 1 for encrypting 1 for decrypting
            cipherE = Cipher.getInstance("AES");
            cipherE.init(Cipher.ENCRYPT_MODE, key);
            cipherD = Cipher.getInstance("AES");
            cipherD.init(Cipher.DECRYPT_MODE, key);
        }catch (Exception e)
        {
            System.out.println("Something wrong with initial");
        }
        System.out.println("static initialized");
    }

    /**
     * Encrypts an input string with the 128-bit AES cipherE, and returns a string of binary digits representing the
     * ciphertext.
     * @param plainText The string to be encrypted
     * @return The cipherE text, in a binary form string
     */
    public static String encryptAES(String plainText)
    {
        try
        {
            //encrypt plain text with AES cipherE
            byte[] encryptedBytes = cipherE.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

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


    public static String decryptAES(String cipherText)
    {
        try
        {
            //first convert it back into byte array and decrypt
            byte[] encryptedBytes = HexFormat.of().parseHex(cipherText);
            byte[] decryptedBytes = cipherD.doFinal(encryptedBytes);

            //then turn byte array back into a string
            String plainText = new String(decryptedBytes, StandardCharsets.UTF_8);

            return plainText;
        } catch (Exception e) //if decryption fails, return blank string
        {
            return "";
        }
    }
}
