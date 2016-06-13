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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.utils.Util;

/**
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class PlotPlayerVehicleListener implements Listener {
	
	@EventHandler
	public void rideHorsePig(VehicleEnterEvent e){
		if(e.getVehicle().getType().equals(EntityType.BOAT) || e.getVehicle().getType().equals(EntityType.MINECART) || e.getVehicle().getType().equals(EntityType.HORSE) || e.getVehicle().getType().equals(EntityType.PIG)){
			Entity rider = e.getEntered();
			
			if(rider instanceof Player){
				Player p = (Player) rider;
				Location loc = e.getVehicle().getLocation();
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						//Si esta permitido montar en la calle.
						return;
					}
					
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(!plot.hasOwner()){
						//Permitido montar si el plot no tiene dueño.
						return;
					}
					
					if(!plot.havePermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void vehicleCollision(VehicleEntityCollisionEvent e){
		
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			Entity v = e.getVehicle();
			Location loc = v.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(plot.hasOwner()){
						if(!plot.havePermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}
				}
				
			}
		}
	}
	
	@EventHandler
	public void vehicleDamage(VehicleDamageEvent e){
		Entity damager = e.getAttacker();
		
		if(damager instanceof Player){
			Player p = (Player) damager;
			Entity ent = e.getVehicle();
			Location loc = ent.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					if(!abw.hasAdminPermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					}
				}else{
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(plot.hasOwner()){
						if(!plot.havePermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							return;
						}
					}else{
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							return;
						}
					}
				}
			}
		}else if(damager instanceof Arrow){
			Arrow arrow = (Arrow) damager;
			
			if(arrow.getShooter() instanceof Player){
				Player p = (Player) arrow.getShooter();
				Entity ent = e.getVehicle();
				Location loc = ent.getLocation();
				
				if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
					
					if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
						if(!abw.hasAdminPermissions(p)){
							e.setCancelled(true);
							Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						}
					}else{
						AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
						
						if(plot.hasOwner()){
							if(!plot.havePermissions(p) && !Util.entityIsHostil(ent)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}else{
							if(!abw.hasAdminPermissions(p)){
								e.setCancelled(true);
								Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
								return;
							}
						}
					}
				}
			}
		}
	}
}
