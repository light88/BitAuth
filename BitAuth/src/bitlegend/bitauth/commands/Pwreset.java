package bitlegend.bitauth.commands;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import bitlegend.bitauth.BitAuth;

public class Pwreset implements CommandExecutor {
	private BitAuth instance;
	
	// Database info
	private String user = "";
	private String pass = "";
	private String url = "";
	private String logintable = "";
	
	public Pwreset(BitAuth instance) {
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
			if ((instance.pex.has(player, "bitauth.pwreset") || player.isOp()) 
					&& split.length == 2) { // Proper syntax usage and permission
				String username = split[0];
				String passwd = split[1];
				
				try {
					// Create connection and exect SELECT query
					Connection conn = DriverManager.getConnection(url, user, pass);
					String query = "SELECT * FROM `" + logintable + "` WHERE `username`='" + username + "'";
					Statement statement = conn.createStatement();
					ResultSet result = statement.executeQuery(query);
					
					if (result.next()) { // Data found
						// Generate new password
						byte[] salt = GenerateSalt();
						byte[] pass = GenerateHash(passwd, salt);
						
						// Create update query
						String queryUpdate = "UPDATE `"
								+ logintable
								+ "` SET `pwreset`= ?, `temppwd`= ?, `tmpsalt`= ? WHERE `username`= ?";
						
						PreparedStatement update = conn.prepareStatement(queryUpdate);
						
						// Set values
						update.setInt(1, 1);
						update.setBytes(2, pass);
						update.setBytes(3, salt);
						update.setString(4, username);
						
						// Execute the query
						update.execute();
						
						// Clean up
						update.close();
						
						// Send feedback to player
						player.sendMessage(ChatColor.YELLOW
								+ "Password for "
								+ username
								+ " has been reset; Inform them of their new temp password");
					}
					
					result.close();
					statement.close();
					conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				} catch (NoSuchAlgorithmException nsae) {
					nsae.printStackTrace();
				} catch (UnsupportedEncodingException uee) {
					uee.printStackTrace();
				}

				r = true;
			}
			else if (!player.hasPermission("bitauth.pwreset")) {
				player.sendMessage(ChatColor.YELLOW + "You do not have access to this feature");
				r = true;
			}
		}
		if (sender instanceof ConsoleCommandSender) {
			if (split.length == 2) {
				String username = split[0];
				String passwd = split[1];
				
				try {
					// Create connection and exect SELECT query
					Connection conn = DriverManager.getConnection(url, user, pass);
					String query = "SELECT * FROM `" + logintable + "` WHERE `username`='" + username + "'";
					Statement statement = conn.createStatement();
					ResultSet result = statement.executeQuery(query);
					
					if (result.next()) { // Data found
						// Generate new password
						byte[] salt = GenerateSalt();
						byte[] pass = GenerateHash(passwd, salt);
						
						// Create update query
						String queryUpdate = "UPDATE `"
								+ logintable
								+ "` SET `pwreset`= ?, `temppwd`= ?, `tmpsalt`= ?, `lastlogintime`= ? WHERE `username`= ?";
						
						PreparedStatement update = conn.prepareStatement(queryUpdate);
						
						// Set values
						update.setInt(1, 1);
						update.setBytes(2, pass);
						update.setBytes(3, salt);
						update.setLong(4, 0);
						update.setString(5, username);
						
						// Execute the query
						update.execute();
						
						// Clean up
						update.close();
						
						// Send feedback to player
						instance.logInfo("Password for "
								+ username
								+ " has been reset; Inform them of their new temp password");
					}
					
					result.close();
					statement.close();
					conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				} catch (NoSuchAlgorithmException nsae) {
					nsae.printStackTrace();
				} catch (UnsupportedEncodingException uee) {
					uee.printStackTrace();
				}

				r = true;
			}
		}
		
		return r;
	}
	
	private byte[] GenerateSalt() {
		Random r = new SecureRandom();
		byte[] salt = new byte[20];
		r.nextBytes(salt);
		
		return salt;
	}
	
	private byte[] GenerateHash(String input, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.reset();
		md.update(salt);

		return md.digest(input.getBytes("UTF-8"));
	}
}
