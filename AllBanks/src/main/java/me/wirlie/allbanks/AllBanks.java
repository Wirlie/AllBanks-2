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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.wirlie.allbanks.Updater.UpdateResult;
import me.wirlie.allbanks.Updater.UpdateType;
import me.wirlie.allbanks.data.BankSession;
import me.wirlie.allbanks.listeners.ChargeLoanOnPlayerJoin;
import me.wirlie.allbanks.listeners.PlayerChatBSListener;
import me.wirlie.allbanks.listeners.PlayerMoveListener;
import me.wirlie.allbanks.listeners.SignBreakListener;
import me.wirlie.allbanks.listeners.SignChangeListener;
import me.wirlie.allbanks.listeners.SignInteractListener;
import me.wirlie.allbanks.listeners.VirtualChestClose;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.runnable.BankLoanRunnable;
import me.wirlie.allbanks.runnable.BankTimerRunnable;
import me.wirlie.allbanks.runnable.LotteryRunnable;
import net.milkbowl.vault.economy.Economy;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class AllBanks extends JavaPlugin {
	
	private static AllBanks AllBanksInstance;
	private static DataBaseSQLite dbSQLite = new DataBaseSQLite();
	private static DataBaseMySQL dbMySQL = new DataBaseMySQL();
	private static Connection dbc;
	private static StorageType storageMethod = StorageType.SQLITE;
	
	private static Economy econ = null;

	public final static String[] COMPATIBLE_VERSIONS = {
			"1.8",
			"1.8.3",
			"1.8.4",
			"1.8.5",
			"1.8.6",
			"1.8.7",
			"1.8.8",
			"1.9",
			"1.9.2",
			};
	
	public enum StorageType{
		FLAT_FILE,
		SQLITE,
		MYSQL;
	}
	
	@Override
	public void onEnable(){

		AllBanksInstance = this;
		
		//Logger
		AllBanksLogger.initializeLogger();
		
		AllBanksLogger.info("Enabling AllBanks " + getDescription().getVersion());
		
		//Método de almacenamiento.
		String storageStr = getConfig().getString("pl.storage-system", "SQLite");
		
		if(storageStr.equalsIgnoreCase("sqlite")) {
			storageMethod = StorageType.SQLITE;
		}else if(storageStr.equalsIgnoreCase("mysql")) {
			storageMethod = StorageType.MYSQL;
		}else if(storageStr.equalsIgnoreCase("flatfile")){
			storageMethod = StorageType.FLAT_FILE;
		}
		
		//intentar
		switch(getStorageMethod()) {
		case FLAT_FILE:
			//Sin acciones
    		AllBanksLogger.info("Storage Method: FlatFile");
			break;
		case MYSQL:
    		AllBanksLogger.info("Storage Method: MySQL");
			if(!DataBaseMySQL.tryForClass()) {
	    		AllBanksLogger.warning("Ops! DriverManager not found, setting storage method: FlatFile");
	    		storageMethod = StorageType.FLAT_FILE;
			}
			break;
		case SQLITE:
    		AllBanksLogger.info("Storage Method: SQLite");
			if(!DataBaseSQLite.tryForClass()) {
	    		AllBanksLogger.warning("Ops! DriverManager not found, setting storage method: FlatFile");
	    		storageMethod = StorageType.FLAT_FILE;
			}
			break;
		
		}
		
		//Version del servidor
		verifyServerVersion();
		
		if(getStorageMethod().equals(StorageType.MYSQL)) {
			AllBanksLogger.info("Initializing MySQL database...");
			dbc = dbMySQL.setConnection("global");
		}else {
			AllBanksLogger.info("Initializing SQLite database...");
			dbc = dbSQLite.setConnection(getDataFolder() + File.separator + "LocalDataBase.db", "local");
		}
		
		//Instalar DB
		if(getStorageMethod().equals(StorageType.MYSQL) || getStorageMethod().equals(StorageType.SQLITE)) {
			installDatabase();
		}
		
		//Economy
		if(!setupEconomy()){
		    Bukkit.getPluginManager().disablePlugin(this);
		    return;
		}

		//Ejecutar lo demás normalmente.
		ensureConfigIsUpToDate();
		Console.sendMessage(StringsID.ENABLING);
		
		//comando
		AllBanksLogger.info("Commands: Set executor (/allbanks).");
		Bukkit.getPluginCommand("allbanks").setExecutor(new Commands());
		AllBanksLogger.info("Commands: Set tab completer (/allbanks).");
		Bukkit.getPluginCommand("allbanks").setTabCompleter(new CommandsTabCompleter());
		
		//Registrar listener
		AllBanksLogger.info("Registering events...");
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
		AllBanksLogger.info("Enabling BankLoanRunnable...");
		AllBanksLogger.info("Reading Config.yml -> banks.bank-loan.collect-interest-every");
		int collectLoanEvery = Util.ConfigUtil.convertTimeValueToSeconds(getConfig().getString("banks.bank-loan.collect-interest-every"));
		
		if(collectLoanEvery == -1 || collectLoanEvery == 0){ 

			AllBanksLogger.severe("Invalid configuration :");
			//No se puede usar el sistema cuando el tiempo es inválido.
			getLogger().severe("Invalid configuration in Config.yml.");
			getLogger().severe("banks.bank-loan.collect-interest-every is not valid.");
			getLogger().severe("Please set a numeric value more than 0.");
			getLogger().severe("BankLoan: Collect Loan System disabled...");
			
			AllBanksLogger.severe("Invalid configuration in Config.yml.");
			AllBanksLogger.severe("banks.bank-loan.collect-interest-every is not valid.");
			AllBanksLogger.severe("Please set a numeric value more than 0.");
			AllBanksLogger.severe("BankLoan: Collect Loan System disabled...");

			AllBanksLogger.info("Aborting enabling of BankLoanRunnable...");
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
				AllBanksLogger.info("BankLoanRunnable: Initializing system...", true);
				AllBanksLogger.info("BankLoanRunnable: Next execution: " + collectLoanEvery + " seconds.", true);
				
				AllBanksLogger.info("BankLoanRunnable: Starting runnable (TaskTimer)");
				
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
				AllBanksLogger.info("BankLoanRunnable: Initializing system...", true);
				AllBanksLogger.info("BankLoanRunnable: Next execution: " + nextCollection + " seconds.", true);
				
				AllBanksLogger.info("BankLoanRunnable: Starting runnable (TaskTimer)");
				
				new BankLoanRunnable().runTaskTimer(this, nextCollection * 20, collectLoanEvery * 20);
			}
		}
		
		//BankLottery
		LotteryRunnable.initializeLottery();
		try {
			LotteryRunnable.startRunnable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Updater
		boolean checkForUpdates = getConfig().getBoolean("pl.updater.check-for-updates", true);
		boolean forceUpdate = getConfig().getBoolean("pl.updater.auto-update", true);
		
		UpdateType uptype = UpdateType.DEFAULT;
		
		if(checkForUpdates && !forceUpdate) {
			uptype = UpdateType.NO_DOWNLOAD;
		}
		
		if(!checkForUpdates && forceUpdate) {
			uptype = UpdateType.NO_VERSION_CHECK;
		}
		
		if(checkForUpdates || forceUpdate) {
			Updater updater = new Updater(this, 98949, this.getFile(), uptype, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			    this.getLogger().info("New version available! " + updater.getLatestName());
			}
		}
		
	}
	
	@Override
	public void onDisable(){
		Console.sendMessage(StringsID.DISABLING);
		AllBanksLogger.info("Disabling AllBanks...");
		
		//Cerrar todas las sesiones.
		Collection<BankSession> sessions = BankSession.getAllActiveSessions();
		
		for(BankSession bs : sessions){
			bs.closeSession();
		}
		
		AllBanksLogger.info("Closing database connections...");
		
		if(getStorageMethod().equals(StorageType.SQLITE)) {
			for(Connection c : dbSQLite.multipleConnections.values()){
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			dbSQLite.multipleConnections.clear();
		}
		
		if(getStorageMethod().equals(StorageType.MYSQL)) {
			for(Connection c : dbMySQL.multipleConnections.values()){
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			dbMySQL.multipleConnections.clear();
		}
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
        	Console.sendMessage(ChatColor.RED + "[Error] Ops! Vault plugin is required for AllBanks...");
        	AllBanksLogger.severe("Vault plugin is required for AllBanks...");
        	return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	Console.sendMessage(ChatColor.RED + "[Error] Ops! An economy plugin is required for AllBanks...");
        	AllBanksLogger.severe("An economy plugin is required for AllBanks...");
        	return false;
        }
        
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public static Economy getEconomy(){
		return econ;
	}
	
	public static void installDatabase(){
		
		Statement stm = null;
		
		if(Util.DatabaseUtil.databaseIsLocked()){
			AllBanksLogger.severe("Database is locked! Database installation aborted.");
			return;
		}
		
		try{
			AllBanksLogger.info("Try to install the database...");
			stm = dbc.createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS signs (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, location TEXT NOT NULL)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS bankloan_accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, loan TEXT NOT NULL)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS bankmoney_accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, money TEXT NOT NULL)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS bankxp_accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, xp NUMBER)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS banktime_accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, time NUMBER)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS bankloan_pending_charges (id INTEGER PRIMARY KEY AUTOINCREMENT, owner TEXT NOT NULL, amount TEXT NOT NULL)");
			AllBanksLogger.info("Success: 0 problems found.");
		}catch (SQLException e){
			AllBanksLogger.info("Ops! An SQLException has ocurred...");
			Util.DatabaseUtil.checkDatabaseIsLocked(e);
		}finally{
			if(stm != null)
				try {
					stm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
		AllBanksLogger.info("Checking if Config.yml is up to date...");
		
		File cfgFile = new File(getInstance().getDataFolder() + File.separator + "Config.yml");
		if(!cfgFile.exists()) ensureConfigExists();
		
		YamlConfiguration loadCfg = YamlConfiguration.loadConfiguration(cfgFile);
		String version = loadCfg.getString("cfg-version", "-1");
		
		if(version.equals("-1")){
			AllBanksLogger.warning("cfg-version can not resolved... updating Config.yml");
			//No se encontró la versión, forzaremos una actualización
			ensureConfigExists(true);
			//Cargar de nuevo la versión
			version = loadCfg.getString("cfg-version", "-1");
			//Comprobar de nuevo
			if(version.equals("-1")){
				//Error
				try {
					AllBanksLogger.severe("Exception: Can't get 'cfg-version' from Config.yml.");
					throw new Exception("Can't get 'cfg-version' from Config.yml.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			//Bien, procesar
			if(!version.equalsIgnoreCase(getInstance().getDescription().getVersion())){
				AllBanksLogger.info("Changes detected, updating Config.yml...");
				//Distintas versiones, intentar actualizar nativamente.
				UpdateConfigWithNativeFile();
				//Actualizar
				getInstance().reloadConfig();
				//Tratar de añadir comentarios al archivo
				FixConfigComments();
			}
		}
	}
	
	public static void FixConfigComments() {
		//Definir archivo
		File configFile = new File(AllBanks.getInstance().getDataFolder() + File.separator + "Config.yml");
		if(!configFile.exists()) return;
		
		try {
			//Cargar archivo
			FileInputStream inputStream = new FileInputStream(configFile);
			//Crear stream
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			//Crear buffer
			BufferedReader read = new BufferedReader(inputStreamReader);
			
			//Lineas del archivo leido
			List<String> lines = new ArrayList<String>();
			//Nuevas lineas, para la escritura del archivo
			List<String> newLines = new ArrayList<String>();
			
			//While(true), esto hace que el bucle nunca termine
			while(true) {
				String line;
				try {
					//Leer linea
					line = read.readLine();
					//Si la linea es nula, esto quiere decir que se ha alcanzado el fin del archivo asi que rompemos el bucle.
					if(line == null) break;
					//Si el bucle continua, añadimos la linea al listado
					lines.add(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
			for(String line : lines) {
				
				if(line.contains("cfg-version:")) {
					newLines.add("# do not edit this.");
				}else if(line.contains("default-permissions:")) {
					newLines.add("# These permissions are given to all Players, it is useful if you want to use AllBanks without a Permission-Plugin.");
					newLines.add("# Note: For example, if you set \"allbanks.sign.loan.use\" into a default permission, and you do want to deny this for a specific group/player, ");
					newLines.add("# you can use \"-\" before it (\"-allbanks.sign.loan.use\") in your Permission-Plugin.");
				}else if(line.contains("language:")) {
					newLines.add("  # Language (Supported languages: EnUs and EsMx)");
				}else if(line.contains("prefix:")) {
					newLines.add("  # Set chat prefix (colors supported)");
				}else if(line.contains("enable-metrics:")) {
					newLines.add("  # Enable metrics");
				}else if(line.contains("check-for-updates:")) {
					newLines.add("    # Check for new updates");
				}else if(line.contains("auto-update:")) {
					newLines.add("    # Update AllBanks if a new version was found.");
				}else if(line.contains("storage-system:")) {
					newLines.add("  # Available storage systems: FlatFile, SQLite (default) and MySQL");
				}else if(line.contains("mysql-host:")) {
					newLines.add("# If you set \"storage-system\" with MySQL these configurations are required:");
				}else if(line.contains("ticket-cost:")) {
					newLines.add("  # Ticket cost.");
				}else if(line.contains("get-winer-every:")) {
					newLines.add("  # Get winner every X time:");
				}else if(line.contains("max-tickets-per-player:")) {
					newLines.add("  # Max tickets per player:");
				}else if(line.contains("broadcast-message:")) {
					newLines.add("  # Broadcast message when the Lottery gets a winner.");
				}else if(line.contains("interest:")) {
					newLines.add("    #Interest (in percent) for the loan. (For example 2% -> 5000 = 100)");
				}else if(line.contains("max-loan:")) {
					newLines.add("    #How much can a player borrow? (Default: 5000)");
				}else if(line.contains("collect-interest-every:")) {
					newLines.add("    #This configuration accept these \"time values\": day(s), hour(s), minute(s), second(s)");
					newLines.add("    #You can set multiple time values, for example: \"1 days, 20 hours, 15 minutes, 1 second\" = (1440 * 60) + (1200 * 60) + (15 * 60) + 1 = 156901 seconds.");
				}else if(line.contains("stop-collect-if-player-balance-is-minor-than:")) {
					newLines.add("    #Stop interest collection if player have a balance minor than 500 (-500).");
					newLines.add("    #It is useful when the player has left the server for several days, ");
					newLines.add("    #it prevents that balance of the player reaches high negative amounts (Impossibles for paying)...");
				}else if(line.contains("max-money-player-can-save:")) {
					newLines.add("    # How much money can save the player in the bank?");
					newLines.add("    # -1 = unlimited");
				}else if(line.contains("pay-per-minute:")) {
					newLines.add("    # Pay $1 per minute. Example: 10 minutes = 10 x 1 = $10");
				}else if(line.contains("add-minute-every:")) {
					newLines.add("    # Add one minute every 60 seconds.");
				}else if(line.contains("max-virtual-chests-per-player:")) {
					newLines.add("    # Number of max virtual chest per player.");
				}else if(line.contains("max-shop-per-user:")) {
					newLines.add("    # Number of shops per player.");
					newLines.add("    # -1 = unlimited");
				}
				
				newLines.add(line);
			}
			
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Escribir
			FileOutputStream outpuStream = new FileOutputStream(configFile, false);
			OutputStreamWriter outpuStreamWriter = new OutputStreamWriter(outpuStream);
			BufferedWriter write = new BufferedWriter(outpuStreamWriter);
			
			for(String s : newLines) {
				try {
					write.write(s);
					write.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void UpdateConfigWithNativeFile(){
		//Copiar la anterior Configuración
		File Config = new File(getInstance().getDataFolder() + File.separator + "Config.yml");
		File tempConfig = new File(getInstance().getDataFolder() + File.separator + "TMP-Config.yml");

		AllBanksLogger.info("Renaming Config.yml to TMP-Config.yml");
		Config.renameTo(tempConfig);
		
		//guardar nueva configuración
		AllBanksLogger.info("Saving native resource: Config.yml");
		getInstance().saveResource("Config.yml", true);
		
		//Comenzar a comparar
		AllBanksLogger.info("Loading: TMP-Config.yml");
		YamlConfiguration nativeCfg = YamlConfiguration.loadConfiguration(Config);
		AllBanksLogger.info("Loading: Config.yml");
		YamlConfiguration userCfg = YamlConfiguration.loadConfiguration(tempConfig);
		
		AllBanksLogger.info("Searching for changes...");
		for(String key : nativeCfg.getKeys(true)){
			Object obj = userCfg.get(key, null);
			
			if(obj == null){
				userCfg.set(key, nativeCfg.get(key));
				AllBanksLogger.info("New entry: " + key);
			}
		}
		
		//Actualizar la versión
		AllBanksLogger.info("Updating cfg-version...");
		userCfg.set("cfg-version", getInstance().getDescription().getVersion());
		
		//guardar
		AllBanksLogger.info("Saving changes...");
		try {
			userCfg.save(tempConfig);
			AllBanksLogger.info("Success!");
		} catch (IOException e) {
			getInstance().getLogger().severe("An error has ocurred while trying update Config.yml to the latest version. (IOException)");
			e.printStackTrace();
			AllBanksLogger.severe("An error has ocurred while trying update Config.yml to the latest version. (IOException)");
		}
		
		//eliminar configuración nativa
		AllBanksLogger.info("Removing temporal file (Config.yml)");
		Config.delete();
		//Cambiar configuración temporal a su estado normal
		AllBanksLogger.info("Renaming TMP-Config.yml to Config.yml (restore file)");
		tempConfig.renameTo(Config);
		AllBanksLogger.info("Success: 0 problems found.");
	}
	
	public static AllBanks getInstance(){
		return AllBanksInstance;
	}
	
	public static Connection getDataBaseConnection(){
		return dbc;
	}
	
	public static StorageType getStorageMethod() {
		return storageMethod;
	}
	
	private static void verifyServerVersion() {
		
		AllBanksLogger.info("Verifying compatibles versions...");
		
		String rawBukkitVersion = Bukkit.getServer().getBukkitVersion();
		String rawVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
		
		
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
		replaceMap.put("%1%", rawBukkitVersion);
		
		if(compatible){
			AllBanksLogger.info("You are using a compatible version of CraftBukkit.");
			Console.sendMessage(StringsID.YOU_ARE_RUNNING_A_COMPATIBLE_VERSION_OF_CB, replaceMap);
		}else{
			AllBanksLogger.info("You are not using a compatible version of CraftBukkit.");
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
