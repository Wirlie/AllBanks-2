/**
 * 
 */
package me.wirlie.allbanks.main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.wirlie.allbanks.listeners.SignChangeListener;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class AllBanks extends JavaPlugin {
	
	private static AllBanks AllBanksInstance;
	private static DataBase db = new DataBase();
	private static Connection dbc;
	
	@Override
	public void onEnable(){
		AllBanksInstance = this;
		dbc = db.setConnection(getDataFolder() + File.separator + "LocalDataBase.db", "local");
		
		//Ejecutar lo demás normalmente.
		ensureConfigIsUpToDate();
		Console.sendMessage(StringsID.ENABLING);
		
		//Registrar listener
		Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
	}
	
	@Override
	public void onDisable(){
		Console.sendMessage(StringsID.DISABLING);
		
		for(Connection c : db.multipleConnections.values()){
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		db.multipleConnections.clear();
	}
	
	public static void ensureConfigExists(){
		ensureConfigExists(false);
	}
	
	public static void ensureConfigExists(boolean force){
		File cfgFile = new File(getInstance().getDataFolder() + File.separator + "Config.yml");
		if(!cfgFile.exists() || force){
			getInstance().saveResource("Config.yml", true);
		}
	}
	
	public static void ensureConfigIsUpToDate(){
		File cfgFile = new File(getInstance().getDataFolder() + File.separator + "Config.yml");
		if(!cfgFile.exists()) ensureConfigExists();
		
		YamlConfiguration loadCfg = YamlConfiguration.loadConfiguration(cfgFile);
		String version = loadCfg.getString("cfg-version", "-1");
		
		if(version.equals("-1")){
			//No se encontró la versión, forzaremos una actualización
			ensureConfigExists(true);
			//Cargar de nuevo la versión
			version = loadCfg.getString("cfg-version", "-1");
			//Comprobar de nuevo
			if(version.equals("-1")){
				//Error
				try {
					throw new Exception("Can't get 'cfg-version' from Config.yml.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			//Bien, procesar
			if(!version.equalsIgnoreCase(getInstance().getDescription().getVersion())){
				//Distintas versiones, intentar actualizar nativamente.
				UpdateConfigWithNativeFile();
			}
		}
	}
	
	public static void UpdateConfigWithNativeFile(){
		//Copiar la anterior Configuración
		File Config = new File(getInstance().getDataFolder() + File.separator + "Config.yml");
		File tempConfig = new File(getInstance().getDataFolder() + File.separator + "TMP-Config.yml");
	
		Config.renameTo(tempConfig);
		
		//guardar nueva configuración
		getInstance().saveResource("Config.yml", true);
		
		//Comenzar a comparar
		YamlConfiguration nativeCfg = YamlConfiguration.loadConfiguration(Config);
		YamlConfiguration userCfg = YamlConfiguration.loadConfiguration(tempConfig);
		
		for(String key : nativeCfg.getKeys(true)){
			Object obj = userCfg.get(key, null);
			
			if(obj == null){
				userCfg.set(key, nativeCfg.get(key));
			}
		}
		
		//Actualizar la versión
		userCfg.set("cfg-version", getInstance().getDescription().getVersion());
		
		//guardar
		try {
			userCfg.save(tempConfig);
		} catch (IOException e) {
			getInstance().getLogger().severe("An error has ocurred while trying update Config.yml to the latest version. (IOException)");
			e.printStackTrace();
		}
		
		//eliminar configuración nativa
		Config.delete();
		//Cambiar configuración temporal a su estado normal
		tempConfig.renameTo(Config);
	}
	
	public static AllBanks getInstance(){
		return AllBanksInstance;
	}
	
	public static Connection getDBC(){
		return dbc;
	}
}
