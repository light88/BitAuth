package com.dechiridas.bitauth.util;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.dechiridas.bitauth.commands.Unregister;

public class PasswordFilter implements Filter {
	@Override
	public boolean isLoggable(LogRecord lr) {
		boolean r = true;
		String msg = lr.getMessage();
		
		if (msg.matches("(?i).* issued server command: /(login|changepw|resetpw|register|unregister) .*")) {
			String[] infoOut = msg.split("\\s+");
			
			if (infoOut[4].equalsIgnoreCase("/unregister")) {
				Player player = Bukkit.getServer().getPlayer(infoOut[0]);

				// Only hides arguments if the player is confirming an unregister, not if they're unregistering another user.
				if (Unregister.confirmUnregister.containsKey(player))
					r = false;
				else
					return true;
			}
			
			Logger.getLogger("BitAuth").info(infoOut[0] + " issued server command: " + infoOut[4]);
			r = false;
		}
		
		return r;
	}
}
