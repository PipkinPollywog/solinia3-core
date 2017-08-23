package com.solinia.solinia.Models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.solinia.solinia.Adapters.SoliniaEntityAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Interfaces.ISoliniaEntity;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class SoliniaPlayer implements ISoliniaPlayer {

	private static final long serialVersionUID = 9075039437399478391L;
	private UUID uuid;
	private String forename;
	private String lastname;

	@Override
	public UUID getUUID() {
		return uuid;
	}
	
	@Override
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getForename() {
		return forename;
	}

	@Override
	public void setForename(String forename) {
		this.forename = forename;
		updateDisplayName();
	}

	@Override
	public String getLastname() {
		return lastname;
	}

	@Override
	public void setLastname(String lastname) {
		this.lastname = lastname;
		updateDisplayName();
	}
	
	@Override
	public void updateDisplayName()
	{
		Player player = Bukkit.getPlayer(uuid);
		if (player != null)
		{
			player.setDisplayName(getFullName());
			player.setPlayerListName(getFullName());
		}
	}
	
	@Override
	public String getFullName()
	{
		String displayName = forename;
		if (lastname != null && !lastname.equals(""))
			displayName = forename + "_" + lastname;
		
		return displayName;
	}
	
	@Override
	public ISoliniaEntity getEntity() throws CoreStateInitException
	{
		return SoliniaEntityAdapter.Adapt(Bukkit.getPlayer(uuid));
	}
}
