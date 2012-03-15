package bitlegend.bitauth.commands;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

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
		if (split.length >= 1 && split.length <= 2) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				
				try {
					boolean isLoggedIn = false;
					for (Player p : instance.loggedIn) {
						if (p.getName().equals(player.getName()))
							isLoggedIn = true;
					}
					
					if (!isLoggedIn) {
						// Create connection
						Connection conn = DriverManager.getConnection(url, user, pass);
						
						Statement select = conn.createStatement();
						ResultSet result = select.executeQuery(
								"SELECT * FROM `" + logintable + "` WHERE username='" + player.getName() + "'");
						if (result.next()) { // Results found
							byte[] salt = result.getBytes(2);
							byte[] hash = result.getBytes(3);
							byte[] passwordCheck = GenerateHash(split[0], salt);
							boolean pwreset = result.getInt(8) == 1 ? true : false;
							long playerIP = result.getLong(6);
							boolean checkIP = result.getBoolean(7);
							
							// Get player IP
							InetAddress iAddress = player.getAddress().getAddress();
							long currentIP = instance.ipToLong(iAddress.getHostAddress());
							
							// Check player IP if necessary
							if ((!checkIP) || (checkIP && playerIP == currentIP)) { // IP addresses match
								if (pwreset == true) {
									hash = result.getBytes(9);
									salt = result.getBytes(10);
									passwordCheck = GenerateHash(split[0], salt);
									
									if (split.length == 1) { // They didn't include the new password or don't know their password has been reset
										player.sendMessage(ChatColor.YELLOW + "Reset your password: /login <temp passwd> <new passwd>");
										player.sendMessage(ChatColor.YELLOW + "The temp password is provided by a mod or admin");
									}
									else {
										if (instance.byteToString(passwordCheck).equals(
												instance.byteToString(hash))) { // Password matches temp password
											if (split.length == 2) { // Check to ensure newpass was entered too
												player.sendMessage(ChatColor.GREEN + 
														"Password reset properly, welcome back " + player.getName());
												
												// Get unix time stamp
												long unixtime = System.currentTimeMillis() / 1000L;
												
												// Create new salt and password hash
												byte[] newsalt = GenerateSalt();
												byte[] newhash = GenerateHash(split[1], newsalt);
												
												// Create query
												String query = "UPDATE `"
														+ logintable
														+ "` SET `salt`= ?, `password`= ?, `pwreset`= ?, `temppwd`= ?, "
														+ "`tmpsalt`= ?, `lastlogintime`= ? WHERE `username`= ?";
												
												PreparedStatement update = conn.prepareStatement(query);
												
												// Set query values
												update.setBytes(1, newsalt);
												update.setBytes(2, newhash);
												update.setInt(3, 0);
												update.setBytes(4, null);
												update.setBytes(5, null);
												update.setLong(6, unixtime);
												update.setString(7, player.getName());
												
												// Execute query
												update.execute();
												
												// Clean up
												update.close();
												
												// Add player to the logged in list
												instance.loggedIn.add(player);
		
												// Remove player from requireLogin list
												int index = 0;
												for (Player p : instance.requireLogin) {
													if (p.getName().equals(player.getName()))
														index = instance.requireLogin
																.indexOf(p);
												}
												instance.requireLogin.remove(index);
												if (instance.requireLogin.indexOf(player) > 0)
													instance.requireLogin.remove(player);
											}
										}
										else { // Password doesn't match
											player.sendMessage(ChatColor.YELLOW + "Temp password does not match");
										}
									}
								}
								else if (instance.byteToString(passwordCheck).equals(
										instance.byteToString(hash))) { // Passwords match
									player.sendMessage(ChatColor.GREEN
											+ "Password correct, welcome back "
											+ player.getName());
	
									// Get unix time stamp
									long unixtime = System.currentTimeMillis() / 1000L;
	
									// Set up the query
									String query = "UPDATE `" + logintable
											+ "` SET lastlogintime='" + unixtime
											+ "' WHERE username='"
											+ player.getName() + "'";
									PreparedStatement statement = conn
											.prepareStatement(query);
									statement.executeUpdate();
									statement.close();
	
									// Add player to the logged in list
									instance.loggedIn.add(player);
	
									// Remove player from requireLogin list
									int index = 0;
									for (Player p : instance.requireLogin) {
										if (p.getName().equals(player.getName()))
											index = instance.requireLogin
													.indexOf(p);
									}
									instance.requireLogin.remove(index);
									if (instance.requireLogin.indexOf(player) > 0)
										instance.requireLogin.remove(player);
								} else { // Passwords do not match
									player.sendMessage(ChatColor.YELLOW
											+ "Password incorrect, try again.");
								}
							}
							else { // IP addresses do not match
								player.sendMessage(new String[] {
										ChatColor.YELLOW
												+ "Looks like you are under lockdown and your IP doesn't match.",
										ChatColor.YELLOW
												+ "If this is an error, contact an admin in IRC." });
							}
							r = true;
						}
					}
					else {
						player.sendMessage(ChatColor.YELLOW + "You are already logged in, silly!");
						r = true;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				instance.logInfo("This is an in-game only command.");
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
