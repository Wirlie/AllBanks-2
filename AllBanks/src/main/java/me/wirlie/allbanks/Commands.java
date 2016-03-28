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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.wirlie.allbanks.Util.DatabaseUtil;
import me.wirlie.allbanks.data.BankAccount;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Commands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 0){
			//TODO Agregar un mensaje que muestre información referente al plugin.
			return true;
		}
		
		String mainAction = args[0];
		
		if(mainAction.equalsIgnoreCase("database")){
			if(args.length >= 2){
				if(args[1].equalsIgnoreCase("try-query")){
					
					if(sender instanceof Player || sender instanceof BlockCommandSender){
						Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_CONSOLE, true);
						return true;
					}
					
					if(!sender.hasPermission("allbanks.commands.database.executequery")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
						return true;
					}
					
					if(args.length >= 2){
						String query = "";
						
						for(int i = 2; i < args.length; i++){
							query += args[i] + " ";
						}
						
						//Intentar ejecutar
						if(DatabaseUtil.databaseIsLocked()){
							DatabaseUtil.sendDatabaseLockedMessage(sender);
							return true;
						}
						
						Statement stm = null;
						ResultSet res = null;
						
						try{
							stm = AllBanks.getDBC().createStatement();
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
							DatabaseUtil.checkDatabaseIsLocked(e);
							if(!DatabaseUtil.databaseIsLocked()){
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
					
					if(!sender.hasPermission("allbanks.commands.database.executequery")){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
						return true;
					}
					
					if(args.length >= 2){
						String query = "";
			
						for(int i = 2; i < args.length; i++){
							query += args[i] + " ";
						}
						
						//Intentar ejecutar
						if(DatabaseUtil.databaseIsLocked()){
							DatabaseUtil.sendDatabaseLockedMessage(sender);
							return true;
						}
						
						Statement stm = null;
						
						try{
							stm = AllBanks.getDBC().createStatement();
							stm.executeUpdate(query);
							
							Translation.getAndSendMessage(sender, StringsID.COMMANDS_DATABASE_QUERY_SUCCESS, (sender instanceof Player));
							
							//Vaciar caché de BankAccount
							BankAccount.Cache.clearCache();
							
							stm.close();
						}catch(SQLException e){
							DatabaseUtil.checkDatabaseIsLocked(e);
							if(!DatabaseUtil.databaseIsLocked()){
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
			if(DatabaseUtil.databaseIsLocked()){
				DatabaseUtil.sendDatabaseLockedMessage(sender);
				return true;
			}
			
			if(args.length >= 2){
				if(args[1].equalsIgnoreCase("buyticket")){
					if(args.length == 3){
						
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
						Statement stm = null;
						ResultSet res = null;
						int currentTicketsBought = 0;
						
						try{
							stm = AllBanks.getDBC().createStatement();
							res = stm.executeQuery("SELECT owner FROM lottery_tickets WHERE owner = '" + sender.getName() + "'");
							
							while(res.next()){
								currentTicketsBought++;
							}
						}catch(SQLException e){
							DatabaseUtil.checkDatabaseIsLocked(e);
							Translation.getAndSendMessage(sender, StringsID.SQL_EXCEPTION_PROBLEM, true);
							return true;
						}finally{
							try {
								stm.close();
								res.close();
							} catch (SQLException e) {
								e.printStackTrace();
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
						
						if(canBuy - amount <= 0){
							//Ya no puede comprar más tickets
							HashMap<String, String> replaceMap = new HashMap<String, String>();
							replaceMap.put("%1%", String.valueOf(amount));
							replaceMap.put("%2%", String.valueOf(canBuy));
							Translation.getAndSendMessage(sender, StringsID.LOTTERY_CAN_NOT_BUY_MORE_TICKETS, replaceMap, true);
							return true;
						}
						
						//Comprar
						try{
							stm = AllBanks.getDBC().createStatement();
							for(int i = 0; i < canBuy; i++){
								stm.executeUpdate("INSERT INTO lottery_tickets (owner) VALUES ('" + sender.getName() + "')");
							}
						}catch(SQLException e){
							DatabaseUtil.checkDatabaseIsLocked(e);
							Translation.getAndSendMessage(sender, StringsID.SQL_EXCEPTION_PROBLEM, true);
							return true;
						}finally{
							try {
								stm.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						
						//Bien, todo se ha procesado con éxito
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", String.valueOf(amount));
						Translation.getAndSendMessage(sender, StringsID.LOTTERY_BUY_TICKETS_SUCCESS, replaceMap, true);
						
						return true;
					}else{
						//No válido /ab lottery buyticket [INT]
						return false;
					}
				}else if(args[1].equalsIgnoreCase("check")){
					//Mostrar el estado actual de la lotería (cuantos participantes hay, cuando se buscará el próximo ganador).
					
				}else if(args[1].equalsIgnoreCase("force")){
					//Forzar para buscar un ganador
					
				}else if(args[1].equalsIgnoreCase("enable")){
					//Habilitar lotería
					
				}else if(args[1].equalsIgnoreCase("disable")){
					//Deshabilitar lotería
					
				}
			}
		}
		
		return false;
	}
	
}
