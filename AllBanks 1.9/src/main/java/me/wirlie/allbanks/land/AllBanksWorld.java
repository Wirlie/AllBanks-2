package me.wirlie.allbanks.land;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.FileDirectory;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.WorldLoadAsync;

public class AllBanksWorld {
	
	private static final Connection DBC = AllBanks.getSQLConnection("AllBanksLand");
	
	public enum WorldGenerationResult{
		SUCCESS,
		ERROR_WORLD_ID_ALREADY_EXISTS,
		ERROR_IO_EXCEPTION,
		ERROR_DATABASE_EXCEPTION,
	}
	
	public static HashMap<String, AllBanksWorld> registeredMaps = new HashMap<String, AllBanksWorld>();

	public static AllBanksWorld getPlotWorld(String worldID){
		return registeredMaps.get(worldID.toLowerCase());
	}
	
	public static boolean unloadPlotWorld(String worldID, boolean saveWorld){
		if(Bukkit.unloadWorld(worldID.toLowerCase(), saveWorld)){
			registeredMaps.remove(worldID.toLowerCase());
			return true;
		}
		
		return false;
	}
	
	public static int removePlotWorldFolderAndDataBase(String worldID){
		return removePlotWorldFolderAndDataBase(worldID, false);
	}
	
	public static int removePlotWorldFolderAndDataBase(String worldID, boolean forceDropTable){
		
		worldID = worldID.toLowerCase();
		
		//Remover carpeta
		final File worldFolder = new File(new File("").getAbsolutePath() + File.separator + worldID);
		
		if(!worldFolder.exists() && !forceDropTable) return -2;
		
		//Eliminar tabla para este mundo
				Statement stm = null;
				try {
					stm = DBC.createStatement();
					stm.executeUpdate("DROP TABLE IF EXISTS world_" + worldID + "_plots");
					stm.executeUpdate("DELETE FROM worlds_cfg WHERE world_id = '" + worldID + "'");
				} catch (SQLException e) {
					e.printStackTrace();
					return -1;
				} finally {
					if(stm != null)
						try {
							stm.close();
						} catch (SQLException e) {
							DataBaseUtil.checkDatabaseIsLocked(e);
						}
				}
		
		registeredMaps.remove(worldID.toLowerCase());
		
		if(forceDropTable) return 1;
		
		new BukkitRunnable(){
			public void run() {
				Util.deleteDirectory(worldFolder);
			}
		}.runTaskAsynchronously(AllBanks.getInstance());
		
		return 1;
	}
	
	public static boolean checkPlotWorld(String worldID){
		return registeredMaps.containsKey(worldID.toLowerCase());
	}
	
	public static WorldGenerationResult generatePlotWorld(String worldID){
		return generatePlotWorld(worldID.toLowerCase(), null);
	}
	
	public static WorldGenerationResult generatePlotWorld(String worldIDP, final CommandSender sender){
		
		final String worldID = worldIDP.toLowerCase();
		
		if(checkPlotWorld(worldID)){
			return WorldGenerationResult.ERROR_WORLD_ID_ALREADY_EXISTS;
		}
		
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		//Crear tabla para este mundo
		Statement stm = null;
		try {
			stm = DBC.createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS world_" + worldID + "_plots (id INTEGER PRIMARY KEY AUTOINCREMENT, plot_coord TEXT NOT NULL, first_bound_loc TEXT NOT NULL, second_bound_loc TEXT NOT NULL)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS worlds_cfg (id INTEGER PRIMARY KEY AUTOINCREMENT, world_id TEXT NOT NULL, plot_size NUMBER NOT NULL, road_size NUMBER NOT NULL, current_plot_cursor TEXT NULL)");
		} catch (SQLException e) {
			e.printStackTrace();
			return WorldGenerationResult.ERROR_DATABASE_EXCEPTION;
		} finally {
			if(stm != null)
				try {
					stm.close();
				} catch (SQLException e) {
					DataBaseUtil.checkDatabaseIsLocked(e);
				}
		}
		
		//Bien, comenzar a generar mundo
		final WorldGenerationCfg worldCfg = new WorldGenerationCfg(worldID);
		final WorldCreator wc = new WorldCreator(worldID)
				.generateStructures(false)
				.generator(WorldGenerator.getDefaultWorldGenerator(worldCfg, "AllBanksPlotGenerator"));
		
		new BukkitRunnable(){

			public void run() {
				WorldLoadAsync.createAsyncWorld(wc, sender);
				
				//Tabla en la base de datos
				Statement stm = null;
				try {
					stm = DBC.createStatement();
					stm.executeUpdate("INSERT INTO worlds_cfg (world_id, plot_size, road_size) VALUES ('" + worldID + "', " + worldCfg.plot_size + ", " + worldCfg.road_size + ")");
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if(stm != null)
						try {
							stm.close();
						} catch (SQLException e) {
							DataBaseUtil.checkDatabaseIsLocked(e);
						}
				}
			}
			
		}.runTaskAsynchronously(AllBanks.getInstance());
		
		registeredMaps.put(worldID.toLowerCase(), new AllBanksWorld(worldID.toLowerCase()));
		
		return WorldGenerationResult.SUCCESS;
		
	}
	
	public static void loadWorldsStartup(){
		
		Statement stm = null;
		ResultSet res = null;
		
		try{
			stm = DBC.createStatement();
			res = stm.executeQuery("SELECT * FROM worlds_cfg");
			
			HashSet<String> removeList = new HashSet<String>();
			
			while(res.next()){
				String worldID = res.getString("world_id").toLowerCase();
				int plotSize = res.getInt("plot_size");
				int roadSize = res.getInt("road_size");
				
				File worldFolder = new File(".", worldID);
				
				if(worldFolder.exists() && worldFolder.isDirectory()){
					if(Bukkit.getWorld(worldID) == null){
						AllBanks.getInstance().getLogger().info("Loading world " + worldID + "...");
						WorldGenerationCfg worldCfg = new WorldGenerationCfg(worldID);
						
						worldCfg.plot_size = plotSize;
						worldCfg.road_size = roadSize;
						
						Bukkit.createWorld(new WorldCreator(worldID).generateStructures(false).generator(WorldGenerator.getDefaultWorldGenerator(worldCfg, "AllBanksPlotGenerator")));
					
						registeredMaps.put(worldID.toLowerCase(), new AllBanksWorld(worldID.toLowerCase()));
					}
				}else{
					AllBanks.getInstance().getLogger().warning("Invalid world entry for " + worldID + ", invalid file path. (Removed)");
					removeList.add(worldID);
				}
			}
			
			res.close();
			stm.close();
			
			for(String wid : removeList){
				removePlotWorldFolderAndDataBase(wid, true);
			}
		}catch(SQLException e){
			DataBaseUtil.checkDatabaseIsLocked(e);
		}finally{
				try {
					if(res != null){
						res.close();
					}
					
					if(stm != null){
						stm.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static AllBanksWorld getInstance(String worldID){
		return registeredMaps.get(worldID);
	}
	
	//Funciones no estáticas usadas para obtener información del mundo
	
	private String world_id = null;

	public AllBanksWorld(String worldID) {
		if(checkPlotWorld(worldID)) throw new IllegalArgumentException(worldID + " already initialized, use getInstance(ID) instead of a new instance.");
		
		this.world_id = worldID;
	}
	
}
