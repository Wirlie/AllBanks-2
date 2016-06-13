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

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.PermissionsConstants;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.command.Command.CommandExecuteResult;
import me.wirlie.allbanks.utils.AllBanksLogger;

/**
 * Clase encargada de procesar todo lo relacionado con los comandos.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class CommandExecutorAB extends AllBanksExecutor implements CommandExecutor{
	
	/**
	 * Ejecutor del comando /allbanks
	 */
	public CommandExecutorAB(){
		//ItemInfo
		registerCommand(new CommandItemInfo(PermissionsConstants.COMMAND_AB_ITEMINFO_PERMISSION), "iteminfo");
		registerCommand(new CommandItemInfo(null), "showPreview");
		//Lotería
		registerCommand(new CommandLottery(null), "lottery", "?");
		registerCommand(new CommandLottery(null), "lottery", "help");
		registerCommand(new CommandLottery(PermissionsConstants.COMMAND_AB_LOTTERY_INFO_PERMISSION), "lottery", "info");
		registerCommand(new CommandLottery(PermissionsConstants.COMMAND_AB_LOTTERY_FORCE_PERMISSION), "lottery", "force");
		registerCommand(new CommandLottery(PermissionsConstants.COMMAND_AB_LOTTERY_ENABLE_PERMISSION), "lottery", "enable");
		registerCommand(new CommandLottery(PermissionsConstants.COMMAND_AB_LOTTERY_DISABLE_PERMISSION), "lottery", "disable");
		registerCommand(new CommandLottery(PermissionsConstants.COMMAND_AB_LOTTERY_BUYTICKET_PERMISSION), "lottery", "buyticket", "RegEx->([0-9]){1,}:<amount>");
		//DataBase
		registerCommand(new CommandDataBase(null), "database", "?");
		registerCommand(new CommandDataBase(null), "database", "help");
		registerCommand(new CommandDataBase(PermissionsConstants.COMMAND_AB_DATABASE_EXECUTEQUERY_PERMISSION), "database", "try-query", "RegEx->(.){1,}:<SQL>");
		registerCommand(new CommandDataBase(PermissionsConstants.COMMAND_AB_DATABASE_EXECUTEQUERY_PERMISSION), "database", "try-update", "RegEx->(.){1,}:<SQL>");
		//TopRank
		registerCommand(new CommandTopRank(null), "toprank", "?");
		registerCommand(new CommandTopRank(null), "toprank", "help");
		registerCommand(new CommandTopRank(PermissionsConstants.COMMAND_AB_TOPRANK_BANKMONEY_PERMISSION), "toprank", "bankmoney");
		registerCommand(new CommandTopRank(PermissionsConstants.COMMAND_AB_TOPRANK_BANKXP_PERMISSION), "toprank", "bankxp");
		//Ayuda
		registerCommand(new CommandHelp(null), "help");
		registerCommand(new CommandHelp(null), "help", "RegEx->([0-9]){1,}:<page>");
		registerCommand(new CommandHelp(null), "?");
		registerCommand(new CommandHelp(null), "?", "RegEx->([0-9]){1,}:<page>");
		//actualizador
		registerCommand(new CommandUpdate(PermissionsConstants.COMMAND_UPDATER_CHECK_UPDATES_PERMISSION), "updater", "check-updates");
		registerCommand(new CommandUpdate(PermissionsConstants.COMMAND_UPDATER_DOWNLOAD_UPDATE_PERMISSION), "updater", "download-update");
		registerCommand(new CommandUpdate(PermissionsConstants.COMMAND_UPDATER_CURRENT_VERSION_PERMISSION), "updater", "current-version");
		registerCommand(new CommandUpdate(PermissionsConstants.COMMAND_UPDATER_FORCE_UPDATE_PERMISSION), "updater", "force-download");
		registerCommand(new CommandUpdate(null), "updater", "?");
		registerCommand(new CommandUpdate(null), "updater", "help");
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
				Translation.getAndSendMessage(sender, StringsID.COMMAND_NO_ARGUMENT_MATCH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab help"), true);
				return true;
			}
			
			String argsCommand = "";
			
			for(String s : args){
				argsCommand += s + " ";
			}

			Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMANDS_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab " + argsCommand), true);

			int showed = 0;
			
			for(Command cmd : possibleCommands){
				sender.sendMessage(ChatColor.GRAY + "/ab " + cmd.getSyntax());
				
				showed++;

				if(showed > 15){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMAND_HIGH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab help"), true);
					break;
				}
			}
		}
		
		return true;
	}
	
}
