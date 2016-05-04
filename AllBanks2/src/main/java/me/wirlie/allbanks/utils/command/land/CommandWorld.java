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

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksWorld;
import me.wirlie.allbanks.utils.WorldLoadAsync;
import me.wirlie.allbanks.utils.command.Command;

/**
 * @author Wirlie
 *
 */
public class CommandWorld extends Command {

	@Override
	public boolean execute(CommandSender sender, String[] args){
		
		boolean displayHelp = false;
		
		if(args.length <= 1){
			//	/abland plot
			displayHelp = true;
		}else if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
			displayHelp = true;
		}
		
		if(displayHelp){
			
			return true;
		}
		
		if(args[0].equalsIgnoreCase("spawn")){
			
			if(sender instanceof Player){
				if(args.length >= 2){
					String worldName = args[1].toLowerCase();
					
					if(AllBanksWorld.worldIsAllBanksWorld(worldName)){
						if(WorldLoadAsync.isBusy() && WorldLoadAsync.lastWorldGenerated.equalsIgnoreCase(worldName)){
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
		
		return true;
	}
}