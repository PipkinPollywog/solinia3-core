package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaNPCFactory;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

public class CommandCreateNpcCopy implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player) && !(sender instanceof CommandSender))
			return false;
		
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!player.isOp())
			{
				player.sendMessage("This is an operator only command");
				return false;
			}
		}
		
		// Args
		// Level
		// NPC Name
		
		if (args.length < 2)
		{
			sender.sendMessage("Insufficient arguments: sourcenpcid npcname");
			return false;
		}
		
		int npcid = Integer.parseInt(args[0]);
		if (npcid < 1)
		{
			sender.sendMessage("Invalid npc id");
			return false;
		}
		
		try {
			if (StateManager.getInstance().getConfigurationManager().getNPC(npcid) == null)
			{
				sender.sendMessage("Cannot locate npc id: " + npcid);
				return false;
			}
			
			String name = "";
			int i = 0;
			for(String element : args)
			{
				if (i <= 0)
				{
					i++;
					continue;
				}
				
				name += element;
				i++;
			}
			
			if (name.equals(""))
			{
				sender.sendMessage("Name of NPC cannot be null");
				return false;
			}
			
			if (name.length() > 16)
			{
				sender.sendMessage("Name of NPC cannot exceed 16 characters");
				return false;
			}
			
			name = name.replace(" ", "_");
		
			ISoliniaNPC npc = SoliniaNPCFactory.CreateNPCCopy(npcid,name);
			sender.sendMessage("Created NPC Copy: " + npc.getId());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}
		return true;
	}

}
