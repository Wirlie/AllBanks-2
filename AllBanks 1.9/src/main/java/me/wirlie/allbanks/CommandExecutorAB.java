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

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.utils.command.Command;
import me.wirlie.allbanks.utils.command.CommandDataBase;
import me.wirlie.allbanks.utils.command.CommandHelp;
import me.wirlie.allbanks.utils.command.CommandItemInfo;
import me.wirlie.allbanks.utils.command.CommandLottery;
import me.wirlie.allbanks.utils.command.CommandReload;
import me.wirlie.allbanks.utils.command.CommandTopRank;

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
		CommandManagerABLand.registerCommand(new CommandItemInfo(), "iteminfo");
		//Lotería
		CommandManagerABLand.registerCommand(new CommandLottery(), "lottery", "?");
		CommandManagerABLand.registerCommand(new CommandLottery(), "lottery", "help");
		CommandManagerABLand.registerCommand(new CommandLottery(), "lottery", "info");
		CommandManagerABLand.registerCommand(new CommandLottery(), "lottery", "force");
		CommandManagerABLand.registerCommand(new CommandLottery(), "lottery", "enable");
		CommandManagerABLand.registerCommand(new CommandLottery(), "lottery", "disable");
		CommandManagerABLand.registerCommand(new CommandLottery(), "lottery", "buyticket", "RegEx->([0-9]){1,}:<amount>");
		//DataBase
		CommandManagerABLand.registerCommand(new CommandDataBase(), "database");
		CommandManagerABLand.registerCommand(new CommandDataBase(), "database", "try-query", "RegEx->(.){1,}:<SQL>");
		CommandManagerABLand.registerCommand(new CommandDataBase(), "database", "try-update", "RegEx->(.){1,}:<SQL>");
		//TopRank
		CommandManagerABLand.registerCommand(new CommandTopRank(), "toprank");
		CommandManagerABLand.registerCommand(new CommandTopRank(), "toprank", "?");
		CommandManagerABLand.registerCommand(new CommandTopRank(), "toprank", "help");
		CommandManagerABLand.registerCommand(new CommandTopRank(), "toprank", "bankmoney");
		CommandManagerABLand.registerCommand(new CommandTopRank(), "toprank", "bankxp");
		//Recargar
		CommandManagerABLand.registerCommand(new CommandReload(), "reload");
		//Ayuda
		CommandManagerABLand.registerCommand(new CommandHelp(), "help");
		CommandManagerABLand.registerCommand(new CommandHelp(), "?");
		CommandManagerABLand.registerCommand(new CommandHelp(), "help", "RegEx->([0-9]){1,}:<page>");
		CommandManagerABLand.registerCommand(new CommandHelp(), "?", "RegEx->([0-9]){1,}:<page>");
	}
	
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		
		if(CommandManagerABLand.checkCommandMatch(args)){
			CommandManagerABLand.executeCommand(sender, args);
		}else{
			List<Command> possibleCommands = CommandManagerAB.possibleMatches(args);
			
			if(possibleCommands.size() == 0){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_NO_ARGUMENT_MATCH, true);
			}
			
			for(Command cmd : possibleCommands){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMANDS_HEADER, true);
				sender.sendMessage(ChatColor.GRAY + "/" + cmd.getSyntax());
			}
		}
		
		return true;
	}
}
