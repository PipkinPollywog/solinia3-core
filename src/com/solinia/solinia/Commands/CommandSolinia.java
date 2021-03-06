package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaZone;
import com.solinia.solinia.Utils.Utils;

public class CommandSolinia implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		sender.sendMessage("Solinia Stats:");
		
		if (args.length > 0 && 
				(
						sender.isOp() || sender instanceof ConsoleCommandSender 
				)) {
			if (args.length > 0 && args[0].equals("showtown")) {
				if (args.length > 1)
				{
					StateManager.getInstance().renderTownsOnDynmap = args[1];
					sender.sendMessage("Show town on dynmap set to: " + StateManager.getInstance().renderTownsOnDynmap);
				}
				else
				{
					StateManager.getInstance().renderTownsOnDynmap = "";
				sender.sendMessage("Show town on dynmap set to off");
				}
			}
			
			if (args.length > 0 && args[0].equals("showspawns")) {
				StateManager.getInstance().toggleShowSpawns();
				sender.sendMessage("Show spawns set to: " + StateManager.getInstance().showSpawns);
			}
			
			if (args.length > 0 && args[0].equals("zoneinfo") && sender instanceof Player) {
				sender.sendMessage("In Zone: ");
				try
				{
					for (SoliniaZone zone : StateManager.getInstance().getConfigurationManager().getZones())
					{
						if (zone.isLocationInside(((Player)sender).getLocation()))
							sender.sendMessage(zone.getName() + " size: " + zone.getCornerDistances());
					}
				} catch (CoreStateInitException e)
				{
					
				}
			}
				
			
			if (args[0].equals("patch")) {
				System.out.println("Patching");
				Utils.Patcher();
			}
		}
		
		return true;
	}
}
