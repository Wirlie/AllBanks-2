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
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Banks.AllBanksAction;
import me.wirlie.allbanks.Banks.BankType;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.Util;
import me.wirlie.allbanks.data.BankSession;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class SignBreakListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBreakBank(BlockBreakEvent e){
		if(e.getBlock().getType().equals(Material.WALL_SIGN)){
			Sign s = (Sign) e.getBlock().getState();
			
			if(Util.ChatFormatUtil.removeChatFormat(s.getLine(0)).equalsIgnoreCase("AllBanks")){
				//Bien, es un letrero de AB
				if(Banks.signIsRegistered(s.getLocation())){
					//Bien, se trata de un banco registrado
					
					BankType btype = BankType.getByString(Util.ChatFormatUtil.removeChatFormat(s.getLine(1)));
					
					if(Banks.playerHasPermissions(e.getPlayer(), AllBanksAction.DESTROY_SIGN, btype)){
						Banks.removeSign(s.getLocation());
						Translation.getAndSendMessage(e.getPlayer(), StringsID.BANK_REMOVED, true);
						//Cerrar sesión
						BankSession.closeSession(e.getPlayer());
						e.setCancelled(false);
					}else{
						//sin permisos
						Translation.getAndSendMessage(e.getPlayer(), StringsID.NO_PERMISSIONS_FOR_THIS, true);
						e.setCancelled(true);
					}
					
					
				}else{
					//banco no registrado...
					s.getBlock().breakNaturally();
					e.setCancelled(false);
				}
			}
		}
	}
	
}
