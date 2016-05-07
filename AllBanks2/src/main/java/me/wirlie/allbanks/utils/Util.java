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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.AllBanks;

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


	
	/**
	 * Compara dos versiones del tipo {@code 1.0.0}.
	 * @param version1 Version
	 * @param version2 Version que se desea comparar con {@code arg0}
	 * @return 1 si {@code version1} > {@code version2} <br> 0 si {@code arg0} = {@code arg1} <br> -1 si {@code version1} < {@code version2} <br> -2 si la operación falla
	 */
	
	public static int compareVersionsString(String version1, String version2) {
		if(version1.equalsIgnoreCase(version2)) return 0;
		
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
    	
    	String debug = "";
		
		for(String s : version2Split){
			debug += s + ".";
		}
		
		String debug2 = "";
		
		for(String s : version1Split){
			debug2 += s + ".";
		}

		System.out.println("DEBUG version1" + debug);
		System.out.println("DEBUG version2" + debug2);
    	
    	for(int i = 0; i < maxParameters; i++) {
    		//Comprobar si es posible comparar ambos parámetros en ambos splits
			try {
				int version1ValueParameter = Integer.parseInt(version2Split.get(i));
				int version2ValueParameter = Integer.parseInt(version1Split.get(i));
				
				if(version1ValueParameter > version2ValueParameter) {
					return 1;
				}else {
					continue;
				}
			}catch (NumberFormatException e) {
				return -2;
			}
    	}

        return -1;
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
	
	//Reflection Util, Internal method, used as shorthand to grab our method in a nice friendly manner

	

	

	
}
