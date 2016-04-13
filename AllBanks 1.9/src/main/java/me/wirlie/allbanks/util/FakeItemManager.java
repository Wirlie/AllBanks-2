/*
 * AllBanks - AllBanks is a plugin for CraftBukkit and Spigot.
 * Copyright (C) 2016 Josue Israel Acevedo Peña
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package me.wirlie.allbanks.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.logger.AllBanksLogger;

/**
 * @author Wirlie
 *
 */
public class FakeItemManager extends BukkitRunnable {
	
	private static BiMap<Location, Item> itemsLoc = HashBiMap.create();
	
	private FakeItemManager(){
		//Privatizar constructor
	}
	
	private static boolean initialized = false;
	
	public static void initializeItemManeger(){
		if(initialized) return;
		
		AllBanksLogger.info("Initializing FakeItemManager module.");
		
		//restaurar backup
		restoreItemManagerBackup();
		
		//cada 5 segundos
		AllBanksLogger.info("Initializing runnable...");
		new FakeItemManager().runTaskTimer(AllBanks.getInstance(), 20 * 5, 20 * 5);
		
		initialized = true;
	}

	/**
	 * @param location
	 * @param itemStack
	 */
	
	public static void spawnFakeItemAt(Location signLoc, ItemStack item) {
		spawnFakeItemAt(signLoc, item, false);
	}

	public static void spawnFakeItemAt(Location signLoc, ItemStack item, boolean ignoreConfig) {
		
		if(item == null) return;
		
		if(!ignoreConfig && !AllBanks.getInstance().getConfig().getBoolean("shop.enable-fake-item", true)){
			AllBanksLogger.warning("Cannot spawn a fake item because this feature is disabled in Config.yml (loc: " + signLoc + " )");
			return;
		}
		
		//Corregir localización añadiendo 0.5 a las coordenadas X y Z y disminuyendo 1 en Y
		Location fixLoc = signLoc.add(0.5, 0, 0.5).subtract(0, 1, 0);
		
		//Si la localización donde se tratará de colocar el item ya está ocupado entonces ignoramos.
		if(!fixLoc.getBlock().getType().equals(Material.AIR)){
			AllBanksLogger.warning("Cannot spawn a fake item because the location is obstructed by another block (loc: " + signLoc + " )");
			return;
		}
		
		//Bien, intentar colocar el item falso
		ItemStack craftFakeItem = item.clone();
		craftFakeItem.setAmount(0);
		
		World w = fixLoc.getWorld();
		
		//Bloque de abajo del item: Madera
		Block downBlock = fixLoc.getBlock().getRelative(BlockFace.DOWN);
		if(!downBlock.getType().equals(Material.AIR)) downBlock.breakNaturally();
		downBlock.setType(Material.LOG);
		
		Item fakeItem = w.dropItem(fixLoc, craftFakeItem);
		fakeItem.setItemStack(craftFakeItem);
		fakeItem.setVelocity(new Vector(0, 0, 0));
		fakeItem.teleport(fixLoc);
		fakeItem.setPickupDelay(Integer.MAX_VALUE);
		
		registerFakeItem(fixLoc, fakeItem);
	}
	
	public static boolean despawnFakeItem(Location signLoc){
		File itemManagerFile = getItemManagerFile();
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(itemManagerFile);
		System.out.println("1");
		ConfigurationSection sec = yaml.getConfigurationSection("fakeitems");
		if(sec == null) return false;
		System.out.println("2");
		for(String key : sec.getKeys(false)){
			String signLocStr = yaml.getString("fakeitems." + key + ".signloc", null);
			System.out.println("3");
			if(signLocStr == null) return false;
			System.out.println("4");
			Location getLoc = StringLocationUtil.convertStringToLocation(signLocStr);
			System.out.println("5");
			if(getLoc.equals(signLoc)){
				return despawnFakeItem(UUID.fromString(key));
			}
		}
		
		return false;
	}
	
	public static boolean despawnFakeItem(UUID fakeItemUUID){
		File itemManagerFile = getItemManagerFile();
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(itemManagerFile);
		Location getLoc = StringLocationUtil.convertStringToLocation(yaml.getString("fakeitems." + fakeItemUUID + ".signloc"));
		
		Chunk c = getLoc.getBlock().getChunk();
		
		for(Entity e : c.getEntities()){
			if(e.getUniqueId().equals(fakeItemUUID)){
				e.remove();
				yaml.set("fakeitems." + fakeItemUUID, null);
				try {
					yaml.save(itemManagerFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				AllBanksLogger.debug("Fake item despawned. (uuid: " + fakeItemUUID + ")");
				
				return true;
			}
		}
		
		AllBanksLogger.warning("Wops! Item with UUID " + fakeItemUUID + " not found at " + getLoc);
		
		return false;
	}

	/**
	 * @param loc
	 * @param fakeItem
	 */
	public static void registerFakeItem(Location loc, Item item) {
		
		File itemManagerFile = getItemManagerFile();
		
		Location signLoc = loc.clone().add(0, 1, 0).subtract(0.5, 0, 0.5);
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(itemManagerFile);
		yaml.set("fakeitems." + item.getUniqueId() + ".loc", StringLocationUtil.convertLocationToString(loc, false));
		yaml.set("fakeitems." + item.getUniqueId() + ".signloc", StringLocationUtil.convertLocationToString(signLoc, true));
		
		try {
			yaml.save(itemManagerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		itemsLoc.put(loc, item);			
		
		AllBanksLogger.debug("New fake item registered. (uuid: " + item.getUniqueId() + ", loc:" + loc + ")");

	}
	
	public static void unregisterFakeItem(UUID fakeItemUUID) {
		
		File itemManagerFile = getItemManagerFile();
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(itemManagerFile);
		yaml.set("fakeitems." + fakeItemUUID, null);
		try {
			yaml.save(itemManagerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		itemsLoc.inverse().remove(fakeItemUUID);
		AllBanksLogger.debug("Fake item unregistered. (uuid: " + fakeItemUUID + ")");

	}
	
	public static void restoreItemManagerBackup(){
		AllBanksLogger.info("Restoring backup.");
		
		File itemManagerFile = getItemManagerFile();
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(itemManagerFile);
		
		if(yaml.getConfigurationSection("fakeitems") == null) return;
		
		for(String key : yaml.getConfigurationSection("fakeitems").getKeys(false)){
			UUID uuid = null;
			try{
				uuid = UUID.fromString(key);
			} catch (IllegalArgumentException e){
				continue;
			}
			
			String locStr = yaml.getString("fakeitems." + key + ".loc", null);
			String locSignStr = yaml.getString("fakeitems." + key + ".signloc", null);
			if(locStr == null || locSignStr == null || locStr.equalsIgnoreCase("") || locSignStr.equalsIgnoreCase("")) continue;
			
			Location getLoc = StringLocationUtil.convertStringToLocation(locStr);
			Location getSignLoc = StringLocationUtil.convertStringToLocation(locSignStr);
			
			Chunk c = getLoc.getChunk();
			
			for(Entity e : c.getEntities()){
				if(e.getUniqueId().equals(uuid) && e instanceof Item){
					itemsLoc.put(getLoc, (Item) e);
					return;
				}
			}
			
			//Si llega aquí, es por que no ha encontrado el item
			yaml.set("fakeitems." + key, null);
			
			try {
				yaml.save(itemManagerFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			Block b = getSignLoc.getBlock();
			if(!b.getType().equals(Material.WALL_SIGN)){
				return;
			}
			
			Sign s = (Sign) b.getState();
			
			spawnFakeItemAt(getLoc, ShopUtil.getItemStack(s));
		}
	}
	
	private static File getItemManagerFile(){
		File dataFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "data");
		File itemManagerFile = new File(dataFolder + File.separator + "fakeItemManagerBackup.yml");
		
		if(!dataFolder.exists()) dataFolder.mkdirs();
		
		if(!itemManagerFile.exists())
			try {
				itemManagerFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return itemManagerFile;
	}

	public void run() {
		System.out.println("EXEC");
		Iterator<Entry<Location, Item>> entryData = itemsLoc.entrySet().iterator();
		HashSet<Location> removeFromMap = new HashSet<Location>();
		HashMap<Location, Item> addToMap = new HashMap<Location, Item>();
		
		File itemManagerFile = getItemManagerFile();
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(itemManagerFile);
		
		while(entryData.hasNext()){
			Entry<Location, Item> data = entryData.next();
			
			Item item = data.getValue();
			Location loc = data.getKey();
			
			if(item.isDead()){
				//Si ya no existe, es necesario re-spawnearlo
				
				//Obtener la localización de la tienda revirtiendo los cálculos añadidos a la localización
				Location signLoc = loc.clone().add(0, 1, 0).subtract(0.5, 0, 0.5);
				Block b = signLoc.getBlock();
				
				if(!b.getType().equals(Material.WALL_SIGN)){
					//Ya no es un objeto válido...
					yaml.set("fakeitems." + item.getUniqueId(), null);
					removeFromMap.add(loc);
					continue;
				}
				
				Sign s = (Sign) b.getState();
				ItemStack itemstack = ShopUtil.getItemStack(s);
				itemstack.setAmount(0);
				Item newItem = loc.getWorld().dropItem(loc, itemstack);
				newItem.setVelocity(new Vector(0, 0, 0));
				newItem.teleport(loc);
				newItem.setPickupDelay(Integer.MAX_VALUE);
				
				addToMap.put(loc, newItem);

				yaml.set("fakeitems." + item.getUniqueId(), null);
				yaml.set("fakeitems." + newItem.getUniqueId() + ".loc", StringLocationUtil.convertLocationToString(loc, false));
				yaml.set("fakeitems." + newItem.getUniqueId() + ".signloc", StringLocationUtil.convertLocationToString(s.getLocation(), true));
				
				
			}else{
				//La distancia entre la localización original y el objeto es distinta?
				if(Math.abs(loc.getY() - item.getLocation().getY()) > 0.5
						|| Math.abs(loc.getX() - item.getLocation().getX()) > 0.5
						|| Math.abs(loc.getZ() - item.getLocation().getZ()) > 0.5){
					//Checar si abajo del item hay un bloque para sostenerlo
					Block downBlock = loc.getBlock().getRelative(BlockFace.DOWN);
					if(downBlock.getType().equals(Material.AIR)){
						downBlock.setType(Material.LOG);
					}
					
					//La localización está bloqueada?
					if(!loc.getBlock().getType().equals(Material.AIR)){
						loc.getBlock().breakNaturally();
					}
					
					item.teleport(loc);
				}
			}
		}
		
		itemsLoc.putAll(addToMap);
		
		for(Location l : removeFromMap){
			itemsLoc.remove(l);
		}
		
		try {
			yaml.save(itemManagerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
