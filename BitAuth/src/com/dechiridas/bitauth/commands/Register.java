package com.dechiridas.bitauth.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dechiridas.bitauth.BitAuth;

public class Register implements CommandExecutor {
	private BitAuth plugin;
	
	public Register(BitAuth instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;
		
		if (sender instanceof Player) {
			Player player = (Player)sender;
			
			if (split.length == 1) {
				if (player.getName().matches("(?i)[a-z0-9_]*") && player.getName().length() <= 16) {
					plugin.database.tryRegister(player, split[0]);
				} else {
					player.sendMessage(new String[] {ChatColor.YELLOW + "Your username can only contain a-Z, 0-9 and _,",
						ChatColor.YELLOW + "and cannot be longer than 16 characters.",
						ChatColor.YELLOW + "Please log back in with a valid username to register."
					});
				}
				r = true;
			}
		} else {
			plugin.log.println("This is an in-game only command.");
			r = true;
		}
		
		return r;
	}
	
}
