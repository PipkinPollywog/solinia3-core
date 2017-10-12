package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidFactionSettingException;
import com.solinia.solinia.Exceptions.InvalidSpawnGroupSettingException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Managers.StateManager;

import net.md_5.bungee.api.ChatColor;

public class SoliniaFaction implements ISoliniaFaction {
	private int id;
	private String name;
	private int base;
	private List<FactionStandingEntry> factionEntries = new ArrayList<FactionStandingEntry>();
	private String allyGrantsTitle = "";
	private String scowlsGrantsTitle = "";
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int getBase() {
		return base;
	}
	
	@Override
	public void setBase(int base) {
		this.base = base;
	}
	
	@Override
	public void sendFactionSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "SpawnGroup Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- base: " + ChatColor.GOLD + getBase() + ChatColor.RESET);
		sender.sendMessage("- allygrantstitle: " + ChatColor.GOLD + getAllyGrantsTitle() + ChatColor.RESET);
		sender.sendMessage("- scowlsgrantstitle: " + ChatColor.GOLD + getScowlsGrantsTitle() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- factionentry:");
		for(FactionStandingEntry entry : this.getFactionEntries())
		{
			sender.sendMessage("- " + entry.getFaction().getName() + "[" + entry.getFactionId() + "]: " + entry.getValue());
		}
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidFactionSettingException, NumberFormatException, CoreStateInitException, java.io.IOException {

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidFactionSettingException("Name is empty");

			if (value.length() > 25)
				throw new InvalidFactionSettingException("Name is longer than 25 characters");
			setName(value);
			break;
		case "base":
			if (Integer.parseInt(value) < -1500 || Integer.parseInt(value) > 1500)
				throw new InvalidFactionSettingException("Bounds are -1500 to 1500");
			setBase(Integer.parseInt(value));
			break;
		case "allygrantstitle":
			setAllyGrantsTitle(value);
			break;
		case "scowlsgrantstitle":
			setScowlsGrantsTitle(value);
			break;
		case "factionentry":
			String[] data = value.split(" ");
			if (data.length < 2)
				throw new InvalidFactionSettingException("Missing factionid and base value to set factionstanding (ie -1500 > 1500)");
			int factionId = Integer.parseInt(data[0]);
			ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager().getFaction(factionId);
			if (faction == null)
				throw new InvalidFactionSettingException("Faction doesnt exist");

			if (Integer.parseInt(data[1]) < -1500 || Integer.parseInt(data[1]) > 1500)
				throw new InvalidFactionSettingException("Bounds are -1500 to 1500");
			setFactionEntry(faction.getId(), Integer.parseInt(data[1]));
			break;
		default:
			throw new InvalidFactionSettingException(
					"Invalid SpawnGroup setting. Valid Options are: name, base, factionentry");
		}
	}
	
	@Override
	public List<FactionStandingEntry> getFactionEntries() {
		return factionEntries;
	}

	@Override
	public void setFactionEntries(List<FactionStandingEntry> factionEntries) {
		this.factionEntries = factionEntries;
	}
	
	@Override
	public FactionStandingEntry getFactionEntry(int factionId) {
		for(FactionStandingEntry entry : getFactionEntries())
		{
			if (entry.getFactionId() == factionId)
				return entry;
		}
		
		return null;
	}
	
	@Override
	public void setFactionEntry(int factionId, int value) {
		FactionStandingEntry factionEntry = getFactionEntry(factionId);
		if (factionEntry == null)
			factionEntry = createFactionStandingEntry(factionId);
		factionEntry.setValue(value);
	}
	
	@Override
	public FactionStandingEntry createFactionStandingEntry(int factionId)
	{
		FactionStandingEntry entry = new FactionStandingEntry();
		entry.setFactionId(factionId);
		entry.setValue(0);
		getFactionEntries().add(entry);
		return getFactionEntry(factionId);
	}

	@Override
	public String getAllyGrantsTitle() {
		return allyGrantsTitle;
	}

	@Override
	public void setAllyGrantsTitle(String allyGrantsTitle) {
		this.allyGrantsTitle = allyGrantsTitle;
	}

	@Override
	public String getScowlsGrantsTitle() {
		return scowlsGrantsTitle;
	}

	@Override
	public void setScowlsGrantsTitle(String scowlsGrantsTitle) {
		this.scowlsGrantsTitle = scowlsGrantsTitle;
	}
}
