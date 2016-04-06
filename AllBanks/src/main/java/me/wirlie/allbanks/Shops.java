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
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.util.InteractiveUtil;
import me.wirlie.allbanks.util.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Shops {
	
	public static void makeNewShop(String[] lines, Block b, Player owner) {
		if(!b.getType().equals(Material.WALL_SIGN) || lines.length < 4) {
			b.breakNaturally();
			return;
		}
		
		if(Banks.registerAllBanksSign(b.getLocation(), owner)) {
			Sign sign = (Sign) b.getState();
			
			sign.setLine(0, ChatColor.WHITE + "" + ChatColor.BOLD + lines[0]);
			sign.setLine(1, ChatColor.YELLOW + lines[1]);
			sign.setLine(2, ChatColor.YELLOW + lines[2]);
			sign.setLine(3, ChatColor.AQUA + lines[3]);
			
			sign.update();
			
			Translation.getAndSendMessage(owner, StringsID.SHOP_NEW_SHOP, true);
			InteractiveUtil.sendSound(owner, SoundType.NEW_BANK);
			
			if(lines[3].equalsIgnoreCase("???")) {
				//Aviso
				Translation.getAndSendMessage(owner, StringsID.SHOP_WARNING_ITEM_NAME, true);
			}
		} else {
			Translation.getAndSendMessage(owner, StringsID.SQL_EXCEPTION_PROBLEM, true);
			InteractiveUtil.sendSound(owner, SoundType.DENY);
			b.breakNaturally();
			return;
		}
	}
}
