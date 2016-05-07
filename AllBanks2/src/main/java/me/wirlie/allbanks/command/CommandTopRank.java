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
package me.wirlie.allbanks.command;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.AllBanks.StorageType;
import me.wirlie.allbanks.utils.ConfigurationUtil;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.ExperienceConversionUtil;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 *
 */
public class CommandTopRank extends Command {
	
	private static Map<String, BigDecimal> bankMoneyTopRankCache = new HashMap<String, BigDecimal>();
	private static long bankMoneyTopRankCacheTime = 0;
	
	private static Map<String, Integer> bankXPTopRankCache = new HashMap<String, Integer>();
	private static long bankXPTopRankCacheTime = 0;
	
	@Override
	public CommandExecuteResult execute(final CommandSender sender, String[] args){
		if(args.length >= 2){
			
			if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
				//Ayuda: /ab toprank ?
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab toprank " + ChatColor.AQUA + "bankmoney" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_TOPRANK_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab toprank " + ChatColor.AQUA + "bankxp" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_TOPRANK_DESC.toString(false));
				return CommandExecuteResult.OTHER;
			}else if(args[1].equalsIgnoreCase("bankmoney")) {
				
				if(!AllBanks.getInstance().getConfig().getBoolean("modules.top-ranks.enable")){
					Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>TopRanks"), true);
					return CommandExecuteResult.OTHER;
				}
				
				int page = 1;
				
				if(args.length >= 3) {
					try {
						page = Integer.parseInt(args[2]);
						
						if(page < 1) {
							page = 1;
						}
					}catch(NumberFormatException e) {
						page = 1;
					}
				}
				
				final int finalPage = page;
				
				if(!Util.hasPermission(sender, "allbanks.commands.toprank.bankmoney")){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
					if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				//Generar toprank del bankmoney
				new BukkitRunnable() {

					public void run() {
						long time = new Date().getTime();
						long difInSeconds = (time - bankMoneyTopRankCacheTime) / 1000;
						
						boolean bankMoneyTopRankFirstUse = (bankMoneyTopRankCacheTime == 0) ? true : false;
						
						boolean update = (difInSeconds > ConfigurationUtil.convertTimeValueToSeconds(AllBanks.getInstance().getConfig().getString("topranks.update-cache-every", "5 seconds")) || bankMoneyTopRankFirstUse);
						
						if(update) {
							Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_GENERATING, true);
							bankMoneyTopRankCacheTime = time;
							difInSeconds = 0;
						}
						
						if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
							//FlatFile
							File dataFolder = Util.FlatFile_bankAccountFolder;
							if(!dataFolder.exists()) {
								Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_NO_STATS, true);
								return;
							}
							
							if(update) {
								//Para evitar una sobrecarga, cada 5 minutos se puede generar un nuevo re-mapeo
								Map<String, BigDecimal> topRankMap = new HashMap<String, BigDecimal>();
								for(File f : dataFolder.listFiles()) {
									YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
									topRankMap.put(yaml.getString("owner"), new BigDecimal(yaml.getString("money")));
								}
								
								//guardar en el caché
								bankMoneyTopRankCache = topRankMap;
								
								bankMoneyTopRankFirstUse = false;
							}
						}else {
							//DataBase
							
							if(update) {
								try {
									Statement stm = AllBanks.getDataBaseConnection().createStatement();
									ResultSet res = stm.executeQuery("SELECT * FROM bankmoney_accounts");
									
									Map<String, BigDecimal> topRankMap = new HashMap<String, BigDecimal>();
									
									while(res.next()) {
										BigDecimal money = new BigDecimal(res.getString("money"));
										String owner = res.getString("owner");
										
										topRankMap.put(owner, money);
										
									}
									
									if(topRankMap.isEmpty()) {
										Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_NO_STATS, true);
										return;
									}
									
									//guardar en el caché
									bankMoneyTopRankCache = topRankMap;
									bankMoneyTopRankFirstUse = false;
								} catch(SQLException e) {
									DataBaseUtil.checkDatabaseIsLocked(e);
								}
							}
						}
						
						int minIndex = (finalPage - 1) * 20;
						int maxIndex = minIndex + 20;
						
						if(difInSeconds == (time / 1000)) {
							difInSeconds = 0;
						}
						
						Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_BANKMONEY_HEADER, true);
						Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_LATEST_UPDATE, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + ConfigurationUtil.convertSecondsIntoTimeAgo((int) difInSeconds, 1)), true);
						
						//Mostrar al usuario
						SortedSet<Entry<String, BigDecimal>> data = Util.entriesSortedByValues(bankMoneyTopRankCache);
						Object[] dataArray = data.toArray();
						
						for(int i = minIndex; i < maxIndex; i++) {
							if(dataArray.length <= i) break;
							
							@SuppressWarnings("unchecked")
							Entry<String, BigDecimal> entrydata = (Entry<String, BigDecimal>) dataArray[i];
							
							sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + (i + 1) + ": " + ChatColor.YELLOW + AllBanks.getEconomy().format(entrydata.getValue().doubleValue()) + ChatColor.GRAY + " - " + ChatColor.AQUA + entrydata.getKey());
						}
					}
					
				}.runTaskAsynchronously(AllBanks.getInstance());
				
				return CommandExecuteResult.SUCCESS;
			}else if(args[1].equalsIgnoreCase("bankxp")) {
				
				if(!AllBanks.getInstance().getConfig().getBoolean("modules.top-ranks.enable")){
					Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>TopRanks"), true);
					return CommandExecuteResult.OTHER;
				}
				
				int page = 1;
				
				if(args.length >= 3) {
					try {
						page = Integer.parseInt(args[2]);
						
						if(page < 1) {
							page = 1;
						}
					}catch(NumberFormatException e) {
						page = 1;
					}
				}
				
				final int finalPage = page;
				
				if(!Util.hasPermission(sender, "allbanks.commands.toprank.bankxp")){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
					if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				new BukkitRunnable() {
					public void run() {
						long time = new Date().getTime();
						long difInSeconds = (time - bankXPTopRankCacheTime) / 1000;

						boolean bankXPTopRankFirstUse = (bankXPTopRankCacheTime == 0) ? true : false;
						
						boolean update = (difInSeconds > ConfigurationUtil.convertTimeValueToSeconds(AllBanks.getInstance().getConfig().getString("topranks.update-cache-every", "5 seconds")) || bankXPTopRankFirstUse);
						
						
						if(update) {
							Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_GENERATING, true);
							bankXPTopRankCacheTime = time;
							difInSeconds = 0;
						}
						
						if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
							//FlatFile
							File dataFolder = Util.FlatFile_bankAccountFolder;
							if(!dataFolder.exists()) {
								Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_NO_STATS, true);
								return;
							}
							
							if(update) {
								//Para evitar una sobrecarga, cada 5 minutos se puede generar un nuevo re-mapeo
								Map<String, Integer> topRankMap = new HashMap<String, Integer>();
								for(File f : dataFolder.listFiles()) {
									YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
									topRankMap.put(yaml.getString("owner"), yaml.getInt("xp"));
								}
								
								//guardar en el caché
								bankXPTopRankCache = topRankMap;
								
								bankXPTopRankFirstUse = false;
							}
						}else {
							//DataBase
							
							if(update) {
								try {
									Statement stm = AllBanks.getDataBaseConnection().createStatement();
									ResultSet res = stm.executeQuery("SELECT * FROM bankxp_accounts");
									
									Map<String, Integer> topRankMap = new HashMap<String, Integer>();
									
									while(res.next()) {
										int xp = res.getInt("xp");
										String owner = res.getString("owner");
										
										topRankMap.put(owner, xp);
									}
									
									if(topRankMap.isEmpty()) {
										Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_NO_STATS, true);
										return;
									}
									
									//guardar en el caché
									bankXPTopRankCache = topRankMap;
									bankXPTopRankFirstUse = false;
								} catch(SQLException e) {
									DataBaseUtil.checkDatabaseIsLocked(e);
								}
							}
						}
						
						int minIndex = (finalPage - 1) * 20;
						int maxIndex = minIndex + 20;
						
						if(difInSeconds == (time / 1000)) {
							difInSeconds = 0;
						}
						
						Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_BANKXP_HEADER, true);
						Translation.getAndSendMessage(sender, StringsID.COMMAND_TOPRANK_LATEST_UPDATE, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + ConfigurationUtil.convertSecondsIntoTimeAgo((int) difInSeconds, 1)), true);
						
						//Mostrar al usuario
						SortedSet<Entry<String, Integer>> data = Util.entriesSortedByValues(bankXPTopRankCache);
						Object[] dataArray = data.toArray();
						
						for(int i = minIndex; i < maxIndex; i++) {
							if(dataArray.length <= i) break;
							
							@SuppressWarnings("unchecked")
							Entry<String, Integer> entrydata = (Entry<String, Integer>) dataArray[i];
							
							sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + (i + 1) + ": " + ChatColor.YELLOW + ExperienceConversionUtil.convertExpToLevel(entrydata.getValue()) + " " + Translation.get(StringsID.LEVELS, false)[0] + ChatColor.GRAY + " - " + ChatColor.AQUA + entrydata.getKey());
						}
					}
				}.runTaskAsynchronously(AllBanks.getInstance());
			}else{
				Translation.getAndSendMessage(sender, StringsID.COMMAND_SYNTAX_ERROR, true);
				Translation.getAndSendMessage(sender, 
						StringsID.COMMAND_SUGGEST_HELP, 
						Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab toprank ?"),
						true);
				return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
			}
		}else{
			Translation.getAndSendMessage(sender, 
					StringsID.COMMAND_SUGGEST_HELP, 
					Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab toprank ?"),
					true);
			return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
		}
		
		return CommandExecuteResult.INVALID_ARGUMENTS;
	}
	
}
