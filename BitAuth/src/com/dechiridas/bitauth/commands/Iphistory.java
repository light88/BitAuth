package com.dechiridas.bitauth.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.dechiridas.bitauth.BitAuth;

public class Iphistory implements CommandExecutor {
	private BitAuth plugin;
	
	public Iphistory(BitAuth instance) {
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;
		
		if (sender instanceof Player) {
			Player player = (Player)sender;
			
			if (plugin.pex.has(player, "bitauth.iphistory.self") 
					|| plugin.pex.has(player, "bitauth.iphistory.other") 
					|| player.isOp()) {
				String target = "";
				
				if (split.length == 1)
					if (plugin.pex.has(player, "bitauth.iphistory.other") || player.isOp()) {
						target = split[0];
				}
				
				if (split.length == 0) {
					if (plugin.pex.has(player, "bitauth.iphistory.self") || player.isOp()) {
						target = player.getName();
					}
				}
				
				if (!target.equals("")) {
					if (plugin.database.playerExists(target)) {
						String[] history = plugin.database.getPlayerIPHistory(
								plugin.getServer().getPlayer(target));
						player.sendMessage(new String[] {
								ChatColor.YELLOW + "The most recent IP addresses for " + ChatColor.GREEN + target + ChatColor.YELLOW + ":",
								(history[0] != null ? ChatColor.GREEN + history[0] : "") +
								(history[1] != null ? ChatColor.YELLOW + ", " + ChatColor.GREEN + history[1] : "") +
								(history[2] != null ? ChatColor.YELLOW + ", " + ChatColor.GREEN + history[2] : "") +
								(history[3] != null ? ChatColor.YELLOW + ", " + ChatColor.GREEN + history[3] : "") +
								(history[4] != null ? ChatColor.YELLOW + ", " + ChatColor.GREEN + history[4] : "")
						});
					} else {
						player.sendMessage(ChatColor.YELLOW + 
							"No player by the name of " + ChatColor.GREEN + target + ChatColor.YELLOW + " found.");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + 
							"You do not have access to this feature.");
				}
			}
			
			r = true;
		} else if (sender instanceof ConsoleCommandSender) {
			String target = "";
			if (split.length == 1) {
				target = split[0];
				if (plugin.database.playerExists(target)) {
					String[] history = plugin.database.getPlayerIPHistory(target);
					String output = "The most recent IP Addresses for " + target + ": ";
					
					for (int i = 0; i < history.length; i++)
						if ((i == history.length - 1 || i == 0) && history[i] != null)
							output += history[i];
						else if (i != history.length - 1 && i != 0 && history[i] != null)
							output += history[i] + ", ";
					output = output.trim();
					plugin.log.println(output);
				} else {
					plugin.log.println("No player by the name of " + target + " found.");
				}
			} else {
				plugin.log.println("You must include a username to query.");
			}
			
			r = true;
		}
		
		return r;
	}

}
