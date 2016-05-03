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
package me.wirlie.allbanks.utils.command;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.runnables.LotteryRunnable;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 *
 */
public class CommandLottery extends Command {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(DataBaseUtil.databaseIsLocked()){
			DataBaseUtil.sendDatabaseLockedMessage(sender);
			return true;
		}
		
		if(args.length >= 2){
			if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "info" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_INFO_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "buyticket [amount]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_BUYTICKET_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "[enable|disable]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_ENABLE_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "force" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_FORCE_DESC.toString(false));
				
			}else if(args[1].equalsIgnoreCase("buyticket")){
				if(args.length == 3){
					
					if(!(sender instanceof Player)){
						Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
						return true;
					}
					
					if(!Util.hasPermission(sender, "allbanks.commands.lottery.buyticket")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
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
					Translation.getAndSendMessage(sender, StringsID.COMMAND_SYNTAX_ERROR, true);
					Translation.getAndSendMessage(sender, 
							StringsID.COMMAND_SUGGEST_HELP, 
							Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab lottery ?"),
							true);
					return true;
				}
			}else if(args[1].equalsIgnoreCase("info")){
				
				if(!Util.hasPermission(sender, "allbanks.commands.lottery.info")){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
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
					if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
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
					if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
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
					if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
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
		}else{
			//Sin argumentos
			Translation.getAndSendMessage(sender, 
					StringsID.COMMAND_SUGGEST_HELP, 
					Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab lottery ?"),
					true);
			return true;
		}
		
		return true;
	}
}
