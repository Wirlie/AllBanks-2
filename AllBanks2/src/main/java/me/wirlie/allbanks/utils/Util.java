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
package me.wirlie.allbanks.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Console;
import me.wirlie.allbanks.StringsID;

/**
 * Utilidades
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Util {
	
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists() && directory.isDirectory()){
	        File[] files = directory.listFiles();
	        if(null != files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    
	    return (directory.delete());
	}

	public static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {

	            public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	            	int res = e2.getValue().compareTo(e1.getValue());
	                if (e1.getKey().equals(e2.getKey())) {
	                    return res; // Code will now handle equality properly
	                } else {
	                    return res != 0 ? res : 1; // While still adding all entries
	                }
	            }
	            
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
	public static Entity[] getNearbyEntities(Location l, int radius) {	
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		HashSet<Entity> radiusEntities = new HashSet<Entity>();

		for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
				for (Entity e: new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
					if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
						radiusEntities.add(e);
				}
			}
		}
		
		return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}

	public enum CompareVersionResult{
		VERSION_1_IS_GREATER,
		VERSION_2_IS_GREATER,
		VERSION_EQUALS,
		NUMBER_FORMAT_EXCEPTION
	}
	
	/**
	 * Compara dos versiones del tipo {@code 1.0.0}.
	 * @param pluginVersion Version
	 * @param bukkitVersion Version que se desea comparar con {@code arg0}
	 * @return 1 si {@code version1} > {@code version2} <br> 0 si {@code arg0} = {@code arg1} <br> -1 si {@code version1} < {@code version2} <br> -2 si la operación falla
	 */
	
	public static CompareVersionResult compareVersionsString(String version1, String version2) {
		if(version1.equalsIgnoreCase(version2)) return CompareVersionResult.VERSION_EQUALS;
		
    	List<String> version1Split = new ArrayList<String>(Arrays.asList(version1.split("\\D")));
		List<String> version2Split = new ArrayList<String>(Arrays.asList(version2.split("\\D")));
    	
    	int maxParameters = (version2Split.size() > version1Split.size()) ? version2Split.size() : version1Split.size();
    	
    	if(version2Split.size() != maxParameters) {
    		//Igualar version 2 a version 1
    		int excedent = maxParameters - version2Split.size();
    		for(int i = 0; i < excedent; i++) {
    			version2Split.add("0");
    		}
    	}else {
    		//Igualar version 1 a version 2
    		int excedent = maxParameters - version1Split.size();
    		for(int i = 0; i < excedent; i++) {
    			version1Split.add("0");
    		}
    	}
    	
    	for(int i = 0; i < maxParameters; i++) {
    		//Comprobar si es posible comparar ambos parámetros en ambos splits
			try {
				int version1Arg = Integer.parseInt(version1Split.get(i));
				int version2Arg = Integer.parseInt(version2Split.get(i));
				
				if(version1Arg < version2Arg) {
					return CompareVersionResult.VERSION_2_IS_GREATER;
				} else if(version1Arg > version2Arg) {
					return CompareVersionResult.VERSION_1_IS_GREATER;
				}
			}catch (NumberFormatException e) {
				return CompareVersionResult.NUMBER_FORMAT_EXCEPTION;
			}
    	}
    	
    	//??
		return null;
	}
	
	//Directorios
	public static File FlatFile_signFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "SignData");
	public static File FlatFile_bankAccountFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "BankAccount");
	public static File FlatFile_pendingCharges = new File(AllBanks.getInstance().getDataFolder() + File.separator + "PendingCharge");
	
	public static boolean hasPermission(Player p, String str) {
		
		boolean permRegistered = false;
		
		if(Bukkit.getPluginManager().getPermission(str) != null) {
			permRegistered = true;
		}
		
		if(!p.hasPermission(str) && permRegistered) {
			//No tiene permiso desde el registro.
			return false;
		} else if(!p.hasPermission(str)){
			List<String> defaultPermissions = AllBanks.getInstance().getConfig().getStringList("default-permissions");
			if(defaultPermissions.contains(str)) {
				//Es un permiso default
				return true;
			}else {
				//No es un permiso default, no tiene permiso
				return false;
			}
		} else {
			//en este resultado, hasPermission es true
			return true;
		}
	}
	
	public static String capitalizeFirstLetter(String text) {
		String[] split = text.split(" ");
		String finalStr = "";
		
		for(String s : split) {
			finalStr += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
		}
		
		return finalStr.substring(0, finalStr.length() - 1);
	}
	
	public static boolean hasPermission(CommandSender s, String str) {
		
		boolean permRegistered = false;
		
		if(Bukkit.getPluginManager().getPermission(str) != null) {
			permRegistered = true;
		}
		
		if(!s.hasPermission(str) && permRegistered) {
			//No tiene permiso desde el registro.
			return false;
		} else if(!s.hasPermission(str)){
			List<String> defaultPermissions = AllBanks.getInstance().getConfig().getStringList("default-permissions");
			if(defaultPermissions.contains(str)) {
				//Es un permiso default
				return true;
			}else {
				//No es un permiso default, no tiene permiso
				return false;
			}
		} else {
			//en este resultado, hasPermission es true
			return true;
		}
	}

	public static String convertLocationToString(Location loc, boolean blockLocation){
		String returnStr;
		if(blockLocation)
			returnStr = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
		else
			returnStr = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
		
		return returnStr;
	}

	public static Location convertStringToLocation(String loc){
		
		String[] args = loc.split(":");
		return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
	}

	public static boolean stringIsValidLocation(String loc){
		
		String[] args = loc.split(":");
		if(args.length != 4) return false;
		
		if(Bukkit.getWorld(args[0]) == null) return false;
		
		try{
			Double.parseDouble(args[1]);
			Double.parseDouble(args[2]);
			Double.parseDouble(args[3]);
		}catch(NumberFormatException e){
			return false;
		}
		
		return true;
	}

	public static boolean entityIsHostil(Entity e){
		if(!Util.entityIsAnimal(e) && !Util.entityIsNeutral(e)){
			return true;
		}
		
		return false;
	}

	public static boolean entityIsNeutral(Entity e){
		if(e.getType().equals(EntityType.SNOWMAN) ||
				e.getType().equals(EntityType.IRON_GOLEM) ||
				e.getType().equals(EntityType.VILLAGER) ||
				e.getType().equals(EntityType.MINECART) ||
				e.getType().equals(EntityType.MINECART_CHEST) ||
				e.getType().equals(EntityType.MINECART_COMMAND) ||
				e.getType().equals(EntityType.MINECART_FURNACE) ||
				e.getType().equals(EntityType.MINECART_HOPPER) ||
				e.getType().equals(EntityType.MINECART_MOB_SPAWNER) ||
				e.getType().equals(EntityType.MINECART_TNT) ||
				e.getType().equals(EntityType.ARMOR_STAND) ||
				e.getType().equals(EntityType.BOAT) ||
				e.getType().equals(EntityType.DROPPED_ITEM) ||
				e.getType().equals(EntityType.ENDER_CRYSTAL) ||
				e.getType().equals(EntityType.ENDER_SIGNAL) ||
				e.getType().equals(EntityType.PAINTING) ||
				e.getType().equals(EntityType.ITEM_FRAME))
		{
			return true;
		}
		
		return false;
	}

	public static boolean entityIsAnimal(Entity e){
		if(e.getType().equals(EntityType.COW) ||
				e.getType().equals(EntityType.CHICKEN) ||
				e.getType().equals(EntityType.PIG) ||
				e.getType().equals(EntityType.SHEEP) ||
				e.getType().equals(EntityType.OCELOT) ||
				e.getType().equals(EntityType.WOLF) ||
				e.getType().equals(EntityType.HORSE))
		{
			return true;
		}
		
		return false;
	}
	
	public enum VersionPackage{
		NMS_1_9_R1,
		NMS_1_9_R2,
		NOT_SUPPORTED,
	}
	
	public static VersionPackage resolveNMSVersion(){
		String name = AllBanks.getInstance().getServer().getClass().getPackage().getName();
		String version = name.substring(name.lastIndexOf('.') + 1);
		
		if(version.equalsIgnoreCase("v1_9_R1")){
			return VersionPackage.NMS_1_9_R1;
		}else if(version.equalsIgnoreCase("v1_9_R2")){
			return VersionPackage.NMS_1_9_R2;
		}else{
			return VersionPackage.NOT_SUPPORTED;
		}
	}

	public static String getItemCodeOrGetCustomName(Object asNMSCopy) {
		if(resolveNMSVersion() == VersionPackage.NMS_1_9_R1){
			return Util_R1.getItemCodeOrGetCustomName((net.minecraft.server.v1_9_R1.ItemStack) asNMSCopy);
		}else{
			return Util_R2.getItemCodeOrGetCustomName((net.minecraft.server.v1_9_R2.ItemStack) asNMSCopy);
		}
	}

	public static ChatColor convertEnumChatFormatToChatColor(Object e) {
		if(resolveNMSVersion() == VersionPackage.NMS_1_9_R1){
			return Util_R1.convertEnumChatFormatToChatColor((net.minecraft.server.v1_9_R1.EnumChatFormat) e);
		}else{
			return Util_R2.convertEnumChatFormatToChatColor((net.minecraft.server.v1_9_R2.EnumChatFormat) e);
		}
	}

	/**
	 * Verificar si la versión del servidor es compatible con esta version de AllBanks.
	 * @return Resultado de la operación.
	 */
	public static AllBanks.VersionCheckResult verifyServerVersion() {
		
		AllBanksLogger.info("Verifying compatibles versions...");
		
		String rawBukkitVersion = Bukkit.getServer().getBukkitVersion();
		String rawVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
		
		
		//String[] version = Bukkit.getServer().getBukkitVersion().split("-");
		
		//String mcversion = version[0];
		//String bukkitversion = version[1];
		
		boolean compatible = false;
		
		for(String sv : AllBanks.COMPATIBLE_VERSIONS){
			if(rawVersion.equalsIgnoreCase(sv)){
				//La version es igual al minimo o maximo, no es necesario calcular nada
				compatible = true;
				break;
			}
		}
		
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		replaceMap.put("%1%", rawBukkitVersion);
		
		if(compatible){
			AllBanksLogger.info("You are using a compatible version of CraftBukkit.");
			Console.sendMessage(StringsID.YOU_ARE_RUNNING_A_COMPATIBLE_VERSION_OF_CB, replaceMap);
			return AllBanks.VersionCheckResult.COMPATIBLE;
		}else{
			//Detectar si se está usando una versión incompatible o una versión no probada
			if(compareVersionsString(AllBanks.INCOMPATIBLE_MIN, rawVersion) == CompareVersionResult.VERSION_1_IS_GREATER 
				|| compareVersionsString(AllBanks.INCOMPATIBLE_MIN, rawVersion) == CompareVersionResult.VERSION_EQUALS 
				|| compareVersionsString(AllBanks.INCOMPATIBLE_MAX, rawVersion) == CompareVersionResult.VERSION_2_IS_GREATER && !AllBanks.INCOMPATIBLE_MAX.equalsIgnoreCase("0")
				|| compareVersionsString(AllBanks.INCOMPATIBLE_MAX, rawVersion) == CompareVersionResult.VERSION_EQUALS && !AllBanks.INCOMPATIBLE_MAX.equalsIgnoreCase("0")) {
				AllBanksLogger.severe("Please use the correct version of CraftBukkit/Spigot.", true);
				AllBanksLogger.severe("For this build, CB 1.9 is expected.", true);
				return AllBanks.VersionCheckResult.NOT_COMPATIBLE;
			} else {
				AllBanksLogger.severe("You are not using a compatible version of CraftBukkit.");
				Console.sendMessage(StringsID.YOU_ARENT_RUNNING_A_COMPATIBLE_VERSION_OF_CB, replaceMap);
				return AllBanks.VersionCheckResult.PROCCEED_WITH_PRECAUTION;
			}
		}
		
	}

	/**
	 * Obtener la versión de Bukkit (sólamente la parte de la versión del servidor)
	 * @return Devuelve la versión de Bukkit en un formato de "1.0.0"
	 */
	public static String getBukkitVersion(){
		return Bukkit.getServer().getBukkitVersion().split("-")[0];
	}

	/**
	 * Actualizar la configuración del usuario con la configuración nativa de AllBanks.
	 */
	public static void UpdateConfigWithNativeFile(){
		//Copiar la anterior Configuración
		File Config = new File(AllBanks.getInstance().getDataFolder() + File.separator + "Config.yml");
		File tempConfig = new File(AllBanks.getInstance().getDataFolder() + File.separator + "TMP-Config.yml");
	
		AllBanksLogger.info("Renaming Config.yml to TMP-Config.yml");
		Config.renameTo(tempConfig);
		
		//guardar nueva configuración
		AllBanksLogger.info("Saving native resource: Config.yml");
		AllBanks.getInstance().saveResource("Config.yml", true);
		
		//Comenzar a comparar
		AllBanksLogger.info("Loading: TMP-Config.yml");
		YamlConfiguration nativeCfg = YamlConfiguration.loadConfiguration(Config);
		AllBanksLogger.info("Loading: Config.yml");
		YamlConfiguration userCfg = YamlConfiguration.loadConfiguration(tempConfig);
		
		AllBanksLogger.info("Searching for changes...");
		for(String key : nativeCfg.getKeys(true)){
			Object obj = userCfg.get(key, null);
			
			if(obj == null){
				userCfg.set(key, nativeCfg.get(key));
				AllBanksLogger.info("New entry: " + key);
			}
		}
		
		//Permisos default
		List<String> defaultPerms = userCfg.getStringList("default-permissions");
		
		if(compareVersionsString("1.1.4", userCfg.getString("cfg-version", "0")) == CompareVersionResult.VERSION_1_IS_GREATER){
			if(!defaultPerms.contains("allbanks.land.commands.plot.sethomespawn")) defaultPerms.add("allbanks.land.commands.plot.sethomespawn");
			if(!defaultPerms.contains("allbanks.land.commands.plot.setshopspawn")) defaultPerms.add("allbanks.land.commands.plot.setshopspawn");
			if(!defaultPerms.contains("allbanks.land.commands.plot.home")) defaultPerms.add("allbanks.land.commands.plot.home");
			if(!defaultPerms.contains("allbanks.land.commands.plot.teleport")) defaultPerms.add("allbanks.land.commands.plot.teleport");
			if(!defaultPerms.contains("allbanks.land.commands.plot.auto")) defaultPerms.add("allbanks.land.commands.plot.auto");
		}else if(compareVersionsString("1.1", userCfg.getString("cfg-version", "0")) == CompareVersionResult.VERSION_1_IS_GREATER){
			if(!defaultPerms.contains("allbanks.land.commands.plot.claim")) defaultPerms.add("allbanks.land.commands.plot.claim");
			if(!defaultPerms.contains("allbanks.land.commands.plot.unclaim")) defaultPerms.add("allbanks.land.commands.plot.unclaim");
			if(!defaultPerms.contains("allbanks.land.commands.plot.set.flags")) defaultPerms.add("allbanks.land.commands.plot.set.flags");
			if(!defaultPerms.contains("allbanks.land.commands.plot.add")) defaultPerms.add("allbanks.land.commands.plot.add");
			if(!defaultPerms.contains("allbanks.land.commands.plot.deny")) defaultPerms.add("allbanks.land.commands.plot.deny");
		}
		
		//Actualizar la versión
		AllBanksLogger.info("Updating cfg-version...");
		userCfg.set("cfg-version", AllBanks.getInstance().getDescription().getVersion());
		
		userCfg.set("default-permissions", defaultPerms);
		
		//guardar
		AllBanksLogger.info("Saving changes...");
		try {
			userCfg.save(tempConfig);
			AllBanksLogger.info("Success!");
		} catch (IOException e) {
			AllBanksLogger.severe("An error has ocurred while trying update Config.yml to the latest version. (IOException)", true);
			e.printStackTrace();
		}
		
		//eliminar configuración nativa
		AllBanksLogger.info("Removing temporal file (Config.yml)");
		Config.delete();
		//Cambiar configuración temporal a su estado normal
		AllBanksLogger.info("Renaming TMP-Config.yml to Config.yml (restore file)");
		tempConfig.renameTo(Config);
		AllBanksLogger.info("Success: 0 problems found.");
	}
}
