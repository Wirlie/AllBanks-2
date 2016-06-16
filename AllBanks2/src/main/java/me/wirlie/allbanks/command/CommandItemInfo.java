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
package me.wirlie.allbanks.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
	
	/**
	 * Comando para monstrar información sobre el objeto en la mano.
	 * @param permissionNode Permiso necesario para ejecutar este comando.
	 */
	public CommandItemInfo(String permissionNode){
		super(permissionNode);
	}
	
	@Override
	public CommandExecuteResult execute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
			return CommandExecuteResult.OTHER;
		}
		
		if(!this.hasPermission(sender)){
			Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
			if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
			return CommandExecuteResult.NO_PERMISSIONS;
		}
		
		if(args[0].equalsIgnoreCase("iteminfo")){
			Player p = (Player) sender;
			ItemStack itemHand = p.getInventory().getItemInMainHand();
			
			if(ItemNameUtil.itemIsOnBlackList(itemHand)){
				//Un objeto en la lista negra no debe ser mostrado.
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ITEMINFO_INVALID_ITEM, true);
			}else{
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
			}
			return CommandExecuteResult.SUCCESS;
		}else if(args[0].equalsIgnoreCase("showpreview")){
			if(!(sender instanceof Player)) {
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			if(!this.hasPermission(sender)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			Player p = (Player) sender;
			
			Location loc = Util.convertStringToLocation(args[1]);
			Inventory fakeInv = Bukkit.createInventory(p, 9, "AB2:ItemPreview");
			
			fakeInv.setItem(0, ShopUtil.getItemStack(loc)); //slot 1
			
			ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE);
			glassPane.setDurability((short) 14);
			
			fakeInv.setItem(1, glassPane); //slot 2
			fakeInv.setItem(2, glassPane); //slot 3
			fakeInv.setItem(3, glassPane); //slot 4
			fakeInv.setItem(4, glassPane); //slot 5
			fakeInv.setItem(5, glassPane); //slot 6
			fakeInv.setItem(6, glassPane); //slot 7
			fakeInv.setItem(7, glassPane); //slot 8
			fakeInv.setItem(8, glassPane); //slot 9
			
			p.openInventory(fakeInv);
		}
		
		return CommandExecuteResult.INVALID_ARGUMENTS;
	}
}
