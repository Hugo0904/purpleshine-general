package com.purpleshine.general.helpers;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class SecurityUtil {

    /**
     * 轉換MD5
     * 
     * @return
     */
    static public String md5(final String content) {
        return DigestUtils.md5Hex(content);
    }

    static public String encryptAES(final String data, final String key, final String iv) throws GeneralSecurityException, UnsupportedEncodingException {
        final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        final int blockSize = cipher.getBlockSize();
        final byte[] dataBytes = data.getBytes("UTF-8");
        
        int plainTextLength = dataBytes.length;
        if (plainTextLength % blockSize != 0) {
            plainTextLength = plainTextLength + (blockSize - plainTextLength % blockSize);
        }
        
        final byte[] plaintext = new byte[plainTextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        final SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        final IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        final byte[] encrypted = cipher.doFinal(plaintext);
        return Base64.encodeBase64URLSafeString(encrypted);
    }
    
    static public String encryptAES(String input, String key) throws GeneralSecurityException {
        byte[] crypted = null;
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skey);
        crypted = cipher.doFinal(input.getBytes());
        return new String(Base64.encodeBase64(crypted));
    }
    
    static public String decryptAES(String input, String key) throws GeneralSecurityException {
        byte[] output = null;
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skey);
        output = cipher.doFinal(Base64.decodeBase64(input));
        return new String(output);
    }
}
