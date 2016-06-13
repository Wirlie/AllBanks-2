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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;

/**
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class PlotPvPListener implements Listener {
	@EventHandler
	public void onPlayerHitPlayer(EntityDamageByEntityEvent e){
		if((e.getDamager() instanceof Player) && (e.getEntity() instanceof Player) || (e.getDamager() instanceof Arrow) && (e.getEntity() instanceof Player)){
			
			//Jugador a jugador.
			Player attacker = null;
			
			if(e.getDamager() instanceof Arrow){
				Arrow arrow = (Arrow) e.getDamager();
				if(!(arrow.getShooter() instanceof Player)){
					return;
				}
				
				attacker = (Player) arrow.getShooter();
			}else{
				attacker = (Player) e.getDamager();
			}
			
			Player victim = (Player) e.getEntity();
			
			Location locAttacker = attacker.getLocation();
			Location locVictim = victim.getLocation();
			
			if(locAttacker.getWorld().equals(locVictim.getWorld())){
				if(AllBanksWorld.worldIsAllBanksWorld(locVictim.getWorld().getName())){
					AllBanksWorld abw = AllBanksWorld.getInstance(locVictim.getWorld().getName());
					
					//Alguno de ellos 2 está en la calle
					if(!abw.locationIsPlot(locVictim.getBlockX(), locVictim.getBlockZ()) || !abw.locationIsPlot(locAttacker.getBlockX(), locAttacker.getBlockZ())){
						e.setCancelled(true);
						Translation.getAndSendMessage(attacker, StringsID.PLOT_PVP_DISABLED, true);
						return;
					}
					
					AllBanksPlot plotAttacker = abw.getPlot(locAttacker.getBlockX(), locAttacker.getBlockZ());
					AllBanksPlot plotVictim = abw.getPlot(locVictim.getBlockX(), locVictim.getBlockZ());
					
					//Alguno de ellos 2 está en una parcela sin dueño
					if(!plotAttacker.hasOwner() || !plotVictim.hasOwner()){
						e.setCancelled(true);
						Translation.getAndSendMessage(attacker, StringsID.PLOT_PVP_DISABLED, true);
						return;
					}
					
					//Alguno de ellos 2 se encuentra en una parcela sin pvp.
					if(!plotAttacker.getPlotConfiguration().pvp() || !plotVictim.getPlotConfiguration().pvp()){
						e.setCancelled(true);
						Translation.getAndSendMessage(attacker, StringsID.PLOT_PVP_DISABLED, true);
						return;
					}
				}
			}
		}
	}
}
