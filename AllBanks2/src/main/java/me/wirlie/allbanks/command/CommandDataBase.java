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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.AllBanks.StorageType;
import me.wirlie.allbanks.banks.bankdata.BankAccount;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 *
 */
public class CommandDataBase extends Command {
	
	public CommandDataBase(String permissionNode){
		super(permissionNode);
	}
	
	@Override
	public CommandExecuteResult execute(CommandSender sender, String label, String[] args) {
		if(args.length >= 2){
			if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
				//comando de ayuda: /ab database ?
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab database " + ChatColor.AQUA + "try-query" + ChatColor.YELLOW + "<SQL>" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_DATABASE_QUERY.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab database " + ChatColor.AQUA + "try-update" + ChatColor.YELLOW + "<SQL>" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_DATABASE_UPDATE.toString(false));
				return CommandExecuteResult.SUCCESS;
			}else if(args[1].equalsIgnoreCase("try-query")){
				
				if(sender instanceof Player || sender instanceof BlockCommandSender){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_CONSOLE, true);
					return CommandExecuteResult.OTHER;
				}
				
				if(!this.hasPermission(sender)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
					if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_AVAILABLE_ON_DATABASE, true);
					return CommandExecuteResult.OTHER;
				}
				
				if(args.length >= 2){
					String query = "";
					
					for(int i = 2; i < args.length; i++){
						query += args[i] + " ";
					}
					
					//Intentar ejecutar
					if(DataBaseUtil.databaseIsLocked()){
						DataBaseUtil.sendDatabaseLockedMessage(sender);
						return CommandExecuteResult.OTHER;
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
					Translation.getAndSendMessage(sender, StringsID.COMMAND_SYNTAX_ERROR, true);
					Translation.getAndSendMessage(sender, 
							StringsID.COMMAND_SUGGEST_HELP, 
							Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab database ?"),
							true);
					return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
				}
			}else if(args[1].equalsIgnoreCase("try-update")){

				if(sender instanceof Player || sender instanceof BlockCommandSender){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_CONSOLE, true);
					return CommandExecuteResult.OTHER;
				}	
				
				if(!this.hasPermission(sender)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
					if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_AVAILABLE_ON_DATABASE, true);
					return CommandExecuteResult.OTHER;
				}
				
				if(args.length >= 2){
					String query = "";
		
					for(int i = 2; i < args.length; i++){
						query += args[i] + " ";
					}
					
					//Intentar ejecutar
					if(DataBaseUtil.databaseIsLocked()){
						DataBaseUtil.sendDatabaseLockedMessage(sender);
						return CommandExecuteResult.OTHER;
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
					Translation.getAndSendMessage(sender, StringsID.COMMAND_SYNTAX_ERROR, true);
					Translation.getAndSendMessage(sender, 
							StringsID.COMMAND_SUGGEST_HELP, 
							Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab database ?"),
							true);
					return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
				}
				
				return CommandExecuteResult.INVALID_ARGUMENTS;
			}else{
				//No cumple con NINGUN argumento valido para /ab database
				Translation.getAndSendMessage(sender, StringsID.COMMAND_SYNTAX_ERROR, true);
				Translation.getAndSendMessage(sender, 
						StringsID.COMMAND_SUGGEST_HELP, 
						Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab database ?"),
						true);
				return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
			}
		}else{
			//No cumple con los requisitos: /ab database <arg>
			Translation.getAndSendMessage(sender, 
					StringsID.COMMAND_SUGGEST_HELP, 
					Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab database ?"),
					true);
			return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
		}
		
		return CommandExecuteResult.SUCCESS;
	}
}
