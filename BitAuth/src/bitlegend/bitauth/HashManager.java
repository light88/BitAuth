package bitlegend.bitauth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class HashManager {
	public static byte[] GenerateSalt() {
		Random r = new SecureRandom();
		byte[] salt = new byte[20];
		r.nextBytes(salt);
		
		return salt;
	}
	
	public static byte[] GenerateHash(String input, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.reset();
		md.update(salt);
		
		return md.digest(input.getBytes("UTF-8"));
	}
	
	public static String RandomString() {
		return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}
}
