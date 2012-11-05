package com.dechiridas.bitauth.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.ChatColor;

import com.dechiridas.bitauth.BitAuth;

public class BACommandListener implements Listener {
	private BitAuth plugin;
	
	public BACommandListener(BitAuth instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String[] command = event.getMessage().split("\\s+");
		// Splits the raw message into an array with space delimiter, e.g. command[0] is /login, command[1] is <password>
		if (command[0].equalsIgnoreCase("/login") || command[0].equalsIgnoreCase("/chpasswd")) {
			String [] args = new String[command.length - 1];
			System.arraycopy(command, 1, args, 0, command.length - 1);
			Player player = event.getPlayer();
			plugin.log.println(ChatColor.stripColor(player.getDisplayName()) + " issued server command: " + command[0]);
			if (command[0].equalsIgnoreCase("/login"))
				plugin.database.tryLoginManual(player, args);
			else if (command[0].equalsIgnoreCase("/chpasswd"))
				plugin.database.tryChangePassword(player, args);
			event.setCancelled(true);
		}
	}
}