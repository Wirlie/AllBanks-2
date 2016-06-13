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
package me.wirlie.allbanks.commands.allbanksland;

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
 * @author Wirlie
 *
 */
public class CommandExecutorABLand extends AllBanksExecutor implements CommandExecutor {
	
	static CommandExecutorABLand instance;

	/**
	 * Ejecutor del comando /allbanksland
	 */
	public CommandExecutorABLand(){
		instance = this;
		
		//Ayuda
		registerCommand(new CommandHelp(null), "?");
		registerCommand(new CommandHelp(null), "help");
		
		//Relacionado a comandos Admin
		registerCommand(new CommandAdmin(null), "admin", "?");
		registerCommand(new CommandAdmin(null), "admin", "help");
		registerCommand(new CommandAdmin(PermissionsConstants.COMMAND_LAND_ADMIN_WORLD_GENERATE_PERMISSION), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "generate");
		registerCommand(new CommandAdmin(PermissionsConstants.COMMAND_LAND_ADMIN_WORLD_UNLOAD_PERMISSION), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "unload");
		registerCommand(new CommandAdmin(PermissionsConstants.COMMAND_LAND_ADMIN_WORLD_REMOVE_PERMISSION), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "remove");
		registerCommand(new CommandAdmin(PermissionsConstants.COMMAND_LAND_ADMIN_WORLD_INFO_PERMISSION), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "info");
		registerCommand(new CommandAdmin(PermissionsConstants.COMMAND_LAND_ADMIN_WORLD_SET_PERMISSION), "admin", "world", "RegEx->([A-Za-z0-9_.?!#-()]){1,}:<worldName>", "set", "RegEx->(.){1,}:<Flag>", "RegEx->(.){1,}:<Value>");
		
		//Spawns de mundos
		registerCommand(new CommandWorld("allbanks.land.commands.spawn"), "spawn", "RegEx->(.){1,}:<worldName>");
		
		//Relacionado al plot y comandos de usuario
		registerCommand(new CommandPlot(null), "plot", "?");
		registerCommand(new CommandPlot(null), "plot", "help");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_CLAIM_PERMISSION), "plot", "claim");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_DISPOSE_PERMISSION), "plot", "dispose");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_ADD_PERMISSION), "plot", "add", "RegEx->(.){1,}:<Player>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_ADD_PERMISSION), "plot", "remove", "RegEx->(.){1,}:<Player>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_DENY_PERMISSION), "plot", "deny", "RegEx->(.){1,}:<Player>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_DENY_PERMISSION), "plot", "undeny", "RegEx->(.){1,}:<Player>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_SET_FLAGS_PERMISSION), "plot", "set", "RegEx->(.){1,}:<Config>", "RegEx->(.){1,}:<Value>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_SETHOMESPAWN_PERMISSION), "plot", "setHomeSpawn");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_SETSHOPSPAWN_PERMISSION), "plot", "setShopSpawn");
		registerCommand(new CommandPlot(null), "plot", "info");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_HOME_PERMISSION), "plot", "home");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_HOME_PERMISSION), "plot", "home", "RegEx->([0-9]){1,}:<#>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_TELEPORT_PERMISSION), "plot", "teleport", "RegEx->(.){1,}:<PlotOwner>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_AUTO_PERMISSION), "plot", "auto");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_AUTO_PERMISSION), "plot", "autoclaim");
		registerCommand(new CommandPlot(null), "plot", "list");
		registerCommand(new CommandPlot(null), "plot", "list", "RegEx->([0-9]){1,}:<page>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_SETBIOME_PERMISSION), "plot", "setbiome", "RegEx->([A-Za-z0-9_]){1,}:<biome>");
		registerCommand(new CommandPlot(null), "plot", "biomelist");
		registerCommand(new CommandPlot(null), "plot", "biomelist", "RegEx->([0-9]){1,}:<page>");
		registerCommand(new CommandPlot(PermissionsConstants.COMMAND_PLOT_CLEAR_PERMISSION), "plot", "clear");
		registerCommand(new CommandPlot(null), "plot", "clear", "confirm", "RegEx->(.*):<token>");
		registerCommand(new CommandPlot(null), "plot", "clear", "cancel", "RegEx->(.*):<token>");
	}
	
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		
		if(checkCommandMatch(args)){
			CommandExecuteResult result = executeCommand(sender, label, args);
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
			List<Command> possibleCommands = possibleMatches(args);
			
			if(possibleCommands.size() == 0){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_NO_ARGUMENT_MATCH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abl help"), true);
				return true;
			}
			
			String argsCommand = "";
			
			for(String s : args){
				argsCommand += s + " ";
			}
			
			if(label.equalsIgnoreCase("plot")){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMANDS_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/plot " + argsCommand.replace("plot ", "")), true);
			}else{
				Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMANDS_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abl " + argsCommand), true);
			}
			
			int showed = 0;
			
			for(Command cmd : possibleCommands){
				
				if(label.equalsIgnoreCase("plot")){
					sender.sendMessage(ChatColor.GRAY + "/plot " + cmd.getSyntax().replace("plot ", ""));
				}else{
					sender.sendMessage(ChatColor.GRAY + "/abl " + cmd.getSyntax());
				}
				
				showed++;

				if(showed > 15){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_POSSIBLE_COMMAND_HIGH, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/abl help"), true);
					break;
				}
			}
		}
		
		return true;
	}

	/**
	 * Obtener la instancia de la clase.
	 * @return instancia de esta clase.
	 */
	public static CommandExecutorABLand getInstance() {
		return instance;
	}

}
