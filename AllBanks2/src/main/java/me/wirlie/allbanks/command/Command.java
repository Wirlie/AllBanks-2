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
	
	/**
	 * Constructor principal de un comando.
	 * @param permission Permiso necesario para la ejecución.
	 */
	public Command(String permission){
		permissionNode = permission;
	}
	
	/**
	 * Resultados de la ejecución del comando.
	 * @author Wirlie
	 */
	public enum CommandExecuteResult{
		/**
		 * Cuando el comando se ha ejecutado exitosamente.
		 */
		SUCCESS,
		/**
		 * Cuando el jugador o el que ha enviado el comando no tiene permisos.
		 */
		NO_PERMISSIONS,
		/**
		 * Argumentos insuficientes.
		 */
		INSUFICIENT_ARGUMENTS,
		/**
		 * Cuando por accidente no se ha especificado un resultado. Este resultado es devuelto por defecto.
		 */
		DEFAULT,
		/**
		 * Cuando el comando no es procesado por otra causa.
		 */
		OTHER,
		/**
		 * Argumentos inválidos.
		 */
		INVALID_ARGUMENTS,
		/**
		 * Cuando una excepción ocurre y el comando no puede ser procesado.
		 */
		EXCEPTION
	}
	
	/**
	 * Establecer los argumentos del comando.
	 * @param args Argumentos
	 */
	public void setArguments(String... args) {
		for(String s : args){
			commandArgs.add(s);
		}
	}

	/**
	 * Comprobar si los argumentos a prueba coinciden con los argumentos previamente definidos por
	 * el método {@link #setArguments(String...)}.
	 * @param testArgs Argumentos a poner a prueba.
	 * @return {@code true} si los argumentos coinciden, {@code false} si no.
	 */
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

	/**
	 * Acción cuando el comando es ejecutado. Esto tiene que ser reemplazado por una anotación
	 * {@code @Override} al momento de usar la clase Command.
	 * @param sender El ejecutor de este comando.
	 * @param label La etiqueta de este comando.
	 * @param args Argumentos del comando.
	 * @return {@link CommandExecuteResult}, resultado de la ejecución del comando.
	 */
	public CommandExecuteResult execute(CommandSender sender, String label, String[] args) {
		//Esto tiene que ser reemplazado con un override
		return CommandExecuteResult.DEFAULT;
	}

	/**
	 * Usado para obtener las posibles coincidencias con este comando.
	 * @param testArgs Argumentos a probar.
	 * @return {@code true} si este comando coincide, {@code false} si no.
	 */
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

	/**
	 * Obtener la sintáxis de este comando.
	 * @return Sintáxis del comando.
	 */
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
	
	/**
	 * Comprobar si el ejecutor tiene permiso o no.
	 * @param sender
	 * @return {@code true} si tiene permiso o {@code false} sino.
	 */
	public boolean hasPermission(CommandSender sender){
		
		if(permissionNode == null || permissionNode.equalsIgnoreCase("")) return true;
		
		return Util.hasPermission(sender, permissionNode);
	}
	
	/**
	 * Comprobar si el jugador tiene permiso o no.
	 * @param sender
	 * @return {@code true} si tiene permiso o {@code false} sino.
	 */
	public boolean hasPermission(Player sender){
		
		if(permissionNode == null || permissionNode.equalsIgnoreCase("")) return true;
		
		return Util.hasPermission(sender, permissionNode);
	}

}
