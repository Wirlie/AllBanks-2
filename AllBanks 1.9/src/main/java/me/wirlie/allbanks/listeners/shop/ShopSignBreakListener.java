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
package me.wirlie.allbanks.listeners.shop;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Shops;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.hooks.HookManager.ResidenceHook;
import me.wirlie.allbanks.hooks.HookManager.TownyHook;
import me.wirlie.allbanks.hooks.HookManager.WorldGuardHook;
import me.wirlie.allbanks.hooks.ResidenceFunctions;
import me.wirlie.allbanks.hooks.TownyFunctions;
import me.wirlie.allbanks.hooks.WorldGuardFunctions;
import me.wirlie.allbanks.util.ChatUtil;
import me.wirlie.allbanks.util.DataBaseUtil;
import me.wirlie.allbanks.util.FakeItemManager;
import me.wirlie.allbanks.util.InteractiveUtil;
import me.wirlie.allbanks.util.InteractiveUtil.SoundType;
import me.wirlie.allbanks.util.Util;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ShopSignBreakListener implements Listener {
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerBreakSign(BlockBreakEvent e) {
		
		Block b = e.getBlock();
		Player p = e.getPlayer();
		
		if(b.getType().equals(Material.WALL_SIGN)) {
			Sign sign = (Sign) b.getState();
			
			if(sign.getLine(0).equalsIgnoreCase(Shops.HEADER_FORMAT)) {
				//Ok, se trata de un letrero de tienda, está registrado?
				if(!Banks.signIsRegistered(sign.getLocation())) {
					if(DataBaseUtil.databaseIsLocked()) {
						//Problema de la base de datos
						Translation.getAndSendMessage(p, StringsID.DATABASE_IS_LOCKED_PLEASE_RESTART_SERVER, true);
						InteractiveUtil.sendSound(p, SoundType.DENY);
						e.setCancelled(true);
						return;
					}else {
						//Pasar evento
						e.setCancelled(false);
						return;
					}
				}
				
				//Está registrado
				String owner = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER));

				if(owner.equalsIgnoreCase(Shops.ADMIN_TAG)) {
					if(!Util.hasPermission(p, "allbanks.sign.shop.admin")) {
						Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						InteractiveUtil.sendSound(p, SoundType.DENY);
						e.setCancelled(true);
						return;
					}else {
						if(Banks.removeAllBanksSign(sign.getLocation())) {
							Translation.getAndSendMessage(p, StringsID.SHOP_REMOVED, true);
							InteractiveUtil.sendSound(p, SoundType.SUCCESS);

							FakeItemManager.DespawnFakeItemForShop(sign.getLocation());
							e.setCancelled(false);
						}else {
							Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
							e.setCancelled(true);
						}
						return;
					}
				}
				
				//Comprobar si puede romper el letrero de AllBanks.
				boolean canBreakSign = false;
				
				//WorldGuard, comprobar si el letrero de AllBanks está en un terreno de WG
				if(WorldGuardHook.isHooked()){
					if(WorldGuardFunctions.canBreakAllBanksShop(p, b.getLocation())){
						canBreakSign = true;
					}
				}
				
				//Towny, comprobar si el jugador es dueño o tiene permisos en el plot
				if(TownyHook.isHooked()){
					if(TownyFunctions.canBreakAllBanksShop(p, b.getLocation())){
						canBreakSign = true;
					}
				}
				
				//Residence, comprobar si el jugador es dueño o tiene permisos en el plot
				if(ResidenceHook.isHooked()){
					if(ResidenceFunctions.canBreakAllBanksShop(p, b.getLocation())){
						canBreakSign = true;
					}
				}
				
				//Si es el dueño del letrero
				if(owner.equalsIgnoreCase(p.getName())) canBreakSign = true;

				//Está intentando remover su propio letrero?
				if(canBreakSign || Util.hasPermission(p, "allbanks.sign.shop.admin")) {
					if(Banks.removeAllBanksSign(sign.getLocation())) {
						Translation.getAndSendMessage(p, StringsID.SHOP_REMOVED, true);
						InteractiveUtil.sendSound(p, SoundType.SUCCESS);
						FakeItemManager.DespawnFakeItemForShop(sign.getLocation());
					}else {
						Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
						e.setCancelled(true);
					}
				} else {
					Translation.getAndSendMessage(p, StringsID.SHOP_NO_YOUR_OWN, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
}
