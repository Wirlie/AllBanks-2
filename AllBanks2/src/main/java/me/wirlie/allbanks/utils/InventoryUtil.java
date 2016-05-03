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
package me.wirlie.allbanks.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Utilidad para funciones de inventario.
 * @author Wirlie
 *
 */
public class InventoryUtil {

	private InventoryUtil(){
	}
	
	/**
	 * @param p
	 * @param itemStack
	 * @param totalItems
	 */
	public static synchronized void removeItemsFromInventory(Inventory inv, ItemStack itemStack, int totalItems) {
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
	
	public static synchronized int getInventoryFreeSpaceForItem(Sign sign, ItemStack shopItem) {
		Block tryChest = sign.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if(!tryChest.getType().equals(Material.CHEST)) return -1;
		Chest chest = (Chest) tryChest.getState();
		return getInventoryFreeSpaceForItem(chest.getInventory(), shopItem);
	}
	

	public static synchronized int getInventoryFreeSpaceForItem(PlayerInventory inv, ItemStack shopItem) {
		return getInventoryFreeSpaceForItem((Inventory) inv, shopItem);
	}

	/**
	 * @param sign
	 * @param shopItem
	 * @return
	 */
	public static synchronized int getInventoryFreeSpaceForItem(Inventory inv, ItemStack shopItem) {
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
	
	public static synchronized boolean putItemsToInventory(Sign sign, ItemStack shopItem, int totalItems) {
		Block tryChest = sign.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if(!tryChest.getType().equals(Material.CHEST)) return false;
		Chest chest = (Chest) tryChest.getState();
		
		return putItemsToInventory(chest.getInventory(), shopItem, totalItems);
	}

	/**
	 * @param sign
	 * @param shopItem
	 * @param totalItems
	 */
	public static synchronized boolean putItemsToInventory(Inventory inv, ItemStack shopItem, int totalItems) {
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
