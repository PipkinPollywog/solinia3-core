package com.solina.solinia.Interfaces;

import java.io.Serializable;
import java.util.UUID;

public interface ISoliniaPlayer extends Serializable {
	public UUID getUUID();

	public void setUUID(UUID uuid);

	public String getForename();

	public void setForename(String _forename);

	public String getLastname();

	public void setLastname(String _lastname);
}
