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
package me.wirlie.allbanks.land.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.command.Command;
import me.wirlie.allbanks.command.Command.CommandExecuteResult;
import me.wirlie.allbanks.logger.AllBanksLogger;

/**
 * @author Wirlie
 *
 */
public class CommandExecutorABLand implements CommandExecutor {

	public CommandExecutorABLand(){
		//Relacionado a comandos Admin
		CommandManagerABLand.registerCommand(new CommandAdmin(null), "admin", "?");
		CommandManagerABLand.registerCommand(new CommandAdmin(null), "admin", "help");
		CommandManagerABLand.registerCommand(new CommandAdmin("allbanks.land.commands.admin.world.generate"), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "generate");
		CommandManagerABLand.registerCommand(new CommandAdmin("allbanks.land.commands.admin.world.unload"), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "unload");
		CommandManagerABLand.registerCommand(new CommandAdmin("allbanks.land.commands.admin.world.remove"), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "remove");
		CommandManagerABLand.registerCommand(new CommandAdmin("allbanks.land.commands.admin.world.info"), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "info");
		CommandManagerABLand.registerCommand(new CommandAdmin("allbanks.land.commands.admin.world.set"), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "set", "RegEx->(.){1,}:<Flag>", "RegEx->(.){1,}:<Value>");
		
		//Spawns de mundos
		CommandManagerABLand.registerCommand(new CommandWorld("allbanks.land.commands.spawn"), "spawn", "RegEx->(.){1,}:<worldName>");
		//Relacionado al plot y comandos de usuario
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "?");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "help");
		CommandManagerABLand.registerCommand(new CommandPlot("allbanks.land.commands.plot.claim"), "plot", "claim");
		CommandManagerABLand.registerCommand(new CommandPlot("allbanks.land.commands.plot.dispose"), "plot", "dispose");
		CommandManagerABLand.registerCommand(new CommandPlot("allbanks.land.commands.plot.add"), "plot", "add", "RegEx->(.){1,}:<Player>");
		CommandManagerABLand.registerCommand(new CommandPlot("allbanks.land.commands.plot.add"), "plot", "remove", "RegEx->(.){1,}:<Player>");
		CommandManagerABLand.registerCommand(new CommandPlot("allbanks.land.commands.plot.deny"), "plot", "deny", "RegEx->(.){1,}:<Player>");
		CommandManagerABLand.registerCommand(new CommandPlot("allbanks.land.commands.plot.deny"), "plot", "undeny", "RegEx->(.){1,}:<Player>");
		CommandManagerABLand.registerCommand(new CommandPlot("allbanks.land.commands.plot.set.flags"), "plot", "set", "RegEx->(.){1,}:<Config>", "RegEx->(.){1,}:<Value>");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "setHomeSpawn");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "setShopSpawn");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "info");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "home");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "home", "RegEx->([0-9]){1,}:<#>");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "teleport", "RegEx->(.){1,}:<PlotOwner>");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "auto");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "autoclaim");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "list");
		CommandManagerABLand.registerCommand(new CommandPlot(null), "plot", "list", "RegEx->([0-9]){1,}:<page>");
	}
	
public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		
		if(CommandManagerABLand.checkCommandMatch(args)){
			CommandExecuteResult result = CommandManagerABLand.executeCommand(sender, args);
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
			}
			
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
