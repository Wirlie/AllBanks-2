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
package me.wirlie.allbanks.land;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import me.wirlie.allbanks.AllBanks;

/**
 * @author josue
 *
 */
public class AllBanksPlayer {
	
	private static HashMap<String, HashMap<String, AllBanksPlayer>> playerCache = new HashMap<String, HashMap<String, AllBanksPlayer>>();
	
	int currentPlots = 0;
	String playerName;
	String worldID;
	
	public AllBanksPlayer(String worldID, String playerName){
		
		this.playerName = playerName = playerName.toLowerCase();
		this.worldID = worldID = worldID.toLowerCase();
		
		if(!playerCache.containsKey(worldID) || !playerCache.get(worldID).containsKey(playerName)){
			Statement stm = null;
			ResultSet res = null;
			
			try{
				stm = AllBanks.getSQLConnection("AllBanksLand").createStatement();
				res = stm.executeQuery("SELECT * FROM world_" + worldID + "_plots WHERE plot_owner = '" + playerName + "'");
				while(res.next()){
					currentPlots++;
				}
				if(playerCache.containsKey(worldID)){
					HashMap<String, AllBanksPlayer> getMap = playerCache.get(worldID);
					getMap.put(playerName, this);
					playerCache.put(worldID, getMap);
					
				}else{
					HashMap<String, AllBanksPlayer> newMap = new HashMap<String, AllBanksPlayer>();
					newMap.put(playerName, this);
					playerCache.put(worldID, newMap);
				}
				
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(stm != null) stm.close();
					if(res != null) res.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}else{
			//Leer desde el caché
			HashMap<String, AllBanksPlayer> getMap = playerCache.get(worldID);
			
			AllBanksPlayer cached = getMap.get(playerName);
			
			currentPlots = cached.currentPlots;
		}
	}
	
	public int currentPlots(){
		return currentPlots;
	}
	
	public void currentPlots(int newVal){
		
		currentPlots = newVal;
		
		if(!playerCache.containsKey(worldID) || !playerCache.get(worldID).containsKey(playerName)){
			if(playerCache.containsKey(worldID)){
				HashMap<String, AllBanksPlayer> getMap = playerCache.get(worldID);
				getMap.put(playerName, this);
				playerCache.put(worldID, getMap);
			}else{
				HashMap<String, AllBanksPlayer> newMap = new HashMap<String, AllBanksPlayer>();
				newMap.put(playerName, this);
				playerCache.put(worldID, newMap);
			}
		}else{
			HashMap<String, AllBanksPlayer> getMap = playerCache.get(worldID);
			getMap.put(playerName, this);
			playerCache.put(worldID, getMap);
		}
	}
	
}
