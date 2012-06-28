package com.dechiridas.bitauth.util;

public class Utils {
	public static String byteToString(byte[] input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length; i++)
			sb.append(input[i]);
		return sb.toString();
	}
	
	public static long ipToLong(String ipString) {
		String[] ipSplit = ipString.split("\\.");
		long ip = 0;
		for (int i = 0; i < ipSplit.length; i++) {
			int power = 3-i;
			ip += ((Integer.parseInt(ipSplit[i]) % 256 * Math.pow(256, power)));
		}
		return ip;
	}
	
	public static long unixTimestamp() {
		return System.currentTimeMillis() / 1000L;
	}
}
