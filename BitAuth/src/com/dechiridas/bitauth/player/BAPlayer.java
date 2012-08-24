package com.dechiridas.bitauth.player;

import org.bukkit.entity.Player;

import com.dechiridas.bitauth.BitAuth;

public class BAPlayer {
	private BitAuth plugin;
	private Player player;
	private BAState state;
	private TimeoutTimer tt;
	private int timeout;
	
	public BAPlayer(BitAuth instance, Player player) {
		this.plugin = instance;
		this.player = player;
		this.tt = new TimeoutTimer();
		this.timeout = Integer.parseInt(plugin.config.getString("settings.timeout"));
		this.state = BAState.LOGGEDOUT;
		
		tt.start();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public BAState getState() {
		return state;
	}
	
	public void setState(BAState state) {
		this.state = state;
	}
	
	class TimeoutTimer extends Thread {
		public TimeoutTimer() {
			
		}
		
		public void run() {
			try {
				sleep(timeout * 1000);
				
				if (state == BAState.LOGGEDOUT || state == BAState.UNREGISTERED)
					player.kickPlayer("Failed to log in within " + timeout + " seconds.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
