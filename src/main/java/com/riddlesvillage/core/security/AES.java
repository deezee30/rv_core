/*
 * rv_core
 * 
 * Created on 12 July 2017 at 12:23 AM.
 */

package com.riddlesvillage.core.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private AES() {}

    public static SecretKey generateKey() {
        try {
            KeyGenerator KeyGen = KeyGenerator.getInstance("AES");
            KeyGen.init(128);
            return KeyGen.generateKey();
        } catch (Exception ignored) {}
        return null;
    }

    public static String encrypt(String data, SecretKey key) {
        String str = null;
        try {
            Cipher AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.ENCRYPT_MODE, key);
            str = B64.toBase64(AesCipher.doFinal(data.getBytes()));
        } catch (Exception ignored) {}
        return str;
    }


    public static String decrypt(String data, SecretKey key) {
        String str = null;
        try {
            Cipher AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytePlainText = AesCipher.doFinal(B64.fromBase64(data));
            str = new String(bytePlainText);
        } catch (Exception ignored) {}
        return str;
    }

    public static SecretKey toKey(String key) {
        byte[] decodedKey = B64.fromBase64(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String toString(SecretKey key) {
        return B64.toBase64(key.getEncoded());
    }
}