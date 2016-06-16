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

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
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
import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.PermissionsConstants;
import me.wirlie.allbanks.Shops;

/**
 * Utilidad para las funciones de la tienda.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ShopUtil {
	
	/**
	 * Validar la lína de precio de un letrero
	 * @param s Línea de precio
	 * @return {@code true} si la cadena de texto es válida.
	 */
	public static boolean validatePriceLine(String s) {
		
		Pattern r = Pattern.compile("^[0-9]{1,2} ((s:[0-9]{1,}(|(\\.[0-9]{1,2})))|(s:[0-9]{1,}(|(\\.[0-9]{1,2}))) (b:[0-9]{1,}(|(\\.[0-9]{1,2})))|(b:[0-9]{1,}(|(\\.[0-9]{1,2}))) (s:[0-9]{1,}(|(\\.[0-9]{1,2})))|(b:[0-9]{1,}(|(\\.[0-9]{1,2}))))$", Pattern.CASE_INSENSITIVE);
		
		return r.matcher(s).matches();
	}
	
	/**
	 * Comprobar si un jugador tiene permisos para remover un letrero.
	 * @param p Jugador
	 * @param sign Letrero
	 * @return {@code true} si el jugador tiene permisos.
	 */
	public static boolean playerHasPermissionForRemove(Player p, Sign sign){
		if(Util.hasPermission(p, PermissionsConstants.SHOP_ADMIN_PERMISSION)) return true;
		
		//Comprobar dueño
		String ownerSTR = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER));
		
		if(p.getName().equalsIgnoreCase(ownerSTR)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Validar el cofre de un letrero.
	 * @param loc Localización del letrero.
	 * @return {@code true} si el cofre fue encontrado.
	 */
	public static boolean validateNearbyChest(Location loc) {
		Location testLoc = loc.getBlock().getRelative(BlockFace.DOWN).getLocation();
		return testLoc.getBlock().getType().equals(Material.CHEST);
	}
	
	/**
	 * Obtener el monto del objeto de un letrero en base a la línea de precios.
	 * @param sign Letrero de AllBanksShop
	 * @return monto del objeto
	 */
	public static int getItemAmount(Sign sign) {
		return getItemAmount(sign.getLine(Shops.LINE_PRICE));
	}
	
	/**
	 * Obtener el monto del objeto de un letrero en base a la línea de precios.
	 * @param priceLine Cadena de texto representativa
	 * @return monto del objeto
	 */
	public static int getItemAmount(String priceLine) {
		priceLine = ChatUtil.removeChatFormat(priceLine);
		
		String[] str = priceLine.split(" ");
		try {
			return Integer.parseInt(str[0]);
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
	/**
	 * Obtener el ítem en venta de un letrero
	 * @param signLoc Localización del letrero de AllBanksShop
	 * @return Item
	 */
	public static ItemStack getItemStack(Location signLoc) {
		Block b = signLoc.getBlock();
		
		if(!b.getType().equals(Material.WALL_SIGN)){
			return null;
		}
		
		return getItemStack((Sign) b.getState());
	}
	
	/**
	 * Obtener el ítem en venta de un letrero
	 * @param sign Letrero de AllBanksShop
	 * @return Item
	 */
	public static ItemStack getItemStack(Sign sign) {
		String itemLine = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_ITEM));
		
		Pattern defaultItemSyntax = Pattern.compile("^([A-Za-z0-9 ]{1,})(|:([0-9]{1,}))$");
		Pattern specialItemSyntax = Pattern.compile("^([A-Za-z0-9 ]{1,})#([0-9]{1,})$");
		
		Matcher match = defaultItemSyntax.matcher(itemLine);

		if(match.matches()){
			String[] split = itemLine.split(":");

			if(split.length == 2) {
				int durability = Integer.parseInt(match.group(2).replace(":", ""));
				Material mat = ItemNameUtil.getItemByShortName(match.group(1));
				
				if(mat == null) return null;
				
				ItemStack item = new ItemStack(mat, getItemAmount(sign));
				item.setDurability((short) durability);
				return item;
			}else {
				Material mat = ItemNameUtil.getItemByShortName(match.group(1));
				
				if(mat == null) return null;
				
				return new ItemStack(mat, getItemAmount(sign));
			}
		} else {
			match = specialItemSyntax.matcher(itemLine);
			
			if(match.matches()){
				ItemStack getItem = getItemBySpecialID(match.group(2));
				getItem.setAmount(getItemAmount(sign));
				return getItem;
			}else{
				return null;
			}
		}
	}
	
	/**
	 * Obtener el precio de compra de un letrero de AllBanksShop
	 * @param sign Letrero de AllBanksShop
	 * @return Precio de compra
	 */
	public static BigDecimal getBuyPrice(Sign sign) {
		String priceLine = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_PRICE));
		String[] str = priceLine.split(" ");
		for(String s : str) {
			s.toLowerCase();
			
			if(s.startsWith("b:") || s.startsWith("B:")) {
				return new BigDecimal(s.replace("b:", "").replace("B:", ""));
			}
		}
		
		return null;
	}
	
	/**
	 * Obtener el precio de venta de un letrero de AllBanksShop
	 * @param sign Letrero de AllBanksShop
	 * @return Precio de venta
	 */
	public static BigDecimal getSellPrice(Sign sign) {
		String priceLine = ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_PRICE));
		String[] str = priceLine.split(" ");
		for(String s : str) {
			
			if(s.startsWith("s:") || s.startsWith("S:")) {
				return new BigDecimal(s.replace("s:", "").replace("S:", ""));
			}
		}
		
		return null;
	}
	
	/**
	 * Comprobar si un letrero soporta la acción VENDER
	 * @param sign Letrero de AllBanksShop
	 * @return {@code true} si el letrero soporta la acción VENDER
	 */
	public static boolean signSupportSellAction(Sign sign) {
		return (getSellPrice(sign) == null) ? false : true;
	}
	
	/**
	 * Comprobar si un letrero soporta la acción COMPRAR
	 * @param sign Letrero de AllBanksShop
	 * @return {@code true} si el letrero soporta la acción COMPRAR
	 */
	public static boolean signSupportBuyAction(Sign sign) {
		return (getBuyPrice(sign) == null) ? false : true;
	}
	
	/**
	 * Intentar obtener el letrero relativo de AllBanksShop
	 * @param mainSign Letrero principal
	 * @return Letrero obtenido
	 */
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
	 * Comprobar si el jugador tiene los items necesarios para la transacción
	 * @param p Jugador
	 * @param shopItem Item de la tienda
	 * @param checkAmount Monto a checar
	 * @return {@code true} si el jugador tiene los objetos necesarios.
	 */
	public static boolean checkItemForPlayerInventory(Player p, ItemStack shopItem, boolean checkAmount) {
		
		//Necesario para comparar 2 objetos.
		ItemStack oneShopItem = shopItem.clone();
		oneShopItem.setAmount(1);
		
		PlayerInventory inv = p.getInventory();
		int totalItems = 0;
		
		for(ItemStack item : inv.getContents()) {
			
			if(item == null || item.getType().equals(Material.AIR)) continue;
			
			//Necesario para comparar 2 objetos.
			ItemStack oneItem = item.clone();
			oneItem.setAmount(1);
			
			if(!checkAmount && oneItem.equals(oneShopItem) && oneItem.getItemMeta().equals(shopItem.getItemMeta()) && oneItem.getDurability() == oneShopItem.getDurability()) 
				return true;
			else if (checkAmount && oneItem.equals(oneShopItem) && oneItem.getItemMeta().equals(shopItem.getItemMeta()) && oneItem.getDurability() == oneShopItem.getDurability()) {
				totalItems += item.getAmount();
			}
		}
		
		if(totalItems >= shopItem.getAmount()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Obtener todos los objetos totales de un inventario
	 * @param inv Inventario
	 * @param shopItem Item de la tienda
	 * @return Cantidad de item
	 */
	public static int getTotalItemsInventory(Inventory inv, ItemStack shopItem) {
		int totalItems = 0;
		
		ItemStack oneShopItem = shopItem.clone();
		oneShopItem.setAmount(1);
		
		for(ItemStack item : inv.getContents()) {
			
			if(item == null || item.getType().equals(Material.AIR)) continue;
			
			ItemStack oneItem = item.clone();
			oneItem.setAmount(1);
			
			if (oneItem.equals(oneShopItem) && oneItem.getItemMeta().equals(shopItem.getItemMeta()) && oneItem.getDurability() == oneShopItem.getDurability()) {
				totalItems += item.getAmount();
			}
		}
		
		return totalItems;
	}
	
	/**
	 * Obtener el dueño del letrero de AllBanksShop
	 * @param sign Letrero de AllBanksShop
	 * @return Dueño
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
	 * Obtener el cofre de un Letrero de AllBanksShop.
	 * @param sign Letrero de AllBanksShop
	 * @return Cofre
	 */
	public static Chest getNearbyChest(Sign sign) {
		Block signBlock = sign.getBlock();
		Block tryChestBlock = signBlock.getRelative(BlockFace.DOWN);
		
		if(!tryChestBlock.getType().equals(Material.CHEST)) return null;
		
		return (Chest) tryChestBlock.getState();
	}

	/**
	 * Comprobar si una tienda es una tienda administrativa.
	 * @param sign Letrero de AllBanksShop
	 * @return {@code true} si la tienda es una tienda administrativa.
	 */
	public static boolean isAdminShop(Sign sign) {
		return ChatUtil.removeChatFormat(sign.getLine(Shops.LINE_OWNER)).equalsIgnoreCase(Shops.ADMIN_TAG);
	}
	
	/**
	 * Comprobar si un letrero necesita un ID especial, por ejemplo, los libros encantados ocupan de una duración especial.
	 * @param item Objeto a comprobar
	 * @return {@code true} si el item necesita un ID especial
	 */
	public static boolean itemNeedResolveCustomDurability(ItemStack item){
		if(item.getType() == Material.AIR) return false;
		
		if(item.getItemMeta().getDisplayName() != null){
			return true;
		}else if(!item.getItemMeta().getEnchants().isEmpty()){
			return true;
		}
		
		return itemNeedResolveCustomDurability(item.getType());
	}
	
	/**
	 * Comprobar si un letrero necesita un ID especial, por ejemplo, los libros encantados ocupan de una duración especial.
	 * @param mat Material a comprobar
	 * @return {@code true} si el item necesita un ID especial
	 */
	public static boolean itemNeedResolveCustomDurability(Material mat){
		switch(mat){
		case BANNER:
		case POTION:
		case ENCHANTED_BOOK:
		case MOB_SPAWNER:
		case MONSTER_EGG:
		case TIPPED_ARROW:
		case SPLASH_POTION:
		case LINGERING_POTION:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Resolver el ID especial
	 * @param item Objeto a resolver
	 * @return ID especial
	 */
	public static String resolveCustomDurabilityIDFor(ItemStack item){
		if(item == null) throw new NullPointerException("item is null");
		
		item.setAmount(1);
		
		boolean resolve = false;
		
		switch(item.getType()){
		case BANNER:
		case POTION:
		case ENCHANTED_BOOK:
		case MOB_SPAWNER:
		case MONSTER_EGG:
		case TIPPED_ARROW:
		case SPLASH_POTION:
		case LINGERING_POTION:
			resolve = true;
			break;
		default:
			resolve = false;
			break;
		}
		
		if(item.getItemMeta().getDisplayName() != null){
			resolve = true;
		}else if(!item.getItemMeta().getEnchants().isEmpty()){
			resolve = true;
		}
		
		if(resolve){
			String base64str = ItemStackBase64.toBase64(item);
			
			Statement selectStatement = null;
			ResultSet res = null;
			Statement insertStatement = null;
			ResultSet generatedKeys = null;
			
			try {
				
				selectStatement = AllBanks.getSQLConnectionx(DBUtil.ITEMSOLUTION_DATABASE_CONNECTION_NAME).createStatement();
				res = selectStatement.executeQuery("SELECT * FROM items WHERE itemmeta = '" + base64str + "'");
				
				if(res.next()){
					return "#" + String.valueOf(res.getInt("id"));
				} else {
					//Registrar
					insertStatement = AllBanks.getSQLConnectionx(DBUtil.ITEMSOLUTION_DATABASE_CONNECTION_NAME).createStatement();
					insertStatement.executeUpdate("INSERT INTO items (itemmeta) VALUES ('" + base64str + "')");
					
					generatedKeys = insertStatement.getGeneratedKeys();
							
		            if (generatedKeys.next()) {
		                return "#" + String.valueOf(generatedKeys.getLong(1));
		            }
					
					return null; 
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null; 
			} finally {
					try {
						if(res != null) res.close();
						if(selectStatement != null) selectStatement.close();
						if(generatedKeys != null) generatedKeys.close();
						if(insertStatement != null) insertStatement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}else{
			return null;
		}
	}
	
	/**
	 * Obtener el ítem a través del objeto especial.
	 * @param specialID ID especial 
	 * @return Objeto perteneciente al ID especificado
	 */
	public static ItemStack getItemBySpecialID(String specialID){
		specialID = specialID.replace("#", "");
		
		Statement selectStatement = null;
		ResultSet res = null;
		
		try{
			selectStatement = AllBanks.getSQLConnectionx(DBUtil.ITEMSOLUTION_DATABASE_CONNECTION_NAME).createStatement();
			res = selectStatement.executeQuery("SELECT * FROM items WHERE id = " + specialID);
			
			if(res.next()){
				String fromBase64 = res.getString("itemmeta");
				
				return ItemStackBase64.stacksFromBase64(fromBase64)[0];
			}
		}catch (SQLException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				res.close();
				selectStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/**
	 * Comprobar si un ID especial ya está en uso
	 * @param specialID ID especial
	 * @return {@code true} si el ID especial está en uso.
	 */
	public static boolean checkForSpecialID(String specialID) {
		
		specialID = specialID.replace("#", "");
		
		Statement selectStatement = null;
		ResultSet res = null;
		
		try{
			Integer.parseInt(specialID);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		
		try {
			
			selectStatement = AllBanks.getSQLConnectionx(DBUtil.ITEMSOLUTION_DATABASE_CONNECTION_NAME).createStatement();
			res = selectStatement.executeQuery("SELECT * FROM items WHERE id = " + specialID);
			
			return res.next();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
				try {
					if(res != null) res.close();
					if(selectStatement != null) selectStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return false;
	}

	/**
	 * Comprobar si un letrero pertenece a AllBanksShop
	 * @param s Letrero a comprobar
	 * @return {@code true} si el letrero pertenece a AllBanksShop
	 */
	public static boolean isShopSign(Sign s) {
		return ChatUtil.removeChatFormat(s.getLine(Shops.LINE_HEADER)).equalsIgnoreCase("AllBanks SHOP");
	}

}
