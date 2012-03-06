package bitlegend.bitauth.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bitlegend.bitauth.BitAuth;

public class Unregister implements CommandExecutor {
	private BitAuth instance;
	
	// Database info
	private String user = "";
	private String pass = "";
	private String url = "";
	
	public Unregister(BitAuth instance) {
		this.instance = instance;
		user = instance.config.readString("DB_User");
		pass = instance.config.readString("DB_Pass");
		url = "jdbc:mysql://" + instance.config.readString("DB_Host") + 
				"/" + instance.config.readString("DB_Name");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;
		Player player = (Player)sender;
		
		try {
			// Create connection
			Connection conn = DriverManager.getConnection(url, user, pass);
			String query = "DELETE FROM `bit_login` WHERE username='" + player.getDisplayName() + "'";
			Statement delete = conn.createStatement();
			delete.executeUpdate(query);
			
			// Remove player from loggedIn list
			int index = 0;
			for (Player p : instance.loggedIn) {
				if (p.getDisplayName().equals(player.getDisplayName()))
					index = instance.loggedIn.indexOf(p);
			}
			instance.loggedIn.remove(index);
			
			// Add player to unregistered list
			instance.unregistered.add(player);
			
			// Inform the player that they have been unregistered from the server
			player.sendMessage("Successfully unregistered, goodbye!");
			r = true;
			
		} catch (SQLException se) {
			se.printStackTrace();
		}
		
		return r;
	}

}
