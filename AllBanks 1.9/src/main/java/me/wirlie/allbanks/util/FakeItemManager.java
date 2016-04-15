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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Banks;

/**
 * @author Wirlie
 *
 */
public class FakeItemManager extends BukkitRunnable {
	
	private static HashMap<Location, Item> itemsLoc = new HashMap<Location, Item>();
	
	private FakeItemManager(){
		//Privatizar constructor
		loadBackup();
	}
	
	private void loadBackup() {
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getBackupFile());
		
		readkeys:
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
						//El parámetro "readkeys" interrumpe el estado for() anterior
						continue readkeys;
					}
				}
			}
			
			//No existe, intentar regenerar
			Location calculateItemLoc = signLoc.clone().subtract(0, 0.1, 0).add(0.5, 0, 0.5);
			ItemStack shopItem = ShopUtil.getItemStack(signLoc);
			
			if(shopItem == null){
				continue;
			}
			
			shopItem.setAmount(1);
			
			Item item = signLoc.getWorld().dropItem(calculateItemLoc, shopItem);
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
		
		Location calculateItemLoc = signLoc.clone().subtract(0, 0.1, 0).add(0.5, 0, 0.5);
		ItemStack craftItem = ShopUtil.getItemStack(signLoc);
		
		craftItem.setAmount(1);
		
		Item item = signLoc.getWorld().dropItem(calculateItemLoc, craftItem);
		item.setPickupDelay(Integer.MAX_VALUE);
		item.setVelocity(new Vector(0, 0, 0));
		item.teleport(calculateItemLoc);
		
		//Registrar datos
		yaml.set(transformSignlocToKey(signLoc) + ".itemUUID", item.getUniqueId().toString());
		yaml.set(transformSignlocToKey(signLoc) + ".itemLoc", StringLocationUtil.convertLocationToString(calculateItemLoc, false));
	
		//añadir al mapa
		itemsLoc.put(signLoc, item);
		
		try {
			yaml.save(getBackupFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void DespawnFakeItemForShop(Location signLoc){
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getBackupFile());
		
		if(!yaml.contains(transformSignlocToKey(signLoc))) return;
		
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
		//Eliminarl del mapa
		itemsLoc.remove(signLoc);
		
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
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getBackupFile());
		
		Iterator<Entry<Location, Item>> it = itemsLoc.entrySet().iterator();
		HashMap<Location, Item> replaceLater = new HashMap<Location, Item>();
		
		while(it.hasNext()){
			Entry<Location, Item> entry = it.next();
			
			//Parámetros
			Item item = entry.getValue();
			Location signLoc = entry.getKey();
			
			//Comprobar si el objeto existe
			if(item.isDead()){
				//¿El letrero de AllBanks existe?
				if(!signLoc.getBlock().getType().equals(Material.WALL_SIGN) || !Banks.signIsAllBanksSign((Sign) signLoc.getBlock().getState())){
					yaml.set(transformSignlocToKey(signLoc), null);
					continue;
				}

				Location calculateItemLoc = signLoc.clone().subtract(0, 0.1, 0).add(0.5, 0, 0.5);
				
				//Tratamos de despejar el bloque en donde se colocará el objeto
				Block b = calculateItemLoc.getBlock();
				
				if(!materialIsSlab(b.getType()) && !b.getType().equals(Material.AIR)){
					b.breakNaturally();
				}
				
				//Ahora, buscaremos el bloque de abajo
				Block downb = b.getRelative(BlockFace.DOWN);
				
				if(downb.getType().equals(Material.AIR)){
					downb.setType(Material.LOG);
				}
				
				//No existe, intentar regenerar
				ItemStack craftItem = ShopUtil.getItemStack(signLoc);
				craftItem.setAmount(1);
				
				item = signLoc.getWorld().dropItem(calculateItemLoc, craftItem);
				item.setPickupDelay(Integer.MAX_VALUE);
				item.setVelocity(new Vector(0, 0, 0));
				item.teleport(calculateItemLoc);
				
				//Actualizar datos
				yaml.set(transformSignlocToKey(signLoc) + ".itemUUID", item.getUniqueId().toString());
				yaml.set(transformSignlocToKey(signLoc) + ".itemLoc", StringLocationUtil.convertLocationToString(calculateItemLoc, false));
			
				//Regenerado, colocar en el mapa
				replaceLater.put(signLoc, item);
			}else{
				//¿El letrero de AllBanks existe?
				if(!signLoc.getBlock().getType().equals(Material.WALL_SIGN) || !Banks.signIsAllBanksSign((Sign) signLoc.getBlock().getState())){
					yaml.set(transformSignlocToKey(signLoc), null);
					continue;
				}
				
				//Comprobar si el objeto no se encuentra lejos de su origen
				Location calculateItemLoc = signLoc.clone().subtract(0, 0.1, 0).add(0.5, 0, 0.5);
				
				if(Math.abs(calculateItemLoc.getBlockX() - item.getLocation().getBlockX()) > 1
						|| Math.abs(calculateItemLoc.getBlockY() - item.getLocation().getBlockY()) > 1
						|| Math.abs(calculateItemLoc.getBlockZ() - item.getLocation().getBlockZ()) > 1){
					//Se encuentra lejos
					
					//Tratamos de despejar el bloque en donde se colocará el objeto
					Block b = calculateItemLoc.getBlock();
					
					if(!materialIsSlab(b.getType()) && !b.getType().equals(Material.AIR)){
						b.breakNaturally();
					}
					
					//Ahora, buscaremos el bloque de abajo
					Block downb = b.getRelative(BlockFace.DOWN);
					
					if(downb.getType().equals(Material.AIR)){
						downb.setType(Material.LOG);
					}
					
					//Transportar
					item.teleport(calculateItemLoc);
				}
			}
		}
		
		itemsLoc.putAll(replaceLater);
		
		try {
			yaml.save(getBackupFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean materialIsSlab(Material material){
		if(material.equals(Material.STEP)
				|| material.equals(Material.WOOD_STEP)
				|| material.equals(Material.PURPUR_SLAB)
				|| material.equals(Material.STONE_SLAB2)){
			return true;
		}
		
		return false;
	}
	
	public static String transformSignlocToKey(Location signLoc){
		return signLoc.getWorld().getName() + ">>>" + signLoc.getBlockX() + ">>>" + signLoc.getBlockY() + ">>>" + signLoc.getBlockZ();
	}
	
	public static Location transformKeyToSignloc(String key){
		String[] split = key.split(">>>");
		
		return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
	}

}
