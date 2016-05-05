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
		//FIXME AÑADIR PERMISOS A LOS COMANDOS
		//Relacionado a comandos Admin
		CommandManagerABLand.registerCommand(new CommandAdmin(), "admin", "?");
		CommandManagerABLand.registerCommand(new CommandAdmin(), "admin", "help");
		CommandManagerABLand.registerCommand(new CommandAdmin(), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "generate");
		CommandManagerABLand.registerCommand(new CommandAdmin(), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "unload");
		CommandManagerABLand.registerCommand(new CommandAdmin(), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "remove");
		//Spawns de mundos
		CommandManagerABLand.registerCommand(new CommandWorld(), "spawn", "RegEx->(.){1,}:<worldName>");
		//Relacionado al plot y comandos de usuario
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "?");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "help");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "claim");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "unclaim");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "add", "RegEx->(.){1,}:<Player>");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "remove", "RegEx->(.){1,}:<Player>");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "deny", "RegEx->(.){1,}:<Player>");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "undeny", "RegEx->(.){1,}:<Player>");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "set", "RegEx->(.){1,}:<Config>", "RegEx->(.){1,}:<Value>");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "setHomeSpawn");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "setShopSpawn");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "info");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "home");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "home", "RegEx->([0-9]){1,}:<#>");
		CommandManagerABLand.registerCommand(new CommandPlot(), "plot", "teleport", "RegEx->(.){1,}:<PlotOwner>");
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
				
				sender.sendMessage(ChatColor.GRAY + "/abl " + cmd.getSyntax());
				
				showed++;

				if(showed > 15){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMAND_HIGH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abl help"), true);
					break;
				}
			}
		}
		
		return true;
	}

}
