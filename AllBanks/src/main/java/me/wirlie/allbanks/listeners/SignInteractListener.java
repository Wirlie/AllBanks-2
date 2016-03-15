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
package me.wirlie.allbanks.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.wirlie.allbanks.main.Util;
import me.wirlie.allbanks.data.BankSession;
import me.wirlie.allbanks.main.Banks.BankType;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class SignInteractListener implements Listener {
	
	@EventHandler
	public void onPlayerInteractWithAllBanksSign(PlayerInteractEvent e){
		
		if(e.getClickedBlock() == null) return;
		
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		
		if(b.getType().equals(Material.WALL_SIGN)){
			
			Sign s = (Sign) b.getState();
			
			if(Util.ChatFormatUtil.removeChatFormat(s.getLine(0)).equalsIgnoreCase("AllBanks")){
				//Se puede tratar de un letrero AllBanks.
				String btypeStr = Util.ChatFormatUtil.removeChatFormat(s.getLine(1));
				BankType btype = BankType.getByString(btypeStr);
				
				if(btype != null){
					e.setCancelled(true);
					
					//Bien ya sabemos de que banco se trata.
					if(BankSession.checkSession(p)){
						//¿Ya está usando otro banco?
						return;
					}
					
					//Iniciar nueva sesión
					BankSession.startSession(p, new BankSession(p, (Sign) b.getState(), btype, 0));
				}
			}
		}
		
	}
	
}
