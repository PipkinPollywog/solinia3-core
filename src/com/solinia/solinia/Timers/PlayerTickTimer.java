package com.solinia.solinia.Timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.solinia.solinia.Events.PlayerTickEvent;

public class PlayerTickTimer extends BukkitRunnable {

	@Override
	public void run() {

		Plugin plugin = Bukkit.getPluginManager().getPlugin("Solinia3Core");
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try
			{
				final PlayerTickEvent soliniaevent = new PlayerTickEvent(player.getUniqueId());
				
				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.getPluginManager().callEvent(soliniaevent);
					}

				}.runTaskLater(plugin, 10);
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
