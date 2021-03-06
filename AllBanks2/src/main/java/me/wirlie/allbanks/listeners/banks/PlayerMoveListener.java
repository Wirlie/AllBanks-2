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
package me.wirlie.allbanks.listeners.banks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.wirlie.allbanks.banks.bankdata.BankSession;
import me.wirlie.allbanks.utils.AllBanksLogger;

/**
 * Detectar si un jugador se ha alejado lo suficiente de un letrero.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
@SuppressWarnings("javadoc")
public class PlayerMoveListener implements Listener {
	
	public PlayerMoveListener(){
		AllBanksLogger.info("PlayerMoveListener");
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent e){
		
		Player p = e.getPlayer();
		
		if(BankSession.checkSession(p)){
			//Bien, está en una sesión.
			BankSession bs = BankSession.getSession(p);
			Location signLoc = bs.getSign().getLocation();
			
			if(signLoc.distance(p.getLocation()) > 4){
				bs.closeSession();
			}
		}
		
	}
	
}
