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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.utils.Util;

/**
 * @author Wirlie
 *
 */
public class Command {

	private List<String> commandArgs = new ArrayList<String>();
	private String permissionNode = "";
	
	public Command(String permission){
		permissionNode = permission;
	}
	
	public enum CommandExecuteResult{
		SUCCESS,
		NO_PERMISSIONS,
		INSUFICIENT_ARGUMENTS,
		DEFAULT,
		OTHER,
		INVALID_ARGUMENTS
	}
	
	public void setArguments(String... args) {
		for(String s : args){
			commandArgs.add(s);
		}
	}

	public boolean matchArguments(String[] testArgs) {
		if(commandArgs.size() > testArgs.length) return false;
		
		for(int i = 0; i < commandArgs.size(); i ++){

			String commandArgument = commandArgs.get(i);
			String testArgument = testArgs[i];

			if(commandArgument.startsWith("RegEx->")){
				//Regex argumento
				commandArgument = commandArgument.replace("RegEx->", "");
				String regexp = commandArgument.split(":")[0];

				Pattern regexPattern = Pattern.compile(regexp);
				Matcher regexMatcher = regexPattern.matcher(testArgument);

				if(!regexMatcher.matches()){
					return false;
				}
			}else{
				//Argumento normal
				if(!commandArgument.equalsIgnoreCase(testArgument)){
					return false;
				}
			}
		}
		
		return true;
	}

	public CommandExecuteResult execute(CommandSender sender, String[] args) {
		//Esto tiene que ser reemplazado con un override
		return CommandExecuteResult.DEFAULT;
	}

	public boolean possibleMatch(String[] testArgs) {
		
		for(int i = 0; i < commandArgs.size() && i < testArgs.length; i ++){
			String commandArgument = commandArgs.get(i);
			String testArgument = testArgs[i];
			
			if(commandArgument.startsWith("RegEx->")){
				//Regex argumento
				commandArgument = commandArgument.replace("RegEx->", "");
				String regexp = commandArgument.split(":")[0];
				
				Pattern regexPattern = Pattern.compile(regexp);
				Matcher regexMatcher = regexPattern.matcher(testArgument);
				
				if(!regexMatcher.matches()){
					return false;
				}
			}else{
				//Argumento normal
				if(!commandArgument.equalsIgnoreCase(testArgument) && !commandArgument.contains(testArgument)){
					return false;
				}
			}
		}
		
		return true;
	}

	public String getSyntax() {
		String returnSyntax = "";
		
		for(String s : commandArgs){
			if(s.startsWith("RegEx->")){
				returnSyntax += ChatColor.GOLD + s.split(":")[1] + " ";
			}else{
				returnSyntax += ChatColor.GRAY + s + " ";
			}
		}
		
		return returnSyntax;
	}
	
	public boolean hasPermission(CommandSender sender){
		
		if(permissionNode == null || permissionNode.equalsIgnoreCase("")) return true;
		
		return Util.hasPermission(sender, permissionNode);
	}
	
	public boolean hasPermission(Player sender){
		
		if(permissionNode == null || permissionNode.equalsIgnoreCase("")) return true;
		
		return Util.hasPermission(sender, permissionNode);
	}

}
