package bitlegend.bitauth.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
		boolean r = true; // You can't really fail to type this command properly
		Player player = (Player)sender;

		// Find the player in the loggedIn list
		int index = 0;
		boolean playerFound = false;
		for (Player p : instance.loggedIn) {
			if (p.getName().equals(player.getName())) {
				index = instance.loggedIn.indexOf(p);
				playerFound = true;
			}
		}
		
		if (playerFound == true) {
			// Remove the player from the loggedIn list
			instance.loggedIn.remove(index);
			
			// Add the player to the requireLogin list
			instance.requireLogin.add(player);
			
			try {
				Connection conn = DriverManager.getConnection(url, user, pass);
				String query = "UPDATE `" + logintable + "` SET lastlogintime='1' WHERE username='"
						+ player.getName() + "'";
				Statement update = conn.createStatement();
				update.executeUpdate(query);
				
				update.close();
				conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		else {
			player.sendMessage(ChatColor.GREEN
					+ "It appears that you are not logged in to begin with!");
		}
		
		if (playerFound == true)
			player.sendMessage(ChatColor.GREEN + "Successfully logged out");

		return r;
	}

}
