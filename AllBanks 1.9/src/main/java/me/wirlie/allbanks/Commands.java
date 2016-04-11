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
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks.StorageType;
import me.wirlie.allbanks.data.BankAccount;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.runnable.LotteryRunnable;
import me.wirlie.allbanks.util.ConfigurationUtil;
import me.wirlie.allbanks.util.DataBaseUtil;
import me.wirlie.allbanks.util.ExperienceConversionUtil;
import me.wirlie.allbanks.util.InteractiveUtil;
import me.wirlie.allbanks.util.InteractiveUtil.SoundType;
import me.wirlie.allbanks.util.ItemNameUtil;
import me.wirlie.allbanks.util.Util;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Commands implements CommandExecutor {
	
	private static Map<String, BigDecimal> bankMoneyTopRankCache = new HashMap<String, BigDecimal>();
	private static long bankMoneyTopRankCacheTime = 0;
	
	private static Map<String, Integer> bankXPTopRankCache = new HashMap<String, Integer>();
	private static long bankXPTopRankCacheTime = 0;

	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {

	            public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	            	int res = e2.getValue().compareTo(e1.getValue());
	                if (e1.getKey().equals(e2.getKey())) {
	                    return res; // Code will now handle equality properly
	                } else {
	                    return res != 0 ? res : 1; // While still adding all entries
	                }
	            }
	            
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		
		boolean senderIsPlayer = false;
		
		if(sender instanceof Player) {
			senderIsPlayer = true;
		}
		
		if(args.length == 0){
			Translation.getAndSendMessage(sender, StringsID.COMMAND_MAIN_INFO, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + AllBanks.getInstance().getDescription().getVersion()), true);
			return true;
		}
		
		final String mainAction = args[0];
		
		if(mainAction.equalsIgnoreCase("?") || mainAction.equalsIgnoreCase("help")) {
			//Comando de ayuda
			if(!Util.hasPermission(sender, "allbanks.commands.help")){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
				if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
				return true;
			}
			
			int page = 1;
			
			if(args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
					
					if(page < 1) {
						page = 1;
					}
					
				}catch(NumberFormatException e) {
					page = 1;
				}
			}
			
			int maxPages = 1;
			if(page > maxPages) page = maxPages;
			
			Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + page, "%2%>>>" + maxPages), true);
			
			switch(page) {
			case 1:
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab ? " + ChatColor.AQUA + "[page]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_HELP_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab toprank " + ChatColor.AQUA + "[bankmoney|bankxp]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_TOPRANK_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "info" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_INFO_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "buyticket [amount]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_BUYTICKET_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "[enable|disable]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_ENABLE_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "force" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_FORCE_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab reload" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_RELOAD_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab iteminfo" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_ITEMINFO_DESC.toString(false));
				
				break;
			}
			
			return true;
		}else if(mainAction.equalsIgnoreCase("reload")){
			if(!Util.hasPermission(sender, "allbanks.commands.reload")){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
				if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
				return true;
			}
			
			AllBanks.getInstance().reloadConfig();
			Translation.getAndSendMessage(sender, StringsID.COMMAND_RELOAD_SUCCESS, true);
			
			return true;
		}else if(mainAction.equalsIgnoreCase("testsound")){
			//REMOVE Remover esto, es un comando "debug"
			if(!Util.hasPermission(sender, "allbanks.commands.testsound")){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
				if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
				return true;
			}
			
			if(args.length >= 3){
				String soundName = args[1];
				float soundVariant = 1;
				
				try {
					soundVariant = (float) Double.parseDouble(args[2]);
				} catch(NumberFormatException e) {
					try {
						soundVariant = Integer.parseInt(args[2]);
					} catch(NumberFormatException e2) {
						soundVariant = 1;
					}
				}
				
				if(sender instanceof Player) {
					Player p = (Player) sender;
					p.playSound(p.getLocation(), Sound.valueOf(soundName), 10, soundVariant);
					System.out.println(soundVariant);
				}
				
				return true;
			}else if(args.length >= 2) {
				String soundName = args[1];
				
				if(sender instanceof Player) {
					Player p = (Player) sender;
					p.playSound(p.getLocation(), Sound.valueOf(soundName), 10, 0);
				}
				
				return true;
			}
				
			
		}else if(mainAction.equalsIgnoreCase("toprank")){
			
			if(args.length >= 2){
				
				if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return true;
				}
				
				if(args[1].equalsIgnoreCase("bankmoney")) {
					
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
						if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
						return true;
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
							SortedSet<Entry<String, BigDecimal>> data = entriesSortedByValues(bankMoneyTopRankCache);
							Object[] dataArray = data.toArray();
							
							for(int i = minIndex; i < maxIndex; i++) {
								if(dataArray.length <= i) break;
								
								@SuppressWarnings("unchecked")
								Entry<String, BigDecimal> entrydata = (Entry<String, BigDecimal>) dataArray[i];
								
								sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + (i + 1) + ": " + ChatColor.YELLOW + AllBanks.getEconomy().format(entrydata.getValue().doubleValue()) + ChatColor.GRAY + " - " + ChatColor.AQUA + entrydata.getKey());
							}
						}
						
					}.runTaskAsynchronously(AllBanks.getInstance());
					
					return true;
				}else if(args[1].equalsIgnoreCase("bankxp")) {
					
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
						if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
						return true;
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
							SortedSet<Entry<String, Integer>> data = entriesSortedByValues(bankXPTopRankCache);
							Object[] dataArray = data.toArray();
							
							for(int i = minIndex; i < maxIndex; i++) {
								if(dataArray.length <= i) break;
								
								@SuppressWarnings("unchecked")
								Entry<String, Integer> entrydata = (Entry<String, Integer>) dataArray[i];
								
								sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + (i + 1) + ": " + ChatColor.YELLOW + ExperienceConversionUtil.convertExpToLevel(entrydata.getValue()) + " " + Translation.get(StringsID.LEVELS, false)[0] + ChatColor.GRAY + " - " + ChatColor.AQUA + entrydata.getKey());
							}
						}
					}.runTaskAsynchronously(AllBanks.getInstance());
				}
			}
			
		}else if(mainAction.equalsIgnoreCase("database")){
			if(args.length >= 2){
				if(args[1].equalsIgnoreCase("try-query")){
					
					if(sender instanceof Player || sender instanceof BlockCommandSender){
						Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_CONSOLE, true);
						return true;
					}
					
					if(!Util.hasPermission(sender, "allbanks.commands.database.executequery")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
						if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
						return true;
					}
					
					if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
						Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_AVAILABLE_ON_DATABASE, true);
						return true;
					}
					
					if(args.length >= 2){
						String query = "";
						
						for(int i = 2; i < args.length; i++){
							query += args[i] + " ";
						}
						
						//Intentar ejecutar
						if(DataBaseUtil.databaseIsLocked()){
							DataBaseUtil.sendDatabaseLockedMessage(sender);
							return true;
						}
						
						Statement stm = null;
						ResultSet res = null;
						
						try{
							stm = AllBanks.getDataBaseConnection().createStatement();
							res = stm.executeQuery(query);
							int numColumns = res.getMetaData().getColumnCount();
							
							for(int i = 0; i < 10; i++){
								
								//solo 10 registros
								if(res.next()){
									sender.sendMessage("#" + i + ":");
									for(int i2 = 1; i2 <= numColumns; i2++){
										Object object = res.getObject(i2);
										sender.sendMessage(res.getMetaData().getColumnName(i2) + " = " + object);
									}
								}else{
									break;
								}
							}
							
							Translation.getAndSendMessage(sender, StringsID.COMMANDS_DATABASE_QUERY_SUCCESS, (sender instanceof Player));
							
						}catch(SQLException e){
							DataBaseUtil.checkDatabaseIsLocked(e);
							if(!DataBaseUtil.databaseIsLocked()){
								Translation.getAndSendMessage(sender, StringsID.COMMANDS_DATABASE_INVALID_QUERY, (sender instanceof Player));
								sender.sendMessage(ChatColor.RED + e.getMessage());
							}
						}finally{
							try {
								res.close();
								stm.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}else{
						//No cumple con: /ab database query <QUERY>
						return false;
					}
					
					return true;
				}else if(args[1].equalsIgnoreCase("try-update")){

					if(sender instanceof Player || sender instanceof BlockCommandSender){
						Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_CONSOLE, true);
						return true;
					}	
					
					if(!Util.hasPermission(sender, "allbanks.commands.database.executequery")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
						if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
						return true;
					}
					
					if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
						Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_AVAILABLE_ON_DATABASE, true);
						return true;
					}
					
					if(args.length >= 2){
						String query = "";
			
						for(int i = 2; i < args.length; i++){
							query += args[i] + " ";
						}
						
						//Intentar ejecutar
						if(DataBaseUtil.databaseIsLocked()){
							DataBaseUtil.sendDatabaseLockedMessage(sender);
							return true;
						}
						
						Statement stm = null;
						
						try{
							stm = AllBanks.getDataBaseConnection().createStatement();
							stm.executeUpdate(query);
							
							Translation.getAndSendMessage(sender, StringsID.COMMANDS_DATABASE_QUERY_SUCCESS, (sender instanceof Player));
							
							//Vaciar caché de BankAccount
							BankAccount.Cache.clearCache();
							
							stm.close();
						}catch(SQLException e){
							DataBaseUtil.checkDatabaseIsLocked(e);
							if(!DataBaseUtil.databaseIsLocked()){
								Translation.getAndSendMessage(sender, StringsID.COMMANDS_DATABASE_INVALID_QUERY, (sender instanceof Player));
								sender.sendMessage(ChatColor.RED + e.getMessage());
							}
						}finally{
							try {
								stm.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}else{
						//No cumple con: /ab database query <QUERY>
						return false;
					}
					
					return true;
				}else{
					//No cumple con NINGUN argumento valido para /ab database
					return false;
				}
			}else{
				//No cumple con los requisitos: /ab database <arg>
				return false;
			}
		}else if(mainAction.equalsIgnoreCase("lottery")){
			if(DataBaseUtil.databaseIsLocked()){
				DataBaseUtil.sendDatabaseLockedMessage(sender);
				return true;
			}
			
			if(args.length >= 2){
				if(args[1].equalsIgnoreCase("buyticket")){
					if(args.length == 3){
						
						if(!(sender instanceof Player)){
							Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
							return true;
						}
						
						if(!Util.hasPermission(sender, "allbanks.commands.lottery.buyticket")){
							Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
							if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
							return true;
						}
						
						int amount = 0;
						
						try{
							amount = Integer.parseInt(args[2]);
							
							if(amount <= 0){
								Translation.getAndSendMessage(sender, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
								return true;
							}
						}catch(NumberFormatException e){
							//No es un número válido
							HashMap<String, String> replaceMap = new HashMap<String, String>();
							replaceMap.put("%1%", args[2]);
							Translation.getAndSendMessage(sender, StringsID.NO_VALID_NUMBER, replaceMap, true);
							return true;
						}
						
						//Bien intentar comprar un ticket
						int currentTicketsBought = 0;
						
						if(LotteryRunnable.dirTickets.exists()){
							for(File f : LotteryRunnable.dirTickets.listFiles()){
								YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
								if(yaml.getString("owner").equalsIgnoreCase(sender.getName())){
									currentTicketsBought++;
								}
							}
						}

						int maxBuy = AllBanks.getInstance().getConfig().getInt("lottery.max-tickets-per-player", 5);
						
						for(PermissionAttachmentInfo pinfo : sender.getEffectivePermissions()){
							if(pinfo.getPermission().startsWith("allbanks.lottery.maxtickets.")){
								try{
									maxBuy = Integer.parseInt(pinfo.getPermission().replace("allbanks.lottery.maxtickets.", ""));
								}catch(NumberFormatException e2){
									
								}
							}
						}
						
						//No se pueden permitir valores negativos.
						if(maxBuy < 0) maxBuy = 0;
						
						int canBuy = maxBuy - currentTicketsBought;
						
						if(canBuy - amount < 0){
							//Ya no puede comprar más tickets
							HashMap<String, String> replaceMap = new HashMap<String, String>();
							replaceMap.put("%1%", String.valueOf(amount));
							replaceMap.put("%2%", String.valueOf(canBuy));
							Translation.getAndSendMessage(sender, StringsID.LOTTERY_CAN_NOT_BUY_MORE_TICKETS, replaceMap, true);
							return true;
						}
						
						//¿Tiene dinero?
						double ticket_cost = AllBanks.getInstance().getConfig().getDouble("lottery.ticket-cost", 50);
						BigDecimal total_cost = new BigDecimal(ticket_cost).multiply(new BigDecimal(amount));
						
						if(!AllBanks.getEconomy().has((Player) sender, total_cost.doubleValue())){
							Translation.getAndSendMessage(sender, StringsID.YOU_DO_NOT_HAVE_MONEY, true);
							return true;
						}
						
						//Comprar
							
						if(!LotteryRunnable.dirTickets.exists())
							LotteryRunnable.dirTickets.mkdirs();
						
						for(int i = 0; i < amount; i++){
							File ticketFile = new File(LotteryRunnable.dirTickets + File.separator + "ticket-" + sender.getName() + "-" + new Date().getTime() + "-" + i + ".yml");
							try {
								ticketFile.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							YamlConfiguration yaml = YamlConfiguration.loadConfiguration(ticketFile);
							yaml.set("owner", sender.getName());
							try {
								yaml.save(ticketFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						//Bien, todo se ha procesado con éxito
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", String.valueOf(amount));
						Translation.getAndSendMessage(sender, StringsID.LOTTERY_BUY_TICKETS_SUCCESS, replaceMap, true);
						
						//Cobrar
						AllBanks.getEconomy().withdrawPlayer((Player) sender, total_cost.doubleValue());
						
						//Stats
						YamlConfiguration yaml = YamlConfiguration.loadConfiguration(LotteryRunnable.lotteryFile);
						int currenttickets = yaml.getInt("total-tickets", 0);
						yaml.set("total-tickets", currenttickets + amount);
						try {
							yaml.save(LotteryRunnable.lotteryFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						return true;
					}else{
						//No válido /ab lottery buyticket [INT]
						return false;
					}
				}else if(args[1].equalsIgnoreCase("info")){
					
					if(!Util.hasPermission(sender, "allbanks.commands.lottery.info")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
						return true;
					}
					
					//Mostrar el estado actual de la lotería (cuantos participantes hay, cuando se buscará el próximo ganador).
					if(!LotteryRunnable.lotteryFile.exists()){
						//No hay un último tiempo de ejecución...
						Translation.getAndSendMessage(sender, StringsID.LOTTERY_CHECK_ERROR_NO_LOTTERY_FILE, true);
						return true;
					}
					
					//Bien tratar de leer
					YamlConfiguration lotteryYaml = YamlConfiguration.loadConfiguration(LotteryRunnable.lotteryFile);
					long lastExecution = lotteryYaml.getLong("last-execution", new Date().getTime());
					long time = new Date().getTime();
					long difExecution = time - lastExecution;
					
					int runEvery = LotteryRunnable.runEvery;
					long remainingTime = runEvery - (difExecution / 1000);
					
					int totalParticipants = 0;
					BigDecimal totalMoney = BigDecimal.ZERO;
					double ticket_cost = AllBanks.getInstance().getConfig().getDouble("lottery.ticket-cost", 50);
					
					if(LotteryRunnable.dirTickets.exists()){
						totalParticipants = LotteryRunnable.dirTickets.listFiles().length;
					}
					
					if(totalParticipants > 0){
						totalMoney = new BigDecimal(ticket_cost).multiply(new BigDecimal(totalParticipants));
					}
					
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", String.valueOf(lotteryYaml.getInt("total-winners", 0)));
					replaceMap.put("%2%", String.valueOf(lotteryYaml.getInt("total-tickets", 0)));
					replaceMap.put("%3%", lotteryYaml.getString("last-winner", "----"));
					replaceMap.put("%5%", String.valueOf(totalParticipants));
					replaceMap.put("%6%", AllBanks.getEconomy().format(totalMoney.doubleValue()));
					replaceMap.put("%7%", AllBanks.getEconomy().format(ticket_cost));
					if(LotteryRunnable.enable)
						replaceMap.put("%4%", String.valueOf(remainingTime));
					else
						replaceMap.put("%4%", ChatColor.RED + "----");
					Translation.getAndSendMessage(sender, StringsID.LOTTER_CHECK_INFO, replaceMap, true);
					
					return true;
				}else if(args[1].equalsIgnoreCase("force")){
					//Forzar para buscar un ganador
					if(!Util.hasPermission(sender, "allbanks.commands.lottery.force")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
						return true;
					}
					
					Bukkit.getServer().broadcastMessage(Translation.get(StringsID.LOTTERY_BROADCAST_FORCE_BY_ADMIN, true)[0]);
					
					try {
						LotteryRunnable.stopRunnable();
					} catch (Exception e) {
						e.printStackTrace();
					}

					LotteryRunnable.instance.run();
					
					try {
						LotteryRunnable.startRunnable();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					AllBanksLogger.warning("[Lottery] Lottery forced by " + sender.getName());
					return true;
				}else if(args[1].equalsIgnoreCase("enable")){
					if(!Util.hasPermission(sender, "allbanks.commands.lottery.enable")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
						return true;
					}
					
					AllBanksLogger.warning("[Lottery] Enabling lottery... " + sender.getName());
					
					//Habilitar lotería
					AllBanks.getInstance().getConfig().set("lottery.enable", true);
					AllBanks.getInstance().saveConfig();
					AllBanks.getInstance().reloadConfig();
					
					LotteryRunnable.enable = true;
					
					try {
						LotteryRunnable.startRunnable();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					Translation.getAndSendMessage(sender, StringsID.LOTTERY_COMMAND_ENABLE, true);
					
					AllBanksLogger.warning("[Lottery] Lottery enabled by " + sender.getName());
					return true;
				}else if(args[1].equalsIgnoreCase("disable")){
					if(!Util.hasPermission(sender, "allbanks.commands.lottery.disable")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
						return true;
					}

					AllBanksLogger.warning("[Lottery] Disabling lottery... " + sender.getName());
					
					//Deshabilitar lotería
					AllBanks.getInstance().getConfig().set("lottery.enable", false);
					AllBanks.getInstance().saveConfig();
					AllBanks.getInstance().reloadConfig();
					
					LotteryRunnable.enable = false;
					
					try {
						LotteryRunnable.stopRunnable();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					Translation.getAndSendMessage(sender, StringsID.LOTTERY_COMMAND_DISABLE, true);
					
					AllBanksLogger.warning("[Lottery] Lottery disabled by " + sender.getName());
					return true;
				}
			}
		}else if(mainAction.equalsIgnoreCase("iteminfo")){
			if(!(sender instanceof Player)) {
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return true;
			}
			
			if(!Util.hasPermission(sender, "allbanks.commands.iteminfo")){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				if(senderIsPlayer) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
				return true;
			}//pushs
			
			Player p = (Player) sender;
			ItemStack itemHand = p.getInventory().getItemInMainHand();
			
			String name = ItemNameUtil.getItemName(itemHand);

			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.NAME, false)[0] + ": " + ChatColor.GRAY + name);
			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.DURABILITY, false)[0] + ": " + ChatColor.GRAY + itemHand.getDurability());
			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.SHOP_FOR_SHOP_LINE, false)[0] + ": " + ChatColor.GRAY + name + ":" + itemHand.getDurability());
			
			return true;
		}
		
		return false;
	}
	
}
