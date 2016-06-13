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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.allbanksland.WorldConfiguration;
import me.wirlie.allbanks.utils.Util;

/**
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class PlotEntityListener implements Listener{
	
	@EventHandler
	public void entityDamage(EntityDamageEvent event){
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			Entity damager = e.getDamager();
			
			if(damager instanceof Player){
				Player p = (Player) damager;
				Entity ent = e.getEntity();
				Location loc = ent.getLocation();
				
				//Esto se procesa en otro listener
				if(ent.getType().equals(EntityType.PLAYER)) return;
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(ent instanceof Minecart || ent instanceof ArmorStand || ent instanceof Painting || ent instanceof ItemFrame){
							if(!abw.hasAdminPermissions(p)){
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								e.setCancelled(true);
							}
						}
						return;
					}
					
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(!plot.hasOwner()){
						if(ent instanceof Minecart || ent instanceof ArmorStand || ent instanceof Painting || ent instanceof ItemFrame){
							if(!abw.hasAdminPermissions(p)){
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								e.setCancelled(true);
							}
						}
						return;
					}
					
					if(!plot.havePermissions(p) && !Util.entityIsHostil(ent)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
			}else if(damager instanceof Arrow){
				Arrow arrow = (Arrow) damager;
				
				if(arrow.getShooter() instanceof Player){
					Player p = (Player) arrow.getShooter();
					Entity ent = e.getEntity();
					Location loc = ent.getLocation();
					
					if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
						AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
						
						if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
							if(ent instanceof Minecart || ent instanceof ArmorStand || ent instanceof Painting || ent instanceof ItemFrame){
								if(!abw.hasAdminPermissions(p)){
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
									e.setCancelled(true);
								}
							}
							return;
						}
						
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(!plot.hasOwner()){
							if(ent instanceof Minecart || ent instanceof ArmorStand || ent instanceof Painting || ent instanceof ItemFrame){
								if(!abw.hasAdminPermissions(p)){
									Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
									e.setCancelled(true);
								}
							}
							return;
						}
						
						if(!plot.havePermissions(p) && !Util.entityIsHostil(ent)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							return;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent e){
		Entity ent = e.getEntity();
		Location loc = ent.getLocation();
		
		boolean animal = Util.entityIsAnimal(ent);
		boolean hostil = Util.entityIsHostil(ent);
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			WorldConfiguration wcfg = abw.getWorldConfiguration();
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				if(!wcfg.animalSpawn() && animal || !wcfg.mobSpawn() && hostil){
					e.setCancelled(true);
					return;
				}
			}else{
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					if(!wcfg.animalSpawn() && animal || !wcfg.mobSpawn() && hostil){
						e.setCancelled(true);
						return;
					}
				}else{
					if(!wcfg.mobSpawn() && hostil || !plot.getPlotConfiguration().mobs() && hostil || !wcfg.animalSpawn() && animal){
						e.setCancelled(true);
						return;
					}
				}
			}
			
			//Wither
			if(ent.getType().equals(EntityType.WITHER)){
				if(!wcfg.allowWither()){
					e.setCancelled(true);
					return;
				}
			}
			
		}
	}
}
