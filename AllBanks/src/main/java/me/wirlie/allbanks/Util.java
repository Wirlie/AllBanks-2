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

import java.io.File;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;

import me.wirlie.allbanks.logger.AllBanksLogger;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Util {
	
	//Directorios
	public static File FlatFile_signFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "SignData");
	public static File FlatFile_bankAccountFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "BankAccount");
	public static File FlatFile_pendingCharges = new File(AllBanks.getInstance().getDataFolder() + File.separator + "PendingCharge");
	
	public static boolean hasPermission(Player p, String str) {
		
		boolean permRegistered = false;
		
		if(Bukkit.getPluginManager().getPermission(str) != null) {
			permRegistered = true;
		}
		
		if(!p.hasPermission(str) && permRegistered) {
			//No tiene permiso desde el registro.
			return false;
		} else if(!p.hasPermission(str)){
			List<String> defaultPermissions = AllBanks.getInstance().getConfig().getStringList("default-permissions");
			if(defaultPermissions.contains(str)) {
				//Es un permiso default
				return true;
			}else {
				//No es un permiso default, no tiene permiso
				return false;
			}
		} else {
			//en este resultado, hasPermission es true
			return true;
		}
	}
	
	public static String capitalizeFirstLetter(String text) {
		String[] split = text.split(" ");
		String finalStr = "";
		
		for(String s : split) {
			finalStr += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
		}
		
		return finalStr.substring(0, finalStr.length() - 1);
	}
	
	public static boolean hasPermission(CommandSender s, String str) {
		
		boolean permRegistered = false;
		
		if(Bukkit.getPluginManager().getPermission(str) != null) {
			permRegistered = true;
		}
		
		if(!s.hasPermission(str) && permRegistered) {
			//No tiene permiso desde el registro.
			return false;
		} else if(!s.hasPermission(str)){
			List<String> defaultPermissions = AllBanks.getInstance().getConfig().getStringList("default-permissions");
			if(defaultPermissions.contains(str)) {
				//Es un permiso default
				return true;
			}else {
				//No es un permiso default, no tiene permiso
				return false;
			}
		} else {
			//en este resultado, hasPermission es true
			return true;
		}
	}
	
	//Reflection Util, Internal method, used as shorthand to grab our method in a nice friendly manner
	public static class ReflectionUtil{
		public static Method getMethod(Class<?> cl, String method) {
			for(Method m : cl.getMethods()) {
				if(m.getName().equals(method)) {
					return m;
				}
			}
			return null;
		}
	}
	
	public static class EffectUtil{
		public static void playFireworkEffect(Location loc, int power, Type type, long detonateDelay, Color... fireworkColors) {
			final Firework fw = (Firework) loc.getWorld().spawn(loc, Firework.class);
			FireworkMeta meta = fw.getFireworkMeta();
			FireworkEffect effect = FireworkEffect.builder()
					.flicker(false)
					.trail(true)
					.with(type)
					.withColor(fireworkColors)
					.build();
			meta.addEffect(effect);
			meta.setPower(power);
			fw.setFireworkMeta(meta);
			
			if(detonateDelay >= 0) {
				new BukkitRunnable() {
	
					public void run() {
						fw.detonate();
					}
					
				}.runTaskLater(AllBanks.getInstance(), detonateDelay);
			}
		}
	}
	
	public static class SoundUtil{
		public static enum SoundType{
			SUCCESS,
			DENY,
			SWITCH_BANK_STEP,
			VIRTUAL_CHEST_OPEN,
			VIRTUAL_CHEST_CLOSE,
			NEW_BANK;
		}
		
		public static void sendSound(Player p, SoundType stype) {
			
			Sound sendSound = null;
			float soundPitch = 1;
			
			switch(stype) {
			case SUCCESS:
				sendSound = Sound.ENTITY_PLAYER_LEVELUP;
				soundPitch = 1;
				break;
			case DENY:
				sendSound = Sound.BLOCK_NOTE_PLING;
				soundPitch = (float) 0.7;
				break;
			case SWITCH_BANK_STEP:
				sendSound = Sound.BLOCK_NOTE_HAT;
				soundPitch = 1;
				break;
			case VIRTUAL_CHEST_OPEN:
				sendSound = Sound.BLOCK_CHEST_OPEN;
				soundPitch = 1;
				break;
			case VIRTUAL_CHEST_CLOSE:
				sendSound = Sound.BLOCK_CHEST_CLOSE;
				soundPitch = 1;
				break;
			case NEW_BANK:
				sendSound = Sound.ENTITY_FIREWORK_BLAST;
				soundPitch = 1;
				break;
			}
			
			p.playSound(p.getLocation(), sendSound, 5, soundPitch);
		}
	}
	
	public static class DatabaseUtil{
		
		static boolean  databaseLocked = false;
		
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
		
		public static String convertSecondsIntoTimeAgo(int seconds, boolean fullString) {
			return convertSecondsIntoTimeAgo(seconds, fullString, 2);
		}
		
		public static String convertSecondsIntoTimeAgo(int seconds, int maxShow) {
			return convertSecondsIntoTimeAgo(seconds, false, maxShow);
		}
		
		private static String convertSecondsIntoTimeAgo(int seconds, boolean fullString, int maxShow) {
			//Semanas
			int weeks = (int) Math.floor(seconds / (60 * 60 * 24 * 7));
			seconds -= weeks * (60 * 60 * 24 * 7);
			
			//Días
			int days = (int) Math.floor(seconds / (60 * 60 * 24)); 
			seconds -= days * (60 * 60 * 24);
			
			//Horas
			int hours = (int) Math.floor(seconds / (60 * 60)); 
			seconds -= hours * (60 * 60);
			
			//Minutos
			int minutes = (int) Math.floor(seconds / (60)); 
			seconds -= minutes * (60);
			
			String returnStr = "";
			int showed = 0;
			
			if(weeks > 0) {
				if(showed < maxShow || fullString) {
					returnStr += weeks + " " + Translation.get(StringsID.WEEKS, false)[0];
					showed++;
				}
			}
			
			if(days > 0) {
				if(showed < maxShow || fullString) {
					returnStr += ((showed == 0) ? "" : ", ") + days + " " + Translation.get(StringsID.DAYS, false)[0];
					showed++;
				}
			}
			
			if(hours > 0) {
				if(showed < maxShow || fullString) {
					returnStr += ((showed == 0) ? "" : ", ") + hours + " " + Translation.get(StringsID.HOURS, false)[0];
					showed++;
				}
			}
			
			if(minutes > 0) {
				if(showed < maxShow || fullString) {
					returnStr += ((showed == 0) ? "" : ", ") + minutes + " " + Translation.get(StringsID.MINUTES, false)[0];
					showed++;
				}
			}
			
			if(seconds > 0) {
				if(showed < maxShow || fullString) {
					returnStr += ((showed == 0) ? "" : ", ") + seconds + " " + Translation.get(StringsID.SECONDS, false)[0];
					showed++;
				}
			}
			
			if(seconds <= 0 && showed == 0) {
				returnStr += ((showed == 0) ? "" : ", ") + 0 + " " + Translation.get(StringsID.SECONDS, false)[0];
				showed++;
			}
			
			return returnStr;
		}
		
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
	
	public static class ShorterMaterialNameUtil {
		
		private static Set<Material> blackList = new ImmutableSet.Builder<Material>()
				.add(Material.STATIONARY_LAVA)
				.add(Material.STATIONARY_WATER)
				.add(Material.WATER)
				.add(Material.LAVA)
				.add(Material.BED_BLOCK)
				.add(Material.PISTON_EXTENSION)
				.add(Material.PISTON_MOVING_PIECE)
				.add(Material.DOUBLE_STEP)
				.add(Material.FIRE)
				.add(Material.REDSTONE_WIRE)
				.add(Material.CROPS)
				.add(Material.BURNING_FURNACE)
				.add(Material.SIGN_POST)
				.add(Material.WALL_SIGN)
				.add(Material.IRON_DOOR_BLOCK)
				.add(Material.GLOWING_REDSTONE_ORE)
				.add(Material.REDSTONE_TORCH_OFF)
				.add(Material.SUGAR_CANE_BLOCK)
				.add(Material.PORTAL)
				.add(Material.CAKE_BLOCK)
				.add(Material.DIODE_BLOCK_OFF)
				.add(Material.DIODE_BLOCK_ON)
				.add(Material.PUMPKIN_STEM)
				.add(Material.MELON_STEM)
				.add(Material.NETHER_WARTS)
				.add(Material.BREWING_STAND)
				.add(Material.PORTAL)
				.add(Material.REDSTONE_LAMP_ON)
				.add(Material.WOOD_DOUBLE_STEP)
				.add(Material.COCOA)
				.add(Material.TRIPWIRE)
				.add(Material.FLOWER_POT)
				.add(Material.REDSTONE_COMPARATOR_OFF)
				.add(Material.REDSTONE_COMPARATOR_ON)
				.add(Material.STANDING_BANNER)
				.add(Material.WALL_BANNER)
				.add(Material.DAYLIGHT_DETECTOR_INVERTED)
				.add(Material.DOUBLE_STONE_SLAB2)
				.add(Material.PURPUR_DOUBLE_SLAB)
				.add(Material.BEETROOT_BLOCK)
				.add(Material.END_GATEWAY)
				.add(Material.FROSTED_ICE)
				.add(Material.STRUCTURE_BLOCK)
				.add(Material.NETHER_BRICK)
				.add(Material.POTATO)
				.add(Material.CARROT)
				.add(Material.SKULL)
				.add(Material.SPRUCE_DOOR)
				.add(Material.ACACIA_DOOR)
				.add(Material.JUNGLE_DOOR)
				.add(Material.DARK_OAK_DOOR)
				.build();
		
		private static BiMap<String, String> itemNames = new ImmutableBiMap.Builder<String, String>()
				.put("AIR" , 				"Air")
				.put("STONE" , 				"Stone")
				.put("GRASS" , 				"GrassBlock")
				.put("DIRT" , 				"Dirt")
				.put("COBBLESTONE" , 		"Cobblestone")
				.put("WOOD" , 				"Wood")
				.put("SAPLING" , 			"Sapling")
				.put("BEDROCK" , 			"Bedrock")
				.put("SAND" , 				"Sand")
				.put("GRAVEL" , 			"Gravel")
				.put("GOLD_ORE" , 			"GoldOre")
				.put("IRON_ORE" , 			"IronOre")
				.put("COAL_ORE" , 			"CoalOre")
				.put("LOG" , 				"Log")
				.put("LEAVES" , 			"Leaves")
				.put("SPONGE" , 			"Sponge")
				.put("GLASS" , 				"Glass")
				.put("LAPIS_ORE" , 			"LapisOre")
				.put("LAPIS_BLOCK" , 		"LapisBlock")
				.put("DISPENSER" , 			"Dispenser")
				.put("SANDSTONE" , 			"Sandstone")
				.put("NOTE_BLOCK" , 		"NoteBlock")
				.put("POWERED_RAIL" , 		"PoweredRail")
				.put("DETECTOR_RAIL" , 		"DetectorRail")
				.put("PISTON_STICKY_BASE" , "StickyPiston")
				.put("WEB" , 				"Cobweb")
				.put("LONG_GRASS" , 		"Shrub")
				.put("DEAD_BUSH" , 			"DeadBush")
				.put("PISTON_BASE" , 		"Piston")
				.put("WOOL" , 				"Wool")
				.put("YELLOW_FLOWER" , 		"Dandelion")
				.put("RED_ROSE" , 			"Poppy")
				.put("BROWN_MUSHROOM" , 	"BrowMushroom")
				.put("RED_MUSHROOM" , 		"Mushroom")
				.put("GOLD_BLOCK" , 		"GoldBlock")
				.put("IRON_BLOCK" , 		"IronBlock")
				.put("STEP" , 				"StoneSlab")
				.put("BRICK" , 				"Bricks")
				.put("TNT" , 				"TNT")
				.put("BOOKSHELF" , 			"Bookshelf")
				.put("MOSSY_COBBLESTONE" , 	"MossStone")
				.put("OBSIDIAN" , 			"Obsidian")
				.put("TORCH" , 				"Torch")
				.put("MOB_SPAWNER" , 		"MobSpawner")
				.put("WOOD_STAIRS" , 		"WoodStairs")
				.put("CHEST" , 				"Chest")
				.put("DIAMOND_ORE" , 		"DiamondOre")
				.put("DIAMOND_BLOCK" , 		"DiamondBlock")
				.put("WORKBENCH" , 			"CraftingTable")
				.put("SOIL" , 				"Farmland")
				.put("FURNACE" , 			"Furnace")
				.put("WOODEN_DOOR" , 		"OakDoor")
				.put("LADDER" , 			"Ladder")
				.put("RAILS" , 				"Rails")
				.put("COBBLESTONE_STAIRS" , "CobbleStairs")
				.put("LEVER" , 				"Lever")
				.put("STONE_PLATE" , 		"StonePlate")
				.put("WOOD_PLATE" , 		"WoodPlate")
				.put("REDSTONE_ORE" , 		"RedstoneOre")
				.put("REDSTONE_TORCH_ON" , 	"RedstoneTorch")
				.put("STONE_BUTTON" , 		"StoneButton")
				.put("SNOW" , 				"Snow")
				.put("ICE" , 				"Ice")
				.put("SNOW_BLOCK" , 		"SnowBlock")
				.put("CACTUS" , 			"Cactus")
				.put("CLAY" , 				"Clay")
				.put("JUKEBOX" , 			"Jukebox")
				.put("FENCE" , 				"OakFence")
				.put("PUMPKIN" , 			"Pumpkin")
				.put("NETHERRACK" , 		"Netherrack")
				.put("SOUL_SAND" , 			"SoulSand")
				.put("GLOWSTONE" , 			"Glowstone")
				.put("JACK_O_LANTERN" , 	"LightPumpkin")
				.put("STAINED_GLASS" , 		"StainedGlass")
				.put("TRAP_DOOR" , 			"Trap Door")
				.put("MONSTER_EGGS" , 		"SilverFStone")
				.put("SMOOTH_BRICK" , 		"StoneBricks")
				.put("HUGE_MUSHROOM_1" , 	"MushBlock")
				.put("HUGE_MUSHROOM_2" , 	"RedMushBlock")
				.put("IRON_FENCE" , 		"IronBars")
				.put("THIN_GLASS" , 		"GlassPane")
				.put("MELON_BLOCK" , 		"MelonBlock")
				.put("VINE" , 				"Vines")
				.put("FENCE_GATE" , 		"OakFenceGate")
				.put("BRICK_STAIRS" , 		"BrickStairs")
				.put("SMOOTH_STAIRS" , 		"StBrickStair")
				.put("MYCEL" , 				"Mycel")
				.put("WATER_LILY" , 		"Lily Pad")
				.put("NETHER_FENCE" , 		"NetherFence")
				.put("NETHER_BRICK_STAIRS", "NetherStair")
				.put("ENCHANTMENT_TABLE" , 	"EnchantTable")
				.put("CAULDRON" , 			"Cauldron")
				.put("ENDER_PORTAL_FRAME" , "EndFrame")
				.put("ENDER_STONE" , 		"EndStone")
				.put("DRAGON_EGG" , 		"DragonEgg")
				.put("REDSTONE_LAMP_OFF" , 	"RedstoneLamp")
				.put("WOOD_STEP" , 			"OakWoodSlab")
				.put("SANDSTONE_STAIRS" , 	"SandStairs")
				.put("EMERALD_ORE" , 		"EmeraldOre")
				.put("ENDER_CHEST" , 		"EnderChest")
				.put("TRIPWIRE_HOOK" , 		"TripwireHook")
				.put("EMERALD_BLOCK" , 		"EmeraldBlock")
				.put("SPRUCE_WOOD_STAIRS" , "SpruceStairs")
				.put("BIRCH_WOOD_STAIRS" , 	"BirchStairs")
				.put("JUNGLE_WOOD_STAIRS" , "JungleStairs")
				.put("COMMAND" , 			"CommandBlock")
				.put("BEACON" , 			"Beacon")
				.put("COBBLE_WALL" , 		"CobbleWall")
				.put("WOOD_BUTTON" , 		"WoodButton")
				.put("ANVIL" , 				"Anvil")
				.put("TRAPPED_CHEST" , 		"TrappedChest")
				.put("GOLD_PLATE" , 		"GoldPlate")
				.put("IRON_PLATE" , 		"IronPlate")
				.put("DAYLIGHT_DETECTOR" , 	"DaySensor")
				.put("REDSTONE_BLOCK" , 	"RedstoneBlock")
				.put("QUARTZ_ORE" , 		"QuartzOre")
				.put("HOPPER" , 			"Hopper")
				.put("QUARTZ_BLOCK" , 		"QuartzBlock")
				.put("QUARTZ_STAIRS" , 		"QuartzStairs")
				.put("ACTIVATOR_RAIL" , 	"ActivatoRail")
				.put("DROPPER" , 			"Dropper")
				.put("STAINED_CLAY" , 		"StainedClay")
				.put("STAINED_GLASS_PANE" , "StainedPane")
				.put("LEAVES_2" , 			"Leaves2")
				.put("LOG_2" , 				"Log2")
				.put("ACACIA_STAIRS" , 		"AcaciaStairs")
				.put("DARK_OAK_STAIRS" , 	"DarkStairs")
				.put("SLIME_BLOCK" , 		"SlimeBlock")
				.put("BARRIER" , 			"Barrier")
				.put("IRON_TRAPDOOR" , 		"IronTrapdoor")
				.put("PRISMARINE" , 		"Prismarine")
				.put("SEA_LANTERN" , 		"SeaLantern")
				.put("HAY_BLOCK" , 			"HayBale")
				.put("CARPET" , 			"Carpet")
				.put("HARD_CLAY" , 			"HardenedClay")
				.put("COAL_BLOCK" , 		"CoalBlock")
				.put("PACKED_ICE" , 		"PackedIce")
				.put("DOUBLE_PLANT" , 		"DoublePlant")
				.put("RED_SANDSTONE" , 		"RedSandstone")
				.put("RED_SANDSTONE_STAIRS" , "RedStairs")
				.put("STONE_SLAB2" , 		"RedSandSlab")
				.put("SPRUCE_FENCE_GATE" , 	"SpruceFGate")
				.put("BIRCH_FENCE_GATE" , 	"BirchFGate")
				.put("JUNGLE_FENCE_GATE" , 	"JungleFGate")
				.put("DARK_OAK_FENCE_GATE" ,"DarkOakFGate")
				.put("ACACIA_FENCE_GATE" , 	"AcaciaFGate")
				.put("SPRUCE_FENCE" , 		"SpruceFence")
				.put("BIRCH_FENCE" , 		"BirchFence")
				.put("JUNGLE_FENCE" , 		"JungleFence")
				.put("DARK_OAK_FENCE" , 	"DarkOakFence")
				.put("ACACIA_FENCE" , 		"AcaciaFence")
				.put("END_ROD" , 			"EndRod")
				.put("CHORUS_PLANT" , 		"ChorusPlant")
				.put("CHORUS_FLOWER" , 		"ChorusFlower")
				.put("PURPUR_BLOCK" , 		"PurpurBlock")
				.put("PURPUR_PILLAR" , 		"PurpurPillar")
				.put("PURPUR_STAIRS" , 		"PurpurStairs")
				.put("PURPUR_SLAB" , 		"Purpur Slab")
				.put("END_BRICKS" , 		"EndBricks")
				.put("GRASS_PATH" , 		"GrassPath")
				.put("COMMAND_REPEATING" , 	"CommandRepeat")
				.put("COMMAND_CHAIN" , 		"CommandChain")
				.put("IRON_SPADE" , 		"IronShovel")
				.put("IRON_PICKAXE" , 		"IronPickaxe")
				.put("IRON_AXE" , 			"IronAxe")
				.put("FLINT_AND_STEEL" , 	"FlintSteel")
				.put("APPLE" , 				"Apple")
				.put("BOW" , 				"Bow")
				.put("ARROW" , 				"Arrow")
				.put("COAL" , 				"Coal")
				.put("DIAMOND" , 			"Diamond")
				.put("IRON_INGOT" , 		"IronIngot")
				.put("GOLD_INGOT" , 		"GoldIngot")
				.put("IRON_SWORD" , 		"IronSword")
				.put("WOOD_SWORD" , 		"WoodSword")
				.put("WOOD_SPADE" , 		"WoodShovel")
				.put("WOOD_PICKAXE" , 		"WoodPickaxe")
				.put("WOOD_AXE" , 			"WoodAxe")
				.put("STONE_SWORD" , 		"StoneSword")
				.put("STONE_SPADE" , 		"StoneShovel")
				.put("STONE_PICKAXE" , 		"StonePickaxe")
				.put("STONE_AXE" , 			"StoneAxe")
				.put("DIAMOND_SWORD" , 		"DiamondSword")
				.put("DIAMOND_SPADE" , 		"DiamondShovel")
				.put("DIAMOND_PICKAXE" , 	"DiamPickaxe")
				.put("DIAMOND_AXE" , 		"DiamondAxe")
				.put("STICK" , 				"Stick")
				.put("BOWL" , 				"Bowl")
				.put("MUSHROOM_SOUP" , 		"MushroomSoup")
				.put("GOLD_SWORD" , 		"GoldSword")
				.put("GOLD_SPADE" , 		"GoldShovel")
				.put("GOLD_PICKAXE" , 		"GoldPickaxe")
				.put("GOLD_AXE" , 			"GoldAxe")
				.put("STRING" , 			"String")
				.put("FEATHER" , 			"Feather")
				.put("SULPHUR" , 			"Gunpowder")
				.put("WOOD_HOE" , 			"WoodHoe")
				.put("STONE_HOE" , 			"StoneHoe")
				.put("IRON_HOE" , 			"IronHoe")
				.put("DIAMOND_HOE" , 		"DiamondHoe")
				.put("GOLD_HOE" , 			"GoldHoe")
				.put("SEEDS" , 				"Seeds")
				.put("WHEAT" , 				"Wheat")
				.put("BREAD" , 				"Bread")
				.put("LEATHER_HELMET" , 	"LeatherCap")
				.put("LEATHER_CHESTPLATE" , "LeatherTunic")
				.put("LEATHER_LEGGINGS" , 	"LeatherPants")
				.put("LEATHER_BOOTS" , 		"LeatherBoots")
				.put("CHAINMAIL_HELMET" , 	"ChainHelmet")
				.put("CHAINMAIL_CHESTPLATE" , "ChainCplate")
				.put("CHAINMAIL_LEGGINGS" , "ChainLeggs")
				.put("CHAINMAIL_BOOTS" , 	"ChainBoots")
				.put("IRON_HELMET" , 		"IronHelmet")
				.put("IRON_CHESTPLATE" , 	"IronChestPlate")
				.put("IRON_LEGGINGS" , 		"IronLeggings")
				.put("IRON_BOOTS" , 		"IronBoots")
				.put("DIAMOND_HELMET" , 	"DiamondHelmet")
				.put("DIAMOND_CHESTPLATE" , "DiamondCPlate")
				.put("DIAMOND_LEGGINGS" , 	"DiamondLeggs")
				.put("DIAMOND_BOOTS" , 		"DiamondBoots")
				.put("GOLD_HELMET" , 		"GoldHelmet")
				.put("GOLD_CHESTPLATE" , 	"GoldCPlate")
				.put("GOLD_LEGGINGS" , 		"GoldLeggs")
				.put("GOLD_BOOTS" , 		"GoldBoots")
				.put("FLINT" , 				"Flint")
				.put("PORK" , 				"RawPorkchop")
				.put("GRILLED_PORK" , 		"CookedPorkchop")
				.put("PAINTING" , 			"Painting")
				.put("GOLDEN_APPLE" , 		"GoldenApple")
				.put("SIGN" , 				"Sign")
				.put("WOOD_DOOR" , 			"OakDoor")
				.put("BUCKET" , 			"Bucket")
				.put("WATER_BUCKET" , 		"WaterBucket")
				.put("LAVA_BUCKET" , 		"LavaBucket")
				.put("MINECART" , 			"Minecart")
				.put("SADDLE" , 			"Saddle")
				.put("IRON_DOOR" , 			"IronDoor")
				.put("REDSTONE" , 			"Redstone")
				.put("SNOW_BALL" , 			"SnowBall")
				.put("BOAT" , 				"Boat")
				.put("LEATHER" , 			"Leather")
				.put("MILK_BUCKET" , 		"MilkBucket")
				.put("CLAY_BRICK" , 		"Brick")
				.put("CLAY_BALL" , 			"Clay")
				.put("SUGAR_CANE" , 		"SugarCane")
				.put("PAPER" , 				"Paper")
				.put("BOOK" , 				"Book")
				.put("SLIME_BALL" , 		"SlimeBall")
				.put("STORAGE_MINECART" , 	"ChestMinecart")
				.put("POWERED_MINECART" , 	"FurnaceMinecar")
				.put("EGG" , "				Egg")
				.put("COMPASS" , 			"Compass")
				.put("FISHING_ROD" , 		"FishingRod")
				.put("WATCH" , 				"Clock")
				.put("GLOWSTONE_DUST" , 	"GlowstoneDust")
				.put("RAW_FISH" , 			"RawFish")
				.put("COOKED_FISH" , 		"CookedFish")
				.put("INK_SACK" , 			"InkSack")
				.put("BONE" , 				"Bone")
				.put("SUGAR" , 				"Sugar")
				.put("CAKE" , 				"Cake")
				.put("BED" , 				"Bed")
				.put("DIODE" , 				"Diode")
				.put("COOKIE" , 			"Cookie")
				.put("MAP" , 				"Map")
				.put("SHEARS" , 			"Shears")
				.put("MELON" , 				"Melon")
				.put("PUMPKIN_SEEDS" , 		"PumpkinSeeds")
				.put("MELON_SEEDS" , 		"MelonSeeds")
				.put("RAW_BEEF" , 			"RawBeef")
				.put("COOKED_BEEF" , 		"CookedBeef")
				.put("RAW_CHICKEN" , 		"RawChicken")
				.put("COOKED_CHICKEN" , 	"CookeChicken")
				.put("ROTTEN_FLESH" , 		"RottenFlesh")
				.put("ENDER_PEARL" , 		"EnderPearl")
				.put("BLAZE_ROD" , 			"BlazeRod")
				.put("GHAST_TEAR" , 		"GhastTear")
				.put("GOLD_NUGGET" , 		"GoldNugget")
				.put("NETHER_STALK" , 		"NetherStalk")
				.put("POTION" , 			"Potion")
				.put("GLASS_BOTTLE" , 		"GlassBottle")
				.put("SPIDER_EYE" , 		"SpiderEye")
				.put("FERMENTED_SPIDER_EYE" , "FermentedEye")
				.put("BLAZE_POWDER" , 		"BlazePowder")
				.put("MAGMA_CREAM" , 		"MagmaCream")
				.put("BREWING_STAND_ITEM" , "BrewingStand")
				.put("CAULDRON_ITEM" , 		"Cauldron")
				.put("EYE_OF_ENDER" , 		"EyeOfEnder")
				.put("SPECKLED_MELON" , 	"GlisterMelon")
				.put("MONSTER_EGG" , 		"MonsterEgg")
				.put("EXP_BOTTLE" , 		"ExpBottle")
				.put("FIREBALL" , 			"Fireball")
				.put("BOOK_AND_QUILL" , 	"BookAndQuill")
				.put("WRITTEN_BOOK" , 		"WrittenBook")
				.put("EMERALD" , 			"Emerald")
				.put("ITEM_FRAME" , 		"ItemFrame")
				.put("FLOWER_POT_ITEM" , 	"FlowerPot")
				.put("CARROT_ITEM" , 		"Carrot")
				.put("POTATO_ITEM" , 		"Potato")
				.put("BAKED_POTATO" , 		"BakedPotato")
				.put("POISONOUS_POTATO" , 	"PoisonPotato")
				.put("EMPTY_MAP" , 			"EmptyMap")
				.put("GOLDEN_CARROT" , 		"GoldenCarrot")
				.put("SKULL_ITEM" , 		"Skull")
				.put("CARROT_STICK" , 		"CarrotStick")
				.put("NETHER_STAR" , 		"NetherStar")
				.put("PUMPKIN_PIE" , 		"PumpkinPie")
				.put("FIREWORK" , 			"Firework")
				.put("FIREWORK_CHARGE" , 	"FireworkStar")
				.put("ENCHANTED_BOOK" , 	"EnchantedBook")
				.put("REDSTONE_COMPARATOR" ,"Comparator")
				.put("NETHER_BRICK_ITEM" , 	"NetherBrick")
				.put("QUARTZ" , 			"Quartz")
				.put("EXPLOSIVE_MINECART" , "ExplosiveCart")
				.put("HOPPER_MINECART" , 	"HopperMCart")
				.put("PRISMARINE_SHARD" , 	"PrismaShard")
				.put("PRISMARINE_CRYSTALS" ,"PrismaCrystal")
				.put("RABBIT" , 			"Rabbit")
				.put("COOKED_RABBIT" , 		"CookedRabbit")
				.put("RABBIT_STEW" , 		"RabbitStew")
				.put("RABBIT_FOOT" , 		"RabbitFoot")
				.put("RABBIT_HIDE" , 		"RabbitHide")
				.put("ARMOR_STAND" , 		"ArmorStand")
				.put("IRON_BARDING" , 		"IronBarding")
				.put("GOLD_BARDING" , 		"GoldBarding")
				.put("DIAMOND_BARDING" , 	"DiamndBarding")
				.put("LEASH" , 				"Leash")
				.put("NAME_TAG" , 			"NameTag")
				.put("COMMAND_MINECART" , 	"CmdMinecart")
				.put("MUTTON" , 			"Mutton")
				.put("COOKED_MUTTON" , 		"CookedMutton")
				.put("BANNER" , 			"Banner")
				.put("END_CRYSTAL" , 		"EndCrystal")
				.put("SPRUCE_DOOR_ITEM" , 	"SpruceDoor")
				.put("BIRCH_DOOR_ITEM" , 	"BirchDoor")
				.put("JUNGLE_DOOR_ITEM" , 	"JungleDoor")
				.put("ACACIA_DOOR_ITEM" , 	"AcaciaDoor")
				.put("DARK_OAK_DOOR_ITEM" , "DarkOakDoor")
				.put("CHORUS_FRUIT" , 		"ChorusFruit")
				.put("CHORUS_FRUIT_POPPED" ,"ChorusFruitPop")
				.put("BEETROOT" , 			"Beetroot")
				.put("BEETROOT_SEEDS" , 	"BeetrootSeeds")
				.put("BEETROOT_SOUP" , 		"BeetrootSoup")
				.put("DRAGONS_BREATH" , 	"DragonBreath")
				.put("SPLASH_POTION" , 		"SplashPotion")
				.put("SPECTRAL_ARROW" , 	"SpectralArrow")
				.put("TIPPED_ARROW" , 		"TippedArrow")
				.put("LINGERING_POTION" , 	"LingerPotion")
				.put("SHIELD" , 			"Shield")
				.put("ELYTRA" , 			"Elytra")
				.put("BOAT_SPRUCE" , 		"BoatSpruce")
				.put("BOAT_BIRCH" , 		"BoatBirch")
				.put("BOAT_JUNGLE" , 		"BoatJungle")
				.put("BOAT_ACACIA" , 		"BoatAcacia")
				.put("BOAT_DARK_OAK" , 		"BoatDark Oak")
				.put("GOLD_RECORD" , 		"GoldRecord")
				.put("GREEN_RECORD" , 		"GreenRecord")
				.put("RECORD_3" , 			"Record 03")
				.put("RECORD_4" , 			"Record 04")
				.put("RECORD_5" , 			"Record 05")
				.put("RECORD_6" , 			"Record 06")
				.put("RECORD_7" , 			"Record 07")
				.put("RECORD_8" , 			"Record 08")
				.put("RECORD_9" , 			"Record 09")
				.put("RECORD_10" , 			"Record 10")
				.put("RECORD_11" , 			"Record 11")
				.put("RECORD_12" , 			"Record 12")
				.build();
		
		public String getItemName(ItemStack item) {
			return getItemName(item.getType());
		}
		
		public String getItemName(Material material) {
			if(itemIsOnBlackList(material)) return null;
			
			if(itemNames.containsKey(material.toString())) {
				return itemNames.get(material.toString());
			} else {
				AllBanksLogger.warning("Material: " + material + " is not registered! AllBanks is used with a new version of MC? CBV: " + Bukkit.getBukkitVersion());
				AllBanksLogger.warning("Trying to get a short name... (This can be changed on future versions of AllBanks)");
				return registerNameNativeMethod(material);
			}
		}
		
		public boolean itemIsOnBlackList(Material material) {
				return blackList.contains(material);
		}
		
		public boolean itemIsOnBlackList(ItemStack item) {
			return itemIsOnBlackList(item.getType());
		}
		
		public Material getItemByShortName(String shortName) {
			if(itemNames.containsValue(shortName)) {
				return Material.valueOf(itemNames.inverse().get(shortName));
			}
			
			return null;
		}
		
		public static String registerNameNativeMethod(Material m) {
			
			String shortName = "";
			
			String name = m.toString();
			
			if(name.length() > 13) {
				
				String[] splittedName = name.split("_");
				
				for(int i = 0; i < splittedName.length && i < 2; i++) {
					shortName += splittedName[i] + " ";
				}
				
				if(shortName.length() > 13) {
					String[] split = shortName.substring(0, shortName.length() - 1).split(" ");
					if(split.length == 2) {
						int rem = 12 - split[1].length();
						if(rem > 0) {
							shortName = capitalizeFirstLetter(split[0].substring(0, rem)) + capitalizeFirstLetter(split[1]);
						} else {
							shortName = capitalizeFirstLetter(split[1]);
						}
					}else {
						shortName = capitalizeFirstLetter(shortName.substring(0, 13));
					}
				}
				
			} else {
				shortName = capitalizeFirstLetter(name.replace("_", " "));
			}
			
			if(itemNames.containsValue(shortName)) {
				//Hasta 10 intentos
				for(int i = 2; i < 10; i++) {
					String tryName = shortName + i;
					if(!itemNames.containsValue(tryName)){
						shortName = tryName;
						break;
					}
				}
			}

			AllBanksLogger.warning("Registering new ShortName for " + m.toString() + ", new short name: " + shortName);
			
			itemNames.put(m.toString(), shortName);
			
			return shortName;
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
