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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Shops;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.util.InteractiveUtil;
import me.wirlie.allbanks.util.ItemNameUtil;
import me.wirlie.allbanks.util.InteractiveUtil.SoundType;
import me.wirlie.allbanks.util.ShopUtil;
import me.wirlie.allbanks.util.Util;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ShopSignChangeListener implements Listener {
	@EventHandler
	public void onSignChange(final SignChangeEvent e){
		String[] lines = e.getLines();
		
		if(lines[0].equalsIgnoreCase("AllBanks Shop") || lines[0].equalsIgnoreCase("AllBanksShop") || lines[0].equalsIgnoreCase("AB Shop") || lines[0].equalsIgnoreCase("ABShop")) {
			//Bien antes que nada, el usuario tiene permisos?
			final Player p = e.getPlayer();
			
			if(!Util.hasPermission(p, "allbanks.sign.shop.new")) {
				Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return;
			}
			
			boolean isAdminShop = false;
			
			if(lines[1].equalsIgnoreCase("admin")) {
				if(!Util.hasPermission(p, "allbanks.sign.shop.admin")) {
					Translation.getAndSendMessage(p, StringsID.SHOP_NO_PERMISSIONS_FOR_ADMIN_SHOP, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					e.getBlock().breakNaturally();
					return;
				}else {
					isAdminShop = true;
				}
			}
			
			if(!isAdminShop) lines[1] = e.getPlayer().getName();
			
			//Validar la línea de precio:
			if(!ShopUtil.validatePriceLine(lines[2])) {
				Translation.getAndSendMessage(p, StringsID.SHOP_PRICE_LINE_NOT_VALID, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				e.getBlock().breakNaturally();
				return;
			}
			
			//Validar el nombre de item
			if(ItemNameUtil.getItemByShortName(lines[3]) == null) {
				//No pertenece a un nombre válido, pero puede ser configurado
				lines[3] = "???";
			}
			
			final String[] finalLines = lines;
			
			//Procesar
			new BukkitRunnable() {

				public void run() {
					Shops.makeNewShop(finalLines, e.getBlock(), p);
				}
				
			}.runTaskLater(AllBanks.getInstance(), 20 * 2);
		}
	}
}
