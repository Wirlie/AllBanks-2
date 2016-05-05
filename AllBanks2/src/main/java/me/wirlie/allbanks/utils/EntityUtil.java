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

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * @author josue
 *
 */
public class EntityUtil {
	
	private EntityUtil(){
		
	}
	
	public static boolean isAnimal(Entity e){
		if(e.getType().equals(EntityType.COW) ||
				e.getType().equals(EntityType.CHICKEN) ||
				e.getType().equals(EntityType.PIG) ||
				e.getType().equals(EntityType.SHEEP) ||
				e.getType().equals(EntityType.OCELOT) ||
				e.getType().equals(EntityType.WOLF) ||
				e.getType().equals(EntityType.HORSE))
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean isNeutral(Entity e){
		if(e.getType().equals(EntityType.SNOWMAN) ||
				e.getType().equals(EntityType.IRON_GOLEM) ||
				e.getType().equals(EntityType.VILLAGER) ||
				e.getType().equals(EntityType.MINECART) ||
				e.getType().equals(EntityType.MINECART_CHEST) ||
				e.getType().equals(EntityType.MINECART_COMMAND) ||
				e.getType().equals(EntityType.MINECART_FURNACE) ||
				e.getType().equals(EntityType.MINECART_HOPPER) ||
				e.getType().equals(EntityType.MINECART_MOB_SPAWNER) ||
				e.getType().equals(EntityType.MINECART_TNT) ||
				e.getType().equals(EntityType.ARMOR_STAND) ||
				e.getType().equals(EntityType.BOAT) ||
				e.getType().equals(EntityType.DROPPED_ITEM) ||
				e.getType().equals(EntityType.ENDER_CRYSTAL) ||
				e.getType().equals(EntityType.ENDER_SIGNAL))
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean isHostil(Entity e){
		if(!isAnimal(e) && !isNeutral(e)){
			return true;
		}
		
		return false;
	}
}
