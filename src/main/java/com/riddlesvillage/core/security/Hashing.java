/*
 * rv_core
 * 
 * Created on 11 July 2017 at 11:48 PM.
 */

package com.riddlesvillage.core.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Hashing {

    private Hashing() {}

    /**
     * The fastest way possible to hash a {@param a} with a
     * {@code MD5} algorithm.  Usually takes about {@code 0}
     * to {@code 1} milliseconds.  The hashed String has a
     * length of 32 characters with no dashes.
     *
     * @param	a The String to be serialized.
     * @return	The hashed String in length of 32 characters,
     * 			with no dashes.
     */
    public static String hash(String a) {
        try {

            MessageDigest b = MessageDigest.getInstance("MD5");
            byte[] c = b.digest(a.getBytes());
            StringBuilder d = new StringBuilder();

            for (byte e : c)
                d.append(Integer.toHexString((e & 0xFF) | 0x100).substring(1, 3));

            return d.toString();
        } catch (NoSuchAlgorithmException ignored) {}
        return "";
    }
}