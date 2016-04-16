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

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

/**
 * @author Wirlie
 *
 */
public class TownyFunctions {
	protected static Towny pluginInstance = null;
	
	//Privatizar constructor
	private TownyFunctions() {}

	public static Towny getTownyPlugin(){
		return pluginInstance;
	}
	
	public static boolean canBreakAllBanksShop(Player player, Location blockLocation){

		if(TownyUniverse.isWilderness(blockLocation.getBlock())){
			//Los bloques fuera de una ciudad se consideran como "tierras de nadie"
			return false;
		}
		
		TownBlock tbl = TownyUniverse.getTownBlock(blockLocation);
		
		if(tbl != null){
			try {
				Town town = tbl.getTown();
				Resident res = null;
				
				if(tbl.hasResident()){
					res = tbl.getResident();
				}
				
				Resident resTryBreak = new Resident(player.getName());
				
				if(res != null && res.getName().equalsIgnoreCase(player.getName()) || town != null && town.hasMayor() && town.getMayor().getName().equalsIgnoreCase(player.getName())){
					//El jugador es dueño de este plot (o alcalde de la ciudad), por lo que puede construir.
					return true;
				}else{
					//Buscar amigos
					if(res != null && res.hasFriend(resTryBreak)){
						//El jugador no es dueño del plot, pero es amigo del dueño por lo que comparten permisos.
						return true;
					}
					
					//Buscar asistentes
					if(town != null && town.hasAssistant(resTryBreak)){
						//El jugador es un asistente de la ciudad
						return true;
					}
				}
			} catch (NotRegisteredException e) {
				return false;
			}
		}
		
		return false;
	}
	
}
