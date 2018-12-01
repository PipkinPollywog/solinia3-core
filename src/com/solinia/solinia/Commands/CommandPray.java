package com.solinia.solinia.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Factories.SoliniaNPCMerchantFactory;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

public class CommandPray implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This is a player only command");
			return false;
		}
		
		if (args.length == 0)
        {
			sender.sendMessage("You must say something for your prayer");
			return false;
        }
		
		Player player = (Player)sender;
        try
        {
        	ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
        	if (
        			!solPlayer.getClassObj().getName().toUpperCase().equals("CLERIC") &&
        			!solPlayer.getClassObj().getName().toUpperCase().equals("PALADIN") &&
        			!solPlayer.getClassObj().getName().toUpperCase().equals("SHADOWKNIGHT")        			
        			)
        	{
        		player.sendMessage("* The Gods are silent (only a cleric, paladin or shadowknight may act as a conduit for the Gods)");
        		return false;
        	}
        	
        	String prayerText = "";
        	for (String entry : args) {
        		prayerText = prayerText + entry + " ";
			}
        	prayerText.trim();
        	
        	String message = "[" + solPlayer.getClassObj().getName() + "/" + player.getName() + "] " + player.getDisplayName() + " is praying to the Gods! " + prayerText;
        		
        	StateManager.getInstance().getChannelManager().sendToDiscordMC(null, StateManager.getInstance().getChannelManager().getDiscordContentTeamChannelId(), message);
        		
        	sender.sendMessage("* The Gods have heard your prayer");
        } catch (Exception e)
        {
        	e.printStackTrace();
        	sender.sendMessage(e.getMessage());
        }

		return true;
	}
}