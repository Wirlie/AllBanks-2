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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksPlot;
import me.wirlie.allbanks.land.AllBanksWorld;

/**
 * @author Wirlie
 *
 */
public class PlotBlockPlace implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e){
		Block b = e.getBlock();
		Location bl = b.getLocation();
		Player p = e.getPlayer();
		
		if(AllBanksWorld.worldIsAllBanksWorld(bl.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(bl.getWorld().getName());
			
			if(!abw.locationIsPlot(bl.getBlockX(), bl.getBlockZ())){
				e.setCancelled(true);
				return;
			}
			
			AllBanksPlot plot = abw.getPlot(bl.getBlockX(), bl.getBlockZ());
			
			if(!plot.hasOwner() || !plot.havePermissions(p.getName())){
				e.setCancelled(true);
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return;
			}
			
		}
	}
}
