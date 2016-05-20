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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

/**
 * @author Wirlie
 *
 */
public class FlatOrePopulator extends BlockPopulator {
	
	/**
	 * [00] | x + 0 | y + 0 | z + 1 | <br>
	 * [01] | x + 1 | y + 0 | z + 1 | <br>
	 * [02] | x + 1 | y + 0 | z + 0 | <br>
	 * [03] | x + 0 | y + 1 | z + 0 | <br>
	 * [04] | x + 0 | y + 1 | z + 1 | <br>
	 * [05] | x + 1 | y + 1 | z + 1 | <br>
	 * [06] | x + 1 | y + 1 | z + 0 | <br>
	 * [07] | x + 0 | y + 0 | z + 2 | <br>
	 * [08] | x + 2 | y + 0 | z + 2 | <br>
	 * [09] | x + 2 | y + 0 | z + 0 | <br>
	 * [10] | x + 0 | y + 2 | z + 0 | <br>
	 * [11] | x + 0 | y + 2 | z + 2 | <br>
	 * [12] | x + 2 | y + 2 | z + 2 | <br>
	 * [13] | x + 2 | y + 2 | z + 0 | <br>
	 * [14] | x + 0 | y + 0 | z + 1 | <br>
	 */
	
	private static int[][] relativeBlockPos = new int[15][3];
	
    static{
    	//	x + 0	y + 0	z + 1
    	relativeBlockPos[0][0] = 0;
    	relativeBlockPos[0][1] = 0;
    	relativeBlockPos[0][2] = 1;
    	//	x + 1	y + 0	z + 1
    	relativeBlockPos[1][0] = 1;
    	relativeBlockPos[1][1] = 0;
    	relativeBlockPos[1][2] = 1;
    	//	x + 1 	y + 0	z + 0
    	relativeBlockPos[2][0] = 1;
    	relativeBlockPos[2][1] = 0;
    	relativeBlockPos[2][2] = 0;
    	//	x + 0	y + 1	z + 0
    	relativeBlockPos[3][0] = 0;
    	relativeBlockPos[3][1] = 1;
    	relativeBlockPos[3][2] = 0;
    	//	x + 0	y + 1	z + 1
    	relativeBlockPos[4][0] = 0;
    	relativeBlockPos[4][1] = 1;
        relativeBlockPos[4][2] = 1;
        //	x + 1	y + 1	z + 1
        relativeBlockPos[5][0] = 1;
        relativeBlockPos[5][1] = 1;
        relativeBlockPos[5][2] = 1;
        //	x + 1	y + 1	z + 0
        relativeBlockPos[6][0] = 1;
        relativeBlockPos[6][1] = 1;
        relativeBlockPos[6][2] = 0;
    	//	x + 0	y + 0	z + 2
    	relativeBlockPos[7][0] = 0;
    	relativeBlockPos[7][1] = 0;
    	relativeBlockPos[7][2] = 2;
    	//	x + 2	y + 0	z + 2
    	relativeBlockPos[8][0] = 2;
    	relativeBlockPos[8][1] = 0;
    	relativeBlockPos[8][2] = 2;
    	//	x + 2 	y + 0	z + 0
    	relativeBlockPos[9][0] = 2;
    	relativeBlockPos[9][1] = 0;
    	relativeBlockPos[9][2] = 0;
    	//	x + 0	y + 2	z + 0
    	relativeBlockPos[10][0] = 0;
    	relativeBlockPos[10][1] = 2;
    	relativeBlockPos[10][2] = 0;
    	//	x + 0	y + 2	z + 2
    	relativeBlockPos[11][0] = 0;
    	relativeBlockPos[11][1] = 2;
        relativeBlockPos[11][2] = 2;
        //	x + 2	y + 2	z + 2
        relativeBlockPos[12][0] = 1;
        relativeBlockPos[12][1] = 2;
        relativeBlockPos[12][2] = 2;
        //	x + 2	y + 2	z + 0
        relativeBlockPos[13][0] = 2;
        relativeBlockPos[13][1] = 2;
        relativeBlockPos[13][2] = 0;
    	//	x + 0	y + 0	z + 1
    	relativeBlockPos[14][0] = 0;
    	relativeBlockPos[14][1] = 0;
    	relativeBlockPos[14][2] = 3;
    }

    private WorldGenerationCfg worldCfg;
    
    /**
     * [0] Coal - y128 <br>
     * [1] Iron - y64 <br>
     * [2] Lapis - y23 <br>
     * [3] Gold - y60 <br>
     * [4] Redstone - y15 <br>
     * [5] Diamond - y15 <br>
     * [6] Emerald - y30 <br>
     */
    private int[] configOresMaxY = new int[7];
    
    /**
     * [0] Coal<br>
     * [1] Iron<br>
     * [2] Lapis<br>
     * [3] Gold<br>
     * [4] Redstone<br>
     * [5] Diamond<br>
     * [6] Emerald<br>
     */
    
    private int[][] configOresRand = new int[7][2];
    
    /**
     * [0] Coal<br>
     * [1] Iron<br>
     * [2] Lapis<br>
     * [3] Gold<br>
     * [4] Redstone<br>
     * [5] Diamond<br>
     * [6] Emerald<br>
     */
    private boolean[] configOresGenerate = new boolean[7];
    
    /**
     * [0] Coal<br>
     * [1] Iron<br>
     * [2] Lapis<br>
     * [3] Gold<br>
     * [4] Redstone<br>
     * [5] Diamond<br>
     * [6] Emerald<br>
     */
    private int[] oresAmount = new int[7];
    
    /**
     * [0] Coal<br>
     * [1] Iron<br>
     * [2] Lapis<br>
     * [3] Gold<br>
     * [4] Redstone<br>
     * [5] Diamond<br>
     * [6] Emerald<br>
     */
    private Material[] oresMaterial = new Material[]{
    		Material.COAL_ORE, 		//[0]
    		Material.IRON_ORE, 		//[1]
    		Material.LAPIS_ORE, 	//[2]
    		Material.GOLD_ORE,		//[3]
    		Material.REDSTONE_ORE,	//[4]
    		Material.DIAMOND_ORE,	//[5]
    		Material.EMERALD_ORE	//[6]
    };
    
    /**
	 * @param worldCfg
	 */
	public FlatOrePopulator(WorldGenerationCfg worldCfg) {
		this.worldCfg = worldCfg;
		
		configOresMaxY[0] = worldCfg.coal_ore_max_y;
		configOresMaxY[1] = worldCfg.iron_ore_max_y;
		configOresMaxY[2] = worldCfg.lapis_ore_max_y;
		configOresMaxY[3] = worldCfg.gold_ore_max_y;
		configOresMaxY[4] = worldCfg.redstone_ore_max_y;
		configOresMaxY[5] = worldCfg.diamond_ore_max_y;
		configOresMaxY[6] = worldCfg.emerald_ore_max_y;
		
		configOresRand[0][0] = worldCfg.coal_rand_min;
		configOresRand[0][1] = worldCfg.coal_rand_max;
		configOresRand[1][0] = worldCfg.iron_rand_min;
		configOresRand[1][1] = worldCfg.iron_rand_max;
		configOresRand[2][0] = worldCfg.lapis_rand_min;
		configOresRand[2][1] = worldCfg.lapis_rand_max;
		configOresRand[3][0] = worldCfg.gold_rand_min;
		configOresRand[3][1] = worldCfg.gold_rand_max;
		configOresRand[4][0] = worldCfg.redstone_rand_min;
		configOresRand[4][1] = worldCfg.redstone_rand_max;
		configOresRand[5][0] = worldCfg.diamond_rand_min;
		configOresRand[5][1] = worldCfg.diamond_rand_max;
		configOresRand[6][0] = worldCfg.emerald_rand_min;
		configOresRand[6][1] = worldCfg.emerald_rand_max;
		
		configOresGenerate[0] = worldCfg.coal_ore;
		configOresGenerate[1] = worldCfg.iron_ore;
		configOresGenerate[2] = worldCfg.lapis_ore;
		configOresGenerate[3] = worldCfg.gold_ore;
		configOresGenerate[4] = worldCfg.redstone_ore;
		configOresGenerate[5] = worldCfg.diamond_ore;
		configOresGenerate[6] = worldCfg.emerald_ore;
	}

	@Override
    public void populate(World world, Random rand, Chunk chunk) {
    	//Coal
		if(configOresRand[0][0] < 0) configOresRand[0][0] = 0;
		if(configOresRand[0][1] < configOresRand[0][0]) configOresRand[0][1] = configOresRand[0][0];
		oresAmount[0] = (rand.nextInt(configOresRand[0][1] - configOresRand[0][0]) + configOresRand[0][0]);
    	//Iron
    	if(configOresRand[1][0] < 0) configOresRand[1][0] = 0;
		if(configOresRand[1][1] < configOresRand[1][0]) configOresRand[1][1] = configOresRand[1][0];
		oresAmount[1] = (rand.nextInt(configOresRand[1][1] - configOresRand[1][0]) + configOresRand[1][0]);
    	//Lapis
    	if(configOresRand[2][0] < 0) configOresRand[2][0] = 0;
		if(configOresRand[2][1] < configOresRand[2][0]) configOresRand[2][1] = configOresRand[2][0];
		oresAmount[2] = (rand.nextInt(configOresRand[2][1] - configOresRand[2][0]) + configOresRand[2][0]);
    	//Gold
    	if(configOresRand[3][0] < 0) configOresRand[3][0] = 0;
		if(configOresRand[3][1] < configOresRand[3][0]) configOresRand[3][1] = configOresRand[3][0];
		oresAmount[3] = (rand.nextInt(configOresRand[3][1] - configOresRand[3][0]) + configOresRand[3][0]);
    	//Redstone
    	if(configOresRand[4][0] < 0) configOresRand[4][0] = 0;
		if(configOresRand[4][1] < configOresRand[4][0]) configOresRand[4][1] = configOresRand[4][0];
		oresAmount[4] = (rand.nextInt(configOresRand[4][1] - configOresRand[4][0]) + configOresRand[4][0]);
    	//Diamond
    	if(configOresRand[5][0] < 0) configOresRand[5][0] = 0;
		if(configOresRand[5][1] < configOresRand[5][0]) configOresRand[5][1] = configOresRand[5][0];
		oresAmount[5] = (rand.nextInt(configOresRand[5][1] - configOresRand[5][0]) + configOresRand[5][0]);
    	//Emerald
    	if(configOresRand[6][0] < 0) configOresRand[6][0] = 0;
		if(configOresRand[6][1] < configOresRand[6][0]) configOresRand[6][1] = configOresRand[6][0];
		oresAmount[6] = (rand.nextInt(configOresRand[6][1] - configOresRand[6][0]) + configOresRand[6][0]);
    	
    	boolean shouldContinue = true;
    	int i = 0;
    	
        while(shouldContinue) {
        	shouldContinue = false;
        	
			if(i < oresAmount[0] && configOresGenerate[0]){ 
				populateRandomOre(chunk, rand, oresMaterial[0]);
	        	shouldContinue = true;
			}
            if(i < oresAmount[1] && configOresGenerate[1]){
            	populateRandomOre(chunk, rand, oresMaterial[1]);
	        	shouldContinue = true;
            }
            if(i < oresAmount[2] && configOresGenerate[2]){
            	populateRandomOre(chunk, rand, oresMaterial[2]);
	        	shouldContinue = true;
            }
            if(i < oresAmount[3] && configOresGenerate[3]){
            	populateRandomOre(chunk, rand, oresMaterial[3]);
	        	shouldContinue = true;
            }
            if(i < oresAmount[4] && configOresGenerate[4]){
            	populateRandomOre(chunk, rand, oresMaterial[4]);
	        	shouldContinue = true;
            }
			if(i < oresAmount[5] && configOresGenerate[5]){
				populateRandomOre(chunk, rand, oresMaterial[5]);
	        	shouldContinue = true;
			}
			if(i < oresAmount[6] && configOresGenerate[6]){
				populateRandomOre(chunk, rand, oresMaterial[6]);
	        	shouldContinue = true;
			}
			
        	i++;
        }
    }

    private void populateRandomOre(Chunk chunk, Random rand, Material material) {
    	
    	int maxY = 0, min_amount = 3, max_amount = 3;
    	
    	if(material == Material.COAL_ORE){
    		maxY = configOresMaxY[0];
    		max_amount = 15;
    		min_amount = 5;
    	}
    	if(material == Material.IRON_ORE){
    		maxY = configOresMaxY[1];
    		max_amount = 12;
    		min_amount = 5;
    	}
    	if(material == Material.LAPIS_ORE){
    		maxY = configOresMaxY[2];
    		max_amount = 10;
    	}
    	if(material == Material.GOLD_ORE){
    		maxY = configOresMaxY[3];
    		max_amount = 8;
    	}
    	if(material == Material.REDSTONE_ORE){
    		maxY = configOresMaxY[4];
    		max_amount = 8;
    	}
    	if(material == Material.DIAMOND_ORE){
    		maxY = configOresMaxY[5];
    		max_amount = 12;
    	}
    	if(material == Material.EMERALD_ORE){
    		maxY = configOresMaxY[6];
    		max_amount = 2; 
    		min_amount = 1;
    	}
    	
    	if(maxY > worldCfg.world_height) maxY = worldCfg.world_height;
    	
        int y = 0, z = 0, x = rand.nextInt(14) + 1;
        
        if (chunk.getBlock(x, y = rand.nextInt(maxY - 2) + 1, z = rand.nextInt(14) + 1).getType() == Material.STONE) {
            chunk.getBlock(x, y, z).setType(material);
        }
        
        this.setBlockChunk(material, chunk, x, y, z);
        
        int ore_amount = rand.nextInt(max_amount - min_amount) + min_amount;
        
        for(int i = 0; i < ore_amount; i++) {
                this.setBlockChunk(material, chunk, x + FlatOrePopulator.relativeBlockPos[i][0], y + FlatOrePopulator.relativeBlockPos[i][1], z + FlatOrePopulator.relativeBlockPos[i][2]);
        }
    }

    private void setBlockChunk(Material ore, Chunk chunk, int x, int y, int z) {
        Block block = chunk.getBlock(x, y - 1, z);
        if (block.getY() < (worldCfg.world_height - 2) && !block.getType().equals(Material.BEDROCK)) block.setType(ore);
    }
}
