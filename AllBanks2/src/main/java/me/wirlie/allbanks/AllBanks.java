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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.Updater.UpdateResult;
import me.wirlie.allbanks.Updater.UpdateType;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.banks.bankdata.BankSession;
import me.wirlie.allbanks.command.CommandExecutorAB;
import me.wirlie.allbanks.command.CommandTabCompleterAB;
import me.wirlie.allbanks.command.shops.CommandExecutorAllBanksShop;
import me.wirlie.allbanks.command.shops.CommandTabCompleterAllBanksShop;
import me.wirlie.allbanks.commands.allbanksland.CommandExecutorABLand;
import me.wirlie.allbanks.commands.allbanksland.CommandExecutorABLandPlotAccess;
import me.wirlie.allbanks.commands.allbanksland.CommandTabCompleterABLand;
import me.wirlie.allbanks.commands.allbanksland.CommandTabCompleterABLandPlotAccess;
import me.wirlie.allbanks.hooks.HookManager;
import me.wirlie.allbanks.listeners.allbanksland.PlotBucketListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotEntityCroopsPhysicalListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotEntityExplosionListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotEntityListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotFireSpreadListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotLWCListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotLavaWaterFlowListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotLeashUnleashListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotLocketteListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotPlayerBlockBreakListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotPlayerBlockPlaceListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotPlayerDropItemListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotPlayerInteractListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotPlayerMoveListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotPlayerVehicleListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotPotionListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotPvPListener;
import me.wirlie.allbanks.listeners.allbanksland.PlotTreeGrowEvent;
import me.wirlie.allbanks.listeners.banks.ChargeLoanPlayerJoinListener;
import me.wirlie.allbanks.listeners.banks.PlayerChatBankSessionListener;
import me.wirlie.allbanks.listeners.banks.PlayerMoveListener;
import me.wirlie.allbanks.listeners.banks.PreventRemoveSignForOtherCausesListener;
import me.wirlie.allbanks.listeners.banks.SignBreakListener;
import me.wirlie.allbanks.listeners.banks.SignChangeListener;
import me.wirlie.allbanks.listeners.banks.SignInteractListener;
import me.wirlie.allbanks.listeners.banks.VirtualChestCloseListener;
import me.wirlie.allbanks.listeners.common.PlayerJoinUpdaterMessage;
import me.wirlie.allbanks.listeners.shops.ShopChestInteractListener;
import me.wirlie.allbanks.listeners.shops.ShopSignBreakListener;
import me.wirlie.allbanks.listeners.shops.ShopSignChangeListener;
import me.wirlie.allbanks.listeners.shops.ShopSignInteractListener;
import me.wirlie.allbanks.runnables.BankLoanRunnable;
import me.wirlie.allbanks.runnables.BankTimerRunnable;
import me.wirlie.allbanks.runnables.LotteryRunnable;
import me.wirlie.allbanks.statistics.AllBanksStatistics;
import me.wirlie.allbanks.utils.AllBanksLogger;
import me.wirlie.allbanks.utils.ConfigurationUtil;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.FakeItemManager;
import me.wirlie.allbanks.utils.Util;
import net.milkbowl.vault.economy.Economy;

/**
 * Clase principal de AllBanks usada por la librería de Bukkit para la activación y desactivación
 * del plugin.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class AllBanks extends JavaPlugin {

	private static AllBanks AllBanksInstance;
	/** base de datos SQLite */
	public static DataBaseSQLite dbSQLite = new DataBaseSQLite();
	private static DataBaseMySQL dbMySQL = new DataBaseMySQL();
	private static Connection dbc;
	private static StorageType storageMethod = StorageType.SQLITE;
	private static Economy econ = null;
	
	/**
	 * Si hay una actualización pendiente.
	 */
	public static boolean updatePending = false;
	
	/**
	 * Versión de la actualización pendiente. Sólo si {@link #updatePending} es {@code true}.
	 */
	public static String updatePendingVersion = "";
	
	/** Resultados usados al momento de comparar versiones. */
	public enum VersionCheckResult{
		/**
		 * Si la versión actual de CB es compatible con AllBanks2.
		 */
		COMPATIBLE,
		/**
		 * Si la versión actual de CB no es compatible con AllBanks2.
		 */
		NOT_COMPATIBLE,
		/**
		 * Si la versión actual no ha sido probada.
		 */
		PROCCEED_WITH_PRECAUTION
	}

	/** Versiones compatibles con esta version. */
	public final static String[] COMPATIBLE_VERSIONS = {
			"1.9",
			"1.9.2",
			"1.9.4",
			"1.10"
			};
	
	/** Version incompatible mínima **/
	public static String INCOMPATIBLE_MIN = "1.8";
	/** Version incompatible maxima **/
	public static String INCOMPATIBLE_MAX = "0";
	
	/** Tipo de almacenamiento que usará AllBanks para almacenar los datos. */
	public enum StorageType{
		/**
		 * Almacenamiento por archivo.
		 */
		FLAT_FILE,
		/**
		 * Almacenamiento por base de datos SQLite.
		 */
		SQLITE,
		/**
		 * Almacenamiento vía MySQL.
		 */
		MYSQL;
	}
	
	/** Función para habilitar AllBanks **/
	@Override
	public void onEnable(){

		AllBanksInstance = this;
		
		//Inicializar logger.
		AllBanksLogger.initializeLogger();
		AllBanksLogger.info("Enabling AllBanks " + getDescription().getVersion() + " - CB: " + Bukkit.getBukkitVersion(), true);

		//Comprobar si la configuración se encuentra actualizada.
		ConfigurationUtil.ensureConfigIsUpToDate();
		
		getConfig();
		ConfigurationUtil.checkConfigurationStartup();
		
		//Comprobar la versión del servidor y la versión compatible/incompatible del plugin.
		VersionCheckResult result = Util.verifyServerVersion();
		
		//Si el resultado de la comparación es No Compatible, entonces deshabilitamos AllBanks.
		if(result.equals(VersionCheckResult.NOT_COMPATIBLE)) {
			//No compatible
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		//Resolver el método de almacenamiento que AllBanks usará en este servidor
		String storageStr = getConfig().getString("pl.storage-system", "SQLite");
		
		//Resolver
		if(storageStr.equalsIgnoreCase("sqlite")) {
			storageMethod = StorageType.SQLITE;
		}else if(storageStr.equalsIgnoreCase("mysql")) {
			storageMethod = StorageType.MYSQL;
		}else if(storageStr.equalsIgnoreCase("flatfile")){
			storageMethod = StorageType.FLAT_FILE;
		}
		
		//Comprobar si el método resuelto es soportado por el servidor, especialmente si se ha especificado SQLite o MySQL
		switch(getStorageMethod()) {
		case FLAT_FILE:
			//No es necesario comprobar si el método es soportado por el servidor.
    		AllBanksLogger.info("Storage Method: FlatFile");
			break;
		case MYSQL:
			//Comprobar si el driver para MySQL existe.
    		AllBanksLogger.info("Storage Method: MySQL");
			if(!DataBaseMySQL.tryFindClassForName()) {
	    		AllBanksLogger.warning("Ops! DriverManager not found, setting storage method: FlatFile");
	    		storageMethod = StorageType.FLAT_FILE;
			}
			break;
		case SQLITE:
			//Comprobar si el driver para SQLite existe.
    		AllBanksLogger.info("Storage Method: SQLite");
			if(!DataBaseSQLite.tryForClass()) {
	    		AllBanksLogger.warning("Ops! DriverManager not found, setting storage method: FlatFile");
	    		storageMethod = StorageType.FLAT_FILE;
			}
			break;
		
		}
		
		//Necesario para que AllBanks funcione.
		if(!DataBaseSQLite.tryForClass()) {
    		AllBanksLogger.severe("Ops! Class not found: org.sqlite.JDBC", true);
    		AllBanksLogger.severe("JDBC driver is required, disabling AllBanks...", true);
    		setEnabled(false);
    		return;
		}
		
		//Establecer conexión para la base de datos que ayudará a resolver los objetos que almacenan datos NBT (Libros encantados)
		dbSQLite.setConnection(getDataFolder() + File.separator + "itemSolution.db", "itemSolution");
		//PlotWorlds (Mundos)
		dbSQLite.setConnection(getDataFolder() + File.separator + "AllBanksLandWorldData.db", "AllBanksLand");
		//Instalar la base de datos, en caso de que no exista.
		installItemSolutionDataBase();
		installAllBanksLandDataBase();
		
		//Establecer la conexión global de la base de datos según el método específicado (SQLite o MySQL)
		if(getStorageMethod().equals(StorageType.MYSQL)) {
			AllBanksLogger.info("Initializing MySQL database...");
			dbc = dbMySQL.setConnection();
		}else {
			AllBanksLogger.info("Initializing SQLite database...");
			dbc = dbSQLite.setConnection(getDataFolder() + File.separator + "LocalDataBase.db", "local");
		}
		//Instalar bases de datos tanto MySQL como SQLite
		if(getStorageMethod().equals(StorageType.MYSQL) || getStorageMethod().equals(StorageType.SQLITE)) {
			installDatabase();
		}
		
		//Cargar mundos
		AllBanksWorld.loadWorldsStartup();
		
		//Instalar la economía, si no se ha encontrado Vault o algún plugin de economía AllBanks se deshabilitará con un aviso.
		setupEconomy();
		
		//Registrar comandos y TabCompleter
		AllBanksLogger.info("Commands: Set executor (/allbanks).");
		Bukkit.getPluginCommand("allbanks").setExecutor(new CommandExecutorAB());
		AllBanksLogger.info("Commands: Set tab completer (/allbanks).");
		Bukkit.getPluginCommand("allbanks").setTabCompleter(new CommandTabCompleterAB());
		AllBanksLogger.info("Commands: Set executor (/abland).");
		Bukkit.getPluginCommand("allbanksland").setExecutor(new CommandExecutorABLand());
		AllBanksLogger.info("Commands: Set tab completer (/abland).");
		Bukkit.getPluginCommand("allbanksland").setTabCompleter(new CommandTabCompleterABLand());
		AllBanksLogger.info("Commands: Set executor (/plot).");
		Bukkit.getPluginCommand("plot").setExecutor(new CommandExecutorABLandPlotAccess());
		AllBanksLogger.info("Commands: Set tab completer (/plot).");
		Bukkit.getPluginCommand("plot").setTabCompleter(new CommandTabCompleterABLandPlotAccess());
		AllBanksLogger.info("Commands: Set executor (/allbanksshop).");
		Bukkit.getPluginCommand("allbanksshop").setExecutor(new CommandExecutorAllBanksShop());
		AllBanksLogger.info("Commands: Set tab completer (/allbanksshop).");
		Bukkit.getPluginCommand("allbanksshop").setTabCompleter(new CommandTabCompleterAllBanksShop());
		
		//Hooks
		HookManager.initializeHookManager();
		
		//Solución
		Banks.convertOldVirtualChestDataToNewDataMethod();
		
		//Registrar Listeners para los eventos.
		AllBanksLogger.info("Registering events...");
		Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignBreakListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerChatBankSessionListener(), this);
		Bukkit.getPluginManager().registerEvents(new VirtualChestCloseListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChargeLoanPlayerJoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new ShopSignChangeListener(), this);
		Bukkit.getPluginManager().registerEvents(new ShopSignInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new ShopSignBreakListener(), this);
		Bukkit.getPluginManager().registerEvents(new ShopChestInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new PreventRemoveSignForOtherCausesListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinUpdaterMessage(), this);
		//AllBanksLand
		Bukkit.getPluginManager().registerEvents(new PlotPlayerBlockBreakListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotPlayerBlockPlaceListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotPlayerMoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotPlayerInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotPlayerDropItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotEntityCroopsPhysicalListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotLeashUnleashListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotPlayerVehicleListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotLavaWaterFlowListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotBucketListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotFireSpreadListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotEntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotEntityExplosionListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotPvPListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlotTreeGrowEvent(), this);
		Bukkit.getPluginManager().registerEvents(new PlotPotionListener(), this);
		//LWC
		if(HookManager.LWCHook.isHooked()){
			Bukkit.getPluginManager().registerEvents(new PlotLWCListener(), this);
		}
		//Lockette
		if(HookManager.LocketteHook.isHooked()){
			Bukkit.getPluginManager().registerEvents(new PlotLocketteListener(), this);
		}
		
		/*
		 * RUNNABLES
		 */
		
		//Inicializar el modulo de objetos falsos de AllBanks, unicamente disponible para las tiendas.
		FakeItemManager.initializeItemManeger();
		
		//Inicializar runnable de BankTimer.
		int runSeconds = getConfig().getInt("banks.bank-time.add-minute-every", 60); //segundos
		if(runSeconds <= 0){ runSeconds = 60; } //si se ha especificado un valor inválido
		new BankTimerRunnable().runTaskTimer(this, 20 * runSeconds, 20 * runSeconds); //ejecutar
		
		//Iniciar runnable para BankLoan
		//REMOVE Mover este código a otro lado.
		AllBanksLogger.info("Enabling BankLoanRunnable...");
		AllBanksLogger.info("Reading Config.yml -> banks.bank-loan.collect-interest-every");
		int collectLoanEvery = ConfigurationUtil.convertTimeValueToSeconds(getConfig().getString("banks.bank-loan.collect-interest-every"));
		
		if(collectLoanEvery == -1 || collectLoanEvery == 0){ 

			AllBanksLogger.severe("Invalid configuration :");
			//No se puede usar el sistema cuando el tiempo es inválido.
			AllBanksLogger.severe("Invalid configuration in Config.yml.", true);
			AllBanksLogger.severe("banks.bank-loan.collect-interest-every is not valid.", true);
			AllBanksLogger.severe("Please set a numeric value more than 0.", true);
			AllBanksLogger.severe("BankLoan: Collect Loan System disabled...", true);

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
				AllBanksLogger.info("&7[&fBankLoanRunnable&7] &bInitializing system.", true);
				AllBanksLogger.info("&7[&fBankLoanRunnable&7] &bNext execution: " + collectLoanEvery + " seconds.", true);
				
				AllBanksLogger.info("[BankLoanRunnable] Starting runnable (TaskTimer)");
				
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
				AllBanksLogger.info("&7[&fBankLoanRunnable&7] &bInitializing system.", true);
				AllBanksLogger.info("&7[&fBankLoanRunnable&7] &bNext execution: " + nextCollection + " seconds.", true);
				
				AllBanksLogger.info("[BankLoanRunnable] Starting runnable (TaskTimer)");
				
				new BankLoanRunnable().runTaskTimer(this, nextCollection * 20, collectLoanEvery * 20);
			}
		}
		
		//Iniciar runnable de BankLottery
		LotteryRunnable.initializeLottery();
		try {
			LotteryRunnable.startRunnable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Iniciar runnable para expirar sesiones (esto es útil si un usuario decide quedarse ausente y está usando un banco).
		BankSession.StartExpireSessionRunnable();
		
		//Actualizador, ejecutar en un hilo de fondo (background thread)
		Runnable updaterRunnable = new Runnable(){
			public void run() {
				if(updatePending){
					AllBanksLogger.info("&7[&fUpdater&7] &bAllBanks already updated!", true);
					return;
				}
				
				//Comprobar si está habilitado el chequeo y descarga automática de AllBanks
				boolean checkForUpdates = getConfig().getBoolean("pl.updater.check-for-updates", true);
				boolean forceUpdate = getConfig().getBoolean("pl.updater.auto-update", true);
				
				//Establecer el tipo de actualización en DEFAULT, esto para señalar al Actualizador que procederá a verificar y descargar por defecto
				UpdateType uptype = UpdateType.DEFAULT;
				
				//Si está habilitado el chequeo, pero no la descarga indicaremos al actualizador que no queremos descargar el archivo
				if(checkForUpdates && !forceUpdate) {
					uptype = UpdateType.NO_DOWNLOAD;
				}
				
				if(checkForUpdates) {
					
					AllBanksLogger.info("&7[&fUpdater&7] &bChecking for Updates ...", true);

					//Iniciar actualizador
					Updater updater = new Updater(AllBanks.getInstance(), 98949, AllBanks.getInstance().getFile(), uptype, true);
					
					//Resolver resultado del actualizador
					if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
					    AllBanksLogger.info("&7[&fUpdater&7] &bNew version available! " + updater.getLatestName(), true);
					}else if(updater.getResult() == UpdateResult.NO_UPDATE) {
						AllBanksLogger.info("&7[&fUpdater&7] &bNo updates found. Your AllBanks plugin is up to date.", true);
					}else if(updater.getResult() == UpdateResult.SUCCESS){
						AllBanksLogger.info("&7[&fUpdater&7] &bPlease reload AllBanks... ", true);
						updatePending = true;
						updatePendingVersion = updater.getLatestName().split(" v")[1];
						
						//Nuevo Runnable para informar a los administradores 2 horas.
						new BukkitRunnable(){
							public void run(){
								for(Object obp : Bukkit.getOnlinePlayers().toArray()){
									Player p = (Player) obp;
									
									if(p.isOp() && AllBanks.updatePending){
										Translation.getAndSendMessage(p, StringsID.UPDATER_PLEASE_RELOAD_ALLBANKS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + AllBanks.updatePendingVersion), true);
									}
								}
							}
						}.runTaskTimer(AllBanks.getInstance(), 0, 20 * 60 * 120);
						
						return;
					}else{
						AllBanksLogger.info("&7[&fUpdater&7] &bA problem was ocurred: " + updater.getResult(), true);
					}
				}
			}
		};
		
		//Ejecutar hilo del actualizador en segundo plano
		new Thread(updaterRunnable).start();
		
		//Inicializar métricas de MCStats
		if(getConfig().getBoolean("pl.enable-metrics", true)) {
			try {
		        Metrics metrics = new Metrics(this);
		        metrics.start();
		        AllBanksLogger.info("&7[&fMetrics&7] &bMetrics started!", true);
		    } catch (IOException e) {
		    	AllBanksLogger.severe("&7[&fMetrics&7] &cMetrics failed.", true);
		        e.printStackTrace();
		    }
		}
		
		//Estadísticas
		AllBanksStatistics.initializeSystem();
		
	}
	
	@Override
	public YamlConfiguration getConfig(){
		
		File configFile = new File(getDataFolder() + File.separator + "Config.yml");
		
		if(!configFile.exists()){
			saveResource("Config.yml", true);
		}
		
		if(!configFile.exists()){
			AllBanksLogger.severe("[CONFIG] AllBanks cannot find the configuration file!!", true);
			AllBanksLogger.severe("[CONFIG] AbsolutePath: " + configFile.getAbsolutePath(), true);
			AllBanksLogger.severe("[CONFIG] Check if your server have write permissions for plugins folder.");
			return null;
		}
		
		return YamlConfiguration.loadConfiguration(configFile);
	}
	
	/** Función cuando AllBanks se deshabilita **/
	@Override
	public void onDisable(){
		//Mensaje de deshabilitación
		Console.sendMessage(StringsID.DISABLING);
		AllBanksLogger.info("Disabling AllBanks...");
		
		//Cerrar todas las sesiones actuales de AllBanks.
		Collection<BankSession> sessions = BankSession.getAllActiveSessions();
		
		for(BankSession bs : sessions){
			bs.closeSession();
		}
		
		//Cerrar conexiones de la base de datos
		AllBanksLogger.info("Closing database connections...");
		
		//SQLite siempre conservará una conexión: itemSolution
		for(Connection c : dbSQLite.multipleConnections.values()){
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//Limpiar mapa
		dbSQLite.multipleConnections.clear();
		
		//Cerrar base de datos con el método MySQL
		if(getStorageMethod().equals(StorageType.MYSQL)) {
			for(Connection c : dbMySQL.multipleConnections.values()){
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			//Limpiar mapa
			dbMySQL.multipleConnections.clear();
		}
	}
	
	/**
	 * Instalar Vault y plugin de economía.
	 */
	private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
        	Console.sendMessage(ChatColor.RED + "[Error] Ops! Vault plugin is required for AllBanks...");
        	AllBanksLogger.severe("Vault plugin is required for AllBanks...");
        	setEnabled(false);
        	throw new IllegalStateException("Vault is required.");
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	Console.sendMessage(ChatColor.RED + "[Error] Ops! An economy plugin is required for AllBanks...");
        	AllBanksLogger.severe("An economy plugin is required for AllBanks...");
        	setEnabled(false);
        	throw new IllegalStateException("EconomyPlugin is required.");
        }
        
        econ = rsp.getProvider();
        if(econ == null){
        	setEnabled(false);
        	throw new IllegalStateException("ServicesManager cannot find an Economy.class Provider.");
        }
    }
	
	/**
	 * Devolver la instancia actual de la Economía
	 * @return Instancia de Economy.
	 */
	public static Economy getEconomy(){
		return econ;
	}
	
	/**
	 * Instalar base de datos para la solución de objetos que almacenan datos através de NBT
	 */
	public static void installItemSolutionDataBase() {
		Statement stm = null;
		
		if(DataBaseUtil.databaseIsLocked()){
			AllBanksLogger.severe("Database is locked! Database installation aborted.");
			return;
		}
		
		try{
			AllBanksLogger.info("Try to install ItemSolution database...");
			stm = getSQLConnection("itemSolution").createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, itemmeta TEXT NOT NULL)");
			AllBanksLogger.info("Success: 0 problems found.");
		}catch (SQLException e){
			AllBanksLogger.info("Ops! An SQLException has ocurred...");
			DataBaseUtil.checkDatabaseIsLocked(e);
		}finally{
			if(stm != null)
				try {
					stm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Instalar la base de datos de AllBanksLand.
	 */
	public static void installAllBanksLandDataBase(){
		
		Statement stm = null;
		
		try{
			AllBanksLogger.info("Try to install ItemSolution database...");
			stm = getSQLConnection("AllBanksLand").createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS world_plots (id INTEGER PRIMARY KEY AUTOINCREMENT, world_id TEXT NOT NULL, plot_coord_X NUMBER, plot_coord_Z NUMBER, plot_owner TEXT NOT NULL, plot_config TEXT NOT NULL)");
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS worlds_cfg (id INTEGER PRIMARY KEY AUTOINCREMENT, world_id TEXT NOT NULL, plot_size NUMBER NOT NULL, road_size NUMBER NOT NULL, current_plot_cursor TEXT NULL)");
			AllBanksLogger.info("Success: 0 problems found.");
		}catch (SQLException e){
			AllBanksLogger.info("Ops! An SQLException has ocurred...");
			DataBaseUtil.checkDatabaseIsLocked(e);
		}finally{
			if(stm != null)
				try {
					stm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Instalar la base de datos de AllBanks (si el método de almacenamiento es SQLite o MySQL)
	 */
	public static void installDatabase(){
		
		Statement stm = null;
		
		if(DataBaseUtil.databaseIsLocked()){
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
			DataBaseUtil.checkDatabaseIsLocked(e);
		}finally{
			if(stm != null)
				try {
					stm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Obtener instancia de AllBanks.
	 * @return Instancia de AllBanks.
	 */
	public static AllBanks getInstance(){
		return AllBanksInstance;
	}
	
	/**
	 * Obtener la conexión a la base de datos.
	 * @param connectionName Nombre de la conexión
	 * @return Conexión obtenida por el nombre especificado
	 */
	public static Connection getSQLConnection(String connectionName){
		return dbSQLite.getConnection(connectionName);
	}
	
	/**
	 * Obtener la conexión Global a la base de datos.
	 * @return Conexión global de AllBanks
	 */
	public static Connection getDataBaseConnection(){
		return dbc;
	}
	
	/**
	 * Obtener el método de almacenamiento que actualmente AllBanks está usando.
	 * @return Método de almacenamiento en uso.
	 */
	public static StorageType getStorageMethod() {
		return storageMethod;
	}
	
	//Version para el paquete ej: net.minecraft.VERSION.clase
	/*
	 * String name = getServer().getClass().getPackage().getName();
	   String version = name.substring(name.lastIndexOf('.') + 1);
		
	   System.out.println(version);
	 */
	
	/**
	 * Obtener el archivo actual del plugin. Lo mismo que getFile();
	 * @return Archivo.
	 */
	public File getPluginFile(){
		return getFile();
	}
}
