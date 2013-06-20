package com.dechiridas.bitauth.player;

import java.util.ArrayList;
import java.util.List;

import com.dechiridas.bitauth.BitAuth;

public class PlayerManager {
	// @SuppressWarnings("unused")
	private BitAuth plugin;
	private List<BAPlayer> players;
	
	public PlayerManager(BitAuth instance) {
		this.plugin = instance;
		this.players = new ArrayList<BAPlayer>();
	}
	
	public void addPlayer(BAPlayer player) {
		boolean exists = false;
		
		synchronized(players) {
			for (BAPlayer p : players) {
				if (p.getPlayer().getName().equals(player.getPlayer().getName()))
					exists = true;
			}
		}
		
		if (exists == false)
			players.add(player);
	}
	
	public void removePlayer(BAPlayer player) {
		synchronized(players) {
			for (int i = players.size() - 1; i >= 0; i--){
				if (players.get(i).getPlayer().getName().equals(player.getPlayer().getName())) {
					players.remove(i);
				}
			}
		}
		
	}
	
	public boolean contains(BAPlayer player) {
		boolean r = false;
		
		if (players.contains(player))
			r = true;
		
		return r;
	}
	
	public List<BAPlayer> getPlayers() {
		return players;
	}
	
	public BAPlayer getBAPlayerByName(String name) {
		BAPlayer player = null;
		StringBuilder playerList = new StringBuilder();
		
		synchronized(players) {
			for (BAPlayer p : players) {
				if (p.getPlayer().getName().equals(name))
					player = p;
				playerList.append(p.getPlayer().getName()).append(", ");
			}
		}
		
		/* I'm trying to track down why this method returns null sometimes,
		 * but it's a pretty rare issue that tends to only happen once
		 * every few days, so I'm leaving this here for now.
		 */
		if (player == null) {
			plugin.log.println("###### BITAUTH DEBUG ######");
			plugin.log.println("Player name is: " + name);
			plugin.log.println("Checked player list: " + playerList);
			plugin.log.println("###### END BITAUTH DEBUG ######");
		}
		
		return player;
	}
	
	public BAPlayer getBAPlayerByIndex(int index) {
		BAPlayer player = null;
		
		synchronized(players) {
			if (players.size() - 1 >= index)
				player = players.get(index);
		}
		
		return player;
	}
}
