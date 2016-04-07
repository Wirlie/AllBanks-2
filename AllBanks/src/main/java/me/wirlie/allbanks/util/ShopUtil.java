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
package me.wirlie.allbanks.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.wirlie.allbanks.Shops;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ShopUtil {

	/**
	 * @param string
	 * @return
	 */
	public static boolean validatePriceLine(String s) {

		Pattern r = Pattern.compile("^[0-9]{1,2} ((S:[0-9]{1,}(|(\\.[0-9]{1,2})))|(S:[0-9]{1,}(|(\\.[0-9]{1,2}))) (B:[0-9]{1,}(|(\\.[0-9]{1,2})))|(B:[0-9]{1,}(|(\\.[0-9]{1,2}))) (S:[0-9]{1,}(|(\\.[0-9]{1,2})))|(B:[0-9]{1,}(|(\\.[0-9]{1,2}))))$");
		
		return r.matcher(s).matches();
	}
	
	public static boolean validateNearbyChest(Location loc) {
		Location testLoc = loc.getBlock().getRelative(BlockFace.DOWN).getLocation();
		return testLoc.getBlock().getType().equals(Material.CHEST);
	}
	
	public static int getItemAmount(Sign sign) {
		return getItemAmount(sign.getLine(Shops.LINE_PRICE));
	}
	
	public static int getItemAmount(String priceLine) {
		priceLine = ChatUtil.removeChatFormat(priceLine);
		
		String[] str = priceLine.split(" ");
		try {
			return Integer.parseInt(str[0]);
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
	public static ItemStack getItemStack(Sign sign) {
		String itemLine = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_ITEM));
		String[] split = itemLine.split(":");
		
		if(split.length == 2) {
			int durability = Integer.parseInt(split[1]);
			ItemStack item = new ItemStack(ItemNameUtil.getItemByShortName(split[0]), getItemAmount(sign));
			item.setDurability((short) durability);
			return item;
		}else {
			return new ItemStack(ItemNameUtil.getItemByShortName(split[0]), getItemAmount(sign));
		}
	}
	
	public static BigDecimal getBuyPrice(Sign sign) {
		String priceLine = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_PRICE));
		String[] str = priceLine.split(" ");
		for(String s : str) {
			if(s.startsWith("B:")) {
				return new BigDecimal(s.replace("B:", ""));
			}
		}
		
		return null;
	}
	
	public static BigDecimal getSellPrice(Sign sign) {
		String priceLine = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_PRICE));
		String[] str = priceLine.split(" ");
		for(String s : str) {
			if(s.startsWith("S:")) {
				return new BigDecimal(s.replace("S:", ""));
			}
		}
		
		return null;
	}

	/**
	 * @param line
	 * @return
	 */
	public static boolean signSupportSellAction(Sign sign) {
		return (getSellPrice(sign) == null) ? false : true;
	}
	
	public static boolean signSupportBuyAction(Sign sign) {
		return (getBuyPrice(sign) == null) ? false : true;
	}
	
	public static Sign tryToGetRelativeSignByChest(Sign mainSign) {
		Block chestB = mainSign.getBlock().getRelative(BlockFace.DOWN);
		
		//Intentar obtener los bloques de los 4 puntos cardinales
		for(int i = 0; i < 4; i++) {
			Block tryBlock = chestB.getRelative((i == 0) ? BlockFace.NORTH : ((i == 1) ? BlockFace.SOUTH : ((i == 2) ? BlockFace.EAST : BlockFace.WEST)));
			
			if(tryBlock.getType().equals(Material.CHEST)) {
				//Bien, es el cofre que buscamos.
				Block trySign = tryBlock.getRelative(BlockFace.UP);
				
				if(trySign.getType().equals(Material.WALL_SIGN)) {
					Sign sign = (Sign) trySign.getState();
					
					if(sign.getLine(Shops.LINE_HEADER).equalsIgnoreCase(Shops.HEADER_FORMAT)) {
						return sign;
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * @param itemStack
	 * @return
	 */
	public static boolean playerHaveItemsInYourInventory(Player p, ItemStack shopItem, boolean checkAmount) {
		
		PlayerInventory inv = p.getInventory();
		int totalItems = 0;
		
		for(ItemStack item : inv.getContents()) {
			if(item == null || item.getType().equals(Material.AIR)) continue;
			
			if(!checkAmount && item.getType().equals(shopItem.getType()) && item.getDurability() == shopItem.getDurability()) 
				return true;
			else if (checkAmount && item.getType().equals(shopItem.getType()) && item.getDurability() == shopItem.getDurability()) {
				totalItems += item.getAmount();
			}
		}
		
		if(totalItems >= shopItem.getAmount()) {
			return true;
		}
		
		return false;
	}
	
	public static int getTotalItemsInPlayerInventory(Player p, ItemStack shopItem) {
		PlayerInventory inv = p.getInventory();
		int totalItems = 0;
		
		for(ItemStack item : inv.getContents()) {
			if(item == null || item.getType().equals(Material.AIR)) continue;
			
			if (item.getType().equals(shopItem.getType()) && item.getDurability() == shopItem.getDurability()) {
				totalItems += item.getAmount();
			}
		}
		
		return totalItems;
	}

	/**
	 * @return
	 */
	public static OfflinePlayer getOwner(Sign sign) {
		String ownerName = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER));
		OfflinePlayer getPlayer = Bukkit.getPlayer(ownerName);
		
		if(getPlayer == null) {
			//Intentar con los offline
			for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
				if(p.getName().equals(ownerName)) {
					getPlayer = p;
					break;
				}
			}
		}
		
		return getPlayer;
	}

	/**
	 * @param p
	 * @param itemStack
	 * @param totalItems
	 */
	public static synchronized void removeItemsFromPlayerInventory(Player p, ItemStack itemStack, int totalItems) {
		PlayerInventory inv = p.getInventory();
		int remainingItems = totalItems;
		
		for(int slot = 0; slot < inv.getSize(); slot++) {
			ItemStack itemSlot = inv.getItem(slot);
			if(itemSlot == null || itemSlot.getType().equals(Material.AIR)) continue;
			
			if(itemSlot.getType().equals(itemStack.getType()) && itemSlot.getDurability() == itemStack.getDurability() && remainingItems > 0) {
				remainingItems -= itemSlot.getAmount();
				
				if(remainingItems >= 0) {
					inv.setItem(slot, new ItemStack(Material.AIR));
					
					if(remainingItems == 0) break;
				} else {
					//remainingItems es menor a 0 esto quiere decir que el item en el slot es mayor a lo que falta
					int remaining = remainingItems + itemSlot.getAmount();
					int newItemAmount = itemSlot.getAmount() - remaining;
					ItemStack newItemSlot = new ItemStack(itemSlot);
					newItemSlot.setAmount(newItemAmount);
					inv.setItem(slot, newItemSlot);
					break;
				}
			}
		}
	}

	/**
	 * @param sign
	 * @param shopItem
	 * @return
	 */
	public static synchronized int checkShopChestFreeSpace(Sign sign, ItemStack shopItem) {
		Block tryChest = sign.getLocation().getBlock().getRelative(BlockFace.DOWN);
		
		if(!tryChest.getType().equals(Material.CHEST)) return -1;
		
		Chest chest = (Chest) tryChest.getState();
		Inventory inv = chest.getInventory();
		int freeSpace = 0;
		
		for(int slot = 0; slot < inv.getSize(); slot++) {
			if(inv.getItem(slot) == null || inv.getItem(slot).getType().equals(Material.AIR)) {
				freeSpace += shopItem.getMaxStackSize();
			}else if(inv.getItem(slot).getType().equals(shopItem.getType()) && inv.getItem(slot).getDurability() == shopItem.getDurability()) {
				int getStackFreeSpace = shopItem.getMaxStackSize() - inv.getItem(slot).getAmount();
				if(getStackFreeSpace > 0) {
					freeSpace += getStackFreeSpace;
				}
			}
		}
		
		return freeSpace;
	}

	/**
	 * @param sign
	 * @param shopItem
	 * @param totalItems
	 */
	public static synchronized boolean putItemsToShopChest(Sign sign, ItemStack shopItem, int totalItems) {
		Block tryChest = sign.getLocation().getBlock().getRelative(BlockFace.DOWN);
		
		if(!tryChest.getType().equals(Material.CHEST)) return false;
		
		Chest chest = (Chest) tryChest.getState();
		Inventory inv = chest.getInventory();
		int remainingItems = totalItems;
		for(int slot = 0; slot < inv.getSize(); slot++) {
			if(remainingItems <= 0) break;
			if(inv.getItem(slot) == null || inv.getItem(slot).getType().equals(Material.AIR)) {
				//Podemos depositar un item completo
				ItemStack putItem = new ItemStack(shopItem);
				int putAmount = (remainingItems > shopItem.getMaxStackSize()) ? shopItem.getMaxStackSize() : remainingItems;
				putItem.setAmount(putAmount);
				inv.setItem(slot, putItem);
				remainingItems -= putAmount;
			} else if(inv.getItem(slot).getType().equals(shopItem.getType()) && inv.getItem(slot).getDurability() == shopItem.getDurability()) {
				//¿Hay espacio disponible?
				ItemStack slotItem = inv.getItem(slot);
				int getStackFreeSpace = slotItem.getMaxStackSize() - slotItem.getAmount();
				
				if(getStackFreeSpace > 0) {
					ItemStack putItem = new ItemStack(shopItem);
					putItem.setAmount(slotItem.getAmount() + ((getStackFreeSpace > remainingItems) ? remainingItems : getStackFreeSpace));
					inv.setItem(slot, putItem);
					remainingItems -= getStackFreeSpace;
				}
			}
		}
		
		return true;
	}

}
