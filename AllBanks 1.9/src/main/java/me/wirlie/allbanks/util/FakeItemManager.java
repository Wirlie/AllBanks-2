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
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import me.wirlie.allbanks.AllBanks;

/**
 * @author Wirlie
 *
 */
public class FakeItemManager extends BukkitRunnable {
	
	private static BiMap<Location, Item> itemsLoc = HashBiMap.create();
	
	private FakeItemManager(){
		//Privatizar constructor
		loadBackup();
	}
	
	private void loadBackup() {
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getBackupFile());
		for(String key : yaml.getKeys(false)){
			//Parámetros
			String itemLocStr = yaml.getString(key + ".itemLoc", null);
			String itemUUIDStr = yaml.getString(key + ".itemUUID", null);
			//Transformar parámetros en valores
			Location signLoc = transformKeyToSignloc(key);
			Location itemLoc = StringLocationUtil.convertStringToLocation(itemLocStr);
			UUID itemUUID = UUID.fromString(itemUUIDStr);
			
			//Comprobar si existe
			for(Entity e : Util.getNearbyEntities(itemLoc, 2)){
				if(e instanceof Item){
					if(e.getUniqueId().equals(itemUUID)){
						//existe, colocar en el mapa
						itemsLoc.put(signLoc, (Item) e);
						continue;
					}
				}
			}
			
			//No existe, intentar regenerar
			Location calculateItemLoc = signLoc.clone().subtract(0, 1, 0).add(0.5, 0, 0.5);
			ItemStack craftItem = ShopUtil.getItemStack(signLoc);
			craftItem.setAmount(1);
			
			Item item = signLoc.getWorld().dropItem(calculateItemLoc, craftItem);
			item.setPickupDelay(Integer.MAX_VALUE);
			item.setVelocity(new Vector(0, 0, 0));
			item.teleport(calculateItemLoc);
			
			//Actualizar datos
			yaml.set(key + ".itemUUID", item.getUniqueId().toString());
			yaml.set(key + ".itemLoc", StringLocationUtil.convertLocationToString(calculateItemLoc, false));
		
			//Regenerado, colocar en el mapa
			itemsLoc.put(signLoc, item);
		}
		
		//Guardar, en caso de que se hayan hecho cambios.
		try {
			yaml.save(getBackupFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void SpawnFakeItemForShop(Location signLoc){
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getBackupFile());
		
		Location calculateItemLoc = signLoc.clone().subtract(0, 1, 0).add(0.5, 0, 0.5);
		ItemStack craftItem = ShopUtil.getItemStack(signLoc);
		craftItem.setAmount(1);
		
		Item item = signLoc.getWorld().dropItem(calculateItemLoc, craftItem);
		item.setPickupDelay(Integer.MAX_VALUE);
		item.setVelocity(new Vector(0, 0, 0));
		item.teleport(calculateItemLoc);
		
		//Registrar datos
		yaml.set(transformSignlocToKey(signLoc) + ".itemUUID", item.getUniqueId().toString());
		yaml.set(transformSignlocToKey(signLoc) + ".itemLoc", StringLocationUtil.convertLocationToString(calculateItemLoc, false));
	
	}
	
	public static void DespawnFakeItemForShop(Location signLoc){
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getBackupFile());
		
		//Obtener datos
		String itemUUIDStr = yaml.getString(transformSignlocToKey(signLoc) + ".itemUUID", null);
		
		UUID itemUUID = UUID.fromString(itemUUIDStr);
		
		//Intentar
		for(Entity e : Util.getNearbyEntities(signLoc, 5)){
			if(e instanceof Item){
				if(e.getUniqueId().equals(itemUUID)){
					e.remove();
					break;
				}
			}
		}
		
		//Quitar letrero del registro
		yaml.set(transformSignlocToKey(signLoc), null);
		
		//Guardar
		try {
			yaml.save(getBackupFile());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private static File getBackupFile(){
		File backupFile = new File(AllBanks.getInstance().getDataFolder() + File.separator + "ItemManagerBackup_dont_edit_this.yml");
		File pluginFolder = AllBanks.getInstance().getDataFolder();
		
		if(!pluginFolder.exists()) pluginFolder.mkdirs();
		
		if(!backupFile.exists()){
			try {
				backupFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return backupFile;
	}

	private static boolean initialized = false;
	
	public static void initializeItemManeger(){
		if(initialized) return;

		new FakeItemManager().runTaskTimer(AllBanks.getInstance(), 20 * 3, 20 * 10);
		
		initialized = true;
	}

	public void run() {
		
	}
	
	public static String transformSignlocToKey(Location signLoc){
		return signLoc.getWorld().getName() + ">>>" + signLoc.getBlockX() + ">>>" + signLoc.getBlockY() + ">>>" + signLoc.getBlockZ();
	}
	
	public static Location transformKeyToSignloc(String key){
		String[] split = key.split(">>>");
		
		return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
	}

}
