package com.solinia.solinia.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaNPCMerchantFactory;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCEventHandler;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.InteractionType;
import com.solinia.solinia.Models.SoliniaNPCEventHandler;

public class CommandCreateNPCEvent implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
			return false;

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (!player.isOp()) {
				player.sendMessage("This is an operator only command");
				return false;
			}
		}

		if (args.length < 4) {
			sender.sendMessage("Insufficient arguments: npcid eventtype trigger response");
			return false;
		}

		String response = "";
		int counter = 0;
		for (String entry : args) {
			counter++;
			if (counter < 3)
				continue;

			response += entry;
		}

		if (response.equals("")) {
			sender.sendMessage("Blank responses not allowed when creating an npc event");
			return false;
		}

		Integer npcid = Integer.parseInt(args[0]);
		String eventtype = args[1];
		String trigger = args[2];

		if (npcid < 1) {
			sender.sendMessage("NPC does not exist");
			return false;
		}

		try {

			ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getNPC(npcid);
			if (npc == null) {
				sender.sendMessage("NPC does not exist");
				return false;
			}

			boolean foundtype = false;
			InteractionType interactiontype = null;
			for (InteractionType type : InteractionType.values()) {
				if (!type.name().toUpperCase().equals(eventtype))
					continue;

				foundtype = true;
				interactiontype = type;
				break;
			}

			if (foundtype == false) {
				sender.sendMessage("Cannot find interaction type specified");
				return false;
			}

			if (trigger == null || trigger.equals("")) {
				sender.sendMessage("Trigger provided is empty");
				return false;
			}

			boolean exists = false;
			for (ISoliniaNPCEventHandler seek : npc.getEventHandlers()) {
				if (!seek.getInteractiontype().equals(interactiontype))
					continue;

				if (!seek.getTriggerdata().toUpperCase().equals(trigger))
					continue;

				exists = true;
			}

			if (exists) {
				sender.sendMessage("Event handler already exists");
				return false;
			}

			SoliniaNPCEventHandler eventhandler = new SoliniaNPCEventHandler();
			eventhandler.setInteractiontype(interactiontype);
			eventhandler.setTriggerdata(trigger.toUpperCase());
			eventhandler.setChatresponse(response);
			npc.addEventHandler(eventhandler);
			sender.sendMessage("New EventHandler added to NPC");
		} catch (CoreStateInitException e) {
			sender.sendMessage(e.getMessage());
		}
		return true;
	}
}