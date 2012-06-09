package bitlegend.bitauth.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import bitlegend.bitauth.BitAuth;

public class BABlockListener implements Listener {
	private BitAuth instance;
	
	public BABlockListener(BitAuth instance) {
		this.instance = instance;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		for (Player p : instance.unregistered) {
			if (p.getDisplayName().equals(event.getPlayer().getDisplayName())) {
				event.setCancelled(true);
			}
		}
		for (Player p : instance.requireLogin) {
			if (p.getDisplayName().equals(event.getPlayer().getDisplayName())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		for (Player p : instance.unregistered) {
			if (p.getDisplayName().equals(event.getPlayer().getDisplayName())) {
				event.setCancelled(true);
			}
		}
		for (Player p : instance.requireLogin) {
			if (p.getDisplayName().equals(event.getPlayer().getDisplayName())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		for (Player p : instance.unregistered) {
			if (p.getDisplayName().equals(event.getPlayer().getDisplayName())) {
				event.setCancelled(true);
			}
		}
		for (Player p : instance.requireLogin) {
			if (p.getDisplayName().equals(event.getPlayer().getDisplayName())) {
				event.setCancelled(true);
			}
		}
	}
}
