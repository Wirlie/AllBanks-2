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
package me.wirlie.allbanks.allbanksland;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.PermissionsConstants;
import me.wirlie.allbanks.utils.AllBanksLogger;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.Util.VersionPackage;
import me.wirlie.allbanks.utils.Util_R1;
import me.wirlie.allbanks.utils.Util_R2;
import net.minecraft.server.v1_9_R1.Chunk;
import net.minecraft.server.v1_9_R1.ChunkProviderServer;
import net.minecraft.server.v1_9_R1.WorldServer;

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
	
	private String plotStringID;
	
	private String ownerName = null;
	
	private boolean registeredDatabase = false;
	
	protected AllBanksPlot(AllBanksWorld abw, int startX, int startZ){
		this.abw = abw;
		
		firstBound = new Location(abw.getBukkitWorld(), (startX >= 0) ? startX : startX, abw.getBukkitWorld().getSpawnLocation().getY(), (startZ >= 0) ? startZ : startZ);
		secondBound = new Location(abw.getBukkitWorld(), (startX >= 0) ? startX + abw.plotSize - 1 : startX - abw.plotSize + 1, abw.getBukkitWorld().getMaxHeight(), (startZ >= 0) ? startZ + abw.plotSize - 1 : startZ - abw.plotSize + 1);
		//Id del plot
		int totalSize = abw.plotSize + abw.roadSize + 2;
		plotX = ((startX >= 0) ? (startX / totalSize) : (startX / totalSize) - 1);
		plotZ = ((startZ >= 0) ? (startZ / totalSize) : (startZ / totalSize) - 1);
		plotStringID = plotX + "," + plotZ;
		
		loadPlotData();
	}
	
	/**
	 * Constructor.
	 * @param plotX X id
	 * @param plotZ Z id
	 * @param abw Mundo de AllBanksLand
	 */
	public AllBanksPlot(int plotX, int plotZ, AllBanksWorld abw){
		this.abw = abw;
		this.plotX = plotX;
		this.plotZ = plotZ;
		
		plotStringID = plotX + "," + plotZ;
		
		int totalSize = abw.plotSize + abw.roadSize + 2;
		firstBound = new Location(abw.getBukkitWorld(), plotX * totalSize, abw.getBukkitWorld().getSpawnLocation().getY() + 1, plotZ * totalSize);
		secondBound = new Location(abw.getBukkitWorld(), (plotX * totalSize) + abw.plotSize, abw.getBukkitWorld().getSpawnLocation().getY() + 1, (plotZ * totalSize) + abw.plotSize);
		
		loadPlotData();
	}
	
	/**
	 * Constructor corto de {@link #AllBanksPlot(int, int, AllBanksWorld)}.
	 * @param pid Información de la parcela.
	 */
	public AllBanksPlot(PlotID pid) {
		this.abw = pid.getWorld();
		this.plotX = pid.getPlotX();
		this.plotZ = pid.getPlotZ();
		
		plotStringID = plotX + "," + plotZ;
		
		int totalSize = abw.plotSize + abw.roadSize + 2;
		firstBound = new Location(abw.getBukkitWorld(), plotX * totalSize, abw.getBukkitWorld().getSpawnLocation().getY() + 1, plotZ * totalSize);
		secondBound = new Location(abw.getBukkitWorld(), (plotX * totalSize) + abw.plotSize, abw.getBukkitWorld().getSpawnLocation().getY() + 1, (plotZ * totalSize) + abw.plotSize);
		
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
	
	/**
	 * Obtener la configuración de la parcela.
	 * @return {@link PlotConfiguration} Configuración de la parcela.
	 */
	public PlotConfiguration getPlotConfiguration(){
		return plot_cfg;
	}
	
	/**
	 * Establecer una configuración de esta parcela.
	 * @param key Configuración.
	 * @param value Nuevo valor.
	 */
	public void setPlotConfiguration(String key, String value){
		plot_cfg.setPlotConfiguration(key, value);
	}
	
	/**
	 * Obtener el ID en X
	 * @return X
	 */
	public int getPlotX(){
		return plotX;
	}
	
	/**
	 * Obtener el ID en Z
	 * @return Z
	 */
	public int getPlotZ(){
		return plotZ;
	}
	
	/**
	 * Obtener el ID de la parcela.
	 * @return ID de la parcela.
	 */
	public String getPlotID(){
		return plotStringID;
	}
	
	/**
	 * Obtener el primer límite.
	 * @return Localización de la primer esquina.
	 */
	public Location getFirstBound(){
		return firstBound;
	}
	
	/**
	 * Obtener el segundo límite.
	 * @return Localización de la segunda esquina.
	 */
	public Location getSecondBound(){
		return secondBound;
	}
	
	/**
	 * Si la parcela tiene dueño.
	 * @return {@code true} si la parcela tiene dueño.
	 */
	public boolean hasOwner(){
		if(ownerName == null) return false;
		
		return true;
	}
	
	/**
	 * Obtener el nombre del dueño.
	 * @return Nombre del dueño.
	 */
	public String getOwnerName(){
		return this.ownerName;
	}
	
	/**
	 * Obtener el mundo de AllBanksLand perteneciente a esta parcela.
	 * @return Mundo de AllBanksLand.
	 */
	public AllBanksWorld getAllBanksWorld(){
		return abw;
	}
	
	/**
	 * Desclaimear esta parcela.
	 */
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
	 * Claimear esta parcela
	 * @param newOwnerName Nuevo dueño.
	 */
	public void claim(String newOwnerName) {
		
		newOwnerName = newOwnerName.toLowerCase();
		
		Statement stm = null;
		
		try{
			stm = AllBanks.getSQLConnection("AllBanksLand").createStatement();
			
			if(!registeredDatabase){
				stm.executeUpdate("INSERT INTO world_plots (world_id, plot_coord_X, plot_coord_Z, plot_owner, plot_config) VALUES ('" + abw.getID() + "', '" + plotX + "', '" + plotZ + "', '" + newOwnerName + "', '" + PlotConfiguration.defaultConfiguration(abw.getID()) + "')");
			}else{
				stm.executeUpdate("UPDATE world_plots SET plot_owner = '" + newOwnerName + "' WHERE world_id = '" + abw.getID() + "' AND plot_coord_X = '" + plotX + "' AND plot_coord_Z = '" + plotZ + "'");
			}
			
			//Añadir dueño
			AllBanksPlot plot = plotCache.get(plotStringID);
			plot.ownerName = newOwnerName;
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
	 * Establecer un nuevo bioma.
	 * @param biome Bioma a establecer
	 */
	public void setBiome(Biome biome){
		int firstCursorX = 0;
		int firstCursorZ = 0;
		int secondCursorX = 0;
		int secondCursorZ = 0;
		
		if(firstBound.getBlockX() < secondBound.getBlockX()){
			firstCursorX = firstBound.getBlockX();
			secondCursorX = secondBound.getBlockX();
		}else{
			firstCursorX = secondBound.getBlockX();
			secondCursorX = firstBound.getBlockX();
		}
		
		if(firstBound.getBlockZ() < secondBound.getBlockZ()){
			firstCursorZ = firstBound.getBlockZ();
			secondCursorZ = secondBound.getBlockZ();
		}else{
			firstCursorZ = secondBound.getBlockZ();
			secondCursorZ = firstBound.getBlockZ();
		}
		
		World w = abw.getBukkitWorld();
		int debugIteration = 0;
		
		for(int cursorX = (firstCursorX - 1); cursorX <= (secondCursorX + 1); cursorX++){
			for(int cursorZ = (firstCursorZ - 1); cursorZ <= (secondCursorZ + 1); cursorZ++){
				w.getBlockAt(cursorX, 1, cursorZ).setBiome(biome);
				debugIteration++;
			}
		}
		
		AllBanksLogger.debug("SetBiome report, plot: " + plotStringID + ", firstBound: " + firstBound.toString() + ", secondBound: " + secondBound.toString() + ", totalIterations: " + debugIteration + ", firstCursorX: " + firstCursorX + ", secondCursorX: " + secondCursorX + ", firstCursorZ: " + firstCursorZ + ", secondCursorZ: " + secondCursorZ + ", end of report.");
	}
	
	/**
	 * Comprobar si un jugador tiene permisos.
	 * @param player Jugador a comprobar.
	 * @return {@code true} si tiene permiso.
	 */
	public boolean havePermissions(Player player){
		
		if(player.isOp()){
			return true;
		}
		
		if(Util.hasPermission(player, PermissionsConstants.LAND_ADMIN_PERMISSION)){
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
	
	/**
	 * Utilidad para ayudar con las funciones de las parcelas.
	 * @author Wirlie
	 */
	public static class PlotFinder{
		
		private static int lastCursorIterations = 0;
		
		/**
		 * Obtener la siguiente parcela disponible en el mundo.
		 * @param abw Mundo de AllBanksLand a comprobar.
		 * @return El ID de la parcela disponible.
		 */
		public static synchronized PlotID getNextAvailablePlot(AllBanksWorld abw){
			while(true){
				if(lastCursorIterations == 0){
					if(!new AllBanksPlot(0, 0, abw).hasOwner()){
						return new PlotID(abw, 0, 0);
					}
				}else{
					int startX = -lastCursorIterations;
					int startZ = lastCursorIterations;
					int bound1X = startX + (lastCursorIterations * 2);
					int bound2Z = startZ - (lastCursorIterations * 2);
					int bound3X = startX;
					int bound4Z = startZ - 1;
					
					//Start(X) -> Bound1(X)
					for(int cursorX = startX; cursorX <= bound1X; cursorX++){
						int cursorZ = startZ;
						
						if(!new AllBanksPlot(cursorX, cursorZ, abw).hasOwner()){
							return new PlotID(abw, cursorX, cursorZ);
						}
					}
					
					//Bound1(X) -> Bound2(Z)
					for(int cursorZ = startZ; cursorZ >= bound2Z; cursorZ--){
						int cursorX = bound1X;
						
						if(!new AllBanksPlot(cursorX, cursorZ, abw).hasOwner()){
							return new PlotID(abw, cursorX, cursorZ);
						}
					}
					
					//Bound2(Z) -> Bound3(X)
					for(int cursorX = bound1X; cursorX >= bound3X; cursorX--){
						int cursorZ = bound2Z;
						
						if(!new AllBanksPlot(cursorX, cursorZ, abw).hasOwner()){
							return new PlotID(abw, cursorX, cursorZ);
						}
					}
					
					//Bound3(X) -> Bound4(Z)
					for(int cursorZ = bound2Z; cursorZ <= bound4Z; cursorZ++){
						int cursorX = startX;
						
						if(!new AllBanksPlot(cursorX, cursorZ, abw).hasOwner()){
							return new PlotID(abw, cursorX, cursorZ);
						}
					}
				}

				lastCursorIterations++;
			}
		}
	}
	
	public void clearPlot(CommandSender sender) {
		if(Util.resolveNMSVersion().versionPackageEnum == VersionPackage.NMS_1_9_R1){
			Util_R1.clearPlot(sender, abw.getBukkitWorld(), firstBound, secondBound);
		}else if(Util.resolveNMSVersion().versionPackageEnum == VersionPackage.NMS_1_9_R2){
			Util_R2.clearPlot(sender, abw.getBukkitWorld(), firstBound, secondBound);
		}else{
			throw new IllegalStateException(Util.resolveNMSVersion().versionPackageRaw + " is not supported!");
		}
	}
	
}
