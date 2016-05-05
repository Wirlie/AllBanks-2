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
import me.wirlie.allbanks.utils.DataBaseUtil;

/**
 * @author josue
 *
 */
public class AllBanksPlayer {
	
	private static HashMap<String, AllBanksPlayer> playerCache = new HashMap<String, AllBanksPlayer>();
	
	String playerName;
	
	public AllBanksPlayer(String playerName){
		
		this.playerName = playerName = playerName.toLowerCase();
		
		if(!playerCache.containsKey(playerName)){
			playerCache.put(playerName, this);
		}else{
			//Leer desde el caché
			AllBanksPlayer cached = playerCache.get(playerName);
		}
	}
	
	public int currentPlots(String worldID){
		Statement stm = null;
		ResultSet res = null;
		
		try{
			stm = AllBanks.getSQLConnection("AllBanksLand").createStatement();
			res = stm.executeQuery("SELECT * FROM world_plots WHERE plot_owner = '" + playerName + "' AND world_id = '" + worldID + "'");
		
			int total = 0;
			
			while(res.next()){
				total ++;
			}
			
			return total;
		}catch(SQLException e){
			DataBaseUtil.checkDatabaseIsLocked(e);
		}finally{
			try{
				if(stm != null) stm.close();
				if(res != null) res.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		return 10000;
	}
	
}
