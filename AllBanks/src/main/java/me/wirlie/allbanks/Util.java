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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Util {
	public static class StrLocUtil{
		public static String convertLocationToString(Location loc, boolean blockLocation){
			String returnStr;
			if(blockLocation)
				returnStr = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
			else
				returnStr = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
			
			return returnStr;
		}
		
		public static Location convertStringToLocation(String loc){
			
			String[] args = loc.split(":");
			return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
		}
		
		public static boolean stringIsValidLocation(String loc){
			
			String[] args = loc.split(":");
			if(args.length != 4) return false;
			
			if(Bukkit.getWorld(args[0]) == null) return false;
			
			try{
				Double.parseDouble(args[1]);
				Double.parseDouble(args[2]);
				Double.parseDouble(args[3]);
			}catch(NumberFormatException e){
				return false;
			}
			
			return true;
		}
	}
	
	public static class ChatFormatUtil {
		
	    /**
	     * Reemplaza el formato de chat &0 &1 &2 &3 etc. con su respectivo color
	     * de la clase {@code ChatColor}.
	     * @param txt - Cadena de texto
	     * @return Devuelve la cadena de texto con los formatos de {@code ChatColor} aplicados.
	     */
		public static String replaceChatFormat(String txt){
			txt = txt.replace("&0", ChatColor.BLACK+"");
			txt = txt.replace("&1", ChatColor.DARK_BLUE+"");
			txt = txt.replace("&2", ChatColor.DARK_GREEN+"");
			txt = txt.replace("&3", ChatColor.DARK_AQUA+"");
			txt = txt.replace("&4", ChatColor.DARK_RED+"");
			txt = txt.replace("&5", ChatColor.DARK_PURPLE+"");
			txt = txt.replace("&6", ChatColor.GOLD+"");
			txt = txt.replace("&7", ChatColor.GRAY+"");
			txt = txt.replace("&8", ChatColor.DARK_GRAY+"");
			txt = txt.replace("&9", ChatColor.BLUE+"");
			txt = txt.replace("&a", ChatColor.GREEN+"");
			txt = txt.replace("&b", ChatColor.AQUA+"");
			txt = txt.replace("&c", ChatColor.RED+"");
			txt = txt.replace("&d", ChatColor.LIGHT_PURPLE+"");
			txt = txt.replace("&e", ChatColor.YELLOW+"");
			txt = txt.replace("&f", ChatColor.WHITE+"");
			txt = txt.replace("&l", ChatColor.BOLD+"");
			txt = txt.replace("&r", ChatColor.RESET+"");
			return txt;
		}
		
		/**
		 * Remueve cualquier formato de {@code ChatColor} de una cadena de texto.
		 * @param txt - Cadena de texto
	     * @return Devuelve la cadena de texto limpia.
		 */
		public static String removeChatFormat(String txt){
			txt = txt.replace(ChatColor.BLACK+"","");
			txt = txt.replace(ChatColor.DARK_BLUE+"","");
			txt = txt.replace(ChatColor.DARK_GREEN+"","");
			txt = txt.replace(ChatColor.DARK_AQUA+"","");
			txt = txt.replace(ChatColor.DARK_RED+"","");
			txt = txt.replace(ChatColor.DARK_PURPLE+"","");
			txt = txt.replace(ChatColor.GOLD+"","");
			txt = txt.replace(ChatColor.GRAY+"","");
			txt = txt.replace(ChatColor.DARK_GRAY+"","");
			txt = txt.replace(ChatColor.BLUE+"","");
			txt = txt.replace(ChatColor.GREEN+"","");
			txt = txt.replace(ChatColor.AQUA+"","");
			txt = txt.replace(ChatColor.RED+"","");
			txt = txt.replace(ChatColor.LIGHT_PURPLE+"","");
			txt = txt.replace(ChatColor.YELLOW+"","");
			txt = txt.replace(ChatColor.WHITE+"","");
			txt = txt.replace(ChatColor.BOLD+"","");
			txt = txt.replace(ChatColor.RESET+"","");
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
			return txt;
		}
	}
}
