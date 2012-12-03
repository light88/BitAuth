package com.dechiridas.bitauth.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dechiridas.bitauth.BitAuth;

public class Logout implements CommandExecutor {
	private BitAuth plugin;
	
	public Logout(BitAuth instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;
		
		if (sender instanceof Player) {
			if (split.length == 0) {
				plugin.database.tryLogout(sender, sender.getName());
				
				r = true;
			} else if (split.length == 1) {
				if (sender.hasPermission("bitauth.logout.other")) {
					if (Bukkit.getServer().getPlayer(split[0]) != null)
						plugin.database.tryLogout(sender, split[0]);
					else
						sender.sendMessage(ChatColor.YELLOW + "Player \"" + split[0] + "\" could not be found.");

					r = true;
				} else {
					sender.sendMessage(ChatColor.RED + 
							"You do not have access /logout <username>.");
				}
			}
		} else {
			if (split.length == 1) {
				if (Bukkit.getServer().getPlayer(split[0]) != null)
					plugin.database.tryLogout(sender, split[0]);
				else
					sender.sendMessage("Player \"" + split[0] + "\" could not be found.");

				r = true;
			}
			else
				plugin.log.println("This command requires an argument when sent from the console.");
		}
		
		return r;
	}
}
