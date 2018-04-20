package com.solinia.solinia.Factories;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.SoliniaItemException;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Interfaces.ISoliniaItem;
import com.solinia.solinia.Managers.ConfigurationManager;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaItem;
import com.solinia.solinia.Utils.Utils;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class SoliniaItemFactory {
	public static ISoliniaItem CreateItem(ItemStack itemStack, boolean operatorCreated) throws SoliniaItemException, CoreStateInitException {
		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		SoliniaItem item = new SoliniaItem();
		item.setOperatorCreated(operatorCreated);
		item.setId(StateManager.getInstance().getConfigurationManager().getNextItemId());
		item.setBasename(itemStack.getType().name());
		item.setDisplayname(itemStack.getType().name());
		
		if (itemStack.getData() != null)
		{
			try
			{
				item.setColor(itemStack.getData().getData());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (itemStack.getType().name().equals("SKULL_ITEM"))
	    {
			net.minecraft.server.v1_12_R1.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
			NBTTagCompound tag = stack.hasTag() ? stack.getTag() : new NBTTagCompound();

			tag.getCompound("SkullOwner").getString("Id");
			String texturevalue = tag.getCompound("SkullOwner").getCompound("Properties").getList("textures", 10).get(0).getString("Value");
			
			item.setTexturebase64(texturevalue);
	    }
		
		StateManager.getInstance().getConfigurationManager().addItem(item);
		System.out.println("New Item Added: " + item.getId() + " - " + item.getDisplayname());
		StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
		return item;
	}

	public static List<Integer> CreateClassItemSet(ISoliniaClass classtype, int armourtier, String partialname, boolean prefixClassName, boolean operatorCreated) throws SoliniaItemException {
		List<Integer> items = new ArrayList<Integer>();
		
		try
		{
			StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
			// Get the appropriate material for the class and generate the base item
			ISoliniaItem headItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultHeadMaterial().toUpperCase())),operatorCreated);
			ISoliniaItem chestItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultChestMaterial().toUpperCase())),operatorCreated);
			ISoliniaItem legsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultLegsMaterial().toUpperCase())),operatorCreated);
			ISoliniaItem feetItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultFeetMaterial().toUpperCase())),operatorCreated);
			ISoliniaItem handItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaulthandMaterial().toUpperCase())),operatorCreated);
			ISoliniaItem offhandItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.valueOf(classtype.getDefaultoffHandMaterial().toUpperCase())),operatorCreated);
			
			// Jewelry!
			ISoliniaItem neckItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.SKULL_ITEM), operatorCreated);
			neckItem.setNeckItem(true);
			neckItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRhYjc3ZWVmYWQwYjBjZGJkZjMyNjFhN2E0NzI5ZDU1MDRkNmY5NmQzYzE2MjgzMjE5NzQ0M2ViZTM0NmU2In19fQ==");
			ISoliniaItem shouldersItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.SKULL_ITEM), operatorCreated);
			shouldersItem.setShouldersItem(true);
			shouldersItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFjYTdjZWY3YmMyOTI3ZWI5NGQ0YTY5MGE0MTQ4YTIxNDk4MjJlM2E2MGMwNjExYWEyYTNhNjUzM2I3NzE1In19fQ==");
			ISoliniaItem fingersItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.SKULL_ITEM), operatorCreated);
			fingersItem.setFingersItem(true);
			fingersItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE4M2M4OGRiOTg0MjZjNjRjMzdlNmQ3ODlkNGVjMWUzZGU0M2VmYWFmZTRiZTE2MTk2MWVmOTQzZGJlODMifX19");
			ISoliniaItem earsItem = SoliniaItemFactory.CreateItem(new ItemStack(Material.SKULL_ITEM), operatorCreated);
			earsItem.setEarsItem(true);
			earsItem.setTexturebase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFiYTc0ZDgxMmYzYzVlOTdhZDBmMWU2Y2IxZDI0ZmM5ZTEzNzg4MTk2Y2YxYmM0NzMyMTFmZjE0MmJlYWIifX19");

			items.add(headItem.getId());
			items.add(chestItem.getId());
			items.add(legsItem.getId());
			items.add(feetItem.getId());
			items.add(handItem.getId());
			items.add(offhandItem.getId());
			items.add(neckItem.getId());
			items.add(shouldersItem.getId());
			items.add(fingersItem.getId());
			items.add(earsItem.getId());
			
			for(Integer i : items)
			{
				ISoliniaItem item = StateManager.getInstance().getConfigurationManager().getItem(i);
				List<String> classNames = new ArrayList<String>();
				classNames.add(classtype.getName().toUpperCase());
				item.setAllowedClassNames(classNames);

				item.setWorth(armourtier*25);
				// Randomise the stats of the class armour so we get more unique content in each dungeon
				int rarityChance = Utils.RandomBetween(1, 100);
				int rarityBonus = 0;
				String rarityName = "";

				if (rarityChance > 80) {
					rarityName = "Uncommon ";
					rarityBonus = 1;
				}

				if (rarityChance > 85) {
					rarityName = "Rare ";
					rarityBonus = 3;
				}

				if (rarityChance > 92) {
					rarityName = "Legendary ";
					rarityBonus = 5;
				}
				
				if (rarityChance > 96) {
					rarityName = "Mythical ";
					rarityBonus = 6;
				}

				if (rarityChance > 98) {
					rarityName = "Ancient ";
					rarityBonus = 7;
				}

				
				String className = "";
				if (prefixClassName == true)
				{
					className = classtype.getClassItemPrefix().toLowerCase();
					className = Utils.FormatAsName(className);
					className += " ";
				}
				
				if (!item.isJewelry())
				{
					item.setDisplayname(rarityName + className + classtype.getItemArmorTypeName(item.getBasename().toUpperCase()) + " " + partialname);
				} else {
					String jewelryTypeName = "Jewelry";
					
					if (item.isEarsItem())
						jewelryTypeName = "Earrings";

					if (item.isNeckItem())
						jewelryTypeName = "Necklace";
					
					if (item.isFingersItem())
						jewelryTypeName = "Rings";

					if (item.isShouldersItem())
						jewelryTypeName = "Cloak";
					
					
					item.setDisplayname(rarityName + className + jewelryTypeName + " " + partialname);
				}
				
				int tierMin = 0;
				
				int baseAmount = 5;
				
				if (item.isJewelry())
				{
					baseAmount = 2;
				}
				
				int baseDoubler = baseAmount * 2;
				
				int tierMax = armourtier * baseAmount;
				if (armourtier > 1)
					tierMin =+ (baseAmount * armourtier) - baseAmount;
				
				int lvlMin = 0;
				if (armourtier > 1)
					lvlMin =+ (baseDoubler * armourtier) - baseDoubler;
				
				if (lvlMin > Utils.getMaxLevel())
				{
					lvlMin = Utils.getMaxLevel();
				}
				
				item.setMinLevel(lvlMin);
				
				int classStrBonus = classtype.getItemGenerationBonus("strength");
				int classStaBonus = classtype.getItemGenerationBonus("stamina");
				int classAgiBonus = classtype.getItemGenerationBonus("agility");
				int classDexBonus = classtype.getItemGenerationBonus("dexterity");
				int classIntBonus = classtype.getItemGenerationBonus("intelligence");
				int classWisBonus = classtype.getItemGenerationBonus("wisdom");
				int classChaBonus = classtype.getItemGenerationBonus("charisma");
				int classAcBonus = classtype.getItemGenerationBonus("ac");
				

				// Unless there is a bonus defined, the class doesnt seem to use that statistic
				
				if (classStrBonus > 0)
					item.setStrength(Utils.RandomBetween(tierMin, tierMax) + rarityBonus + classStrBonus);
				if (classStaBonus > 0)
					item.setStamina(Utils.RandomBetween(tierMin, tierMax) + rarityBonus + classStaBonus);
				if (classAgiBonus > 0)
					item.setAgility(Utils.RandomBetween(tierMin, tierMax) + rarityBonus + classAgiBonus);
				if (classDexBonus > 0)
					item.setDexterity(Utils.RandomBetween(tierMin, tierMax) + rarityBonus + classDexBonus);
				if (classIntBonus > 0)
					item.setIntelligence(Utils.RandomBetween(tierMin, tierMax) + rarityBonus+classIntBonus);
				if (classWisBonus > 0)
					item.setWisdom(Utils.RandomBetween(tierMin, tierMax) + rarityBonus+classWisBonus);
				if (classChaBonus > 0)
					item.setCharisma(Utils.RandomBetween(tierMin, tierMax) + rarityBonus+classChaBonus);
				
				// Damage
				if (ConfigurationManager.HandMaterials.contains(item.getBasename().toUpperCase()))
				{
					if (!item.getBasename().toUpperCase().equals("SHIELD"))
					{
						int dmgMin = tierMin;
						int dmgMax = tierMax;
						if (dmgMin < 6)
						{
							dmgMin = 6;
						}
						
						if (dmgMax < 7)
							dmgMax = 7;
						
						int damage = Utils.RandomBetween(dmgMin, dmgMax) + rarityBonus + classStrBonus;
						item.setDamage(damage);
					} else {
						item.setAC(SoliniaItemFactory.generateArmourClass(classAcBonus, armourtier, rarityBonus));
					}
				}
				
				if (ConfigurationManager.ArmourMaterials.contains(item.getBasename().toUpperCase()))
				{
					item.setAC(SoliniaItemFactory.generateArmourClass(classAcBonus, armourtier, rarityBonus));
				}
				
				// mana
				item.setMana(Utils.RandomBetween(0,armourtier + rarityBonus)*10);
								
				// hp
				item.setHp(Utils.RandomBetween(0,armourtier + rarityBonus)*10);
				
				// resists
				item.setColdResist(Utils.RandomBetween(0,armourtier + rarityBonus));
				item.setFireResist(Utils.RandomBetween(0,armourtier + rarityBonus));
				item.setMagicResist(Utils.RandomBetween(0,armourtier + rarityBonus));
				item.setPoisonResist(Utils.RandomBetween(0,armourtier + rarityBonus));
				item.setDiseaseResist(Utils.RandomBetween(0,armourtier + rarityBonus));
				
				// TODO class procs?
				StateManager.getInstance().getConfigurationManager().setItemsChanged(true);
			}
		
		} catch (CoreStateInitException e)
		{
			return new ArrayList<Integer>();
		}
		
		return items;
	}

	public static int generateArmourClass(int classAcBonus, int armourTier, int rarityBonus) {
		// AC
		int acMultiplier = classAcBonus;
		if (acMultiplier < 1)
			acMultiplier = 1;
		
		int acMin = 0;
		int acMax = armourTier * acMultiplier;
		if (armourTier > 1)
			acMin =+ (acMultiplier * armourTier) - acMultiplier;

		int armourClass = Utils.RandomBetween(acMin,acMax)  + rarityBonus;
		return armourClass;
	}
}
