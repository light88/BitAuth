package com.dechiridas.bitauth.player;

import java.util.ArrayList;
import java.util.List;

import com.dechiridas.bitauth.BitAuth;

public class PlayerManager {
	@SuppressWarnings("unused")
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
		
		synchronized(players) {
			for (BAPlayer p : players) {
				if (p.getPlayer().getName().equals(name))
					player = p;
			}
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
