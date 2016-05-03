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

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksPlot;
import me.wirlie.allbanks.land.AllBanksWorld;

/**
 * @author Wirlie
 *
 */
public class PlotBlockBreak implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void playerBlockBreak(BlockBreakEvent e){
		
		Player p = e.getPlayer();
		Block b = e.getBlock();
		World w = b.getWorld();
		
		if(AllBanksWorld.worldIsAllBanksWorld(w.getName().toLowerCase())){
			
			int x = b.getLocation().getBlockX();
			int z = b.getLocation().getBlockZ();
			//Comprobar
			AllBanksWorld abw = AllBanksWorld.getInstance(w.getName().toLowerCase());
			//Es un camino o el limite del plot
			if(!abw.locationIsPlot(x, z)){
				e.setCancelled(true);
				return;
			}
			
			//Obtener el plot
			AllBanksPlot plot = abw.getPlot(x, z);
			
			if(!plot.hasOwner() || !plot.canBuild(p.getName())){
				//No es de nadie o no es el dueño.
				Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				e.setCancelled(true);
				return;
			}
		}
		
	}
	
}
