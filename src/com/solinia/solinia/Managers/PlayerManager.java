package com.solinia.solinia.Managers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Events.PlayerValidatedModEvent;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Factories.SoliniaPlayerFactory;
import com.solinia.solinia.Interfaces.IPlayerManager;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Models.DebuggerSettings;
import com.solinia.solinia.Models.Personality;
import com.solinia.solinia.Utils.PartyWindowUtils;
import com.solinia.solinia.Utils.PlayerUtils;
import net.md_5.bungee.api.ChatColor;

public class PlayerManager implements IPlayerManager {
	private IRepository<ISoliniaPlayer> repository;
	private ConcurrentHashMap<UUID, String> playerVersion = new ConcurrentHashMap<UUID, String>();
	private ConcurrentHashMap<UUID, Integer> playerApplyAugmentation = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Integer> playerActiveBardSong = new ConcurrentHashMap<UUID, Integer>();
	private ConcurrentHashMap<UUID, Timestamp> playerLastChangeChar = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> playerLastUnstuck = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, Timestamp> playerLastSummonSteed = new ConcurrentHashMap<UUID, Timestamp>();
	private ConcurrentHashMap<UUID, DebuggerSettings> playerDebugger = new ConcurrentHashMap<UUID, DebuggerSettings>();
	private ConcurrentHashMap<UUID, Integer> playerLastZoneId = new ConcurrentHashMap<UUID, Integer>();

	public PlayerManager(IRepository<ISoliniaPlayer> context)
	{
		this.repository = context;
	}
	
	@Override
	public void resetPlayer(Player player) throws CoreStateInitException
	{
		ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
		repository.update(SoliniaPlayerFactory.CreatePlayer(player));
		SoliniaPlayerAdapter.Adapt(player).updateDisplayName();
		SoliniaPlayerAdapter.Adapt(player).updateMaxHp();
	}
	
	@Override
	public void addPlayer(ISoliniaPlayer player)
	{
		repository.add(player);
	}
	
	private void updatePlayer(Player player, ISoliniaPlayer solPlayer) throws CoreStateInitException {
		repository.update(solPlayer);
		SoliniaPlayerAdapter.Adapt(player).updateDisplayName();
		SoliniaPlayerAdapter.Adapt(player).updateMaxHp();
	}
	
	@Override
	public ISoliniaPlayer getPlayer(Player player) {
		try
		{
			if (repository.getByKey(player.getUniqueId()) == null)
				SoliniaPlayerFactory.CreatePlayer(player);
			
			return repository.getByKey(player.getUniqueId());
		} catch (CoreStateInitException e)
		{
			return null;
		}
	}
	
	@Override
	public ISoliniaPlayer getPlayerAndDoNotCreate(UUID playerUUID) {
		return repository.getByKey(playerUUID);
	}
	
	@Override
	public List<ISoliniaPlayer> getPlayers() {
		return repository.query(p ->p.getUUID() != null);
	}
	
	@Override
	public IRepository<ISoliniaPlayer> getPlayersRepository() {
		return repository;
	}
	
	
	private List<ISoliniaPlayer> getPlayersByClass(String classname) {
		int classid = 0;
		try
		{
			for (ISoliniaClass classobj : StateManager.getInstance().getConfigurationManager().getClasses())
			{
				if (classobj.getName().toUpperCase().equals(classname.toUpperCase()))
				{
					classid = classobj.getId();
					break;
				}
			}
		} catch (CoreStateInitException e)
		{
			return new ArrayList<ISoliniaPlayer>();
		}
		
		if (classid > 0)
		{
			final int filterclass = classid;
			return repository.query(p ->p.getUUID() != null && p.getClassId() == filterclass);
		}
		
		return new ArrayList<ISoliniaPlayer>();
		
	}
	
	@Override
	public List<ISoliniaPlayer> getTopVotingPlayers() {
		List<ISoliniaPlayer> playerList = getPlayers();
		
		Collections.sort(playerList,(o1, o2) -> o1.getMonthlyVote().compareTo(o2.getMonthlyVote()));
		Collections.reverse(playerList);
		int to = 5;
		if (playerList.size() < 5)
			to = playerList.size();
		
		return playerList.subList(0, to);
	}

	@Override
	public List<ISoliniaPlayer> getTopLevelPlayers(String classname) {
		List<ISoliniaPlayer> playerList = new ArrayList<ISoliniaPlayer>();
		if (!classname.equals(""))
		{
			playerList = getPlayersByClass(classname);
		} else {
			playerList = getPlayers();
		}
		
		Collections.sort(playerList,(o1, o2) -> ((Integer)o1.getAARanks().size()).compareTo(((Integer)o2.getAARanks().size())));
		Collections.sort(playerList,(o1, o2) -> o1.getExperience().compareTo(o2.getExperience()));
		Collections.reverse(playerList);
		int to = 5;
		if (playerList.size() < 5)
			to = playerList.size();
		
		return playerList.subList(0, to);
	}

	@Override
	public boolean IsNewNameValid(String forename, String lastname)
	{
		boolean isForeNameValid = forename.chars().allMatch(Character::isLetter);
		boolean isLastNameValid = lastname.chars().allMatch(Character::isLetter);
		
		if (!isForeNameValid)
		{
			return false;
		}
		
		if (!isLastNameValid && !lastname.equals(""))
		{
			return false;
		}
		
		String newname = forename;
		if (!lastname.equals(""))
			newname = forename + "_" + lastname;
		
		final String nametest = newname;
		
		if (forename.length() < 3)
		{
			return false;
		}
		
		if (nametest.length() < 3 || nametest.length() > 15)
		{
			return false;
		}
		
		if (repository.query(p ->p.getFullName().toUpperCase().equals(nametest.toUpperCase())).size() != 0)
		{
			return false;
		}
		
		try {
			if (StateManager.getInstance().getConfigurationManager().getCharactersRepository().query(p ->p.getFullName().toUpperCase().equals(nametest.toUpperCase())).size() != 0)
			{
				return false;
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	public int getCachedPlayersCount()
	{
		return repository.query(p ->p.getUUID() != null).size();
	}

	@Override
	public void commit() {
		this.repository.commit();
	}

	@Override
	public void setApplyingAugmentation(UUID playerUuid, int itemId) {
		this.playerApplyAugmentation.put(playerUuid, itemId);
	}
	
	@Override
	public Integer getApplyingAugmentation(UUID playerUuid) {
		return this.playerApplyAugmentation.get(playerUuid);
	}

	@Override
	public Integer getPlayerActiveBardSong(UUID playerUuid) {
		return playerActiveBardSong.get(playerUuid);
	}

	@Override
	public void setPlayerActiveBardSong(UUID playerUuid, Integer spellId) {
		this.playerActiveBardSong.put(playerUuid, spellId);
	}

	@Override
	public String getPlayerNameByUUID(UUID king) {
		List<ISoliniaPlayer> players = repository.query(p ->p.getUUID().equals(king));
		for(ISoliniaPlayer player : players)
		{
			return player.getFullName();
		}
		
		return null;
	}

	@Override
	public List<ISoliniaPlayer> getCharactersByPlayerUUID(UUID playerUUID) throws CoreStateInitException {
		return StateManager.getInstance().getConfigurationManager().getCharactersByPlayerUUID(playerUUID);
	}

	@Override
	public ISoliniaPlayer createNewPlayerAlt(Plugin plugin, Player player, boolean includeChangeTimerLimit) {
		LocalDateTime datetime = LocalDateTime.now();
		if (includeChangeTimerLimit == false)
			datetime = LocalDateTime.now().minusDays(10);
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		
		ISoliniaPlayer solPlayer;
		try {
			solPlayer = SoliniaPlayerAdapter.Adapt(player);
			solPlayer.storeInventoryContents();
			solPlayer.storeArmorContents();
			
			solPlayer.removeAllEntityEffects(plugin);
			solPlayer.killAllPets();
			
			if (solPlayer.getGroup() != null) {
				StateManager.getInstance().removePlayerFromGroup(player);
			} else {
				PartyWindowUtils.UpdateGroupWindow(player.getUniqueId(), solPlayer.getGroup(), false, true);
			}
			
			StateManager.getInstance().getConfigurationManager().commitPlayerToCharacterLists(solPlayer);
			solPlayer = SoliniaPlayerFactory.CreatePlayer(player);
			player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            
            solPlayer.sendSlotsAsPacket();
            solPlayer.sendMemorisedSpellSlots();

			setPlayerLastChangeChar(player.getUniqueId(), nowtimestamp);
			if (!player.isDead())
				solPlayer.getSoliniaLivingEntity().setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			return solPlayer;
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public ISoliniaPlayer loadPlayerAlt(Plugin plugin, Player player, UUID characterUUID) {
		LocalDateTime datetime = LocalDateTime.now();
		Timestamp nowtimestamp = Timestamp.valueOf(datetime);
		
		ISoliniaPlayer solPlayer;
		try {
			solPlayer = SoliniaPlayerAdapter.Adapt(player);
			solPlayer.storeInventoryContents();
			solPlayer.storeArmorContents();
			
			// if its the same, why bother?
			if (solPlayer.getCharacterId().equals(characterUUID))
				return solPlayer;
			
			solPlayer.removeAllEntityEffects(plugin);
			solPlayer.killAllPets();
			
			if (solPlayer.getGroup() != null) {
				StateManager.getInstance().removePlayerFromGroup(player);
			} else {
				PartyWindowUtils.UpdateGroupWindow(player.getUniqueId(), solPlayer.getGroup(), true, true);
			}
			
			ISoliniaPlayer altSolPlayer = StateManager.getInstance().getConfigurationManager().getCharacterByCharacterUUID(characterUUID);
			if (altSolPlayer == null)
				return null;
			
			if (!altSolPlayer.getUUID().equals(player.getUniqueId()))
				return null;
			
			// commit current player
			StateManager.getInstance().getConfigurationManager().commitPlayerToCharacterLists(solPlayer);
			
			// Now clear the player and load the old one
			updatePlayer(player, altSolPlayer);

			player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            player.getInventory().setContents(altSolPlayer.getStoredInventoryContents());
			player.getInventory().setArmorContents(altSolPlayer.getStoredArmorContents());
            player.updateInventory();
			
            altSolPlayer.sendSlotsAsPacket();
            altSolPlayer.sendMemorisedSpellSlots();
            
			setPlayerLastChangeChar(player.getUniqueId(), nowtimestamp);
			if (!player.isDead())
				solPlayer.getSoliniaLivingEntity().setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			return altSolPlayer;
		} catch (CoreStateInitException e) {
			return null;
		}
	}

	@Override
	public Timestamp getPlayerLastChangeChar(UUID playerUUID) {
		return playerLastChangeChar.get(playerUUID);
	}
	
	@Override
	public Timestamp getPlayerLastUnstuck(UUID playerUUID) {
		return playerLastUnstuck.get(playerUUID);
	}
	
	@Override
	public void setPlayerLastUnstuck(UUID playerUUID, Timestamp timestamp) {
		this.playerLastUnstuck.put(playerUUID, timestamp);
	}

	@Override
	public void setPlayerLastChangeChar(UUID playerUUID, Timestamp timestamp) {
		this.playerLastChangeChar.put(playerUUID, timestamp);
	}
	
	@Override
	public Timestamp getPlayerLastSteed(UUID playerUUID) {
		return playerLastSummonSteed.get(playerUUID);
	}
	
	@Override
	public void setPlayerLastSteed(UUID playerUUID, Timestamp timestamp) {
		this.playerLastSummonSteed.put(playerUUID, timestamp);
	}
	
	@Override
	public List<ISoliniaPlayer> getCharacters() throws CoreStateInitException {
		return StateManager.getInstance().getConfigurationManager().getCharacters();
	}

	@Override
	public void grantPlayerAttendenceBonus() {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			ISoliniaPlayer solPlayer;
			try {
				solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer.getPendingXp() >= PlayerUtils.getMaxAAXP())
				{
					player.sendMessage("You have exceeded your maximum pending XP! Please /claimxp your additional XP before more can be gained (max: " + solPlayer.getPendingXp().longValue() + ")");
				} else {
					Double xpReward = PlayerUtils.getExperienceRewardAverageForLevel(solPlayer.getLevel()) / 6d;
					if (xpReward < 0)
					{
						xpReward = 1d;
					}
					solPlayer.addXpToPendingXp(xpReward);
				}
			} catch (Exception e) {
				
			}
		}
	}

	@Override
	public void resetPersonality(Player player) throws CoreStateInitException {
		ISoliniaPlayer solplayer = SoliniaPlayerAdapter.Adapt(player);
		solplayer.setPersonality(new Personality());
	}
	
	@Override
	public ConcurrentHashMap<UUID, DebuggerSettings> getDebugger() {
		return this.playerDebugger;
	}

	@Override
	public void toggleDebugger(UUID uniqueId, String classToDebug, String methodToDebug, String focusId) {
		if (this.playerDebugger.get(uniqueId) == null)
			this.playerDebugger.put(uniqueId, new DebuggerSettings());
		
		this.playerDebugger.get(uniqueId).toggleDebug(classToDebug,methodToDebug,focusId);
	}
	
	public String playerModVersion(Player player)
	{
		if (player == null)
			return "";
		
		String version = "";
		if (this.playerVersion.get(player.getUniqueId()) != null)
			version = this.playerVersion.get(player.getUniqueId());
		
		return version;
	}
	
	public void setPlayerVersion(UUID uuid, String version)
	{
		this.playerVersion.put(uuid, version);
	}
	
	@Override
	public void checkPlayerModVersion(Player player)
	{
		if (!playerModVersion(player).equals(StateManager.getInstance().getRequiredModVersion()))
		{
			try
			{
				ISoliniaPlayer solPlayer = SoliniaPlayerAdapter.Adapt(player);
				if (solPlayer.isModMessageEnabled())
				if (!player.isOp() && !player.hasPermission("solinia.characternewunlimited"))
				{
					System.out.println(player.getName() + " was warned with message: Missing Mod Version '" + StateManager.getInstance().getRequiredModVersion() + "' - See https://www.fallofanempire.com/docs/guides/mod/ [You have : '" + playerModVersion(player) + "']");
					player.sendMessage("Missing Mod Version '" + StateManager.getInstance().getRequiredModVersion() + "' - See https://www.fallofanempire.com/docs/guides/mod/ [You have : '" + playerModVersion(player) + "']");
					//player.kickPlayer("Missing Mod Version '" + StateManager.getInstance().getRequiredModVersion() + "' - See https://www.fallofanempire.com/docs/guides/mod/ [You have : '" + playerModVersion(player) + "']");
				} else {
					System.out.println(player.getName() + " excluded from kick - Missing Mod Version '" + StateManager.getInstance().getRequiredModVersion() + "' - See https://www.fallofanempire.com/docs/guides/mod/ [You have : '" + playerModVersion(player) + "']");
					player.sendMessage(ChatColor.GRAY + "Missing Mod Version '" + StateManager.getInstance().getRequiredModVersion() + "' - Due to your status you have been excluded from being kicked");
				}
			} catch (CoreStateInitException e)
			{
				
			}
		} else {
			onPlayerValidMod(player);
		}
	}

	@Override
	public void checkPlayerModVersions() {
		for(Player player : Bukkit.getOnlinePlayers())
		{
			try
			{
				checkPlayerModVersion(player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Triggers when a player has found to have had a valid mod version
	private void onPlayerValidMod(Player player) {
		PlayerValidatedModEvent soliniaevent = new PlayerValidatedModEvent(player);
		Bukkit.getPluginManager().callEvent(soliniaevent);
	}

	@Override
	public int getPlayerLastZone(Player player) {
		if (playerLastZoneId.get(player.getUniqueId()) == null)
			return 0;
		
		return playerLastZoneId.get(player.getUniqueId());
	}

	@Override
	public void setPlayerLastZone(Player player, int zoneId) {
		this.playerLastZoneId.put(player.getUniqueId(), zoneId);
	}
	
	
}
