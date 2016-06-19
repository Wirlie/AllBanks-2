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
package me.wirlie.allbanks.api.utils;

import java.math.BigDecimal;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import me.wirlie.allbanks.utils.ShopUtil;

/**
 * @author Wirlie
 *
 */
public class ShopData {
	private OfflinePlayer owner;
	private BigDecimal sellPrice = null;
	private BigDecimal buyPrice = null;
	private ItemStack shopItem;
	private Sign signInstance;
	
	/**
	 * Get the data of a shop by sign.
	 * @param sign Sign of the shop.
	 */
	public ShopData(Sign sign){
		this.shopItem = ShopUtil.getItemStack(sign);
		if(ShopUtil.signSupportBuyAction(sign)){
			this.buyPrice = ShopUtil.getBuyPrice(sign);
		}
		if(ShopUtil.signSupportSellAction(sign)){
			this.sellPrice = ShopUtil.getSellPrice(sign);
		}
		this.owner = ShopUtil.getOwner(sign);
		this.signInstance = sign;
	}
	
	/**
	 * @return {@link OfflinePlayer Owner of this shop}
	 */
	public OfflinePlayer getShopOwner(){
		return owner;
	}
	
	/**
	 * Get the sell price of this shop. Do not forget that not all shops have a sell price, 
	 * you should check if the shop have a sell price with {@link #shopSupportSellPrice()}
	 * @return SellPrice of this shop.
	 */
	public BigDecimal getSellPrice(){
		return sellPrice;
	}
	
	/**
	 * Get the buy price of this shop. Do not forget that not all shops have a buy price, 
	 * you should check if the shop have a buy price with {@link #shopSupportBuyPrice()}
	 * @return BuyPrice of this shop.
	 */
	public BigDecimal getBuyPrice(){
		return buyPrice;
	}
	
	/**
	 * @return The location of this shop.
	 */
	public Location getShopLocation(){
		return signInstance.getLocation();
	}
	
	/**
	 * Get the item of this shop. This item also have the item amount of this shop, but, this
	 * also can be obtained with {@link #getShopItemAmount()}
	 * @return Item of this shop.
	 */
	public ItemStack getShopItem(){
		return shopItem;
	}
	
	/**
	 * A fast way to get the item amount of {@link #getShopItem()}.
	 * @return Item amount of the item of this shop.
	 */
	public int getShopItemAmount(){
		return shopItem.getAmount();
	}
	
	/**
	 * @return {@code true} if this shop is an admin shop.
	 */
	public boolean shopIsAdminShop(){
		return ShopUtil.isAdminShop(signInstance);
	}
	
	/**
	 * @return {@code true} if this shop have a buy price.
	 */
	public boolean shopSupportBuyPrice(){
		return ShopUtil.signSupportBuyAction(signInstance);
	}
	
	/**
	 * @return {@code true} if this shop have a sell price.
	 */
	public boolean shopSupportSellPrice(){
		return ShopUtil.signSupportSellAction(signInstance);
	}
}
