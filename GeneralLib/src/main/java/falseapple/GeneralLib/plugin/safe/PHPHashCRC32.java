package falseapple.GeneralLib.plugin.safe;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import jonelo.jacksum.algorithm.*;

/**
 * Depends on Jacksum by jonelo
 * http://sourceforge.net/projects/jacksum/
 * 
 * Static method equivalent to PHP's function: hash('crc32', $value);
 * Java's CRC32 class is actually equivalent to: hash('crc32b, $value);
 * 
 * @author tirino
 *
 */
public final class PHPHashCRC32 {
	protected static final String ENCODING = "UTF-8";
	protected static final int LENGTH = 8;
	
	/**
	 * Return a hex string equivalent to calling PHP's function: hash('crc32', $value);
	 * @param value
	 * @return crc32 value in hexadecimal notation.
	 */
	static public String encode(String value) {
		CrcGeneric crc = null;
		try {
			// See: http://regregex.bbcmicro.net/crc-catalogue.htm#crc.cat.crc-32-bzip2
			crc = new CrcGeneric(32, 0x04C11DB7L, 0xFFFFFFFFL, false, false, 0xFFFFFFFFL);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		byte[] bytes = null;
		try {
			bytes = value.getBytes(ENCODING); // Try using an specific encoding
		} catch (UnsupportedEncodingException ex) {
			bytes = value.getBytes(); // Try without setting a charset
		} finally {
			if (bytes != null) {
				crc.update(bytes, 0, bytes.length);
			}
		}
		int crcValue = (int) crc.getValue();

		// We need to reverse the byte order to match PHP's result
		crcValue = Integer.reverseBytes(crcValue);
		String result = Integer.toHexString(crcValue);
		
		// Add leading zeros
		if (result.length() < LENGTH) {
			for(int i=result.length(); i < LENGTH; i++) {
				result = "0" + result;
			}
		}
		return result;
	}
}