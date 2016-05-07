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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * @author Wirlie
 *
 */
public class CommandHelp extends Command {
	
	@Override
	public CommandExecuteResult execute(CommandSender sender, String[] args) {
		//Comando de ayuda
		if(!Util.hasPermission(sender, "allbanks.commands.help")){
			Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
			if(sender instanceof Player) InteractiveUtil.sendSound((Player) sender, SoundType.DENY);
			return CommandExecuteResult.NO_PERMISSIONS;
		}
		
		int page = 1;
		
		if(args.length > 1) {
			try {
				page = Integer.parseInt(args[1]);
				
				if(page < 1) {
					page = 1;
				}
				
			}catch(NumberFormatException e) {
				page = 1;
			}
		}
		
		int maxPages = 2;
		if(page > maxPages) page = maxPages;
		
		Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + page, "%2%>>>" + maxPages), true);
		
		switch(page) {
		case 1:
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab ? " + ChatColor.AQUA + "[page]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_HELP_DESC.toString(false));
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab toprank " + ChatColor.AQUA + "[bankmoney|bankxp]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_TOPRANK_DESC.toString(false));
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "info" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_INFO_DESC.toString(false));
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "buyticket [amount]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_BUYTICKET_DESC.toString(false));
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "[enable|disable]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_ENABLE_DESC.toString(false));
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "force" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_FORCE_DESC.toString(false));
			
			break;
			
		case 2:
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab reload" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_RELOAD_DESC.toString(false));
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab iteminfo" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_ITEMINFO_DESC.toString(false));
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab database " + ChatColor.AQUA + "try-update" + ChatColor.YELLOW + "<SQL>" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_DATABASE_UPDATE.toString(false));
			sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab database " + ChatColor.AQUA + "try-query" + ChatColor.YELLOW + "<SQL>" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_DATABASE_QUERY.toString(false));

			break;
		}
		
		return CommandExecuteResult.SUCCESS;
	}

}
