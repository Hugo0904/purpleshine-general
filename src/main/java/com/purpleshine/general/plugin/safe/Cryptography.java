package com.purpleshine.general.plugin.safe;

import java.security.MessageDigest;

public final class Cryptography {
	static private String[][] Cryptography = 	{{"W","D","u","X","t","y","x","C"}
										,{"V","v","7","K","A","z","w","Z"}
										,{"U","T","B","J","s","Y","L","0"}
										,{"a","S","","b","r","c","2","1"}
										,{"R","E","I","%","8","q","d","O"}
										,{"M","Q","m","n","o","p","e","9"}
										,{"N","l","f","P","h","3","i","H"}
										,{"6","k","5","g","4","F","G","j"}};
	
	static private String Account = "javafollow";
	static private String PassWord = "4321qaz";
	public Cryptography() {
		// TODO Auto-generated constructor stub
	}
	
	static public String toMD5(String TimeString) {
		String md5String = null;
		String Plaintext = Account + PassWord + TimeString;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(Plaintext.getBytes());
			byte[] digest = md.digest();
			md5String = toHex(digest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5String;
	}

	static private String toHex(byte[] digest) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < digest.length; ++i) {
			byte b = digest[i];
			int value = (b & 0x7F) + (b < 0 ? 128 : 0);
			buffer.append(value < 16 ? "0" : "");
			buffer.append(Integer.toHexString(value));
		}
		return buffer.toString();
	}
	static public String toCostomEncryption(String TimeString){
		StringBuffer before = new StringBuffer(TimeString);
		StringBuffer after = new StringBuffer(0);
		char[] c = before.toString().toCharArray();
		for (int i = c.length-1; i >= 0; i--) {
			after.append(c[i]);
		}
		return after.toString();
	}
	static public String toCostomDecryption(String TimeString){
		StringBuffer before = new StringBuffer(TimeString);
		StringBuffer after = new StringBuffer(0);
		char[] c = before.toString().toCharArray();
		for (int i = c.length-1; i >= 0; i--) {
			after.append(c[i]);
		}
		return after.toString();
	}
	static public String toEncrypt(String data){
		char[] dataArr = data.toCharArray();
		String result = "";	
		String tmpString = "";
		for (int k = 0; k < dataArr.length; k++) {
			tmpString = "";
			for (int i = 0; i < Cryptography.length; i++) {
				for (int j = 0; j < Cryptography[i].length; j++) {
					if (Cryptography[i][j].length() > 0 && dataArr[k] == Cryptography[i][j].charAt(0)) {
						tmpString += i+""+j;
						break;
					}
				}
			}
			result += !tmpString.equals("") ? tmpString:dataArr[k];
		}
		return result;
	}
}
