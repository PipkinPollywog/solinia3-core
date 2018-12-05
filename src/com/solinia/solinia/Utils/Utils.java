package com.solinia.solinia.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.example.Vector3D;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rit.sucy.player.TargetHelper;
import com.solinia.solinia.Adapters.ItemStackAdapter;
import com.solinia.solinia.Adapters.SoliniaItemAdapter;
import com.solinia.solinia.Adapters.SoliniaLivingEntityAdapter;
import com.solinia.solinia.Adapters.SoliniaPlayerAdapter;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Exceptions.SoliniaCraftCreationException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Factories.SoliniaCraftFactory;
import com.solinia.solinia.Factories.SoliniaItemFactory;
import com.solinia.solinia.Factories.SoliniaNPCMerchantFactory;
import com.solinia.solinia.Interfaces.ISoliniaAAAbility;
import com.solinia.solinia.Interfaces.ISoliniaAARank;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLivingEntity;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchant;
import com.solinia.solinia.Interfaces.ISoliniaPatch;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;
import com.solinia.solinia.Interfaces.ISoliniaRace;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.ActiveSpellEffect;
import com.solinia.solinia.Models.AugmentationSlotType;
import com.solinia.solinia.Models.DisguisePackage;
import com.solinia.solinia.Models.FactionStandingType;
import com.solinia.solinia.Models.NumHit;
import com.solinia.solinia.Models.SkillReward;
import com.solinia.solinia.Models.SkillType;
import com.solinia.solinia.Models.SoliniaAARankEffect;
import com.solinia.solinia.Models.SoliniaActiveSpell;
import com.solinia.solinia.Models.SoliniaCraft;
import com.solinia.solinia.Models.SoliniaEntitySpells;
import com.solinia.solinia.Models.SoliniaSpell;
import com.solinia.solinia.Models.SoliniaSpellClass;
import com.solinia.solinia.Models.SpellEffectIndex;
import com.solinia.solinia.Models.SpellEffectType;
import com.solinia.solinia.Models.SpellResistType;
import com.solinia.solinia.Models.WorldWidePerk;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class Utils {
	public static final int MAX_ENTITY_AGGRORANGE = 100;

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public static double calculateExpLoss(ISoliniaPlayer player) {
		double loss = 0;
		loss = (double) (player.getLevel() * (player.getLevel() / 18.0) * 12000);
		return (double) loss;
	}

	public static void CancelEvent(Cancellable event) {
		// System.out.println("Cancel event found for Event Type: " +
		// event.getClassObj().getName());
		event.setCancelled(true);
	}

	public static void BroadcastPlayers(String message) {
		for (World world : Bukkit.getWorlds()) {
			for (Player player : world.getPlayers()) {
				player.sendMessage(ChatColor.YELLOW + "[Announcement] " + message + ChatColor.RESET);
			}
		}
	}

	public static List<WorldWidePerk> getActiveWorldWidePerks() {
		List<WorldWidePerk> perks = new ArrayList<WorldWidePerk>();

		LocalDateTime datetime = LocalDateTime.now();
		Timestamp currenttimestamp = Timestamp.valueOf(datetime);

		for (WorldWidePerk entity : StateManager.getInstance().getWorldWidePerks()) {
			// System.out.println("Comparing Perk [" + entity.getId() + "/" +
			// entity.getContributor() + "] time: " +
			// entity.getEndtimeAsTimestamp().toLocaleString() + " against now " +
			// now.toLocaleString());
			if (entity.getEndtimeAsTimestamp().after(currenttimestamp)) {
				perks.add(entity);
			}
		}
		return perks;
	}

	public static void RepairAllowedClasses(ISoliniaSpell spell) {

		List<SoliniaSpellClass> allowedClasses = new ArrayList<SoliniaSpellClass>();

		if (spell.getClasses1() != null && spell.getClasses1() != 254 && spell.getClasses1() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("WARRIOR");
			spellClass.setMinlevel(spell.getClasses1());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses2() != null && spell.getClasses2() != 254 && spell.getClasses2() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("CLERIC");
			spellClass.setMinlevel(spell.getClasses2());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses3() != null && spell.getClasses3() != 254 && spell.getClasses3() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("PALADIN");
			spellClass.setMinlevel(spell.getClasses3());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses4() != null && spell.getClasses4() != 254 && spell.getClasses4() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("RANGER");
			spellClass.setMinlevel(spell.getClasses4());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses5() != null && spell.getClasses5() != 254 && spell.getClasses5() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("SHADOWKNIGHT");
			spellClass.setMinlevel(spell.getClasses5());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses6() != null && spell.getClasses6() != 254 && spell.getClasses6() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("DRUID");
			spellClass.setMinlevel(spell.getClasses6());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses7() != null && spell.getClasses7() != 254 && spell.getClasses7() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("MONK");
			spellClass.setMinlevel(spell.getClasses7());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses8() != null && spell.getClasses8() != 254 && spell.getClasses8() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("BARD");
			spellClass.setMinlevel(spell.getClasses8());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses9() != null && spell.getClasses9() != 254 && spell.getClasses9() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("ROGUE");
			spellClass.setMinlevel(spell.getClasses9());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses10() != null && spell.getClasses10() != 254 && spell.getClasses10() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("SHAMAN");
			spellClass.setMinlevel(spell.getClasses10());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses11() != null && spell.getClasses11() != 254 && spell.getClasses11() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("NECROMANCER");
			spellClass.setMinlevel(spell.getClasses11());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses12() != null && spell.getClasses12() != 254 && spell.getClasses12() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("WIZARD");
			spellClass.setMinlevel(spell.getClasses12());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses13() != null && spell.getClasses13() != 254 && spell.getClasses13() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("MAGICIAN");
			spellClass.setMinlevel(spell.getClasses13());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses14() != null && spell.getClasses14() != 254 && spell.getClasses14() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("ENCHANTER");
			spellClass.setMinlevel(spell.getClasses14());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses15() != null && spell.getClasses15() != 254 && spell.getClasses15() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("BEASTLORD");
			spellClass.setMinlevel(spell.getClasses15());
			allowedClasses.add(spellClass);
		}

		if (spell.getClasses16() != null && spell.getClasses16() != 254 && spell.getClasses16() != 255) {
			SoliniaSpellClass spellClass = new SoliniaSpellClass();
			spellClass.setClassname("BERSERKER");
			spellClass.setMinlevel(spell.getClasses16());
			allowedClasses.add(spellClass);
		}
		try {
			StateManager.getInstance().getConfigurationManager().getSpell(spell.getId())
					.setAllowedClasses(allowedClasses);
			System.out.println("Fixed spell " + spell.getName() + " with classe count: " + allowedClasses.size());
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int GetWorldPerkDropCountModifier() {
		int dropcount = 1;

		for (WorldWidePerk perk : getActiveWorldWidePerks()) {
			if (perk.getPerkname().equals("DROP100")) {
				dropcount += 1;
			}
			if (perk.getPerkname().equals("DROP1000")) {
				dropcount += 10;
			}
		}

		return dropcount;
	}

	public static double getWorldPerkXPModifier() {
		double xppercent = 100;
		for (WorldWidePerk perk : getActiveWorldWidePerks()) {
			if (perk.getPerkname().equals("XPBONUS50")) {
				xppercent += 50;
			}

			if (perk.getPerkname().equals("XPBONUS100")) {
				xppercent += 100;
			}

			if (perk.getPerkname().equals("XPBONUS150")) {
				xppercent += 150;
			}

			if (perk.getPerkname().equals("XPBONUS200")) {
				xppercent += 200;
			}

			if (perk.getPerkname().equals("XPBONUS1000")) {
				xppercent += 1000;
			}
		}

		return xppercent;
	}

	public static long compareTwoTimeStamps(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime) {
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();

		long diff = milliseconds2 - milliseconds1;
		long diffSeconds = diff / 1000;
		return diffSeconds;
	}

	public static void broadcastPerks() {
		for (WorldWidePerk perk : getActiveWorldWidePerks()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage("* You are currently receiving " + perk.getPerkname() + " from contributor "
						+ perk.getContributor());
			}
		}
	}

	public static String getUUIDFromPlayerName(String playerName) throws IOException {
		URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
		InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
		String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();
		return uuid;
	}

	public static String getTextureFromName(String name) {
		String texture = "";
		try {
			String uuid = getUUIDFromPlayerName(name);

			URL url_1 = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
			JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties")
					.getAsJsonArray().get(0).getAsJsonObject();
			texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();
		} catch (IOException e) {
			System.err.println("Could not get skin data from session servers!");
			e.printStackTrace();
			return null;
		}

		return texture;
	}

	public static int getPlayerTotalCountOfItemId(Player player, int itemid) {
		int total = 0;

		for (int i = 0; i < 36; i++) {
			ItemStack itemstack = player.getInventory().getItem(i);
			if (itemstack == null)
				continue;

			if (itemstack.getType().equals(Material.AIR))
				continue;

			if (!Utils.IsSoliniaItem(itemstack))
				continue;

			int tmpitemid = 0;
			
			try {
				tmpitemid = SoliniaItemAdapter.Adapt(itemstack).getId();
			} catch (SoliniaItemException e) {
				continue;
			} catch (CoreStateInitException e) {
				continue;
			}

			if (tmpitemid == itemid) {
				total = total + itemstack.getAmount();
			}
		}

		return total;
	}

	public static int removeItemsFromInventory(Player player, int itemid, int count) {
		int removed = 0;
		int remaining = count;
		for (int i = 0; i < 36; i++) {
			ItemStack itemstack = player.getInventory().getItem(i);
			if (itemstack == null)
				continue;

			if (itemstack.getType().equals(Material.AIR))
				continue;

			if (!Utils.IsSoliniaItem(itemstack))
				continue;

			// covers cases of special tmp ids
			int tmpitemid = itemstack.getEnchantmentLevel(Enchantment.DURABILITY);
			if (tmpitemid == 999)
				continue;

			try {
				tmpitemid = SoliniaItemAdapter.Adapt(itemstack).getId();
			} catch (SoliniaItemException e) {
				continue;
			} catch (CoreStateInitException e) {
				continue;
			}

			if (remaining < 1)
				break;

			if (tmpitemid != itemid)
				continue;

			if (remaining <= itemstack.getAmount()) {
				removed = removed + remaining;
				itemstack.setAmount(itemstack.getAmount() - remaining);
				remaining = 0;
				break;
			}

			if (remaining > 64) {
				if (itemstack.getAmount() < 64) {
					removed = removed + itemstack.getAmount();
					remaining = remaining - itemstack.getAmount();
					itemstack.setAmount(0);
				} else {
					removed = removed + 64;
					remaining = remaining - 64;
					itemstack.setAmount(itemstack.getAmount() - 64);
				}
			} else {
				removed = removed + itemstack.getAmount();
				remaining = remaining - itemstack.getAmount();
				itemstack.setAmount(0);
			}
		}

		player.updateInventory();
		return removed;
	}

	public static SpellTargetType getSpellTargetType(int spellTargetId) {
		switch (spellTargetId) {
		case 1:
			return SpellTargetType.TargetOptional;
		case 2:
			return SpellTargetType.AEClientV1;
		case 3:
			return SpellTargetType.GroupTeleport;
		case 4:
			return SpellTargetType.AECaster;
		case 5:
			return SpellTargetType.Target;
		case 6:
			return SpellTargetType.Self;
		case 8:
			return SpellTargetType.AETarget;
		case 9:
			return SpellTargetType.Animal;
		case 10:
			return SpellTargetType.Undead;
		case 11:
			return SpellTargetType.Summoned;
		case 13:
			return SpellTargetType.Tap;
		case 14:
			return SpellTargetType.Pet;
		case 15:
			return SpellTargetType.Corpse;
		case 16:
			return SpellTargetType.Plant;
		case 17:
			return SpellTargetType.Giant;
		case 18:
			return SpellTargetType.Dragon;
		case 20:
			return SpellTargetType.TargetAETap;
		case 24:
			return SpellTargetType.UndeadAE;
		case 25:
			return SpellTargetType.SummonedAE;
		case 32:
			return SpellTargetType.AETargetHateList;
		case 33:
			return SpellTargetType.HateList;
		case 36:
			return SpellTargetType.AreaClientOnly;
		case 37:
			return SpellTargetType.AreaNPCOnly;
		case 38:
			return SpellTargetType.SummonedPet;
		case 39:
			return SpellTargetType.GroupNoPets;
		case 40:
			return SpellTargetType.AEBard;
		case 41:
			return SpellTargetType.Group;
		case 42:
			return SpellTargetType.Directional;
		case 43:
			return SpellTargetType.GroupClientAndPet;
		case 44:
			return SpellTargetType.Beam;
		case 45:
			return SpellTargetType.Ring;
		case 46:
			return SpellTargetType.TargetsTarget;
		case 47:
			return SpellTargetType.PetMaster;
		case 50:
			return SpellTargetType.TargetAENoPlayersPets;
		default:
			return SpellTargetType.Error;
		}

	}

	public static int getLevelFromExperience(Double experience) {
		Double classmodifier = 10d;
		Double racemodifier = 100d;
		Double levelfactor = 1d;

		Double level = experience / levelfactor / racemodifier / classmodifier;
		level = java.lang.Math.pow(level, 0.25) + 1;
		return (int) java.lang.Math.floor(level);
	}

	public static boolean RandomChance(int minmum) {
		Random r = new Random();
		int randomInt = r.nextInt(100) + 1;
		if (randomInt > minmum) {
			return true;
		}

		return false;
	}

	public static boolean RandomRoll(int max) {
		Random r = new Random();
		int random = Utils.RandomBetween(0, 100);
		if (random < max) {
			return true;
		}

		return false;
	}

	public static int RandomBetween(int minnumber, int maxnumber) {
		Random r = new Random();
		return r.nextInt((maxnumber - minnumber) + 1) + minnumber;
	}

	public static SkillReward getSkillForMaterial(String materialstring) {
		SkillReward reward = null;

		int xp = 0;
		String skill = "";

		switch (materialstring) {
		case "WOODEN_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "STONE_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "IRON_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "GOLDEN_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "DIAMOND_SWORD":
			xp = 1;
			skill = "SLASHING";
			break;
		case "WOODEN_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "STONE_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "IRON_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "GOLDEN_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "DIAMOND_AXE":
			xp = 1;
			skill = "SLASHING";
			break;
		case "AIR":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "STICK":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "WOODEN_SHOVEL":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "STONE_SHOVEL":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "IRON_SHOVEL":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "GOLDEN_SHOVEL":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "DIAMOND_SHOVEL":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "WOODEN_HOE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "STONE_HOE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "IRON_HOE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "GOLDEN_HOE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "DIAMOND_HOE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "WOODEN_PICKAXE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "STONE_PICKAXE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "IRON_PICKAXE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "GOLDEN_PICKAXE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "DIAMOND_PICKAXE":
			xp = 1;
			skill = "CRUSHING";
			break;
		case "BOW":
			xp = 1;
			skill = "ARCHERY";
			break;
		default:
			// Fall back on crushing
			xp = 1;
			skill = "CRUSHING";
			break;
		}

		if (xp > 0 && !skill.equals("")) {
			reward = new SkillReward(skill, xp);
		}

		return reward;
	}

	public static int getMaxLevel() {
		return 65;
	}

	public static Double getExperienceRewardAverageForLevel(int level) {
		Double experience = (Math.pow(level, 2) * 10) * getMaxLevel() - 1;
		experience = experience / 2;
		if (experience < 1) {
			experience = 1d;
		}
		
		if (level < 10)
		{
			return experience * 6d;
		}
		
		if (level < 20)
		{
			return experience * 5d;
		}

		
		if (level < 30)
		{
			return experience * 4d;
		}

		
		if (level < 40)
		{
			return experience * 3d;
		}

		
		if (level < 50)
		{
			return experience * 2d;
		}

		
		return experience;
	}

	public static Double getMaxAAXP() {
		// TODO Auto-generated method stub
		return 578360000d;
	}

	public static double getExperienceRequirementForLevel(int level) {
		Double classmodifier = 10d;
		Double racemodifier = 100d;
		Double levelfactor = 1d;

		Double experiencerequired = (java.lang.Math.pow(level - 1, 4)) * classmodifier * racemodifier * levelfactor;
		return experiencerequired;
	}

	// TODO - Move this to a value setting on the SoliniaClass object
	public static double getClassXPModifier(ISoliniaClass soliniaClass) {
		double percentagemodifier = 100;

		if (soliniaClass == null)
			return percentagemodifier;

		if (soliniaClass.getName().equals("CLERIC") || soliniaClass.getName().equals("DRUID")
				|| soliniaClass.getName().equals("SHAMAN"))
			percentagemodifier = 120;

		return percentagemodifier;
	}

	public static int getSkillCap(String skillname, ISoliniaClass profession, int level, String specialisation) {
		skillname = skillname.toUpperCase();
		
		if (!Utils.isValidSkill(skillname.toUpperCase()))
			return 0;

		// If the skill being queried happens to be a race name, the cap for
		// language is always 100
		try {
			List<ISoliniaRace> races = StateManager.getInstance().getConfigurationManager().getRaces();
			for (ISoliniaRace race : races) {
				if (race.getName().toUpperCase().equals(skillname.toUpperCase())) {
					return 100;
				}
			}

		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO - Move all these skill cap bonuses to the race configuration
		// classes

		if (skillname.toUpperCase().equals("SLASHING")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();
					return cap;
				}
		}

		if (skillname.toUpperCase().equals("BACKSTAB")) {
			if (profession != null)
				if (profession.getName().toUpperCase().equals("ROGUE")) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();
					return cap;
				}
		}

		if (skillname.toUpperCase().equals("TAUNT")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("PALADIN"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();
					return cap;
				}
		}
		
		if (skillname.toUpperCase().equals("BINDWOUND")) {
			if (profession != null)
			{
				if ((profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						))
				{
					int cap = (int) ((5 * level) + 5);
					if (cap > 210)
						return 210;
					return cap;
				}
				
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("BARD")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("SHAMAN")
						|| profession.getName().toUpperCase().equals("CLERIC")
						|| profession.getName().toUpperCase().equals("DRUID")
						))
				{
					int cap = (int) ((5 * level) + 5);
					if (cap > 200)
						return 200;
					return cap;
				}
				
				if ((profession.getName().toUpperCase().equals("ENCHANTER")
						|| profession.getName().toUpperCase().equals("MAGICIAN")
						|| profession.getName().toUpperCase().equals("NECROMANCER")
						|| profession.getName().toUpperCase().equals("WIZARD")
						))
				{
					int cap = (int) ((5 * level) + 5);
					if (cap > 100)
						return 100;
					return cap;
				}
			}
			
			
		}

		if (skillname.toUpperCase().equals("CRUSHING")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("DODGE")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("RIPOSTE")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("DOUBLEATTACK")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("ARCHERY")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("HUNTER"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("MEDITATION")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("DRUID")
						|| profession.getName().toUpperCase().equals("WIZARD")
						|| profession.getName().toUpperCase().equals("MAGICIAN")
						|| profession.getName().toUpperCase().equals("NECROMANCER")
						|| profession.getName().toUpperCase().equals("ENCHANTER")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("ARCANIST")
						|| profession.getName().toUpperCase().equals("EXARCH"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("OFFENSE")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("DEFENSE")) {
			if (profession != null)
				if ((profession.getName().toUpperCase().equals("RANGER")
						|| profession.getName().toUpperCase().equals("ROGUE")
						|| profession.getName().toUpperCase().equals("PALADIN")
						|| profession.getName().toUpperCase().equals("WARRIOR")
						|| profession.getName().toUpperCase().equals("SHADOWKNIGHT")
						|| profession.getName().toUpperCase().equals("MONK")
						|| profession.getName().toUpperCase().equals("HUNTER")
						|| profession.getName().toUpperCase().equals("BEASTLORD")
						|| profession.getName().toUpperCase().equals("BERSERKER")
						|| profession.getName().toUpperCase().equals("KNIGHT"))) {
					int cap = (int) ((5 * level) + 5);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				}
		}

		if (skillname.toUpperCase().equals("SPECIALISEABJURATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		if (skillname.toUpperCase().equals("SPECIALISEALTERATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		if (skillname.toUpperCase().equals("SPECIALISECONJURATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		if (skillname.toUpperCase().toUpperCase().equals("SPECIALISEDIVINATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}
		
		if (skillname.toUpperCase().equals("ALCHEMY")) {
			return 255;
		}
		
		if (skillname.equals("JEWELRYMAKING")) {
			return 255;
		}
		
		if (skillname.toUpperCase().equals("TAILORING")) {
			return 255;
		}
		
		if (skillname.toUpperCase().equals("FLETCHING")) {
			return 255;
		}
		
		if (skillname.equals("BLACKSMITHING")) {
			return 255;
		}
		
		if (skillname.toUpperCase().equals("TINKERING")) {
			return 255;
		}
		
		if (skillname.toUpperCase().equals("MAKEPOISON")) {
			return 255;
		}

		if (skillname.toUpperCase().equals("SPECIALISEEVOCATION")) {
			if (profession != null) {
				if (profession.getSpecialiselevel() < 1)
					return 0;

				if (specialisation == null || specialisation.equals(""))
					return 0;

				if (level >= profession.getSpecialiselevel()) {
					int cap = (int) ((2 * level) + 2);
					if (cap > Utils.getMaxSkillValue())
						return Utils.getMaxSkillValue();

					return cap;
				} else {
					return 0;
				}
			}
		}

		int cap = (int) ((2 * level) + 2);
		if (cap > Utils.getMaxSkillValue())
			return Utils.getMaxSkillValue();

		return cap;
	}

	public static Enchantment getEnchantmentFromEnchantmentName(String name) throws Exception {
		switch (name) {
		case "ARROW_DAMAGE":
			return Enchantment.ARROW_DAMAGE;
		case "ARROW_FIRE":
			return Enchantment.ARROW_FIRE;
		case "ARROW_INFINITE":
			return Enchantment.ARROW_INFINITE;
		case "ARROW_KNOCKBACK":
			return Enchantment.ARROW_KNOCKBACK;
		case "DAMAGE_ALL":
			return Enchantment.DAMAGE_ALL;
		case "DAMAGE_ARTHROPODS":
			return Enchantment.DAMAGE_ARTHROPODS;
		case "DAMAGE_UNDEAD":
			return Enchantment.DAMAGE_UNDEAD;
		case "DEPTH_STRIDER":
			return Enchantment.DEPTH_STRIDER;
		case "DIG_SPEED":
			return Enchantment.DIG_SPEED;
		case "DURABILITY":
			return Enchantment.DURABILITY;
		case "FIRE_ASPECT":
			return Enchantment.FIRE_ASPECT;
		case "FROST_WALKER":
			return Enchantment.FROST_WALKER;
		case "KNOCKBACK":
			return Enchantment.KNOCKBACK;
		case "LOOT_BONUS_BLOCKS":
			return Enchantment.LOOT_BONUS_BLOCKS;
		case "LOOT_BONUS_MOBS":
			return Enchantment.LOOT_BONUS_MOBS;
		case "LUCK":
			return Enchantment.LUCK;
		case "LURE":
			return Enchantment.LURE;
		case "MENDING":
			return Enchantment.MENDING;
		case "PROTECTION_ENVIRONMENTAL":
			return Enchantment.PROTECTION_ENVIRONMENTAL;
		case "PROTECTION_EXPLOSIONS":
			return Enchantment.PROTECTION_EXPLOSIONS;
		case "PROTECTION_FALL":
			return Enchantment.PROTECTION_FALL;
		case "PROTECTION_FIRE":
			return Enchantment.PROTECTION_FIRE;
		case "PROTECTION_PROJECTILE":
			return Enchantment.PROTECTION_PROJECTILE;
		case "SILK_TOUCH":
			return Enchantment.SILK_TOUCH;
		case "THORNS":
			return Enchantment.THORNS;
		case "WATER_WORKER":
			return Enchantment.WATER_WORKER;
		default:
			throw new Exception("Unsupported enchantment type for SoliniaItem");
		}
	}

	public static <T> T getRandomItemFromList(List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

	public static void checkArmourEquip(ISoliniaPlayer solplayer, PlayerInteractEvent event) {
		ItemStack itemstack = event.getItem();
		if (itemstack == null)
			return;
		
		if (!(CraftItemStack.asNMSCopy(itemstack).getItem() instanceof net.minecraft.server.v1_13_R2.ItemArmor)) {
			return;
		}

		if (Utils.IsSoliniaItem(itemstack) && !itemstack.getType().equals(Material.ENCHANTED_BOOK)) {
			try {
				ISoliniaItem soliniaitem = StateManager.getInstance().getConfigurationManager().getItem(itemstack);

				if (soliniaitem.getAllowedClassNames().size() == 0)
					return;

				if (solplayer.getClassObj() == null) {
					Utils.CancelEvent(event);
					;
					event.getPlayer().updateInventory();
					event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
					return;
				}

				if (!soliniaitem.getAllowedClassNames().contains(solplayer.getClassObj().getName())) {
					Utils.CancelEvent(event);
					event.getPlayer().updateInventory();
					event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
					return;
				}

				if (soliniaitem.getMinLevel() > solplayer.getLevel()) {
					Utils.CancelEvent(event);
					;
					event.getPlayer().updateInventory();
					event.getPlayer().sendMessage(ChatColor.GRAY + "Your class cannot wear this armour");
					return;
				}

			} catch (CoreStateInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static String FormatAsName(String name) {
		// TODO Auto-generated method stub
		return CapitaliseFirstLetter(name);
	}
	
	public static String CapitaliseFirstLetter(String word)
	{
		return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
	}

	public static String getCasterClass(String classname) {
		switch (classname) {
		case "CLERIC":
		case "PALADIN":
		case "RANGER":
		case "DRUID":
		case "SHAMAN":
		case "HUNTER":
		case "EXARCH":
		case "KNIGHT":
			return "W";
		case "ARCANIST":
		case "SHADOWKNIGHT":
		case "BARD":
		case "NECROMANCER":
		case "WIZARD":
		case "MAGICIAN":
		case "ENCHANTER":
			return "I";
		default:
			return "N";
		}
	}
	
	public static boolean isEntityInLineOfSightCone(LivingEntity entity, Entity target, int arc, int range) {
		if (!TargetHelper.getConeTargets(entity, arc, range).contains(target))
			return false;
		
		if (entity.hasLineOfSight(target))
			return true;
		
		return false;
	}

	public static LivingEntity getTargettedLivingEntity(LivingEntity observer, int reach) {
		Location observerPos = observer.getEyeLocation();
		Vector3D observerDir = new Vector3D(observerPos.getDirection());

		Vector3D observerStart = new Vector3D(observerPos);
		Vector3D observerEnd = observerStart.add(observerDir.multiply(reach));

		LivingEntity hit = null;

		// Get nearby entities
		for (Entity targetEntity : observer.getNearbyEntities(reach, reach, reach)) {
			if (!(targetEntity instanceof LivingEntity))
				continue;

			LivingEntity target = (LivingEntity) targetEntity;
			// Bounding box of the given player
			Vector3D targetPos = new Vector3D(target.getLocation());
			Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
			Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

			if (target != observer && hasIntersection(observerStart, observerEnd, minimum, maximum)) {
				if (hit == null || hit.getLocation().distanceSquared(observerPos) > target.getLocation()
						.distanceSquared(observerPos)) {

					hit = target;
				}
			}
		}

		return hit;
	}

	private static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
		final double epsilon = 0.0001f;

		Vector3D d = p2.subtract(p1).multiply(0.5);
		Vector3D e = max.subtract(min).multiply(0.5);
		Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
		Vector3D ad = d.abs();

		if (Math.abs(c.x) > e.x + ad.x)
			return false;
		if (Math.abs(c.y) > e.y + ad.y)
			return false;
		if (Math.abs(c.z) > e.z + ad.z)
			return false;

		if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
			return false;
		if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
			return false;
		if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
			return false;

		return true;
	}

	public static boolean isEntityInLineOfSight(LivingEntity entityfrom, Entity entityto) {
		if (entityto instanceof LivingEntity) {
			entityto = (LivingEntity) entityto;
			double x = entityfrom.getLocation().toVector().distance(entityto.getLocation().toVector());
			Vector direction = entityfrom.getLocation().getDirection().multiply(x);
			Vector answer = direction.add(entityfrom.getLocation().toVector());
			if (answer.distance(entityto.getLocation().toVector()) < 1.37) {
				if (entityfrom.hasLineOfSight(entityto)) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static int getEffectIdFromEffectType(SpellEffectType spellEffectType) {
		switch (spellEffectType) {
		case CurrentHP:
			return 0;
		case ArmorClass:
			return 1;
		case ATK:
			return 2;
		case MovementSpeed:
			return 3;
		case STR:
			return 4;
		case DEX:
			return 5;
		case AGI:
			return 6;
		case STA:
			return 7;
		case INT:
			return 8;
		case WIS:
			return 9;
		case CHA:
			return 10;
		case AttackSpeed:
			return 11;
		case Invisibility:
			return 12;
		case SeeInvis:
			return 13;
		case WaterBreathing:
			return 14;
		case CurrentMana:
			return 15;
		case NPCFrenzy:
			return 16;
		case NPCAwareness:
			return 17;
		case Lull:
			return 18;
		case AddFaction:
			return 19;
		case Blind:
			return 20;
		case Stun:
			return 21;
		case Charm:
			return 22;
		case Fear:
			return 23;
		case Stamina:
			return 24;
		case BindAffinity:
			return 25;
		case Gate:
			return 26;
		case CancelMagic:
			return 27;
		case InvisVsUndead:
			return 28;
		case InvisVsAnimals:
			return 29;
		case ChangeFrenzyRad:
			return 30;
		case Mez:
			return 31;
		case SummonItem:
			return 32;
		case SummonPet:
			return 33;
		case Confuse:
			return 34;
		case DiseaseCounter:
			return 35;
		case PoisonCounter:
			return 36;
		case DetectHostile:
			return 37;
		case DetectMagic:
			return 38;
		case DetectPoison:
			return 39;
		case DivineAura:
			return 40;
		case Destroy:
			return 41;
		case ShadowStep:
			return 42;
		case Berserk:
			return 43;
		case Lycanthropy:
			return 44;
		case Vampirism:
			return 45;
		case ResistFire:
			return 46;
		case ResistCold:
			return 47;
		case ResistPoison:
			return 48;
		case ResistDisease:
			return 49;
		case ResistMagic:
			return 50;
		case DetectTraps:
			return 51;
		case SenseDead:
			return 52;
		case SenseSummoned:
			return 53;
		case SenseAnimals:
			return 54;
		case Rune:
			return 55;
		case TrueNorth:
			return 56;
		case Levitate:
			return 57;
		case Illusion:
			return 58;
		case DamageShield:
			return 59;
		case TransferItem:
			return 60;
		case Identify:
			return 61;
		case ItemID:
			return 62;
		case WipeHateList:
			return 63;
		case SpinTarget:
			return 64;
		case InfraVision:
			return 65;
		case UltraVision:
			return 66;
		case EyeOfZomm:
			return 67;
		case ReclaimPet:
			return 68;
		case TotalHP:
			return 69;
		case CorpseBomb:
			return 70;
		case NecPet:
			return 71;
		case PreserveCorpse:
			return 72;
		case BindSight:
			return 73;
		case FeignDeath:
			return 74;
		case VoiceGraft:
			return 75;
		case Sentinel:
			return 76;
		case LocateCorpse:
			return 77;
		case AbsorbMagicAtt:
			return 78;
		case CurrentHPOnce:
			return 79;
		case EnchantLight:
			return 80;
		case Revive:
			return 81;
		case SummonPC:
			return 82;
		case Teleport:
			return 83;
		case TossUp:
			return 84;
		case WeaponProc:
			return 85;
		case Harmony:
			return 86;
		case MagnifyVision:
			return 87;
		case Succor:
			return 88;
		case ModelSize:
			return 89;
		case Cloak:
			return 90;
		case SummonCorpse:
			return 91;
		case InstantHate:
			return 92;
		case StopRain:
			return 93;
		case NegateIfCombat:
			return 94;
		case Sacrifice:
			return 95;
		case Silence:
			return 96;
		case ManaPool:
			return 97;
		case AttackSpeed2:
			return 98;
		case Root:
			return 99;
		case HealOverTime:
			return 100;
		case CompleteHeal:
			return 101;
		case Fearless:
			return 102;
		case CallPet:
			return 103;
		case Translocate:
			return 104;
		case AntiGate:
			return 105;
		case SummonBSTPet:
			return 106;
		case AlterNPCLevel:
			return 107;
		case Familiar:
			return 108;
		case SummonItemIntoBag:
			return 109;
		case IncreaseArchery:
			return 110;
		case ResistAll:
			return 111;
		case CastingLevel:
			return 112;
		case SummonHorse:
			return 113;
		case ChangeAggro:
			return 114;
		case Hunger:
			return 115;
		case CurseCounter:
			return 116;
		case MagicWeapon:
			return 117;
		case Amplification:
			return 118;
		case AttackSpeed3:
			return 119;
		case HealRate:
			return 120;
		case ReverseDS:
			return 121;
		case ReduceSkill:
			return 122;
		case Screech:
			return 123;
		case ImprovedDamage:
			return 124;
		case ImprovedHeal:
			return 125;
		case SpellResistReduction:
			return 126;
		case IncreaseSpellHaste:
			return 127;
		case IncreaseSpellDuration:
			return 128;
		case IncreaseRange:
			return 129;
		case SpellHateMod:
			return 130;
		case ReduceReagentCost:
			return 131;
		case ReduceManaCost:
			return 132;
		case FcStunTimeMod:
			return 133;
		case LimitMaxLevel:
			return 134;
		case LimitResist:
			return 135;
		case LimitTarget:
			return 136;
		case LimitEffect:
			return 137;
		case LimitSpellType:
			return 138;
		case LimitSpell:
			return 139;
		case LimitMinDur:
			return 140;
		case LimitInstant:
			return 141;
		case LimitMinLevel:
			return 142;
		case LimitCastTimeMin:
			return 143;
		case LimitCastTimeMax:
			return 144;
		case Teleport2:
			return 145;
		case ElectricityResist:
			return 146;
		case PercentalHeal:
			return 147;
		case StackingCommand_Block:
			return 148;
		case StackingCommand_Overwrite:
			return 149;
		case DeathSave:
			return 150;
		case SuspendPet:
			return 151;
		case TemporaryPets:
			return 152;
		case BalanceHP:
			return 153;
		case DispelDetrimental:
			return 154;
		case SpellCritDmgIncrease:
			return 155;
		case IllusionCopy:
			return 156;
		case SpellDamageShield:
			return 157;
		case Reflect:
			return 158;
		case AllStats:
			return 159;
		case MakeDrunk:
			return 160;
		case MitigateSpellDamage:
			return 161;
		case MitigateMeleeDamage:
			return 162;
		case NegateAttacks:
			return 163;
		case AppraiseLDonChest:
			return 164;
		case DisarmLDoNTrap:
			return 165;
		case UnlockLDoNChest:
			return 166;
		case PetPowerIncrease:
			return 167;
		case MeleeMitigation:
			return 168;
		case CriticalHitChance:
			return 169;
		case SpellCritChance:
			return 170;
		case CrippBlowChance:
			return 171;
		case AvoidMeleeChance:
			return 172;
		case RiposteChance:
			return 173;
		case DodgeChance:
			return 174;
		case ParryChance:
			return 175;
		case DualWieldChance:
			return 176;
		case DoubleAttackChance:
			return 177;
		case MeleeLifetap:
			return 178;
		case AllInstrumentMod:
			return 179;
		case ResistSpellChance:
			return 180;
		case ResistFearChance:
			return 181;
		case HundredHands:
			return 182;
		case MeleeSkillCheck:
			return 183;
		case HitChance:
			return 184;
		case DamageModifier:
			return 185;
		case MinDamageModifier:
			return 186;
		case BalanceMana:
			return 187;
		case IncreaseBlockChance:
			return 188;
		case CurrentEndurance:
			return 189;
		case EndurancePool:
			return 190;
		case Amnesia:
			return 191;
		case Hate:
			return 192;
		case SkillAttack:
			return 193;
		case FadingMemories:
			return 194;
		case StunResist:
			return 195;
		case StrikeThrough:
			return 196;
		case SkillDamageTaken:
			return 197;
		case CurrentEnduranceOnce:
			return 198;
		case Taunt:
			return 199;
		case ProcChance:
			return 200;
		case RangedProc:
			return 201;
		case IllusionOther:
			return 202;
		case MassGroupBuff:
			return 203;
		case GroupFearImmunity:
			return 204;
		case Rampage:
			return 205;
		case AETaunt:
			return 206;
		case FleshToBone:
			return 207;
		case PurgePoison:
			return 208;
		case DispelBeneficial:
			return 209;
		case PetShield:
			return 210;
		case AEMelee:
			return 211;
		case FrenziedDevastation:
			return 212;
		case PetMaxHP:
			return 213;
		case MaxHPChange:
			return 214;
		case PetAvoidance:
			return 215;
		case Accuracy:
			return 216;
		case HeadShot:
			return 217;
		case PetCriticalHit:
			return 218;
		case SlayUndead:
			return 219;
		case SkillDamageAmount:
			return 220;
		case Packrat:
			return 221;
		case BlockBehind:
			return 222;
		case DoubleRiposte:
			return 223;
		case GiveDoubleRiposte:
			return 224;
		case GiveDoubleAttack:
			return 225;
		case TwoHandBash:
			return 226;
		case ReduceSkillTimer:
			return 227;
		case ReduceFallDamage:
			return 228;
		case PersistantCasting:
			return 229;
		case ExtendedShielding:
			return 230;
		case StunBashChance:
			return 231;
		case DivineSave:
			return 232;
		case Metabolism:
			return 233;
		case ReduceApplyPoisonTime:
			return 234;
		case ChannelChanceSpells:
			return 235;
		case FreePet:
			return 236;
		case GivePetGroupTarget:
			return 237;
		case IllusionPersistence:
			return 238;
		case FeignedCastOnChance:
			return 239;
		case StringUnbreakable:
			return 240;
		case ImprovedReclaimEnergy:
			return 241;
		case IncreaseChanceMemwipe:
			return 242;
		case CharmBreakChance:
			return 243;
		case RootBreakChance:
			return 244;
		case TrapCircumvention:
			return 245;
		case SetBreathLevel:
			return 246;
		case RaiseSkillCap:
			return 247;
		case SecondaryForte:
			return 248;
		case SecondaryDmgInc:
			return 249;
		case SpellProcChance:
			return 250;
		case ConsumeProjectile:
			return 251;
		case FrontalBackstabChance:
			return 252;
		case FrontalBackstabMinDmg:
			return 253;
		case Blank:
			return 254;
		case ShieldDuration:
			return 255;
		case ShroudofStealth:
			return 256;
		case PetDiscipline:
			return 257;
		case TripleBackstab:
			return 258;
		case CombatStability:
			return 259;
		case AddSingingMod:
			return 260;
		case SongModCap:
			return 261;
		case RaiseStatCap:
			return 262;
		case TradeSkillMastery:
			return 263;
		case HastenedAASkill:
			return 264;
		case MasteryofPast:
			return 265;
		case ExtraAttackChance:
			return 266;
		case AddPetCommand:
			return 267;
		case ReduceTradeskillFail:
			return 268;
		case MaxBindWound:
			return 269;
		case BardSongRange:
			return 270;
		case BaseMovementSpeed:
			return 271;
		case CastingLevel2:
			return 272;
		case CriticalDoTChance:
			return 273;
		case CriticalHealChance:
			return 274;
		case CriticalMend:
			return 275;
		case Ambidexterity:
			return 276;
		case UnfailingDivinity:
			return 277;
		case FinishingBlow:
			return 278;
		case Flurry:
			return 279;
		case PetFlurry:
			return 280;
		case FeignedMinion:
			return 281;
		case ImprovedBindWound:
			return 282;
		case DoubleSpecialAttack:
			return 283;
		case LoHSetHeal:
			return 284;
		case NimbleEvasion:
			return 285;
		case FcDamageAmt:
			return 286;
		case SpellDurationIncByTic:
			return 287;
		case SkillAttackProc:
			return 288;
		case CastOnFadeEffect:
			return 289;
		case IncreaseRunSpeedCap:
			return 290;
		case Purify:
			return 291;
		case StrikeThrough2:
			return 292;
		case FrontalStunResist:
			return 293;
		case CriticalSpellChance:
			return 294;
		case ReduceTimerSpecial:
			return 295;
		case FcSpellVulnerability:
			return 296;
		case FcDamageAmtIncoming:
			return 297;
		case ChangeHeight:
			return 298;
		case WakeTheDead:
			return 299;
		case Doppelganger:
			return 300;
		case ArcheryDamageModifier:
			return 301;
		case FcDamagePctCrit:
			return 302;
		case FcDamageAmtCrit:
			return 303;
		case OffhandRiposteFail:
			return 304;
		case MitigateDamageShield:
			return 305;
		case ArmyOfTheDead:
			return 306;
		case Appraisal:
			return 307;
		case SuspendMinion:
			return 308;
		case GateCastersBindpoint:
			return 309;
		case ReduceReuseTimer:
			return 310;
		case LimitCombatSkills:
			return 311;
		case Sanctuary:
			return 312;
		case ForageAdditionalItems:
			return 313;
		case Invisibility2:
			return 314;
		case InvisVsUndead2:
			return 315;
		case ImprovedInvisAnimals:
			return 316;
		case ItemHPRegenCapIncrease:
			return 317;
		case ItemManaRegenCapIncrease:
			return 318;
		case CriticalHealOverTime:
			return 319;
		case ShieldBlock:
			return 320;
		case ReduceHate:
			return 321;
		case GateToHomeCity:
			return 322;
		case DefensiveProc:
			return 323;
		case HPToMana:
			return 324;
		case NoBreakAESneak:
			return 325;
		case SpellSlotIncrease:
			return 326;
		case MysticalAttune:
			return 327;
		case DelayDeath:
			return 328;
		case ManaAbsorbPercentDamage:
			return 329;
		case CriticalDamageMob:
			return 330;
		case Salvage:
			return 331;
		case SummonToCorpse:
			return 332;
		case CastOnRuneFadeEffect:
			return 333;
		case BardAEDot:
			return 334;
		case BlockNextSpellFocus:
			return 335;
		case IllusionaryTarget:
			return 336;
		case PercentXPIncrease:
			return 337;
		case SummonAndResAllCorpses:
			return 338;
		case TriggerOnCast:
			return 339;
		case SpellTrigger:
			return 340;
		case ItemAttackCapIncrease:
			return 341;
		case ImmuneFleeing:
			return 342;
		case InterruptCasting:
			return 343;
		case ChannelChanceItems:
			return 344;
		case AssassinateLevel:
			return 345;
		case HeadShotLevel:
			return 346;
		case DoubleRangedAttack:
			return 347;
		case LimitManaMin:
			return 348;
		case ShieldEquipDmgMod:
			return 349;
		case ManaBurn:
			return 350;
		case PersistentEffect:
			return 351;
		case IncreaseTrapCount:
			return 352;
		case AdditionalAura:
			return 353;
		case DeactivateAllTraps:
			return 354;
		case LearnTrap:
			return 355;
		case ChangeTriggerType:
			return 356;
		case FcMute:
			return 357;
		case CurrentManaOnce:
			return 358;
		case PassiveSenseTrap:
			return 359;
		case ProcOnKillShot:
			return 360;
		case SpellOnDeath:
			return 361;
		case PotionBeltSlots:
			return 362;
		case BandolierSlots:
			return 363;
		case TripleAttackChance:
			return 364;
		case ProcOnSpellKillShot:
			return 365;
		case GroupShielding:
			return 366;
		case SetBodyType:
			return 367;
		case FactionMod:
			return 368;
		case CorruptionCounter:
			return 369;
		case ResistCorruption:
			return 370;
		case AttackSpeed4:
			return 371;
		case ForageSkill:
			return 372;
		case CastOnFadeEffectAlways:
			return 373;
		case ApplyEffect:
			return 374;
		case DotCritDmgIncrease:
			return 375;
		case Fling:
			return 376;
		case CastOnFadeEffectNPC:
			return 377;
		case SpellEffectResistChance:
			return 378;
		case ShadowStepDirectional:
			return 379;
		case Knockdown:
			return 380;
		case KnockTowardCaster:
			return 381;
		case NegateSpellEffect:
			return 382;
		case SympatheticProc:
			return 383;
		case Leap:
			return 384;
		case LimitSpellGroup:
			return 385;
		case CastOnCurer:
			return 386;
		case CastOnCure:
			return 387;
		case SummonCorpseZone:
			return 388;
		case FcTimerRefresh:
			return 389;
		case FcTimerLockout:
			return 390;
		case LimitManaMax:
			return 391;
		case FcHealAmt:
			return 392;
		case FcHealPctIncoming:
			return 393;
		case FcHealAmtIncoming:
			return 394;
		case FcHealPctCritIncoming:
			return 395;
		case FcHealAmtCrit:
			return 396;
		case PetMeleeMitigation:
			return 397;
		case SwarmPetDuration:
			return 398;
		case FcTwincast:
			return 399;
		case HealGroupFromMana:
			return 400;
		case ManaDrainWithDmg:
			return 401;
		case EndDrainWithDmg:
			return 402;
		case LimitSpellClass:
			return 403;
		case LimitSpellSubclass:
			return 404;
		case TwoHandBluntBlock:
			return 405;
		case CastonNumHitFade:
			return 406;
		case CastonFocusEffect:
			return 407;
		case LimitHPPercent:
			return 408;
		case LimitManaPercent:
			return 409;
		case LimitEndPercent:
			return 410;
		case LimitClass:
			return 411;
		case LimitRace:
			return 412;
		case FcBaseEffects:
			return 413;
		case LimitCastingSkill:
			return 414;
		case FFItemClass:
			return 415;
		case ACv2:
			return 416;
		case ManaRegen_v2:
			return 417;
		case SkillDamageAmount2:
			return 418;
		case AddMeleeProc:
			return 419;
		case FcLimitUse:
			return 420;
		case FcIncreaseNumHits:
			return 421;
		case LimitUseMin:
			return 422;
		case LimitUseType:
			return 423;
		case GravityEffect:
			return 424;
		case Display:
			return 425;
		case IncreaseExtTargetWindow:
			return 426;
		case SkillProc:
			return 427;
		case LimitToSkill:
			return 428;
		case SkillProcSuccess:
			return 429;
		case PostEffect:
			return 430;
		case PostEffectData:
			return 431;
		case ExpandMaxActiveTrophyBen:
			return 432;
		case CriticalDotDecay:
			return 433;
		case CriticalHealDecay:
			return 434;
		case CriticalRegenDecay:
			return 435;
		case BeneficialCountDownHold:
			return 436;
		case TeleporttoAnchor:
			return 437;
		case TranslocatetoAnchor:
			return 438;
		case Assassinate:
			return 439;
		case FinishingBlowLvl:
			return 440;
		case DistanceRemoval:
			return 441;
		case TriggerOnReqTarget:
			return 442;
		case TriggerOnReqCaster:
			return 443;
		case ImprovedTaunt:
			return 444;
		case AddMercSlot:
			return 445;
		case AStacker:
			return 446;
		case BStacker:
			return 447;
		case CStacker:
			return 448;
		case DStacker:
			return 449;
		case MitigateDotDamage:
			return 450;
		case MeleeThresholdGuard:
			return 451;
		case SpellThresholdGuard:
			return 452;
		case TriggerMeleeThreshold:
			return 453;
		case TriggerSpellThreshold:
			return 454;
		case AddHatePct:
			return 455;
		case AddHateOverTimePct:
			return 456;
		case ResourceTap:
			return 457;
		case FactionModPct:
			return 458;
		case DamageModifier2:
			return 459;
		case Ff_Override_NotFocusable:
			return 460;
		case ImprovedDamage2:
			return 461;
		case FcDamageAmt2:
			return 462;
		case Shield_Target:
			return 463;
		case PC_Pet_Rampage:
			return 464;
		case PC_Pet_AE_Rampage:
			return 465;
		case PC_Pet_Flurry_Chance:
			return 466;
		case DS_Mitigation_Amount:
			return 467;
		case DS_Mitigation_Percentage:
			return 468;
		case Chance_Best_in_Spell_Grp:
			return 469;
		case SE_Trigger_Best_in_Spell_Grp:
			return 470;
		case Double_Melee_Round:
			return 471;
		case Backstab:
			return 472;
		default:
			return -1;
		}
	}

	public static SpellEffectType getSpellEffectType(Integer typeId) {
		switch (typeId) {
		case 0:
			return SpellEffectType.CurrentHP;
		case 1:
			return SpellEffectType.ArmorClass;
		case 2:
			return SpellEffectType.ATK;
		case 3:
			return SpellEffectType.MovementSpeed;
		case 4:
			return SpellEffectType.STR;
		case 5:
			return SpellEffectType.DEX;
		case 6:
			return SpellEffectType.AGI;
		case 7:
			return SpellEffectType.STA;
		case 8:
			return SpellEffectType.INT;
		case 9:
			return SpellEffectType.WIS;
		case 10:
			return SpellEffectType.CHA;
		case 11:
			return SpellEffectType.AttackSpeed;
		case 12:
			return SpellEffectType.Invisibility;
		case 13:
			return SpellEffectType.SeeInvis;
		case 14:
			return SpellEffectType.WaterBreathing;
		case 15:
			return SpellEffectType.CurrentMana;
		case 16:
			return SpellEffectType.NPCFrenzy;
		case 17:
			return SpellEffectType.NPCAwareness;
		case 18:
			return SpellEffectType.Lull;
		case 19:
			return SpellEffectType.AddFaction;
		case 20:
			return SpellEffectType.Blind;
		case 21:
			return SpellEffectType.Stun;
		case 22:
			return SpellEffectType.Charm;
		case 23:
			return SpellEffectType.Fear;
		case 24:
			return SpellEffectType.Stamina;
		case 25:
			return SpellEffectType.BindAffinity;
		case 26:
			return SpellEffectType.Gate;
		case 27:
			return SpellEffectType.CancelMagic;
		case 28:
			return SpellEffectType.InvisVsUndead;
		case 29:
			return SpellEffectType.InvisVsAnimals;
		case 30:
			return SpellEffectType.ChangeFrenzyRad;
		case 31:
			return SpellEffectType.Mez;
		case 32:
			return SpellEffectType.SummonItem;
		case 33:
			return SpellEffectType.SummonPet;
		case 34:
			return SpellEffectType.Confuse;
		case 35:
			return SpellEffectType.DiseaseCounter;
		case 36:
			return SpellEffectType.PoisonCounter;
		case 37:
			return SpellEffectType.DetectHostile;
		case 38:
			return SpellEffectType.DetectMagic;
		case 39:
			return SpellEffectType.DetectPoison;
		case 40:
			return SpellEffectType.DivineAura;
		case 41:
			return SpellEffectType.Destroy;
		case 42:
			return SpellEffectType.ShadowStep;
		case 43:
			return SpellEffectType.Berserk;
		case 44:
			return SpellEffectType.Lycanthropy;
		case 45:
			return SpellEffectType.Vampirism;
		case 46:
			return SpellEffectType.ResistFire;
		case 47:
			return SpellEffectType.ResistCold;
		case 48:
			return SpellEffectType.ResistPoison;
		case 49:
			return SpellEffectType.ResistDisease;
		case 50:
			return SpellEffectType.ResistMagic;
		case 51:
			return SpellEffectType.DetectTraps;
		case 52:
			return SpellEffectType.SenseDead;
		case 53:
			return SpellEffectType.SenseSummoned;
		case 54:
			return SpellEffectType.SenseAnimals;
		case 55:
			return SpellEffectType.Rune;
		case 56:
			return SpellEffectType.TrueNorth;
		case 57:
			return SpellEffectType.Levitate;
		case 58:
			return SpellEffectType.Illusion;
		case 59:
			return SpellEffectType.DamageShield;
		case 60:
			return SpellEffectType.TransferItem;
		case 61:
			return SpellEffectType.Identify;
		case 62:
			return SpellEffectType.ItemID;
		case 63:
			return SpellEffectType.WipeHateList;
		case 64:
			return SpellEffectType.SpinTarget;
		case 65:
			return SpellEffectType.InfraVision;
		case 66:
			return SpellEffectType.UltraVision;
		case 67:
			return SpellEffectType.EyeOfZomm;
		case 68:
			return SpellEffectType.ReclaimPet;
		case 69:
			return SpellEffectType.TotalHP;
		case 70:
			return SpellEffectType.CorpseBomb;
		case 71:
			return SpellEffectType.NecPet;
		case 72:
			return SpellEffectType.PreserveCorpse;
		case 73:
			return SpellEffectType.BindSight;
		case 74:
			return SpellEffectType.FeignDeath;
		case 75:
			return SpellEffectType.VoiceGraft;
		case 76:
			return SpellEffectType.Sentinel;
		case 77:
			return SpellEffectType.LocateCorpse;
		case 78:
			return SpellEffectType.AbsorbMagicAtt;
		case 79:
			return SpellEffectType.CurrentHPOnce;
		case 80:
			return SpellEffectType.EnchantLight;
		case 81:
			return SpellEffectType.Revive;
		case 82:
			return SpellEffectType.SummonPC;
		case 83:
			return SpellEffectType.Teleport;
		case 84:
			return SpellEffectType.TossUp;
		case 85:
			return SpellEffectType.WeaponProc;
		case 86:
			return SpellEffectType.Harmony;
		case 87:
			return SpellEffectType.MagnifyVision;
		case 88:
			return SpellEffectType.Succor;
		case 89:
			return SpellEffectType.ModelSize;
		case 90:
			return SpellEffectType.Cloak;
		case 91:
			return SpellEffectType.SummonCorpse;
		case 92:
			return SpellEffectType.InstantHate;
		case 93:
			return SpellEffectType.StopRain;
		case 94:
			return SpellEffectType.NegateIfCombat;
		case 95:
			return SpellEffectType.Sacrifice;
		case 96:
			return SpellEffectType.Silence;
		case 97:
			return SpellEffectType.ManaPool;
		case 98:
			return SpellEffectType.AttackSpeed2;
		case 99:
			return SpellEffectType.Root;
		case 100:
			return SpellEffectType.HealOverTime;
		case 101:
			return SpellEffectType.CompleteHeal;
		case 102:
			return SpellEffectType.Fearless;
		case 103:
			return SpellEffectType.CallPet;
		case 104:
			return SpellEffectType.Translocate;
		case 105:
			return SpellEffectType.AntiGate;
		case 106:
			return SpellEffectType.SummonBSTPet;
		case 107:
			return SpellEffectType.AlterNPCLevel;
		case 108:
			return SpellEffectType.Familiar;
		case 109:
			return SpellEffectType.SummonItemIntoBag;
		case 110:
			return SpellEffectType.IncreaseArchery;
		case 111:
			return SpellEffectType.ResistAll;
		case 112:
			return SpellEffectType.CastingLevel;
		case 113:
			return SpellEffectType.SummonHorse;
		case 114:
			return SpellEffectType.ChangeAggro;
		case 115:
			return SpellEffectType.Hunger;
		case 116:
			return SpellEffectType.CurseCounter;
		case 117:
			return SpellEffectType.MagicWeapon;
		case 118:
			return SpellEffectType.Amplification;
		case 119:
			return SpellEffectType.AttackSpeed3;
		case 120:
			return SpellEffectType.HealRate;
		case 121:
			return SpellEffectType.ReverseDS;
		case 122:
			return SpellEffectType.ReduceSkill;
		case 123:
			return SpellEffectType.Screech;
		case 124:
			return SpellEffectType.ImprovedDamage;
		case 125:
			return SpellEffectType.ImprovedHeal;
		case 126:
			return SpellEffectType.SpellResistReduction;
		case 127:
			return SpellEffectType.IncreaseSpellHaste;
		case 128:
			return SpellEffectType.IncreaseSpellDuration;
		case 129:
			return SpellEffectType.IncreaseRange;
		case 130:
			return SpellEffectType.SpellHateMod;
		case 131:
			return SpellEffectType.ReduceReagentCost;
		case 132:
			return SpellEffectType.ReduceManaCost;
		case 133:
			return SpellEffectType.FcStunTimeMod;
		case 134:
			return SpellEffectType.LimitMaxLevel;
		case 135:
			return SpellEffectType.LimitResist;
		case 136:
			return SpellEffectType.LimitTarget;
		case 137:
			return SpellEffectType.LimitEffect;
		case 138:
			return SpellEffectType.LimitSpellType;
		case 139:
			return SpellEffectType.LimitSpell;
		case 140:
			return SpellEffectType.LimitMinDur;
		case 141:
			return SpellEffectType.LimitInstant;
		case 142:
			return SpellEffectType.LimitMinLevel;
		case 143:
			return SpellEffectType.LimitCastTimeMin;
		case 144:
			return SpellEffectType.LimitCastTimeMax;
		case 145:
			return SpellEffectType.Teleport2;
		case 146:
			return SpellEffectType.ElectricityResist;
		case 147:
			return SpellEffectType.PercentalHeal;
		case 148:
			return SpellEffectType.StackingCommand_Block;
		case 149:
			return SpellEffectType.StackingCommand_Overwrite;
		case 150:
			return SpellEffectType.DeathSave;
		case 151:
			return SpellEffectType.SuspendPet;
		case 152:
			return SpellEffectType.TemporaryPets;
		case 153:
			return SpellEffectType.BalanceHP;
		case 154:
			return SpellEffectType.DispelDetrimental;
		case 155:
			return SpellEffectType.SpellCritDmgIncrease;
		case 156:
			return SpellEffectType.IllusionCopy;
		case 157:
			return SpellEffectType.SpellDamageShield;
		case 158:
			return SpellEffectType.Reflect;
		case 159:
			return SpellEffectType.AllStats;
		case 160:
			return SpellEffectType.MakeDrunk;
		case 161:
			return SpellEffectType.MitigateSpellDamage;
		case 162:
			return SpellEffectType.MitigateMeleeDamage;
		case 163:
			return SpellEffectType.NegateAttacks;
		case 164:
			return SpellEffectType.AppraiseLDonChest;
		case 165:
			return SpellEffectType.DisarmLDoNTrap;
		case 166:
			return SpellEffectType.UnlockLDoNChest;
		case 167:
			return SpellEffectType.PetPowerIncrease;
		case 168:
			return SpellEffectType.MeleeMitigation;
		case 169:
			return SpellEffectType.CriticalHitChance;
		case 170:
			return SpellEffectType.SpellCritChance;
		case 171:
			return SpellEffectType.CrippBlowChance;
		case 172:
			return SpellEffectType.AvoidMeleeChance;
		case 173:
			return SpellEffectType.RiposteChance;
		case 174:
			return SpellEffectType.DodgeChance;
		case 175:
			return SpellEffectType.ParryChance;
		case 176:
			return SpellEffectType.DualWieldChance;
		case 177:
			return SpellEffectType.DoubleAttackChance;
		case 178:
			return SpellEffectType.MeleeLifetap;
		case 179:
			return SpellEffectType.AllInstrumentMod;
		case 180:
			return SpellEffectType.ResistSpellChance;
		case 181:
			return SpellEffectType.ResistFearChance;
		case 182:
			return SpellEffectType.HundredHands;
		case 183:
			return SpellEffectType.MeleeSkillCheck;
		case 184:
			return SpellEffectType.HitChance;
		case 185:
			return SpellEffectType.DamageModifier;
		case 186:
			return SpellEffectType.MinDamageModifier;
		case 187:
			return SpellEffectType.BalanceMana;
		case 188:
			return SpellEffectType.IncreaseBlockChance;
		case 189:
			return SpellEffectType.CurrentEndurance;
		case 190:
			return SpellEffectType.EndurancePool;
		case 191:
			return SpellEffectType.Amnesia;
		case 192:
			return SpellEffectType.Hate;
		case 193:
			return SpellEffectType.SkillAttack;
		case 194:
			return SpellEffectType.FadingMemories;
		case 195:
			return SpellEffectType.StunResist;
		case 196:
			return SpellEffectType.StrikeThrough;
		case 197:
			return SpellEffectType.SkillDamageTaken;
		case 198:
			return SpellEffectType.CurrentEnduranceOnce;
		case 199:
			return SpellEffectType.Taunt;
		case 200:
			return SpellEffectType.ProcChance;
		case 201:
			return SpellEffectType.RangedProc;
		case 202:
			return SpellEffectType.IllusionOther;
		case 203:
			return SpellEffectType.MassGroupBuff;
		case 204:
			return SpellEffectType.GroupFearImmunity;
		case 205:
			return SpellEffectType.Rampage;
		case 206:
			return SpellEffectType.AETaunt;
		case 207:
			return SpellEffectType.FleshToBone;
		case 208:
			return SpellEffectType.PurgePoison;
		case 209:
			return SpellEffectType.DispelBeneficial;
		case 210:
			return SpellEffectType.PetShield;
		case 211:
			return SpellEffectType.AEMelee;
		case 212:
			return SpellEffectType.FrenziedDevastation;
		case 213:
			return SpellEffectType.PetMaxHP;
		case 214:
			return SpellEffectType.MaxHPChange;
		case 215:
			return SpellEffectType.PetAvoidance;
		case 216:
			return SpellEffectType.Accuracy;
		case 217:
			return SpellEffectType.HeadShot;
		case 218:
			return SpellEffectType.PetCriticalHit;
		case 219:
			return SpellEffectType.SlayUndead;
		case 220:
			return SpellEffectType.SkillDamageAmount;
		case 221:
			return SpellEffectType.Packrat;
		case 222:
			return SpellEffectType.BlockBehind;
		case 223:
			return SpellEffectType.DoubleRiposte;
		case 224:
			return SpellEffectType.GiveDoubleRiposte;
		case 225:
			return SpellEffectType.GiveDoubleAttack;
		case 226:
			return SpellEffectType.TwoHandBash;
		case 227:
			return SpellEffectType.ReduceSkillTimer;
		case 228:
			return SpellEffectType.ReduceFallDamage;
		case 229:
			return SpellEffectType.PersistantCasting;
		case 230:
			return SpellEffectType.ExtendedShielding;
		case 231:
			return SpellEffectType.StunBashChance;
		case 232:
			return SpellEffectType.DivineSave;
		case 233:
			return SpellEffectType.Metabolism;
		case 234:
			return SpellEffectType.ReduceApplyPoisonTime;
		case 235:
			return SpellEffectType.ChannelChanceSpells;
		case 236:
			return SpellEffectType.FreePet;
		case 237:
			return SpellEffectType.GivePetGroupTarget;
		case 238:
			return SpellEffectType.IllusionPersistence;
		case 239:
			return SpellEffectType.FeignedCastOnChance;
		case 240:
			return SpellEffectType.StringUnbreakable;
		case 241:
			return SpellEffectType.ImprovedReclaimEnergy;
		case 242:
			return SpellEffectType.IncreaseChanceMemwipe;
		case 243:
			return SpellEffectType.CharmBreakChance;
		case 244:
			return SpellEffectType.RootBreakChance;
		case 245:
			return SpellEffectType.TrapCircumvention;
		case 246:
			return SpellEffectType.SetBreathLevel;
		case 247:
			return SpellEffectType.RaiseSkillCap;
		case 248:
			return SpellEffectType.SecondaryForte;
		case 249:
			return SpellEffectType.SecondaryDmgInc;
		case 250:
			return SpellEffectType.SpellProcChance;
		case 251:
			return SpellEffectType.ConsumeProjectile;
		case 252:
			return SpellEffectType.FrontalBackstabChance;
		case 253:
			return SpellEffectType.FrontalBackstabMinDmg;
		case 254:
			return SpellEffectType.Blank;
		case 255:
			return SpellEffectType.ShieldDuration;
		case 256:
			return SpellEffectType.ShroudofStealth;
		case 257:
			return SpellEffectType.PetDiscipline;
		case 258:
			return SpellEffectType.TripleBackstab;
		case 259:
			return SpellEffectType.CombatStability;
		case 260:
			return SpellEffectType.AddSingingMod;
		case 261:
			return SpellEffectType.SongModCap;
		case 262:
			return SpellEffectType.RaiseStatCap;
		case 263:
			return SpellEffectType.TradeSkillMastery;
		case 264:
			return SpellEffectType.HastenedAASkill;
		case 265:
			return SpellEffectType.MasteryofPast;
		case 266:
			return SpellEffectType.ExtraAttackChance;
		case 267:
			return SpellEffectType.AddPetCommand;
		case 268:
			return SpellEffectType.ReduceTradeskillFail;
		case 269:
			return SpellEffectType.MaxBindWound;
		case 270:
			return SpellEffectType.BardSongRange;
		case 271:
			return SpellEffectType.BaseMovementSpeed;
		case 272:
			return SpellEffectType.CastingLevel2;
		case 273:
			return SpellEffectType.CriticalDoTChance;
		case 274:
			return SpellEffectType.CriticalHealChance;
		case 275:
			return SpellEffectType.CriticalMend;
		case 276:
			return SpellEffectType.Ambidexterity;
		case 277:
			return SpellEffectType.UnfailingDivinity;
		case 278:
			return SpellEffectType.FinishingBlow;
		case 279:
			return SpellEffectType.Flurry;
		case 280:
			return SpellEffectType.PetFlurry;
		case 281:
			return SpellEffectType.FeignedMinion;
		case 282:
			return SpellEffectType.ImprovedBindWound;
		case 283:
			return SpellEffectType.DoubleSpecialAttack;
		case 284:
			return SpellEffectType.LoHSetHeal;
		case 285:
			return SpellEffectType.NimbleEvasion;
		case 286:
			return SpellEffectType.FcDamageAmt;
		case 287:
			return SpellEffectType.SpellDurationIncByTic;
		case 288:
			return SpellEffectType.SkillAttackProc;
		case 289:
			return SpellEffectType.CastOnFadeEffect;
		case 290:
			return SpellEffectType.IncreaseRunSpeedCap;
		case 291:
			return SpellEffectType.Purify;
		case 292:
			return SpellEffectType.StrikeThrough2;
		case 293:
			return SpellEffectType.FrontalStunResist;
		case 294:
			return SpellEffectType.CriticalSpellChance;
		case 295:
			return SpellEffectType.ReduceTimerSpecial;
		case 296:
			return SpellEffectType.FcSpellVulnerability;
		case 297:
			return SpellEffectType.FcDamageAmtIncoming;
		case 298:
			return SpellEffectType.ChangeHeight;
		case 299:
			return SpellEffectType.WakeTheDead;
		case 300:
			return SpellEffectType.Doppelganger;
		case 301:
			return SpellEffectType.ArcheryDamageModifier;
		case 302:
			return SpellEffectType.FcDamagePctCrit;
		case 303:
			return SpellEffectType.FcDamageAmtCrit;
		case 304:
			return SpellEffectType.OffhandRiposteFail;
		case 305:
			return SpellEffectType.MitigateDamageShield;
		case 306:
			return SpellEffectType.ArmyOfTheDead;
		case 307:
			return SpellEffectType.Appraisal;
		case 308:
			return SpellEffectType.SuspendMinion;
		case 309:
			return SpellEffectType.GateCastersBindpoint;
		case 310:
			return SpellEffectType.ReduceReuseTimer;
		case 311:
			return SpellEffectType.LimitCombatSkills;
		case 312:
			return SpellEffectType.Sanctuary;
		case 313:
			return SpellEffectType.ForageAdditionalItems;
		case 314:
			return SpellEffectType.Invisibility2;
		case 315:
			return SpellEffectType.InvisVsUndead2;
		case 316:
			return SpellEffectType.ImprovedInvisAnimals;
		case 317:
			return SpellEffectType.ItemHPRegenCapIncrease;
		case 318:
			return SpellEffectType.ItemManaRegenCapIncrease;
		case 319:
			return SpellEffectType.CriticalHealOverTime;
		case 320:
			return SpellEffectType.ShieldBlock;
		case 321:
			return SpellEffectType.ReduceHate;
		case 322:
			return SpellEffectType.GateToHomeCity;
		case 323:
			return SpellEffectType.DefensiveProc;
		case 324:
			return SpellEffectType.HPToMana;
		case 325:
			return SpellEffectType.NoBreakAESneak;
		case 326:
			return SpellEffectType.SpellSlotIncrease;
		case 327:
			return SpellEffectType.MysticalAttune;
		case 328:
			return SpellEffectType.DelayDeath;
		case 329:
			return SpellEffectType.ManaAbsorbPercentDamage;
		case 330:
			return SpellEffectType.CriticalDamageMob;
		case 331:
			return SpellEffectType.Salvage;
		case 332:
			return SpellEffectType.SummonToCorpse;
		case 333:
			return SpellEffectType.CastOnRuneFadeEffect;
		case 334:
			return SpellEffectType.BardAEDot;
		case 335:
			return SpellEffectType.BlockNextSpellFocus;
		case 336:
			return SpellEffectType.IllusionaryTarget;
		case 337:
			return SpellEffectType.PercentXPIncrease;
		case 338:
			return SpellEffectType.SummonAndResAllCorpses;
		case 339:
			return SpellEffectType.TriggerOnCast;
		case 340:
			return SpellEffectType.SpellTrigger;
		case 341:
			return SpellEffectType.ItemAttackCapIncrease;
		case 342:
			return SpellEffectType.ImmuneFleeing;
		case 343:
			return SpellEffectType.InterruptCasting;
		case 344:
			return SpellEffectType.ChannelChanceItems;
		case 345:
			return SpellEffectType.AssassinateLevel;
		case 346:
			return SpellEffectType.HeadShotLevel;
		case 347:
			return SpellEffectType.DoubleRangedAttack;
		case 348:
			return SpellEffectType.LimitManaMin;
		case 349:
			return SpellEffectType.ShieldEquipDmgMod;
		case 350:
			return SpellEffectType.ManaBurn;
		case 351:
			return SpellEffectType.PersistentEffect;
		case 352:
			return SpellEffectType.IncreaseTrapCount;
		case 353:
			return SpellEffectType.AdditionalAura;
		case 354:
			return SpellEffectType.DeactivateAllTraps;
		case 355:
			return SpellEffectType.LearnTrap;
		case 356:
			return SpellEffectType.ChangeTriggerType;
		case 357:
			return SpellEffectType.FcMute;
		case 358:
			return SpellEffectType.CurrentManaOnce;
		case 359:
			return SpellEffectType.PassiveSenseTrap;
		case 360:
			return SpellEffectType.ProcOnKillShot;
		case 361:
			return SpellEffectType.SpellOnDeath;
		case 362:
			return SpellEffectType.PotionBeltSlots;
		case 363:
			return SpellEffectType.BandolierSlots;
		case 364:
			return SpellEffectType.TripleAttackChance;
		case 365:
			return SpellEffectType.ProcOnSpellKillShot;
		case 366:
			return SpellEffectType.GroupShielding;
		case 367:
			return SpellEffectType.SetBodyType;
		case 368:
			return SpellEffectType.FactionMod;
		case 369:
			return SpellEffectType.CorruptionCounter;
		case 370:
			return SpellEffectType.ResistCorruption;
		case 371:
			return SpellEffectType.AttackSpeed4;
		case 372:
			return SpellEffectType.ForageSkill;
		case 373:
			return SpellEffectType.CastOnFadeEffectAlways;
		case 374:
			return SpellEffectType.ApplyEffect;
		case 375:
			return SpellEffectType.DotCritDmgIncrease;
		case 376:
			return SpellEffectType.Fling;
		case 377:
			return SpellEffectType.CastOnFadeEffectNPC;
		case 378:
			return SpellEffectType.SpellEffectResistChance;
		case 379:
			return SpellEffectType.ShadowStepDirectional;
		case 380:
			return SpellEffectType.Knockdown;
		case 381:
			return SpellEffectType.KnockTowardCaster;
		case 382:
			return SpellEffectType.NegateSpellEffect;
		case 383:
			return SpellEffectType.SympatheticProc;
		case 384:
			return SpellEffectType.Leap;
		case 385:
			return SpellEffectType.LimitSpellGroup;
		case 386:
			return SpellEffectType.CastOnCurer;
		case 387:
			return SpellEffectType.CastOnCure;
		case 388:
			return SpellEffectType.SummonCorpseZone;
		case 389:
			return SpellEffectType.FcTimerRefresh;
		case 390:
			return SpellEffectType.FcTimerLockout;
		case 391:
			return SpellEffectType.LimitManaMax;
		case 392:
			return SpellEffectType.FcHealAmt;
		case 393:
			return SpellEffectType.FcHealPctIncoming;
		case 394:
			return SpellEffectType.FcHealAmtIncoming;
		case 395:
			return SpellEffectType.FcHealPctCritIncoming;
		case 396:
			return SpellEffectType.FcHealAmtCrit;
		case 397:
			return SpellEffectType.PetMeleeMitigation;
		case 398:
			return SpellEffectType.SwarmPetDuration;
		case 399:
			return SpellEffectType.FcTwincast;
		case 400:
			return SpellEffectType.HealGroupFromMana;
		case 401:
			return SpellEffectType.ManaDrainWithDmg;
		case 402:
			return SpellEffectType.EndDrainWithDmg;
		case 403:
			return SpellEffectType.LimitSpellClass;
		case 404:
			return SpellEffectType.LimitSpellSubclass;
		case 405:
			return SpellEffectType.TwoHandBluntBlock;
		case 406:
			return SpellEffectType.CastonNumHitFade;
		case 407:
			return SpellEffectType.CastonFocusEffect;
		case 408:
			return SpellEffectType.LimitHPPercent;
		case 409:
			return SpellEffectType.LimitManaPercent;
		case 410:
			return SpellEffectType.LimitEndPercent;
		case 411:
			return SpellEffectType.LimitClass;
		case 412:
			return SpellEffectType.LimitRace;
		case 413:
			return SpellEffectType.FcBaseEffects;
		case 414:
			return SpellEffectType.LimitCastingSkill;
		case 415:
			return SpellEffectType.FFItemClass;
		case 416:
			return SpellEffectType.ACv2;
		case 417:
			return SpellEffectType.ManaRegen_v2;
		case 418:
			return SpellEffectType.SkillDamageAmount2;
		case 419:
			return SpellEffectType.AddMeleeProc;
		case 420:
			return SpellEffectType.FcLimitUse;
		case 421:
			return SpellEffectType.FcIncreaseNumHits;
		case 422:
			return SpellEffectType.LimitUseMin;
		case 423:
			return SpellEffectType.LimitUseType;
		case 424:
			return SpellEffectType.GravityEffect;
		case 425:
			return SpellEffectType.Display;
		case 426:
			return SpellEffectType.IncreaseExtTargetWindow;
		case 427:
			return SpellEffectType.SkillProc;
		case 428:
			return SpellEffectType.LimitToSkill;
		case 429:
			return SpellEffectType.SkillProcSuccess;
		case 430:
			return SpellEffectType.PostEffect;
		case 431:
			return SpellEffectType.PostEffectData;
		case 432:
			return SpellEffectType.ExpandMaxActiveTrophyBen;
		case 433:
			return SpellEffectType.CriticalDotDecay;
		case 434:
			return SpellEffectType.CriticalHealDecay;
		case 435:
			return SpellEffectType.CriticalRegenDecay;
		case 436:
			return SpellEffectType.BeneficialCountDownHold;
		case 437:
			return SpellEffectType.TeleporttoAnchor;
		case 438:
			return SpellEffectType.TranslocatetoAnchor;
		case 439:
			return SpellEffectType.Assassinate;
		case 440:
			return SpellEffectType.FinishingBlowLvl;
		case 441:
			return SpellEffectType.DistanceRemoval;
		case 442:
			return SpellEffectType.TriggerOnReqTarget;
		case 443:
			return SpellEffectType.TriggerOnReqCaster;
		case 444:
			return SpellEffectType.ImprovedTaunt;
		case 445:
			return SpellEffectType.AddMercSlot;
		case 446:
			return SpellEffectType.AStacker;
		case 447:
			return SpellEffectType.BStacker;
		case 448:
			return SpellEffectType.CStacker;
		case 449:
			return SpellEffectType.DStacker;
		case 450:
			return SpellEffectType.MitigateDotDamage;
		case 451:
			return SpellEffectType.MeleeThresholdGuard;
		case 452:
			return SpellEffectType.SpellThresholdGuard;
		case 453:
			return SpellEffectType.TriggerMeleeThreshold;
		case 454:
			return SpellEffectType.TriggerSpellThreshold;
		case 455:
			return SpellEffectType.AddHatePct;
		case 456:
			return SpellEffectType.AddHateOverTimePct;
		case 457:
			return SpellEffectType.ResourceTap;
		case 458:
			return SpellEffectType.FactionModPct;
		case 459:
			return SpellEffectType.DamageModifier2;
		case 460:
			return SpellEffectType.Ff_Override_NotFocusable;
		case 461:
			return SpellEffectType.ImprovedDamage2;
		case 462:
			return SpellEffectType.FcDamageAmt2;
		case 463:
			return SpellEffectType.Shield_Target;
		case 464:
			return SpellEffectType.PC_Pet_Rampage;
		case 465:
			return SpellEffectType.PC_Pet_AE_Rampage;
		case 466:
			return SpellEffectType.PC_Pet_Flurry_Chance;
		case 467:
			return SpellEffectType.DS_Mitigation_Amount;
		case 468:
			return SpellEffectType.DS_Mitigation_Percentage;
		case 469:
			return SpellEffectType.Chance_Best_in_Spell_Grp;
		case 470:
			return SpellEffectType.SE_Trigger_Best_in_Spell_Grp;
		case 471:
			return SpellEffectType.Double_Melee_Round;
		case 472:
			return SpellEffectType.Backstab;
		default:
			return SpellEffectType.ERROR;
		}
	}

	public static int getDurationFromSpell(ISoliniaLivingEntity solEntity, SoliniaSpell soliniaSpell) {
		int duration = soliniaSpell.calcBuffDuration(solEntity, solEntity.getLevel());
		if (duration > 0)
		{
			duration = soliniaSpell.getActSpellDuration(solEntity, duration);
		}
		
		return duration;
	}

	// Used for one off patching, added in /commit patch command for console sender
	public static void Patcher()  {
		
	}
	
	public static void disableLootOverLevel110()
	{
		int fixed = 0;
		try
		{
			for (ISoliniaLootDrop lootDrop : StateManager.getInstance().getConfigurationManager().getLootDrops())
			{
				for(ISoliniaLootDropEntry lootentry : lootDrop.getEntries())
				{
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(lootentry.getItemid());
					
					if (item == null)
						continue;
					
					if (!item.isSpellscroll())
						continue;
					
					if (item.getAbilityid() <= 0)
						continue;
					
					ISoliniaSpell spell = StateManager.getInstance().getConfigurationManager().getSpell(item.getAbilityid());
					
					boolean found = false;
					
					for(SoliniaSpellClass spellClass : spell.getAllowedClasses())
					{
						
						if (spellClass.getMinlevel() > 110)
						{
							found = true;
							break;
						}
					}
					
					if (found == false)
						continue;
					
					// We found an entry to remove
					
					item.setNeverDrop(true);
					fixed++;
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
		System.out.println("Disabled loot: " + fixed);
	}
	
	private static void genCultural()
	{
		int portableFurnaceId = 81542;
		int tailoringKitId = 84602;
		ISoliniaItem[] dustItems = new ISoliniaItem[13];
		try
		{
			for (int i = 1; i < 13; i++)
			{
				ISoliniaItem dustItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.BOOK), true);
				
				switch (i)
				{
					case 12:
						dustItem.setDisplayname("Holy Words of Manandu");
					break;
					case 11:
						dustItem.setDisplayname("Holy Words of Ingorir");
					break;
					case 10:
						dustItem.setDisplayname("Holy Words of Gias");
					break;
					case 9:
						dustItem.setDisplayname("Holy Words of Zephyr");
					break;
					case 8:
						dustItem.setDisplayname("Holy Words of Zurvan");
					break;
					case 7:
						dustItem.setDisplayname("Holy Words of Methabeht");
					break;
					case 6:
						dustItem.setDisplayname("Holy Words of Valhas");
					break;
					case 5:
						dustItem.setDisplayname("Holy Words of Tisroeh");
					break;
					case 4:
						dustItem.setDisplayname("Holy Words of Boralis");
					break;
					case 3:
						dustItem.setDisplayname("Holy Words of Irduolo");
					break;
					case 2:
						dustItem.setDisplayname("Holy Words of Lumyle");
						break;
					case 1:
						dustItem.setDisplayname("Holy Words of Harashi");
						break;
					default:
						dustItem.setDisplayname("Holy Words");
				}
				
				dustItems[i] = dustItem;
			}
			
			// Foreach race create an ore
			for (ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces())
			{
				try
				{
					ISoliniaItem oreItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_ORE), true);
					oreItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Ore");
	
					ISoliniaItem ingotItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_INGOT), true);
					ingotItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Ingot");
					
					ISoliniaItem leatherItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEATHER), true);
					leatherItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Raw Cattle Hide");
					
					ISoliniaItem preparedLeatherItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEATHER), true);
					preparedLeatherItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Prepared Cattle Leather");
					
					// Iron Molds
					// Head
					ISoliniaItem ironheadMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					ironheadMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					ironheadMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Helm Mold");
					ironheadMoldItem.setWorth(35);
					
					ISoliniaItem ironheadItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_HELMET), true);
					ironheadItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Helm");
					ironheadItem.setAC(4);
					ironheadItem.setAllowedClassNames(Arrays.asList("BARD", "CLERIC", "PALADIN", "SHADOWKNIGHT", "WARRIOR"));

					// Chest
					ISoliniaItem ironchestMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					ironchestMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					ironchestMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Chestplate Mold");
					ironchestMoldItem.setWorth(35);
					
					ISoliniaItem ironchestItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_CHESTPLATE), true);
					ironchestItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Chestplate");
					ironchestItem.setAC(4);
					ironchestItem.setAllowedClassNames(Arrays.asList("BARD", "CLERIC", "PALADIN", "SHADOWKNIGHT", "WARRIOR"));

					// Legs
					ISoliniaItem ironlegsMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					ironlegsMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					ironlegsMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Greaves Mold");
					ironlegsMoldItem.setWorth(35);
					
					ISoliniaItem ironlegsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_LEGGINGS), true);
					ironlegsItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Greaves");
					ironlegsItem.setAC(4);
					ironlegsItem.setAllowedClassNames(Arrays.asList("BARD", "CLERIC", "PALADIN", "SHADOWKNIGHT", "WARRIOR"));

					// Feet
					ISoliniaItem ironfeetMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					ironfeetMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					ironfeetMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Sabatons Mold");
					ironfeetMoldItem.setWorth(35);

					ISoliniaItem ironfeetItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_BOOTS), true);
					ironfeetItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Iron Sabatons");
					ironfeetItem.setAC(4);
					ironfeetItem.setAllowedClassNames(Arrays.asList("BARD", "CLERIC", "PALADIN", "SHADOWKNIGHT", "WARRIOR"));

					// Chainmail Molds
					// Head
					ISoliniaItem chainheadMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					chainheadMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					chainheadMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Chainmail Helm Mold");
					chainheadMoldItem.setWorth(35);
					
					ISoliniaItem chainheadItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CHAINMAIL_HELMET), true);
					chainheadItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Chainmail Helm");
					chainheadItem.setAC(3);
					chainheadItem.setAllowedClassNames(Arrays.asList("BERSERKER", "RANGER", "ROGUE", "SHAMAN"));

					// Chest
					ISoliniaItem chainchestMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					chainchestMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					chainchestMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Chainmail Chestplate Mold");
					chainchestMoldItem.setWorth(35);
					
					ISoliniaItem chainchestItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), true);
					chainchestItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Chainmail Chestplate");
					chainchestItem.setAC(3);
					chainchestItem.setAllowedClassNames(Arrays.asList("BERSERKER", "RANGER", "ROGUE", "SHAMAN"));

					// Legs
					ISoliniaItem chainlegsMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					chainlegsMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					chainlegsMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Chainmail Greaves Mold");
					chainlegsMoldItem.setWorth(35);
					
					ISoliniaItem chainlegsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CHAINMAIL_LEGGINGS), true);
					chainlegsItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Chainmail Greaves");
					chainlegsItem.setAC(3);
					chainlegsItem.setAllowedClassNames(Arrays.asList("BERSERKER", "RANGER", "ROGUE", "SHAMAN"));

					// Feet
					ISoliniaItem chainfeetMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					chainfeetMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					chainfeetMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Chainmail Sabatons Mold");					
					chainfeetMoldItem.setWorth(35);
					
					ISoliniaItem chainfeetItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CHAINMAIL_BOOTS), true);
					chainfeetItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Chainmail Sabatons");					
					chainfeetItem.setAC(3);
					chainfeetItem.setAllowedClassNames(Arrays.asList("BERSERKER", "RANGER", "ROGUE", "SHAMAN"));

					// Leather Molds
					// Head
					ISoliniaItem leatherheadMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					leatherheadMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					leatherheadMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leather Helmet Pattern");
					leatherheadMoldItem.setWorth(35);
					
					ISoliniaItem leatherheadItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEATHER_HELMET), true);
					leatherheadItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leather Helmet");
					leatherheadItem.setAC(2);
					leatherheadItem.setAllowedClassNames(Arrays.asList("BEASTLORD", "DRUID", "MONK", "ENCHANTER", "MAGICIAN", "NECROMANCER", "WIZARD"));

					// Chest
					ISoliniaItem leatherchestMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					leatherchestMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					leatherchestMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leather Tunic Pattern");
					leatherchestMoldItem.setWorth(35);
					
					ISoliniaItem leatherchestItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEATHER_CHESTPLATE), true);
					leatherchestItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leather Tunic");
					leatherchestItem.setAC(2);
					leatherchestItem.setAllowedClassNames(Arrays.asList("BEASTLORD", "DRUID", "MONK", "ENCHANTER", "MAGICIAN", "NECROMANCER", "WIZARD"));

					// Legs
					ISoliniaItem leatherlegsMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					leatherlegsMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					leatherlegsMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leather Leggings Pattern");
					leatherlegsMoldItem.setWorth(35);
					
					ISoliniaItem leatherlegsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEATHER_LEGGINGS), true);
					leatherlegsItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leather Leggings");
					leatherlegsItem.setAC(2);
					leatherlegsItem.setAllowedClassNames(Arrays.asList("BEASTLORD", "DRUID", "MONK", "ENCHANTER", "MAGICIAN", "NECROMANCER", "WIZARD"));

					// Feet
					ISoliniaItem leatherfeetMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					leatherfeetMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					leatherfeetMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leather Boots Pattern");					
					leatherfeetMoldItem.setWorth(35);
					
					ISoliniaItem leatherfeetItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEATHER_BOOTS), true);
					leatherfeetItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leather Boots");					
					leatherfeetItem.setAC(2);
					leatherfeetItem.setAllowedClassNames(Arrays.asList("BEASTLORD", "DRUID", "MONK", "ENCHANTER", "MAGICIAN", "NECROMANCER", "WIZARD"));

					// Equipment
					// Sword
					ISoliniaItem swordMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					swordMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					swordMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Sword Mold");
					swordMoldItem.setWorth(35);
					
					ISoliniaItem swordItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_SWORD), true);
					swordItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Sword");
					swordItem.setDamage(6);
					
					// Bow
					ISoliniaItem bowMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					bowMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					bowMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Bow Pattern");					
					bowMoldItem.setWorth(35);
					
					ISoliniaItem bowItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.BOW), true);
					bowItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Bow");					
					bowItem.setDamage(8);

					// Shield
					ISoliniaItem shieldMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					shieldMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					shieldMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Shield Mold");					
					shieldMoldItem.setWorth(35);
					
					ISoliniaItem shieldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.SHIELD), true);
					shieldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Shield");					
					shieldItem.setAC(5);
					
					// Staff
					ISoliniaItem staffMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_SHOVEL), true);
					staffMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					staffMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Staff Mold");					
					staffMoldItem.setWorth(35);
					
					ISoliniaItem staffItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_SHOVEL), true);
					staffItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Staff");					
					staffItem.setDamage(5);
					
					// Axe
					ISoliniaItem axeMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					axeMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					axeMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Axe Mold");					
					axeMoldItem.setWorth(35);
					
					ISoliniaItem axeItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.IRON_AXE), true);
					axeItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Axe");					
					axeItem.setDamage(7);

					// Earrings
					ISoliniaItem earringMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					earringMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					earringMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Earring Mold");	
					earringMoldItem.setWorth(35);
					
					ISoliniaItem earringItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					earringItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Earring");	
					earringItem.setEarsItem(true);
					earringItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFiYTc0ZDgxMmYzYzVlOTdhZDBmMWU2Y2IxZDI0ZmM5ZTEzNzg4MTk2Y2YxYmM0NzMyMTFmZjE0MmJlYWIifX19");
	
					// Necklace
					ISoliniaItem necklaceMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					necklaceMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					necklaceMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Necklace Mold");	
					necklaceMoldItem.setWorth(35);
					
					ISoliniaItem necklaceItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					necklaceItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Necklace");	
					necklaceItem.setNeckItem(true);
					necklaceItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRhYjc3ZWVmYWQwYjBjZGJkZjMyNjFhN2E0NzI5ZDU1MDRkNmY5NmQzYzE2MjgzMjE5NzQ0M2ViZTM0NmU2In19fQ==");
					
					// Cloak
					ISoliniaItem cloakMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					cloakMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					cloakMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Cloak Pattern");	
					cloakMoldItem.setWorth(35);
					
					ISoliniaItem cloakItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					cloakItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Cloak");	
					cloakItem.setShouldersItem(true);
					cloakItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFjYTdjZWY3YmMyOTI3ZWI5NGQ0YTY5MGE0MTQ4YTIxNDk4MjJlM2E2MGMwNjExYWEyYTNhNjUzM2I3NzE1In19fQ==");

					// Rings
					ISoliniaItem ringMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					ringMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					ringMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Ring Mold");	
					ringMoldItem.setWorth(35);
					
					ISoliniaItem ringItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					ringItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Ring");	
					ringItem.setFingersItem(true);
					ringItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE4M2M4OGRiOTg0MjZjNjRjMzdlNmQ3ODlkNGVjMWUzZGU0M2VmYWFmZTRiZTE2MTk2MWVmOTQzZGJlODMifX19");
					
					// Weapon aug
					ISoliniaItem weaponcharmMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					weaponcharmMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					weaponcharmMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Weapon Charm Pattern");	
					weaponcharmMoldItem.setWorth(35);
					
					ISoliniaItem weaponcharmItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CLOCK), true);
					weaponcharmItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Weapon Charm");	
					weaponcharmItem.setAugmentation(true);
					weaponcharmItem.setAugmentationFitsSlotType(AugmentationSlotType.WEAPON);
					
					// Helmet aug
					ISoliniaItem hatcharmMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					hatcharmMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					hatcharmMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Hat Charm Pattern");	
					hatcharmMoldItem.setWorth(35);
					
					ISoliniaItem hatcharmItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CLOCK), true);
					hatcharmItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Hat Charm");	
					hatcharmItem.setAugmentation(true);
					hatcharmItem.setAugmentationFitsSlotType(AugmentationSlotType.HELMET);
					
					// Chestplate aug
					ISoliniaItem tuniccharmMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					tuniccharmMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					tuniccharmMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Tunic Charm Pattern");	
					tuniccharmMoldItem.setWorth(35);
					
					ISoliniaItem tuniccharmItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CLOCK), true);
					tuniccharmItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Tunic Charm");	
					tuniccharmItem.setAugmentation(true);
					tuniccharmItem.setAugmentationFitsSlotType(AugmentationSlotType.CHESTPLATE);
					
					// Leggings aug
					ISoliniaItem leggingscharmMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					leggingscharmMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					leggingscharmMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leggings Charm Pattern");	
					leggingscharmMoldItem.setWorth(35);
					
					ISoliniaItem leggingscharmItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CLOCK), true);
					leggingscharmItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Leggings Charm");	
					leggingscharmItem.setAugmentation(true);
					leggingscharmItem.setAugmentationFitsSlotType(AugmentationSlotType.LEGGINGS);
					
					// Boots aug
					ISoliniaItem bootscharmMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					bootscharmMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					bootscharmMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Boots Charm Pattern");	
					bootscharmMoldItem.setWorth(35);
					
					ISoliniaItem bootscharmItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CLOCK), true);
					bootscharmItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Boots Charm");	
					bootscharmItem.setAugmentation(true);
					bootscharmItem.setAugmentationFitsSlotType(AugmentationSlotType.BOOTS);
					
					// Shield aug
					ISoliniaItem shieldcharmMoldItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.LEGACY_SKULL_ITEM), true);
					shieldcharmMoldItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc2YWQ5ZmY3ZDYwNmYzMWFkYjYyNGIxNDk2ZjY3ZWI2ZDI2OTk0NGUxNDcwNTJlNTdlNDg3NDFiMTQ4MmE0In19fQ==");
					shieldcharmMoldItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Shield Charm Pattern");	
					shieldcharmMoldItem.setWorth(35);
					
					ISoliniaItem shieldcharmItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.CLOCK), true);
					shieldcharmItem.setDisplayname(Utils.CapitaliseFirstLetter(race.getName()) + " Shield Charm");	
					shieldcharmItem.setAugmentation(true);
					shieldcharmItem.setAugmentationFitsSlotType(AugmentationSlotType.SHIELD);
					
					// Create smelting recipe
					try {
						SoliniaCraft ingotRecipe = SoliniaCraftFactory.Create(ingotItem.getDisplayname().replace(" ", "_").toUpperCase(), oreItem.getId(), portableFurnaceId, ingotItem.getId(), true);
						ingotRecipe.setSkill("BLACKSMITHING");
						SoliniaCraft leatherRecipe = SoliniaCraftFactory.Create(preparedLeatherItem.getDisplayname().replace(" ", "_").toUpperCase(), leatherItem.getId(), tailoringKitId, preparedLeatherItem.getId(), true);
						leatherRecipe.setSkill("TAILORING");
						
						// Iron Molds
						SoliniaCraft ironheadRecipe = SoliniaCraftFactory.Create(ironheadItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), ironheadMoldItem.getId(), ironheadItem.getId(), true);
						ironheadRecipe.setSkill("BLACKSMITHING");
						SoliniaCraft ironchestRecipe = SoliniaCraftFactory.Create(ironchestItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), ironchestMoldItem.getId(), ironchestItem.getId(), true);
						ironchestRecipe.setSkill("BLACKSMITHING");
						SoliniaCraft ironlegsRecipe = SoliniaCraftFactory.Create(ironlegsItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), ironlegsMoldItem.getId(), ironlegsItem.getId(), true);
						ironlegsRecipe.setSkill("BLACKSMITHING");
						SoliniaCraft ironfeetRecipe = SoliniaCraftFactory.Create(ironfeetItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), ironfeetMoldItem.getId(), ironfeetItem.getId(), true);
						ironfeetRecipe.setSkill("BLACKSMITHING");

						// Chainmail Molds
						SoliniaCraft chainheadRecipe = SoliniaCraftFactory.Create(chainheadItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), chainheadMoldItem.getId(), chainheadItem.getId(), true);
						chainheadRecipe.setSkill("BLACKSMITHING");
						SoliniaCraft chainchestRecipe = SoliniaCraftFactory.Create(chainchestItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), chainchestMoldItem.getId(), chainchestItem.getId(), true);
						chainchestRecipe.setSkill("BLACKSMITHING");
						SoliniaCraft chainlegsRecipe = SoliniaCraftFactory.Create(chainlegsItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), chainlegsMoldItem.getId(), chainlegsItem.getId(), true);
						chainlegsRecipe.setSkill("BLACKSMITHING");
						SoliniaCraft chainfeetRecipe = SoliniaCraftFactory.Create(chainfeetItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), chainfeetMoldItem.getId(), chainfeetItem.getId(), true);
						chainfeetRecipe.setSkill("BLACKSMITHING");

						// Leather Molds
						SoliniaCraft leatherheadRecipe = SoliniaCraftFactory.Create(leatherheadItem.getDisplayname().replace(" ", "_").toUpperCase(), preparedLeatherItem.getId(), leatherheadMoldItem.getId(), leatherheadItem.getId(), true);
						leatherheadRecipe.setSkill("TAILORING");
						SoliniaCraft leatherchestRecipe = SoliniaCraftFactory.Create(leatherchestItem.getDisplayname().replace(" ", "_").toUpperCase(), preparedLeatherItem.getId(), leatherchestMoldItem.getId(), leatherchestItem.getId(), true);
						leatherchestRecipe.setSkill("TAILORING");
						SoliniaCraft leatherlegsRecipe = SoliniaCraftFactory.Create(leatherlegsItem.getDisplayname().replace(" ", "_").toUpperCase(), preparedLeatherItem.getId(), leatherlegsMoldItem.getId(), leatherlegsItem.getId(), true);
						leatherlegsRecipe.setSkill("TAILORING");
						SoliniaCraft leatherfeetRecipe = SoliniaCraftFactory.Create(leatherfeetItem.getDisplayname().replace(" ", "_").toUpperCase(), preparedLeatherItem.getId(), leatherfeetMoldItem.getId(), leatherfeetItem.getId(), true);
						leatherfeetRecipe.setSkill("TAILORING");
						
						// Equipment
						// Sword
						SoliniaCraft swordRecipe = SoliniaCraftFactory.Create(swordItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), swordMoldItem.getId(), swordItem.getId(), true);
						swordRecipe.setSkill("BLACKSMITHING");
						// Bow
						SoliniaCraft bowRecipe = SoliniaCraftFactory.Create(bowItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), bowMoldItem.getId(), bowItem.getId(), true);
						bowRecipe.setSkill("BLACKSMITHING");
						// Shield
						SoliniaCraft shieldRecipe = SoliniaCraftFactory.Create(shieldItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), shieldMoldItem.getId(), shieldItem.getId(), true);
						shieldRecipe.setSkill("BLACKSMITHING");
						// Staff
						SoliniaCraft staffRecipe = SoliniaCraftFactory.Create(staffItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), staffMoldItem.getId(), staffItem.getId(), true);
						staffRecipe.setSkill("BLACKSMITHING");
						// Axe
						SoliniaCraft axeRecipe = SoliniaCraftFactory.Create(axeItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), axeMoldItem.getId(), axeItem.getId(), true);
						axeRecipe.setSkill("BLACKSMITHING");
						// Earrings
						SoliniaCraft earringRecipe = SoliniaCraftFactory.Create(earringItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), earringMoldItem.getId(), earringItem.getId(), true);
						earringRecipe.setSkill("JEWELRYMAKING");
						// Necklace
						SoliniaCraft necklaceRecipe = SoliniaCraftFactory.Create(necklaceItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), necklaceMoldItem.getId(), necklaceItem.getId(), true);
						necklaceRecipe.setSkill("JEWELRYMAKING");
						// Cloak
						SoliniaCraft cloakRecipe = SoliniaCraftFactory.Create(cloakItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), cloakMoldItem.getId(), cloakItem.getId(), true);
						cloakRecipe.setSkill("TAILORING");
						// Rings
						SoliniaCraft ringRecipe = SoliniaCraftFactory.Create(ringItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), ringMoldItem.getId(), ringItem.getId(), true);
						ringRecipe.setSkill("JEWELRYMAKING");
						// Weapon aug
						SoliniaCraft weaponcharmRecipe = SoliniaCraftFactory.Create(weaponcharmItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), weaponcharmMoldItem.getId(), weaponcharmItem.getId(), true);
						weaponcharmRecipe.setSkill("TINKERING");
						// Helmet aug
						SoliniaCraft hatcharmRecipe = SoliniaCraftFactory.Create(hatcharmItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), hatcharmMoldItem.getId(), hatcharmItem.getId(), true);
						hatcharmRecipe.setSkill("TINKERING");
						// Chestplate aug
						SoliniaCraft tuniccharmRecipe = SoliniaCraftFactory.Create(tuniccharmItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), tuniccharmMoldItem.getId(), tuniccharmItem.getId(), true);
						tuniccharmRecipe.setSkill("TINKERING");
						// Leggings aug
						SoliniaCraft leggingscharmRecipe = SoliniaCraftFactory.Create(leggingscharmItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), leggingscharmMoldItem.getId(), leggingscharmItem.getId(), true);
						leggingscharmRecipe.setSkill("TINKERING");
						// Boots aug
						SoliniaCraft bootscharmRecipe = SoliniaCraftFactory.Create(bootscharmItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), bootscharmMoldItem.getId(), bootscharmItem.getId(), true);
						bootscharmRecipe.setSkill("TINKERING");
						// Shield aug
						SoliniaCraft shieldcharmRecipe = SoliniaCraftFactory.Create(shieldcharmItem.getDisplayname().replace(" ", "_").toUpperCase(), ingotItem.getId(), shieldcharmMoldItem.getId(), shieldcharmItem.getId(), true);
						shieldcharmRecipe.setSkill("TINKERING");
						
						SoliniaNPCMerchantFactory.CreateNPCMerchant(race.getName().toUpperCase() + "_" + "CULTURAL", true);
						ISoliniaNPCMerchant merchantList = StateManager.getInstance().getConfigurationManager().getNPCMerchant(race.getName().toUpperCase() + "_" + "CULTURAL");
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), portableFurnaceId, true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), tailoringKitId, true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), ironheadMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), ironchestMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), ironlegsMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), ironfeetMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), chainheadMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), chainchestMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), chainlegsMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), chainfeetMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), leatherheadMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), leatherchestMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), leatherlegsMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), leatherfeetMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), swordMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), bowMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), shieldMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), staffMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), axeMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), earringMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), necklaceMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), cloakMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), ringMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), weaponcharmMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), hatcharmMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), tuniccharmMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), leggingscharmMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), bootscharmMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), shieldcharmMoldItem.getId(), true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), portableFurnaceId, true);
						SoliniaNPCMerchantFactory.AddNPCMerchantItem(merchantList.getId(), tailoringKitId, true);
						
						// Generate the actual items people want
						for (int i = 1; i < 13; i++)
						{
							int tier = i;
							ISoliniaItem tierItem = dustItems[i];
							String tierPrefix = "";
							switch (i)
							{
								case 12:
									tierPrefix = "Manandu Blessed ";
								break;
								case 11:
									tierPrefix = "Ingorir Blessed ";
								break;
								case 10:
									tierPrefix = "Gias Blessed ";
								break;
								case 9:
									tierPrefix = "Zephyr Blessed ";
								break;
								case 8:
									tierPrefix = "Zurvan Blessed ";
								break;
								case 7:
									tierPrefix = "Methabeht Blessed "; //60 - 70
								break;
								case 6:
									tierPrefix = "Valhas Blessed "; //50 - 60
								break;
								case 5:
									tierPrefix = "Tisroeh Blessed ";// 40 - 50 
								break;
								case 4:
									tierPrefix = "Boralis Blessed "; //30 - 40
								break;
								case 3:
									tierPrefix = "Irduolo Blessed ";// 20 - 30
								break;
								case 2:
									tierPrefix = "Lumyle Blessed "; // 10 - 20
									break;
								case 1:
									tierPrefix = "Harashi Blessed "; // 0 - 10
									break;
								default:
									tierPrefix = "Blessed ";
							}						
							
							int tierMinSkill = i * 25;
							
							// Iron Molds
							ISoliniaItem ironheadItemT = SoliniaItemFactory.CreateItemCopy(ironheadItem, true);
							ironheadItemT.setDisplayname(tierPrefix + ironheadItemT.getDisplayname());
							culturaliseItem(tier,race,ironheadItemT);
							SoliniaCraft ironheadRecipeT = SoliniaCraftFactory.Create(ironheadItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), ironheadItem.getId(), ironheadItemT.getId(), true);
							ironheadRecipeT.setSkill("BLACKSMITHING");
							ironheadRecipeT.setMinSkill(tierMinSkill);

							ISoliniaItem ironchestItemT = SoliniaItemFactory.CreateItemCopy(ironchestItem, true);
							ironchestItemT.setDisplayname(tierPrefix + ironchestItemT.getDisplayname());
							culturaliseItem(tier,race,ironchestItemT);
							SoliniaCraft ironchestRecipeT = SoliniaCraftFactory.Create(ironchestItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), ironchestItem.getId(), ironchestItemT.getId(), true);
							ironchestRecipeT.setSkill("BLACKSMITHING");
							ironchestRecipeT.setMinSkill(tierMinSkill);
							
							ISoliniaItem ironlegsItemT = SoliniaItemFactory.CreateItemCopy(ironlegsItem, true);
							ironlegsItemT.setDisplayname(tierPrefix + ironlegsItemT.getDisplayname());
							culturaliseItem(tier,race,ironlegsItemT);
							SoliniaCraft ironlegsRecipeT = SoliniaCraftFactory.Create(ironlegsItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), ironlegsItem.getId(), ironlegsItemT.getId(), true);
							ironlegsRecipeT.setSkill("BLACKSMITHING");
							ironlegsRecipeT.setMinSkill(tierMinSkill);
							
							ISoliniaItem ironfeetItemT = SoliniaItemFactory.CreateItemCopy(ironfeetItem, true);
							ironfeetItemT.setDisplayname(tierPrefix + ironfeetItemT.getDisplayname());
							culturaliseItem(tier,race,ironfeetItemT);
							SoliniaCraft ironfeetRecipeT = SoliniaCraftFactory.Create(ironfeetItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), ironfeetItem.getId(), ironfeetItemT.getId(), true);
							ironfeetRecipeT.setSkill("BLACKSMITHING");
							ironfeetRecipeT.setMinSkill(tierMinSkill);
							
							// Chainmail Molds
							ISoliniaItem chainheadItemT = SoliniaItemFactory.CreateItemCopy(chainheadItem, true);
							chainheadItemT.setDisplayname(tierPrefix + chainheadItemT.getDisplayname());
							culturaliseItem(tier,race,chainheadItemT);
							SoliniaCraft chainheadRecipeT = SoliniaCraftFactory.Create(chainheadItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), chainheadItem.getId(), chainheadItemT.getId(), true);
							chainheadRecipeT.setSkill("BLACKSMITHING");
							chainheadRecipeT.setMinSkill(tierMinSkill);
							
							ISoliniaItem chainchestItemT = SoliniaItemFactory.CreateItemCopy(chainchestItem, true);
							chainchestItemT.setDisplayname(tierPrefix + chainchestItemT.getDisplayname());
							culturaliseItem(tier,race,chainchestItemT);
							SoliniaCraft chainchestRecipeT = SoliniaCraftFactory.Create(chainchestItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), chainchestItem.getId(), chainchestItemT.getId(), true);
							chainchestRecipeT.setSkill("BLACKSMITHING");
							chainchestRecipeT.setMinSkill(tierMinSkill);
							
							ISoliniaItem chainlegsItemT = SoliniaItemFactory.CreateItemCopy(chainlegsItem, true);
							chainlegsItemT.setDisplayname(tierPrefix + chainlegsItemT.getDisplayname());
							culturaliseItem(tier,race,chainlegsItemT);
							SoliniaCraft chainlegsRecipeT = SoliniaCraftFactory.Create(chainlegsItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), chainlegsItem.getId(), chainlegsItemT.getId(), true);
							chainlegsRecipeT.setSkill("BLACKSMITHING");
							chainlegsRecipeT.setMinSkill(tierMinSkill);
							
							ISoliniaItem chainfeetItemT = SoliniaItemFactory.CreateItemCopy(chainfeetItem, true);
							chainfeetItemT.setDisplayname(tierPrefix + chainfeetItemT.getDisplayname());
							culturaliseItem(tier,race,chainfeetItemT);
							SoliniaCraft chainfeetRecipeT = SoliniaCraftFactory.Create(chainfeetItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), chainfeetItem.getId(), chainfeetItemT.getId(), true);
							chainfeetRecipeT.setSkill("BLACKSMITHING");
							chainfeetRecipeT.setMinSkill(tierMinSkill);
							
							// Leather Molds
							ISoliniaItem leatherheadItemT = SoliniaItemFactory.CreateItemCopy(leatherheadItem, true);
							leatherheadItemT.setDisplayname(tierPrefix + leatherheadItemT.getDisplayname());
							culturaliseItem(tier,race,leatherheadItemT);
							SoliniaCraft leatherheadRecipeT = SoliniaCraftFactory.Create(leatherheadItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), leatherheadItem.getId(), leatherheadItemT.getId(), true);
							leatherheadRecipeT.setSkill("TAILORING");
							leatherheadRecipeT.setMinSkill(tierMinSkill);
							
							ISoliniaItem leatherchestItemT = SoliniaItemFactory.CreateItemCopy(leatherchestItem, true);
							leatherchestItemT.setDisplayname(tierPrefix + leatherchestItemT.getDisplayname());
							culturaliseItem(tier,race,leatherchestItemT);
							SoliniaCraft leatherchestRecipeT = SoliniaCraftFactory.Create(leatherchestItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), leatherchestItem.getId(), leatherchestItemT.getId(), true);
							leatherchestRecipeT.setSkill("TAILORING");
							leatherchestRecipeT.setMinSkill(tierMinSkill);
							
							ISoliniaItem leatherlegsItemT = SoliniaItemFactory.CreateItemCopy(leatherlegsItem, true);
							leatherlegsItemT.setDisplayname(tierPrefix + leatherlegsItemT.getDisplayname());
							culturaliseItem(tier,race,leatherlegsItemT);
							SoliniaCraft leatherlegsRecipeT = SoliniaCraftFactory.Create(leatherlegsItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), leatherlegsItem.getId(), leatherlegsItemT.getId(), true);
							leatherlegsRecipeT.setSkill("TAILORING");
							leatherlegsRecipeT.setMinSkill(tierMinSkill);

							ISoliniaItem leatherfeetItemT = SoliniaItemFactory.CreateItemCopy(leatherfeetItem, true);
							leatherfeetItemT.setDisplayname(tierPrefix + leatherfeetItemT.getDisplayname());
							culturaliseItem(tier,race,leatherfeetItemT);
							SoliniaCraft leatherfeetRecipeT = SoliniaCraftFactory.Create(leatherfeetItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), leatherfeetItem.getId(), leatherfeetItemT.getId(), true);
							leatherfeetRecipeT.setSkill("TAILORING");
							leatherfeetRecipeT.setMinSkill(tierMinSkill);
							
							// Equipment
							// Sword
							ISoliniaItem swordItemT = SoliniaItemFactory.CreateItemCopy(swordItem, true);
							swordItemT.setDisplayname(tierPrefix + swordItemT.getDisplayname());
							culturaliseItem(tier,race,swordItemT);
							SoliniaCraft swordRecipeT = SoliniaCraftFactory.Create(swordItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), swordItem.getId(), swordItemT.getId(), true);
							swordRecipeT.setSkill("BLACKSMITHING");
							swordRecipeT.setMinSkill(tierMinSkill);
							// Bow
							ISoliniaItem bowItemT = SoliniaItemFactory.CreateItemCopy(bowItem, true);
							bowItemT.setDisplayname(tierPrefix + bowItemT.getDisplayname());
							culturaliseItem(tier,race,bowItemT);
							SoliniaCraft bowRecipeT = SoliniaCraftFactory.Create(bowItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), bowItem.getId(), bowItemT.getId(), true);
							bowRecipeT.setSkill("BLACKSMITHING");
							bowRecipeT.setMinSkill(tierMinSkill);
							// Shield
							ISoliniaItem shieldItemT = SoliniaItemFactory.CreateItemCopy(shieldItem, true);
							shieldItemT.setDisplayname(tierPrefix + shieldItemT.getDisplayname());
							culturaliseItem(tier,race,shieldItemT);
							SoliniaCraft shieldRecipeT = SoliniaCraftFactory.Create(shieldItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), shieldItem.getId(), shieldItemT.getId(), true);
							shieldRecipeT.setSkill("BLACKSMITHING");
							shieldRecipeT.setMinSkill(tierMinSkill);
							// Staff
							ISoliniaItem staffItemT = SoliniaItemFactory.CreateItemCopy(staffItem, true);
							staffItemT.setDisplayname(tierPrefix + staffItemT.getDisplayname());
							culturaliseItem(tier,race,staffItemT);
							SoliniaCraft staffRecipeT = SoliniaCraftFactory.Create(staffItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), staffItem.getId(), staffItemT.getId(), true);
							staffRecipeT.setSkill("BLACKSMITHING");
							staffRecipeT.setMinSkill(tierMinSkill);
							// Axe
							ISoliniaItem axeItemT = SoliniaItemFactory.CreateItemCopy(axeItem, true);
							axeItemT.setDisplayname(tierPrefix + axeItemT.getDisplayname());
							culturaliseItem(tier,race,axeItemT);
							SoliniaCraft axeRecipeT = SoliniaCraftFactory.Create(axeItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), axeItem.getId(), axeItemT.getId(), true);
							axeRecipeT.setSkill("BLACKSMITHING");
							axeRecipeT.setMinSkill(tierMinSkill);
							// Earrings
							ISoliniaItem earringItemT = SoliniaItemFactory.CreateItemCopy(earringItem, true);
							earringItemT.setDisplayname(tierPrefix + earringItemT.getDisplayname());
							culturaliseItem(tier,race,earringItemT);
							SoliniaCraft earringRecipeT = SoliniaCraftFactory.Create(earringItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), earringItem.getId(), earringItemT.getId(), true);
							earringRecipeT.setSkill("JEWELRYMAKING");
							earringRecipeT.setMinSkill(tierMinSkill);
							// Necklace
							ISoliniaItem necklaceItemT = SoliniaItemFactory.CreateItemCopy(necklaceItem, true);
							necklaceItemT.setDisplayname(tierPrefix + necklaceItemT.getDisplayname());
							culturaliseItem(tier,race,necklaceItemT);
							SoliniaCraft necklaceRecipeT = SoliniaCraftFactory.Create(necklaceItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), necklaceItem.getId(), necklaceItemT.getId(), true);
							necklaceRecipeT.setSkill("JEWELRYMAKING");
							necklaceRecipeT.setMinSkill(tierMinSkill);
							// Cloak
							ISoliniaItem cloakItemT = SoliniaItemFactory.CreateItemCopy(cloakItem, true);
							cloakItemT.setDisplayname(tierPrefix + cloakItemT.getDisplayname());
							culturaliseItem(tier,race,cloakItemT);
							SoliniaCraft cloakRecipeT = SoliniaCraftFactory.Create(cloakItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), cloakItem.getId(), cloakItemT.getId(), true);
							cloakRecipeT.setSkill("TAILORING");
							cloakRecipeT.setMinSkill(tierMinSkill);
							// Rings
							ISoliniaItem ringItemT = SoliniaItemFactory.CreateItemCopy(ringItem, true);
							ringItemT.setDisplayname(tierPrefix + ringItemT.getDisplayname());
							culturaliseItem(tier,race,ringItemT);
							SoliniaCraft ringRecipeT = SoliniaCraftFactory.Create(ringItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), ringItem.getId(), ringItemT.getId(), true);
							ringRecipeT.setSkill("JEWELRYMAKING");
							ringRecipeT.setMinSkill(tierMinSkill);
							// Weapon aug
							ISoliniaItem weaponcharmItemT = SoliniaItemFactory.CreateItemCopy(weaponcharmItem, true);
							weaponcharmItemT.setDisplayname(tierPrefix + weaponcharmItemT.getDisplayname());
							culturaliseItem(tier,race,weaponcharmItemT);
							SoliniaCraft weaponcharmRecipeT = SoliniaCraftFactory.Create(weaponcharmItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), weaponcharmItem.getId(), weaponcharmItemT.getId(), true);
							weaponcharmRecipeT.setSkill("TINKERING");
							weaponcharmRecipeT.setMinSkill(tierMinSkill);
							// Helmet aug
							ISoliniaItem hatcharmItemT = SoliniaItemFactory.CreateItemCopy(hatcharmItem, true);
							hatcharmItemT.setDisplayname(tierPrefix + hatcharmItemT.getDisplayname());
							culturaliseItem(tier,race,hatcharmItemT);
							SoliniaCraft hatcharmRecipeT = SoliniaCraftFactory.Create(hatcharmItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), hatcharmItem.getId(), hatcharmItemT.getId(), true);
							hatcharmRecipeT.setSkill("TINKERING");
							hatcharmRecipeT.setMinSkill(tierMinSkill);
							// Chestplate aug
							ISoliniaItem tuniccharmItemT = SoliniaItemFactory.CreateItemCopy(tuniccharmItem, true);
							tuniccharmItemT.setDisplayname(tierPrefix + tuniccharmItemT.getDisplayname());
							culturaliseItem(tier,race,tuniccharmItemT);
							SoliniaCraft tuniccharmRecipeT = SoliniaCraftFactory.Create(tuniccharmItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), tuniccharmItem.getId(), tuniccharmItemT.getId(), true);
							tuniccharmRecipeT.setSkill("TINKERING");
							tuniccharmRecipeT.setMinSkill(tierMinSkill);
							// Leggings aug
							ISoliniaItem leggingscharmItemT = SoliniaItemFactory.CreateItemCopy(leggingscharmItem, true);
							leggingscharmItemT.setDisplayname(tierPrefix + leggingscharmItemT.getDisplayname());
							culturaliseItem(tier,race,leggingscharmItemT);
							SoliniaCraft leggingscharmRecipeT = SoliniaCraftFactory.Create(leggingscharmItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), leggingscharmItem.getId(), leggingscharmItemT.getId(), true);
							leggingscharmRecipeT.setSkill("TINKERING");
							leggingscharmRecipeT.setMinSkill(tierMinSkill);
							// Boots aug
							ISoliniaItem bootscharmItemT = SoliniaItemFactory.CreateItemCopy(bootscharmItem, true);
							bootscharmItemT.setDisplayname(tierPrefix + bootscharmItemT.getDisplayname());
							culturaliseItem(tier,race,bootscharmItemT);
							SoliniaCraft bootscharmRecipeT = SoliniaCraftFactory.Create(bootscharmItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), bootscharmItem.getId(), bootscharmItemT.getId(), true);
							bootscharmRecipeT.setSkill("TINKERING");
							bootscharmRecipeT.setMinSkill(tierMinSkill);
							// Shield aug
							ISoliniaItem shieldcharmItemT = SoliniaItemFactory.CreateItemCopy(shieldcharmItem, true);
							shieldcharmItemT.setDisplayname(tierPrefix + shieldcharmItemT.getDisplayname());
							culturaliseItem(tier,race,shieldcharmItemT);
							SoliniaCraft shieldcharmRecipeT = SoliniaCraftFactory.Create(shieldcharmItemT.getDisplayname().replace(" ", "_").toUpperCase(), tierItem.getId(), shieldcharmItem.getId(), shieldcharmItemT.getId(), true);
							shieldcharmRecipeT.setSkill("TINKERING");
							shieldcharmRecipeT.setMinSkill(tierMinSkill);
						}
						
					} catch (SoliniaCraftCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (SoliniaItemException e)
				{
					
				}
			}
		} catch (CoreStateInitException | SoliniaItemException e)
		{
			
		}
	}

	private static void culturaliseItem(int tier, ISoliniaRace race, ISoliniaItem item) {
		int strength = (((race.getStrength() /2)-15)/7)*tier;
		int stamina = (((race.getStamina() /2)-15)/7)*tier;
		int agility = (((race.getAgility() /2)-15)/7)*tier;
		int dexterity = (((race.getDexterity() /2)-15)/7)*tier;
		int intelligence = (((race.getIntelligence() /2)-15)/7)*tier;
		int wisdom = (((race.getWisdom() /2)-15)/7)*tier;
		int charisma = (((race.getCharisma() /2)-15)/7)*tier;
		
		if (strength > 0)
		{
			item.setStrength(strength + Utils.RandomBetween(0, 7));
		}
		if (stamina > 0)
		{
			item.setStamina(stamina + Utils.RandomBetween(0, 7));
		}
		if (agility > 0)
		{
			item.setAgility(agility + Utils.RandomBetween(0, 7));
		}
		if (dexterity > 0)
		{
			item.setDexterity(dexterity + Utils.RandomBetween(0, 7));
		}
		if (intelligence > 0)
		{
			item.setIntelligence(intelligence + Utils.RandomBetween(0, 7));
		}
		if (wisdom > 0)
		{
			item.setWisdom(wisdom + Utils.RandomBetween(0, 7));
		}
		if (charisma > 0)
		{
			item.setCharisma(charisma + Utils.RandomBetween(0, 7));
		}
		
		item.setMana((stamina*3)+ Utils.RandomBetween(0, 7));
		item.setHp((intelligence*3)+ Utils.RandomBetween(0, 7));
		
		int damageBonus = ((strength/tier)/2) + Utils.RandomBetween(1, 3);
		int acBonus = ((stamina/tier)/2) + Utils.RandomBetween(1, 3);
		
		int baseAmount = SoliniaItemFactory.getBaseAmount(item);
		
		SoliniaItemFactory.setMinLevel(item, tier, baseAmount, false);
		SoliniaItemFactory.setItemDamageAndAc(item, tier, SoliniaItemFactory.getTierMin(item, tier, baseAmount), SoliniaItemFactory.getTierMax(item, tier, baseAmount), acBonus, 0, damageBonus);
	}

	public static int convertRawClassToClass(int rawClassId) {
		switch (rawClassId) {
		case 1: // war
			return 1;
		case 2: // cle
			return 2;
		case 3: // pal
			return 6;
		case 4: // rng
			return 3;
		case 5: // shd
			return 7;
		case 6: // dru
			return 9;
		case 7: // mnk
			return 12;
		case 8: // brd
			return 10;
		case 9: // rog
			return 4;
		case 10: // shm
			return 8;
		case 11: // nec
			return 13;
		case 12: // wiz
			return 5;
		case 13: // mge
			return 11;
		case 14: // enc
			return 14;
		default:
			return 0;
		}
	}

	private static void patchNpcClasses() {
		try {
			for (ISoliniaPatch patch : StateManager.getInstance().getConfigurationManager().getPatches()) {
				// Lookup npc and edit class
				String npcName = patch.getClasses().get(0);
				int rawClassId = Integer.parseInt(patch.getClasses().get(1));
				int convertedClass = convertRawClassToClass(rawClassId);

				ISoliniaNPC npc = StateManager.getInstance().getConfigurationManager().getPetNPCByName(npcName);
				if (npc == null)
					continue;

				npc.setClassid(convertedClass);
				System.out.println("Updated NPC: " + npc.getName() + " to class " + convertedClass);
			}
		} catch (CoreStateInitException e) {
			// skip
		}
	}

	public static boolean isLivingEntityNPC(LivingEntity livingentity) {
		String metaid = "";
		for (MetadataValue val : livingentity.getMetadata("mobname")) {
			metaid = val.asString();
		}

		for (MetadataValue val : livingentity.getMetadata("npcid")) {
			metaid = val.asString();
		}

		if (metaid.equals(""))
			return false;

		if (!metaid.contains("NPCID_"))
			return false;

		return true;
	}

	public static SpellResistType getSpellResistType(Integer resisttype) {
		switch (resisttype) {
		case 0:
			return SpellResistType.RESIST_NONE;
		case 1:
			return SpellResistType.RESIST_MAGIC;
		case 2:
			return SpellResistType.RESIST_FIRE;
		case 3:
			return SpellResistType.RESIST_COLD;
		case 4:
			return SpellResistType.RESIST_POISON;
		case 5:
			return SpellResistType.RESIST_DISEASE;
		case 6:
			return SpellResistType.RESIST_CHROMATIC;
		case 7:
			return SpellResistType.RESIST_PRISMATIC;
		case 8:
			return SpellResistType.RESIST_PHYSICAL;
		case 9:
			return SpellResistType.RESIST_CORRUPTION;
		default:
			return SpellResistType.RESIST_NONE;
		}
	}

	// Graphical effects
	public static SpellEffectIndex getSpellEffectIndex(int sai) {
		switch (sai) {
		case -1:
			return SpellEffectIndex.Summon_Mount_Unclass;
		case 0:
			return SpellEffectIndex.Direct_Damage;
		case 1:
			return SpellEffectIndex.Heal_Cure;
		case 2:
			return SpellEffectIndex.AC_Buff;
		case 3:
			return SpellEffectIndex.AE_Damage;
		case 4:
			return SpellEffectIndex.Summon;
		case 5:
			return SpellEffectIndex.Sight;
		case 6:
			return SpellEffectIndex.Mana_Regen_Resist_Song;
		case 7:
			return SpellEffectIndex.Stat_Buff;
		case 9:
			return SpellEffectIndex.Vanish;
		case 10:
			return SpellEffectIndex.Appearance;
		case 11:
			return SpellEffectIndex.Enchanter_Pet;
		case 12:
			return SpellEffectIndex.Calm;
		case 13:
			return SpellEffectIndex.Fear;
		case 14:
			return SpellEffectIndex.Dispell_Sight;
		case 15:
			return SpellEffectIndex.Stun;
		case 16:
			return SpellEffectIndex.Haste_Runspeed;
		case 17:
			return SpellEffectIndex.Combat_Slow;
		case 18:
			return SpellEffectIndex.Damage_Shield;
		case 19:
			return SpellEffectIndex.Cannibalize_Weapon_Proc;
		case 20:
			return SpellEffectIndex.Weaken;
		case 21:
			return SpellEffectIndex.Banish;
		case 22:
			return SpellEffectIndex.Blind_Poison;
		case 23:
			return SpellEffectIndex.Cold_DD;
		case 24:
			return SpellEffectIndex.Poison_Disease_DD;
		case 25:
			return SpellEffectIndex.Fire_DD;
		case 27:
			return SpellEffectIndex.Memory_Blur;
		case 28:
			return SpellEffectIndex.Gravity_Fling;
		case 29:
			return SpellEffectIndex.Suffocate;
		case 30:
			return SpellEffectIndex.Lifetap_Over_Time;
		case 31:
			return SpellEffectIndex.Fire_AE;
		case 33:
			return SpellEffectIndex.Cold_AE;
		case 34:
			return SpellEffectIndex.Poison_Disease_AE;
		case 40:
			return SpellEffectIndex.Teleport;
		case 41:
			return SpellEffectIndex.Direct_Damage_Song;
		case 42:
			return SpellEffectIndex.Combat_Buff_Song;
		case 43:
			return SpellEffectIndex.Calm_Song;
		case 45:
			return SpellEffectIndex.Firework;
		case 46:
			return SpellEffectIndex.Firework_AE;
		case 47:
			return SpellEffectIndex.Weather_Rocket;
		case 50:
			return SpellEffectIndex.Convert_Vitals;
		case 60:
			return SpellEffectIndex.NPC_Special_60;
		case 61:
			return SpellEffectIndex.NPC_Special_61;
		case 62:
			return SpellEffectIndex.NPC_Special_62;
		case 63:
			return SpellEffectIndex.NPC_Special_63;
		case 70:
			return SpellEffectIndex.NPC_Special_70;
		case 71:
			return SpellEffectIndex.NPC_Special_71;
		case 80:
			return SpellEffectIndex.NPC_Special_80;
		case 88:
			return SpellEffectIndex.Trap_Lock;
		}

		return null;
	}

	public static SpellEffectType getSpellEffectTypeFromResistType(SpellResistType type) {
		switch (type) {
		case RESIST_COLD:
			return SpellEffectType.ResistCold;
		case RESIST_FIRE:
			return SpellEffectType.ResistFire;
		case RESIST_POISON:
			return SpellEffectType.ResistPoison;
		case RESIST_DISEASE:
			return SpellEffectType.ResistDisease;
		case RESIST_MAGIC:
			return SpellEffectType.ResistMagic;
		case RESIST_CORRUPTION:
			return SpellEffectType.ResistCorruption;
		case RESIST_NONE:
			return null;
		default:
			return null;

		}
	}

	public static DisguisePackage getDisguiseTypeFromDisguiseId(int disguiseid) {
		switch (disguiseid) {
		case 1:
			return new DisguisePackage(DisguiseType.PLAYER, "human", "k0h"); // human
		case 2:
			return new DisguisePackage(DisguiseType.PLAYER, "barbarian", "Lemoh"); // barbarian
		case 3:
			return new DisguisePackage(DisguiseType.PLAYER, "highhuman", "hiccupwindwalker"); // high human
		case 4:
			return new DisguisePackage(DisguiseType.PLAYER, "woodelf", "Knogi"); // wood elf
		case 5:
			return new DisguisePackage(DisguiseType.PLAYER, "highelf", "MoistWater"); // highelf
		case 6:
			return new DisguisePackage(DisguiseType.PLAYER, "darkelf", "Kenzo74_"); // dark elf
		case 7:
			return new DisguisePackage(DisguiseType.PLAYER, "halfelf", "Maechu_"); // halfelf
		case 8:
			return new DisguisePackage(DisguiseType.PLAYER, "dwarf", "Faenon"); // dwarf
		case 9:
			return new DisguisePackage(DisguiseType.PLAYER, "troll", "Gronghk"); // troll
		case 10:
			return new DisguisePackage(DisguiseType.PLAYER, "ogre", "theorc"); // ogre
		case 11:
			return new DisguisePackage(DisguiseType.PLAYER, "halfling", "Yeus"); // halfling
		case 12:
			return new DisguisePackage(DisguiseType.PLAYER, "gnome", "Yeus"); // gnome
		case 13:
			return new DisguisePackage(DisguiseType.PLAYER, "aviak", "Titus_Vogel"); // aviak
		case 14:
			return new DisguisePackage(DisguiseType.PLAYER, "werewolf", "Apiii"); // werewolf
		case 15:
			return new DisguisePackage(DisguiseType.PLAYER, "Brownie");
		case 16:
			return new DisguisePackage(DisguiseType.PLAYER, "Centaur");
		case 17:
			return new DisguisePackage(DisguiseType.PLAYER, "katxxx");
		case 18:
			return new DisguisePackage(DisguiseType.GIANT, "Giant/Cyclops");
		case 19:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Poison Dragon");
		case 20:
			return new DisguisePackage(DisguiseType.PLAYER, "Doppleganger");
		case 21:
			return new DisguisePackage(DisguiseType.GUARDIAN, "Evil Eye");
		case 22:
			return new DisguisePackage(DisguiseType.SILVERFISH, "Beetle");
		case 23:
			return new DisguisePackage(DisguiseType.PLAYER, "Kerra");
		case 24:
			return new DisguisePackage(DisguiseType.GUARDIAN, "Fish");
		case 25:
			return new DisguisePackage(DisguiseType.VEX, "Fairy");
		case 26:
			return new DisguisePackage(DisguiseType.PLAYER, "Frog man");
		case 27:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman ghoul");
		case 28:
			return new DisguisePackage(DisguiseType.PLAYER, "Fungusman");
		case 29:
			return new DisguisePackage(DisguiseType.PLAYER, "Gargoyle");
		case 31:
			return new DisguisePackage(DisguiseType.SLIME, "Slime cube");
		case 32:
			return new DisguisePackage(DisguiseType.PLAYER, "ghost", "Orbitly"); // ghost
		case 33:
			return new DisguisePackage(DisguiseType.PLAYER, "Ghoul");
		case 34:
			return new DisguisePackage(DisguiseType.BAT, "Giant Bat");
		case 35:
			return new DisguisePackage(DisguiseType.GUARDIAN, "Giant Eel");
		case 36:
			return new DisguisePackage(DisguiseType.PLAYER, "Giant Rat");
		case 37:
			return new DisguisePackage(DisguiseType.PLAYER, "Giant Snake");
		case 38:
			return new DisguisePackage(DisguiseType.PLAYER, "Giant Spider");
		case 39:
			return new DisguisePackage(DisguiseType.PLAYER, "Gnoll");
		case 40:
			return new DisguisePackage(DisguiseType.PLAYER, "Goblin");
		case 41:
			return new DisguisePackage(DisguiseType.PLAYER, "Gorilla");
		case 42:
			return new DisguisePackage(DisguiseType.WOLF, "Wolf");
		case 43:
			return new DisguisePackage(DisguiseType.POLAR_BEAR, "Bear");
		case 44:
			return new DisguisePackage(DisguiseType.PLAYER, "Human Guards");
		case 45:
			return new DisguisePackage(DisguiseType.WITHER_SKELETON, "Demi Lich");
		case 46:
			return new DisguisePackage(DisguiseType.VEX, "Imp");
		case 47:
			return new DisguisePackage(DisguiseType.PLAYER, "Griffin");
		case 48:
			return new DisguisePackage(DisguiseType.PLAYER, "kobold", "PeterPugger");// kobold
		case 49:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Lava Dragon");
		case 50:
			return new DisguisePackage(DisguiseType.OCELOT, "Lion");
		case 51:
			return new DisguisePackage(DisguiseType.PLAYER, "Lizard Man");
		case 52:
			return new DisguisePackage(DisguiseType.PLAYER, "Mimic");
		case 53:
			return new DisguisePackage(DisguiseType.PLAYER, "minotaur", "_CrimsonBlade_"); // minotaur
		case 54:
			return new DisguisePackage(DisguiseType.PLAYER, "orc", "Jeeorc"); // orc
		case 55:
			return new DisguisePackage(DisguiseType.PLAYER, "Human Beggar");
		case 56:
			return new DisguisePackage(DisguiseType.PLAYER, "Pixie");
		case 57:
			return new DisguisePackage(DisguiseType.SPIDER, "half human spider");
		case 58:
			return new DisguisePackage(DisguiseType.PLAYER, "Fire prince");
		case 59:
			return new DisguisePackage(DisguiseType.PLAYER, "goblin", "AllOgreNow"); // goblin
		case 60:
			return new DisguisePackage(DisguiseType.SKELETON, "Skeleton");
		case 61:
			return new DisguisePackage(DisguiseType.PLAYER, "Shark");
		case 62:
			return new DisguisePackage(DisguiseType.PLAYER, "Elf priestess");
		case 63:
			return new DisguisePackage(DisguiseType.OCELOT, "Tiger");
		case 64:
			return new DisguisePackage(DisguiseType.PLAYER, "treant", "zero_kage"); // treant
		case 65:
			return new DisguisePackage(DisguiseType.PLAYER, "vampire", "Kenzo74_"); // vampire
		case 66:
			return new DisguisePackage(DisguiseType.PLAYER, "Iron knight");
		case 67:
			return new DisguisePackage(DisguiseType.PLAYER, "Human Citizen");
		case 68:
			return new DisguisePackage(DisguiseType.PLAYER, "Tentacle");
		case 69:
			return new DisguisePackage(DisguiseType.PLAYER, "Wisp");
		case 70:
			return new DisguisePackage(DisguiseType.ZOMBIE, "Zombie");
		case 71:
			return new DisguisePackage(DisguiseType.PLAYER, "Citizen");
		case 72:
			return new DisguisePackage(DisguiseType.BOAT, "Ship");
		case 73:
			return new DisguisePackage(DisguiseType.PLAYER, "Launch");
		case 74:
			return new DisguisePackage(DisguiseType.PLAYER, "Piranha");
		case 75:
			return new DisguisePackage(DisguiseType.BLAZE, "Elemental");
		case 76:
			return new DisguisePackage(DisguiseType.OCELOT, "Puma");
		case 77:
			return new DisguisePackage(DisguiseType.PLAYER, "Dark elf Citizen");
		case 78:
			return new DisguisePackage(DisguiseType.PLAYER, "High human Citizen");
		case 79:
			return new DisguisePackage(DisguiseType.VEX, "Bixie");
		case 80:
			return new DisguisePackage(DisguiseType.PLAYER, "Reanimated Hand");
		case 81:
			return new DisguisePackage(DisguiseType.PLAYER, "Halfling Citizen");
		case 82:
			return new DisguisePackage(DisguiseType.PLAYER, "Scarecrow");
		case 83:
			return new DisguisePackage(DisguiseType.PLAYER, "Skunk");
		case 84:
			return new DisguisePackage(DisguiseType.PLAYER, "Snake Elemental");
		case 85:
			return new DisguisePackage(DisguiseType.WITHER, "Spectre");
		case 86:
			return new DisguisePackage(DisguiseType.PLAYER, "Sphinx");
		case 87:
			return new DisguisePackage(DisguiseType.PLAYER, "Armadillo");
		case 88:
			return new DisguisePackage(DisguiseType.PLAYER, "Clockwork Gnome");
		case 89:
			return new DisguisePackage(DisguiseType.PLAYER, "Drake");
		case 90:
			return new DisguisePackage(DisguiseType.PLAYER, "Barbarian Citizen");
		case 91:
			return new DisguisePackage(DisguiseType.PLAYER, "Alligator");
		case 92:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll Citizen");
		case 93:
			return new DisguisePackage(DisguiseType.PLAYER, "ogre", "theorc"); // ogre
		case 94:
			return new DisguisePackage(DisguiseType.PLAYER, "dwarf", "Faenon"); // dwarf
		case 95:
			return new DisguisePackage(DisguiseType.PLAYER, "God of fear");
		case 96:
			return new DisguisePackage(DisguiseType.PLAYER, "Cockatrice");
		case 97:
			return new DisguisePackage(DisguiseType.PLAYER, "Daisy Man");
		case 98:
			return new DisguisePackage(DisguiseType.PLAYER, "elf", "Kenzo74_"); // elf vampire
		case 99:
			return new DisguisePackage(DisguiseType.PLAYER, "darkelf", "Kenzo74_"); // dark elf
		case 100:
			return new DisguisePackage(DisguiseType.PLAYER, "Dervish");
		case 101:
			return new DisguisePackage(DisguiseType.PLAYER, "Efreeti");
		case 102:
			return new DisguisePackage(DisguiseType.PLAYER, "Tadpole");
		case 103:
			return new DisguisePackage(DisguiseType.PLAYER, "Fish man");
		case 104:
			return new DisguisePackage(DisguiseType.PLAYER, "Leech");
		case 105:
			return new DisguisePackage(DisguiseType.PLAYER, "Swordfish");
		case 106:
			return new DisguisePackage(DisguiseType.PLAYER, "Elf guard");
		case 107:
			return new DisguisePackage(DisguiseType.PLAYER, "Mammoth");
		case 108:
			return new DisguisePackage(DisguiseType.ELDER_GUARDIAN, "Floating eye");
		case 109:
			return new DisguisePackage(DisguiseType.PLAYER, "Wasp");
		case 110:
			return new DisguisePackage(DisguiseType.PLAYER, "mermaid", "Suim67"); // mermaid
		case 111:
			return new DisguisePackage(DisguiseType.PLAYER, "Harpie");
		case 112:
			return new DisguisePackage(DisguiseType.PLAYER, "Pixie guard");
		case 113:
			return new DisguisePackage(DisguiseType.PLAYER, "Drixie");
		case 114:
			return new DisguisePackage(DisguiseType.PLAYER, "Ghost Ship");
		case 115:
			return new DisguisePackage(DisguiseType.PLAYER, "Clam");
		case 116:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea Horse");
		case 117:
			return new DisguisePackage(DisguiseType.WITHER, "ghost dwarf", "Faenon"); // ghost dwarf
		case 118:
			return new DisguisePackage(DisguiseType.WITHER, "ghost high human", "Orbitly"); // ghost high human
		case 119:
			return new DisguisePackage(DisguiseType.OCELOT, "Sabertooth Cat");
		case 120:
			return new DisguisePackage(DisguiseType.WOLF, "Wolf Elemental");
		case 121:
			return new DisguisePackage(DisguiseType.PLAYER, "Gorgon");
		case 122:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Dragon Skeleton");
		case 123:
			return new DisguisePackage(DisguiseType.PLAYER, "God of hate");
		case 124:
			return new DisguisePackage(DisguiseType.HORSE, "Unicorn");
		case 125:
			return new DisguisePackage(DisguiseType.PLAYER, "Pegasus");
		case 126:
			return new DisguisePackage(DisguiseType.PLAYER, "Genie");
		case 127:
			return new DisguisePackage(DisguiseType.PLAYER, "Invisible Man");
		case 128:
			return new DisguisePackage(DisguiseType.PLAYER, "Iksar");
		case 129:
			return new DisguisePackage(DisguiseType.PLAYER, "Scorpion");
		case 130:
			return new DisguisePackage(DisguiseType.PLAYER, "Cat man");
		case 131:
			return new DisguisePackage(DisguiseType.PLAYER, "Sarnak");
		case 132:
			return new DisguisePackage(DisguiseType.PLAYER, "Dragon frog");
		case 133:
			return new DisguisePackage(DisguiseType.PLAYER, "Lycanthrope");
		case 134:
			return new DisguisePackage(DisguiseType.PLAYER, "Mosquito");
		case 135:
			return new DisguisePackage(DisguiseType.PLAYER, "Rhino");
		case 136:
			return new DisguisePackage(DisguiseType.PLAYER, "Half human dragon");
		case 137:
			return new DisguisePackage(DisguiseType.PLAYER, "goblin", "AllOgreNow"); // goblin
		case 138:
			return new DisguisePackage(DisguiseType.PLAYER, "Yeti");
		case 139:
			return new DisguisePackage(DisguiseType.PLAYER, "Scaled human Citizen");
		case 140:
			return new DisguisePackage(DisguiseType.GIANT, "Forest Giant");
		case 141:
			return new DisguisePackage(DisguiseType.BOAT, "Boat");
		case 142:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown 142");
		case 143:
			return new DisguisePackage(DisguiseType.PLAYER, "tree", "zero_kage"); // tree
		case 144:
			return new DisguisePackage(DisguiseType.PLAYER, "Badger man");
		case 145:
			return new DisguisePackage(DisguiseType.SLIME, "Goo");
		case 146:
			return new DisguisePackage(DisguiseType.PLAYER, "Spectral Half dragon");
		case 147:
			return new DisguisePackage(DisguiseType.PLAYER, "Spectral Scaled Human");
		case 148:
			return new DisguisePackage(DisguiseType.PLAYER, "Fish");
		case 149:
			return new DisguisePackage(DisguiseType.PLAYER, "Scorpion");
		case 150:
			return new DisguisePackage(DisguiseType.PLAYER, "Plant fiend");
		case 151:
			return new DisguisePackage(DisguiseType.PLAYER, "God of justice");
		case 152:
			return new DisguisePackage(DisguiseType.PLAYER, "God of disease");
		case 153:
			return new DisguisePackage(DisguiseType.PLAYER, "God of mischieve");
		case 154:
			return new DisguisePackage(DisguiseType.PLAYER, "Butterfly Drake");
		case 155:
			return new DisguisePackage(DisguiseType.PLAYER, "Half dragon Skeleton");
		case 156:
			return new DisguisePackage(DisguiseType.PLAYER, "Ratman");
		case 157:
			return new DisguisePackage(DisguiseType.PLAYER, "Wyvern");
		case 158:
			return new DisguisePackage(DisguiseType.PLAYER, "Wurm");
		case 159:
			return new DisguisePackage(DisguiseType.PLAYER, "Devourer");
		case 160:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Scaled man Golem");
		case 161:
			return new DisguisePackage(DisguiseType.PLAYER, "Scaled man Skeleton");
		case 162:
			return new DisguisePackage(DisguiseType.PLAYER, "Man Eating Plant");
		case 163:
			return new DisguisePackage(DisguiseType.PLAYER, "Raptor");
		case 164:
			return new DisguisePackage(DisguiseType.PLAYER, "Half dragon Golem");
		case 165:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Water Dragon");
		case 166:
			return new DisguisePackage(DisguiseType.PLAYER, "Scaled man Hand");
		case 167:
			return new DisguisePackage(DisguiseType.PLAYER, "Plant monster");
		case 168:
			return new DisguisePackage(DisguiseType.PLAYER, "Flying Monkey");
		case 169:
			return new DisguisePackage(DisguiseType.PLAYER, "Rhino");
		case 170:
			return new DisguisePackage(DisguiseType.PLAYER, "Snow air elemental");
		case 171:
			return new DisguisePackage(DisguiseType.WOLF, "Dire Wolf");
		case 172:
			return new DisguisePackage(DisguiseType.PLAYER, "Manticore");
		case 173:
			return new DisguisePackage(DisguiseType.PLAYER, "Totem");
		case 174:
			return new DisguisePackage(DisguiseType.WITHER, "Cold Spectre");
		case 175:
			return new DisguisePackage(DisguiseType.PLAYER, "Enchanted Armor");
		case 176:
			return new DisguisePackage(DisguiseType.RABBIT, "Snow Bunny");
		case 177:
			return new DisguisePackage(DisguiseType.PLAYER, "Walrus");
		case 178:
			return new DisguisePackage(DisguiseType.PLAYER, "Rock-gem Men");
		case 179:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 180:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 181:
			return new DisguisePackage(DisguiseType.PLAYER, "bullman", "_CrimsonBlade_"); // bullman
		case 182:
			return new DisguisePackage(DisguiseType.PLAYER, "Faun");
		case 183:
			return new DisguisePackage(DisguiseType.PLAYER, "Coldain");
		case 184:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Dragons");
		case 185:
			return new DisguisePackage(DisguiseType.WITCH, "Hag");
		case 186:
			return new DisguisePackage(DisguiseType.PLAYER, "Hippogriff");
		case 187:
			return new DisguisePackage(DisguiseType.PLAYER, "Siren");
		case 188:
			return new DisguisePackage(DisguiseType.GIANT, "Frost Giant");
		case 189:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 190:
			return new DisguisePackage(DisguiseType.PLAYER, "Ottermen");
		case 191:
			return new DisguisePackage(DisguiseType.PLAYER, "Walrus Man");
		case 192:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Clockwork Dragon");
		case 193:
			return new DisguisePackage(DisguiseType.PLAYER, "Abhorent");
		case 194:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea Turtle");
		case 195:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Black and White Dragons");
		case 196:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Ghost Dragon");
		case 197:
			return new DisguisePackage(DisguiseType.PLAYER, "Ronnie Test");
		case 198:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Prismatic Dragon");
		case 199:
			return new DisguisePackage(DisguiseType.PLAYER, "Bug");
		case 200:
			return new DisguisePackage(DisguiseType.PLAYER, "Raptor");
		case 201:
			return new DisguisePackage(DisguiseType.PLAYER, "Bug");
		case 202:
			return new DisguisePackage(DisguiseType.PLAYER, "Weird man");
		case 203:
			return new DisguisePackage(DisguiseType.PLAYER, "Worm");
		case 204:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 205:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 206:
			return new DisguisePackage(DisguiseType.PLAYER, "Owlbear");
		case 207:
			return new DisguisePackage(DisguiseType.PLAYER, "Rhino Beetle");
		case 208:
			return new DisguisePackage(DisguiseType.PLAYER, "vampire", "Kenzo74_"); // vampyre
		case 209:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Earth Elemental");
		case 210:
			return new DisguisePackage(DisguiseType.PARROT, "Air Elemental");
		case 211:
			return new DisguisePackage(DisguiseType.GUARDIAN, "Water Elemental");
		case 212:
			return new DisguisePackage(DisguiseType.BLAZE, "Fire Elemental");
		case 213:
			return new DisguisePackage(DisguiseType.PLAYER, "Fish");
		case 214:
			return new DisguisePackage(DisguiseType.WITHER, "Horror");
		case 215:
			return new DisguisePackage(DisguiseType.PLAYER, "Small man");
		case 216:
			return new DisguisePackage(DisguiseType.HORSE, "Horse");
		case 217:
			return new DisguisePackage(DisguiseType.PLAYER, "Snake person");
		case 218:
			return new DisguisePackage(DisguiseType.PLAYER, "Fungus");
		case 219:
			return new DisguisePackage(DisguiseType.PLAYER, "vampire", "Kenzo74_"); // vampire
		case 220:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Stone golem");
		case 221:
			return new DisguisePackage(DisguiseType.OCELOT, "Red Cheetah");
		case 222:
			return new DisguisePackage(DisguiseType.PLAYER, "Camel");
		case 223:
			return new DisguisePackage(DisguiseType.PLAYER, "Millipede");
		case 224:
			return new DisguisePackage(DisguiseType.PLAYER, "shade", "RainbowF"); // shade
		case 225:
			return new DisguisePackage(DisguiseType.PLAYER, "Exotic plant creature");
		case 226:
			return new DisguisePackage(DisguiseType.PLAYER, "assassin", "epicafroninja"); // assassin
		case 227:
			return new DisguisePackage(DisguiseType.PLAYER, "ghost", "Orbitly"); // ghost
		case 228:
			return new DisguisePackage(DisguiseType.PLAYER, "ogre", "theorc"); // ogre
		case 229:
			return new DisguisePackage(DisguiseType.PLAYER, "Alien");
		case 230:
			return new DisguisePackage(DisguiseType.PLAYER, "4 armed human");
		case 231:
			return new DisguisePackage(DisguiseType.WITHER_SKELETON, "Ghost");
		case 232:
			return new DisguisePackage(DisguiseType.WOLF, "Wolf bat");
		case 233:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Ground Shaker");
		case 234:
			return new DisguisePackage(DisguiseType.PLAYER, "Cat man Skeleton");
		case 235:
			return new DisguisePackage(DisguiseType.PLAYER, "Mutant Human");
		case 236:
			return new DisguisePackage(DisguiseType.PLAYER, "Human king");
		case 237:
			return new DisguisePackage(DisguiseType.PLAYER, "Bandit");
		case 238:
			return new DisguisePackage(DisguiseType.PLAYER, "Catman King");
		case 239:
			return new DisguisePackage(DisguiseType.PLAYER, "Catman Guard");
		case 240:
			return new DisguisePackage(DisguiseType.PLAYER, "Teleporter man");
		case 241:
			return new DisguisePackage(DisguiseType.PLAYER, "werewolf", "Apiii"); // were wolf
		case 242:
			return new DisguisePackage(DisguiseType.PLAYER, "naiad", "Suim67"); // naiad
		case 243:
			return new DisguisePackage(DisguiseType.PLAYER, "nymph", "Suim67"); // nymph
		case 244:
			return new DisguisePackage(DisguiseType.PLAYER, "ent", "zero_kage"); // ent
		case 245:
			return new DisguisePackage(DisguiseType.PLAYER, "Fly Man");
		case 246:
			return new DisguisePackage(DisguiseType.PLAYER, "God of water");
		case 247:
			return new DisguisePackage(DisguiseType.PLAYER, "God of fire");
		case 248:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Clockwork Golem");
		case 249:
			return new DisguisePackage(DisguiseType.PLAYER, "Clockwork Brain");
		case 250:
			return new DisguisePackage(DisguiseType.PLAYER, "Banshee");
		case 251:
			return new DisguisePackage(DisguiseType.PLAYER, "human hooded guard");
		case 252:
			return new DisguisePackage(DisguiseType.PLAYER, "Unknown");
		case 253:
			return new DisguisePackage(DisguiseType.PLAYER, "fat disease man");
		case 254:
			return new DisguisePackage(DisguiseType.PLAYER, "Fire God Guard");
		case 255:
			return new DisguisePackage(DisguiseType.PLAYER, "GOd of disease");
		case 269:
			return new DisguisePackage(DisguiseType.PLAYER, "Rat creature");
		case 270:
			return new DisguisePackage(DisguiseType.PLAYER, "Disease");
		case 272:
			return new DisguisePackage(DisguiseType.PLAYER, "Mounted Undead");
		case 273:
			return new DisguisePackage(DisguiseType.PLAYER, "Clockwork beast");
		case 274:
			return new DisguisePackage(DisguiseType.PLAYER, "Broken Clockwork");
		case 275:
			return new DisguisePackage(DisguiseType.PLAYER, "Giant Clockwork");
		case 276:
			return new DisguisePackage(DisguiseType.PLAYER, "Clockwork Beetle");
		case 277:
			return new DisguisePackage(DisguiseType.PLAYER, "goblin", "AllOgreNow"); // goblin
		case 278:
			return new DisguisePackage(DisguiseType.PLAYER, "God of storms");
		case 279:
			return new DisguisePackage(DisguiseType.PLAYER, "Blood Raven");
		case 280:
			return new DisguisePackage(DisguiseType.PLAYER, "Gargoyle");
		case 281:
			return new DisguisePackage(DisguiseType.PLAYER, "Mouth monster");
		case 282:
			return new DisguisePackage(DisguiseType.HORSE, "Skeletal Horse");
		case 283:
			return new DisguisePackage(DisguiseType.PLAYER, "God of pain");
		case 284:
			return new DisguisePackage(DisguiseType.PLAYER, "God of fire");
		case 285:
			return new DisguisePackage(DisguiseType.PLAYER, "torment inquisitor");
		case 286:
			return new DisguisePackage(DisguiseType.PLAYER, "necromancer priest");
		case 287:
			return new DisguisePackage(DisguiseType.HORSE, "Nightmare");
		case 288:
			return new DisguisePackage(DisguiseType.PLAYER, "god of war");
		case 289:
			return new DisguisePackage(DisguiseType.PLAYER, "god of tactics");
		case 290:
			return new DisguisePackage(DisguiseType.PLAYER, "god of strategy");
		case 291:
			return new DisguisePackage(DisguiseType.PARROT, "Air Mephit");
		case 292:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Earth Mephit");
		case 293:
			return new DisguisePackage(DisguiseType.BLAZE, "Fire Mephit");
		case 294:
			return new DisguisePackage(DisguiseType.WITHER, "Nightmare Mephit");
		case 295:
			return new DisguisePackage(DisguiseType.PLAYER, "god of knowledge");
		case 296:
			return new DisguisePackage(DisguiseType.PLAYER, "god of truth");
		case 297:
			return new DisguisePackage(DisguiseType.SKELETON, "Undead Knight");
		case 298:
			return new DisguisePackage(DisguiseType.PLAYER, "god of earth");
		case 299:
			return new DisguisePackage(DisguiseType.PLAYER, "god of air");
		case 300:
			return new DisguisePackage(DisguiseType.PLAYER, "Fiend");
		case 301:
			return new DisguisePackage(DisguiseType.PLAYER, "Test");
		case 302:
			return new DisguisePackage(DisguiseType.PLAYER, "Crab");
		case 303:
			return new DisguisePackage(DisguiseType.PLAYER, "Phoenix");
		case 304:
			return new DisguisePackage(DisguiseType.ENDER_DRAGON, "Dragon");
		case 305:
			return new DisguisePackage(DisguiseType.POLAR_BEAR, "Bear");
		case 306:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 307:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 308:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 309:
			return new DisguisePackage(DisguiseType.GIANT, "Storm Giant");
		case 310:
			return new DisguisePackage(DisguiseType.BLAZE, "Storm Mana");
		case 311:
			return new DisguisePackage(DisguiseType.BLAZE, "Storm Fire");
		case 312:
			return new DisguisePackage(DisguiseType.BLAZE, "Storm Celestial");
		case 313:
			return new DisguisePackage(DisguiseType.PLAYER, "War Wraith");
		case 314:
			return new DisguisePackage(DisguiseType.PLAYER, "paladin guard");
		case 315:
			return new DisguisePackage(DisguiseType.PLAYER, "Kraken");
		case 316:
			return new DisguisePackage(DisguiseType.PLAYER, "Poison Frog");
		case 317:
			return new DisguisePackage(DisguiseType.PLAYER, "Quezticoatal");
		case 318:
			return new DisguisePackage(DisguiseType.PLAYER, "paladin guard");
		case 319:
			return new DisguisePackage(DisguiseType.PLAYER, "War Boar");
		case 320:
			return new DisguisePackage(DisguiseType.BLAZE, "Efreeti");
		case 321:
			return new DisguisePackage(DisguiseType.PLAYER, "War Boar Unarmored");
		case 322:
			return new DisguisePackage(DisguiseType.PLAYER, "Black Knight");
		case 323:
			return new DisguisePackage(DisguiseType.PLAYER, "Animated Armor");
		case 324:
			return new DisguisePackage(DisguiseType.PLAYER, "Undead Footman");
		case 325:
			return new DisguisePackage(DisguiseType.PLAYER, "Rallos Zek Minion");
		case 326:
			return new DisguisePackage(DisguiseType.SPIDER, "Arachnid");
		case 327:
			return new DisguisePackage(DisguiseType.SPIDER, "Crystal Spider");
		case 328:
			return new DisguisePackage(DisguiseType.PLAYER, "Cage");
		case 329:
			return new DisguisePackage(DisguiseType.PLAYER, "Portal");
		case 330:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman");
		case 331:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll seaman");
		case 332:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll seaman");
		case 333:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll seaman");
		case 334:
			return new DisguisePackage(DisguiseType.PLAYER, "Spectre Pirate Boss");
		case 335:
			return new DisguisePackage(DisguiseType.PLAYER, "Pirate Boss");
		case 336:
			return new DisguisePackage(DisguiseType.PLAYER, "Pirate Dark Shaman");
		case 337:
			return new DisguisePackage(DisguiseType.PLAYER, "Pirate Officer");
		case 338:
			return new DisguisePackage(DisguiseType.PLAYER, "Gnome Pirate");
		case 339:
			return new DisguisePackage(DisguiseType.PLAYER, "Dark Elf Pirate");
		case 340:
			return new DisguisePackage(DisguiseType.PLAYER, "ogre pirate", "theorc"); // ogre pirate
		case 341:
			return new DisguisePackage(DisguiseType.PLAYER, "Human Pirate");
		case 342:
			return new DisguisePackage(DisguiseType.PLAYER, "High Human Pirate");
		case 343:
			return new DisguisePackage(DisguiseType.PLAYER, "Poison Dart Frog");
		case 344:
			return new DisguisePackage(DisguiseType.PLAYER, "Troll Zombie");
		case 345:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea creature man Land");
		case 346:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea creature man Armored");
		case 347:
			return new DisguisePackage(DisguiseType.PLAYER, "Sea creature man Robed");
		case 348:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman Mount");
		case 349:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman Skeleton");
		case 350:
			return new DisguisePackage(DisguiseType.PLAYER, "Undead Frogman");
		case 351:
			return new DisguisePackage(DisguiseType.PLAYER, "Chosen Warrior");
		case 352:
			return new DisguisePackage(DisguiseType.PLAYER, "chosen wizard", "Zelinx"); // chosen wizard
		case 353:
			return new DisguisePackage(DisguiseType.PLAYER, "Lizard creature");
		case 354:
			return new DisguisePackage(DisguiseType.PLAYER, "Greater Lizard creature");
		case 355:
			return new DisguisePackage(DisguiseType.PLAYER, "Lizard creature Boss");
		case 356:
			return new DisguisePackage(DisguiseType.PLAYER, "scaled dog");
		case 357:
			return new DisguisePackage(DisguiseType.PLAYER, "Undead scaled dog");
		case 358:
			return new DisguisePackage(DisguiseType.PLAYER, "Undead lizard creature");
		case 359:
			return new DisguisePackage(DisguiseType.PLAYER, "lesser vampire", "Kenzo74_"); // vampire lesser
		case 360:
			return new DisguisePackage(DisguiseType.PLAYER, "elite vampire", "Kenzo74_"); // vampire elite
		case 361:
			return new DisguisePackage(DisguiseType.PLAYER, "orc", "Jeeorc"); // orc
		case 362:
			return new DisguisePackage(DisguiseType.WITHER_SKELETON, "Bone Golem");
		case 363:
			return new DisguisePackage(DisguiseType.PLAYER, "Huge gargoyle");
		case 364:
			return new DisguisePackage(DisguiseType.PLAYER, "Sand Elf");
		case 365:
			return new DisguisePackage(DisguiseType.PLAYER, "Vampire Master");
		case 366:
			return new DisguisePackage(DisguiseType.PLAYER, "orc", "Jeeorc"); // orc
		case 367:
			return new DisguisePackage(DisguiseType.SKELETON, "Skeleton New");
		case 368:
			return new DisguisePackage(DisguiseType.ZOMBIE, "mummy", "Greng"); // mummy
		case 369:
			return new DisguisePackage(DisguiseType.PLAYER, "goblin", "AllOgreNow"); // goblin
		case 370:
			return new DisguisePackage(DisguiseType.PLAYER, "Insect");
		case 371:
			return new DisguisePackage(DisguiseType.PLAYER, "Frogman Ghost");
		case 372:
			return new DisguisePackage(DisguiseType.PLAYER, "Spinning Air Elemental");
		case 373:
			return new DisguisePackage(DisguiseType.PLAYER, "Shadow Creatue");
		case 374:
			return new DisguisePackage(DisguiseType.IRON_GOLEM, "Golem New");
		case 375:
			return new DisguisePackage(DisguiseType.PLAYER, "Evil Eye New");
		case 376:
			return new DisguisePackage(DisguiseType.PLAYER, "Box");
		case 377:
			return new DisguisePackage(DisguiseType.PLAYER, "Barrel");
		case 378:
			return new DisguisePackage(DisguiseType.PLAYER, "Chest");
		case 379:
			return new DisguisePackage(DisguiseType.PLAYER, "Vase");
		case 380:
			return new DisguisePackage(DisguiseType.PLAYER, "Table");
		case 381:
			return new DisguisePackage(DisguiseType.PLAYER, "Weapons Rack");
		case 382:
			return new DisguisePackage(DisguiseType.PLAYER, "Coffin");
		case 383:
			return new DisguisePackage(DisguiseType.PLAYER, "Bones");
		case 384:
			return new DisguisePackage(DisguiseType.PLAYER, "Joker");
		case 454:
			return new DisguisePackage(DisguiseType.PLAYER, "Apiii");
		default:
			return new DisguisePackage(DisguiseType.UNKNOWN, "Unknown");
		}
	}

	public static double getTotalEffectTotalHP(LivingEntity livingEntity) {
		double allTotalHpEffects = 0;
		try {
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(SpellEffectType.TotalHP)))
						continue;

					allTotalHpEffects += effect.getCalculatedValue();
				}
			}

		} catch (CoreStateInitException e) {
			return 0;
		}
		return allTotalHpEffects;
	}

	public static List<ActiveSpellEffect> getActiveSpellEffects(LivingEntity livingEntity, SpellEffectType effectType) {
		List<ActiveSpellEffect> returnEffects = new ArrayList<ActiveSpellEffect>();

		SoliniaEntitySpells effects;
		try {
			effects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(effectType)))
						continue;

					returnEffects.add(effect);
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}

		return returnEffects;
	}
	
	public static List<ActiveSpellEffect> getActiveSpellEffects(LivingEntity livingEntity, List<SpellEffectType> effectTypes) {
		List<ActiveSpellEffect> returnEffects = new ArrayList<ActiveSpellEffect>();

		SoliniaEntitySpells effects;
		try {
			effects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effectTypes.contains(effect.getSpellEffectType())))
						continue;

					returnEffects.add(effect);
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}

		return returnEffects;
	}

	public static int getActiveSpellEffectsRemainingValue(LivingEntity livingEntity, SpellEffectType effectType) {
		int totalRemaining = 0;

		SoliniaEntitySpells effects;
		try {
			effects = StateManager.getInstance().getEntityManager().getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(effectType)))
						continue;

					totalRemaining += effect.getRemainingValue();
				}
			}
		} catch (CoreStateInitException e) {
			// skip
		}

		return totalRemaining;
	}

	public static int getTotalEffectStat(LivingEntity livingEntity, String stat) {
		int statTotal = 0;

		try {
			SoliniaEntitySpells effects = StateManager.getInstance().getEntityManager()
					.getActiveEntitySpells(livingEntity);

			for (SoliniaActiveSpell activeSpell : effects.getActiveSpells()) {
				for (ActiveSpellEffect effect : activeSpell.getActiveSpellEffects()) {
					if (!(effect.getSpellEffectType().equals(SpellEffectType.STR))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.STA))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.AGI))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.DEX))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.INT))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.WIS))
							&& !(effect.getSpellEffectType().equals(SpellEffectType.CHA)))
						continue;

					switch (stat) {
					case "STRENGTH":
						if (!(effect.getSpellEffectType().equals(SpellEffectType.STR)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case "STAMINA":
						if (!(effect.getSpellEffectType().equals(SpellEffectType.STA)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case "AGILITY":
						if (!(effect.getSpellEffectType().equals(SpellEffectType.AGI)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case "DEXTERITY":
						if (!(effect.getSpellEffectType().equals(SpellEffectType.DEX)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case "INTELLIGENCE":
						if (!(effect.getSpellEffectType().equals(SpellEffectType.INT)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case "WISDOM":
						if (!(effect.getSpellEffectType().equals(SpellEffectType.WIS)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					case "CHARISMA":
						if (!(effect.getSpellEffectType().equals(SpellEffectType.CHA)))
							break;
						statTotal += effect.getCalculatedValue();
						break;
					default:
						break;
					}
				}
			}

		} catch (CoreStateInitException e) {
			return 0;
		}
		return statTotal;
	}

	public static double getStatMaxHP(ISoliniaClass classObj, int tmplevel, int stamina) {
		// level multiplier
		double multiplier = 1;

		String profession = "UNSKILLED";
		if (classObj != null)
			profession = classObj.getName().toUpperCase();

		if (profession != null) {
			switch (profession) {
			case "WARRIOR":
				if (tmplevel < 20)
					multiplier = 22;
				else if (tmplevel < 30)
					multiplier = 23;
				else if (tmplevel < 40)
					multiplier = 25;
				else if (tmplevel < 53)
					multiplier = 27;
				else if (tmplevel < 57)
					multiplier = 28;
				else
					multiplier = 30;
				break;

			case "DRUID":
			case "CLERIC":
			case "SHAMAN":
				multiplier = 15;
				break;

			case "PALADIN":
			case "SHADOWKNIGHT":
				if (tmplevel < 35)
					multiplier = 21;
				else if (tmplevel < 45)
					multiplier = 22;
				else if (tmplevel < 51)
					multiplier = 23;
				else if (tmplevel < 56)
					multiplier = 24;
				else if (tmplevel < 60)
					multiplier = 25;
				else
					multiplier = 26;
				break;

			case "MONK":
			case "BARD":
			case "ROGUE":
				// case BEASTLORD:
				if (tmplevel < 51)
					multiplier = 18;
				else if (tmplevel < 58)
					multiplier = 19;
				else
					multiplier = 20;
				break;

			case "RANGER":
				if (tmplevel < 58)
					multiplier = 20;
				else
					multiplier = 21;
				break;

			case "MAGICIAN":
			case "WIZARD":
			case "NECROMANCER":
			case "ENCHANTER":
				multiplier = 12;
				break;
			default:
				if (tmplevel < 35)
					multiplier = 21;
				else if (tmplevel < 45)
					multiplier = 22;
				else if (tmplevel < 51)
					multiplier = 23;
				else if (tmplevel < 56)
					multiplier = 24;
				else if (tmplevel < 60)
					multiplier = 25;
				else
					multiplier = 26;
				break;
			}
		}

		double hp = tmplevel * multiplier;
		double hpmain = (stamina / 12) * tmplevel;

		double calculatedhp = hp + hpmain;
		return (int) Math.floor(calculatedhp);
	}

	public static int getMaxDamage(int level, int strength) {
		// TODO Auto-generated method stub
		double basedmg = ((level * 0.45) + 0.8);

		double racestatbonus = strength + (level * 5);
		double bonus = racestatbonus / 100;
		double damagemlt = basedmg * bonus;
		double newdmg = damagemlt;
		double damagepct = newdmg / basedmg;

		return (int) Math.floor(basedmg * damagepct);
	}

	public static SkillType getSkillType(Integer skill) {
		switch (skill) {
		case 0:
			return SkillType.Slashing;
		case 1:
			return SkillType.Crushing;
		case 2:
			return SkillType.TwoHandBlunt;
		case 3:
			return SkillType.TwoHandSlashing;
		case 4:
			return SkillType.Abjuration;
		case 5:
			return SkillType.Alteration;
		case 6:
			return SkillType.ApplyPoison;
		case 7:
			return SkillType.Archery;
		case 8:
			return SkillType.Backstab;
		case 9:
			return SkillType.BindWound;
		case 10:
			return SkillType.Bash;
		case 11:
			return SkillType.Block;
		case 12:
			return SkillType.BrassInstruments;
		case 13:
			return SkillType.Channeling;
		case 14:
			return SkillType.Conjuration;
		case 15:
			return SkillType.Defense;
		case 16:
			return SkillType.Disarm;
		case 17:
			return SkillType.DisarmTraps;
		case 18:
			return SkillType.Divination;
		case 19:
			return SkillType.Dodge;
		case 20:
			return SkillType.DoubleAttack;
		case 21:
			return SkillType.DragonPunch;
		case 22:
			return SkillType.DualWield;
		case 23:
			return SkillType.EagleStrike;
		case 24:
			return SkillType.Evocation;
		case 25:
			return SkillType.FeignDeath;
		case 26:
			return SkillType.FlyingKick;
		case 27:
			return SkillType.Forage;
		case 28:
			return SkillType.HandtoHand;
		case 29:
			return SkillType.Hide;
		case 30:
			return SkillType.Kick;
		case 31:
			return SkillType.Meditation;
		case 32:
			return SkillType.Mend;
		case 33:
			return SkillType.Offense;
		case 34:
			return SkillType.Parry;
		case 35:
			return SkillType.PickLock;
		case 36:
			return SkillType.OneHandPiercing;
		case 37:
			return SkillType.Riposte;
		case 38:
			return SkillType.RoundKick;
		case 39:
			return SkillType.SafeFall;
		case 40:
			return SkillType.SenseHeading;
		case 41:
			return SkillType.Singing;
		case 42:
			return SkillType.Sneak;
		case 43:
			return SkillType.SpecialiseAbjuration;
		case 44:
			return SkillType.SpecialiseAlteration;
		case 45:
			return SkillType.SpecialiseConjuration;
		case 46:
			return SkillType.SpecialiseDivination;
		case 47:
			return SkillType.SpecialiseEvocation;
		case 48:
			return SkillType.PickPockets;
		case 49:
			return SkillType.StringedInstruments;
		case 50:
			return SkillType.Swimming;
		case 51:
			return SkillType.Throwing;
		case 52:
			return SkillType.TigerClaw;
		case 53:
			return SkillType.Tracking;
		case 54:
			return SkillType.WindInstruments;
		case 55:
			return SkillType.Fishing;
		case 56:
			return SkillType.MakePoison;
		case 57:
			return SkillType.Tinkering;
		case 58:
			return SkillType.Research;
		case 59:
			return SkillType.Alchemy;
		case 60:
			return SkillType.Baking;
		case 61:
			return SkillType.Tailoring;
		case 62:
			return SkillType.SenseTraps;
		case 63:
			return SkillType.Blacksmithing;
		case 64:
			return SkillType.Fletching;
		case 65:
			return SkillType.Brewing;
		case 66:
			return SkillType.AlcoholTolerance;
		case 67:
			return SkillType.Begging;
		case 68:
			return SkillType.JewelryMaking;
		case 69:
			return SkillType.Pottery;
		case 70:
			return SkillType.PercussionInstruments;
		case 71:
			return SkillType.Intimidation;
		case 72:
			return SkillType.Berserking;
		case 73:
			return SkillType.Taunt;
		case 74:
			return SkillType.Frenzy;
		case 75:
			return SkillType.RemoveTraps;
		case 76:
			return SkillType.TripleAttack;
		case 77:
			return SkillType.TwoHandPiercing;
		case 78:
			return SkillType.None;
		case 79:
			return SkillType.Count;
		default:
			return SkillType.None;
		}
	}

	public static boolean isInvalidNpcSpell(ISoliniaSpell spell) {
		if (spell.getSpellEffectTypes().contains(SpellEffectType.Gate)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Teleport)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Teleport2)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.TeleporttoAnchor)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Translocate)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.TranslocatetoAnchor)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Charm)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.SummonItem)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.BindAffinity)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Levitate)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.FeignDeath)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.ShadowStep)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.ShadowStepDirectional)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.Familiar)
				|| spell.getSpellEffectTypes().contains(SpellEffectType.SummonPet))
			return true;

		return false;
	}

	public static FactionStandingType getFactionStandingType(int factionId, int playerFactionValue) {
		try {
			ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager().getFaction(factionId);
			if (faction != null)
				playerFactionValue += faction.getBase();
		} catch (CoreStateInitException e) {

		}

		if (playerFactionValue >= 1101) {
			return FactionStandingType.FACTION_ALLY;
		}
		if (playerFactionValue >= 701 && playerFactionValue <= 1100) {
			return FactionStandingType.FACTION_WARMLY;
		}
		if (playerFactionValue >= 401 && playerFactionValue <= 700) {
			return FactionStandingType.FACTION_KINDLY;
		}
		if (playerFactionValue >= 101 && playerFactionValue <= 400) {
			return FactionStandingType.FACTION_AMIABLE;
		}
		if (playerFactionValue >= 0 && playerFactionValue <= 100) {
			return FactionStandingType.FACTION_INDIFFERENT;
		}
		if (playerFactionValue >= -100 && playerFactionValue <= -1) {
			return FactionStandingType.FACTION_APPREHENSIVE;
		}
		if (playerFactionValue >= -700 && playerFactionValue <= -101) {
			return FactionStandingType.FACTION_DUBIOUS;
		}
		if (playerFactionValue >= -999 && playerFactionValue <= -701) {
			return FactionStandingType.FACTION_THREATENLY;
		}
		if (playerFactionValue <= -1000) {
			return FactionStandingType.FACTION_SCOWLS;
		}
		return FactionStandingType.FACTION_INDIFFERENT;
	}

	public static AugmentationSlotType getItemStackAugSlotType(String basename, boolean isAugmentation) {
		if (isAugmentation)
			return AugmentationSlotType.NONE;

		switch (basename.toUpperCase()) {
		case "WOODEN_SWORD":
			return AugmentationSlotType.WEAPON;
		case "WOOD_SWORD":
			return AugmentationSlotType.WEAPON;
		case "STONE_SWORD":
			return AugmentationSlotType.WEAPON;
		case "IRON_SWORD":
			return AugmentationSlotType.WEAPON;
		case "GOLD_SWORD":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_SWORD":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_SWORD":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_AXE":
			return AugmentationSlotType.WEAPON;
		case "WOOD_AXE":
			return AugmentationSlotType.WEAPON;
		case "STONE_AXE":
			return AugmentationSlotType.WEAPON;
		case "IRON_AXE":
			return AugmentationSlotType.WEAPON;
		case "GOLD_AXE":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_AXE":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_AXE":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_SPADE":
			return AugmentationSlotType.WEAPON;
		case "WOOD_SPADE":
			return AugmentationSlotType.WEAPON;
		case "STONE_SPADE":
			return AugmentationSlotType.WEAPON;
		case "IRON_SPADE":
			return AugmentationSlotType.WEAPON;
		case "GOLD_SPADE":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_SPADE":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_SPADE":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_HOE":
			return AugmentationSlotType.WEAPON;
		case "WOOD_HOE":
			return AugmentationSlotType.WEAPON;
		case "STONE_HOE":
			return AugmentationSlotType.WEAPON;
		case "IRON_HOE":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_HOE":
			return AugmentationSlotType.WEAPON;
		case "GOLD_HOE":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_HOE":
			return AugmentationSlotType.WEAPON;
		case "WOODEN_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "WOOD_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "STONE_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "IRON_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "GOLDEN_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "GOLD_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "DIAMOND_PICKAXE":
			return AugmentationSlotType.WEAPON;
		case "LEATHER_HELMET":
			return AugmentationSlotType.HELMET;
		case "LEATHER_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "LEATHER_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "LEATHER_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "CHAINMAIL_HELMET":
			return AugmentationSlotType.HELMET;
		case "CHAINMAIL_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "CHAINMAIL_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "CHAINMAIL_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "IRON_HELMET":
			return AugmentationSlotType.HELMET;
		case "IRON_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "IRON_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "IRON_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "DIAMOND_HELMET":
			return AugmentationSlotType.HELMET;
		case "DIAMOND_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "DIAMOND_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "DIAMOND_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "GOLD_HELMET":
			return AugmentationSlotType.HELMET;
		case "GOLD_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "GOLD_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "GOLD_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "GOLDEN_HELMET":
			return AugmentationSlotType.HELMET;
		case "GOLDEN_CHESTPLATE":
			return AugmentationSlotType.CHESTPLATE;
		case "GOLDEN_LEGGINGS":
			return AugmentationSlotType.LEGGINGS;
		case "GOLDEN_BOOTS":
			return AugmentationSlotType.BOOTS;
		case "SHIELD":
			return AugmentationSlotType.SHIELD;
		default:
			return AugmentationSlotType.NONE;
		}
	}
	
	public static List<SoliniaAARankEffect> getHighestRankEffectsForEffectId(ISoliniaPlayer soliniaPlayer, int effectId)
	{
		List<SoliniaAARankEffect> rankEffects = new ArrayList<SoliniaAARankEffect>();
		
		for (ISoliniaAAAbility aaAbility : soliniaPlayer.getAAAbilitiesWithEffectType(effectId)) 
		{
			int currentRank = 0;
			SoliniaAARankEffect highestEffect = null;
			for (ISoliniaAARank rank : aaAbility.getRanks())
			{
				if (soliniaPlayer.hasRank(rank))
					if (rank.getPosition() > currentRank)
					{
						currentRank = rank.getPosition();
						for (SoliniaAARankEffect effect : rank.getEffects())
							if (effect.getEffectId() == effectId)
								highestEffect = effect;
					}
			}
			
			if (highestEffect != null)
			{
				rankEffects.add(highestEffect);
			}
		}
		return rankEffects;
	}
	
	public static int getHighestAAEffectEffectType(LivingEntity bukkitLivingEntity, SpellEffectType effectType) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		int highest = 0;
		
		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			for (SoliniaAARankEffect effect : player
					.getRanksEffectsOfEffectType(Utils.getEffectIdFromEffectType(effectType))) {
				
				if (effect.getBase1() > highest)
				{
					highest = effect.getBase1();
				}
			}
			return highest;
		} catch (CoreStateInitException e) {
			return 0;
		}

	}
	
	public static int getTotalAAEffectEffectType(LivingEntity bukkitLivingEntity, SpellEffectType effectType) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			int total = 0;
			for (SoliniaAARankEffect effect : player
					.getRanksEffectsOfEffectType(Utils.getEffectIdFromEffectType(effectType))) {
				total += effect.getBase1();
			}
			return total;
		} catch (CoreStateInitException e) {
			return 0;
		}

	}
	
	public static ISoliniaAARank getRankOfAAAbility(LivingEntity bukkitLivingEntity, ISoliniaAAAbility aa) {
		if (!(bukkitLivingEntity instanceof Player))
			return null;

		ISoliniaAARank foundRank = null;
		
		int position = 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			for (ISoliniaAARank rank : player.getAARanks()) {
				if (aa.getId() != rank.getAbilityid())
					continue;

				if (rank.getPosition() > position)
				{
					position = rank.getPosition();
					foundRank = rank;
				}
			}
		} catch (CoreStateInitException e) {
			return null;
		}

		return foundRank;
	}

	public static int getRankPositionOfAAAbility(LivingEntity bukkitLivingEntity, ISoliniaAAAbility aa) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		int position = 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			for (ISoliniaAARank rank : player.getAARanks()) {
				if (aa.getId() != rank.getAbilityid())
					continue;

				if (rank.getPosition() > position)
					position = rank.getPosition();
			}
		} catch (CoreStateInitException e) {
			return 0;
		}

		return position;
	}

	public static int getTotalAAEffectStat(LivingEntity bukkitLivingEntity, String stat) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			int total = 0;

			int effectIdLookup = 0;

			switch (stat) {
			case "STRENGTH":
				effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.STR);
				break;
			case "STAMINA":
				effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.STA);
				break;
			case "AGILITY":
				effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.AGI);
				break;
			case "DEXTERITY":
				effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.DEX);
				break;
			case "INTELLIGENCE":
				effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.INT);
				break;
			case "WISDOM":
				effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.WIS);
				break;
			case "CHARISMA":
				effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.CHA);
				break;
			default:
				break;
			}

			if (effectIdLookup == 0)
				return 0;

			for (SoliniaAARankEffect effect : player.getRanksEffectsOfEffectType(effectIdLookup)) {
				total += effect.getBase1();
			}
			return total;
		} catch (CoreStateInitException e) {
			return 0;
		}
	}

	public static double getTotalAAEffectMaxHp(LivingEntity bukkitLivingEntity) {
		if (!(bukkitLivingEntity instanceof Player))
			return 0;

		try {
			ISoliniaPlayer player = SoliniaPlayerAdapter.Adapt((Player) bukkitLivingEntity);
			int total = 0;

			int effectIdLookup = 0;
			effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.MaxHPChange);

			if (effectIdLookup > 0) {
				for (SoliniaAARankEffect effect : player.getRanksEffectsOfEffectType(effectIdLookup)) {
					total += effect.getBase1();
				}
			}
			effectIdLookup = Utils.getEffectIdFromEffectType(SpellEffectType.TotalHP);

			if (effectIdLookup > 0) {
				for (SoliniaAARankEffect effect : player.getRanksEffectsOfEffectType(effectIdLookup)) {
					total += effect.getBase1();
				}
			}

			return total;
		} catch (CoreStateInitException e) {
			return 0;
		}
	}

	public static int getCriticalChanceBonus(ISoliniaLivingEntity entity, String skillname) {
		int critical_chance = 0;

		// All skills + Skill specific
		critical_chance += entity.getSpellBonuses(SpellEffectType.CriticalHitChance);

		// TODO - take items, aa spells etc into account
		if (critical_chance < -100)
			critical_chance = -100;

		return critical_chance;
	}

	public static int getCritDmgMod(String skillname) {
		int critDmg_mod = 0;
		// TODO take aa and item bonuses into affect
		return critDmg_mod;
	}

	public static boolean isValidSkill(String skillname) {
		if (skillname == null || skillname.equals(""))
			return false;
		
		try {
			for (ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces()) {
				if (skillname.toUpperCase().equals(race.getName().toUpperCase()))
					return true;
			}

			for (SkillType skillType : SkillType.values()) {
				if (skillType.name().toUpperCase().equals(skillname.toUpperCase()))
					return true;
			}
		} catch (CoreStateInitException e) {
			return false;
		}

		return false;
	}

	public static String uuidFromBase64(String str) {
		byte[] bytes = Base64.decodeBase64(str);
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		UUID uuid = new UUID(bb.getLong(), bb.getLong());
		return uuid.toString();
	}

	public static void DebugMessage(String string) {
		// System.out.println(string);
	}

	public static boolean hasSpellActive(ISoliniaLivingEntity target, ISoliniaSpell spell) {
		for (SoliniaActiveSpell activeSpell : target.getActiveSpells()) {
			if (activeSpell.getSpell().getId() == spell.getId())
				continue;

			return true;
		}

		return false;
	}

	public static Integer getDefaultNPCManaRegen(ISoliniaNPC npc) {
		if (npc.isBoss())
			return npc.getLevel() * Utils.getBossMPRegenMultipler();
		if (npc.isHeroic())
			return npc.getLevel() * Utils.getHeroicMPRegenMultipler();
		if (npc.isRaidboss())
			return npc.getLevel() * Utils.getRaidBossMPRegenMultipler();
		if (npc.isRaidheroic())
			return npc.getLevel() * Utils.getRaidHeroicMPRegenMultipler();
		return npc.getLevel() * 3;
	}

	public static List<String> getSpecialisationSkills() {
		List<String> validSpecialisationSkills = new ArrayList<String>();
		validSpecialisationSkills.add("ABJURATION");
		validSpecialisationSkills.add("ALTERATION");
		validSpecialisationSkills.add("CONJURATION");
		validSpecialisationSkills.add("DIVINATION");
		validSpecialisationSkills.add("EVOCATION");
		return validSpecialisationSkills;
	}

	public static void sendRaceInfo(CommandSender sender) throws CoreStateInitException {
		List<ISoliniaClass> classes = StateManager.getInstance().getConfigurationManager().getClasses();
		for (ISoliniaRace race : StateManager.getInstance().getConfigurationManager().getRaces()) {
			if (race.isAdmin())
				continue;

			String classBuilder = "";
			for (ISoliniaClass solclass : classes) {
				if (solclass.getValidRaces().contains(race.getId()))
					classBuilder += solclass.getName() + " ";
			}

			TextComponent tc = new TextComponent();
			tc.setText(ChatColor.RED + "~ RACE: " + ChatColor.GOLD + race.getName().toUpperCase() + ChatColor.GRAY
					+ " [" + race.getId() + "] - " + ChatColor.RESET);
			TextComponent tc2 = new TextComponent();
			tc2.setText("Hover for more details");
			String details = ChatColor.GOLD + race.getName() + ChatColor.RESET + "\nRecommended Alignment: "
					+ ChatColor.GOLD + race.getAlignment() + ChatColor.RESET + "\n" + race.getDescription() + "\nSTR: "
					+ ChatColor.GOLD + race.getStrength() + ChatColor.RESET + " STA: " + ChatColor.GOLD
					+ race.getStamina() + ChatColor.RESET + " AGI: " + ChatColor.GOLD + race.getAgility()
					+ ChatColor.RESET + " DEX: " + ChatColor.GOLD + race.getDexterity() + ChatColor.RESET + " INT: "
					+ ChatColor.GOLD + race.getIntelligence() + ChatColor.RESET + " WIS: " + ChatColor.GOLD
					+ race.getWisdom() + ChatColor.RESET + " CHA: " + ChatColor.GOLD + race.getCharisma()
					+ ChatColor.GOLD + " \nClasses: " + ChatColor.RESET + classBuilder;
			tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(details).create()));
			tc.addExtra(tc2);
			sender.spigot().sendMessage(tc);
		}
	}

	public static boolean isInventoryMerchant(Inventory inventory) {
		if (inventory.getSize() != 27) {
			// System.out.println("Inventory size not 27");
			return false;
		}

		if (inventory.getStorageContents()[19] == null) {
			// System.out.println("Identifier is null");
			return false;
		}

		try {
			ItemStack identifierStack = inventory.getStorageContents()[19];
			if (!identifierStack.getItemMeta().getDisplayName().startsWith("MERCHANT:")) {
				// System.out.println("Missing start with merchant on identifier");
				return false;
			}

			if (identifierStack.getEnchantmentLevel(Enchantment.DURABILITY) != 999) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static UUID getInventoryUniversalMerchant(Inventory inventory) {
		if (!isInventoryMerchant(inventory))
			return null;

		ItemStack identifierStack = inventory.getStorageContents()[19];

		return UUID.fromString(identifierStack.getItemMeta().getLore().get(0));
	}

	public static int getInventoryPage(Inventory inventory) {
		if (!isInventoryMerchant(inventory))
			return 0;

		ItemStack identifierStack = inventory.getStorageContents()[19];

		return Integer.parseInt(identifierStack.getItemMeta().getLore().get(1));
	}

	public static int getInventoryNextPage(Inventory inventory) {
		if (!isInventoryMerchant(inventory))
			return 0;

		ItemStack identifierStack = inventory.getStorageContents()[19];

		return Integer.parseInt(identifierStack.getItemMeta().getLore().get(2));
	}

	public static int getMaxSkillValue() {
		// TODO Auto-generated method stub
		return 255;
	}

	// Heroic

	public static float getHeroicRunSpeed() {
		// TODO Auto-generated method stub
		return 0.4f;
	}

	public static int getHeroicDamageMultiplier() {
		// TODO Auto-generated method stub
		return 4;
	}

	public static int getHeroicHPMultiplier() {
		// TODO Auto-generated method stub
		return 20;
	}

	public static int getHeroicMPRegenMultipler() {
		// TODO Auto-generated method stub
		return 10;
	}

	// Boss

	public static float getBossRunSpeed() {
		// TODO Auto-generated method stub
		return 0.4f;
	}

	public static int getBossDamageMultiplier() {
		// TODO Auto-generated method stub
		return 10;
	}

	public static int getBossHPMultiplier() {
		// TODO Auto-generated method stub
		return 200;
	}

	public static int getBossMPRegenMultipler() {
		// TODO Auto-generated method stub
		return 40;
	}

	// Raid Heroic

	public static float getRaidHeroicRunSpeed() {
		// TODO Auto-generated method stub
		return 0.4f;
	}

	public static int getRaidHeroicDamageMultiplier() {
		// TODO Auto-generated method stub
		return 10;
	}

	public static int getRaidHeroicHPMultiplier() {
		// TODO Auto-generated method stub
		return 200;
	}

	public static int getRaidHeroicMPRegenMultipler() {
		// TODO Auto-generated method stub
		return 40;
	}

	// Raid Boss

	public static float getRaidBossRunSpeed() {
		// TODO Auto-generated method stub
		return 0.5f;
	}

	public static int getRaidBossDamageMultiplier() {
		// TODO Auto-generated method stub
		return 30;
	}

	public static int getRaidBossHPMultiplier() {
		// TODO Auto-generated method stub
		return 1000;
	}

	public static int getRaidBossMPRegenMultipler() {
		// TODO Auto-generated method stub
		return 200;
	}

	public static int getBaseInstrumentSoftCap() {
		// TODO Auto-generated method stub
		return 36;
	}

	public static boolean IsBardInstrumentSkill(SkillType skill) {
		switch (skill) {
		case BrassInstruments:
		case Singing:
		case StringedInstruments:
		case WindInstruments:
		case PercussionInstruments:
			return true;
		default:
			return false;
		}
	}

	public static void playSpecialEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		int sai = activeSpell.getSpell().getSpellAffectIndex();

		SpellEffectIndex effectType = Utils.getSpellEffectIndex(sai);
		if (effectType == null) {
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			return;
		}

		switch (effectType) {
		case Summon_Mount_Unclass:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Direct_Damage:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Heal_Cure:
			SpecialEffectUtils.playLoveEffect(entity, activeSpell);
			break;
		case AC_Buff:
			SpecialEffectUtils.playShieldEffect(entity, activeSpell);
			break;
		case AE_Damage:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Summon:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Sight:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Mana_Regen_Resist_Song:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Stat_Buff:
			SpecialEffectUtils.playShieldEffect(entity, activeSpell);
			break;
		case Vanish:
			SpecialEffectUtils.playPortalEffect(entity, activeSpell);
			break;
		case Appearance:
			SpecialEffectUtils.playSmokeEffect(entity, activeSpell);
			break;
		case Enchanter_Pet:
			SpecialEffectUtils.playSmokeEffect(entity, activeSpell);
			break;
		case Calm:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Fear:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Dispell_Sight:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Stun:
			SpecialEffectUtils.playStunEffect(entity, activeSpell);
			break;
		case Haste_Runspeed:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Combat_Slow:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Damage_Shield:
			SpecialEffectUtils.playShieldEffect(entity, activeSpell);
			break;
		case Cannibalize_Weapon_Proc:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Weaken:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Banish:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Blind_Poison:
			SpecialEffectUtils.playBleedEffect(entity, activeSpell);
			break;
		case Cold_DD:
			SpecialEffectUtils.playColdEffect(entity, activeSpell);
			break;
		case Poison_Disease_DD:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Fire_DD:
			SpecialEffectUtils.playFlameEffect(entity, activeSpell);
			break;
		case Memory_Blur:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Gravity_Fling:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Suffocate:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Lifetap_Over_Time:
			SpecialEffectUtils.playBleedEffect(entity, activeSpell);
			break;
		case Fire_AE:
			SpecialEffectUtils.playFlameEffect(entity, activeSpell);
			break;
		case Cold_AE:
			SpecialEffectUtils.playColdEffect(entity, activeSpell);
			break;
		case Poison_Disease_AE:
			SpecialEffectUtils.playPoisonEffect(entity, activeSpell);
			break;
		case Teleport:
			SpecialEffectUtils.playPortalEffect(entity, activeSpell);
			break;
		case Direct_Damage_Song:
			SpecialEffectUtils.playMusicEffect(entity, activeSpell);
			break;
		case Combat_Buff_Song:
			SpecialEffectUtils.playMusicEffect(entity, activeSpell);
			break;
		case Calm_Song:
			SpecialEffectUtils.playMusicEffect(entity, activeSpell);
			break;
		case Firework:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Firework_AE:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Weather_Rocket:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Convert_Vitals:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case NPC_Special_60:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case NPC_Special_61:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case NPC_Special_62:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case NPC_Special_63:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case NPC_Special_70:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case NPC_Special_71:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case NPC_Special_80:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		case Trap_Lock:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
			break;
		default:
			SpecialEffectUtils.playLegacy(entity, activeSpell);
		}
	}

	public static void dismountEntity(LivingEntity livingEntity) {
		Entity vehicle = livingEntity.getVehicle();
		if (vehicle != null) {
			vehicle.eject();
		}
	}

	public static void AddPotionEffect(LivingEntity entity, PotionEffectType effectType, int amplifier) {
		entity.addPotionEffect(new PotionEffect(effectType, Utils.GetPotionEffectTickLength(effectType), amplifier),
				true);
	}

	public static int GetPotionEffectTickLength(PotionEffectType effectType) {
		if (effectType == PotionEffectType.NIGHT_VISION) {
			return 16 * 20;
		}

		return 8 * 20;
	}

	public static boolean IsSoliniaItem(ItemStack itemStack) {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		
		String soliniaid = compound.getString("soliniaid");
		
		return soliniaid.matches("-?\\d+");
	}

	public static int getMaxUnspentAAPoints() {
		// TODO Auto-generated method stub
		return 1000;
	}

	public static void DropLoot(int lootTableId, World world, Location location) {
		try {
			ISoliniaLootTable table = StateManager.getInstance().getConfigurationManager().getLootTable(lootTableId);

			List<ISoliniaLootDropEntry> absoluteitems = new ArrayList<ISoliniaLootDropEntry>();
			List<ISoliniaLootDropEntry> rollitems = new ArrayList<ISoliniaLootDropEntry>();

			for (ISoliniaLootTableEntry entry : StateManager.getInstance().getConfigurationManager()
					.getLootTable(table.getId()).getEntries()) {
				ISoliniaLootDrop droptable = StateManager.getInstance().getConfigurationManager()
						.getLootDrop(entry.getLootdropid());
				for (ISoliniaLootDropEntry dropentry : StateManager.getInstance().getConfigurationManager()
						.getLootDrop(droptable.getId()).getEntries()) {

					if (dropentry.isAlways() == true) {
						absoluteitems.add(dropentry);
						continue;
					}

					rollitems.add(dropentry);
				}
			}

			// Now we have prepared our loot list items let's choose which will
			// drop

			// System.out.println("Prepared a Loot List of ABS: " + absoluteitems.size() + "
			// and ROLL: " + rollitems.size());

			if (absoluteitems.size() == 0 && rollitems.size() == 0)
				return;

			int dropcount = StateManager.getInstance().getWorldPerkDropCountModifier();

			Random r = new Random();
			int randomInt = r.nextInt(100) + 1;

			if (rollitems.size() > 0) {
				// Based on the chance attempt to drop this item
				for (int i = 0; i < dropcount; i++) {
					ISoliniaLootDropEntry droptableentry = rollitems.get(new Random().nextInt(rollitems.size()));
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(droptableentry.getItemid());
					
					if (item == null)
					{
						System.out.println("Missing item id [" + droptableentry.getItemid() + "] in lootdrop id [" + droptableentry.getLootdropid() + "].. skipping..");
						continue;
					}
					
					if (item.isNeverDrop())
						continue;
					
					randomInt = r.nextInt(100) + 1;
					// System.out.println("Rolled a " + randomInt + " against a max of " +
					// droptableentry.getChance()+ " for item: " + item.getDisplayname());

					// Handle unique item checking also
					if (item.isArtifact() == true && item.isArtifactFound() == true)
						continue;

					if (randomInt <= droptableentry.getChance()) {

						// Handle unique item setting also
						if (item.isArtifact() == true && item.isArtifactFound() == false)
						{
							item.setArtifactFound(true);
							StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
						}

						if (item.isArtifact() == true)
						{
							Utils.BroadcastPlayers("A unique artifact [" + item.getDisplayname() + "] has been discovered!");
						}
						world.dropItem(location, item.asItemStack());

					}
				}
			}

			// Always drop these items
			if (absoluteitems.size() > 0) {
				for (int i = 0; i < absoluteitems.size(); i++) {
					ISoliniaItem item = StateManager.getInstance().getConfigurationManager()
							.getItem(absoluteitems.get(i).getItemid());
					for (int c = 0; c < absoluteitems.get(i).getCount(); c++) {

						if (item.isNeverDrop())
							continue;
						
						// Handle unique item checking also
						if (item.isArtifact() == true && item.isArtifactFound() == true)
							continue;

						if (item.isArtifact() == true)
						{
							Utils.BroadcastPlayers("A unique artifact [" + item.getDisplayname() + "] has been discovered!");
						}
						
						world.dropItem(location, item.asItemStack());

						// Handle unique item setting also
						if (item.isArtifact() == true && item.isArtifactFound() == false)
						{
							StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
							item.setArtifactFound(true);
						}

					}
				}
			}
		} catch (CoreStateInitException e) {
			//
		}
	}

	public static ItemStack getTargetingItemStack() {
		ItemStack itemStack = new ItemStack(Material.LEGACY_SKULL_ITEM);
		itemStack.setItemMeta(ItemStackAdapter.buildSkull((SkullMeta) itemStack.getItemMeta(), UUID.fromString("9c3bb224-bc6e-4da8-8b15-a35c97bc3b16"), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFlNDI1YzViYTlmM2MyOTYyYjM4MTc4Y2JjMjMxNzJhNmM2MjE1YTExYWNjYjkyNzc0YTQ3MTZlOTZjYWRhIn19fQ==", null));
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add("This is a targetting tool");
		lore.add("Right click on an entity with this");
		lore.add("Left click to target self");
		lore.add("To clear, right click on nothing");
		
		itemMeta.setLore(lore);
		itemStack.setDurability((short) 3);
		itemMeta.setDisplayName("Targetting Tool");
		itemStack.setItemMeta(itemMeta);
		itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 997);
		return itemStack;
	}

	public static void despawnBoatIfNotNearWater(Boat entity) {
		int y = (int)entity.getLocation().getY();
		if (!(
			entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y+1,(int)entity.getLocation().getZ()).getType().equals(Material.LEGACY_STATIONARY_WATER) ||
			entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y+1,(int)entity.getLocation().getZ()).getType().equals(Material.WATER) ||
			entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y,(int)entity.getLocation().getZ()).getType().equals(Material.LEGACY_STATIONARY_WATER) ||
			entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y,(int)entity.getLocation().getZ()).getType().equals(Material.WATER) ||
			entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y-1,(int)entity.getLocation().getZ()).getType().equals(Material.LEGACY_STATIONARY_WATER) ||
			entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y-1,(int)entity.getLocation().getZ()).getType().equals(Material.WATER) ||
			entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y-2,(int)entity.getLocation().getZ()).getType().equals(Material.LEGACY_STATIONARY_WATER) ||
			entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y-2,(int)entity.getLocation().getZ()).getType().equals(Material.WATER)
				))
		{
			System.out.println("Despawned Boat on: " + entity.getWorld().getBlockAt((int)entity.getLocation().getX(),y,(int)entity.getLocation().getZ()).getType().name());
			entity.remove();
		}
		
	}

	public static int getMinLevelFromLevel(int highestlevel) {
		int minlevel = 1;
		if (highestlevel <= 14)
		{
			minlevel = highestlevel - 5;
			if (minlevel < 1)
				return 1;
			
			return minlevel;
		}
		
		minlevel = (highestlevel / 3)*2;
		if (minlevel < 1)
			return 1;
		
		return minlevel;
	}

	public static int getMaxLimitInclude() {
		// TODO Auto-generated method stub
		return 16;
	}
	
	public static int getMaxProcs() {
		// TODO Auto-generated method stub
		return 4;
	}

	public static String getStringFromTimestamp(Timestamp timestamp) {
		Instant instant = timestamp.toInstant(); 
		OffsetDateTime odt = OffsetDateTime.now ();
		ZoneOffset zoneOffset = odt.getOffset ();
		
		ZoneId zoneId = ZoneId.of( zoneOffset.getId() );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "uuuu.MM.dd.HH.mm.ss" );
		String output = zdt.format( formatter );
		return output;
	}
	
	public static List<String> GetRunic()
	{
		List<String> runic = new ArrayList<String>();
		runic.add("ᚠ");
		runic.add("ᚡ");
		runic.add("ᚢ");
		runic.add("ᚣ");
		runic.add("ᚤ");
		runic.add("ᚥ");
		runic.add("ᚦ");
		runic.add("ᚧ");
		runic.add("ᚨ");
		runic.add("ᚩ");
		runic.add("ᚪ");
		runic.add("ᚫ");
		runic.add("ᚬ");
		runic.add("ᚭ");
		runic.add("ᚮ");
		runic.add("ᚯ");
		runic.add("ᚰ");
		runic.add("ᚱ");
		runic.add("ᚲ");
		runic.add("ᚳ");
		runic.add("ᚴ");
		runic.add("ᚵ");
		runic.add("ᚶ");
		runic.add("ᚷ");
		runic.add("ᚸ");
		runic.add("ᚹ");
		runic.add("ᚺ");
		runic.add("ᚻ");
		runic.add("ᚼ");
		runic.add("ᚽ");
		runic.add("ᚾ");
		runic.add("ᚿ");
		runic.add("ᛀ");
		runic.add("ᛁ");
		runic.add("ᛂ");
		runic.add("ᛃ");
		runic.add("ᛄ");
		runic.add("ᛅ");
		runic.add("ᛆ");
		runic.add("ᛇ");
		runic.add("ᛈ");
		runic.add("ᛉ");
		runic.add("ᛊ");
		runic.add("ᛋ");
		runic.add("ᛌ");
		runic.add("ᛍ");
		runic.add("ᛎ");
		runic.add("ᛏ");
		runic.add("ᛐ");
		runic.add("ᛑ");
		runic.add("ᛒ");
		runic.add("ᛓ");
		runic.add("ᛔ");
		runic.add("ᛕ");
		runic.add("ᛖ");
		runic.add("ᛗ");
		runic.add("ᛘ");
		runic.add("ᛙ");
		runic.add("ᛚ");
		runic.add("ᛛ");
		runic.add("ᛜ");
		runic.add("ᛝ");
		runic.add("ᛞ");
		runic.add("ᛟ");
		runic.add("ᛠ");
		runic.add("ᛡ");
		runic.add("ᛢ");
		runic.add("ᛣ");
		runic.add("ᛤ");
		runic.add("ᛥ");
		runic.add("ᛦ");
		runic.add("ᛧ");
		runic.add("ᛨ");
		runic.add("ᛩ");
		runic.add("ᛪ");
		runic.add("᛫");
		runic.add("᛬");
		runic.add("᛭");
		runic.add("ᛮ");
		runic.add("ᛯ");
		runic.add("ᛰ");
		return runic;
}

	public static String ConvertToRunic(String message) {
		List<String> runicChars = GetRunic();
		
		String newmessage = "";
		for(int i = 0; i < message.length(); i++)
		{
			if (message.toCharArray()[i] == ' ')
			{
				newmessage += message.toCharArray()[i];
			} else {
				newmessage += getRandomItemFromList(runicChars);
			}
		}
		
		return newmessage;
	}

	public static void spinLivingEntity(LivingEntity livingEntity) {
		Location newLocation = livingEntity.getLocation();
		// todo
		newLocation.setYaw(0.0f);
		livingEntity.teleport(newLocation);
	}
	
	public static Location getLocationAroundCircle(Location center, double radius, double angleInRadian, double y) {
        double x = center.getX() + radius * Math.cos(angleInRadian);
        double z = center.getZ() + radius * Math.sin(angleInRadian);
   

        Location loc = new Location(center.getWorld(), x, y, z);
        Vector difference = center.toVector().clone().subtract(loc.toVector());
        loc.setDirection(difference);

        return loc;
    }

	public static double getNPCDefaultDamage(ISoliniaNPC npc) {
		int damage = Utils.getMaxDamage(npc.getLevel(), 75);
		if (npc.isHeroic())
		{
			damage += (Utils.getHeroicDamageMultiplier() * npc.getLevel());
		}
		
		if (npc.isBoss())
		{
			damage += (Utils.getBossDamageMultiplier() * npc.getLevel());
		}
		
		if (npc.isRaidheroic())
		{
			damage += (Utils.getRaidHeroicDamageMultiplier() * npc.getLevel());
		}
		
		if (npc.isRaidboss())
		{
			damage += (Utils.getRaidBossDamageMultiplier() * npc.getLevel());
		}

		return damage;
	}

	public static double DistanceOverAggroLimit(LivingEntity attacker, LivingEntity aggroCheckEntity) 
	{
		double distance = attacker.getLocation().distance(aggroCheckEntity.getLocation());
		if (distance > 100D)
			return 100D-distance;
		
		net.minecraft.server.v1_13_R2.EntityLiving entity = ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity)aggroCheckEntity).getHandle();
		if (entity == null)
			return 0D;
		
		if (entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE) == null)
			return 0D;
		
		double distanceLimit = entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).getValue();
		
		if (attacker.isOp())
		{
			System.out.println("OP Attacker Distance check: " + distanceLimit);
		}
		
		if (distance > distanceLimit)
			return distance-distanceLimit;
		
		return 0D;
	}

	public static boolean IsNight(World world) {
	    long time = world.getTime();

	    return time > 0 && time < 12300;
	}

	public static boolean isSkullItem(ItemStack itemStack) {
		if (itemStack.getType().name().equals("SKULL_ITEM"))
			return true;
		
		if (itemStack.getType().name().equals("LEGACY_SKULL_ITEM"))
			return true;
		
		return false;
	}

	public static void RecommitNpcs() {
		try {
			System.out.println("Recommiting all NPCs via provider");
			for (ISoliniaNPC npc : StateManager.getInstance().getConfigurationManager().getNPCs()) {
				try {
					npc.editSetting("name", npc.getName());
					StateManager.getInstance().getEntityManager().getNPCEntityProvider().updateNpc(npc);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidNpcSettingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (CoreStateInitException e) {

		}
	}
	
	public static void dispatchCommandLater(Plugin plugin, String command) {
		final Plugin pluginToSend = plugin;
		final CommandSender senderToSend = pluginToSend.getServer().getConsoleSender();
		final String commandToSend = command;
		new BukkitRunnable() {
	        
            @Override
            public void run() {
            	pluginToSend.getServer().dispatchCommand(senderToSend,commandToSend);
            }
            
        }.runTaskLater(plugin, 10);
	}
	
	public static void dispatchCommandLater(Plugin plugin, CommandSender sender, String command) {
		final Plugin pluginToSend = plugin;
		final CommandSender senderToSend = sender;
		final String commandToSend = command;
		new BukkitRunnable() {
	        
            @Override
            public void run() {
    			pluginToSend.getServer().dispatchCommand(senderToSend,commandToSend);
            }
            
        }.runTaskLater(plugin, 10);
		
		
	}

	public static NumHit getNumHitsType(Integer numhitstype) {
		switch(numhitstype)
		{
			case 0:
				return NumHit.None;
			case 1:
				return NumHit.IncomingHitAttempts;  // Attempted incoming melee attacks (hit or miss) on YOU.
			case 2:
				return NumHit.OutgoingHitAttempts;  // Attempted outgoing melee attacks (hit or miss) on YOUR TARGET.
			case 3:
				return NumHit.IncomingSpells;       // Incoming detrimental spells
			case 4:
				return NumHit.OutgoingSpells;       // Outgoing detrimental spells
			case 5:
				return NumHit.OutgoingHitSuccess;   // Successful outgoing melee attack HIT on YOUR TARGET.
			case 6:
				return NumHit.IncomingHitSuccess;   // Successful incoming melee attack HIT on YOU.
			case 7:
				return NumHit.MatchingSpells;       // Any casted spell matching/triggering a focus effect.
			case 8:
				return NumHit.IncomingDamage;       // Successful incoming spell or melee dmg attack on YOU
			case 9:
				return NumHit.ReflectSpell;	 // Incoming Reflected spells.
			case 10:
				return NumHit.DefensiveSpellProcs; // Defensive buff procs
			case 11:
				return NumHit.OffensiveSpellProcs;  // Offensive buff procs
			default:
				return NumHit.None;
		}
	}

	public static void ClearHateAndResetNpcsNotInList(List<UUID> entitiesNearPlayers) {
		try
		{
			List<UUID> activeHateLists = StateManager.getInstance().getEntityManager().getActiveHateListUUIDs();
			for(UUID uuid : activeHateLists)
			{
				try
				{
					if (entitiesNearPlayers.contains(uuid))
						continue;
					
					Entity entity = Bukkit.getEntity(uuid);
					if (entity == null)
					{
						StateManager.getInstance().getEntityManager().clearHateList(uuid);
						continue;
					}
					
					if (!(entity instanceof LivingEntity))
					{
						continue;
					}
					
					ISoliniaLivingEntity solEntity = SoliniaLivingEntityAdapter.Adapt((LivingEntity)entity);
					solEntity.clearHateList();
					solEntity.resetPosition(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		} catch (CoreStateInitException e)
		{
			
		}
	}
}
