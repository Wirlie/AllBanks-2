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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
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
import org.bukkit.inventory.meta.BannerMeta;
import net.minecraft.server.v1_8_R1.Achievement;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Shops;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ShopUtil {
	Achievement ach = null;
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
	public static boolean checkItemForPlayerInventory(Player p, ItemStack shopItem, boolean checkAmount) {
		
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
	
	public static int getTotalItemsInventory(Inventory inv, ItemStack shopItem) {
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
	 * @param sign
	 * @return
	 */
	public static Chest getNearbyChest(Sign sign) {
		Block signBlock = sign.getBlock();
		Block tryChestBlock = signBlock.getRelative(BlockFace.DOWN);
		
		if(!tryChestBlock.getType().equals(Material.CHEST)) return null;
		
		return (Chest) tryChestBlock.getState();
	}

	/**
	 * @param sign
	 * @return
	 */
	public static boolean isAdminShop(Sign sign) {
		return ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER)).equalsIgnoreCase(Shops.ADMIN_TAG);
	}
	
	public boolean isASpecialItem(Material mat){
		if(mat.equals(Material.POTION)
				|| mat.equals(Material.ENCHANTED_BOOK)
				|| mat.equals(Material.MOB_SPAWNER)
				|| mat.equals(Material.MONSTER_EGG)
				|| mat.equals(Material.BANNER)){
			return true;
		}
		
		return false;
	}
	
	public static String resolveCustomDurabilityIDFor(ItemStack item){
		if(item == null) throw new NullPointerException("item is null");
		
		ObjectOutputStream out;
		switch(item.getType()){
		case BANNER:
			
			BannerMeta bannerMeta = (BannerMeta) item.getItemMeta();
			Map<String, Object> map = bannerMeta.serialize();
			
			// Serialize to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			try {
				out = new ObjectOutputStream(bos) ;
				out.writeObject(map);
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			
			try {

				// Get the bytes of the serialized object
				byte[] buf = bos.toByteArray();
				
				PreparedStatement prepareStatement = AllBanks.getSQLConnection("itemSolution").prepareStatement("SELECT * FROM banner WHERE itemmeta = ?");
				prepareStatement.setBinaryStream(1, new ByteArrayInputStream(buf), buf.length);
				ResultSet res = prepareStatement.executeQuery();
				
				if(res.next()){
					res.close();
					prepareStatement.close();
					return "#" + String.valueOf(res.getInt("id"));
				} else {
					//Registrar
					PreparedStatement insertStatement = AllBanks.getSQLConnection("itemSolution").prepareStatement("INSERT INTO banner (itemmeta) VALUES (?)");
					prepareStatement.setBinaryStream(1, new ByteArrayInputStream(buf), buf.length);
					insertStatement.executeUpdate();
					
					ResultSet generatedKeys = insertStatement.getGeneratedKeys();
							
		            if (generatedKeys.next()) {
		            	generatedKeys.close();
		            	insertStatement.close();
		            	res.close();
		            	prepareStatement.close();
		                return "#" + String.valueOf(generatedKeys.getLong(1));
		            }
					
		            res.close();
		            prepareStatement.close();
					return "#-2"; 
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			break;
		case POTION:
			break;
		case ENCHANTED_BOOK:
			break;
		case MOB_SPAWNER:
			break;
		case MONSTER_EGG:
			break;
		default:
			return "-1";
		}
		
		return "-1";
	}

}
