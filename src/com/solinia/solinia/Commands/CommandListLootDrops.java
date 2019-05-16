package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Models.SoliniaLootDrop;
import com.solinia.solinia.Utils.Utils;

public class CommandListLootDrops implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (!sender.isOp() && !sender.hasPermission("solinia.listlootdrops"))
		{
			sender.sendMessage("You do not have permission to access this command");
			return false;
		}
		
		String filter = "";
		if (args.length > 0)
		{
			filter = args[0].toUpperCase();
		}
		
		if (args[0].equals(".criteria"))
		{
			try {
				Utils.sendFilterByCriteria(StateManager.getInstance().getConfigurationManager().getLootDrops(), sender, args,SoliniaLootDrop.class);
			return true;
			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				sender.sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		
		try
		{
			// Filter for name
			for(ISoliniaLootDrop entitylist : StateManager.getInstance().getConfigurationManager().getLootDrops())
			{
				if (!filter.equals(""))
				{
					if (entitylist.getName().toUpperCase().contains(filter))
					{
						sender.sendMessage("ID: " + entitylist.getId() + " - " + entitylist.getName());
					}
				} else {
					sender.sendMessage("ID: : " + entitylist.getId() + " - " + entitylist.getName());
				}
			}
		} catch (CoreStateInitException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}
}
