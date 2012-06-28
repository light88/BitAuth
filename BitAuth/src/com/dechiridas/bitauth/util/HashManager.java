package com.dechiridas.bitauth.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class HashManager {
	public final static int HASH_ITERATIONS = 1000;
	public final static String HASH_ALGORITHM = "SHA-512";
	public final static String ENCODING = "UTF-8";
	
	public static byte[] GenerateSalt() {
		Random r = new SecureRandom();
		byte[] salt = new byte[20];
		r.nextBytes(salt);
		
		return salt;
	}
	
	public static byte[] GenerateHash(String input, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
		md.reset();
		md.update(salt);
		
		byte[] store = md.digest(input.getBytes(ENCODING));
		
		for (int i = 0; i < HASH_ITERATIONS - 1; i++)
			store = md.digest(store);
		
		return store;
	}
	
	public static String RandomString() {
		return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}
}
