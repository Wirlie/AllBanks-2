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
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * Auto completador usando la tecla Shift mientras escribes un argumento.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class CommandTabCompleterAB implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1){
			List<String> results = Arrays.asList(
					"database",
					"lottery",
					"toprank",
					"help",
					"iteminfo"
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
			if(args[0].equalsIgnoreCase("database")){
				List<String> results = Arrays.asList(
						"try-update",
						"try-query",
						"help"
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
			}else if(args[0].equalsIgnoreCase("toprank")){
				List<String> results = Arrays.asList(
						"bankmoney",
						"bankxp",
						"help"
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
			}else if(args[0].equalsIgnoreCase("help")){
				List<String> results = Arrays.asList(
						"1",
						"2"
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
			}else if(args[0].equalsIgnoreCase("lottery")){
				List<String> results = Arrays.asList(
						"buyticket",
						"info",
						"force",
						"enable",
						"disable",
						"help"
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
			}
		}
		
		if(args.length == 3){
			if(args[0].equalsIgnoreCase("database") && args[1].equalsIgnoreCase("try-update") ||
					args[0].equalsIgnoreCase("database") && args[1].equalsIgnoreCase("try-query")){
				return Arrays.asList(
						"{SQL_SENTENCE}"
						);
			}else if(args[0].equalsIgnoreCase("lottery") && args[1].equalsIgnoreCase("buyticket")) {
				return Arrays.asList("0");
			}else if(args[0].equalsIgnoreCase("toprank") && args[1].equalsIgnoreCase("bankmoney")){
				return Arrays.asList("1");
			}
		}
		
		return Arrays.asList("");
	}

}
