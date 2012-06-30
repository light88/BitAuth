package com.dechiridas.bitauth;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.dechiridas.bitauth.commands.*;
import com.dechiridas.bitauth.listeners.*;
import com.dechiridas.bitauth.player.PlayerManager;
import com.dechiridas.bitauth.util.*;

public class BitAuth extends JavaPlugin {
	// public objects
	public Log log = new Log(this);
	public Config config = new Config(this);
	public PlayerManager pman = new PlayerManager(this);
	public Database database = new Database(this);
	public PermissionManager pex;
	
	// public listeners
	public BAPlayerListener playerListener = new BAPlayerListener(this);
	public BABlockListener blockListener = new BABlockListener(this);
	public BAEntityListener entityListener = new BAEntityListener(this);

	@Override
	public void onDisable() {
		
	}
	
	@Override
	public void onEnable() {
		// Get permission manager
		pex = PermissionsEx.getPermissionManager();
		
		// Check if config.yml was modified
		if (!config.readString("DB_Host").equals("hostipaddress")) {
			if (database.tablesExist() != true) { // Check if tables exist
				log.println("Generating tables `"
						+ config.readString("DB_Table_login") + "` and `"
						+ config.readString("DB_Table_whitelist") + "`");
				database.generateTables();
			} else {
				log.println("Database and tables found.");
			}
		}
		
		// Create variables
		PluginManager pm = getServer().getPluginManager();
		
		// Check if whitelist is enabled
		if (config.readBoolean("Use_Whitelist") == true)
			log.println("Starting with whitelist enabled");
		else
			log.println("Starting with whitelist disabled");
		
		// Register events
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.entityListener, this);
		
		// Register commands
		getCommand("register").setExecutor(new Register(this));
		getCommand("login").setExecutor(new Login(this));
		getCommand("ipcheck").setExecutor(new Ipcheck(this));
		getCommand("whitelist").setExecutor(new Whitelist(this));
		getCommand("logout").setExecutor(new Logout(this));
		getCommand("unregister").setExecutor(new Unregister(this));
		getCommand("chpasswd").setExecutor(new Chpasswd(this));
		getCommand("pwreset").setExecutor(new Pwreset(this));
		getCommand("jumblepw").setExecutor(new Jumblepw(this));
		getCommand("baversion").setExecutor(new Baversion(this));
	}
}
