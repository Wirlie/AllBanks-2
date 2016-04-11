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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Banks.PlayerAction;
import me.wirlie.allbanks.Banks.BankType;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.data.BankSession;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.util.ChatUtil;
import me.wirlie.allbanks.util.DataBaseUtil;
import me.wirlie.allbanks.util.InteractiveUtil;
import me.wirlie.allbanks.util.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class SignBreakListener implements Listener {

	public SignBreakListener(){
		AllBanksLogger.info("SignBreakListener");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBreakBank(BlockBreakEvent e){
		if(e.getBlock().getType().equals(Material.WALL_SIGN)){
			Sign s = (Sign) e.getBlock().getState();
			Location signLoc = s.getLocation();
			Player p = e.getPlayer();
			
			if(ChatUtil.removeChatFormat(s.getLine(0)).equalsIgnoreCase("AllBanks")){
				//Bien, es un letrero de AB
				if(Banks.signIsRegistered(s.getLocation())){
					//Bien, se trata de un banco registrado
					
					BankType btype = BankType.getTypeByString(ChatUtil.removeChatFormat(s.getLine(1)));
					
					if(Banks.playerHasPermissions(p, PlayerAction.DESTROY_SIGN, btype)){
						if(btype != null){
							if(Banks.removeAllBanksSign(s.getLocation())){
								Translation.getAndSendMessage(p, StringsID.BANK_REMOVED, true);
								//Cerrar sesión
								if(BankSession.checkSession(p))
									BankSession.closeSession(p);
								e.setCancelled(false);
							}else{
								if(DataBaseUtil.databaseIsLocked()){
									DataBaseUtil.sendDatabaseLockedMessage(p);
								}else{
									Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
								}
								
								e.setCancelled(true);
							}
						}
					}else{
						//sin permisos
						Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						e.setCancelled(true);
						AllBanksLogger.warning("BREAK-BANK: Player " + p.getName() + " (" + p.getDisplayName() + ") has tried to destroy a bank sign. (Deny cause: permissions)(Location: world:" + signLoc.getWorld().getName() + ", x:" + signLoc.getX() + ", y:" + signLoc.getY() + ", z:" + signLoc.getZ() + ").");
						InteractiveUtil.sendSound(p, SoundType.DENY);
					}
					
					
				}else{
					
					if(DataBaseUtil.databaseIsLocked()){
						DataBaseUtil.sendDatabaseLockedMessage(e.getPlayer());
						e.setCancelled(true);
						return;
					}
					
					//banco no registrado...
					s.getBlock().breakNaturally();
					e.setCancelled(false);
				}
			}
		}
	}
	
}
