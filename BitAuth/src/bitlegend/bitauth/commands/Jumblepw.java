package bitlegend.bitauth.commands;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import bitlegend.bitauth.BitAuth;
import bitlegend.bitauth.HashManager;

public class Jumblepw implements CommandExecutor {

	private BitAuth instance;

	// Database info
	private String user = "";
	private String pass = "";
	private String url = "";
	private String logintable = "";

	public Jumblepw(BitAuth instance) {
		this.instance = instance;
		user = instance.config.readString("DB_User");
		pass = instance.config.readString("DB_Pass");
		url = "jdbc:mysql://" + instance.config.readString("DB_Host") + "/"
				+ instance.config.readString("DB_Name");
		logintable = instance.config.readString("DB_Table_login");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;

		if (split.length == 1) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				if (instance.pex.has(player, "bitauth.jumble") || player.isOp()) {
					String username = split[0];
					
					try {
						Connection conn = DriverManager.getConnection(url, user, pass);
						Statement select = conn.createStatement();
						ResultSet result = select.executeQuery(
								"SELECT * FROM `" + logintable + "` WHERE `username`='" + username + "'");
						
						boolean playerFound = false;
						if (result.next()) { // Results found
							do {
								String name = result.getString(1);
								if (name.equals(username)) { // Player data found
									playerFound = true;
								}
							} while (result.next());
						}
						
						if (playerFound == true) {
							byte[] rpasswd = HashManager.GenerateHash(
									HashManager.RandomString(), HashManager.GenerateSalt());
							Statement update = conn.createStatement();
							String query = "UPDATE `" + logintable
									+ "` SET `password`='" + rpasswd
									+ "' WHERE `username`='" + username + "'";
							update.executeUpdate(query);
							update.close();
							
							player.sendMessage(ChatColor.YELLOW +
									"Password for " + username + " has been replaced");
						}
						else
							player.sendMessage(ChatColor.YELLOW + 
									"Player " + username + " not found");
						result.close();
						select.close();
						conn.close();
						
						r = true;
					} catch (SQLException se) {
						se.printStackTrace();
					} catch (NoSuchAlgorithmException nsae) {
						nsae.printStackTrace();
					} catch (UnsupportedEncodingException uee) {
						uee.printStackTrace();
					}
				}
				else {
					player.sendMessage(ChatColor.YELLOW +
							"You do not have access to this feature.");
					r = true;
				}
			}

			if (sender instanceof ConsoleCommandSender) {
				String username = split[0];
				
				try {
					Connection conn = DriverManager.getConnection(url, user, pass);
					Statement select = conn.createStatement();
					ResultSet result = select.executeQuery(
							"SELECT * FROM `" + logintable + "` WHERE `username`='" + username + "'");
					
					boolean playerFound = false;
					if (result.next()) { // Results found
						do {
							String name = result.getString(1);
							if (name.equals(username)) { // Player data found
								playerFound = true;
							}
						} while (result.next());
					}
					
					if (playerFound == true) {
						byte[] rpasswd = HashManager.GenerateHash(
								HashManager.RandomString(), HashManager.GenerateSalt());
						Statement update = conn.createStatement();
						String query = "UPDATE `" + logintable
								+ "` SET `password`='" + rpasswd
								+ "' WHERE `username`='" + username + "'";
						update.executeUpdate(query);
						update.close();
						
						instance.logInfo(
								"Password for " + username + " has been replaced");
						for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							if (p.getName().equals(username)) {
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "logout " + username);
								p.kickPlayer("Goodbye.");
								instance.logInfo("Player " + username + " has been kicked from the server");
							}
						}
					}
					else
						instance.logInfo("Player " + username + " not found");
					result.close();
					select.close();
					conn.close();
					
					r = true;
				} catch (SQLException se) {
					se.printStackTrace();
				} catch (NoSuchAlgorithmException nsae) {
					nsae.printStackTrace();
				} catch (UnsupportedEncodingException uee) {
					uee.printStackTrace();
				}
			}
		}

		return r;
	}
}
