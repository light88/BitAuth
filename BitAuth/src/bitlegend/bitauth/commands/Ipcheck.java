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

public class Ipcheck implements CommandExecutor {
	@SuppressWarnings("unused")
	private BitAuth instance;
	
	// Database info
	private String user = "";
	private String pass = "";
	private String url = "";

	public Ipcheck(BitAuth instance) {
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
		
		if (split.length == 1) {
			if (split[0].equals("enable") || split[0].equals("disable")) {
				boolean ipcheckFlag = false;
				int ipcheckFlagInt = 0;
				
				// Check the input
				if (split[0].equals("enable"))
					ipcheckFlag = true;
				if (split[0].equals("disable"))
					ipcheckFlag = false;
				
				if (ipcheckFlag == true)
					ipcheckFlagInt = 1;
				if (ipcheckFlag == false)
					ipcheckFlagInt = 0;
				
				try {
					// Create connection
					Connection conn = DriverManager.getConnection(url, user, pass);
					String query = "UPDATE `bit_login` SET enableipcheck='"
							+ ipcheckFlagInt + "' WHERE username='"
							+ player.getDisplayName() + "'";
					Statement update = conn.createStatement();
					update.executeUpdate(query);
					
					update.close();
					
					player.sendMessage(ChatColor.GREEN
							+ "IP checking for your account has been "
							+ ((ipcheckFlag == true) ? "enabled" : "disabled"));
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
			else {
				player.sendMessage("Error: Argument " + split[0] + " not recognized");
			}
			r = true;
		}
		
		return r;
	}

}
