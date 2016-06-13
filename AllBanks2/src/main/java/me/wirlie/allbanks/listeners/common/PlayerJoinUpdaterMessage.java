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
package me.wirlie.allbanks.listeners.common;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;

/**
 * Enviar mensaje de actualización a los jugadores operadores.
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class PlayerJoinUpdaterMessage implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		
		if(AllBanks.updatePending)
			new BukkitRunnable(){
				public void run(){
					if(p.isOp()){
						Translation.getAndSendMessage(p, StringsID.UPDATER_PLEASE_RELOAD_ALLBANKS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + AllBanks.updatePendingVersion), true);
					}
				}
			}.runTaskLater(AllBanks.getInstance(), 20 * 4);
	}
}
