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
	private String logintable = "";

	public Ipcheck(BitAuth instance) {
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
						String query = "UPDATE `" + logintable + "` SET enableipcheck='"
								+ ipcheckFlagInt + "' WHERE username='"
								+ player.getName() + "'";
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
					player.sendMessage(ChatColor.YELLOW + "Error: Argument " + split[0] + " not recognized");
				}
				r = true;
			}
		}
		else {
			instance.logInfo("This is an in-game only command.");
			r = true;
		}
		return r;
	}

}
