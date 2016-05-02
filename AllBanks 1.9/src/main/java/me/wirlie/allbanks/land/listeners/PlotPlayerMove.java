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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksPlot;
import me.wirlie.allbanks.land.AllBanksWorld;

/**
 * @author josue
 *
 */
public class PlotPlayerMove implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		Location to = e.getTo();
		Player p = e.getPlayer();
		
		if(AllBanksWorld.worldIsAllBanksWorld(to.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(to.getWorld().getName());
			
			if(abw.locationIsPlot(to.getBlockX(), to.getBlockZ())){
				AllBanksPlot plot = abw.getPlot(to.getBlockX(), to.getBlockZ());
				
				if(plot.hasOwner()){
					if(plot.getPlotConfiguration().getDenyPlayers().contains(p.getName().toLowerCase())
							|| !plot.getPlotConfiguration().allowEntry() && !plot.getPlotConfiguration().getFriends().contains(p.getName().toLowerCase()) && !plot.getOwnerName().equals(p.getName().toLowerCase())){
						//denegar
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_ALLOW_TO_ENTRY, true);
					}
				}
			}
		}
	}
	
	@EventHandler()
	public void enderPearlThrown(PlayerTeleportEvent e) {
		Location to = e.getTo();
		Player p = e.getPlayer();
		
		if(AllBanksWorld.worldIsAllBanksWorld(to.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(to.getWorld().getName());
			
			if(abw.locationIsPlot(to.getBlockX(), to.getBlockZ())){
				AllBanksPlot plot = abw.getPlot(to.getBlockX(), to.getBlockZ());
				
				if(plot.hasOwner()){
					if(plot.getPlotConfiguration().getDenyPlayers().contains(p.getName().toLowerCase())
							|| !plot.getPlotConfiguration().allowEntry() && !plot.getPlotConfiguration().getFriends().contains(p.getName().toLowerCase()) && !plot.getOwnerName().equals(p.getName().toLowerCase())){
						//denegar
						e.setCancelled(true);
						Translation.getAndSendMessage(p, StringsID.PLOT_NOT_ALLOW_TO_ENTRY, true);
					}
				}
			}
		}
	}

}
