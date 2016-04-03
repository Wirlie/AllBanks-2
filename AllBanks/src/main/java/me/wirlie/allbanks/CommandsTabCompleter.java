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
package me.wirlie.allbanks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class CommandsTabCompleter implements TabCompleter {

	private static List<String> soundListName = new ArrayList<String>();
	private static List<String> soundListNamePossibleArgs = new ArrayList<String>();
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1){
			return Arrays.asList(
					"database",
					"testsound",
					"lottery"
					);
		}
		
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("database")){
				return Arrays.asList(
						"try-update",
						"try-query"
						);
			}else if(args[0].equalsIgnoreCase("toprank")){
				return Arrays.asList(
						"bankxp",
						"bankmoney",
						"banktime",
						"lottery"
						);
			}else if(args[0].equalsIgnoreCase("lottery")){
				return Arrays.asList(
						"buyticket",
						"info",
						"force",
						"enable",
						"disable"
						);
			}else if(args[0].equalsIgnoreCase("testsound")){
				
				if(soundListName.isEmpty()) {
					for(Sound s : Sound.values()) {
						soundListName.add(s.toString());
					}
				} else {
					if(!args[1].equalsIgnoreCase(" ")) {
						
						boolean match = false;
						for(String s : soundListName) {
							if(args[1].equalsIgnoreCase(s)) {
								match = true;
							}
						}
						
						if(match) {
							return soundListNamePossibleArgs;
						}
						
						for(String s : soundListName) {
							if(s.startsWith(args[1])) {
								soundListNamePossibleArgs.add(s);
							}
						}
						
						return soundListNamePossibleArgs;
					}
				}
				
				
				return soundListName;
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
			}else if(args[0].equalsIgnoreCase("testsound")){
				return Arrays.asList("0");
			}
		}
		
		return Arrays.asList("");
	}

}
