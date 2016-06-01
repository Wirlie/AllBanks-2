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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.command.Command;
import me.wirlie.allbanks.utils.WorldLoadAsync_1_9_R1;
import me.wirlie.allbanks.utils.WorldLoadAsync_1_9_4_R2;

/**
 * @author Wirlie
 *
 */
public class CommandWorld extends Command {
	
	public CommandWorld(String permissionNode){
		super(permissionNode);
	}

	@Override
	public CommandExecuteResult execute(CommandSender sender, String label, String[] args){
		
		boolean displayHelp = false;
		
		if(args.length <= 1){
			//	/abland plot
			displayHelp = true;
		}else if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
			displayHelp = true;
		}
		
		if(displayHelp){
			int page = 1;
			int maxPage = 1;
			Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + page, "%2%>>>" + maxPage), true);
			
			sender.sendMessage(ChatColor.GRAY + "/abl spawn " + ChatColor.GOLD + "<world> " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_SPAWN_DESC, false)[0]);
			
			return CommandExecuteResult.SUCCESS;
		}
		
		if(args[0].equalsIgnoreCase("spawn")){
			
			if(!this.hasPermission(sender)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			if(!AllBanks.getInstance().getConfig().getBoolean("modules.allbanksland.enable")){
				Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksLand"), true);
				return CommandExecuteResult.OTHER;
			}
			
			if(sender instanceof Player){
				if(args.length >= 2){
					String worldName = args[1].toLowerCase();
					
					if(AllBanksWorld.worldIsAllBanksWorld(worldName)){
						try {
							//R1 Support
				    		Class.forName("org.bukkit.craftbukkit.v1_9_R1.CraftServer");
				    		if(WorldLoadAsync_1_9_R1.isBusy() && WorldLoadAsync_1_9_R1.lastWorldGenerated.equalsIgnoreCase(worldName)){
								//El mundo se está generando
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_IN_PROGRESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
							}else{
								World w = Bukkit.getWorld(worldName);
								
								if(w != null){
									Bukkit.getPlayer(sender.getName()).teleport(w.getSpawnLocation());
									Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_SUCCESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
								}else{
									Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_BUKKIT_NULL, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
								}
							}
				    	}catch (ClassNotFoundException e) {
				    		try {
								//R1 Support
					    		Class.forName("org.bukkit.craftbukkit.v1_9_R2.CraftServer");
					    		if(WorldLoadAsync_1_9_4_R2.isBusy() && WorldLoadAsync_1_9_4_R2.lastWorldGenerated.equalsIgnoreCase(worldName)){
									//El mundo se está generando
									Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_IN_PROGRESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
								}else{
									World w = Bukkit.getWorld(worldName);
									
									if(w != null){
										Bukkit.getPlayer(sender.getName()).teleport(w.getSpawnLocation());
										Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_SUCCESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
									}else{
										Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_BUKKIT_NULL, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
									}
								}
					    	}catch (ClassNotFoundException e2) {
					    		e2.printStackTrace();
					    		return CommandExecuteResult.EXCEPTION;
					    	}
				    	}
					}else{
						//Mundo no cargado en AllBanks
						Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_NOT_IS_A_WORLD_OF_ALLBANKS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
					}
				}else{
					//Argumentos inválidos
					sender.sendMessage("INVALID-ARGS");
				}
			}else{
				//No es un jugador
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
			}
			
		}
		
		return CommandExecuteResult.SUCCESS;
	}
}
