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
package me.wirlie.allbanks.utils.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.ItemNameUtil;
import me.wirlie.allbanks.utils.ShopUtil;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 *
 */
public class CommandItemInfo extends Command {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
			return true;
		}
		
		if(!Util.hasPermission(sender, "allbanks.commands.iteminfo")){
			Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
			if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
			return true;
		}
		
		Player p = (Player) sender;
		ItemStack itemHand = p.getInventory().getItemInMainHand();
		
		if(ShopUtil.itemNeedResolveCustomDurability(itemHand)){
			String resolveID = ShopUtil.resolveCustomDurabilityIDFor(itemHand);
			
			if(resolveID == null) throw new NullPointerException("Cannot resolve a custom ID, null returned");
			
			String name = ItemNameUtil.getItemName(itemHand);

			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.NAME, false)[0] + ": " + ChatColor.GRAY + name);
			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.DURABILITY, false)[0] + ": " + ChatColor.GRAY + name + ChatColor.AQUA + resolveID);
			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.SHOP_FOR_SHOP_LINE, false)[0] + ": " + ChatColor.GRAY + name + resolveID);
		} else {
			
			String name = ItemNameUtil.getItemName(itemHand);
			
			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.NAME, false)[0] + ": " + ChatColor.GRAY + name);
			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.DURABILITY, false)[0] + ": " + ChatColor.GRAY + itemHand.getDurability());
			sender.sendMessage(ChatColor.GOLD + Translation.get(StringsID.SHOP_FOR_SHOP_LINE, false)[0] + ": " + ChatColor.GRAY + name + ":" + itemHand.getDurability());
		}
		return true;
	}
}
