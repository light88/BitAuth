package bitlegend.bitauth.timers;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.entity.Player;

import bitlegend.bitauth.BitAuth;

public class Login {
	private BitAuth instance;
	private int time = 0;
	Timer timer;
	
	public Login(Player player, int seconds, BitAuth instance) {
		this.time = seconds;
		this.instance = instance;
		timer = new Timer();
		timer.schedule(new CheckForLoginThenKick(player), seconds * 1000);
	}
	
	class CheckForLoginThenKick extends TimerTask {
		Player player;
		
		public CheckForLoginThenKick(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			if (!instance.loggedIn.contains(player))
				player.kickPlayer("You did not log in within "
						+ time + " seconds");
		}
	}
}
