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

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Shops;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.util.ChatUtil;
import me.wirlie.allbanks.util.DataBaseUtil;
import me.wirlie.allbanks.util.InteractiveUtil;
import me.wirlie.allbanks.util.InteractiveUtil.SoundType;
import me.wirlie.allbanks.util.ItemNameUtil;
import me.wirlie.allbanks.util.ShopUtil;
import me.wirlie.allbanks.util.Util;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ShopSignInteractListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onSignInteract(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		
		if(b == null) return;
		if(!b.getType().equals(Material.WALL_SIGN)) return;
		
		Sign sign = (Sign) b.getState();
		
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(sign.getLine(Shops.LINE_HEADER).equalsIgnoreCase(Shops.HEADER_FORMAT)) {
				//Comprobar si el letrero está registrado
				if(!Banks.signIsRegistered(sign.getLocation())) {
					if(DataBaseUtil.databaseIsLocked()) {
						Translation.getAndSendMessage(p, StringsID.DATABASE_IS_LOCKED_PLEASE_RESTART_SERVER, true);
						e.setCancelled(true);
						return;
					}
					
					Translation.getAndSendMessage(p, StringsID.BANK_NOT_REGISTERED_ON_ALLBANKS, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return;
				}
				
				String owner = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER));
				
				//La última línea tiene un ???
				if(ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_ITEM)).equalsIgnoreCase("???")) {
					if(owner.equalsIgnoreCase(p.getName()) || owner.equalsIgnoreCase(Shops.ADMIN_TAG) && Util.hasPermission(p, "allbanks.sign.shop.admin")) {
						Translation.getAndSendMessage(p, StringsID.SHOP_CONFIGURE_NEEDED, true);
					} else {
						Translation.getAndSendMessage(p, StringsID.SHOP_IS_NOT_CONFIGURED, true);
					}
					InteractiveUtil.sendSound(p, SoundType.DENY);
					e.setCancelled(true);
					return;
				} else {
					//Bien, el banco se encuentra configurado adecuadamente.
					
					//¿Está tratando de usar una tienda de sí mismo?
					if(false && ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER)).equalsIgnoreCase(p.getName())) {
						Translation.getAndSendMessage(p, StringsID.SHOP_CANNOT_USE_YOUR_SHOP, true);
						InteractiveUtil.sendSound(p, SoundType.DENY);
						return;
					}
					
					//Bien, está usando la tienda de alguien más. (SELL)
					
					//No tiene la etiqueta S:X
					if(!ShopUtil.signSupportBuyAction(sign.getLine(Shops.LINE_PRICE))) {
						Translation.getAndSendMessage(p, StringsID.SHOP_NOT_SUPPORT_BUY_ACTION, true);
						return;
					}
					
					//TODO COMPRAR OBJETO
					//Bien, vamos a COMPRAR el objeto al jugador.
					//ShopUtil.playerHaveItemsInYourInventory(sign.getLine(Shops.LINE_ITEM)){
						
					//}
				}
			}
		}else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if(sign.getLine(Shops.LINE_HEADER).equalsIgnoreCase(Shops.HEADER_FORMAT)) {
				if(ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_ITEM)).equalsIgnoreCase("???")) {
					
					String owner = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER));
					
					if(owner.equalsIgnoreCase(p.getName()) || owner.equalsIgnoreCase(Shops.ADMIN_TAG) && Util.hasPermission(p, "allbanks.sign.shop.admin")) {
						ItemStack item = p.getInventory().getItemInMainHand();
						
						if(!item.getType().equals(Material.AIR)) {
							
							if(item.getDurability() > 0) {
								sign.setLine(Shops.LINE_ITEM, ChatColor.DARK_AQUA + ItemNameUtil.getItemName(item) + ":" + item.getDurability());
							} else {
								sign.setLine(Shops.LINE_ITEM, ChatColor.DARK_AQUA + ItemNameUtil.getItemName(item));
							}
							
							sign.update();
							
							Translation.getAndSendMessage(p, StringsID.SHOP_CONFIGURATION_SUCCESS, true);
							InteractiveUtil.sendSound(p, SoundType.SUCCESS);
							e.setCancelled(true);
						}
					}
				} else {
					//Está usando el creativo, y si usa el clic izquierdo removerá el letrero por lo que no tiene caso procesar este evento.
					if(p.getGameMode().equals(GameMode.CREATIVE)) return;
					
					//¿Está tratando de usar una tienda de sí mismo?
					if(false && ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER)).equalsIgnoreCase(p.getName())) {
						Translation.getAndSendMessage(p, StringsID.SHOP_CANNOT_USE_YOUR_SHOP, true);
						InteractiveUtil.sendSound(p, SoundType.DENY);
						return;
					}
					
					//Bien, está usando la tienda de alguien más. (BUY)
					
					//No tiene la etiqueta B:X
					if(!ShopUtil.signSupportSellAction(sign.getLine(Shops.LINE_PRICE))) {
						Translation.getAndSendMessage(p, StringsID.SHOP_NOT_SUPPORT_SELL_ACTION, true);
						return;
					}
					
					//Bien, vamos a VENDER el objeto al jugador.
					//TODO VENDER OBJETO
				}
			}
		}
	}
	
}
