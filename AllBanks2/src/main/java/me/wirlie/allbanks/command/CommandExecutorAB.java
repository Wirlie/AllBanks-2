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

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.command.Command.CommandExecuteResult;
import me.wirlie.allbanks.logger.AllBanksLogger;

/**
 * Clase encargada de procesar todo lo relacionado con los comandos.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class CommandExecutorAB implements CommandExecutor {
	
	public CommandExecutorAB(){
		//ItemInfo
		CommandManagerAB.registerCommand(new CommandItemInfo("allbanks.commands.iteminfo"), "iteminfo");
		//Lotería
		CommandManagerAB.registerCommand(new CommandLottery(null), "lottery", "?");
		CommandManagerAB.registerCommand(new CommandLottery(null), "lottery", "help");
		CommandManagerAB.registerCommand(new CommandLottery("allbanks.commands.lottery.info"), "lottery", "info");
		CommandManagerAB.registerCommand(new CommandLottery("allbanks.commands.lottery.force"), "lottery", "force");
		CommandManagerAB.registerCommand(new CommandLottery("allbanks.commands.lottery.enable"), "lottery", "enable");
		CommandManagerAB.registerCommand(new CommandLottery("allbanks.commands.lottery.disable"), "lottery", "disable");
		CommandManagerAB.registerCommand(new CommandLottery("allbanks.commands.lottery.buyticket"), "lottery", "buyticket", "RegEx->([0-9]){1,}:<amount>");
		//DataBase
		CommandManagerAB.registerCommand(new CommandDataBase(null), "database", "?");
		CommandManagerAB.registerCommand(new CommandDataBase(null), "database", "help");
		CommandManagerAB.registerCommand(new CommandDataBase("allbanks.commands.database.executequery"), "database", "try-query", "RegEx->(.){1,}:<SQL>");
		CommandManagerAB.registerCommand(new CommandDataBase("allbanks.commands.database.executequery"), "database", "try-update", "RegEx->(.){1,}:<SQL>");
		//TopRank
		CommandManagerAB.registerCommand(new CommandTopRank(null), "toprank", "?");
		CommandManagerAB.registerCommand(new CommandTopRank(null), "toprank", "help");
		CommandManagerAB.registerCommand(new CommandTopRank("allbanks.commands.toprank.bankmoney"), "toprank", "bankmoney");
		CommandManagerAB.registerCommand(new CommandTopRank("allbanks.commands.toprank.bankxp"), "toprank", "bankxp");
		//Ayuda
		CommandManagerAB.registerCommand(new CommandHelp("allbanks.commands.help"), "help");
		CommandManagerAB.registerCommand(new CommandHelp("allbanks.commands.help"), "help", "RegEx->([0-9]){1,}:<page>");
		CommandManagerAB.registerCommand(new CommandHelp("allbanks.commands.help"), "?");
		CommandManagerAB.registerCommand(new CommandHelp("allbanks.commands.help"), "?", "RegEx->([0-9]){1,}:<page>");
	}
	
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		
		if(CommandManagerAB.checkCommandMatch(args)){
			CommandExecuteResult result = CommandManagerAB.executeCommand(sender, label, args);
			String argsString = "";
			for(String s : args){
				argsString += s + " ";
			}
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
			List<Command> possibleCommands = CommandManagerAB.possibleMatches(args);
			
			if(possibleCommands.size() == 0){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_NO_ARGUMENT_MATCH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab help"), true);
				return true;
			}

			Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMANDS_HEADER, true);

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
