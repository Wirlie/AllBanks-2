package me.wirlie.allbanks.land;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.WorldGenerator;

public class AllBanksWorld {
	
	private static final File WORLDS_DATA_FOLDER = new File(AllBanks.getInstance() + File.separator + "Worlds");
	private static final Connection DBC = AllBanks.getSQLConnection("AllBanksLand");
	
	public enum WorldGenerationResult{
		SUCCESS,
		ERROR_WORLD_ID_ALREADY_EXISTS,
		ERROR_IO_EXCEPTION,
		ERROR_DATABASE_EXCEPTION,
	}
	
	private static HashMap<String, AllBanksWorld> storedMaps = new HashMap<String, AllBanksWorld>();
	
	public static AllBanksWorld getPlotWorld(String worldID){
		return storedMaps.get(worldID);
	}
	
	public static boolean unloadPlotWorld(String worldID, boolean saveWorld){
		if(Bukkit.unloadWorld(worldID, saveWorld)){
			storedMaps.remove(worldID);
			return true;
		}
		
		return false;
	}
	
	public static int removePlotWorldFolder(String worldID){
		//Remover carpeta
		File worldFolder = new File(new File("").getAbsolutePath() + File.separator + worldID);
		
		if(!worldFolder.exists()) return -2;
		
		//Eliminar tabla para este mundo
				Statement stm = null;
				try {
					stm = DBC.createStatement();
					stm.executeUpdate("DROP TABLE IF EXISTS world_" + worldID + "_plots");
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
		
		if(Util.deleteDirectory(worldFolder)){
			storedMaps.remove(worldID);
			return 1;
		}else{
			return 0;
		}
	}
	
	public static boolean checkPlotWorld(String worldID){
		return storedMaps.containsKey(worldID);
	}
	
	public static WorldGenerationResult generatePlotWorld(String worldID){
		return generatePlotWorld(worldID, null);
	}
	
	public static WorldGenerationResult generatePlotWorld(final String worldID, final CommandSender sender){
		if(checkPlotWorld(worldID)){
			return WorldGenerationResult.ERROR_WORLD_ID_ALREADY_EXISTS;
		}
		
		if(!WORLDS_DATA_FOLDER.exists()) WORLDS_DATA_FOLDER.mkdirs();
		
		File worldConfigurationFile = new File(WORLDS_DATA_FOLDER + File.separator + worldID + ".yml" );
		
		try {
			worldConfigurationFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return WorldGenerationResult.ERROR_IO_EXCEPTION;
		}
		
		//Crear tabla para este mundo
		Statement stm = null;
		try {
			stm = DBC.createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS world_" + worldID + "_plots (id INTEGER PRIMARY KEY AUTOINCREMENT, allbanks_coord TEXT NOT NULL, first_bound_loc TEXT NOT NULL, second_bound_loc)");
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
		WorldCreator wc = new WorldCreator(worldID);
		wc.generateStructures(false);
		wc.generator(WorldGenerator.getDefaultWorldGenerator(worldID, "AllBanksPlotGenerator"));
		
		final WorldCreator wc_final = wc;
		
		new BukkitRunnable(){

			public void run() {
				Bukkit.getServer().createWorld(wc_final);	
				if(sender != null) Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_GENERATING_FINISH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldID), true);
			}
			
		}.runTaskAsynchronously(AllBanks.getInstance());
		
		storedMaps.put(worldID, new AllBanksWorld());
		
		return WorldGenerationResult.SUCCESS;
		
	}
	
}
