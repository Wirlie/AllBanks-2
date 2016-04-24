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

import org.bukkit.generator.ChunkGenerator;

/**
 * @author Wirlie
 *
 */
public class WorldGenerator {
	 
	/**
	*
	* @param worldName
	* The name of the world the generator is being applied to
	* @param GenId
	* The id (if any) specified by the user. It can be used if the plugin
	* wants to have multiple generators in one plugin. More on this later.
	* @return
	* The ChunkGenerator that this plugin provides
	*/
	public static ChunkGenerator getDefaultWorldGenerator(String worldName, String GenId) {
	     return new WorldChunkGenerator();
	}
	
}
