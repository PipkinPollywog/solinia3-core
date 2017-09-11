package com.solinia.solinia.Models;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidNpcSettingException;
import com.solinia.solinia.Interfaces.ISoliniaFaction;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Interfaces.ISoliniaLootDrop;
import com.solinia.solinia.Interfaces.ISoliniaLootDropEntry;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Interfaces.ISoliniaLootTableEntry;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Interfaces.ISoliniaNPCMerchantEntry;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.chat.ComponentSerializer;

public class SoliniaNPC implements ISoliniaNPC {
	private int id;
	private String name;
	private String mctype = "SKELETON"; // do not use zombie, it ignores assist rules
	private int level = 1;
	private int factionid;
	private boolean usedisguise = false;
	private String disguisetype;
	private String headitem;
	private String chestitem;
	private String legsitem;
	private String feetitem;
	private String handitem;
	private String offhanditem;
	private boolean boss = false;
	private boolean burning = false;
	private boolean invisible = false;
	private boolean customhead = false;
	private String customheaddata;
	private int merchantid;
	private boolean upsidedown = false;
	private int loottableid;
	private int raceid;
	private int classid;
	private boolean isRandomSpawn = false;

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
	public String getMctype() {
		return mctype;
	}

	@Override
	public void setMctype(String mctype) {
		this.mctype = mctype;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int getFactionid() {
		return factionid;
	}

	@Override
	public void setFactionid(int factionid) {
		this.factionid = factionid;
	}

	@Override
	public boolean isUsedisguise() {
		return usedisguise;
	}

	@Override
	public void setUsedisguise(boolean usedisguise) {
		this.usedisguise = usedisguise;
	}

	@Override
	public String getDisguisetype() {
		return disguisetype;
	}

	@Override
	public void setDisguisetype(String disguisetype) {
		this.disguisetype = disguisetype;
	}

	@Override
	public String getHeaditem() {
		return headitem;
	}

	@Override
	public void setHeaditem(String headitem) {
		this.headitem = headitem;
	}

	@Override
	public String getChestitem() {
		return chestitem;
	}

	@Override
	public void setChestitem(String chestitem) {
		this.chestitem = chestitem;
	}

	@Override
	public String getLegsitem() {
		return legsitem;
	}

	@Override
	public void setLegsitem(String legsitem) {
		this.legsitem = legsitem;
	}

	@Override
	public String getFeetitem() {
		return feetitem;
	}

	@Override
	public void setFeetitem(String feetitem) {
		this.feetitem = feetitem;
	}

	@Override
	public String getHanditem() {
		return handitem;
	}

	@Override
	public void setHanditem(String handitem) {
		this.handitem = handitem;
	}

	@Override
	public String getOffhanditem() {
		return offhanditem;
	}

	@Override
	public void setOffhanditem(String offhanditem) {
		this.offhanditem = offhanditem;
	}

	@Override
	public boolean isBoss() {
		return boss;
	}

	@Override
	public void setBoss(boolean boss) {
		this.boss = boss;
	}

	@Override
	public boolean isBurning() {
		return burning;
	}

	@Override
	public void setBurning(boolean burning) {
		this.burning = burning;
	}

	@Override
	public boolean isInvisible() {
		return invisible;
	}

	@Override
	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	@Override
	public boolean isCustomhead() {
		return customhead;
	}

	@Override
	public void setCustomhead(boolean customhead) {
		this.customhead = customhead;
	}

	@Override
	public String getCustomheaddata() {
		return customheaddata;
	}

	@Override
	public void setCustomheaddata(String customheaddata) {
		this.customheaddata = customheaddata;
	}

	@Override
	public int getMerchantid() {
		return merchantid;
	}

	@Override
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}

	@Override
	public boolean isUpsidedown() {
		return upsidedown;
	}

	@Override
	public void setUpsidedown(boolean upsidedown) {
		this.upsidedown = upsidedown;
	}

	@Override
	public int getLoottableid() {
		return loottableid;
	}

	@Override
	public void setLoottableid(int loottableid) {
		this.loottableid = loottableid;
	}

	@Override
	public int getRaceid() {
		return raceid;
	}

	@Override
	public void setRaceid(int raceid) {
		this.raceid = raceid;
	}

	@Override
	public int getClassid() {
		return classid;
	}

	@Override
	public void setClassid(int classid) {
		this.classid = classid;
	}

	@Override
	public void sendMerchantItemListToPlayer(Player player) 
	{
		String debugLastJson = "";
		try
		{
			for (ISoliniaNPCMerchantEntry merchantitem : StateManager.getInstance().getConfigurationManager()
					.getNPCMerchant(this.getMerchantid()).getEntries()) {
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(merchantitem.getItemid());
				int price = item.getWorth();
	
				// Buy 1
				final String jsonbuy = "{\"text\":\"~ (CLICK-BUY) " + item.getDisplayname() + " x1: $" + price
						+ "\",\"color\":\"aqua\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcbuy "
						+ merchantitem.getItemid() + " 1\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
						+ item.asJsonStringEscaped() + "\"}}";
				player.spigot().sendMessage(ComponentSerializer.parse(jsonbuy));
	
				// Buy 64
				final String jsonbuy64 = "{\"text\":\"~ (CLICK-BUY) " + item.getDisplayname() + " x64: $" + (price * 64)
						+ "\",\"color\":\"aqua\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcbuy "
						+ merchantitem.getItemid() + " 64\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
						+ item.asJsonStringEscaped() + "\"}}";
				player.spigot().sendMessage(ComponentSerializer.parse(jsonbuy64));
	
			}
	
			for (int i = 0; i < 36; i++) {
				ItemStack itemstack = player.getInventory().getItem(i);
				if (itemstack == null)
					continue;
	
				if (itemstack.getType().equals(Material.AIR))
					continue;
	
				int ench = itemstack.getEnchantmentLevel(Enchantment.OXYGEN);
				if (ench < 1000)
					continue;
	
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(itemstack);
				if (item == null)
					continue;
	
				int sellprice = item.getWorth();
	
				// List all items sellable by player
				final String jsonsell = "{\"text\":\"~ (CLICK-SELL) " + item.getDisplayname() + " x1: $" + sellprice
						+ "\",\"color\":\"yellow\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcsell "
						+ item.getId() + " 1\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
						+ item.asJsonStringEscaped() + "\"}}";

				player.spigot().sendMessage(ComponentSerializer.parse(jsonsell));

				if (itemstack.getAmount() > 1) {
					final String jsonsellmany = "{\"text\":\"~ (CLICK-SELL) " + item.getDisplayname() + " x"
							+ itemstack.getAmount() + ": $" + (sellprice * itemstack.getAmount())
							+ "\",\"color\":\"yellow\",\"underlined\":false,\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/npcsell "
							+ item.getId() + " " + itemstack.getAmount()
							+ "\"},\"hoverEvent\":{\"action\":\"show_item\",\"value\":\""
							+ item.asJsonStringEscaped() + "\"}}";
					player.spigot().sendMessage(ComponentSerializer.parse(jsonsellmany));
				}
			}
		} catch (CoreStateInitException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			player.sendMessage("* This npc is currently not trading");
			e.printStackTrace();
			System.out.println("Last Json Was:");
			System.out.println(debugLastJson);
		}
	}

	@Override
	public void sendNpcSettingsToSender(CommandSender sender) throws CoreStateInitException {
		sender.sendMessage(ChatColor.RED + "NPC Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("- raceid: " + ChatColor.GOLD + getRaceid() + ChatColor.RESET);
		sender.sendMessage("- professionid: " + ChatColor.GOLD + getClassid() + ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "STATS" + ChatColor.RESET);
		sender.sendMessage("- level: " + ChatColor.GOLD + getLevel() + ChatColor.RESET);
		sender.sendMessage("  - HP: " + ChatColor.GOLD + getMaxHP() + ChatColor.RESET);
		sender.sendMessage("  - Damage: " + ChatColor.GOLD + getMaxDamage() + ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "APPEARANCE" + ChatColor.RESET);
		sender.sendMessage("- mctype: " + ChatColor.GOLD + getMctype() + ChatColor.RESET);
		sender.sendMessage("- usedisguise: " + ChatColor.GOLD + isUsedisguise() + ChatColor.RESET);
		sender.sendMessage("- disguisetype: " + ChatColor.GOLD + getDisguisetype() + ChatColor.RESET);
		sender.sendMessage("- customhead: " + ChatColor.GOLD + isCustomhead() + ChatColor.RESET);
		sender.sendMessage("- customheaddata: " + ChatColor.GOLD + getCustomheaddata() + ChatColor.RESET);
		sender.sendMessage("- upsidedown: " + ChatColor.GOLD + isUpsidedown() + ChatColor.RESET);
		sender.sendMessage("- burning: " + ChatColor.GOLD + isBurning() + ChatColor.RESET);
		sender.sendMessage("- invisible: " + ChatColor.GOLD + isInvisible() + ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "EQUIPMENT" + ChatColor.RESET);
		if (getLoottableid() != 0) {
			sender.sendMessage("- loottableid: " + ChatColor.GOLD + getLoottableid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getLoottableid()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- loottableid: " + ChatColor.GOLD + getLoottableid() + " (No Loot Table)" + ChatColor.RESET);
		}
		sender.sendMessage("- handitem: " + ChatColor.GOLD + getHanditem() + ChatColor.RESET);
		sender.sendMessage("- offhanditem: " + ChatColor.GOLD + getOffhanditem() + ChatColor.RESET);
		sender.sendMessage("- headitem: " + ChatColor.GOLD + getHeaditem() + ChatColor.RESET);
		sender.sendMessage("- chestitem: " + ChatColor.GOLD + getChestitem() + ChatColor.RESET);
		sender.sendMessage("- legsitem: " + ChatColor.GOLD + getLegsitem() + ChatColor.RESET);
		sender.sendMessage("- feetitem: " + ChatColor.GOLD + getFeetitem() + ChatColor.RESET);
		sender.sendMessage(ChatColor.RED + "FACTION & MERCHANT" + ChatColor.RESET);
		if (getFactionid() != 0) {
			sender.sendMessage("- factionid: " + ChatColor.GOLD + getFactionid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getFaction(getFactionid()).getName() + ")"
					+ ChatColor.RESET);
		} else {
			sender.sendMessage("- factionid: " + ChatColor.GOLD + getFactionid() + " (No Faction)" + ChatColor.RESET);
		}
		if (getMerchantid() != 0) {
			sender.sendMessage("- merchantid: " + ChatColor.GOLD + getMerchantid() + " ("
					+ StateManager.getInstance().getConfigurationManager().getNPCMerchant(getMerchantid()).getName()
					+ ")" + ChatColor.RESET);
		} else {
			sender.sendMessage(
					"- merchantid: " + ChatColor.GOLD + getMerchantid() + " (No Merchant Table)" + ChatColor.RESET);
		}
		sender.sendMessage(ChatColor.RED + "MISC" + ChatColor.RESET);
		sender.sendMessage("- boss: " + ChatColor.GOLD + isBoss());
		sender.sendMessage("----------------------------");
		if (getLoottableid() != 0) {
			sender.sendMessage(ChatColor.RED + "LOOT" + ChatColor.RESET + "[" + getLoottableid() + "] - " + "("
					+ StateManager.getInstance().getConfigurationManager().getLootTable(getLoottableid()).getName()
					+ ")");
			ISoliniaLootTable loottable = StateManager.getInstance().getConfigurationManager()
					.getLootTable(getLoottableid());
			for (ISoliniaLootTableEntry le : StateManager.getInstance().getConfigurationManager()
					.getLootTable(loottable.getId()).getEntries()) {
				ISoliniaLootDrop ld = StateManager.getInstance().getConfigurationManager()
						.getLootDrop(le.getLootdropid());
				sender.sendMessage(
						"- " + ChatColor.GOLD + ld.getName().toUpperCase() + ChatColor.RESET + "[" + ld.getId() + "]:");
				for (ISoliniaLootDropEntry lde : StateManager.getInstance().getConfigurationManager()
						.getLootDrop(ld.getId()).getEntries()) {
					ISoliniaItem i = StateManager.getInstance().getConfigurationManager().getItem(lde.getItemid());
					sender.sendMessage("  - " + ChatColor.GOLD + i.getDisplayname() + ChatColor.RESET + "[" + i.getId()
							+ "] - " + lde.getChance() + "% chance Count: " + lde.getCount() + " Always: "
							+ lde.isAlways());
				}
			}
		}

	}

	@Override
	public int getMaxDamage() {
		// TODO Auto-generated method stub
		double basedmg = ((level * 0.45) + 0.8);

		double racestatbonus = 75 + (level * 5);
		double bonus = racestatbonus / 100;
		double damagemlt = basedmg * bonus;
		double newdmg = damagemlt;
		double damagepct = newdmg / basedmg;

		return (int) Math.floor(basedmg * damagepct);
	}

	@Override
	public int getMaxHP() {
		double levelmultiplier = 15;

		double hp = level * levelmultiplier;
		double stamina = 75;
		double hpmain = (stamina / 12) * level;

		double calculatedhp = hp + hpmain;
		return (int) Math.floor(calculatedhp);
	}

	@Override
	public void editSetting(String setting, String value)
			throws InvalidNpcSettingException, NumberFormatException, CoreStateInitException {
		String name = getName();
		String mctype = getMctype();
		int level = getLevel();
		int factionid = getFactionid();
		boolean usedisguise = isUsedisguise();
		String disguisetype = getDisguisetype();
		String headitem = getHeaditem();
		String chestitem = getChestitem();
		String legsitem = getLegsitem();
		String feetitem = getFeetitem();
		String handitem = getHanditem();
		String offhanditem = getOffhanditem();
		boolean boss = isBoss();
		boolean burning = isBurning();
		boolean invisible = isInvisible();
		boolean customhead = isCustomhead();
		String customheaddata = getCustomheaddata();
		int merchantid = getMerchantid();
		boolean upsidedown = isUpsidedown();
		int loottableid = getLoottableid();
		int raceid = getRaceid();
		int classid = getClassid();

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidNpcSettingException("Name is empty");

			if (value.length() > 15)
				throw new InvalidNpcSettingException("Name is longer than 15 characters");
			setName(value);
			break;
		case "mctype":
			setMctype(value);
			break;
		case "level":
			setLevel(Integer.parseInt(value));
			break;
		case "factionid":
			if (Integer.parseInt(value) == 0) {
				setFactionid(Integer.parseInt(value));
				break;
			}

			ISoliniaFaction faction = StateManager.getInstance().getConfigurationManager()
					.getFaction(Integer.parseInt(value));
			if (faction == null)
				throw new InvalidNpcSettingException("Faction ID does not exist");
			setFactionid(Integer.parseInt(value));
			break;
		case "usedisguise":
			setUsedisguise(Boolean.parseBoolean(value));
			break;
		case "disguisetype":
			setDisguisetype(value);
			break;
		case "headitem":
			setHeaditem(value);
			break;
		case "chestitem":
			setChestitem(value);
			break;
		case "legsitem":
			setLegsitem(value);
			break;
		case "feetitem":
			setFeetitem(value);
			break;
		case "handitem":
			setHanditem(value);
			break;
		case "offhanditem":
			setOffhanditem(value);
			break;
		case "boss":
			setBoss(Boolean.parseBoolean(value));
			break;
		case "burning":
			setBurning(Boolean.parseBoolean(value));
			break;
		case "invisible":
			setInvisible(Boolean.parseBoolean(value));
			break;
		case "customhead":
			setCustomhead(Boolean.parseBoolean(value));
			break;
		case "customheaddata":
			// fetches custom head texture by a player name
			setCustomheaddata(Utils.getTextureFromName(value));
			break;
		case "merchantid":
			if (StateManager.getInstance().getConfigurationManager().getNPCMerchant(Integer.parseInt(value)) == null)
				throw new InvalidNpcSettingException("MerchantID does not exist");
			setMerchantid(Integer.parseInt(value));
			break;
		case "upsidedown":
			setUpsidedown(Boolean.parseBoolean(value));
			break;
		case "loottableid":
			setLoottableid(Integer.parseInt(value));
			break;
		case "raceid":
			setRaceid(Integer.parseInt(value));
			break;
		case "classid":
			setClassid(Integer.parseInt(value));
			break;
		case "randomspawn":
			setRandomSpawn(Boolean.parseBoolean(value));
			break;
		default:
			throw new InvalidNpcSettingException(
					"Invalid NPC setting. Valid Options are: name,mctype,health,damage,factionid,usedisguise,disguisetype,headitem,chestitem,legsitem,feetitem,handitem,offhanditem,boss,burning,invisible,customhead,customheaddata,merchantid,upsidedown,loottableid,randomspawn");
		}
	}

	@Override
	public boolean isRandomSpawn() {
		return isRandomSpawn;
	}

	@Override
	public void setRandomSpawn(boolean isRandomSpawn) {
		this.isRandomSpawn = isRandomSpawn;
	}
}