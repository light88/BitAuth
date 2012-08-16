package com.dechiridas.bitauth;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.dechiridas.bitauth.commands.*;
import com.dechiridas.bitauth.listeners.*;
import com.dechiridas.bitauth.player.BAPlayer;
import com.dechiridas.bitauth.player.BAState;
import com.dechiridas.bitauth.player.PlayerManager;
import com.dechiridas.bitauth.util.*;

public class BitAuth extends JavaPlugin {
	// public objects
	public Log log = null;
	public Config config = null;
	public PlayerManager pman = null;
	public Database database = null;
	public PermissionManager pex = null;
	
	// public listeners
	public BAPlayerListener playerListener = null;
	public BABlockListener blockListener = null;
	public BAEntityListener entityListener = null;

	@Override
	public void onDisable() {
		// Clean up
		for (int i = pman.getPlayers().size() - 1; i >= 0; i--) {
			BAPlayer b = pman.getPlayers().get(i);
			if (b.getState() != BAState.LOGGEDIN)
				b.getPlayer().kickPlayer("Force disconnect by server.");
		}
	}
	
	@Override
	public void onEnable() {
		// Initialize
		if (log == null) log = new Log(this);
		if (config == null) config = new Config(this);
		if (pman == null) pman = new PlayerManager(this);
		if (database == null) database = new Database(this);
		 
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
		
		// Initialize player manager contents for players connected during a reload
		for (int i = 0; i < this.getServer().getOnlinePlayers().length; i++) {
			Player p = this.getServer().getOnlinePlayers()[i];
			BAPlayer b = new BAPlayer(this, p);
			b.setState(BAState.LOGGEDIN);
			pman.addPlayer(b);
		}
		
		// Register events
		if (playerListener == null) playerListener = new BAPlayerListener(this);
		if (blockListener == null) blockListener = new BABlockListener(this);
		if (entityListener == null) entityListener = new BAEntityListener(this);
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
