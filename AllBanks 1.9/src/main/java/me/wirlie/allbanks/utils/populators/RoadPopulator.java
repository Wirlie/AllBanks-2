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
package me.wirlie.allbanks.utils.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

/**
 * @author Wirlie
 *
 */
public class RoadPopulator extends BlockPopulator  {
	
	@Override
	public void populate(World world, Random rand, Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < world.getMaxHeight(); y++) {
					int realX = x + chunk.getX() * 16; //find the world location of chunk location x
					int realZ = z + chunk.getZ() * 16;
					
					//tamaño de calle 6
					int roadSize = 7;
					
					//tamaño de plot
					int plotSize = 20;
					
					boolean placeRoad = false;
					boolean placePlotLimit = false;

					int cursorX = 0;
					int cursorZ = 0;
					
					while(cursorX < realX){
						
						cursorX += roadSize;
						
						if(realX <= cursorX){
							placeRoad = true;
							break;
						}
						
						//Limite del plot, decoración
						cursorX++;
						
						if(realX == cursorX){
							placePlotLimit = true;
							break;
						}
						
						//Tamaño del plot
						cursorX += plotSize;
						
						if(realX < cursorX){
							//El cursor apunta a una parcela
							break;
						}
						
						cursorX ++;
						
						if(realX == cursorX){
							placePlotLimit = true;
							break;
						}
						
						//Continuar con el calculo del cursor
						continue;
					}
					
					if(!placeRoad && !placePlotLimit)
						while(cursorZ < realZ){
							
							cursorZ += roadSize;
							
							if(realZ <= cursorZ){
								placeRoad = true;
								break;
							}
							
							//Limite del plot, decoración
							cursorZ++;
							
							if(realZ == cursorZ){
								placePlotLimit = true;
								break;
							}
							
							//Tamaño del plot
							cursorZ += plotSize;
							
							if(realZ < cursorZ){
								//El cursor apunta a una parcela
								break;
							}
							
							cursorZ ++;
							
							if(realZ == cursorZ){
								placePlotLimit = true;
								break;
							}
							
							//Continuar con el calculo del cursor
							continue;
						}
					
					if(!placeRoad && !placePlotLimit) continue;
					
					if(placeRoad){
	                    Block block = chunk.getBlock(x, y, z);
	                    
	                    if(!block.getType().equals(Material.AIR)){
	                    	continue;
	                    }
	                    
	                    block = block.getRelative(BlockFace.DOWN);
	                    
	                    block.setType(Material.WOOD);
					}
					
					if(placePlotLimit){
						Block block = chunk.getBlock(x, y, z);
	                    
	                    if(!block.getType().equals(Material.AIR)){
	                    	continue;
	                    }
	                    
	                    block = block.getRelative(BlockFace.DOWN);
	                    
	                    block.setType(Material.STONE_SLAB2);
					}
                    
					break;
				}
			}
		}
	}
}
