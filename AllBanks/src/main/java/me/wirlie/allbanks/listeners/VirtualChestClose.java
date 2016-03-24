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

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.wirlie.allbanks.Banks;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class VirtualChestClose implements Listener{

	@EventHandler
	public void onVirtualChestMove(InventoryMoveItemEvent e){
		System.out.println("MOVE");
	}
	
	@EventHandler
	public void onVirtualChestInteractt(InventoryClickEvent e){
		final Inventory inv = e.getInventory();
		
		if(inv.getName().startsWith("ab:virtualchest:")){
			
			Player p = (Player) e.getWhoClicked();
			
			if(!e.getAction().equals(InventoryAction.PLACE_ALL) && !e.getAction().equals(InventoryAction.PLACE_ONE)){
				//El resto de las acciones no pueden ser procesadas...
				return;
			}
			
			//Cofre virtual
			Integer chestNumber = 0;
			
			try{
				chestNumber = Integer.parseInt(inv.getName().replace("ab:virtualchest:", ""));
			}catch(NumberFormatException e2){
				return;
			}
			
			HashMap<Integer, ItemStack> armMap = new HashMap<Integer, ItemStack>();

			//Procesar
			for(int i = 0; i < inv.getSize(); i++){
				ItemStack get = inv.getItem(i);
				
				//Fix... no sé por que el evento no actualiza el inventario apropiadamente
				if(i == e.getRawSlot())
					armMap.put(i, e.getCursor());
				else
					armMap.put(i, get);
				
			}
			
			Banks.setVirtualChestContents(p.getName(), chestNumber, armMap);
		}
	}
	
	@EventHandler
	public void onVirtualChestClosed(InventoryCloseEvent e){
		Inventory inv = e.getInventory();

		if(inv.getName().startsWith("ab:virtualchest:")){
			//Cofre virtual
			Integer chestNumber = 0;
			
			try{
				chestNumber = Integer.parseInt(inv.getName().replace("ab:virtualchest:", ""));
			}catch(NumberFormatException e2){
				return;
			}
			
			HashMap<Integer, ItemStack> armMap = new HashMap<Integer, ItemStack>();
			
			//Procesar
			for(int i = 0; i < inv.getSize(); i++){
				ItemStack get = inv.getItem(i);
				
				armMap.put(i, get);
			}
			
			Banks.setVirtualChestContents(e.getPlayer().getName(), chestNumber, armMap);
		}
	}
	
}
