package bitlegend.bitauth;

import bitlegend.bitauth.commands.Ipcheck;
import bitlegend.bitauth.commands.Login;
import bitlegend.bitauth.commands.Logout;
import bitlegend.bitauth.commands.Register;
import bitlegend.bitauth.commands.Unregister;
import bitlegend.bitauth.commands.Whitelist;
import bitlegend.bitauth.listeners.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BitAuth extends JavaPlugin {
	public PluginDescriptionFile pluginInfo = this.getDescription();
	public Config config = new Config(this);
	public Logger log = Logger.getLogger("Minecraft"); // broken, causes nullPointerException
	public String enableOnStart = "Enabled On Startup";
	public boolean enabled;
	
	public List<Player> unregistered;
	public List<Player> requireLogin;
	public List<Player> loggedIn;
	
	private final BAPlayerListener playerListener = new BAPlayerListener(this);
	private final BABlockListener blockListener = new BABlockListener(this);
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public void onEnable() {
		// Create variables
		PluginManager pm = getServer().getPluginManager();
		unregistered = new ArrayList<Player>();
		requireLogin = new ArrayList<Player>();
		loggedIn = new ArrayList<Player>();
		
		// Check the configuration
		config.checkConfig();
		
		// Register events
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.blockListener, this);
		
		// Register commands
		getCommand("register").setExecutor(new Register(this));
		getCommand("login").setExecutor(new Login(this));
		getCommand("ipcheck").setExecutor(new Ipcheck(this));
		getCommand("whitelist").setExecutor(new Whitelist(this));
		getCommand("logout").setExecutor(new Logout(this));
		getCommand("unregister").setExecutor(new Unregister(this));
		
		// Incomplete commands
		//getCommand("pwreset").setExecutor(new Pwreset(this));
	}
	
	public String byteToString(byte[] input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length; i++)
			sb.append(input[i]);
		return sb.toString();
	}
	
	public long ipToLong(String ipString) {
		String[] ipSplit = ipString.split("\\.");
		long ip = 0;
		for (int i = 0; i < ipSplit.length; i++) {
			int power = 3-i;
			ip += ((Integer.parseInt(ipSplit[i]) % 256 * Math.pow(256, power)));
		}
		return ip;
	}
}
