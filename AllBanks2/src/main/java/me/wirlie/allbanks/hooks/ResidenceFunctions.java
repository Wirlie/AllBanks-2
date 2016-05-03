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

import com.bekvon.bukkit.residence.ResidenceCommandListener;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

/**
 * @author Wirlie
 *
 */
public class ResidenceFunctions {
	protected static ResidenceCommandListener pluginInstance = null;
	
	//Privatizar constructor
	private ResidenceFunctions() {}

	public static ResidenceCommandListener getResidencePlugin(){
		return pluginInstance;
	}
	
	public static boolean canBreakAllBanksShop(Player player, Location blockLocation){
		ResidenceInterface residenceManager = ResidenceApi.getResidenceManager();
		ClaimedResidence claim = residenceManager.getByLoc(blockLocation);
		
		if(claim == null) return false;
		
		if(claim.getOwner().equalsIgnoreCase(player.getName())){
			//Es el dueño del claimeo
			return true;
		}else{
			
			ClaimedResidence tryParentClaim = claim.getParent();
			
			while(claim.getParent() != null){
				
				if(tryParentClaim.getOwner().equalsIgnoreCase(player.getName())){
					//Es el dueño del claimeo pariente
					return true;
				}
				
				tryParentClaim = tryParentClaim.getParent();
			}
		}
		
		return false;
	}
}
