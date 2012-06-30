package com.dechiridas.bitauth.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.dechiridas.bitauth.BitAuth;

public class Baversion implements CommandExecutor {
	private BitAuth plugin;
	
	public Baversion(BitAuth instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		PluginDescriptionFile pdf = plugin.getDescription();
		
		if (sender instanceof Player) {
			((Player)sender).sendMessage(ChatColor.YELLOW + "BitAuth version: "
					+ ChatColor.GREEN + pdf.getVersion());
		} else {
			plugin.log.println("BitAuth version: " + pdf.getVersion());
		}
		
		return true;
	}
}
