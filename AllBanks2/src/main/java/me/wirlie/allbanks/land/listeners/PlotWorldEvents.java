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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksPlot;
import me.wirlie.allbanks.land.AllBanksWorld;
import me.wirlie.allbanks.land.WorldConfiguration;

/**
 * @author josue
 *
 */
public class PlotWorldEvents implements Listener {
	
	public boolean isAnimal(Entity e){
		if(e.getType().equals(EntityType.COW) ||
				e.getType().equals(EntityType.CHICKEN) ||
				e.getType().equals(EntityType.PIG) ||
				e.getType().equals(EntityType.SHEEP) ||
				e.getType().equals(EntityType.OCELOT) ||
				e.getType().equals(EntityType.WOLF) ||
				e.getType().equals(EntityType.HORSE))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isNeutral(Entity e){
		if(e.getType().equals(EntityType.SNOWMAN) ||
				e.getType().equals(EntityType.IRON_GOLEM) ||
				e.getType().equals(EntityType.VILLAGER))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isHostil(Entity e){
		if(!isAnimal(e) && !isNeutral(e)){
			return true;
		}
		
		return false;
	}
	
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

	@EventHandler
	public void bucketFill(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		Location loc = e.getBlockClicked().getLocation();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
			if(!plot.havePermissions(p.getName())){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
			}
			
		}
	}
	
	@EventHandler
	public void bucketEmpty(PlayerBucketEmptyEvent e){
		Player p = e.getPlayer();
		Block clickedBlock = e.getBlockClicked();
		Block bukketBlock = 
				(e.getBlockFace().equals(BlockFace.NORTH)) ? clickedBlock.getRelative(BlockFace.NORTH) :
					((e.getBlockFace().equals(BlockFace.SOUTH)) ? clickedBlock.getRelative(BlockFace.SOUTH) : 
						((e.getBlockFace().equals(BlockFace.WEST)) ? clickedBlock.getRelative(BlockFace.WEST) : 
							((e.getBlockFace().equals(BlockFace.EAST)) ? clickedBlock.getRelative(BlockFace.EAST) : 
								((e.getBlockFace().equals(BlockFace.UP)) ? clickedBlock.getRelative(BlockFace.UP) : clickedBlock.getRelative(BlockFace.DOWN))
							)
						)
					);
	
		Location loc = bukketBlock.getLocation();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
			if(!plot.havePermissions(p.getName())){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
			}
			
		}
	}

	@EventHandler
	public void entityDamage(EntityDamageEvent event){
		
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			Entity damager = e.getDamager();
			
			if(damager instanceof Player){
				Player p = (Player) damager;
				Entity ent = e.getEntity();
				Location loc = ent.getLocation();
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						//Todas las entidades fuera de un plot pueden ser atacables.
						return;
					}
					
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(!plot.hasOwner()){
						//Todas las entidades que estén en un plot que no tenga dueño pueden ser atacables
						return;
					}
					
					if(!plot.havePermissions(p.getName()) && !isHostil(ent)){
						//No tiene derecho de hacer eso para atacar entidades no hostiles
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
			}
		}
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
		Entity ent = e.getEntity();
		
		boolean animal = isAnimal(ent);
		boolean hostil = isHostil(ent);
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			WorldConfiguration wcfg = new WorldConfiguration(abw.getID());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				if(!wcfg.animalSpawn() && animal || !wcfg.mobSpawn() && hostil){
					e.setCancelled(true);
					return;
				}
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				if(!wcfg.animalSpawn() && animal || !wcfg.mobSpawn() && hostil){
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
