package me.wirlie.allbanks;

import java.io.File;

import me.wirlie.allbanks.land.AllBanksLandWorld;

public class AllBanksLandManager {

	private static File LAND_MANAGER_DATA_FOLDER = new File(AllBanks.getInstance().getDataFolder() + File.separator + "AllBanksLand");
	private static File LAND_MANAGER_WORLD_DATA_FOLDER = new File(LAND_MANAGER_DATA_FOLDER + File.separator + "Worlds");
	
	public static void initializeAllBanksLandManager(){
		loadWorlds();
	}
	
	private static void ensureFolderExists(File folder){
		if(folder.isDirectory()){
			folder.mkdirs();
		}
	}
	
	public static void loadWorlds(){
		
	}

	public static AllBanksLandWorld getWorld(String worldName) {
		ensureFolderExists(LAND_MANAGER_WORLD_DATA_FOLDER);
		
		File worldFile = new File(LAND_MANAGER_WORLD_DATA_FOLDER + File.separator + worldName + ".yml");
		if(!worldFile.exists()) return null;
		
		return new AllBanksLandWorld(worldFile);
	}
	
}
