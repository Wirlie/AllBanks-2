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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.wirlie.allbanks.land.AllBanksWorld;

/**
 * @author Wirlie
 *
 */
public class CommandTabCompleterABLand implements TabCompleter{

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1){
			return Arrays.asList(
					"admin",
					"plot",
					"spawn"
					);
		}
		
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("admin")){
				return Arrays.asList(
						"?",
						"help",
						"world"
						);
			}else if(args[0].equalsIgnoreCase("plot")){
				return Arrays.asList(
						"?",
						"help",
						"claim",
						"unclaim",
						"add",
						"remove",
						"deny",
						"undeny",
						"set",
						"teleport", //TODO AGREGAR ESTE COMANDO
						"setHomeSpawn", //TODO AGREGAR ESTE COMANDO
						"setShopSpawn", //TODO AGREGAR ESTE COMANDO
						"info"
						);
			}else if(args[0].equalsIgnoreCase("spawn")){
				return new ArrayList<String>(AllBanksWorld.registeredMaps.keySet());
			}
		}
		
		if(args.length == 3){
			if(args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("world")){
				return new ArrayList<String>(AllBanksWorld.registeredMaps.keySet());
			}else if(args[0].equalsIgnoreCase("plot")){
				if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("deny") || args[1].equalsIgnoreCase("undeny")){
					return null;
				}else if(args[1].equalsIgnoreCase("set")){
					return Arrays.asList(
							"fire-spread",
							"explosions",
							"mobs",
							"pvp",
							"lava-flow",
							"water-flow",
							"use-door",
							"use-anvil",
							"use-workbench",
							"use-fence-door",
							"use-enchantment-table",
							"use-lever",
							"use-button",
							"drop-item",
							"msg-greeting",
							"msg-farewell",
							"allow-entry"
							);
				}else if(args[1].equalsIgnoreCase("teleport")){
					return Arrays.asList(
							"<PlotID>",
							"<PlotOwner>",
							"<PlotAlias>"
							);
				}
			}
		}
		
		if(args.length == 4){
			if(args[0].equalsIgnoreCase("plot")){
				if(args[1].equalsIgnoreCase("set")){
					if(args[2].equalsIgnoreCase("fire-spread") ||
							args[2].equalsIgnoreCase("explosions") ||
							args[2].equalsIgnoreCase("mobs") ||
							args[2].equalsIgnoreCase("pvp") ||
							args[2].equalsIgnoreCase("lava-flow") ||
							args[2].equalsIgnoreCase("water-flow") ||
							args[2].equalsIgnoreCase("use-door") ||
							args[2].equalsIgnoreCase("use-anvil") ||
							args[2].equalsIgnoreCase("use-workbench") ||
							args[2].equalsIgnoreCase("use-fence-door") ||
							args[2].equalsIgnoreCase("use-enchantment-table") ||
							args[2].equalsIgnoreCase("use-lever") ||
							args[2].equalsIgnoreCase("use-bottom") ||
							args[2].equalsIgnoreCase("drop-item") ||
							args[2].equalsIgnoreCase("allow-entry"))
					{
						return Arrays.asList("true", "false");
					}else if(args[2].equalsIgnoreCase("msg-greeting") || args[2].equalsIgnoreCase("msg-farewell")){
						return Arrays.asList("<Message>");
					}
				}
			}
			
			if(args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("world")){
				return Arrays.asList("generate", "unload", "remove", "info", "set");
			}
		}
		
		if(args.length == 5){
			if(args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("world") && args[3].equalsIgnoreCase("set")){
				return Arrays.asList("allow-nether-portal",
						"allow-tnt-explosion",
						"allow-wither",
						"animal-spawn",
						"claim-cost",
						"creeper-explosion",
						"mob-spawn",
						"wither-explosion",
						"plots-per-user");
			}
		}
		
		if(args.length == 6){
			if(args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("world") && args[3].equalsIgnoreCase("set")){
				if(args[4].equalsIgnoreCase("allow-nether-portal") ||
						args[4].equalsIgnoreCase("allow-tnt-explosion") ||
						args[4].equalsIgnoreCase("allow-wither") ||
						args[4].equalsIgnoreCase("animal-spawn") ||
						args[4].equalsIgnoreCase("creeper-explosion") ||
						args[4].equalsIgnoreCase("mob-spawn") ||
						args[4].equalsIgnoreCase("wither-explosion"))
				{
					return Arrays.asList("true", "false");
				}else if(args[4].equalsIgnoreCase("claim-cost")){
					return Arrays.asList("0.00");
				}else if(args[4].equalsIgnoreCase("plots-per-user")){
					return Arrays.asList("1");
				}
			}
		}
		
		return Arrays.asList("");
	}

}
