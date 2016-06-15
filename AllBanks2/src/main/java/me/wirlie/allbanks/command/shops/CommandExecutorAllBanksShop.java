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
package me.wirlie.allbanks.command.shops;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.PermissionsConstants;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.command.AllBanksExecutor;
import me.wirlie.allbanks.command.Command;
import me.wirlie.allbanks.command.Command.CommandExecuteResult;
import me.wirlie.allbanks.utils.AllBanksLogger;

/**
 * Clase encargada de procesar todo lo relacionado con los comandos.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class CommandExecutorAllBanksShop extends AllBanksExecutor implements CommandExecutor{
	
	/**
	 * Ejecutor del comando /allbanks
	 */
	public CommandExecutorAllBanksShop(){
		//estadísticas
		registerCommand(new CommandShopTransactions(PermissionsConstants.COMMAND_ABS_TRANSACTION), "transactions");
		registerCommand(new CommandShopTransactions(PermissionsConstants.COMMAND_ABS_TRANSACTION), "transactions", "byHour");
		registerCommand(new CommandShopTransactions(PermissionsConstants.COMMAND_ABS_TRANSACTION), "transactions", "byDay");
		registerCommand(new CommandShopTransactions(PermissionsConstants.COMMAND_ABS_TRANSACTION), "transactions", "byMonth");
		registerCommand(new CommandShopTransactions(PermissionsConstants.COMMAND_ABS_TRANSACTION), "transactions", "byYear");
		registerCommand(new CommandShopTransactions(PermissionsConstants.COMMAND_ABS_TRANSACTION), "transactions", "byTransactionPlayer", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<Player>");
		registerCommand(new CommandShopTransactions(PermissionsConstants.COMMAND_ABS_TRANSACTION), "transactions", "byShopOwner", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<Player>");
		registerCommand(new CommandShopTransactions(PermissionsConstants.COMMAND_ABS_TRANSACTION), "transactions", "byTransactionItem");
	}
	
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if(checkCommandMatch(args)){
			
			String argsString = "";
			for(String s : args){
				argsString += s + " ";
			}
			
			CommandExecuteResult result = executeCommand(sender, label, args);
			
			switch(result){
			case DEFAULT:
				AllBanksLogger.info(sender.getName() + " has tried to execute: (/" + label + " " + argsString + "), but, a DEFAULT result returned (probably this command does not exists) [Result:DEFAULT]");
				break;
			case INSUFICIENT_ARGUMENTS:
				AllBanksLogger.info(sender.getName() + " has tried to execute: (/" + label + " " + argsString + "), but AllBanks2 cannot process the command with the specified arguments (more arguments are required) [Result:INSUFICIENT_ARGUMENTS] .");
				break;
			case INVALID_ARGUMENTS:
				AllBanksLogger.info(sender.getName() + " has tried to execute: (/" + label + " " + argsString + "), but a AllBanks2 cannot process the command for an unknow reason (probably, specified arguments are not valid) [Result: INVALID_ARGUMENTS]");
				break;
			case NO_PERMISSIONS:
				AllBanksLogger.warning(sender.getName() + " has tried to execute: (/" + label + " " + argsString + "), but he cannot execute this command because he does not have permissions for this. [Result: NO_PERMISSIONS]");
				break;
			case OTHER:
				AllBanksLogger.info(sender.getName() + " has executed a command: (/" + label + " " + argsString + "), but AllBanks2 cannot know if the result is SUCCESS. [Result: OTHER]");
				break;
			case SUCCESS:
				AllBanksLogger.info(sender.getName() + " has executed a command: (/" + label + " " + argsString + "), the result is SUCCESS. [Result: SUCCESS]");
				break;
			case EXCEPTION:
				AllBanksLogger.info(sender.getName() + " has executed a command: (/" + label + " " + argsString + "), but a EXCEPTION has ocurred!!! [Result: SUCCESS]");
				break;
			}
		}else{
			List<Command> possibleCommands = possibleMatches(args);
			
			if(possibleCommands.size() == 0){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_NO_ARGUMENT_MATCH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abs help"), true);
				return true;
			}
			
			String argsCommand = "";
			
			for(String s : args){
				argsCommand += s + " ";
			}

			Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMANDS_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abs " + argsCommand), true);

			int showed = 0;
			
			for(Command cmd : possibleCommands){
				sender.sendMessage(ChatColor.GRAY + "/abs " + cmd.getSyntax());
				
				showed++;

				if(showed > 15){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMAND_HIGH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abs help"), true);
					break;
				}
			}
		}
		
		return true;
	}
	
}
