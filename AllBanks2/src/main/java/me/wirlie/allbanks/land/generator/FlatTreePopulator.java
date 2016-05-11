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
package me.wirlie.allbanks.land.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import me.wirlie.allbanks.land.WorldGenerationCfg;

/**
 * @author Wirlie
 *
 */
public class FlatTreePopulator extends BlockPopulator {
	
	WorldGenerationCfg worldCfg;
	/**
	 * @param worldCfg
	 */
	public FlatTreePopulator(WorldGenerationCfg worldCfg) {
		this.worldCfg = worldCfg;
	}

	@Override
	public void populate(World world, Random rand, Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int realX = x + chunk.getX() * 16; //find the world location of chunk location x
				int realZ = z + chunk.getZ() * 16;
				int y = worldCfg.world_height;
				
                Block block = chunk.getBlock(x, y, z);
                
                if(!block.getType().equals(Material.AIR)){
                	continue;
                }
                
                Block blockSupp = block.getRelative(BlockFace.DOWN);
                
                if(!blockSupp.getType().equals(Material.GRASS) && !blockSupp.getType().equals(Material.DIRT) && !blockSupp.getType().equals(Material.LONG_GRASS)) continue;
                
                if(blockSupp.getType().equals(Material.LONG_GRASS)){
                	y = y - 1;
                }
                
                if (rand.nextInt(100) < 1) {
                    world.generateTree(new Location(world, (double)realX, (double)(y), (double)realZ), rand.nextInt(100) < 70 ? worldCfg.tree_type_1 : worldCfg.tree_type_2);
                }
			}
		}
	}
}
