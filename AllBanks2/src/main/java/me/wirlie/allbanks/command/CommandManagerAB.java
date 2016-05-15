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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.command.Command.CommandExecuteResult;

/**
 * @author Wirlie
 *
 */
public class CommandManagerAB {
	
	private CommandManagerAB(){
		//privatizar constructor
	}
	
	private static List<Command> registeredCommands = new ArrayList<Command>();
	
	public static void registerCommand(Command command, String... arguments){
		
		List<String> argumentsRepresentation = new ArrayList<String>();
		
		for(String s : arguments){
			argumentsRepresentation.add(s);
		}
		
		command.setArguments(arguments);
		registeredCommands.add(command);
	}
	
	public static boolean checkCommandMatch(String[] args) {
		for(Command command : registeredCommands){
			if(command.matchArguments(args)){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @param args
	 */
	public static CommandExecuteResult executeCommand(CommandSender sender, String label, String[] args) {
		for(Command command : registeredCommands){
			if(command.matchArguments(args)){
				return command.execute(sender, label, args);
			}
		}
		
		return CommandExecuteResult.DEFAULT;
	}
	
	public static List<Command> possibleMatches(String[] args){
		List<Command> returnPossibleMatches = new ArrayList<Command>();
		
		for(Command command : registeredCommands){
			if(command.possibleMatch(args)){
				returnPossibleMatches.add(command);
			}
		}
		
		return returnPossibleMatches;
		
	}
	
}
