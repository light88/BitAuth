package bitlegend.bitauth.commands;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bitlegend.bitauth.BitAuth;

public class Login implements CommandExecutor {
	private BitAuth instance;
	
	// Database info
	private String user = "";
	private String pass = "";
	private String url = "";
	private String logintable = "";

	public Login(BitAuth instance) {
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
		Player player = (Player)sender;
		
		try {
			boolean isLoggedIn = false;
			for (Player p : instance.loggedIn) {
				if (p.getDisplayName().equals(player.getDisplayName()))
					isLoggedIn = true;
			}
			
			if (!isLoggedIn) {
				// Create connection
				Connection conn = DriverManager.getConnection(url, user, pass);
				
				Statement select = conn.createStatement();
				ResultSet result = select.executeQuery(
						"SELECT * FROM `" + logintable + "` WHERE username='" + player.getDisplayName() + "'");
				if (result.next()) { // Results found
					byte[] salt = result.getBytes(2);
					byte[] hash = result.getBytes(3);
					byte[] passwordCheck = GenerateHash(split[0], salt);
					long playerIP = result.getLong(6);
					boolean checkIP = result.getBoolean(7);
					
					// Get player IP
					InetAddress iAddress = player.getAddress().getAddress();
					long currentIP = instance.ipToLong(iAddress.getHostAddress());
					
					// Check player IP if necessary
					if ((!checkIP) || (checkIP && playerIP == currentIP)) { // IP addresses match
						if (instance.byteToString(passwordCheck).equals(
								instance.byteToString(hash))) { // Passwords match
							player.sendMessage(ChatColor.GREEN
									+ "Password correct, welcome back "
									+ player.getDisplayName());
							
							// Get unix time stamp
							long unixtime = System.currentTimeMillis() / 1000L;
							
							// Set up the query
							String query = "UPDATE `" + logintable + "` SET lastlogintime='" +
									unixtime + "' WHERE username='" + player.getDisplayName() + "'";
							PreparedStatement statement = conn.prepareStatement(query);
							statement.executeUpdate();
							statement.close();
							
							// Add player to the logged in list
							instance.loggedIn.add(player);
							
							// Remove player from requireLogin list
							int index = 0;
							for (Player p : instance.requireLogin) {
								if (p.getDisplayName().equals(player.getDisplayName()))
									index = instance.requireLogin.indexOf(p);
							}
							instance.requireLogin.remove(index);
							if (instance.requireLogin.indexOf(player) > 0)
								instance.requireLogin.remove(player);
						}
						else { // Passwords do not match
							player.sendMessage(ChatColor.GREEN + "Password incorrect, try again.");
						}
					}
					else { // IP addresses do not match
						player.sendMessage(new String[] {
								ChatColor.GREEN
										+ "Looks like you are under lockdown and your IP doesn't match.",
								ChatColor.GREEN
										+ "If this is an error, contact an admin in IRC." });
					}
					r = true;
				}
			}
			else {
				player.sendMessage(ChatColor.GREEN + "You are already logged in, silly!");
				r = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return r;
	}
	
	private byte[] GenerateHash(String input, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.reset();
		md.update(salt);
		
		return md.digest(input.getBytes("UTF-8"));
	}

}
