package falseapple.GeneralLib.plugin.safe;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

/**
 * 3DES加解密工具类 Author: @author: lintghi Date: 2015年3月11日 下午2:27:58
 */
public final class TripleDES {

	static private final byte[] NULL_IV = Base64.decodeBase64("AAAAAAAAAAA=");

	private TripleDES() {
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
	static public byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey sec = keyFactory.generateSecret(new DESedeKeySpec(key));
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		IvParameterSpec IvParameters = new IvParameterSpec(iv);
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
	static public byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey sec = keyFactory.generateSecret(new DESedeKeySpec(key));
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		IvParameterSpec IvParameters = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, sec, IvParameters);
		return cipher.doFinal(data);
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
	static public String decrypt(String base64edData, String base64edKey, String base64edIv)
			throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] keyByte = Base64.decodeBase64(base64edKey);
		return StringUtils.newStringUtf8(decrypt(Base64.decodeBase64(base64edData), keyByte,
				base64edIv == null ? NULL_IV : Base64.decodeBase64(base64edIv)));
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
	static public String encrypt(String data, String base64edKey, String base64edIv)
			throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] keyByte = Base64.decodeBase64(base64edKey);
		return Base64.encodeBase64String(encrypt(StringUtils.getBytesUtf8(data), keyByte,
				base64edIv == null ? NULL_IV : Base64.decodeBase64(base64edIv)));
	}

	static public String decrypt(String input, String key) throws Exception {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decodeBase64(input));
			return new String(output);
		} catch (Exception e) {
			throw e;
		}
	}

	static public String encrypt(String input, String key) throws Exception {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
			return new String(Base64.encodeBase64(crypted));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	static public String sgEncrypt(String input, String key) throws Exception {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec IvParameters = new IvParameterSpec(key.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, skey, IvParameters);
			crypted = cipher.doFinal(input.getBytes());
			return new String(Base64.encodeBase64(crypted));
		} catch (Exception e) {
			throw e;
		}
	}

	static public String md5(String input) throws NoSuchAlgorithmException {
	    String result = input;
	    if(input != null) {
	        MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
	        md.update(input.getBytes());
	        BigInteger hash = new BigInteger(1, md.digest());
	        result = hash.toString(16);
	        while(result.length() < 32) { //40 for SHA-1
	            result = "0" + result;
	        }
	    }
	    return result;
	}
}