package com.dechiridas.bitauth.util;

import com.dechiridas.bitauth.BitAuth;

public class Log {
	@SuppressWarnings("unused")
	private BitAuth plugin;
	
	public Log(BitAuth instance) {
		this.plugin = instance;
	}
	
	public void println(String str) {
		System.out.println(str);
	}
}
