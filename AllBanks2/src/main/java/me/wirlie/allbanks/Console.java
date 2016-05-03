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

import java.util.HashMap;

import org.bukkit.Bukkit;

/**
 * Esta clase se encarga de enviar mensajes a la consola junto con el prefix de AllBanks.
 * @author Wirlie
 *
 */
public class Console {
	
	/** Prefix de AllBanks sin formato **/
	static String simplePrefix = "[" + AllBanks.getInstance().getDescription().getName() + "] ";
	
	/**
	 * Enviar un mensaje a la consola.
	 * @param strID ID del string ({@code StringsID})
	 * @param replaceMap Mapa para reemplazar valores, este mapa debe tener el siguiente formato:<br>
	 * {@code HashMap<String, String> = HashMap<Buscar, Reemplazar>}
	 */
	public static void sendMessage(StringsID strID, HashMap<String, String> replaceMap){
		
		for(String s : Translation.get(strID, replaceMap, false)){
			Bukkit.getConsoleSender().sendMessage(simplePrefix + s);
		}
	}
	
	/**
	 * Enviar un mensaje a la consola sin parámetros de reemplazo.
	 * @param strID ID del string ({@code StringsID})
	 */
	public static void sendMessage(StringsID strID){
		sendMessage(strID, new HashMap<String, String>());
	}
	
	/**
	 * Enviar un mensaje a la consola, desde una cadena de texto simple.
	 * @param str Cadena de texto.
	 */
	public static void sendMessage(String str){
		Bukkit.getConsoleSender().sendMessage(simplePrefix + str);
	}
}
