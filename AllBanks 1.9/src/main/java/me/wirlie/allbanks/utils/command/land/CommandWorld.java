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

import me.wirlie.allbanks.land.AllBanksWorld;
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
					String worldName = args[1];
					
					if(AllBanksWorld.checkPlotWorld(worldName)){
						World w = Bukkit.getWorld(worldName);
						
						if(w != null){
							Bukkit.getPlayer(sender.getName()).teleport(w.getSpawnLocation());
							sender.sendMessage("WORLD " + w.getName());
						}else{
							//El mundo no pudo ser cargado desde Bukkit
							sender.sendMessage("BUKKIT-ERROR");
						}
					}else{
						//Mundo no cargado en AllBanks
						sender.sendMessage("NO-LOADED-ALLBANKS");
					}
				}else{
					//Argumentos inválidos
					sender.sendMessage("INVALID-ARGS");
				}
			}else{
				//No es un jugador
				sender.sendMessage("SENDER-NO-IS-PLAYER");
			}
			
		}
		
		return true;
	}
}
