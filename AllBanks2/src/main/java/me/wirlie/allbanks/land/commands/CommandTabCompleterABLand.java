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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.land.AllBanksWorld;

/**
 * @author Wirlie
 *
 */
public class CommandTabCompleterABLand implements TabCompleter{

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1){
			List<String> results = Arrays.asList(
					"admin",
					"plot",
					"spawn"
					);
			
			if(args[0].replace(" ", "").equalsIgnoreCase("")){
				return results;
			}
			
			List<String> suggest = new ArrayList<String>();
			for(String s : results){
				if(s.contains(args[0].toLowerCase())){
					suggest.add(s);
				}
			}
			
			if(suggest.isEmpty()){ 
				return results;
			}
			
			return suggest;
		}
		
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("admin")){
				List<String> results = Arrays.asList(
						"?",
						"help",
						"world"
						);
				
				if(args[1].replace(" ", "").equalsIgnoreCase("")){
					return results;
				}
				
				List<String> suggest = new ArrayList<String>();
				for(String s : results){
					if(s.contains(args[1].toLowerCase())){
						suggest.add(s);
					}
				}
				
				if(suggest.isEmpty()){ 
					return results;
				}
				
				return suggest;
				
			}else if(args[0].equalsIgnoreCase("plot")){
				List<String> results = Arrays.asList(
						"?",
						"help",
						"claim",
						"dispose",
						"add",
						"remove",
						"deny",
						"undeny",
						"set",
						"teleport", //TODO AGREGAR ESTE COMANDO
						"setHomeSpawn",
						"setShopSpawn",
						"info",
						"auto",
						"autoclaim"
						);
				
				if(args[1].replace(" ", "").equalsIgnoreCase("")){
					return results;
				}
				
				List<String> suggest = new ArrayList<String>();
				for(String s : results){
					if(s.contains(args[1].toLowerCase())){
						suggest.add(s);
					}
				}
				
				if(suggest.isEmpty()){ 
					return results;
				}
				
				return suggest;
			}else if(args[0].equalsIgnoreCase("spawn")){
				List<String> results = new ArrayList<String>(AllBanksWorld.registeredMaps.keySet());
				
				if(args[1].replace(" ", "").equalsIgnoreCase("")){
					return results;
				}
				
				List<String> suggest = new ArrayList<String>();
				for(String s : results){
					if(s.contains(args[1].toLowerCase())){
						suggest.add(s);
					}
				}
				
				if(suggest.isEmpty()){ 
					return results;
				}
				
				return suggest;
			}
		}
		
		if(args.length == 3){
			if(args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("world")){
				List<String> results = new ArrayList<String>(AllBanksWorld.registeredMaps.keySet());
				
				if(args[2].replace(" ", "").equalsIgnoreCase("")){
					return results;
				}
				
				List<String> suggest = new ArrayList<String>();
				for(String s : results){
					if(s.contains(args[2].toLowerCase())){
						suggest.add(s);
					}
				}
				
				if(suggest.isEmpty()){ 
					return results;
				}
				
				return suggest;
			}else if(args[0].equalsIgnoreCase("plot")){
				if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("deny") || args[1].equalsIgnoreCase("undeny")){
					List<String> results = new ArrayList<String>();
					
					for(Player p : Bukkit.getOnlinePlayers()){
						results.add(p.getName());
					}
					
					if(args[2].replace(" ", "").equalsIgnoreCase("")){
						return results;
					}
					
					List<String> suggest = new ArrayList<String>();
					for(String s : results){
						if(s.contains(args[2].toLowerCase())){
							suggest.add(s);
						}
					}
					
					if(suggest.isEmpty()){ 
						return results;
					}
					
					return suggest;
					
				}else if(args[1].equalsIgnoreCase("set")){
					List<String> results = Arrays.asList(
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
							"allow-entry",
							"allow-plot-teleport"
							);
					
					if(args[2].replace(" ", "").equalsIgnoreCase("")){
						return results;
					}
					
					List<String> suggest = new ArrayList<String>();
					for(String s : results){
						if(s.contains(args[2].toLowerCase())){
							suggest.add(s);
						}
					}
					
					if(suggest.isEmpty()){ 
						return results;
					}
					
					return suggest;
				}else if(args[1].equalsIgnoreCase("teleport")){
					List<String> results = new ArrayList<String>();
					
					for(Player p : Bukkit.getOnlinePlayers()){
						results.add(p.getName());
					}
					
					if(args[2].replace(" ", "").equalsIgnoreCase("")){
						return results;
					}
					
					List<String> suggest = new ArrayList<String>();
					for(String s : results){
						if(s.contains(args[2].toLowerCase())){
							suggest.add(s);
						}
					}
					
					if(suggest.isEmpty()){ 
						return results;
					}
					
					return suggest;
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
				List<String> results = Arrays.asList("generate", "unload", "remove", "info", "set");
				
				if(args[3].replace(" ", "").equalsIgnoreCase("")){
					return results;
				}
				
				List<String> suggest = new ArrayList<String>();
				for(String s : results){
					if(s.contains(args[3].toLowerCase())){
						suggest.add(s);
					}
				}
				
				if(suggest.isEmpty()){ 
					return results;
				}
				
				return suggest;
			}
		}
		
		if(args.length == 5){
			if(args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("world") && args[3].equalsIgnoreCase("set")){
				List<String> results = Arrays.asList("allow-nether-portal",
						"allow-tnt-explosion",
						"allow-wither",
						"animal-spawn",
						"claim-cost",
						"creeper-explosion",
						"mob-spawn",
						"wither-explosion",
						"plots-per-user");
				
				if(args[4].replace(" ", "").equalsIgnoreCase("")){
					return results;
				}
				
				List<String> suggest = new ArrayList<String>();
				for(String s : results){
					if(s.contains(args[4].toLowerCase())){
						suggest.add(s);
					}
				}
				
				if(suggest.isEmpty()){ 
					return results;
				}
				
				return suggest;
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
