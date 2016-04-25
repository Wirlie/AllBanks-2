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
package me.wirlie.allbanks.generator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import me.wirlie.allbanks.land.AllBanksWorld.WorldGenerationCfg;

/**
 * @author Wirlie
 *
 */
public class FlatOrePopulator extends BlockPopulator {
	static int[][] relativeBlockPos = new int[15][3];
	
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
    
    static final int[] ORES_MAX_Y = {
    	128, //0 Coal	128
        64, //1 Iron	64
        23, //2 Lapis
        40, //3 Gold	60
        15, //4 Redstone
        15, //5 Diamond
        30, //6 Emerald
    };
    
    WorldGenerationCfg worldCfg;

    /**
	 * @param worldCfg
	 */
	public FlatOrePopulator(WorldGenerationCfg worldCfg) {
		this.worldCfg = worldCfg;
	}

	@Override
    public void populate(World world, Random rand, Chunk chunk) {
    	
    	int coal_rand = (rand.nextInt(10 - 6) + 6);
    	int iron_rand = (rand.nextInt(8 - 5) + 5);
    	int lapis_rand = (rand.nextInt(4));
    	int gold_rand = (rand.nextInt(3 - 1) + 1);
    	int redstone_rand = (rand.nextInt(4 - 2) + 2);
    	int diamond_rand = (rand.nextInt(2));
    	int emerald_rand = (rand.nextInt(2));
    	
    	boolean shouldContinue = true;
    	int i = 0;
    	
        while(shouldContinue) {
        	shouldContinue = false;
        	
			if(i < coal_rand && !worldCfg.coal_ore){ 
				populateRandomOre(chunk, rand, Material.COAL_ORE);
	        	shouldContinue = true;
			}
            if(i < iron_rand && !worldCfg.iron_ore){
            	populateRandomOre(chunk, rand, Material.IRON_ORE);
	        	shouldContinue = true;
            }
            if(i < lapis_rand && !worldCfg.lapis_ore){
            	populateRandomOre(chunk, rand, Material.LAPIS_ORE);
	        	shouldContinue = true;
            }
            if(i < gold_rand && !worldCfg.gold_ore){
            	populateRandomOre(chunk, rand, Material.GOLD_ORE);
	        	shouldContinue = true;
            }
            if(i < redstone_rand && !worldCfg.redstone_ore){
            	populateRandomOre(chunk, rand, Material.REDSTONE_ORE);
	        	shouldContinue = true;
            }
			if(i < diamond_rand && !worldCfg.lapis_ore){
				populateRandomOre(chunk, rand, Material.DIAMOND_ORE);
	        	shouldContinue = true;
			}
			if(i < emerald_rand && !worldCfg.emerald_ore){
				populateRandomOre(chunk, rand, Material.EMERALD_ORE);
	        	shouldContinue = true;
			}
			
        	i++;
        }
    }

    public void populateRandomOre(Chunk chunk, Random rand, Material material) {
    	
    	int maxY = 0, min_amount = 3, max_amount = 3;
    	
    	if(material == Material.COAL_ORE){
    		maxY = ORES_MAX_Y[0];
    		max_amount = 15;
    		min_amount = 5;
    	}
    	if(material == Material.IRON_ORE){
    		maxY = ORES_MAX_Y[1];
    		max_amount = 12;
    		min_amount = 5;
    	}
    	if(material == Material.LAPIS_ORE){
    		maxY = ORES_MAX_Y[2];
    		max_amount = 10;
    	}
    	if(material == Material.GOLD_ORE){
    		maxY = ORES_MAX_Y[3];
    		max_amount = 8;
    	}
    	if(material == Material.REDSTONE_ORE){
    		maxY = ORES_MAX_Y[4];
    		max_amount = 8;
    	}
    	if(material == Material.DIAMOND_ORE){
    		maxY = ORES_MAX_Y[5];
    		max_amount = 12;
    	}
    	if(material == Material.EMERALD_ORE){
    		maxY = ORES_MAX_Y[6];
    		max_amount = 2; 
    		min_amount = 1;
    	}
    	
    	if(maxY > worldCfg.world_height) maxY = worldCfg.world_height;
    	
        int y = 0, z = 0, x = rand.nextInt(14) + 1;
        
        if (chunk.getBlock(x, y = rand.nextInt(maxY - 2) + 1, z = rand.nextInt(14) + 1).getType() == Material.STONE) {
            chunk.getBlock(x, y, z).setType(material);
        }
        
        this.setOre(material, chunk, x, y, z);

        int ore_amount = rand.nextInt(max_amount - min_amount) + min_amount;
        
        for(int i = 0; i < ore_amount; i++) {
                this.setOre(material, chunk, x + FlatOrePopulator.relativeBlockPos[i][0], y + FlatOrePopulator.relativeBlockPos[i][1], z + FlatOrePopulator.relativeBlockPos[i][2]);
        }
    }

    public void setOre(Material ore, Chunk chunk, int x, int y, int z) {
        Block block = chunk.getBlock(x, y - 1, z);
        if (block.getType() == Material.STONE) block.setType(ore);
    }
}
