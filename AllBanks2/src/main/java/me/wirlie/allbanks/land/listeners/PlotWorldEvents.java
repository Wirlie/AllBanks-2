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
package me.wirlie.allbanks.land.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;

import me.wirlie.allbanks.land.AllBanksPlot;
import me.wirlie.allbanks.land.AllBanksWorld;
import me.wirlie.allbanks.land.WorldConfiguration;

/**
 * @author josue
 *
 */
public class PlotWorldEvents implements Listener {

	@EventHandler(ignoreCancelled = false)
	public void breaks(BlockBreakEvent e){
		System.out.println("BREAK 2");
	}
	
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
							&& !plot.getPlotConfiguration().getFriends().contains(p.getName().toLowerCase()))
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
	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent e){
		Location loc = e.getLocation();
		
		boolean hostil = false;
		boolean animal = false;
		boolean neutral = false;
		
		if(e.getEntityType().equals(EntityType.COW) ||
				e.getEntityType().equals(EntityType.CHICKEN) ||
				e.getEntityType().equals(EntityType.OCELOT) ||
				e.getEntityType().equals(EntityType.WOLF) ||
				e.getEntityType().equals(EntityType.SHEEP) ||
				e.getEntityType().equals(EntityType.PIG))
		{
			animal = true;
		}else if(e.getEntityType().equals(EntityType.SNOWMAN) ||
				e.getEntityType().equals(EntityType.IRON_GOLEM) ||
				e.getEntityType().equals(EntityType.VILLAGER)){
			neutral = true;
		}else{
			hostil = true;
		}
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			WorldConfiguration wcfg = new WorldConfiguration(abw.getID());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				if(!wcfg.animalSpawn() && animal || !wcfg.mobSpawn() && hostil || neutral){
					e.setCancelled(true);
					return;
				}
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				if(!wcfg.animalSpawn() && animal || !wcfg.mobSpawn() && hostil || neutral){
					e.setCancelled(true);
					return;
				}
			}else{
				if(hostil || !wcfg.animalSpawn() && animal){
					e.setCancelled(true);
					return;
				}
			}
			
		}
	}
	
}
