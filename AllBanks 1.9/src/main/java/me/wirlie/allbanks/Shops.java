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
package me.wirlie.allbanks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.utils.ChatUtil;
import me.wirlie.allbanks.utils.FakeItemManager;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.ShopUtil;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * Clase similar a la clase {@linkplain me.wirlie.allbanks.Banks Banks}, sólo que esta clase se
 * encarga de procesar funciones relacionado con las tiendas de AllBanks.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Shops {
	
	/** Línea cabecera, util para saber si un letrero es de AllBanks **/
	final public static int LINE_HEADER = 0;
	/** Linea de dueño, util para saber quién es el dueño de un letrero **/
	final public static int LINE_OWNER = 1;
	/** Línea de preio, util para saber el precio de venta/compra y cantidad de objetos **/
	final public static int LINE_PRICE = 2;
	/** Linea de objeto, util para saber qué objeto se está comprando o vendiendo **/
	final public static int LINE_ITEM = 3;
	/** Etiqueta Admin, unicamente necesario para saber si una tienda pertenece a un Admin Shop **/
	final public static String ADMIN_TAG = AllBanks.getInstance().getConfig().getString("shop.admin-tag", "admin");
	/** Formato de cabecera, util para saber qué cabecera usar al crear/leer una cabecera de AllBanks en un letrero **/
	final public static String HEADER_FORMAT = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "AllBanks SHOP";
	/** Cabecera sin formato **/
	final public static String HEADER = ChatUtil.removeChatFormat(HEADER_FORMAT);
	
	/**
	 * Hace una nueva tienda de AllBanks.
	 * @param signLines Lineas a establecer en el letrero.
	 * @param block Bloque afectado y que se quiere registrar (letrero)
	 * @param shopOwner Dueño de la tienda.
	 */
	public static void makeNewShop(String[] signLines, Block block, Player shopOwner) {
		if(!block.getType().equals(Material.WALL_SIGN) || signLines.length < 4) {
			block.breakNaturally();
			return;
		}
		
		//Validar cofre
		boolean isAdminShop = false;
		
		if(!signLines[LINE_OWNER].equalsIgnoreCase(ADMIN_TAG)) {
			if(!ShopUtil.validateNearbyChest(block.getLocation())) {
				Translation.getAndSendMessage(shopOwner, StringsID.SHOP_ERROR_NO_CHEST_FOUND, true);
				InteractiveUtil.sendSound(shopOwner, SoundType.DENY);
				block.breakNaturally();
				return;
			} else {
				//Evitar que un cofre tenga 2 letreros de 2 personas distintas.
				Sign relativeSign = ShopUtil.tryToGetRelativeSignByChest((Sign) block.getState());
				
				if(relativeSign != null) {
					OfflinePlayer p = ShopUtil.getOwner(relativeSign);
					
					if(p != null) {
						if(!p.getName().equalsIgnoreCase(shopOwner.getName())) {
							block.breakNaturally();
							Translation.getAndSendMessage(shopOwner, StringsID.SHOP_ANOTHER_SHOP_USES_THIS_CHEST, true);
							return;
						}
					}
				}
			}
		}else{
			isAdminShop = true;
		}
		
		if(Banks.registerNewABSign(block.getLocation(), shopOwner)) {
			Sign sign = (Sign) block.getState();
			
			String[] splitLinePrice = signLines[2].split(" ");
			String buySell_line = "";
			
			for(int i = 0; i < splitLinePrice.length; i++) {
				if(i == 0) continue;
				
				if(i != (splitLinePrice.length - 1))
					buySell_line += splitLinePrice[i] + " ";
				else
					buySell_line += splitLinePrice[i];
			}
			
			sign.setLine(LINE_HEADER, HEADER_FORMAT);
			sign.setLine(LINE_OWNER, ChatColor.DARK_AQUA + signLines[1]);
			sign.setLine(LINE_PRICE, ChatColor.DARK_RED + splitLinePrice[0] + " " + ChatColor.DARK_GREEN + buySell_line);
			sign.setLine(LINE_ITEM, ChatColor.DARK_AQUA + signLines[3]);
			
			sign.update();
			
			Translation.getAndSendMessage(shopOwner, StringsID.SHOP_NEW_SHOP, true);
			InteractiveUtil.sendSound(shopOwner, SoundType.NEW_BANK);
			
			if(signLines[3].equalsIgnoreCase("???")) {
				//Aviso
				Translation.getAndSendMessage(shopOwner, StringsID.SHOP_WARNING_ITEM_NAME, true);
			}
			
			boolean spawnFakeItemUserShops = AllBanks.getInstance().getConfig().getBoolean("shop.enable-fake-item-for-user-shop", true);
			
			//Intentar colocar el objeto falso
			if(!signLines[3].equalsIgnoreCase("???")){
				//Es una tienda admin?
				if(isAdminShop || !isAdminShop && spawnFakeItemUserShops)
					FakeItemManager.SpawnFakeItemForShop(block.getLocation());
			}
			
		} else {
			Translation.getAndSendMessage(shopOwner, StringsID.SQL_EXCEPTION_PROBLEM, true);
			InteractiveUtil.sendSound(shopOwner, SoundType.DENY);
			block.breakNaturally();
			return;
		}
	}
}
