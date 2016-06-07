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
package me.wirlie.allbanks.hooks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.yi.acru.bukkit.Lockette.Lockette;

import com.bekvon.bukkit.residence.ResidenceCommandListener;
import com.griefcraft.lwc.LWCPlugin;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.wirlie.allbanks.Console;

/**
 * Clase encargada de detectar si hay plugins compatibles con AllBanks, los cuales serán
 * enganchados y usados para comprobar ciertas funciones.
 * @author Wirlie
 *
 */
public class HookManager {
	private final static PluginManager PLUGIN_MANAGER = Bukkit.getServer().getPluginManager();
	
	private HookManager() {}
	
	/**
	 * Inicializar
	 */
	public static void initializeHookManager(){
		try{
			WorldGuardHook.tryHook();
			TownyHook.tryHook();
			ResidenceHook.tryHook();
			LWCHook.tryHook();
			LocketteHook.tryHook();
		}catch(Exception e){
			//skip
		}
	}
	
	/**
	 * Para Residence
	 * @author Wirlie
	 *
	 */
	public static class ResidenceHook{
		private static boolean hooked = false;
		
		private static void tryHook(){
			//Residence
			Plugin residencePlugin = PLUGIN_MANAGER.getPlugin("Residence");
		    if (residencePlugin != null && (residencePlugin instanceof ResidenceCommandListener)) {
		    	if(residencePlugin.getDescription().getVersion().equalsIgnoreCase("4.0.6.3")){
			    	//Mensaje
			    	Console.sendMessage(ChatColor.YELLOW + "[Residence] Residence 4.0.6.3 hooked!");
		    	}else{
		    		//Mensaje
			    	Console.sendMessage(ChatColor.YELLOW + "[Residence] Residence hooked! But, you are using an untested version of Residence, please proceed with precaution.");
		    	}
		    	
		    	ResidenceFunctions.pluginInstance = (ResidenceCommandListener) residencePlugin;
		    	
		    	hooked = true;
		    }
		}
		
		public static boolean isHooked(){
			return hooked;
		}
	}
	
	/**
	 * Para Towny
	 * @author Wirlie
	 *
	 */
	public static class TownyHook{
		private static boolean hooked = false;
		
		private static void tryHook(){
			//Towny
			Plugin townyPlugin = PLUGIN_MANAGER.getPlugin("Towny");
		    if (townyPlugin != null && (townyPlugin instanceof Towny)) {
		    	if(townyPlugin.getDescription().getVersion().equalsIgnoreCase("0.91.0.2")){
			    	//Mensaje
			    	Console.sendMessage(ChatColor.YELLOW + "[Towny] Towny 0.91.0.2 hooked!");
		    	}else{
		    		//Mensaje
			    	Console.sendMessage(ChatColor.YELLOW + "[Towny] Towny hooked! But, you are using an untested version of Towny, please proceed with precaution.");
		    	}
		    	
		    	TownyFunctions.pluginInstance = (Towny) townyPlugin;
		    	
		    	hooked = true;
		    }
		}
		
		public static boolean isHooked(){
			return hooked;
		}
	}
	
	/**
	 * Para WorldGuard
	 * @author Wirlie
	 *
	 */
	public static class WorldGuardHook{
		private static boolean hooked = false;
		
		private static void tryHook(){
			//WorldGuard
			Plugin worldGuardPlugin = PLUGIN_MANAGER.getPlugin("WorldGuard");
		    if (worldGuardPlugin != null && (worldGuardPlugin instanceof WorldGuardPlugin)) {
		    	if(worldGuardPlugin.getDescription().getVersion().equalsIgnoreCase("6.1")){
			    	//Mensaje
			    	Console.sendMessage(ChatColor.YELLOW + "[WorldGuard] WG 6.1 hooked!");
		    	}else{
		    		//Mensaje
			    	Console.sendMessage(ChatColor.YELLOW + "[WorldGuard] WG hooked! But, you are using an untested version of WorldGuard, please proceed with precaution.");
		    	}
		    	
		    	WorldGuardFunctions.pluginInstance = (WorldGuardPlugin) worldGuardPlugin;
		    	
		    	hooked = true;
		    }
		}
		
		public static boolean isHooked(){
			return hooked;
		}
	}
	
	/**
	 * Para LWC
	 * @author Wirlie
	 *
	 */
	public static class LWCHook{
		private static boolean hooked = false;
		
		private static void tryHook(){
			//LWCHook
			Plugin lwcPlugin = PLUGIN_MANAGER.getPlugin("LWC");
		    if (lwcPlugin != null && (lwcPlugin instanceof LWCPlugin)) {
		    	Console.sendMessage(ChatColor.YELLOW + "[LWC] LWC hooked!");
		    	LWCFunctions.pluginInstance = (LWCPlugin) lwcPlugin;
		    	
		    	hooked = true;
		    }
		}
		
		public static boolean isHooked(){
			return hooked;
		}
	}
	
	/**
	 * Para Lockette
	 * @author Wirlie
	 *
	 */
	public static class LocketteHook{
		private static boolean hooked = false;
		
		private static void tryHook(){
			//LocketteHook
			Plugin lockettePlugin = PLUGIN_MANAGER.getPlugin("Lockette");
		    if (lockettePlugin != null && (lockettePlugin instanceof Lockette)) {
		    	Console.sendMessage(ChatColor.YELLOW + "[Lockette] Lockette hooked!");
		    	LocketteFunctions.pluginInstance = (Lockette) lockettePlugin;
		    	
		    	hooked = true;
		    }
		}
		
		public static boolean isHooked(){
			return hooked;
		}
	}
	
	
}
