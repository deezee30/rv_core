/*
 * rv_core
 * 
 * Created on 12 July 2017 at 12:17 AM.
 */

package com.riddlesvillage.core.security;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public final class RSA {

    private RSA() {}

    public static KeyPair generateKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException ignored) {}
        return null;
    }

    public static String encrypt(final String data, final PublicKey key) {
        try {
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, key);
            return B64.toBase64(rsa.doFinal(data.getBytes()));
        } catch (Exception ignored) {}
        return null;
    }

    public static String decrypt(final String data, final PrivateKey key) {
        try {
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, key);
            return new String(rsa.doFinal(B64.fromBase64(data)));
        } catch (Exception ignored) {}
        return null;
    }

    public static PrivateKey loadPrivateKey(final String key64) throws GeneralSecurityException, IOException {
        byte[] clear = B64.fromBase64(key64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    public static PublicKey loadPublicKey(final String key64) throws GeneralSecurityException, IOException {
        byte[] data = B64.fromBase64(key64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }

    public static String savePrivateKey(final PrivateKey priv) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(priv, PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String key64 = B64.toBase64(packed);
        Arrays.fill(packed, (byte) 0);
        return key64;
    }

    public static String savePublicKey(final PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publ, X509EncodedKeySpec.class);
        return B64.toBase64(spec.getEncoded());
    }
}