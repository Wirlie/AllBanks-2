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

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Utilidad para convertir un String en una localización y vice versa
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class StringLocationUtil{
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
}
