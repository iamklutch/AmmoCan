package com.yukidev.ammocan.utils;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Created by James on 5/17/2015.
 */
public class Crypto {

    Cipher mEncryptCipher;
    Cipher mDecryptCipher;

    // salt makes everything taste better
    byte[] salt = { *, *, *, *, *, *, *, * };

    // Iteration count
    int iterationCount = 1979;

    public Crypto(String passPhrase) {
        try {
            // create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWITHSHA256AND128BITAES-CBC-BC").
                    generateSecret(keySpec);

            mEncryptCipher = Cipher.getInstance(key.getAlgorithm());
            mDecryptCipher = Cipher.getInstance(key.getAlgorithm());

            // prepare paramaters for ciphers
            AlgorithmParameterSpec parameterSpec = new PBEParameterSpec(salt, iterationCount);

            // create ciphers
            mEncryptCipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
            mDecryptCipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
        } catch (Exception e) {

        }
    }

    public String encrypt(String string) {
        String encryptValue;
        try {
            // encode into bytes using UTF-8
            byte[] utf8 = string.getBytes("UTF8");

            // encrypt
            byte[] encrypted = mEncryptCipher.doFinal(utf8);

            // encode bytes to base64 to get string
            encryptValue = toHex(encrypted);
        } catch (Exception e) {
            encryptValue = "Error encrypting: " + e.getMessage();
        }
        return encryptValue;
    }

    public String decrypt(String string) {
        String decryptValue;
        try {
            // decode base64 to get bytes
            byte[] decrypt = toByte(string);

            // decrypt
            byte[] utf8 = mDecryptCipher.doFinal(decrypt);

            // decode using utf8
            decryptValue = new String(utf8, "UTF8");
        } catch (Exception e) {
            decryptValue = "Error decrypting: " + e.getMessage();
        }
        return decryptValue;
    }

    private static byte[] toByte(String hexSting) {
        int hexLength = hexSting.length() / 2;
        byte[] result = new byte[hexLength];
        for (int i = 0; i < hexLength; i++)
            result[i] = Integer.valueOf(hexSting.substring(2 * i, 2 * i + 2), 16).
                    byteValue();
        return result;
    }

    private static String toHex(byte[] buffer) {
        if (buffer == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buffer.length);
        for (int i = 0; i < buffer.length; i++) {
            appendHex(result, buffer[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer stringBuffer, byte b) {
        stringBuffer.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
