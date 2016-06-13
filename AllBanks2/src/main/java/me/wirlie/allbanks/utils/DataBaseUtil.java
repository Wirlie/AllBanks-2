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
package me.wirlie.allbanks.utils;

import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.Console;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;

/**
 * Funciones de utilidad con la base de datos.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class DataBaseUtil{
	
	static boolean  databaseLocked = false;
	
	/**
	 * Comprobar si una excepción es referente al bloqueo de la base de datos.
	 * @param e Excepción SQL
	 * @return {@code true} si la excepción refiere a un bloqueo.
	 */
	public static boolean checkDatabaseIsLocked(SQLException e){
		
		e.printStackTrace();

		AllBanksLogger.severe("SQLException:");
		
		AllBanksLogger.severe(e.getLocalizedMessage());
		
		for(StackTraceElement ste : e.getStackTrace()){
			AllBanksLogger.severe("    " + ste.toString());
		}
		
		if(e.getMessage().contains("database is locked")){
			
			AllBanksLogger.severe("Database is locked!! Please restart your server for unlock the database...");
			AllBanksLogger.severe("AllBanks will still work (to avoid vandalism actions with the signs), however, it is possible that many functions of AllBanks not work.");
			
			sendServerMessage();
			databaseLocked = true;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checar si la base de datos está bloqueada.
	 * @return {@code true} si la base de datos está bloqueada.
	 */
	public static boolean databaseIsLocked(){
		if(databaseLocked) sendServerMessage();
		return databaseLocked;
	}
	
	/**
	 * Mostrar mensaje al servidor del bloqueo de la base de datos.
	 */
	public static void sendServerMessage(){
		Console.sendMessage("&7[&fDataBase&7] &cDatabase is locked!! Please restart your server for unlock the database...");
		Console.sendMessage("&7[&fDataBase&7] &cAllBanks will still work (to avoid vandalism actions with the signs), however, it is possible that many functions of AllBanks not work.");
	}
	
	/**
	 * Mostrar mensaje del bloqueo de la base de datos a un jugador.
	 * @param p Jugador
	 * @return {@code true} si la base de datos está bloqueada.
	 */
	public static boolean databaseIsLocked(Player p){
		if(databaseLocked) sendDatabaseLockedMessage(p);
		return databaseLocked;
	}
	
	/**
	 * Mostrar mensaje del bloqueo de la base de datos al ejecutor del comando.
	 * @param s Ejecutor del comando
	 * @return {@code true} si la base de datos está bloqueada.
	 */
	public static boolean databaseIsLocked(CommandSender s){
		if(databaseLocked) sendDatabaseLockedMessage(s);
		return databaseLocked;
	}
	
	/**
	 * Enviar el mensaje de bloqueo a un jugador
	 * @param p Jugador
	 */
	public static void sendDatabaseLockedMessage(Player p){
		Translation.getAndSendMessage(p, StringsID.DATABASE_IS_LOCKED_PLEASE_RESTART_SERVER, true);
	}
	
	/**
	 * Enviar el mensaje de bloqueo a un ejecutor
	 * @param s Ejecutor del comando.
	 */
	public static void sendDatabaseLockedMessage(CommandSender s){
		Translation.getAndSendMessage(s, StringsID.DATABASE_IS_LOCKED_PLEASE_RESTART_SERVER, (s instanceof Player));
	}
	
}
