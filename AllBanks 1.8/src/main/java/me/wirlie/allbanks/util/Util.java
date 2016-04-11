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
import java.util.ArrayList;
import java.util.Arrays;
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
	
	/**
	 * Compara dos versiones del tipo {@code 1.0.0}.
	 * @param arg0 Version
	 * @param arg1 Version que se desea comparar con {@code arg0}
	 * @return 1 si {@code arg0} > {@code arg1} <br> 0 si {@code arg0} = {@code arg1} <br> -1 si {@code arg0} < {@code arg1} <br> -2 si la operación falla
	 */
	
	public static int compareVersionString(String arg0, String arg1) {
		if(arg0.equalsIgnoreCase(arg1)) return 0;
		
    	List<String> remoteVersionSplit = new ArrayList<String>(Arrays.asList(arg0.split("\\D")));
		List<String> localVersionSplit = new ArrayList<String>(Arrays.asList(arg1.split("\\D")));
    	
    	int maxParameters = (localVersionSplit.size() > remoteVersionSplit.size()) ? localVersionSplit.size() : remoteVersionSplit.size();
    	
    	if(localVersionSplit.size() != maxParameters) {
    		//Igualar local al remoto
    		int excedent = maxParameters - localVersionSplit.size();
    		for(int i = 0; i < excedent; i++) {
    			localVersionSplit.add("0");
    		}
    	}else {
    		//Igualare remoto al local
    		int excedent = maxParameters - remoteVersionSplit.size();
    		for(int i = 0; i < excedent; i++) {
    			remoteVersionSplit.add("0");
    		}
    	}
    	
    	for(int i = 0; i < maxParameters; i++) {
    		//Comprobar si es posible comparar ambos parámetros en ambos splits
			try {
				int localvp = Integer.parseInt(localVersionSplit.get(i));
				int remotevp = Integer.parseInt(remoteVersionSplit.get(i));

				if(localvp < remotevp) {
					//Este argumento es mayor. EJ: Local 1.0.0   Remote 2.0.0   if(1 < 2)
					return 1;
				}else {
					continue;
				}
			}catch (NumberFormatException e) {
				//Si falla, checamos de la manera nativa
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
