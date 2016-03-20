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

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			if(args.length >= 1){
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
						try{
							Statement stm = AllBanks.getDBC().createStatement();
							
							ResultSet res = stm.executeQuery(query);
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
							
							res.close();
							stm.close();
						}catch(SQLException e){
							
							Translation.getAndSendMessage(sender, StringsID.COMMANDS_DATABASE_INVALID_QUERY, (sender instanceof Player));
							sender.sendMessage(ChatColor.RED + e.getMessage());
							
							AllBanks.getInstance().getLogger().info("Dont worry, the database can not process your Query:");
							AllBanks.getInstance().getLogger().info("Syntax: " + query);
							
							e.printStackTrace();
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
						try{
							Statement stm = AllBanks.getDBC().createStatement();
							stm.executeUpdate(query);
							
							Translation.getAndSendMessage(sender, StringsID.COMMANDS_DATABASE_QUERY_SUCCESS, (sender instanceof Player));
							
							stm.close();
						}catch(SQLException e){
							
							Translation.getAndSendMessage(sender, StringsID.COMMANDS_DATABASE_INVALID_QUERY, (sender instanceof Player));
							sender.sendMessage(ChatColor.RED + e.getMessage());
							
							AllBanks.getInstance().getLogger().info("Dont worry, SQLite can not process your Query:");
							AllBanks.getInstance().getLogger().info("Syntax: " + query);
							
							e.printStackTrace();
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
		}
		
		return false;
	}
	
}
