package ss.sqlsanitation;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HexFormat;

public final class AESEncryption
{
    private static Cipher cipherE;
    private static Cipher cipherD;

    //static variable cipherE initialization, along with key retrieval/creation
    static
    {
        try
        {
            SecretKey key = null;
            char[] pwd = "password".toCharArray();  //password for key store and key store entries

            //check if a key store (i.e. previously generated key) already exists
            File file = new File("./passwords.jks");
            KeyStore ks = KeyStore.getInstance("pkcs12");

            //if it doesn't make a new one
            if (!file.exists())
            {
                //create a new key for AES encryption
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);
                key = keyGen.generateKey();

                //create the key store
                ks.load(null, pwd);

                //store the newly generated key in the key store
                KeyStore.SecretKeyEntry secKey = new KeyStore.SecretKeyEntry(key);
                KeyStore.ProtectionParameter proPara = new KeyStore.PasswordProtection(pwd);
                ks.setEntry("key", secKey, proPara);

                //create the key store file
                FileOutputStream fos = new FileOutputStream("./passwords.jks");
                ks.store(fos, pwd);
            }else   //if a key store already exists
            {
                //load the key store
                FileInputStream fis = new FileInputStream("./passwords.jks");
                ks.load(fis, pwd);

                //load the key from the key store
                key = (SecretKey) ks.getKey("key", pwd);
            }

            //initialize 2 ciphers, 1 for encrypting 1 for decrypting
            cipherE = Cipher.getInstance("AES");
            cipherE.init(Cipher.ENCRYPT_MODE, key);
            cipherD = Cipher.getInstance("AES");
            cipherD.init(Cipher.DECRYPT_MODE, key);
        }catch (IOException e) //if the key store type doesn't match or password is incorrect
        {
            System.out.println("mismatch error");
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
            System.out.println("E: " + e);
            return "";
        }
    }


    /**
     * Decrypts an input string with 128-bit AES cipherD, and returns the original plain text.
     * @param cipherText The string to be decrypted
     * @return The original plain text in string format
     */
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
            System.out.println("E: " + e);
            return "";
        }
    }
}
