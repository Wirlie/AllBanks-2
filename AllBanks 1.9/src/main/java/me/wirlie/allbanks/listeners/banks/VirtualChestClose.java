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

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.tempdata.BankSession;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class VirtualChestClose implements Listener{
	
	public VirtualChestClose(){
		AllBanksLogger.info("VirtualChestClose");
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onVirtualChestInteractt(InventoryClickEvent e){
		final Inventory inv = e.getInventory();
		
		if(inv.getName().startsWith("ab:virtualchest:")){
			
			Player p = (Player) e.getWhoClicked();
			
			BankSession bs = BankSession.getSession(p);
			if(bs != null){
				//Actualizar ultimo uso, para evitar un cierre automático por falta de actividad
				bs.updateLastUse();
			}
			
			if(!e.getAction().equals(InventoryAction.PLACE_ALL) && !e.getAction().equals(InventoryAction.PLACE_ONE)){
				/* Las acciones que no pertenezcan a Place (colocar objeto) no pueden ser procesadas en esta
				 * función, ya que, el método rawSlot() devuelve el slot origen desde donde se dió el clic
				 * y no hay manera de saber hacia dónde se movió un objeto (ej: shift + clic) lo cual hace
				 * impreciso el guardar el inventario de un cofre virtual. Cabe destacar, que ésta función
				 * no es escencial para el funcionamiento de BankChest, únicamente es un método alternativo
				 * para prevenir pérdidas de datos al momento de por ejemplo una interrupción inesperada del
				 * servidor.
				 */
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
				
				//Fix: El inventario del evento (e.getInventory) no se actualiza apropiadamente.
				if(i == e.getRawSlot())
					armMap.put(i, e.getCursor());
				else
					armMap.put(i, get);
				
			}
			
			AllBanksLogger.info("BANK-CHEST: (Event: InventoryClick) VirtualChest #" + chestNumber + " for player " + p.getName() + " (" + p.getDisplayName() + ") updated.");
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
			
			Player p = (Player) e.getPlayer();
			
			AllBanksLogger.info("BANK-CHEST: (Event: InventoryClose) VirtualChest #" + chestNumber + " for player " + p.getName() + " (" + p.getDisplayName() + ") updated.");
			Banks.setVirtualChestContents(e.getPlayer().getName(), chestNumber, armMap);
			
			InteractiveUtil.sendSound(p, SoundType.VIRTUAL_CHEST_CLOSE);
			
			BankSession bs = BankSession.getSession(p);
			if(bs != null){
				//Actualizar ultimo uso, para evitar un cierre automático por falta de actividad
				bs.updateLastUse();
			}
		}
	}
	
}
