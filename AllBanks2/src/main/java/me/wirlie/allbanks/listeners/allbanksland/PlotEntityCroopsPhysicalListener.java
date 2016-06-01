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
package me.wirlie.allbanks.listeners.allbanksland;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;

/**
 * @author josue
 *
 */
public class PlotEntityCroopsPhysicalListener implements Listener {

	@EventHandler
	public void preventCropGrief(PlayerInteractEvent e){
		if(e.getAction().equals(Action.PHYSICAL)){
			if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.SOIL){
				Location loc = e.getClickedBlock().getLocation();
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						e.setCancelled(true);
						return;
					}
					
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(!plot.hasOwner()){
						//Si esta permitido hacer esto en un plot sin dueño
						return;
					}
					
					if(!plot.havePermissions(e.getPlayer())){
						e.setCancelled(true);
						Translation.getAndSendMessage(e.getPlayer(), StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void preventCroopGrief(EntityChangeBlockEvent e){
		if(e.getBlock().getType().equals(Material.SOIL)){
			Location loc = e.getBlock().getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					e.setCancelled(true);
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(plot.hasOwner()){
					if(!e.getEntity().getType().equals(EntityType.PLAYER)) e.setCancelled(true);
					return;
				}
			}
		}
		
		if(e.getEntity().getType().equals(EntityType.WITHER)){
			Location loc = e.getBlock().getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				e.setCancelled(true);
			}
		}
	}
}
