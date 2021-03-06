package me.wirlie.allbanks.allbanksland;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.PermissionsConstants;
import me.wirlie.allbanks.allbanksland.generator.WorldGenerationCfg;
import me.wirlie.allbanks.allbanksland.generator.WorldGenerator;
import me.wirlie.allbanks.utils.AllBanksLogger;
import me.wirlie.allbanks.utils.AssertUtil;
import me.wirlie.allbanks.utils.DBUtil;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.FileDirectory;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.WorldLoadAsync_1_10_R1;
import me.wirlie.allbanks.utils.WorldLoadAsync_1_9_R1;
import me.wirlie.allbanks.utils.WorldLoadAsync_1_9_R2;

/**
 * Interpretación de un mundo de AllBankdLand
 * @author Wirlie
 */
public class AllBanksWorld {
	
	private static final Connection DBC = AllBanks.getSQLConnection(DBUtil.ALLBANKSLAND_DATABASE_CONNECTION_NAME);
	
	/**
	 * Resultado de la generación.
	 * @author Wirlie
	 */
	public enum WorldGenerationResult{
		/**
		 * Exitoso
		 */
		SUCCESS,
		/**
		 * El mundo ya existe
		 */
		ERROR_WORLD_ID_ALREADY_EXISTS,
		/**
		 * IO Exception
		 */
		ERROR_IO_EXCEPTION,
		/**
		 * Error en la base de datos
		 */
		ERROR_DATABASE_EXCEPTION,
	}
	
	/**
	 * Caché que conserva todos los mundos ya instanciados.
	 */
	public static HashMap<String, AllBanksWorld> registeredMaps = new HashMap<String, AllBanksWorld>();
	
	/**
	 * Descargar un mundo de AllBanksLand.
	 * @param worldID ID del mundo.
	 * @param saveWorld Guardar mundo o no guardar mundo.
	 * @return {@code true} si el mundo fue descargado.
	 */
	public static boolean unloadPlotWorld(String worldID, boolean saveWorld){
		World w = (World) AssertUtil.assertNotNull(Bukkit.getWorld(worldID));
		
		//Expulsar jugadores
		for(Entity e : w.getEntities()){
			if(e instanceof Player){
				Player p = (Player) e;
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}
		}
		
		if(Bukkit.unloadWorld(worldID.toLowerCase(), saveWorld)){
			registeredMaps.remove(worldID.toLowerCase());
			return true;
		}
		
		return false;
	}
	
	/**
	 * Remover un mundo en su totalidad.
	 * @param worldID ID del mundo.
	 * @return resultado
	 */
	public static int removePlotWorldFolderAndDataBase(String worldID){
		return removePlotWorldFolderAndDataBase(worldID, false);
	}
	
	/**
	 * Remover un mundo en su totalidad.
	 * @param worldID ID del mundo.
	 * @param forceDropTable ¿Eliminar table asociada en la base de datos?
	 * @return resultado
	 */
	public static int removePlotWorldFolderAndDataBase(String worldID, boolean forceDropTable){
		
		worldID = worldID.toLowerCase();
		
		//Remover carpeta
		final File worldFolder = new File(new File("").getAbsolutePath() + File.separator + worldID);
		
		if(!worldFolder.exists() && !forceDropTable) return -2;
		
		//Eliminar tabla para este mundo
				Statement stm = null;
				try {
					stm = DBC.createStatement();
					stm.executeUpdate("DELETE FROM world_plots WHERE world_id = '" + worldID + "'");
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
	
	/**
	 * Comprobar si un mundo pertenece a AllBanks.
	 * @param worldID
	 * @return {@code true} si el mundo es de AllBanks.
	 */
	public static boolean worldIsAllBanksWorld(String worldID){
		return registeredMaps.containsKey(worldID.toLowerCase());
	}
	
	/**
	 * Generar un nuevo mundo de AllBanks-
	 * @param worldID ID del mundo.
	 * @return {@link WorldGenerationResult} resultado de la generación.
	 */
	public static WorldGenerationResult generatePlotWorld(String worldID){
		return generatePlotWorld(worldID.toLowerCase(), null);
	}
	
	/**
	 * @param worldIDP worldID ID del mundo.
	 * @param sender Ejecutor
	 * @return {@link WorldGenerationResult} resultado de la generación.
	 */
	public static WorldGenerationResult generatePlotWorld(String worldIDP, final CommandSender sender){
		
		final String worldID = worldIDP.toLowerCase();
		
		if(worldIsAllBanksWorld(worldID)){
			return WorldGenerationResult.ERROR_WORLD_ID_ALREADY_EXISTS;
		}
		
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();

		//Bien, comenzar a generar mundo
		final WorldGenerationCfg worldCfg = new WorldGenerationCfg(worldID);
		final WorldCreator wc = new WorldCreator(worldID)
				.generateStructures(false)
				.generator(WorldGenerator.getDefaultWorldGenerator(worldCfg, "AllBanksPlotGenerator"));
		
		new BukkitRunnable(){

			public void run() {
				try {
					//R1 Support
		    		Class.forName("org.bukkit.craftbukkit.v1_9_R1.CraftServer");
		    		WorldLoadAsync_1_9_R1.createAsyncWorld(wc, sender, worldCfg);
		    	}catch (ClassNotFoundException e) {
		            //R2 Support
		    		try {
						//R1 Support
			    		Class.forName("org.bukkit.craftbukkit.v1_9_R2.CraftServer");
			    		WorldLoadAsync_1_9_R2.createAsyncWorld(wc, sender, worldCfg);
			    	}catch (ClassNotFoundException e2) {
			    		try {
							//R1 Support
				    		Class.forName("org.bukkit.craftbukkit.v1_10_R1.CraftServer");
				    		WorldLoadAsync_1_10_R1.createAsyncWorld(wc, sender, worldCfg);
				    	}catch (ClassNotFoundException e3) {
				    		e3.printStackTrace();
				            return;
				        }
			        }
		        }
				
				//Tabla en la base de datos
				Statement stm = null;
				try {
					stm = DBC.createStatement();
					stm.executeUpdate("INSERT INTO worlds_cfg (world_id, plot_size, road_size, world_base_height) VALUES ('" + worldID + "', " + worldCfg.plot_size + ", " + worldCfg.road_size + ", " + worldCfg.world_height + ")");
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
				
				new BukkitRunnable(){
					public void run(){
						registeredMaps.put(worldID.toLowerCase(), new AllBanksWorld(worldID.toLowerCase()));
						WorldGenerationCfg.removeTemporalConfiguration(worldID);
					}
				}.runTask(AllBanks.getInstance());
				
			}
			
		}.runTaskAsynchronously(AllBanks.getInstance());
		
		return WorldGenerationResult.SUCCESS;
		
	}
	
	/**
	 * Cargar mundos al iniciar AllBanks.
	 */
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
						AllBanksLogger.info("&7[&fAllBanksLand&7] &bLoading world " + worldID + "...", true);
						WorldGenerationCfg worldCfg = new WorldGenerationCfg(worldID);
						
						worldCfg.plot_size = plotSize;
						worldCfg.road_size = roadSize;
						
						Bukkit.createWorld(new WorldCreator(worldID).generateStructures(false).generator(WorldGenerator.getDefaultWorldGenerator(worldCfg, "AllBanksPlotGenerator")));
					
						if(!registeredMaps.containsKey(worldID.toLowerCase())) registeredMaps.put(worldID.toLowerCase(), new AllBanksWorld(worldID.toLowerCase()));
					}else{
						if(!registeredMaps.containsKey(worldID.toLowerCase())) registeredMaps.put(worldID.toLowerCase(), new AllBanksWorld(worldID.toLowerCase()));
					}
				}else{
					AllBanksLogger.warning("&7[&fAllBanksLand&7] &eInvalid world entry for " + worldID + ", invalid file path. (Removed)", true);
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
	
	/**
	 * Obtener la instancia de un mundo desde el caché.
	 * @param worldID
	 * @return Instancia del mundo.
	 */
	public static AllBanksWorld getInstance(String worldID){
		return registeredMaps.get(worldID.toLowerCase());
	}
	
	//Funciones no estáticas usadas para obtener información del mundo
	
	private String world_id = null;
	
	int plotSize = 0;
	int roadSize = 0;

	/**
	 * Constructor.
	 * @param worldID ID del mundo.
	 */
	public AllBanksWorld(String worldID) {
		if(worldIsAllBanksWorld(worldID)) throw new IllegalArgumentException(worldID + " already initialized, use getInstance(ID) instead of a new instance.");
		
		this.world_id = worldID;
		loadWorldConfiguration();
	}
	
	/**
	 * Obtener el ID del mundo.
	 * @return ID del mundo.
	 */
	public String getID(){
		return world_id;
	}
	
	private void loadWorldConfiguration(){
		Statement stm = null;
		ResultSet res = null;
		
		try{
			stm = DBC.createStatement();
			res = stm.executeQuery("SELECT * FROM worlds_cfg WHERE world_id = '" + world_id + "'");
			
			if(res.next()){
				plotSize = res.getInt("plot_size");
				roadSize = res.getInt("road_size");
			}else{
				throw new UnsupportedOperationException(world_id + " not exists on AllBanks! (Table: worlds_cfg)");
			}
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
	}
	
	/**
	 * Comprobar si una localización pertenece a una parcela.
	 * @param loc Localización a comprobar.
	 * @return {@code true} si pertenece a una parcela.
	 */
	public boolean locationIsPlot(Location loc){
		return locationIsPlot(loc.getBlockX(), loc.getBlockZ());
	}
	
	/**
	 * Comprobar si una localización pertenece a una parcela.
	 * @param worldX coordenada X en el mundo
	 * @param worldZ coordenada Z en el mundo
	 * @return {@code true} si pertenece a una parcela.
	 */
	public boolean locationIsPlot(int worldX, int worldZ){
		
		int totalSize = 1 + plotSize + 1 + roadSize;
		boolean isPlot = false;
		boolean stopZCursor = false;
		
		if(worldX >= 0){
			//Positivo
			int cursorX = (worldX / totalSize) * totalSize;
			int relativeCursorX = worldX - cursorX;
			
			if(relativeCursorX == 0){
				isPlot = false; //plot limit
				stopZCursor = true;
			}else if(relativeCursorX < (plotSize + 1)){
				isPlot = true; //plot
			}else if(relativeCursorX == (plotSize + 1)){
				isPlot = false; //plot limit
				stopZCursor = true;
			}else{
				isPlot = false; //road
				stopZCursor = true;
			}
		}else{
			//Negativo
			int cursorX = (worldX / totalSize) * totalSize;
			int relativeCursorX = ((worldX - cursorX) * -1);
			
			if(relativeCursorX == 0){
				isPlot = false; //plot limit
				stopZCursor = true;
			}else if(relativeCursorX <= roadSize){
				isPlot = false;
				stopZCursor = true;
			}else if(relativeCursorX == (roadSize + 1)){
				isPlot = false;
				stopZCursor = true;
			}else{
				isPlot = true;
			}
		}
		
		if(!stopZCursor)
			if(worldZ >= 0){
				//Positivo
				int cursorZ = (worldZ / totalSize) * totalSize;
				int relativeCursorZ = worldZ - cursorZ;
				
				if(relativeCursorZ == 0){
					isPlot = false;
				}else if(relativeCursorZ < (plotSize + 1)){
					isPlot = true;
				}else if(relativeCursorZ == (plotSize + 1)){
					isPlot = false;
				}else{
					isPlot = false;
				}
			}else{
				//Negativo
				int cursorZ = (worldZ / totalSize) * totalSize;
				int relativeCursorZ = ((worldZ - cursorZ) * -1);
				
				if(relativeCursorZ == 0){
					isPlot = false;
				}else if(relativeCursorZ <= roadSize){
					isPlot = false;
				}else if(relativeCursorZ == (roadSize + 1)){
					isPlot = false;
				}else{
					isPlot = true;
				}
			}
		
		return isPlot;
	}
	
	/**
	 * Obtener la parcela de la localización especificada.
	 * @param loc Localización.
	 * @return {@link AllBanksPlot} parcela.
	 */
	public AllBanksPlot getPlot(Location loc) {
		return getPlot(loc.getBlockX(), loc.getBlockZ());
	}
	
	/**
	 * Obtener la parcela de la localización especificada
	 * @param worldX coordenada X en el mundo
	 * @param worldZ coordenada Z en el mundo
	 * @return {@link AllBanksPlot} parcela.
	 */
	public AllBanksPlot getPlot(int worldX, int worldZ) {
		
		int totalSize = 1 + plotSize + 1 + roadSize;
		
		int startX = 0;
		
		if(worldX >= 0){
			startX = ((worldX / totalSize) * totalSize) + 1;
		}else{
			startX = ((worldX / totalSize) * totalSize) - roadSize - 1;
		}
		
		int startZ = 0;
		
		if(worldZ >= 0){
			startZ = ((worldZ / totalSize) * totalSize) + 1;
		}else{
			startZ = ((worldZ / totalSize) * totalSize) - roadSize - 1;
		}
		
		return new AllBanksPlot(PlotID.getPlotIDByRealXZ(this, startX, startZ));
	}
	
	/**
	 * Obtener el mundo de Bukkit
	 * @return {@link World} mundo de Bukkit.
	 */
	public World getBukkitWorld(){
		return Bukkit.getWorld(world_id);
	}
	
	/**
	 * Obtener la configuración del mundo.
	 * @return {@link WorldConfiguration} configuración del mundo.
	 */
	public WorldConfiguration getWorldConfiguration(){
		return new WorldConfiguration(getID());
	}
	
	/**
	 * Comprobar si un jugador tiene permisos administrativos.
	 * @param p Jugador.
	 * @return {@code true} si el jugador tiene permisos administrativos en este mundo.
	 */
	public boolean hasAdminPermissions(Player p){
		if(Util.hasPermission(p, PermissionsConstants.LAND_ADMIN_PERMISSION) || p.isOp()){
			return true;
		}
		
		return false;
	}
	
}
