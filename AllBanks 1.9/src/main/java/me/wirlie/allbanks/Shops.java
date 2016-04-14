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

import me.wirlie.allbanks.util.ChatUtil;
import me.wirlie.allbanks.util.FakeItemManager;
import me.wirlie.allbanks.util.InteractiveUtil;
import me.wirlie.allbanks.util.ShopUtil;
import me.wirlie.allbanks.util.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Shops {
	
	final public static int LINE_HEADER = 0;
	final public static int LINE_OWNER = 1;
	final public static int LINE_PRICE = 2;
	final public static int LINE_ITEM = 3;
	
	final public static String ADMIN_TAG = AllBanks.getInstance().getConfig().getString("shop.admin-tag", "admin");
	
	final public static String HEADER_FORMAT = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "AllBanks SHOP";
	final public static String HEADER = ChatUtil.removeChatFormat(HEADER_FORMAT);
	
	public static void makeNewShop(String[] lines, Block b, Player owner) {
		if(!b.getType().equals(Material.WALL_SIGN) || lines.length < 4) {
			b.breakNaturally();
			return;
		}
		
		//Validar cofre
		if(!lines[LINE_OWNER].equalsIgnoreCase(ADMIN_TAG)) {
			if(!ShopUtil.validateNearbyChest(b.getLocation())) {
				Translation.getAndSendMessage(owner, StringsID.SHOP_ERROR_NO_CHEST_FOUND, true);
				InteractiveUtil.sendSound(owner, SoundType.DENY);
				b.breakNaturally();
				return;
			} else {
				//Evitar que un cofre tenga 2 letreros de 2 personas distintas.
				Sign relativeSign = ShopUtil.tryToGetRelativeSignByChest((Sign) b.getState());
				
				if(relativeSign != null) {
					OfflinePlayer p = ShopUtil.getOwner(relativeSign);
					
					if(p != null) {
						if(!p.getName().equalsIgnoreCase(owner.getName())) {
							b.breakNaturally();
							Translation.getAndSendMessage(owner, StringsID.SHOP_ANOTHER_SHOP_USES_THIS_CHEST, true);
							return;
						}
					}
				}
			}
		}
		
		if(Banks.registerAllBanksSign(b.getLocation(), owner)) {
			Sign sign = (Sign) b.getState();
			
			String[] splitLinePrice = lines[2].split(" ");
			String BS_line = "";
			
			for(int i = 0; i < splitLinePrice.length; i++) {
				if(i == 0) continue;
				
				if(i != (splitLinePrice.length - 1))
					BS_line += splitLinePrice[i] + " ";
				else
					BS_line += splitLinePrice[i];
			}
			
			sign.setLine(LINE_HEADER, HEADER_FORMAT);
			sign.setLine(LINE_OWNER, ChatColor.DARK_AQUA + lines[1]);
			sign.setLine(LINE_PRICE, ChatColor.DARK_RED + splitLinePrice[0] + " " + ChatColor.DARK_GREEN + BS_line);
			sign.setLine(LINE_ITEM, ChatColor.DARK_AQUA + lines[3]);
			
			sign.update();
			
			Translation.getAndSendMessage(owner, StringsID.SHOP_NEW_SHOP, true);
			InteractiveUtil.sendSound(owner, SoundType.NEW_BANK);
			
			if(lines[3].equalsIgnoreCase("???")) {
				//Aviso
				Translation.getAndSendMessage(owner, StringsID.SHOP_WARNING_ITEM_NAME, true);
			}
			
			//Intentar colocar el objeto falso
			FakeItemManager.SpawnFakeItemForShop(b.getLocation());
		} else {
			Translation.getAndSendMessage(owner, StringsID.SQL_EXCEPTION_PROBLEM, true);
			InteractiveUtil.sendSound(owner, SoundType.DENY);
			b.breakNaturally();
			return;
		}
	}
}
