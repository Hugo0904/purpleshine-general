package com.purpleshine.general.plugin.safe;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;   

/**
 * 3DES加解密工具类
 */
public final class TripleDES {

    private TripleDES() {
        //
    }

    /**
     * Decrypt DES/CBC/PKCS5Padding
     * 
     * @param base64edData
     *            经过base64编码的数据
     * @param base64edKey
     *            经过base64编码的数据
     * @return 解密后的数据(UTF-8编码)
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    static public String decryptCBCPKCS5Padding(String base64edData, String base64edKey)
            throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        return StringUtils.newStringUtf8(decrypt(base64edData.getBytes(Charsets.UTF_8), base64edKey.getBytes(Charsets.UTF_8)));
    }

    /**
     * Encrypt DES/CBC/PKCS5Padding
     * 
     * @param input
     * @param key
     * @return
     * @throws GeneralSecurityException
     */
    static public byte[] encryptCBCPKCS5Padding(byte[] input, byte[] key) throws GeneralSecurityException {
        final SecretKeySpec skey = new SecretKeySpec(key, "DES");
        final Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        final IvParameterSpec IvParameters = new IvParameterSpec(key);
        cipher.init(Cipher.ENCRYPT_MODE, skey, IvParameters);
        return cipher.doFinal(input);
    }
    
    /**
     * 加密<br/>
     * 
     * @param data
     *            被加密的数据
     * @param base64edKey
     *            经过base64编码的数据
     * @return 加密过的数据(经过Base64编码)
     * @throws Exception
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    static public String encrypt(String data, String base64edKey) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        return Base64.encodeBase64String(encrypt(data.getBytes(Charsets.UTF_8), base64edKey.getBytes(Charsets.UTF_8)));
    }

    /**
     * 加密<br/>
     * 
     * @param data
     * @param key
     * @return 加密过的数据(经过Base64编码)
     * @throws Exception
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    static public byte[] encrypt(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        final SecureRandom random = new SecureRandom();
        final DESKeySpec desKey = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        final SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成加密操作
        final Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
        return cipher.doFinal(data);
    }

    /**
     * 解密<br/>
     * 
     * @param data
     * @param key
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // DES算法要求有一个可信任的随机数源
        final SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        final DESKeySpec desKey = new DESKeySpec(key);
        // 创建一个密匙工厂
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        final SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        final Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(data);
    }
}