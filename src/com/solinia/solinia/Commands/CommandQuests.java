package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.PlayerQuest;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandQuests implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		try
		{
			Player player = (Player)sender;
			ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
			player.sendMessage("Active Quests:");
			for(PlayerQuest playerQuest : solplayer.getPlayerQuests())
			{
				player.sendMessage(playerQuest.getQuest().getName() + " Complete: " + playerQuest.isComplete());
			}
		} catch (CoreStateInitException e)
		{
			
		}
		
		return true;
	}

}