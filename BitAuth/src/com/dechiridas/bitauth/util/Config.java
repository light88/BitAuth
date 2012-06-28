package com.dechiridas.bitauth.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import com.dechiridas.bitauth.BitAuth;

public class Config {
	private BitAuth instance;
	private Log log;
	public String directory = "plugins" + File.separator + BitAuth.class.getSimpleName();
	File file = new File(directory + File.separator + "config.yml");
	
	public Config(BitAuth instance) {
		this.instance = instance;
		this.log = this.instance.log;
		checkConfig();
	}
	
	public void checkConfig() {
		new File(directory).mkdir();
		if (!file.exists()) {
			try {
				file.createNewFile();
				addDefaults();
			} catch (Exception e) {
				log.println("Unable to create config file.");
			}
		} else {
			loadKeys();
		}
	}
	
	public YamlConfiguration load() {
		try {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			return config;
		} catch (Exception e) {
			log.println("Unable to load config file.");
		}
		return null;
	}
	
	private void addDefaults() {
		log.println("Generating Config file...");
		write("Use_Whitelist", false);
		write("Login_Time_Limit", 120);
		write("DB_User", "username");
		write("DB_Name", "database");
		write("DB_Pass", "password");
		write("DB_Host", "hostipaddress");
		write("DB_Table_login", "logintable");
		write("DB_Table_whitelist", "whitelisttable");
	}
	
	private void loadKeys() {
		log.println("Loading Config File...");
	}
	
	public void write(String root, Object x) {
		YamlConfiguration config = load();
		config.set(root, x);
		try {
			config.save(file);
		} catch (IOException e) {
			log.println("There was an error saving configuration to file " + file.getName());
		}
	}
	
	public Boolean readBoolean(String root) {
		YamlConfiguration config = load();
		return config.getBoolean(root, true);
	}
	
	public Double readDouble(String root) {
		YamlConfiguration config = load();
		return config.getDouble(root, 0);
	}
	
	public String readString(String root) {
		YamlConfiguration config = load();
		return config.getString(root);
	}
}
