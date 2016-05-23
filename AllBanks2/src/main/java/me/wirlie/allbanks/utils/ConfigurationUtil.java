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
package me.wirlie.allbanks.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.YamlConfiguration;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.logger.AllBanksLogger;

/**
 * Funciones de utilidad para la configuración.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ConfigurationUtil{

	public static String convertSecondsIntoTimeAgo(int seconds, boolean fullString) {
		return convertSecondsIntoTimeAgo(seconds, fullString, 2);
	}
	
	public static String convertSecondsIntoTimeAgo(int seconds, int maxShow) {
		return convertSecondsIntoTimeAgo(seconds, false, maxShow);
	}
	
	private static String convertSecondsIntoTimeAgo(int seconds, boolean fullString, int maxShow) {
		//Semanas
		int weeks = (int) Math.floor(seconds / (60 * 60 * 24 * 7));
		seconds -= weeks * (60 * 60 * 24 * 7);
		
		//Días
		int days = (int) Math.floor(seconds / (60 * 60 * 24)); 
		seconds -= days * (60 * 60 * 24);
		
		//Horas
		int hours = (int) Math.floor(seconds / (60 * 60)); 
		seconds -= hours * (60 * 60);
		
		//Minutos
		int minutes = (int) Math.floor(seconds / (60)); 
		seconds -= minutes * (60);
		
		String returnStr = "";
		int showed = 0;
		
		if(weeks > 0) {
			if(showed < maxShow || fullString) {
				returnStr += weeks + " " + Translation.get(StringsID.WEEKS, false)[0];
				showed++;
			}
		}
		
		if(days > 0) {
			if(showed < maxShow || fullString) {
				returnStr += ((showed == 0) ? "" : ", ") + days + " " + Translation.get(StringsID.DAYS, false)[0];
				showed++;
			}
		}
		
		if(hours > 0) {
			if(showed < maxShow || fullString) {
				returnStr += ((showed == 0) ? "" : ", ") + hours + " " + Translation.get(StringsID.HOURS, false)[0];
				showed++;
			}
		}
		
		if(minutes > 0) {
			if(showed < maxShow || fullString) {
				returnStr += ((showed == 0) ? "" : ", ") + minutes + " " + Translation.get(StringsID.MINUTES, false)[0];
				showed++;
			}
		}
		
		if(seconds > 0) {
			if(showed < maxShow || fullString) {
				returnStr += ((showed == 0) ? "" : ", ") + seconds + " " + Translation.get(StringsID.SECONDS, false)[0];
				showed++;
			}
		}
		
		if(seconds <= 0 && showed == 0) {
			returnStr += ((showed == 0) ? "" : ", ") + 0 + " " + Translation.get(StringsID.SECONDS, false)[0];
			showed++;
		}
		
		return returnStr;
	}
	
	public static String convertTimeValueToSecondsAndConvertIntoTimeAgo(String strTimeValue, int maxShow){
		int seconds = convertTimeValueToSeconds(strTimeValue);
		
		if(seconds == -1) return "Undefined";
		
		return convertSecondsIntoTimeAgo(seconds, maxShow);
	}
	
	/**
	 * Bueno, esto realmente ayuda con la configuración banks.bank-loan.collect-interest-every
	 * ya que se encuentra en formato 0 days, 0 hours, 0 minutes, 0 seconds y es necesario
	 * convertir todo eso a un mensaje que se pueda adaptar a las traducciones.
	 * @param strTimeValue
	 * @return
	 */
	public static int convertTimeValueToSeconds(String strTimeValue){
		
		if(strTimeValue == null) return -1;
		
		//pasar "0 days, 0 hours" a "0 days,0 hours"
		strTimeValue = strTimeValue.replace(", ", ",");
		
		//Separar valores con la coma.
		String[] split = strTimeValue.split(",");
		
		int totalSeconds = 0;
		
		//comprobar si es válido
		for(String s : split){
			//Usar REGEX
			Pattern r = Pattern.compile("^([0-9]{1,2})+ +(days|day|hours|hour|minutes|minute|seconds|second)$");
			Matcher m = r.matcher(s);
			
			if(!m.matches()){
				//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
				AllBanksLogger.warning("&7[&fUtil&7] &eInvalid timeValue: " + s, true);
				AllBanksLogger.warning("&7[&fUtil&7] &eValid values: days | day | hours | hour | minutes | minute | seconds | second", true);
				try{
					throw new IllegalArgumentException("Invalid timeValue: " + s);
				}catch(IllegalArgumentException e){
					e.printStackTrace();
					return -1;
				}
			}else{
				
				if(m.groupCount() != 2){
					AllBanksLogger.warning("&7[&fUtil&7] &eThe value " + s + " does not have a valid syntax for a timeValue.", true);
					AllBanksLogger.warning("&7[&fUtil&7] &eValid syntax: '{INT} {StringValue}', example: '1 day', '1 minute'", true);
					//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
					return -1;
				}
				
				int intValue = 0;
				String timeValue = m.group(2);
				
				try{
					intValue = Integer.parseInt(m.group(1));
				}catch(NumberFormatException e){
					AllBanksLogger.warning("&7[&fUtil&7] &eThe value " + m.group(1) + " is not a valid number.", true);
					AllBanksLogger.warning("&7[&fUtil&7] &eMatched string: " + s, true);
					AllBanksLogger.warning("&7[&fUtil&7] &eFull string: " + strTimeValue, true);
					e.printStackTrace();
					//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
					return -1;
				}
				
				if(timeValue.equalsIgnoreCase("days")){
					totalSeconds += intValue * 24 * 60 * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("day")){
					totalSeconds += intValue * 24 * 60 * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("hours")){
					totalSeconds += intValue * 60 * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("hour")){
					totalSeconds += intValue * 60 * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("minutes")){
					totalSeconds += intValue * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("minute")){
					totalSeconds += intValue * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("seconds")){
					totalSeconds += intValue;
					continue;
				}else if(timeValue.equalsIgnoreCase("second")){
					totalSeconds += intValue;
					continue;
					
				}
			}
		}
		
		return totalSeconds;
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
			String relativeCfgSection = "";
			
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
				}else if(line.contains("max-time-player-can-gather-in-bank:")) {
					newLines.add("    # Max amount of time that a player can gather in the bank.");
				}else if(line.contains("max-xp-player-can-save:")) {
					newLines.add("    # How much xp can save the player in the bank?");
					newLines.add("    # -1 = unlimited");
					newLines.add("    # Supported values: Exp amount:     2000");
					newLines.add("    #                   Levels:         10Lvl or 10Levels or 10L");
				}else if(line.contains("update-cache-every:")) {
					newLines.add("  # Because the TopRanks needs to read All accounts for Allbanks, the cache is useful if do you want to prevent a higher server consumption.");
					newLines.add("  # Please do not set it with a minimal value (example: 1 second)");
				}
				
				if(line.contains("modules:")){
					relativeCfgSection = "modules";
				}else if(relativeCfgSection.equalsIgnoreCase("modules") && line.contains("banks:")){
					relativeCfgSection = "modules.banks";
				}else if(relativeCfgSection.equalsIgnoreCase("modules.banks") && line.contains("bank-loan:")){
					newLines.add("    # Enable/Disable BankLoan");
				}else if(relativeCfgSection.equalsIgnoreCase("modules.banks") && line.contains("bank-xp:")){
					newLines.add("    # Enable/Disable BankXP");
				}else if(relativeCfgSection.equalsIgnoreCase("modules.banks") && line.contains("bank-money:")){
					newLines.add("    # Enable/Disable BankMoney");
				}else if(relativeCfgSection.equalsIgnoreCase("modules.banks") && line.contains("bank-time:")){
					newLines.add("    # Enable/Disable BankTime");
				}else if(relativeCfgSection.equalsIgnoreCase("modules.banks") && line.contains("bank-chest:")){
					newLines.add("    # Enable/Disable BankChest");
				}else if(relativeCfgSection.contains("modules") && line.contains("shop:")){
					relativeCfgSection = "modules";
					newLines.add("  # Enable/Disable AllBanksShop (Shops)");
				}else if(relativeCfgSection.contains("modules") && line.contains("top-ranks:")){
					relativeCfgSection = "modules";
					newLines.add("  # Enable/Disable TopRanks");
				}else if(relativeCfgSection.contains("modules") && line.contains("allbanksland:")){
					relativeCfgSection = "modules";
					newLines.add("  # Enable/Disable AllBanksLand");
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
	 * Comprobar si la configuración se encuentra actualizada.
	 */
	public static void ensureConfigIsUpToDate(){
		AllBanksLogger.info("Checking if Config.yml is up to date...");
		
		File cfgFile = new File(AllBanks.getInstance().getDataFolder() + File.separator + "Config.yml");
		if(!cfgFile.exists()) ConfigurationUtil.ensureConfigExists();
		
		YamlConfiguration loadCfg = YamlConfiguration.loadConfiguration(cfgFile);
		String version = loadCfg.getString("cfg-version", "-1");
		
		if(version.equals("-1")){
			AllBanksLogger.warning("cfg-version can not resolved... updating Config.yml");
			//No se encontró la versión, forzaremos una actualización
			ConfigurationUtil.ensureConfigExists(true);
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
			if(!version.equalsIgnoreCase(AllBanks.getInstance().getDescription().getVersion())){
				AllBanksLogger.info("Changes detected, updating Config.yml...");
				//Distintas versiones, intentar actualizar nativamente.
				Util.UpdateConfigWithNativeFile();
				//Actualizar
				AllBanks.getInstance().reloadConfig();
				//Tratar de añadir comentarios al archivo
				FixConfigComments();
			}
		}
	}

	/**
	 * Checar si la configuración existe, si no existe se guardará el archivo default.
	 * @param force Forzar el guardado del archivo Config.yml sin importar si ya existe.
	 */
	public static void ensureConfigExists(boolean force){
		File cfgFile = new File(AllBanks.getInstance().getDataFolder() + File.separator + "Config.yml");
		if(!cfgFile.exists() || force){
			AllBanks.getInstance().saveResource("Config.yml", true);
			AllBanks.getInstance().reloadConfig();
		}
	}

	/**
	 * Checar si la configuración existe, si no existe se guardará el archivo default.
	 */
	public static void ensureConfigExists(){
		ensureConfigExists(false);
	}
	
	public static void checkConfigurationStartup(){
		
		AllBanksLogger.debug("[CONFIG] Validating Config.yml ...");
		AllBanksLogger.debug("[CONFIG] Config.yml AbsolutePath: " + new File(AllBanks.getInstance().getDataFolder() + File.separator + "Config.yml").getAbsolutePath());
		AllBanksLogger.debug("[CONFIG] Config.yml LocalPath: " + new File(AllBanks.getInstance().getDataFolder() + File.separator + "Config.yml"));
		AllBanksLogger.debug("[CONFIG] Config.yml exists: " + new File(AllBanks.getInstance().getDataFolder() + File.separator + "Config.yml").exists());
		AllBanksLogger.debug("[CONFIG] Config.yml isFile: " + new File(AllBanks.getInstance().getDataFolder() + File.separator + "Config.yml").isFile());
		int warns = 0, ok = 0;
		
		YamlConfiguration config = AllBanks.getInstance().getConfig();
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
		
		if(config.getString("banks.bank-xp.max-xp-player-can-save", null) == null){
			AllBanksLogger.warning("[CONFIG] banks.bank-xp.max-xp-player-can-save -> return null, configuration not exists or not is a valid String.");
			warns++;
		}else{
			AllBanksLogger.debug("[CONFIG] banks.bank-xp.max-xp-player-can-save -> ok: " + config.getString("banks.bank-xp.max-xp-player-can-save"));
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
}