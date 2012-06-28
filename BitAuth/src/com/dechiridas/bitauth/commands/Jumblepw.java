package com.dechiridas.bitauth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.dechiridas.bitauth.BitAuth;

public class Jumblepw implements CommandExecutor {
	private BitAuth plugin;
	
	public Jumblepw(BitAuth instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		boolean r = false;
		
		plugin.database.tryJumblePassword(sender, split);
		
		return r;
	}
}
