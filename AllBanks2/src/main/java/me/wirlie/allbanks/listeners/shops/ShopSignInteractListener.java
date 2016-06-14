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
package me.wirlie.allbanks.listeners.shops;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

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

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.PermissionsConstants;
import me.wirlie.allbanks.Shops;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.utils.ChatUtil;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.FakeItemManager;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.InventoryUtil;
import me.wirlie.allbanks.utils.ItemNameUtil;
import me.wirlie.allbanks.utils.ShopUtil;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;
import me.wirlie.allbanks.utils.Util.VersionPackage;
import me.wirlie.allbanks.utils.chatcomposer.BuildChatMessage;
import me.wirlie.allbanks.utils.chatcomposer.TextualComponent;

/**
 * Detectar cuando un jugador interactua con una tienda.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
@SuppressWarnings("javadoc")
public class ShopSignInteractListener implements Listener {
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSignInteract(PlayerInteractEvent e) throws InvocationTargetException, IllegalAccessException {
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
				
				if(!AllBanks.getInstance().getConfig().getBoolean("modules.shop.enable")){
					Translation.getAndSendMessage(p, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksShop"), true);
					e.setCancelled(true);
					return;
				}
				
				String owner = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER));
				boolean isAdminShop = owner.equalsIgnoreCase(Shops.ADMIN_TAG);
				
				//La última línea tiene un ???
				if(ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_ITEM)).equalsIgnoreCase("???")) {
					if(owner.equalsIgnoreCase(p.getName()) || isAdminShop && Util.hasPermission(p, PermissionsConstants.SHOP_ADMIN_PERMISSION)) {
						Translation.getAndSendMessage(p, StringsID.SHOP_CONFIGURE_NEEDED, true);
					} else {
						Translation.getAndSendMessage(p, StringsID.SHOP_IS_NOT_CONFIGURED, true);
					}
					InteractiveUtil.sendSound(p, SoundType.DENY);
					e.setCancelled(true);
					return;
				} else {
					//Bien, el banco se encuentra configurado adecuadamente.
					
					//Bien, está usando la tienda de alguien más. (BUY)
					
					//¿Tiene el cofre?
					if(!ShopUtil.validateNearbyChest(sign.getLocation()) && !isAdminShop) {
						Translation.getAndSendMessage(p, StringsID.SHOP_ERROR_NO_CHEST_FOUND, true);
						return;
					}
					
					//Ok, vamos a mostrar un mensaje con la previsualización si está usando shift
					if(!p.isSneaking()){
						
						//¿Está tratando de usar una tienda de sí mismo?
						if(ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER)).equalsIgnoreCase(p.getName())) {
							Translation.getAndSendMessage(p, StringsID.SHOP_CANNOT_USE_YOUR_SHOP, true);
							InteractiveUtil.sendSound(p, SoundType.DENY);
							return;
						}
						
						//No tiene la etiqueta S:X
						if(!ShopUtil.signSupportBuyAction(sign)) {
							Translation.getAndSendMessage(p, StringsID.SHOP_NOT_SUPPORT_BUY_ACTION, true);
							return;
						}
						
						//Bien, vamos a COMPRAR el objeto al jugador.
						ItemStack shopItem = ShopUtil.getItemStack(sign);
						
						if(!ShopUtil.checkItemForPlayerInventory(p, shopItem, false)){
							if(ShopUtil.itemNeedResolveCustomDurability(shopItem)){
								String customID = ShopUtil.resolveCustomDurabilityIDFor(shopItem);
								Translation.getAndSendMessage(p, StringsID.SHOP_PLAYER_NO_HAVE_THIS_ITEM, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + ItemNameUtil.getItemName(shopItem) + customID), true);
							}else{
								Translation.getAndSendMessage(p, StringsID.SHOP_PLAYER_NO_HAVE_THIS_ITEM, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + ItemNameUtil.getItemName(shopItem) + ((shopItem.getDurability() > 0) ? ":" + shopItem.getDurability() : "")), true);
							}
							return;
						}
						
						//Bien, cuantos objetos tiene.
						int totalItems = ShopUtil.getTotalItemsInventory(p.getInventory(), shopItem);
						int shopTotalItems = shopItem.getAmount();
						//Para una "exactitud", si la división es infinita solo tomaremos 10 digitos decimales con el fin de hacer un poco más exacta la división
						BigDecimal pricePerItem = ShopUtil.getBuyPrice(sign).divide(new BigDecimal(shopTotalItems), 10, RoundingMode.HALF_UP);
						if(totalItems > shopTotalItems) totalItems = shopTotalItems;
	
						//Cuanto espacio hay en el cofre
						int freeSpace = InventoryUtil.getInventoryFreeSpaceForItem(sign, shopItem);
						
						if(isAdminShop) freeSpace = Integer.MAX_VALUE;
	
						if(freeSpace <= 0) {
							Translation.getAndSendMessage(p, StringsID.SHOP_ERROR_SHOPCHEST_NOT_HAVE_SPACE, true);
							return;
						}
						
						if(freeSpace < totalItems) {
							totalItems = freeSpace;
						}
						
						BigDecimal totalCost = pricePerItem.multiply(new BigDecimal(totalItems));
						
						//Bien, ahora vamos a quitarle los objetos al jugador y a pagarle
						if(!isAdminShop && !AllBanks.getEconomy().has(ShopUtil.getOwner(sign), totalCost.doubleValue())) {
							Translation.getAndSendMessage(p, StringsID.SHOP_ERROR_OWNER_OF_SHOP_CANNOT_HAVE_MONEY_FOR_BUY, true);
							return;
						}
						if(isAdminShop || AllBanks.getEconomy().withdrawPlayer(ShopUtil.getOwner(sign), totalCost.doubleValue()).transactionSuccess()) {
							//Quitar objetos
							InventoryUtil.removeItemsFromInventory(p.getInventory(), shopItem, totalItems);
							p.updateInventory();
							//Colocar objetos en el cofre
							if(!isAdminShop) InventoryUtil.putItemsToInventory(sign, shopItem, totalItems);
							//Pagar al jugador
							AllBanks.getEconomy().depositPlayer(p, totalCost.doubleValue());
							//Mensaje
							HashMap<String, String> replaceMap = new HashMap<String, String>();
							replaceMap.put("%1%", String.valueOf(totalItems));
							replaceMap.put("%2%", ItemNameUtil.getItemName(shopItem));
							replaceMap.put("%3%", AllBanks.getEconomy().format(totalCost.doubleValue()));
							
							Translation.getAndSendMessage(p, StringsID.SHOP_SUCCESS_BUY, replaceMap, true);
							
							//Estadísticas
							
							return;
						}
					}else{
						//Está usando shift
						ItemStack item = ShopUtil.getItemStack(sign);
						
						if(Util.resolveNMSVersion().equals(VersionPackage.NMS_1_9_R1)){
							new BuildChatMessage(Translation.get(StringsID.ITEM_PREVIEW, false)[0])
								.then("[ ")
									.color(ChatColor.BLUE)
								.then(TextualComponent.localizedText(Util.getItemCodeOrGetCustomName(org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asNMSCopy(item))))
									.color(Util.convertEnumChatFormatToChatColor(org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asNMSCopy(item).u().e))
									.itemTooltip(item)
								.then(" ]")
									.color(ChatColor.BLUE)
								.then(" [ ")
									.color(ChatColor.BLUE)
								.then(Translation.get(StringsID.ITEM_PREVIEW_BUTTON, false)[0])
									.color(ChatColor.GRAY)
									.tooltip(ChatColor.YELLOW + Translation.get(StringsID.CLICK_HERE_TO_SHOW_PREVIEW, false)[0])
									.command("/ab showPreview " + Util.convertLocationToString(sign.getLocation(), true))
								.then(" ]")
									.color(ChatColor.BLUE)
								.send(p);
						}else if(Util.resolveNMSVersion().equals(VersionPackage.NMS_1_9_R2)){
							new BuildChatMessage(Translation.get(StringsID.ITEM_PREVIEW, false)[0])
							.then("[ ")
								.color(ChatColor.BLUE)
							.then(TextualComponent.localizedText(Util.getItemCodeOrGetCustomName(org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(item))))
								.color(Util.convertEnumChatFormatToChatColor(org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack.asNMSCopy(item).u().e))
								.itemTooltip(item)
							.then(" ]")
								.color(ChatColor.BLUE)
							.then(" [ ")
								.color(ChatColor.BLUE)
							.then(Translation.get(StringsID.ITEM_PREVIEW_BUTTON, false)[0])
								.color(ChatColor.GRAY)
								.tooltip(ChatColor.YELLOW + Translation.get(StringsID.CLICK_HERE_TO_SHOW_PREVIEW, false)[0])
								.command("/ab showPreview " + Util.convertLocationToString(sign.getLocation(), true))
							.then(" ]")
								.color(ChatColor.BLUE)
							.send(p);
					}
					}
				}
			}
		}else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if(sign.getLine(Shops.LINE_HEADER).equalsIgnoreCase(Shops.HEADER_FORMAT)) {
				if(ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_ITEM)).equalsIgnoreCase("???")) {
					
					e.setCancelled(true);
					
					if(!AllBanks.getInstance().getConfig().getBoolean("modules.shop.enable")){
						Translation.getAndSendMessage(p, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksShop"), true);
						e.setCancelled(true);
						return;
					}
					
					String owner = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER));
					
					if(owner.equalsIgnoreCase(p.getName()) || owner.equalsIgnoreCase(Shops.ADMIN_TAG) && Util.hasPermission(p, PermissionsConstants.SHOP_ADMIN_PERMISSION)) {
						ItemStack item = p.getInventory().getItemInMainHand();
						
						if(!item.getType().equals(Material.AIR)) {
							
							if(ShopUtil.itemNeedResolveCustomDurability(item)){
								String customID = ShopUtil.resolveCustomDurabilityIDFor(item);
								sign.setLine(Shops.LINE_ITEM, ChatColor.DARK_AQUA + ItemNameUtil.getItemName(item) + customID);
								
								sign.update();
								
								Translation.getAndSendMessage(p, StringsID.SHOP_CONFIGURATION_SUCCESS, true);
								InteractiveUtil.sendSound(p, SoundType.SUCCESS);
								
								boolean adminShop = false;
								if(ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER)).equalsIgnoreCase(Shops.ADMIN_TAG)){
									adminShop = true;
								}
								
								boolean spawnFakeItemUserShops = AllBanks.getInstance().getConfig().getBoolean("shop.enable-fake-item-for-user-shop", true);
								
								if(adminShop || !adminShop && spawnFakeItemUserShops)
									FakeItemManager.SpawnFakeItemForShop(sign.getLocation());
							}else{
								if(item.getDurability() > 0) {
									sign.setLine(Shops.LINE_ITEM, ChatColor.DARK_AQUA + ItemNameUtil.getItemName(item) + ":" + item.getDurability());
								} else {
									sign.setLine(Shops.LINE_ITEM, ChatColor.DARK_AQUA + ItemNameUtil.getItemName(item));
								}
								
								sign.update();
								
								Translation.getAndSendMessage(p, StringsID.SHOP_CONFIGURATION_SUCCESS, true);
								InteractiveUtil.sendSound(p, SoundType.SUCCESS);
								
								boolean adminShop = false;
								if(ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER)).equalsIgnoreCase(Shops.ADMIN_TAG)){
									adminShop = true;
								}
								
								boolean spawnFakeItemUserShops = AllBanks.getInstance().getConfig().getBoolean("shop.enable-fake-item-for-user-shop", true);
								
								if(adminShop || !adminShop && spawnFakeItemUserShops)
									FakeItemManager.SpawnFakeItemForShop(sign.getLocation());
							}
						}
					}
				} else {
					//Está usando el creativo, y si usa el clic izquierdo removerá el letrero por lo que no tiene caso procesar este evento.
					if(p.getGameMode().equals(GameMode.CREATIVE)) return;
					
					String owner = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER));
					boolean isAdminShop = owner.equalsIgnoreCase(Shops.ADMIN_TAG);
					
					//¿Está tratando de usar una tienda de sí mismo?
					if(ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER)).equalsIgnoreCase(p.getName())) {
						Translation.getAndSendMessage(p, StringsID.SHOP_CANNOT_USE_YOUR_SHOP, true);
						InteractiveUtil.sendSound(p, SoundType.DENY);
						return;
					}
					
					//Bien, está usando la tienda de alguien más. (SELL)
					
					//No tiene la etiqueta S:X
					if(!ShopUtil.signSupportSellAction(sign)) {
						Translation.getAndSendMessage(p, StringsID.SHOP_NOT_SUPPORT_SELL_ACTION, true);
						return;
					}
					
					//¿Tiene el cofre?
					if(!isAdminShop && !ShopUtil.validateNearbyChest(sign.getLocation())) {
						Translation.getAndSendMessage(p, StringsID.SHOP_ERROR_NO_CHEST_FOUND, true);
						return;
					}
					
					//Bien, vamos a VENDER el objeto al jugador.
					int totalAmount = ShopUtil.getItemAmount(sign);
					ItemStack shopItem = ShopUtil.getItemStack(sign);
					BigDecimal pricePerItem = ShopUtil.getSellPrice(sign).divide(new BigDecimal(totalAmount), 10, RoundingMode.HALF_UP);
					
					int playerInvFreeSpace = InventoryUtil.getInventoryFreeSpaceForItem(p.getInventory(), shopItem);
					
					if(playerInvFreeSpace <= 0) {
						Translation.getAndSendMessage(p, StringsID.SHOP_ERROR_PLAYER_NOT_HAVE_SPACE, true);
						return;
					}
					
					if(playerInvFreeSpace < totalAmount) totalAmount = playerInvFreeSpace;
					
					//Tiene dinero, hay objetos suficientes en el inventario del cofre?
					int totalItemsChest = (!isAdminShop) ? ShopUtil.getTotalItemsInventory(ShopUtil.getNearbyChest(sign).getInventory(), shopItem) : totalAmount;
					
					if(totalItemsChest <= 0) {
						//Sin objetos
						Translation.getAndSendMessage(p, StringsID.SHOP_OUT_OF_STOCK, true);
						return;
					}
					
					if(totalItemsChest < totalAmount) totalAmount = totalItemsChest;
					
					//¿Tiene el dinero?
					BigDecimal totalCost = pricePerItem.multiply(new BigDecimal(totalAmount));
					
					if(!AllBanks.getEconomy().has(p, totalCost.doubleValue())) {
						//No tiene dinero para comprar esta cantidad de objetos.
						Translation.getAndSendMessage(p, StringsID.YOU_DO_NOT_HAVE_MONEY, true);
						return;
					}
					
					
					//Pagar/Cobrar
					if(AllBanks.getEconomy().withdrawPlayer(p, totalCost.doubleValue()).transactionSuccess()) {
						//Bien, procesar
						if(!isAdminShop) InventoryUtil.removeItemsFromInventory(ShopUtil.getNearbyChest(sign).getInventory(), shopItem, totalAmount);
						InventoryUtil.putItemsToInventory(p.getInventory(), shopItem, totalAmount);
						
						if(!isAdminShop) AllBanks.getEconomy().depositPlayer(ShopUtil.getOwner(sign), totalCost.doubleValue());
						//Bien, mostrar mensaje.
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", String.valueOf(totalAmount));
						replaceMap.put("%2%", ItemNameUtil.getItemName(shopItem));
						replaceMap.put("%3%", AllBanks.getEconomy().format(totalCost.doubleValue()));
						
						Translation.getAndSendMessage(p, StringsID.SHOP_SUCCESS_SELL, replaceMap, true);
					}
					
					return;
				}
			}
		}
	}
	
}
