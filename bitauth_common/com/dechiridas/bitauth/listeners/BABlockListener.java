package com.dechiridas.bitauth.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.dechiridas.bitauth.BitAuth;
import com.dechiridas.bitauth.player.BAPlayer;
import com.dechiridas.bitauth.player.BAState;

public class BABlockListener implements Listener {
	private BitAuth plugin;
	
	public BABlockListener(BitAuth instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		BAPlayer ba = plugin.pman.getBAPlayerByName(player.getName());
		BAState state = ba.getState();
		
		if (state == BAState.LOGGEDOUT || state == BAState.UNREGISTERED)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		BAPlayer ba = plugin.pman.getBAPlayerByName(player.getName());
		BAState state = ba.getState();
		
		if (state == BAState.LOGGEDOUT || state == BAState.UNREGISTERED)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		BAPlayer ba = plugin.pman.getBAPlayerByName(player.getName());
		BAState state = ba.getState();
		
		if (state == BAState.LOGGEDOUT || state == BAState.UNREGISTERED)
			event.setCancelled(true);
	}
}
