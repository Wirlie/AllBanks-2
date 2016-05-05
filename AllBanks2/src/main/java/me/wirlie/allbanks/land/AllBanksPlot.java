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

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.utils.Util;

/**
 * @author josue
 *
 */
public class AllBanksPlot {
	
	private HashMap<String, AllBanksPlot> plotCache = new HashMap<String, AllBanksPlot>();
	
	private AllBanksWorld abw;
	private PlotConfiguration plot_cfg;
	
	private Location firstBound = null;
	private Location secondBound = null;
	
	private int plotX = 0;
	private int plotZ = 0;
	
	String plotStringID;
	
	private String ownerName = null;
	
	private boolean registeredDatabase = false;
	
	protected AllBanksPlot(AllBanksWorld abw, int startX, int startZ){
		this.abw = abw;
		
		firstBound = new Location(abw.getBukkitWorld(), (startX >= 0) ? startX : startX, 0, (startZ >= 0) ? startZ : startZ);
		secondBound = new Location(abw.getBukkitWorld(), (startX >= 0) ? startX + abw.plotSize - 1 : startX - abw.plotSize + 1, abw.getBukkitWorld().getMaxHeight(), (startZ >= 0) ? startZ + abw.plotSize - 1 : startZ - abw.plotSize + 1);
		
		//Id del plot
		int totalSize = abw.plotSize + abw.roadSize + 2;
		plotX = ((startX >= 0) ? (startX / totalSize) : (startX / totalSize) - 1);
		plotZ = ((startZ >= 0) ? (startZ / totalSize) : (startZ / totalSize) - 1);
		plotStringID = plotX + "," + plotZ;
		
		loadPlotData();
	}
	
	private void loadPlotData(){
		if(plotCache.containsKey(plotStringID)){
			//No se necesita obtener información de la base de datos
			AllBanksPlot cached = plotCache.get(plotStringID);
			
			ownerName = cached.getOwnerName();
			plot_cfg = cached.plot_cfg;
			registeredDatabase = true;
			return;
		}
		
		Statement stm = null;
		ResultSet res = null;
		
		try{
			stm = AllBanks.getSQLConnection("AllBanksLand").createStatement();
			res = stm.executeQuery("SELECT * FROM world_plots WHERE plot_coord_X = " + plotX + " AND plot_coord_Z = " + plotZ + " AND world_id = '" + abw.getID() + "'");
			
			if(res.next()){
				ownerName = res.getString("plot_owner");
				plot_cfg = new PlotConfiguration(this, res.getString("plot_config"));
				registeredDatabase = true;
			}else{
				ownerName = null;
				plot_cfg = new PlotConfiguration(this, "");
				registeredDatabase = false;
			}
			
			plotCache.put(plotStringID, this);
			
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
	}
	
	public PlotConfiguration getPlotConfiguration(){
		return plot_cfg;
	}
	
	public void setPlotConfiguration(String key, String value){
		plot_cfg.setPlotConfiguration(key, value);
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
	
	public String getOwnerName(){
		return this.ownerName;
	}
	
	public AllBanksWorld getAllBanksWorld(){
		return abw;
	}
	
	public void unclaim(){
		Statement stm = null;
		
		try{
			stm = AllBanks.getSQLConnection("AllBanksLand").createStatement();
			stm.executeUpdate("DELETE FROM world_plots WHERE world_id = '" + abw.getID() + "' AND plot_coord_X = '" + plotX + "' AND plot_coord_Z = '" + plotZ + "'");
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(stm != null) stm.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param name
	 */
	public void claim(String name) {
		
		name = name.toLowerCase();
		
		Statement stm = null;
		
		try{
			stm = AllBanks.getSQLConnection("AllBanksLand").createStatement();
			
			if(!registeredDatabase){
				stm.executeUpdate("INSERT INTO world_plots (world_id, plot_coord_X, plot_coord_Z, plot_owner, plot_config) VALUES ('" + abw.getID() + "', '" + plotX + "', '" + plotZ + "', '" + name + "', '" + PlotConfiguration.defaultConfiguration(abw.getID()) + "')");
			}else{
				stm.executeUpdate("UPDATE world_plots SET plot_owner = '" + name + "' WHERE world_id = '" + abw.getID() + "' AND plot_coord_X = '" + plotX + "' AND plot_coord_Z = '" + plotZ + "'");
			}
			
			//Añadir dueño
			AllBanksPlot plot = plotCache.get(plotStringID);
			plot.ownerName = name;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(stm != null) stm.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	public boolean havePermissions(Player player){
		
		if(player.isOp()){
			return true;
		}
		
		if(Util.hasPermission(player, "allbanks.land.admin")){
			return true;
		}
		
		String playerName = player.getName().toLowerCase();
		
		if(hasOwner() && getOwnerName().equalsIgnoreCase(playerName)){
			return true;
		}
		
		PlotConfiguration cfg = getPlotConfiguration();
		if(cfg.getFriends().contains(playerName)){
			return true;
		}
		
		return false;
		
	}
	
}
