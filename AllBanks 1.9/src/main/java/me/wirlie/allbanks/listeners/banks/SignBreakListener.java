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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Banks.ABSignAction;
import me.wirlie.allbanks.Banks.ABSignType;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.tempdata.BankSession;
import me.wirlie.allbanks.utils.ChatUtil;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.FakeItemManager;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.ShopUtil;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * Detectar cuando un letrero de AllBanks es roto, y procesarlo.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class SignBreakListener implements Listener {

	public SignBreakListener(){
		AllBanksLogger.info("SignBreakListener");
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerBreakBank(BlockBreakEvent e){
		
		Block b = e.getBlock();
		Player p = e.getPlayer();
		
		if(b.getType().equals(Material.WALL_SIGN)){
			Sign s = (Sign) b.getState();
			Location signLoc = s.getLocation();
			
			if(ChatUtil.removeChatFormat(s.getLine(0)).equalsIgnoreCase("AllBanks")){
				//Bien, es un letrero de AB
				if(Banks.signIsRegistered(s.getLocation())){
					//Bien, se trata de un banco registrado
					
					ABSignType btype = ABSignType.getSignTypeByShortName(ChatUtil.removeChatFormat(s.getLine(1)));
					
					if(Banks.playerHasPermissionForPerformAction(p, ABSignAction.DESTROY_SIGN, btype)){
						if(btype != null){
							if(Banks.removeSignFromAllBanks(s.getLocation())){
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
		}else{
			//Comprobar si ese bloque removido tiene otros letreros de AllBanks
			if(Banks.blockIsSupportForABSigns(b)){
				//Okey, remover
				List<Sign> retrieveSigns = Banks.getABSignsBySupportBlock(b);
				
				List<Sign> doRemoveNormalBank = new ArrayList<Sign>();
				List<Sign> doRemoveShop = new ArrayList<Sign>();
				boolean deny = false;
				
				for(Sign s : retrieveSigns){
					if(ShopUtil.isShopSign(s)){
						//Permisos
						if(!ShopUtil.playerHasPermissionForRemove(p, s)){
							deny = true;
						}else{
							doRemoveShop.add(s);
						}
					}else{
						ABSignType btype = Banks.getABSignTypeBySign(s);
						if(btype.equals(ABSignType.DEFAULT)) continue;
						
						//Permisos
						if(!Banks.playerHasPermissionForPerformAction(p, ABSignAction.DESTROY_SIGN, btype)){
							deny = true;
						}else{
							doRemoveNormalBank.add(s);
						}
					}
				}
				
				if(deny){
					for(Sign s : doRemoveNormalBank){
						Banks.removeSignFromAllBanks(s.getLocation());
						s.getBlock().breakNaturally();
					}
					
					for(Sign s : doRemoveShop){
						Banks.removeSignFromAllBanks(s.getLocation());
						FakeItemManager.DespawnFakeItemForShop(s.getLocation());
						s.getBlock().breakNaturally();
					}
					
					e.setCancelled(true);
				}else{
					for(Sign s : doRemoveNormalBank){
						Banks.removeSignFromAllBanks(s.getLocation());
					}
					
					for(Sign s : doRemoveShop){
						Banks.removeSignFromAllBanks(s.getLocation());
						FakeItemManager.DespawnFakeItemForShop(s.getLocation());
					}
				}
			}
		}
	}
	
}
