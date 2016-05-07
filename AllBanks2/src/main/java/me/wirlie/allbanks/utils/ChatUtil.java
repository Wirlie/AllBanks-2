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

import org.bukkit.ChatColor;

/**
 * Funciones de utilidad para el chat.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ChatUtil {
	
    /**
     * Reemplaza el formato de chat &0 &1 &2 &3 etc. con su respectivo color
     * de la clase {@code ChatColor}.
     * @param txt - Cadena de texto
     * @return Devuelve la cadena de texto con los formatos de {@code ChatColor} aplicados.
     */
	public static String replaceChatFormat(String txt){
		txt = txt.replace("&0", String.valueOf(ChatColor.BLACK));
		txt = txt.replace("&1", String.valueOf(ChatColor.DARK_BLUE));
		txt = txt.replace("&2", String.valueOf(ChatColor.DARK_GREEN));
		txt = txt.replace("&3", String.valueOf(ChatColor.DARK_AQUA));
		txt = txt.replace("&4", String.valueOf(ChatColor.DARK_RED));
		txt = txt.replace("&5", String.valueOf(ChatColor.DARK_PURPLE));
		txt = txt.replace("&6", String.valueOf(ChatColor.GOLD));
		txt = txt.replace("&7", String.valueOf(ChatColor.GRAY));
		txt = txt.replace("&8", String.valueOf(ChatColor.DARK_GRAY));
		txt = txt.replace("&9", String.valueOf(ChatColor.BLUE));
		txt = txt.replace("&a", String.valueOf(ChatColor.GREEN));
		txt = txt.replace("&b", String.valueOf(ChatColor.AQUA));
		txt = txt.replace("&c", String.valueOf(ChatColor.RED));
		txt = txt.replace("&d", String.valueOf(ChatColor.LIGHT_PURPLE));
		txt = txt.replace("&e", String.valueOf(ChatColor.YELLOW));
		txt = txt.replace("&f", String.valueOf(ChatColor.WHITE));
		txt = txt.replace("&l", String.valueOf(ChatColor.BOLD));
		txt = txt.replace("&r", String.valueOf(ChatColor.RESET));
		txt = txt.replace("&k", String.valueOf(ChatColor.MAGIC));
		txt = txt.replace("&m", String.valueOf(ChatColor.STRIKETHROUGH));
		txt = txt.replace("&o", String.valueOf(ChatColor.ITALIC));
		txt = txt.replace("&n", String.valueOf(ChatColor.UNDERLINE));
		return txt;
	}
	
	/**
	 * Remueve cualquier formato de {@code ChatColor} de una cadena de texto.
	 * @param txt - Cadena de texto
     * @return Devuelve la cadena de texto limpia.
	 */
	public static String removeChatFormat(String txt){
		txt = txt.replace(String.valueOf(ChatColor.BLACK),"");
		txt = txt.replace(String.valueOf(ChatColor.DARK_BLUE), "");
		txt = txt.replace(String.valueOf(ChatColor.DARK_GREEN), "");
		txt = txt.replace(String.valueOf(ChatColor.DARK_AQUA), "");
		txt = txt.replace(String.valueOf(ChatColor.DARK_RED), "");
		txt = txt.replace(String.valueOf(ChatColor.DARK_PURPLE), "");
		txt = txt.replace(String.valueOf(ChatColor.GOLD), "");
		txt = txt.replace(String.valueOf(ChatColor.GRAY), "");
		txt = txt.replace(String.valueOf(ChatColor.DARK_GRAY), "");
		txt = txt.replace(String.valueOf(ChatColor.BLUE), "");
		txt = txt.replace(String.valueOf(ChatColor.GREEN), "");
		txt = txt.replace(String.valueOf(ChatColor.AQUA), "");
		txt = txt.replace(String.valueOf(ChatColor.RED), "");
		txt = txt.replace(String.valueOf(ChatColor.LIGHT_PURPLE), "");
		txt = txt.replace(String.valueOf(ChatColor.YELLOW), "");
		txt = txt.replace(String.valueOf(ChatColor.WHITE), "");
		txt = txt.replace(String.valueOf(ChatColor.BOLD), "");
		txt = txt.replace(String.valueOf(ChatColor.RESET), "");
		txt = txt.replace(String.valueOf(ChatColor.MAGIC), "");
		txt = txt.replace(String.valueOf(ChatColor.STRIKETHROUGH), "");
		txt = txt.replace(String.valueOf(ChatColor.ITALIC), "");
		txt = txt.replace(String.valueOf(ChatColor.UNDERLINE), "");
		return txt;
	}
	
	/**
	 * Elimina cualquier formato de chat del tipo &0 &1 &2 &3 etc.
	 * @param txt - Cadena de texto
     * @return Devuelve la cadena de texto limpia.
	 */
	public static String supressChatFormat(String txt){
		txt = txt.replace("&0", "");
		txt = txt.replace("&1", "");
		txt = txt.replace("&2", "");
		txt = txt.replace("&3", "");
		txt = txt.replace("&4", "");
		txt = txt.replace("&5", "");
		txt = txt.replace("&6", "");
		txt = txt.replace("&7", "");
		txt = txt.replace("&8", "");
		txt = txt.replace("&9", "");
		txt = txt.replace("&a", "");
		txt = txt.replace("&b", "");
		txt = txt.replace("&c", "");
		txt = txt.replace("&d", "");
		txt = txt.replace("&e", "");
		txt = txt.replace("&f", "");
		txt = txt.replace("&l", "");
		txt = txt.replace("&r", "");
		txt = txt.replace("&k", "");
		txt = txt.replace("&m", "");
		txt = txt.replace("&o", "");
		txt = txt.replace("&n", "");
		return txt;
	}
}