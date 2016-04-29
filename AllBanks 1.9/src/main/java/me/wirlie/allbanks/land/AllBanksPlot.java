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

import org.bukkit.Location;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.utils.DataBaseUtil;

/**
 * @author josue
 *
 */
public class AllBanksPlot {
	
	AllBanksWorld abw;
	
	Location firstBound = null;
	Location secondBound = null;
	
	int plotX = 0;
	int plotZ = 0;
	
	String ownerName = null;
	String json_config = null;
	
	protected AllBanksPlot(AllBanksWorld abw, int startX, int startZ){
		this.abw = abw;
		
		firstBound = new Location(abw.getBukkitWorld(), (startX >= 0) ? startX : startX, 0, (startZ >= 0) ? startZ : startZ);
		secondBound = new Location(abw.getBukkitWorld(), (startX >= 0) ? startX + abw.plotSize - 1 : startX - abw.plotSize + 1, abw.getBukkitWorld().getMaxHeight(), (startZ >= 0) ? startZ + abw.plotSize - 1 : startZ - abw.plotSize + 1);
		
		//Id del plot
		int totalSize = abw.plotSize + abw.roadSize + 2;
		plotX = ((startX >= 0) ? (startX / totalSize) : (startX / totalSize) - 1);
		plotZ = ((startZ >= 0) ? (startZ / totalSize) : (startZ / totalSize) - 1);
		
		loadPlotData();
	}
	
	private void loadPlotData(){
		
		Statement stm = null;
		ResultSet res = null;
		
		try{
			stm = AllBanks.getSQLConnection("AllBanksLand").createStatement();
			res = stm.executeQuery("SELECT * FROM world_" + abw.getID() + "_plots WHERE plot_coord_X = " + plotX + " AND plot_coord_Z = " + plotZ);
			
			if(res.next()){
				ownerName = res.getString("plot_owner");
				json_config = res.getString("plot_config");
			}
		}catch(SQLException e){
			DataBaseUtil.checkDatabaseIsLocked(e);
		}
	}
	
	public int getPlotX(){
		return plotX;
	}
	
	public int getPlotZ(){
		return plotZ;
	}
	
	public Location getFirstBound(){
		return firstBound;
	}
	
	public Location getSecondBound(){
		return secondBound;
	}
	
	public boolean hasOwner(){
		if(ownerName == null) return false;
		
		return true;
	}
	
}
