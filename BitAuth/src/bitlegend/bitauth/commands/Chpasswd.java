package bitlegend.bitauth.commands;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bitlegend.bitauth.BitAuth;
import bitlegend.bitauth.HashManager;

public class Chpasswd implements CommandExecutor {
	private BitAuth instance;
	
	// Database info
	private String user = "";
	private String pass = "";
	private String url = "";
	private String logintable = "";
	
	public Chpasswd(BitAuth instance) {
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
			
			if (split.length == 2) {
				String oldpasswd = split[0];
				String newpasswd = split[1];
				
				try {
					Connection conn = DriverManager.getConnection(url, user, pass);
					Statement select = conn.createStatement();
					ResultSet result = select.executeQuery(
							"SELECT * FROM `" + logintable + "` WHERE username='" + player.getName() + "'");
					
					if (result.next()) { // Results found
						byte[] salt = result.getBytes(2);
						byte[] pwdhash = result.getBytes(3);
						
						byte[] pwdtest = HashManager.GenerateHash(oldpasswd, salt);
						if (instance.byteToString(pwdtest).equals(instance.byteToString(pwdhash))) { // oldpasswd matches current password
							// Generate new salt for password and new password hash from the input + salt
							byte[] newsalt = HashManager.GenerateSalt();
							byte[] newpass = HashManager.GenerateHash(newpasswd, newsalt);

							// Create query and statement
							String query = "UPDATE `" + logintable
									+ "` SET `salt`= ?, `password`= ? " + "WHERE `username`= ?";
							PreparedStatement update = conn.prepareStatement(query);
							
							// Set the values in the statement
							update.setBytes(1, newsalt);
							update.setBytes(2, newpass);
							update.setString(3, player.getName());
							
							// Execute the statement
							update.execute();
							
							// Clean up
							update.close();
							
							player.sendMessage(ChatColor.GREEN + "Password updated successfully");
						} else { // oldpasswd does not match current password
							player.sendMessage(ChatColor.YELLOW + "Incorrect password");
						}
					}
					
					result.close();
					conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				} catch (UnsupportedEncodingException uee) {
					uee.printStackTrace();
				} catch (NoSuchAlgorithmException nsae) {
					nsae.printStackTrace();
				}
				r = true;
			}
		} else {
			instance.logInfo("This is an in-game only command.");
			r = true;
		}
		
		return r;
	}
}
