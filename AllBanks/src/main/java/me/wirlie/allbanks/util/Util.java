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
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.wirlie.allbanks.AllBanks;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Util {
	
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
