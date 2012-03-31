package bitlegend.bitauth.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import bitlegend.bitauth.BitAuth;

public class BAEntityListener implements Listener {
	private BitAuth instance;
	
	public BAEntityListener(BitAuth instance) {
		this.instance = instance;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player)entity;
			for (Player p : instance.requireLogin)
				if (p.getName().equals(player.getName()))
					event.setCancelled(true);
			for (Player p : instance.unregistered)
				if (p.getName().equals(player.getName()))
					event.setCancelled(true);
		}
	}
}
