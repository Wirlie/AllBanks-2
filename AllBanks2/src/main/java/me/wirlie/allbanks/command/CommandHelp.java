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
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;
import me.wirlie.allbanks.utils.chatcomposer.BuildChatMessage;

/**
 * @author Wirlie
 *
 */
public class CommandHelp extends Command {
	
	public CommandHelp(String permissionNode){
		super(permissionNode);
	}
	
	@Override
	public CommandExecuteResult execute(final CommandSender sender, String label, String[] args) {
		//Comando de ayuda
		if(!this.hasPermission(sender)){
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
		
		final int final_page = page;
		
		final int maxPages = 2;
		if(page > maxPages) page = maxPages;
		
		Thread doBackground = new Thread(){
			@Override
			public void run(){
				switch(final_page) {
				case 1:
					
					if(sender instanceof Player){
						//Jugador
						
						//Mensaje de generando...
						Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
						
						//	/ab ? <pagina>
						BuildChatMessage help01 = new BuildChatMessage("")
							.then("#01 /ab ")
								.color(ChatColor.GRAY)
								.suggest("/ab ? 1")
								.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_HELP_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
							.then("? ")
								.color(ChatColor.GOLD)
								.suggest("/ab ? 1")
								.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_HELP_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
							.then("[page]")
								.color(ChatColor.GREEN)
								.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PAGE, StringsID.CLICK_TO_USE))
								.suggest("/ab ? 1")
							;
						;
						
						//	/ab toprank bankmoney <page>
						BuildChatMessage help02 = new BuildChatMessage("")
						.then("#02 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab toprank bankmoney 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_TOPRANK_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("toprank bankmoney ")
							.color(ChatColor.GOLD)
							.suggest("/ab toprank bankmoney 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_TOPRANK_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[page]")
							.color(ChatColor.GREEN)
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PAGE, StringsID.CLICK_TO_USE))
							.suggest("/ab toprank bankmoney 1")
						;
						
						//	/ab toprank bankxp <page>
						BuildChatMessage help03 = new BuildChatMessage("")
						.then("#03 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab toprank bankxp 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_TOPRANK_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("toprank bankxp ")
							.color(ChatColor.GOLD)
							.suggest("/ab toprank bankxp 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_TOPRANK_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[page]")
							.color(ChatColor.GREEN)
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PAGE, StringsID.CLICK_TO_USE))
							.suggest("/ab toprank bankxp 1")
						;
						
						//	/ab lottery info
						BuildChatMessage help04 = new BuildChatMessage("")
						.then("#04 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab lottery info")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("lottery info")
							.color(ChatColor.GOLD)
							.suggest("/ab lottery info")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/ab lottery buyticket <amount>
						BuildChatMessage help05 = new BuildChatMessage("")
						.then("#05 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab lottery buyticket 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_BUYTICKET_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("lottery buyticket ")
							.color(ChatColor.GOLD)
							.suggest("/ab lottery buyticket 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_BUYTICKET_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[amount]")
							.color(ChatColor.GREEN)
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_BUYTICKET_AMOUNT, StringsID.CLICK_TO_USE))
							.suggest("/ab lottery buyticket 1")
						;
						
						//	/ab lottery enable
						BuildChatMessage help06 = new BuildChatMessage("")
						.then("#06 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab lottery enable")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_ENABLE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("lottery enable")
							.color(ChatColor.GOLD)
							.suggest("/ab lottery enable")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_ENABLE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						BuildChatMessage nextBackButton = prepareNextBackButton(sender, final_page, maxPages);
						
						Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
						help01.send(sender);
						help02.send(sender);
						help03.send(sender);
						help04.send(sender);
						help05.send(sender);
						help06.send(sender);
						nextBackButton.send(sender);
						
					;
					
					}else{
						//Enviado desde la consola.
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab ? " + ChatColor.AQUA + "[page]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_HELP_DESC.toString(false));
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab toprank " + ChatColor.AQUA + "[bankmoney|bankxp]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_TOPRANK_DESC.toString(false));
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "info" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_INFO_DESC.toString(false));
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "buyticket [amount]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_BUYTICKET_DESC.toString(false));
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "[enable|disable]" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_ENABLE_DESC.toString(false));
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab lottery " + ChatColor.AQUA + "force" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_LOTTERY_FORCE_DESC.toString(false));
					}
					
					break;
					
				case 2:
					if(sender instanceof Player){
						
						//Mensaje de generando...
						Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
						
						//	/ab lottery enable
						BuildChatMessage help07 = new BuildChatMessage("")
						.then("#07 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab lottery disable")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_ENABLE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("lottery disable")
							.color(ChatColor.GOLD)
							.suggest("/ab lottery disable")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_ENABLE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/ab lottery force
						BuildChatMessage help08 = new BuildChatMessage("")
						.then("#08 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab lottery force")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_FORCE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("lottery force")
							.color(ChatColor.GOLD)
							.suggest("/ab lottery force")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_LOTTERY_FORCE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/ab iteminfo
						BuildChatMessage help09 = new BuildChatMessage("")
						.then("#09 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab iteminfo")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_ITEMINFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("iteminfo")
							.color(ChatColor.GOLD)
							.suggest("/ab iteminfo")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_ITEMINFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/ab database try-update <SQL>
						BuildChatMessage help10 = new BuildChatMessage("")
						.then("#10 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab database try-update <SQL>")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_DATABASE_UPDATE, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("database try-update ")
							.color(ChatColor.GOLD)
							.suggest("/ab database try-update <SQL>")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_DATABASE_UPDATE, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("<SQL>")
							.color(ChatColor.GREEN)
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_SQL_SYNTAX, StringsID.CLICK_TO_USE))
							.suggest("/ab database try-update <SQL>")
						;
						
						//	/ab database try-update <SQL>
						BuildChatMessage help11 = new BuildChatMessage("")
						.then("#11 /ab ")
							.color(ChatColor.GRAY)
							.suggest("/ab database try-query <SQL>")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_DATABASE_QUERY, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("database try-query ")
							.color(ChatColor.GOLD)
							.suggest("/ab database try-query <SQL>")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_DATABASE_QUERY, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("<SQL>")
							.color(ChatColor.GREEN)
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_SQL_SYNTAX, StringsID.CLICK_TO_USE))
							.suggest("/ab database try-query <SQL>")
						;
						
						BuildChatMessage nextBackButton = prepareNextBackButton(sender, final_page, maxPages);
						
						Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
						help07.send(sender);
						help08.send(sender);
						help09.send(sender);
						help10.send(sender);
						help11.send(sender);
						nextBackButton.send(sender);
					}else{
						//Consola
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab iteminfo" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_ITEMINFO_DESC.toString(false));
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab database " + ChatColor.AQUA + "try-update" + ChatColor.YELLOW + "<SQL>" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_DATABASE_UPDATE.toString(false));
						sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab database " + ChatColor.AQUA + "try-query" + ChatColor.YELLOW + "<SQL>" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_DATABASE_QUERY.toString(false));
					}
					break;
				}
			}
		};
		
		doBackground.start();
		
		return CommandExecuteResult.SUCCESS;
	}
	
	public BuildChatMessage prepareNextBackButton(CommandSender sender, int page, int maxPages){
		boolean supportBackPage = false;
		boolean supportNextPage = false;
		
		if((page - 1) > 0){
			supportBackPage = true;
		}
		if((page + 1) <= maxPages){
			supportNextPage = true;
		}
		
		if(supportBackPage && !supportNextPage)
			return new BuildChatMessage("")
			.then("<< " + Translation.get(StringsID.BACK, false)[0])
				.color(ChatColor.YELLOW)
				.command("/ab ? " + (page - 1))
			;
		else if(supportBackPage && supportNextPage)
			return new BuildChatMessage("")
			.then("<< " + Translation.get(StringsID.BACK, false)[0])
				.color(ChatColor.YELLOW)
				.command("/ab ? " + (page - 1))
			.then(" | " )
				.color(ChatColor.WHITE)
			.then(Translation.get(StringsID.NEXT, false)[0] + " >>")
				.color(ChatColor.YELLOW)
				.command("/ab ? " + (page + 1))
			;
		else
			return new BuildChatMessage("")
			.then(Translation.get(StringsID.NEXT, false)[0] + " >>")
				.color(ChatColor.YELLOW)
				.command("/ab ? " + (page + 1))
			;
	}

}
