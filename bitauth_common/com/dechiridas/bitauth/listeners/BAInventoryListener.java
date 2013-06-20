package com.dechiridas.bitauth.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import com.dechiridas.bitauth.BitAuth;
import com.dechiridas.bitauth.player.BAPlayer;
import com.dechiridas.bitauth.player.BAState;

public class BAInventoryListener implements Listener {
	private BitAuth plugin;
	
	public BAInventoryListener(BitAuth instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getPlayer() != null) {
			BAPlayer b = plugin.pman.getBAPlayerByName(event.getPlayer().getName());
			if (b.getState() != BAState.LOGGEDIN)
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player)event.getWhoClicked();
			BAPlayer b = plugin.pman.getBAPlayerByName(p.getName());
			if (b.getState() != BAState.LOGGEDIN)
				event.setCancelled(true);
		}
	}
}
