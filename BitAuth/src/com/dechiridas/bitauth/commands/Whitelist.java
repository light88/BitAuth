package com.dechiridas.bitauth.commands;

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
		
		if (sender instanceof Player) {
			Player player = (Player)sender;
			
			// Check whether the player has permission to do this or not
			if (plugin.pex.has(player, "bitauth.whitelist") || player.isOp()) {
				plugin.database.modifyWhitelist(sender, split);
			}
			r = true;
		} else {
			plugin.database.modifyWhitelist(sender, split);
			r = true;
		}
		
		return r;
	}
}
