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

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;

/**
 * Clase encargada de procesar todo lo relacionado con los comandos.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class CommandExecutorAB implements CommandExecutor {
	//TODO Mejorar el sistema de comandos a través de un sistema de registro de comandos y argumentos

	
	public CommandExecutorAB(){
		//ItemInfo
		CommandManagerAB.registerCommand(new CommandItemInfo(), "iteminfo");
		//Lotería
		CommandManagerAB.registerCommand(new CommandLottery(), "lottery");
		CommandManagerAB.registerCommand(new CommandLottery(), "lottery", "?");
		CommandManagerAB.registerCommand(new CommandLottery(), "lottery", "help");
		CommandManagerAB.registerCommand(new CommandLottery(), "lottery", "info");
		CommandManagerAB.registerCommand(new CommandLottery(), "lottery", "force");
		CommandManagerAB.registerCommand(new CommandLottery(), "lottery", "enable");
		CommandManagerAB.registerCommand(new CommandLottery(), "lottery", "disable");
		CommandManagerAB.registerCommand(new CommandLottery(), "lottery", "buyticket", "RegEx->([0-9]){1,}:<amount>");
		//DataBase
		CommandManagerAB.registerCommand(new CommandDataBase(), "database");
		CommandManagerAB.registerCommand(new CommandDataBase(), "database", "?");
		CommandManagerAB.registerCommand(new CommandDataBase(), "database", "help");
		CommandManagerAB.registerCommand(new CommandDataBase(), "database", "try-query", "RegEx->(.){1,}:<SQL>");
		CommandManagerAB.registerCommand(new CommandDataBase(), "database", "try-update", "RegEx->(.){1,}:<SQL>");
		//TopRank
		CommandManagerAB.registerCommand(new CommandTopRank(), "toprank");
		CommandManagerAB.registerCommand(new CommandTopRank(), "toprank", "?");
		CommandManagerAB.registerCommand(new CommandTopRank(), "toprank", "help");
		CommandManagerAB.registerCommand(new CommandTopRank(), "toprank", "bankmoney");
		CommandManagerAB.registerCommand(new CommandTopRank(), "toprank", "bankxp");
		//Recargar
		CommandManagerAB.registerCommand(new CommandReload(), "reload");
		//Ayuda
		CommandManagerAB.registerCommand(new CommandHelp(), "help");
		CommandManagerAB.registerCommand(new CommandHelp(), "help", "RegEx->([0-9]){1,}:<page>");
		CommandManagerAB.registerCommand(new CommandHelp(), "?");
		CommandManagerAB.registerCommand(new CommandHelp(), "?", "RegEx->([0-9]){1,}:<page>");
	}
	
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		
		if(CommandManagerAB.checkCommandMatch(args)){
			CommandManagerAB.executeCommand(sender, args);
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
