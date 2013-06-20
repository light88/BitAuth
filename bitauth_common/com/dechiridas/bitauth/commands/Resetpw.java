package com.dechiridas.bitauth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.dechiridas.bitauth.BitAuth;

public class Resetpw implements CommandExecutor {
	private BitAuth plugin;
	
	public Resetpw(BitAuth instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;
		
		if (split.length == 2) {
			plugin.database.tryResetPassword(sender, split);
			r = true;
		}
		
		return r;
	}
}
