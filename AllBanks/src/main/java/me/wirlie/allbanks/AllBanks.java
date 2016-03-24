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
package me.wirlie.allbanks;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.wirlie.allbanks.data.BankSession;
import me.wirlie.allbanks.listeners.ChargeLoanOnPlayerJoin;
import me.wirlie.allbanks.listeners.PlayerChatBSListener;
import me.wirlie.allbanks.listeners.PlayerMoveListener;
import me.wirlie.allbanks.listeners.SignBreakListener;
import me.wirlie.allbanks.listeners.SignChangeListener;
import me.wirlie.allbanks.listeners.SignInteractListener;
import me.wirlie.allbanks.listeners.VirtualChestClose;
import me.wirlie.allbanks.runnable.BankLoanRunnable;
import me.wirlie.allbanks.runnable.BankTimerRunnable;
import net.milkbowl.vault.economy.Economy;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class AllBanks extends JavaPlugin {
	
	private static AllBanks AllBanksInstance;
	private static DataBase db = new DataBase();
	private static Connection dbc;
	
	private static Economy econ = null;

	public final static String[] COMPATIBLE_VERSIONS = {"1.9-R0.1-SNAPSHOT"};
	
	@Override
	public void onEnable(){

		AllBanksInstance = this;
		
		//Version del servidor
		verifyServerVersion();
		
		dbc = db.setConnection(getDataFolder() + File.separator + "LocalDataBase.db", "local");
		
		//Instalar DB
		installDatabase();
		
		//Economy
		if(!setupEconomy()){
		    Bukkit.getPluginManager().disablePlugin(this);
		    return;
		}

		//Ejecutar lo demás normalmente.
		ensureConfigIsUpToDate();
		Console.sendMessage(StringsID.ENABLING);
		
		//comando
		Bukkit.getPluginCommand("allbanks").setExecutor(new Commands());
		Bukkit.getPluginCommand("allbanks").setTabCompleter(new CommandsTabCompleter());
		
		//Registrar listener
		Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignBreakListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerChatBSListener(), this);
		Bukkit.getPluginManager().registerEvents(new VirtualChestClose(), this);
		Bukkit.getPluginManager().registerEvents(new ChargeLoanOnPlayerJoin(), this);
		
		//Runnables
		
		//Para BankTime
		int runSeconds = getConfig().getInt("banks.bank-time.add-minute-every", 60);
		if(runSeconds <= 0){ runSeconds = 60; }
		new BankTimerRunnable().runTaskTimer(this, 20 * runSeconds, 20 * runSeconds);
		
		//Para BankLoan
		int collectLoanEvery = Util.ConfigUtil.convertTimeValueToSeconds(getConfig().getString("banks.bank-loan.collect-interest-every"));
		if(collectLoanEvery == -1 || collectLoanEvery == 0){ 
			//No se puede usar el sistema cuando el tiempo es inválido.
			getLogger().severe("Invalid configuration in Config.yml.");
			getLogger().severe("banks.bank-loan.collect-interest-every is not valid.");
			getLogger().severe("Please set a numeric value more than 0.");
			getLogger().severe("BankLoan: Collect Loan System disabled...");
		}else{
			
			File bankLoanData = new File(getDataFolder() + File.separator + "BankLoanData.yml");
			
			if(!bankLoanData.exists())
				try {
					bankLoanData.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bankLoanData);
			
			long lastExec = yaml.getLong("last-system-execution", 0);
			long currentTime = new Date().getTime();
			long nextCollection = collectLoanEvery - ((currentTime - lastExec) / 1000);
			
			if(lastExec == 0){
				getLogger().info("[CollectLoanSystem] Initializing system...");
				getLogger().info("[CollectLoanSystem] Next execution: " + collectLoanEvery + " seconds.");
				new BankLoanRunnable().runTaskTimer(this, collectLoanEvery * 20, collectLoanEvery * 20);
				
				//Si no hay un tiempo establecido de ultima ejecución hay que establecerlo.
				yaml.set("last-system-execution", currentTime);
				try {
					yaml.save(bankLoanData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				if(nextCollection < 10) nextCollection = 10;
				getLogger().info("[CollectLoanSystem] Initializing system...");
				getLogger().info("[CollectLoanSystem] Next execution: " + nextCollection + " seconds.");
				new BankLoanRunnable().runTaskTimer(this, nextCollection * 20, collectLoanEvery * 20);
			}
		}
	}
	
	@Override
	public void onDisable(){
		Console.sendMessage(StringsID.DISABLING);
		
		//Cerrar todas las sesiones.
		Collection<BankSession> sessions = BankSession.getAllActiveSessions();
		
		for(BankSession bs : sessions){
			bs.closeSession();
		}
		
		for(Connection c : db.multipleConnections.values()){
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		db.multipleConnections.clear();
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
        	Console.sendMessage(ChatColor.RED + "[Error] Ops! Vault plugin is required for AllBanks...");
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	Console.sendMessage(ChatColor.RED + "[Error] Ops! An economy plugin is required for AllBanks...");
        	return false;
        }
        
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public static Economy getEconomy(){
		return econ;
	}
	
	public static void installDatabase(){
		try{
			Statement stm = dbc.createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS signs (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, location TEXT NOT NULL)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS bankloan_accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, loan TEXT NOT NULL)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS bankmoney_accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, money TEXT NOT NULL)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS bankxp_accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, xp NUMBER)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS banktime_accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, time NUMBER)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS bankloan_pending_charges (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, amount TEXT NOT NULL)");
			stm.close();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void ensureConfigExists(){
		ensureConfigExists(false);
	}
	
	public static void ensureConfigExists(boolean force){
		File cfgFile = new File(getInstance().getDataFolder() + File.separator + "Config.yml");
		if(!cfgFile.exists() || force){
			getInstance().saveResource("Config.yml", true);
			getInstance().reloadConfig();
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
				//Actualizar
				getInstance().reloadConfig();
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
	
	private void verifyServerVersion() {
		
		String rawVersion = Bukkit.getServer().getBukkitVersion();
		//String[] version = Bukkit.getServer().getBukkitVersion().split("-");
		
		//String mcversion = version[0];
		//String bukkitversion = version[1];
		
		boolean compatible = false;
		
		for(String sv : COMPATIBLE_VERSIONS){
			if(rawVersion.equalsIgnoreCase(sv)){
				//La version es igual al minimo o maximo, no es necesario calcular nada
				compatible = true;
				break;
			}
		}
		
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		replaceMap.put("%1%", rawVersion);
		
		if(compatible){
			Console.sendMessage(StringsID.YOU_ARE_RUNNING_A_COMPATIBLE_VERSION_OF_CB, replaceMap);
		}else{
			Console.sendMessage(StringsID.YOU_ARENT_RUNNING_A_COMPATIBLE_VERSION_OF_CB, replaceMap);
		}
		
	}
	
	//Version para el paquete ej: net.minecraft.VERSION.clase
	/*
	 * String name = getServer().getClass().getPackage().getName();
	   String version = name.substring(name.lastIndexOf('.') + 1);
		
	   System.out.println(version);
	 */
}
