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
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;

/**
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class PlotLeashUnleashListener implements Listener {
	@EventHandler
	public void preventUnleash(PlayerUnleashEntityEvent e){
		Location loc = e.getEntity().getLocation();
		Player p = e.getPlayer();
		
		if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				if(!abw.hasAdminPermissions(p)) e.setCancelled(true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner()){
				if(!abw.hasAdminPermissions(p)) e.setCancelled(true);
				return;
			}
			
			if(!plot.havePermissions(p)){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
		}
	}
	
	@EventHandler
	public void preventFenceUnleash(HangingBreakByEntityEvent e){
		if(e.getRemover() instanceof Player){
			Player p = (Player) e.getRemover();
			Location loc = e.getEntity().getLocation(); //Localización de la valla con la cuerda sujetada
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					if(!abw.hasAdminPermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					}
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					if(!abw.hasAdminPermissions(p)){
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					}
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
	
	@EventHandler
	public void onPlayerLeash(PlayerLeashEntityEvent e){
		if(e.getLeashHolder() instanceof Player){
			Player p = (Player) e.getLeashHolder();
			Location loc = e.getEntity().getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//Si esta permitido usar esto en la calle.
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					//Si esta permitido usar esto
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
