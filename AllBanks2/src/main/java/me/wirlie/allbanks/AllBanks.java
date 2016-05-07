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
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.Updater.UpdateResult;
import me.wirlie.allbanks.Updater.UpdateType;
import me.wirlie.allbanks.banks.listeners.ChargeLoanPlayerJoinListener;
import me.wirlie.allbanks.banks.listeners.PlayerChatBankSessionListener;
import me.wirlie.allbanks.banks.listeners.PlayerMoveListener;
import me.wirlie.allbanks.banks.listeners.PreventRemoveSignForOtherCausesListener;
import me.wirlie.allbanks.banks.listeners.SignBreakListener;
import me.wirlie.allbanks.banks.listeners.SignChangeListener;
import me.wirlie.allbanks.banks.listeners.SignInteractListener;
import me.wirlie.allbanks.banks.listeners.VirtualChestCloseListener;
import me.wirlie.allbanks.command.CommandExecutorAB;
import me.wirlie.allbanks.command.CommandTabCompleterAB;
import me.wirlie.allbanks.hooks.HookManager;
import me.wirlie.allbanks.land.AllBanksWorld;
import me.wirlie.allbanks.land.commands.CommandExecutorABLand;
import me.wirlie.allbanks.land.commands.CommandTabCompleterABLand;
import me.wirlie.allbanks.land.listeners.PlotBucketListener;
import me.wirlie.allbanks.land.listeners.PlotEntityCroopsPhysicalListener;
import me.wirlie.allbanks.land.listeners.PlotEntityExplosionListener;
import me.wirlie.allbanks.land.listeners.PlotEntityListener;
import me.wirlie.allbanks.land.listeners.PlotFireSpreadListener;
import me.wirlie.allbanks.land.listeners.PlotLavaWaterFlowListener;
import me.wirlie.allbanks.land.listeners.PlotLeashUnleashListener;
import me.wirlie.allbanks.land.listeners.PlotPlayerBlockBreakListener;
import me.wirlie.allbanks.land.listeners.PlotPlayerBlockPlaceListener;
import me.wirlie.allbanks.land.listeners.PlotPlayerDropItemListener;
import me.wirlie.allbanks.land.listeners.PlotPlayerMoveListener;
import me.wirlie.allbanks.land.listeners.PlotPlayerVehicleListener;
import me.wirlie.allbanks.land.listeners.PlotPvPListener;
import me.wirlie.allbanks.land.listeners.PlotPlayerInteractListener;
import me.wirlie.allbanks.listeners.common.PlayerJoinUpdaterMessage;
import me.wirlie.allbanks.listeners.shops.ShopChestInteractListener;
import me.wirlie.allbanks.listeners.shops.ShopSignBreakListener;
import me.wirlie.allbanks.listeners.shops.ShopSignChangeListener;
import me.wirlie.allbanks.listeners.shops.ShopSignInteractListener;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.runnables.BankLoanRunnable;
import me.wirlie.allbanks.runnables.BankTimerRunnable;
import me.wirlie.allbanks.runnables.LotteryRunnable;
import me.wirlie.allbanks.tempdata.BankSession;
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
	private static DataBaseSQLite dbSQLite = new DataBaseSQLite();
	private static DataBaseMySQL dbMySQL = new DataBaseMySQL();
	private static Connection dbc;
	private static StorageType storageMethod = StorageType.SQLITE;
	private static Economy econ = null;
	public static boolean updatePending = false;
	
	/** Resultados usados al momento de comparar versiones. */
	private enum VersionCheckResult{
		COMPATIBLE,
		NOT_COMPATIBLE,
		PROCCEED_WITH_PRECAUTION
	}

	/** Versiones compatibles con esta version. */
	public final static String[] COMPATIBLE_VERSIONS = {
			"1.9",
			};
	
	/** Version incompatible mínima **/
	public static String INCOMPATIBLE_MIN = "1.8";
	/** Version incompatible maxima **/
	public static String INCOMPATIBLE_MAX = "0";
	
	/** Tipo de almacenamiento que usará AllBanks para almacenar los datos. */
	public enum StorageType{
		FLAT_FILE,
		SQLITE,
		MYSQL;
	}
	
	/** Función para habilitar AllBanks **/
	@Override
	public void onEnable(){

		AllBanksInstance = this;
		
		//Inicializar logger.
		AllBanksLogger.initializeLogger();
		AllBanksLogger.info("Enabling AllBanks " + getDescription().getVersion() + " - CB: " + Bukkit.getBukkitVersion());

		//Comprobar si la configuración se encuentra actualizada.
		ensureConfigIsUpToDate();
		
		getConfig();
		checkConfigurationStartup();
		
		//Comprobar la versión del servidor y la versión compatible/incompatible del plugin.
		VersionCheckResult result = verifyServerVersion();
		
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
			if(!DataBaseMySQL.tryForClass()) {
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
    		Bukkit.getServer().getPluginManager().disablePlugin(this);
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
			dbc = dbMySQL.setConnection("global");
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
		if(!setupEconomy()){
		    Bukkit.getPluginManager().disablePlugin(this);
		    return;
		}
		
		//Mensaje de habilitando
		Console.sendMessage(StringsID.ENABLING);
		
		//Registrar comandos y TabCompleter
		AllBanksLogger.info("Commands: Set executor (/allbanks).");
		Bukkit.getPluginCommand("allbanks").setExecutor(new CommandExecutorAB());
		AllBanksLogger.info("Commands: Set tab completer (/allbanks).");
		Bukkit.getPluginCommand("allbanks").setTabCompleter(new CommandTabCompleterAB());
		AllBanksLogger.info("Commands: Set executor (/abland).");
		Bukkit.getPluginCommand("allbanksland").setExecutor(new CommandExecutorABLand());
		AllBanksLogger.info("Commands: Set tab completer (/abland).");
		Bukkit.getPluginCommand("allbanksland").setTabCompleter(new CommandTabCompleterABLand());
		
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
					AllBanks.getInstance().getLogger().info("[Updater] AllBanks already updated!");
					return;
				}
				
				AllBanks.getInstance().getLogger().info("[Updater] Checking for Updates...");
				
				//Comprobar si está habilitado el chequeo y descarga automática de AllBanks
				boolean checkForUpdates = getConfig().getBoolean("pl.updater.check-for-updates", true);
				boolean forceUpdate = getConfig().getBoolean("pl.updater.auto-update", true);
				
				//Establecer el tipo de actualización en DEFAULT, esto para señalar al Actualizador que procederá a verificar y descargar por defecto
				UpdateType uptype = UpdateType.DEFAULT;
				
				//Si está habilitado el chequeo, pero no la descarga indicaremos al actualizador que no queremos descargar el archivo
				if(checkForUpdates && !forceUpdate) {
					uptype = UpdateType.NO_DOWNLOAD;
				}
				
				//Si no está habilitado el chequeo, pero si está habilitada la descarga indicaremos al actualizador que sólo queremos descargar el archivo
				if(!checkForUpdates && forceUpdate) {
					uptype = UpdateType.NO_VERSION_CHECK;
				}
				
				if(checkForUpdates || forceUpdate) {
					//Iniciar actualizador
					Updater updater = new Updater(AllBanks.getInstance(), 98949, AllBanks.getInstance().getFile(), uptype, true);
					
					//Resolver resultado del actualizador
					if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
					    AllBanks.getInstance().getLogger().info("[Updater] New version available! " + updater.getLatestName());
					}else if(updater.getResult() == UpdateResult.NO_UPDATE) {
						AllBanks.getInstance().getLogger().info("[Updater] No updates found. Your AllBanks plugin is up to date.");
					}else if(updater.getResult() == UpdateResult.SUCCESS){
						AllBanks.getInstance().getLogger().info("[Updater] Please reload AllBanks... ");
						updatePending = true;
						
						//Nuevo Runnable para informar a los administradores cada hora.
						new BukkitRunnable(){
							public void run(){
								for(Object obp : Bukkit.getOnlinePlayers().toArray()){
									Player p = (Player) obp;
									
									if(p.isOp() && AllBanks.updatePending){
										Translation.getAndSendMessage(p, StringsID.UPDATER_PLEASE_RELOAD_ALLBANKS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + AllBanks.getInstance().getDescription().getVersion()), true);
									}
								}
							}
						}.runTaskTimer(AllBanks.getInstance(), 0, 20 * 60 * 60);
						
						return;
					}else{
						AllBanks.getInstance().getLogger().info("[Updater] A problem was ocurred: " + updater.getResult());
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
		        getLogger().info("Metrics started!");
		    } catch (IOException e) {
		        getLogger().info("Metrics failed...");
		        e.printStackTrace();
		    }
		}
		
	}
	
	@Override
	public YamlConfiguration getConfig(){
		
		File configFile = new File(getDataFolder() + File.separator + "Config.yml");
		
		if(!configFile.exists()){
			saveResource("Config.yml", true);
		}
		
		if(!configFile.exists()){
			AllBanksLogger.severe("AllBanks cannot find the configuration file!!", true);
			AllBanksLogger.severe("AbsolutePath: " + configFile.getAbsolutePath(), true);
			AllBanksLogger.severe("Check if your server have write permissions for the plugin folder.");
			return null;
		}
		
		return YamlConfiguration.loadConfiguration(configFile);
	}
	
	public void checkConfigurationStartup(){
		
		AllBanksLogger.debug("[CONFIG] Validating Config.yml ...");
		AllBanksLogger.debug("[CONFIG] Config.yml AbsolutePath: " + new File(getDataFolder() + File.separator + "Config.yml").getAbsolutePath());
		AllBanksLogger.debug("[CONFIG] Config.yml LocalPath: " + new File(getDataFolder() + File.separator + "Config.yml"));
		AllBanksLogger.debug("[CONFIG] Config.yml exists: " + new File(getDataFolder() + File.separator + "Config.yml").exists());
		AllBanksLogger.debug("[CONFIG] Config.yml isFile: " + new File(getDataFolder() + File.separator + "Config.yml").isFile());
		int warns = 0, ok = 0;
		
		YamlConfiguration config = getConfig();
		if(config.getStringList("default-permissions") == null){
			AllBanksLogger.warning("[CONFIG] default-permissions -> return null, configuration not exists or not is a valid List of type String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] default-permissions -> ok");
			ok++;
		}
		
		if(config.getString("pl.language") == null){
			AllBanksLogger.warning("[CONFIG] pl.language -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.language -> ok: " + config.getString("pl.language"));
			ok++;
		}
		
		if(config.getString("pl.prefix") == null){
			AllBanksLogger.warning("[CONFIG] pl.prefix -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.prefix -> ok: " + config.getString("pl.prefix"));
			ok++;
		}
		
		if(config.getString("pl.enable-metrics") == null){
			AllBanksLogger.warning("[CONFIG] pl.enable-metrics -> return null, configuration not exists or not is a valid Boolean.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.enable-metrics -> ok: " + config.getString("pl.enable-metrics"));
			ok++;
		}
		
		if(config.getString("pl.updater.check-for-updates") == null){
			AllBanksLogger.warning("[CONFIG] pl.updater.check-for-updates -> return null, configuration not exists or not is a valid Boolean.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.updater.check-for-updates -> ok: " + config.getString("pl.updater.check-for-updates"));
			ok++;
		}
		
		if(config.getString("pl.updater.auto-update") == null){
			AllBanksLogger.warning("[CONFIG] pl.updater.auto-update -> return null, configuration not exists or not is a valid Boolean.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.updater.auto-update -> ok: " + config.getString("pl.updater.auto-update"));
			ok++;
		}
		
		if(config.getString("pl.storage-system") == null){
			AllBanksLogger.warning("[CONFIG] pl.storage-system -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.storage-system -> ok: " + config.getString("pl.storage-system"));
			ok++;
		}
		
		if(config.getString("pl.mysql-host") == null){
			AllBanksLogger.warning("[CONFIG] pl.mysql-host -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.mysql-host -> ok: " + config.getString("pl.mysql-host"));
			ok++;
		}
		
		if(config.getString("pl.mysql-user") == null){
			AllBanksLogger.warning("[CONFIG] pl.mysql-user -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.mysql-user -> ok: " + config.getString("pl.mysql-user"));
			ok++;
		}
		
		if(config.getString("pl.mysql-pass") == null){
			AllBanksLogger.warning("[CONFIG] pl.mysql-pass -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.mysql-pass -> ok: " + config.getString("pl.mysql-pass"));
			ok++;
		}
		
		if(config.getInt("pl.mysql-port", -1) == -1){
			AllBanksLogger.warning("[CONFIG] pl.mysql-port -> return -1, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] pl.mysql-port -> ok: " + config.getString("pl.mysql-port"));
			ok++;
		}
		
		if(config.getString("lottery.enable") == null){
			AllBanksLogger.warning("[CONFIG] lottery.enable -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] lottery.enable -> ok: " + config.getString("lottery.enable"));
			ok++;
		}
		
		if(config.getInt("lottery.ticket-cost", -1) == -1){
			AllBanksLogger.warning("[CONFIG] lottery.ticket-cost -> return -1, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] lottery.ticket-cost -> ok: " + config.getString("lottery.ticket-cost"));
			ok++;
		}
		
		if(config.getString("lottery.get-winer-every") == null){
			AllBanksLogger.warning("[CONFIG] lottery.get-winer-every -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] lottery.get-winer-every -> ok: " + config.getString("lottery.get-winer-every"));
			ok++;
		}
		
		if(config.getInt("lottery.max-tickets-per-player", -1) == -1){
			AllBanksLogger.warning("[CONFIG] lottery.max-tickets-per-player -> return -1, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] lottery.max-tickets-per-player -> ok: " + config.getString("lottery.max-tickets-per-player"));
			ok++;
		}
		
		if(config.getString("lottery.broadcast-message") == null){
			AllBanksLogger.warning("[CONFIG] lottery.broadcast-message -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] lottery.broadcast-message -> ok: " + config.getString("lottery.broadcast-message"));
			ok++;
		}
		
		if(config.getInt("banks.bank-loan.interest", -1) == -1){
			AllBanksLogger.warning("[CONFIG] banks.bank-loan.interest -> return -1, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-loan.interest -> ok: " + config.getString("banks.bank-loan.interest"));
			ok++;
		}
		
		if(config.getInt("banks.bank-loan.max-loan", -1) == -1){
			AllBanksLogger.warning("[CONFIG] banks.bank-loan.max-loan -> return -1, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-loan.max-loan -> ok: " + config.getString("banks.bank-loan.max-loan"));
			ok++;
		}
		
		if(config.getString("banks.bank-loan.collect-interest-every") == null){
			AllBanksLogger.warning("[CONFIG] banks.bank-loan.collect-interest-every -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-loan.collect-interest-every -> ok: " + config.getString("banks.bank-loan.collect-interest-every"));
			ok++;
		}
		
		if(config.getInt("banks.bank-loan.stop-collect-if-player-balance-is-minor-than", -1) == -1){
			AllBanksLogger.warning("[CONFIG] banks.bank-loan.stop-collect-if-player-balance-is-minor-than -> return -1, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-loan.stop-collect-if-player-balance-is-minor-than -> ok: " + config.getString("banks.bank-loan.stop-collect-if-player-balance-is-minor-than"));
			ok++;
		}
		
		if(config.getInt("banks.bank-money.max-money-player-can-save", -2) == -2){
			AllBanksLogger.warning("[CONFIG] banks.bank-money.max-money-player-can-save -> return -2, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-money.max-money-player-can-save -> ok: " + config.getString("banks.bank-money.max-money-player-can-save"));
			ok++;
		}
		
		if(config.getDouble("banks.bank-time.pay-per-minute", -1) == -1){
			AllBanksLogger.warning("[CONFIG] banks.bank-time.pay-per-minute -> return -1, configuration not exists or not is a valid Double.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-time.pay-per-minute -> ok: " + config.getString("banks.bank-time.pay-per-minute"));
			ok++;
		}
		
		if(config.getInt("banks.bank-time.add-minute-every", -1) == -1){
			AllBanksLogger.warning("[CONFIG] banks.bank-time.add-minute-every -> return -1, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-time.add-minute-every -> ok: " + config.getString("banks.bank-time.add-minute-every"));
			ok++;
		}
		
		if(config.getInt("banks.bank-chest.max-virtual-chests-per-player", -1) == -1){
			AllBanksLogger.warning("[CONFIG] banks.bank-chest.max-virtual-chests-per-player -> return -1, configuration not exists or not is a valid Integer.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-chest.max-virtual-chests-per-player -> ok: " + config.getString("banks.bank-chest.max-virtual-chests-per-player"));
			ok++;
		}
		
		if(config.getString("shop.admin-tag") == null){
			AllBanksLogger.warning("[CONFIG] shop.admin-tag -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] shop.admin-tag -> ok: " + config.getString("shop.admin-tag"));
			ok++;
		}
		
		if(config.getString("shop.enable-fake-item") == null){
			AllBanksLogger.warning("[CONFIG] shop.enable-fake-item -> return null, configuration not exists or not is a valid Boolean.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] shop.enable-fake-item -> ok: " + config.getString("shop.enable-fake-item"));
			ok++;
		}
		
		if(config.getString("shop.enable-fake-item-for-user-shop") == null){
			AllBanksLogger.warning("[CONFIG] shop.enable-fake-item-for-user-shop -> return null, configuration not exists or not is a valid Boolean.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] shop.enable-fake-item-for-user-shop -> ok: " + config.getString("shop.enable-fake-item-for-user-shop"));
			ok++;
		}
		
		if(config.getString("topranks.update-cache-every") == null){
			AllBanksLogger.warning("[CONFIG] topranks.update-cache-every -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] topranks.update-cache-every -> ok: " + config.getString("topranks.update-cache-every"));
			ok++;
		}
		
		AllBanksLogger.debug("[CONFIG] Done: " + warns + " warnings, " + ok + " ok, total: " + (ok + warns) + ".");
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
	 * @return true si la instalación fue exitosa.
	 */
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
	 * Checar si la configuración existe, si no existe se guardará el archivo default.
	 */
	public static void ensureConfigExists(){
		ensureConfigExists(false);
	}
	
	/**
	 * Checar si la configuración existe, si no existe se guardará el archivo default.
	 * @param force Forzar el guardado del archivo Config.yml sin importar si ya existe.
	 */
	public static void ensureConfigExists(boolean force){
		File cfgFile = new File(getInstance().getDataFolder() + File.separator + "Config.yml");
		if(!cfgFile.exists() || force){
			getInstance().saveResource("Config.yml", true);
			getInstance().reloadConfig();
		}
	}
	
	/**
	 * Comprobar si la configuración se encuentra actualizada.
	 */
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
	
	/**
	 * Este método repara los comentarios del archivo Config.yml, ya que, Bukkit no conserva
	 * los comentarios al momento de guardar/editar una variable.
	 */
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
			
			int firstLine = 0;
			
			for(String line : lines) {
				
				if(firstLine == 0){
					firstLine++;
					continue;
				}
				
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
					newLines.add("  # Enable metrics (like MCStats)");
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
				}else if(line.contains("enable-fake-item:")) {
					newLines.add("  # If this is configured with true, AllBanks will try to spawn a fake item if an Admin Shop is created.");
				}else if(line.contains("enable-fake-item-for-user-shop:")) {
					newLines.add("  # Enable fake items for shops of users");
				}else if(line.contains("update-cache-every:")) {
					newLines.add("  # Because the TopRanks needs to read All accounts for Allbanks, the cache is useful if do you want to prevent a higher server consumption.");
					newLines.add("  # Please do not set it with a minimal value (example: 1 second)");
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
	
	/**
	 * Actualizar la configuración del usuario con la configuración nativa de AllBanks.
	 */
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
		
		//Permisos default
		List<String> defaultPerms = userCfg.getStringList("default-permissions");
		
		if(Util.compareVersionString(userCfg.getString("cfg-version", "0"), "1.1") == -1){
			if(!defaultPerms.contains("allbanks.land.commands.plot.claim")) defaultPerms.add("allbanks.land.commands.plot.claim");
			if(!defaultPerms.contains("allbanks.land.commands.plot.unclaim")) defaultPerms.add("allbanks.land.commands.plot.unclaim");
			if(!defaultPerms.contains("allbanks.land.commands.plot.set.flags")) defaultPerms.add("allbanks.land.commands.plot.set.flags");
			if(!defaultPerms.contains("allbanks.land.commands.plot.add")) defaultPerms.add("allbanks.land.commands.plot.add");
			if(!defaultPerms.contains("allbanks.land.commands.plot.deny")) defaultPerms.add("allbanks.land.commands.plot.deny");
		}
		
		userCfg.set("default-permissions", defaultPerms);
		
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
	
	/**
	 * Obtener la versión de Bukkit (sólamente la parte de la versión del servidor)
	 * @return Devuelve la versión de Bukkit en un formato de "1.0.0"
	 */
	public static String getBukkitVersion(){
		return Bukkit.getServer().getBukkitVersion().split("-")[0];
	}
	
	/**
	 * Verificar si la versión del servidor es compatible con esta version de AllBanks.
	 * @return Resultado de la operación.
	 */
	private static VersionCheckResult verifyServerVersion() {
		
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
			return VersionCheckResult.COMPATIBLE;
		}else{
			//Detectar si se está usando una versión incompatible o una versión no probada
			if(Util.compareVersionString(INCOMPATIBLE_MIN, rawVersion) == 1 
				|| Util.compareVersionString(INCOMPATIBLE_MIN, rawVersion) == 0 
				|| Util.compareVersionString(INCOMPATIBLE_MAX, rawVersion) == -1 && !INCOMPATIBLE_MAX.equalsIgnoreCase("0")
				|| Util.compareVersionString(INCOMPATIBLE_MAX, rawVersion) == 0 && !INCOMPATIBLE_MAX.equalsIgnoreCase("0")) {
				AllBanks.getInstance().getLogger().severe("Please use the correct version of CraftBukkit/Spigot.");
				AllBanks.getInstance().getLogger().severe("For this build, CB 1.9 is expected.");
				return VersionCheckResult.NOT_COMPATIBLE;
			} else {
				AllBanksLogger.severe("You are not using a compatible version of CraftBukkit.");
				Console.sendMessage(StringsID.YOU_ARENT_RUNNING_A_COMPATIBLE_VERSION_OF_CB, replaceMap);
				return VersionCheckResult.PROCCEED_WITH_PRECAUTION;
			}
		}
		
	}
	
	//Version para el paquete ej: net.minecraft.VERSION.clase
	/*
	 * String name = getServer().getClass().getPackage().getName();
	   String version = name.substring(name.lastIndexOf('.') + 1);
		
	   System.out.println(version);
	 */
}
