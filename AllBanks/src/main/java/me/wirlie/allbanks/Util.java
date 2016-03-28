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

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Util {
	
	public static class DatabaseUtil{
		
		static boolean  databaseLocked = false;
		
		public static boolean checkDatabaseIsLocked(SQLException e){
			
			e.printStackTrace();

			AllBanksLogger.getLogger().severe("SQLException:");
			
			AllBanksLogger.getLogger().severe(e.getLocalizedMessage());
			
			for(StackTraceElement ste : e.getStackTrace()){
				AllBanksLogger.getLogger().severe("    " + ste.toString());
			}
			
			if(e.getMessage().contains("database is locked")){
				
				AllBanksLogger.getLogger().severe("Database is locked!! Please restart your server for unlock the database...");
				AllBanksLogger.getLogger().severe("AllBanks will still work (to avoid vandalism actions with the signs), however, it is possible that many functions of AllBanks not work.");
				
				sendServerMessage();
				databaseLocked = true;
				return true;
			}
			
			return false;
		}
		
		public static boolean databaseIsLocked(){
			if(databaseLocked) sendServerMessage();
			return databaseLocked;
		}
		
		public static void sendServerMessage(){
			AllBanks.getInstance().getLogger().severe("Database is locked!! Please restart your server for unlock the database...");
			AllBanks.getInstance().getLogger().severe("AllBanks will still work (to avoid vandalism actions with the signs), however, it is possible that many functions of AllBanks not work.");
		}
		
		public static boolean databaseIsLocked(Player p){
			if(databaseLocked) sendDatabaseLockedMessage(p);
			return databaseLocked;
		}
		
		public static boolean databaseIsLocked(CommandSender s){
			if(databaseLocked) sendDatabaseLockedMessage(s);
			return databaseLocked;
		}
		
		public static void sendDatabaseLockedMessage(Player p){
			Translation.getAndSendMessage(p, StringsID.DATABASE_IS_LOCKED_PLEASE_RESTART_SERVER, true);
		}
		
		public static void sendDatabaseLockedMessage(CommandSender s){
			Translation.getAndSendMessage(s, StringsID.DATABASE_IS_LOCKED_PLEASE_RESTART_SERVER, (s instanceof Player));
		}
		
	}
	
	public static class XPConversionUtil{
	    
		/**
		 * Establecer experiencia total al jugador.
		 * @param player
		 * @param totalExp
		 */
	    public static void setTotalExpToPlayer(Player player, int totalExp) {
	        if (totalExp < 0) {
	            throw new IllegalArgumentException("Experience can not have a negative value!");
	        }
	        
	        //Reset exp and level...
	        player.setExp(0);
	        player.setLevel(0);
	        player.setTotalExperience(0);

	        //Recalculate experience
	        int amount = totalExp;
	        while (amount > 0) {
	            final int expToLevel = getExpAtLevel(player);
	            amount -= expToLevel;
	            if (amount >= 0) {
	                player.giveExp(expToLevel);
	            } else {
	                amount += expToLevel;
	                player.giveExp(amount);
	                amount = 0;
	            }
	        }
	    }

	    /**
	     * Obtiene la cantidad de experiencia que se necesita para el sig nivel.
	     * @param player
	     * @return
	     */
	    private static int getExpAtLevel(final Player player) {
	        return getExpAtLevel(player.getLevel());
	    }

	    /**
	     * Obtiene la cantidad de experiencia que se necesita para el sig nivel.
	     * @param level
	     * @return
	     */
	    public static int getExpAtLevel(final int level) {
	        if (level <= 15) {
	            return (2 * level) + 7;
	        }
	        if ((level >= 16) && (level <= 30)) {
	            return (5 * level) - 38;
	        }
	        return (9 * level) - 158;
	    }
	    
	    public static int convertExpToLevel(final int xp){
	        int currentLevel = 0;
	        int remXP = xp;
	        
	        while(remXP >= getExpAtLevel(currentLevel)){
	        	remXP -= getExpAtLevel(currentLevel);
	        	currentLevel++;
	        }
	        
	        return currentLevel;
	    }
	    
	    /**
	     * Obtiene la cantidad de experiencia total para el nivel especificado.
	     * @param level
	     * @return
	     */
	    public static int getExpToLevel(final int level) {
	        int currentLevel = 0;
	        int exp = 0;

	        while (currentLevel < level) {
	            exp += getExpAtLevel(currentLevel);
	            currentLevel++;
	        }
	        if (exp < 0) {
	            exp = Integer.MAX_VALUE;
	        }
	        return exp;
	    }

	    /**
	     * Obtiene la cantidad total de experiencia quer tiene el jugador.
	     * @param player
	     * @return
	     */
	    public static int getTotalExperience(final Player player) {
	        int exp = (int) Math.round(getExpAtLevel(player) * player.getExp());
	        int currentLevel = player.getLevel();

	        while (currentLevel > 0) {
	            currentLevel--;
	            exp += getExpAtLevel(currentLevel);
	        }
	        if (exp < 0) {
	            exp = Integer.MAX_VALUE;
	        }
	        return exp;
	    }

	    /**
	     * Obtener la experiencia necesaria para el siguiente nivel del jugador.
	     * @param player
	     * @return
	     */
	    public static int getExpUntilNextLevel(final Player player) {
	        int exp = (int) Math.round(getExpAtLevel(player) * player.getExp());
	        int nextLevel = player.getLevel();
	        return getExpAtLevel(nextLevel) - exp;
	    }
	}

	public static class ConfigUtil{
		/**
		 * Bueno, esto realmente ayuda con la configuración banks.bank-loan.collect-interest-every
		 * ya que se encuentra en formato 0 days, 0 hours, 0 minutes, 0 seconds y es necesario
		 * convertir todo eso a un mensaje que se pueda adaptar a las traducciones.
		 * @param strTimeValue
		 * @return
		 */
		public static int convertTimeValueToSeconds(String strTimeValue){
			
			if(strTimeValue == null) return -1;
			
			//pasar "0 days, 0 hours" a "0 days,0 hours"
			strTimeValue = strTimeValue.replace(", ", ",");
			
			//Separar valores con la coma.
			String[] split = strTimeValue.split(",");
			
			int totalSeconds = 0;
			
			//comprobar si es válido
			for(String s : split){
				//Usar REGEX
				Pattern r = Pattern.compile("^([0-9]{1,2})+ +(days|day|hours|hour|minutes|minute|seconds|second)$");
				Matcher m = r.matcher(s);
				
				if(!m.matches()){
					//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
					AllBanks.getInstance().getLogger().warning("Invalid timeValue: " + s);
					AllBanks.getInstance().getLogger().warning("Valid values: days | day | hours | hour | minutes | minute | seconds | second");
					try{
						throw new IllegalArgumentException("Invalid timeValue: " + s);
					}catch(IllegalArgumentException e){
						e.printStackTrace();
						return -1;
					}
				}else{
					
					if(m.groupCount() != 2){
						AllBanks.getInstance().getLogger().warning("The value " + s + " does not have a valid syntax for a timeValue.");
						AllBanks.getInstance().getLogger().warning("Valid syntax: '{INT} {StringValue}', example: '1 day', '1 minute'");
						//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
						return -1;
					}
					
					int intValue = 0;
					String timeValue = m.group(2);
					
					try{
						intValue = Integer.parseInt(m.group(1));
					}catch(NumberFormatException e){
						AllBanks.getInstance().getLogger().warning("The value " + m.group(1) + " is not a valid number.");
						AllBanks.getInstance().getLogger().warning("Matched string: " + s);
						AllBanks.getInstance().getLogger().warning("Full string: " + strTimeValue);
						e.printStackTrace();
						//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
						return -1;
					}
					
					if(timeValue.equalsIgnoreCase("days")){
						totalSeconds += intValue * 24 * 60 * 60;
						continue;
					}else if(timeValue.equalsIgnoreCase("day")){
						totalSeconds += intValue * 24 * 60 * 60;
						continue;
					}else if(timeValue.equalsIgnoreCase("hours")){
						totalSeconds += intValue * 60 * 60;
						continue;
					}else if(timeValue.equalsIgnoreCase("hour")){
						totalSeconds += intValue * 60 * 60;
						continue;
					}else if(timeValue.equalsIgnoreCase("minutes")){
						totalSeconds += intValue * 60;
						continue;
					}else if(timeValue.equalsIgnoreCase("minute")){
						totalSeconds += intValue * 60;
						continue;
					}else if(timeValue.equalsIgnoreCase("seconds")){
						totalSeconds += intValue;
						continue;
					}else if(timeValue.equalsIgnoreCase("second")){
						totalSeconds += intValue;
						continue;
						
					}
				}
			}
			
			return totalSeconds;
		}
	}
	
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
