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
			boolean isLoggedIn = false;
			for (Player p : instance.loggedIn)
				// Player found in logged in list
				if (p.getName().equals(player.getName()))
					isLoggedIn = true;
			if (isLoggedIn == false)
				event.setCancelled(true);
		}
	}
}
