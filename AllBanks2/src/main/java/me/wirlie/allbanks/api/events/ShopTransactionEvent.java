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
package me.wirlie.allbanks.api.events;

import java.math.BigDecimal;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.wirlie.allbanks.api.utils.ShopData;
import me.wirlie.allbanks.api.utils.ShopTransactionType;

/**
 * @author Wirlie
 *
 */
public class ShopTransactionEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private ShopData shopdata;
	private ItemStack item;
	private ShopTransactionType transactionType;
	private Player transactionPlayer;
	private BigDecimal transactionMoneyAmount;

	public HandlerList getHandlers() {
	    return handlers;
	}

	@SuppressWarnings("javadoc")
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	/**
	 * This event is called after the player make a transaction (like buy or sell an item).
	 * @param shop {@link ShopData}
	 * @param item The item of this post-transaction.
	 * @param transactionType Transaction type.
	 * @param transactionMoneyAmount Amount of money involved with this transaction.
	 * @param transactionPlayer Player of this transaction.
	 */
	public ShopTransactionEvent(ShopData shop, ItemStack item, ShopTransactionType transactionType, BigDecimal transactionMoneyAmount, Player transactionPlayer){
		this.shopdata = shop;
		this.item = item;
		this.transactionType = transactionType;
		this.transactionPlayer = transactionPlayer;
		this.transactionMoneyAmount = transactionMoneyAmount;
	}
	
	/**
	 * Get the data of the shop that is involved with this event.
	 * @return {@link ShopData}
	 */
	public ShopData getShopData(){
		return shopdata;
	}
	
	/**
	 * Get the item involved with this pre-transaction.
	 * @return {@link ItemStack Item}.
	 */
	public ItemStack getItem(){
		return item;
	}
	
	/**
	 * Get the type of this pre-transaction.
	 * @return {@link ShopTransactionType TransactionType}
	 */
	public ShopTransactionType getTransactionType(){
		return transactionType;
	}
	
	/**
	 * Get the player that has fired this event (client).
	 * @return {@link Player Player}
	 */
	public Player getPlayer(){
		return transactionPlayer;
	}
	
	/**
	 * The amount of money involved with this transaction. For example, if a player has sold an item
	 * and he received $50 for this, transactionMoneyAmount should be 50.
	 * @return transactionMoneyAmount
	 */
	public BigDecimal getTransactionMoneyAmount(){
		return transactionMoneyAmount;
	}
}
