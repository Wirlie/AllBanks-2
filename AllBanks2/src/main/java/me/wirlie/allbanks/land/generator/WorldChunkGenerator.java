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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

/**
 * @author Wirlie
 *
 */
public class WorldChunkGenerator extends ChunkGenerator{

	WorldGenerationCfg worldCfg = null;
	
	/**
	 * @param worldGenerationCfg
	 */
	public WorldChunkGenerator(WorldGenerationCfg worldGenerationCfg) {
		worldCfg = worldGenerationCfg;
	}

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
		
		int realXChunk = ChunkX << 4;
		int realZChunk = ChunkZ << 4;
		
		int plotSize = worldCfg.plot_size;
		int roadSize = worldCfg.road_size;
		
		Material roadMaterial = worldCfg.road_material;
		
		int totalSize = 1 + plotSize + 1 + roadSize;
		
		for (int x=0; x<16; x++) { //loop through all of the blocks in the chunk that are lower than maxHeight
			for (int z=0; z<16; z++) {
				for (int y = 0; y < worldCfg.world_height; y++) {

					biome.setBiome(x, z, worldCfg.default_biome);
					
					int worldX = realXChunk + x;
					int worldZ = realZChunk + z;
					
					boolean placeRoad = false;
					boolean placePlotLimit = false;
					
					if(y == (worldCfg.world_height - 1)){
						
						//Eje X
						if(worldX >= 0){
							//Positivo
							int cursorX = (worldX / totalSize) * totalSize;
							int relativeCursorX = worldX - cursorX;
							
							if(relativeCursorX == 0){
								setBlock(x, y + 1, z, chunk, Material.STEP);
								placePlotLimit = true;
							}else if(relativeCursorX < (plotSize + 1)){
								//Plot
								setBlock(x, y, z, chunk, Material.GRASS);
							}else if(relativeCursorX == (plotSize + 1)){
								setBlock(x, y + 1, z, chunk, Material.STEP);
								placePlotLimit = true;
							}else{
								setBlock(x, y, z, chunk, roadMaterial);
								placeRoad = true;
							}
						}else{
							//Negativo
							int cursorX = (worldX / totalSize) * totalSize;
							int relativeCursorX = ((worldX - cursorX) * -1);
							
							if(relativeCursorX == 0){
								setBlock(x, y + 1, z, chunk, Material.STEP);
								placePlotLimit = true;
							}else if(relativeCursorX <= roadSize){
								setBlock(x, y, z, chunk, roadMaterial);
								placeRoad = true;
							}else if(relativeCursorX == (roadSize + 1)){
								setBlock(x, y + 1, z, chunk, Material.STEP);
								placePlotLimit = true;
							}else{
								setBlock(x, y, z, chunk, Material.GRASS);
							}
						}
						
						//Eje Z
						if(!placeRoad)
							if(worldZ >= 0){
								//Positivo
								int cursorZ = (worldZ / totalSize) * totalSize;
								int relativeCursorZ = worldZ - cursorZ;
								
								if(relativeCursorZ == 0){
									setBlock(x, y + 1, z, chunk, Material.STEP);
									placePlotLimit = true;
								}else if(relativeCursorZ < (plotSize + 1)){
									//Plot
									setBlock(x, y, z, chunk, Material.GRASS);
								}else if(relativeCursorZ == (plotSize + 1)){
									setBlock(x, y + 1, z, chunk, Material.STEP);
									placePlotLimit = true;
								}else{
									setBlock(x, y, z, chunk, roadMaterial);
									if(placePlotLimit) setBlock(x, y + 1, z, chunk, Material.AIR);
									placeRoad = true;
								}
							}else{
								//Negativo
								int cursorZ = (worldZ / totalSize) * totalSize;
								int relativeCursorZ = ((worldZ - cursorZ) * -1);
								
								if(relativeCursorZ == 0){
									setBlock(x, y + 1, z, chunk, Material.STEP);
									placePlotLimit = true;
								}else if(relativeCursorZ <= roadSize){
									setBlock(x, y, z, chunk, roadMaterial);
									if(placePlotLimit) setBlock(x, y + 1, z, chunk, Material.AIR);
									placeRoad = true;
								}else if(relativeCursorZ == (roadSize + 1)){
									setBlock(x, y + 1, z, chunk, Material.STEP);
									placePlotLimit = true;
								}else{
									setBlock(x, y, z, chunk, Material.GRASS);
								}
							}
					}else if(y > (worldCfg.world_height - 5) && y < worldCfg.world_height){
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
        //pops.add(new FlatRoadPopulator(worldCfg));
        if(worldCfg.coal_ore || worldCfg.diamond_ore || worldCfg.emerald_ore || worldCfg.redstone_ore
        		|| worldCfg.lapis_ore || worldCfg.gold_ore || worldCfg.iron_ore)
        	pops.add(new FlatOrePopulator(worldCfg));
        if(worldCfg.generate_grass)
        	pops.add(new FlatGrassPopulator(worldCfg));
        if(worldCfg.generate_tree)
        	pops.add(new FlatTreePopulator(worldCfg));
        
        return pops;
    }

}
