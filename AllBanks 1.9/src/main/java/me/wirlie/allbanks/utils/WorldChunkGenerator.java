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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import me.wirlie.allbanks.utils.populators.GrassPopulator;
import me.wirlie.allbanks.utils.populators.OrePopulator;
import me.wirlie.allbanks.utils.populators.RoadPopulator;
import me.wirlie.allbanks.utils.populators.TreePopulator;

/**
 * @author Wirlie
 *
 */
public class WorldChunkGenerator extends ChunkGenerator{
	/**
	 * 
	 * @param x
	 * X co-ordinate of the block to be set in the array
	 * @param y
	 * Y co-ordinate of the block to be set in the array
	 * @param z
	 * Z co-ordinate of the block to be set in the array
	 * @param chunk
	 * An array containing the Block id's of all the blocks in the chunk. The first offset
	 * is the block section number. There are 16 block sections, stacked vertically, each of which
	 * 16 by 16 by 16 blocks.
	 * @param material
	 * The material to set the block to.
	 */
	
	@SuppressWarnings("deprecation")
	void setBlock(int x, int y, int z, byte[][] chunk, Material material) {
		if (chunk[y >> 4] == null)
			chunk[y >> 4] = new byte[16 * 16 * 16];
		if (!(y <= 256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0))
			return;
		try {
			chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
		} catch (Exception e) {
			// do nothing
		}
	}

	@Override
	/**
	 * @param world
	 * The world the chunk belongs to
	 * @param rand
	 * Don't use this, make a new random object using the world seed (world.getSeed())
	 * @param biome
	 * Use this to set/get the current biome
	 * @param ChunkX and ChunkZ
	 * The x and z co-ordinates of the current chunk.
	 */
	public byte[][] generateBlockSections(World world, Random rand, int ChunkX, int ChunkZ, BiomeGrid biome) {
		
		byte[][] chunk = new byte[world.getMaxHeight() / 16][];
		
		for (int x=0; x<16; x++) { //loop through all of the blocks in the chunk that are lower than maxHeight
			for (int z=0; z<16; z++) {
				int maxHeight = 55; //how thick we want out flat terrain to be
				for (int y = 0; y < maxHeight; y++) {

					biome.setBiome(x, z, Biome.PLAINS);
					
					if(y == 54){
						setBlock(x, y, z, chunk, Material.GRASS); //set the current block to stone
					}else if(y > 50 && y < 54){
						setBlock(x, y, z, chunk, Material.DIRT); //set the current block to stone
					}else if(y == 0){
						setBlock(x, y, z, chunk, Material.BEDROCK); //set the current block to stone
					}else{
						setBlock(x, y, z, chunk, Material.STONE); //set the current block to stone
					}
				}
			}
		}
		return chunk;
	}

	/**
	 * Returns a list of all of the block populators (that do "little" features)
	 * to be called after the chunk generator
	 */
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        ArrayList<BlockPopulator> pops = new ArrayList<BlockPopulator>();
        
       //Hacer que crezcan hierbas
        pops.add(new RoadPopulator());
        pops.add(new OrePopulator());
        pops.add(new GrassPopulator());
        pops.add(new TreePopulator());
        
        return pops;
    }

}
