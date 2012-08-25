package com.dechiridas.bitauth.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.entity.Player;

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
	
	public static boolean isPlayerPremium(Player player) {
		return isPlayerPremium(player.getName());
	}
	
	public static boolean isPlayerPremium(String name) {
		boolean premium = false;
		
		try {
			URL url = new URL("http://www.minecraft.net/haspaid.jsp?user=" + name);
			String str = new BufferedReader(new InputStreamReader(url.openStream())).readLine().toUpperCase();
			premium = Boolean.valueOf(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return premium;
	}
}
