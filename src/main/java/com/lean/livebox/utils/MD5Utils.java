package com.lean.livebox.utils;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

/**
 * Created by lean on 16/7/5.
 */
public class MD5Utils {

    public static byte[] toMD5(byte[] source) {
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            return md5Digest.digest(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toMD5Hex(byte[] source) {
        byte[] digest = toMD5(source);
        if (digest != null) {
            return Hex.encodeHexString(digest);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Integer.toHexString(1467708159));
    }
}
