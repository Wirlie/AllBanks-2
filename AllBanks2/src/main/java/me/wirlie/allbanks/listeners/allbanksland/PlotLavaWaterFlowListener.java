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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;

/**
 * @author josue
 *
 */
public class PlotLavaWaterFlowListener implements Listener{
	@EventHandler
	public void waterLavaFlow(BlockFromToEvent e){
		Location loc = e.getToBlock().getLocation();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				e.setCancelled(true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				e.setCancelled(true);
				return;
			}
			
			//Comprobar configuración
			if(!plot.getPlotConfiguration().lavaFlow() && e.getBlock().getType().equals(Material.STATIONARY_LAVA)){
				e.setCancelled(true);
			}
			
			if(!plot.getPlotConfiguration().waterFlow() && e.getBlock().getType().equals(Material.STATIONARY_WATER)){
				e.setCancelled(true);
			}
			
		}
	}
}
