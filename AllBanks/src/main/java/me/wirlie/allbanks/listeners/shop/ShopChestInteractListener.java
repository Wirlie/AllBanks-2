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
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Shops;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.util.DataBaseUtil;
import me.wirlie.allbanks.util.ShopUtil;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ShopChestInteractListener implements Listener {
	
	@EventHandler
	public void onPlayerTryToOpenChest(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		
		if(b == null) return;
		if(!b.getType().equals(Material.CHEST)) return;
		
		Block trySign = b.getRelative(BlockFace.UP);
		
		if(!trySign.getType().equals(Material.WALL_SIGN)) return;
		
		Sign sign = (Sign) trySign.getState();
		
		if(sign.getLine(Shops.LINE_HEADER).equalsIgnoreCase(Shops.HEADER_FORMAT)) {
			//Banco de AllBanks SHOP
			
			if(!Banks.signIsRegistered(sign.getLocation())) {
				if(DataBaseUtil.databaseIsLocked()) {
					//Si hay un error SQLite evitamos el evento por que no sabemos si el banco está registrado
					Translation.getAndSendMessage(p, StringsID.DATABASE_IS_LOCKED_PLEASE_RESTART_SERVER, true);
					e.setCancelled(true);
					return;
				}
				
				//Si el banco no está registrado, no nos interesa.
				return;
			}
			
			//Bien, comprobar si el cofre pertenece al jugador
			if(!ShopUtil.getOwner(sign).getName().equalsIgnoreCase(p.getName())) {
				e.setCancelled(true);
				return;
			}
		}
		
	}
}
