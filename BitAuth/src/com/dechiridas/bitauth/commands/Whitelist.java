package com.dechiridas.bitauth.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dechiridas.bitauth.BitAuth;

public class Whitelist implements CommandExecutor {
	private BitAuth plugin;
	
	public Whitelist(BitAuth instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;
		
		if (split.length == 1 && split[0].matches("(?i)(enable|disable)")) {
			if (sender.hasPermission("bitauth.whitelist.toggle")) {
				plugin.database.toggleWhitelist(sender, split);
				r = true;
			} else
				sender.sendMessage(ChatColor.RED + 
						"You do not have access /iphistory <username>.");
		} else if (split.length == 2 && split[0].matches("(?i)(add|del)")) {
			Boolean addremove = plugin.database.modifyWhitelist(split);
			
			if (sender instanceof Player) {
				sender.sendMessage(ChatColor.YELLOW + split[1]
						+ " has been "
						+ ((addremove == true) ? "added to" : "removed from")
						+ " the whitelist");
			} else {
				plugin.log.println(split[1]
						+ " has been "
						+ ((addremove == true) ? "added to" : "removed from")
						+ " the whitelist");
			}

			r = true;
		}
		
		return r;
	}
}
