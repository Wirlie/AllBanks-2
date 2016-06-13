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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;

/**
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class PlotFireSpreadListener implements Listener {
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e){
		Location loc = e.getBlock().getLocation();
		
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
			
			if(!plot.getPlotConfiguration().fireSpread()){
				e.setCancelled(true);
				return;
			}
			
		}
	}
	
	@EventHandler
	public void onFireSpread(BlockIgniteEvent e){
		Location loc = e.getBlock().getLocation();

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
			
			if(e.getCause().equals(IgniteCause.FLINT_AND_STEEL)){
				if(e.getIgnitingEntity() instanceof Player){
					Player p = (Player) e.getIgnitingEntity();
					if(!plot.getOwnerName().equalsIgnoreCase(p.getName())
							&& !plot.getPlotConfiguration().getFriends().contains(p.getName().toLowerCase())
							&& !abw.hasAdminPermissions(p))
					{
						e.setCancelled(true);
					}
				}else{
					e.setCancelled(true);
					return;
				}
			}else if(e.getCause().equals(IgniteCause.SPREAD)){
				if(!plot.getPlotConfiguration().fireSpread()){
					e.setCancelled(true);
					return;
				}
			}else{
				//Otra causa, por ejemplo, un blaz
				e.setCancelled(true);
				return;
			}
		}
	}
}
