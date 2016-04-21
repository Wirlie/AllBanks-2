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
package me.wirlie.allbanks.utils.command.land;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.utils.command.Command;

/**
 * @author Wirlie
 *
 */
public class CommandExecutorABLand implements CommandExecutor {

	public CommandExecutorABLand(){
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "?");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "help");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "claim");
	}
	
public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		
		if(CommandManagerABLand.checkCommandMatch(args)){
			CommandManagerABLand.executeCommand(sender, args);
		}else{
			List<Command> possibleCommands = CommandManagerABLand.possibleMatches(args);
			
			if(possibleCommands.size() == 0){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_NO_ARGUMENT_MATCH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abland help"), true);
				return true;
			}

			Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMANDS_HEADER, true);
			
			int showed = 0;
			
			for(Command cmd : possibleCommands){
				
				sender.sendMessage(ChatColor.GRAY + "/abland " + cmd.getSyntax());
				
				showed++;

				if(showed > 15){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMAND_HIGH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abland help"), true);
					break;
				}
			}
		}
		
		return true;
	}

}
