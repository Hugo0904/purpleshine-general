package com.purpleshine.general.plugin.safe;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

/**
 * 3DES加解密工具类
 */
public final class TripleDESede {

	static private final byte[] NULL_IV = Base64.decodeBase64("AAAAAAAAAAA=");

	private TripleDESede() {
	    //
	}

	/**
	 * 解密<br/>
	 * ps: 向量使用key的前8个byte
	 * 
	 * @param base64edData
	 *            经过base64编码的数据
	 * @param base64edKey
	 *            经过base64编码的数据
	 * @return 解密后的数据(UTF-8编码)
	 * @param base64edIv
	 *            为空则使用全0
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	static public String decryptCBCPKCS5Padding(String base64edData, String base64edKey, String base64edIv) throws GeneralSecurityException, UnsupportedEncodingException {
	    final byte[] keyByte = Base64.decodeBase64(base64edKey);
		final var iv = base64edIv == null ? NULL_IV : Base64.decodeBase64(base64edIv);
		return StringUtils.newStringUtf8(decryptCBCPKCS5Padding(Base64.decodeBase64(base64edData), keyByte, iv));
	}

	/**
	 * 加密<br/>
	 * ps: 向量使用key的前8个byte
	 * 
	 * @param data
	 *            被加密的数据
	 * @param base64edKey
	 *            经过base64编码的数据
	 * @param base64edIv
	 *            为空则使用全0
	 * @return 加密过的数据(经过Base64编码)
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	static public String encryptCBCPKCS5Padding(String data, String base64edKey, String base64edIv) throws GeneralSecurityException, UnsupportedEncodingException {
		final byte[] keyByte = Base64.decodeBase64(base64edKey);
		final var iv = base64edIv == null ? NULL_IV : Base64.decodeBase64(base64edIv);
		return Base64.encodeBase64String(encryptCBCPKCS5Padding(StringUtils.getBytesUtf8(data), keyByte, iv));
	}

	/**
     * 解密<br/>
     * 
     * @param data
     *            被解码的数据(注意编码转换)
     * @param key
     *            key
     * @param iv
     *            向量(必须为8byte)
     * @return
     * @throws GeneralSecurityException
     */
    static public byte[] decryptCBCPKCS5Padding(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        final SecretKey sec = keyFactory.generateSecret(new DESedeKeySpec(key));
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        final IvParameterSpec IvParameters = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, sec, IvParameters);
        return cipher.doFinal(data);
    }

    /**
     * 加密<br/>
     * 
     * @param data
     *            被解码的数据(注意编码转换)
     * @param key
     *            key
     * @param iv
     *            向量(必须为8byte)
     * @return
     * @throws GeneralSecurityException
     */
    static public byte[] encryptCBCPKCS5Padding(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        final SecretKey sec = keyFactory.generateSecret(new DESedeKeySpec(key));
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        final IvParameterSpec IvParameters = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, sec, IvParameters);
        return cipher.doFinal(data);
    }
    
    /**
     * 
     * @param dataBytes
     * @param keyBytes
     * @param ivBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    static public byte[] encryptCBCNoPadding(byte[] dataBytes, byte[] keyBytes, byte[] ivBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        final Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        final int blockSize = cipher.getBlockSize();

        int plaintextLength = dataBytes.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }

        final byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        final SecretKey sec = keyFactory.generateSecret(new DESedeKeySpec(keyBytes));
        final IvParameterSpec ivspec = new IvParameterSpec(ivBytes);

        cipher.init(Cipher.ENCRYPT_MODE, sec, ivspec);
        return cipher.doFinal(plaintext);
     }
    
    public static byte[] desEncrypt(byte[] dataBytes, byte[] keyBytes, byte[] ivBytes) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        SecretKeySpec keyspec = new SecretKeySpec(keyBytes, "DESede");
        IvParameterSpec ivspec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        return cipher.doFinal(dataBytes);
    }
}