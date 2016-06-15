/*
 * AllBanks2 - AllBanks2 is a plugin for CraftBukkit and Spigot.
 * Copyright (C) 2016 Josue Israel Acevedo Peña (Wirlie)
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

/**
 * 
 */
package me.wirlie.allbanks.statistics;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import me.wirlie.allbanks.statistics.AllBanksShopStatistics.TransactionType;

/**
 * @author Wirlie
 *
 */
@SuppressWarnings("javadoc")
public class ShopTransactionEntry {
	public Location signLoc;
	public String transactionPlayer;
	public ItemStack transactionItem;
	public long date;
	public String shopOwner;
	public TransactionType transactionType;
	
	/**
	 * Construir una nueva entrada de una transacción.
	 * @param signLoc Localización del letrero de la tienda.
	 * @param transactionPlayer Jugador que ha hecho la transacción.
	 * @param transactionItem Ítem de la transacción.
	 * @param date Fecha en milisegundos de la transacción.
	 * @param shopOwner Dueño de la tienda de la transacción.
	 * @param transactionType Tipo de transacción.
	 */
	public ShopTransactionEntry(Location signLoc, String transactionPlayer, ItemStack transactionItem, long date, String shopOwner, TransactionType transactionType){
		this.signLoc = signLoc;
		this.transactionPlayer = transactionPlayer;
		this.transactionItem = transactionItem;
		this.date = date;
		this.shopOwner = shopOwner;
		this.transactionType = transactionType;
	}
	
	
}
