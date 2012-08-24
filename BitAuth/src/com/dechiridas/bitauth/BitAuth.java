package com.dechiridas.bitauth;

import org.bukkit.configuration.file.FileConfiguration;
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
	public FileConfiguration config = null;
	public PlayerManager pman = null;
	public Database database = null;
	public PermissionManager pex = null;
	
	// public listeners
	public BAPlayerListener playerListener = null;
	public BABlockListener blockListener = null;
	public BAEntityListener entityListener = null;
	public BAInventoryListener inventoryListener = null;
	
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
		
		// Create plugin configuration directory if it doesn't exist
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdir();
		config = this.getConfig();
		
		// Create default config if it doesn't exist
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		if (log == null) log = new Log(this);
		if (pman == null) pman = new PlayerManager(this);
		if (database == null) database = new Database(this);
		
		// Check databases, generate if not found
		// will give errors if config isn't set up properly
		if (database.tablesExist() == false)
			log.println("Generating missing tables...");
		 
		// Get permission manager
		pex = PermissionsEx.getPermissionManager();
		
		// Create variables
		PluginManager pm = getServer().getPluginManager();
		
		// Check if whitelist is enabled
		if (database.whitelistEnabled() == true)
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
		if (inventoryListener == null) inventoryListener = new BAInventoryListener(this);
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.entityListener, this);
		pm.registerEvents(this.inventoryListener, this);
		
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
		getCommand("iphistory").setExecutor(new Iphistory(this));
	}
}
