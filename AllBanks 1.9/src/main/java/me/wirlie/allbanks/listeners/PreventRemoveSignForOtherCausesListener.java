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
package me.wirlie.allbanks.listeners;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.wirlie.allbanks.Banks;

/**
 * @author Wirlie
 *
 */
public class PreventRemoveSignForOtherCausesListener implements Listener {
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onExplosionHappens(EntityExplodeEvent  e){
		Iterator<Block> iter = e.blockList().iterator();
		
		iterateBlocks:
		while(iter.hasNext()){
			Block b = iter.next();
			
			//Prevenir que un letrero, o un bloque que contiene un letrero de AllBanks sea destruido en una explosión
			if(b.getType() == Material.WALL_SIGN){
				if(Banks.signIsAllBanksSign((Sign) b.getState())){
					iter.remove();
				}
			}else if(b.getType() == Material.CHEST){
				
				//Intentar localizar el letrero de AllBanks
				Block tryForSign = null;
				
				if(b.getRelative(BlockFace.UP).getType().equals(Material.WALL_SIGN) && Banks.signIsAllBanksSign((Sign) b.getRelative(BlockFace.UP).getState())){
					tryForSign = b.getRelative(BlockFace.UP);
				}else{
					//Buscar cofre relativo en caso de ser un cofre doble
					for(int i = 0; i < 4; i++){
						Block testForChest = (i == 0) ? b.getRelative(BlockFace.NORTH) : ((i == 1) ? b.getRelative(BlockFace.SOUTH) : ((i == 2) ? b.getRelative(BlockFace.WEST) : b.getRelative(BlockFace.EAST)));
						
						if(testForChest.getType().equals(Material.CHEST)){
							
							Block tryForSign2 = testForChest.getRelative(BlockFace.UP);
							
							if(tryForSign2.getType().equals(Material.WALL_SIGN) && Banks.signIsAllBanksSign((Sign) tryForSign2.getState())){
								tryForSign = tryForSign2;
								break;
							}else{
								if(Banks.blockContainsAllBanksSign(b)){
									iter.remove();
								}
								continue iterateBlocks;
							}
						}
					}
				}
				
				if(tryForSign == null) continue;
				
				if(Banks.signIsAllBanksSign((Sign) tryForSign.getState())){
					iter.remove();
				}
			}else{
				if(Banks.blockContainsAllBanksSign(b)){
					iter.remove();
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void preventEntityGrief(EntityChangeBlockEvent e){
		Block b = e.getBlock();
		
		if(b.getType().equals(Material.WALL_SIGN)){
			Sign s = (Sign) b.getState();
			
			//Probablemente nunca vaya a suceder un evento de estos a causa de una entidad
			if(Banks.signIsAllBanksSign(s)){
				e.setCancelled(true);
			}
		}else{
			if(Banks.blockContainsAllBanksSign(b)){
				e.setCancelled(true);
			}
		}
	}
}
