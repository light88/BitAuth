package bitlegend.bitauth.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import bitlegend.bitauth.BitAuth;

public class Logout implements CommandExecutor {
	private BitAuth instance;
	
	// Database info
	private String user = "";
	private String pass = "";
	private String url = "";
	private String logintable = "";
	
	public Logout(BitAuth instance) {
		this.instance = instance;
		user = instance.config.readString("DB_User");
		pass = instance.config.readString("DB_Pass");
		url = "jdbc:mysql://" + instance.config.readString("DB_Host") + 
			"/" + instance.config.readString("DB_Name");
		logintable = instance.config.readString("DB_Table_login");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;

		if (sender instanceof Player) {
			Player psend = (Player)sender;
			if (instance.pex.has(psend, "bitauth.logout") || psend.isOp()) {
				// Arguments are of proper length
				if (split.length == 1) {
					r = true;
					// Get target players name
					String name = split[0];
					Player player = null;
					boolean playerFound = false;
					
					// Look for player in list of connections
					for (Player p : instance.getServer().getOnlinePlayers())
						if (p.getName().equals(name)) {
							player = p;
							playerFound = true;
						}
					
					if (playerFound == true) {
						// Remove the player from the loggedIn list
						instance.loggedIn.remove(player);
						
						// Add the player to the requireLogin list
						instance.requireLogin.add(player);
						
						try {
							// Create connection
							Connection conn = DriverManager.getConnection(url, user, pass);
							// Query to reset last login time to 1
							String query = "UPDATE `"
									+ logintable
									+ "` SET lastlogintime='1' WHERE username='"
									+ player.getName() + "'";
							// Create statement object
							Statement update = conn.createStatement();
							// Execute update
							update.executeUpdate(query);
							
							// Message sender
							psend.sendMessage(ChatColor.YELLOW + 
									"Player " + name + " has been logged out");
							
							// Message recipient
							player.sendMessage(ChatColor.YELLOW + "You have been logged out");
							
							// Clean up
							update.close();
							conn.close();
						} catch (SQLException se) {
							se.printStackTrace();
						}
					}
				}
			}
			else {
				psend.sendMessage(ChatColor.YELLOW +
						"You do not have access to this feature.");
				r = true;
			}
		}
		
		if (sender instanceof ConsoleCommandSender) {
			// Arguments are of proper length
			if (split.length == 1) {
				r = true;
				// Get target players name
				String name = split[0];
				Player player = null;
				boolean playerFound = false;
				
				// Look for player in list of connections
				for (Player p : instance.getServer().getOnlinePlayers())
					if (p.getName().equals(name)) {
						player = p;
						playerFound = true;
					}
				
				if (playerFound == true) {
					// Remove the player from the loggedIn list
					instance.loggedIn.remove(player);
					
					// Add the player to the requireLogin list
					instance.requireLogin.add(player);
					
					try {
						// Create connection
						Connection conn = DriverManager.getConnection(url, user, pass);
						// Query to reset last login time to 1
						String query = "UPDATE `" + logintable
								+ "` SET lastlogintime='1' WHERE username='"
								+ player.getName() + "'";
						// Create statement object
						Statement update = conn.createStatement();
						// Execute update
						update.executeUpdate(query);
						
						// Message console
						instance.logInfo("Player " + name + " has been logged out");
						
						// Message recipient
						player.sendMessage(ChatColor.YELLOW + "You have been logged out");
						
						// Clean up
						update.close();
						conn.close();
					} catch (SQLException se) {
						se.printStackTrace();
					}
				}
			}
		}

		return r;
	}

}
