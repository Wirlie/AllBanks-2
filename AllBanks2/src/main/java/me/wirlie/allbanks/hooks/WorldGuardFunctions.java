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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

/**
 * @author Wirlie
 *
 */
public class WorldGuardFunctions {
	protected static WorldGuardPlugin pluginInstance = null;
	
	//Privatizar constructor
	private WorldGuardFunctions() {}

	/**
	 * @return Instancia del plugin
	 */
	public static WorldGuardPlugin getWorldGuardPlugin(){
		return pluginInstance;
	}
	
	/**
	 * Comprobar si puede remover un letrero de AllBanksShop
	 * @param player Jugador
	 * @param blockLocation Localización del letrero
	 * @return {@code true} si el jugador puede romper este letrero.
	 */
	public static boolean canBreakAllBanksShop(Player player, Location blockLocation){
		
		WorldGuardPlugin worldGuard = WorldGuardFunctions.getWorldGuardPlugin();
		Vector pt = new Vector().setX(blockLocation.getX()).setY(blockLocation.getY()).setZ(blockLocation.getZ());
		RegionManager regionManager = worldGuard.getRegionManager(blockLocation.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
		
		if(set != null && set.getRegions().size() > 0){
			LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
			if(set.testState(localPlayer, DefaultFlag.BUILD)){
				//Puede romper bloques en esta región, lo que hace posible que pueda romper letreros de AllBanks.
				return true;
			}
		}
		
		return false;
	}
	
}
