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
package me.wirlie.allbanks.land.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.command.Command;
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
		final int maxPages = 4;
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
						
						//	/abl help <page>
						BuildChatMessage help01 = new BuildChatMessage("")
						.then("#01 /abl ")
							.color(ChatColor.GRAY)
							.suggest("/abl help 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_HELP_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("help ")
							.color(ChatColor.GOLD)
							.suggest("/abl help 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_HELP_HELP_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Page]")
							.color(ChatColor.GREEN)
							.suggest("/abl help 1")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PAGE, StringsID.CLICK_TO_USE))
						;
						
						//	/abl admin world <WorldName> generate
						BuildChatMessage help02 = new BuildChatMessage("")
						.then("#02 /abl ")
							.color(ChatColor.GRAY)
							.suggest("/abl admin world [WorldName] generate")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_GENERATE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("admin world ")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] generate")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_GENERATE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[WorldName] ")
							.color(ChatColor.GREEN)
							.suggest("/abl admin world [WorldName] generate")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_LAND_ARG_NEW_WORLDNAME, StringsID.CLICK_TO_USE))
						.then("generate")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] generate")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_GENERATE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl admin world <WorldName> unload
						BuildChatMessage help03 = new BuildChatMessage("")
						.then("#03 /abl ")
							.color(ChatColor.GRAY)
							.suggest("/abl admin world [WorldName] unload")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_UNLOAD_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("admin world ")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] unload")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_UNLOAD_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[WorldName] ")
							.color(ChatColor.GREEN)
							.suggest("/abl admin world [WorldName] unload")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_LAND_ARG_UNLOAD_WORLDNAME, StringsID.CLICK_TO_USE))
						.then("unload")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] unload")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_UNLOAD_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl admin world <WorldName> remove
						BuildChatMessage help04 = new BuildChatMessage("")
						.then("#04 /abl ")
							.color(ChatColor.GRAY)
							.suggest("/abl admin world [WorldName] remove")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_REMOVE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("admin world ")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] remove")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_REMOVE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[WorldName] ")
							.color(ChatColor.GREEN)
							.suggest("/abl admin world [WorldName] remove")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_LAND_ARG_REMOVE_WORLDNAME, StringsID.CLICK_TO_USE))
						.then("remove")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] remove")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_REMOVE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl admin world <WorldName> info
						BuildChatMessage help05 = new BuildChatMessage("")
						.then("#05 /abl ")
							.color(ChatColor.GRAY)
							.suggest("/abl admin world [WorldName] info")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("admin world ")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] info")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[WorldName] ")
							.color(ChatColor.GREEN)
							.suggest("/abl admin world [WorldName] info")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_LAND_ARG_WORLDNAME, StringsID.CLICK_TO_USE))
						.then("info")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] info")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl admin world <WorldName> set <Flag> <Value>
						BuildChatMessage help06 = new BuildChatMessage("")
						.then("#06 /abl ")
							.color(ChatColor.GRAY)
							.suggest("/abl admin world [WorldName] set [Flag] [Value]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_SETFLAG_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("admin world ")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] set [Flag] [Value]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_SETFLAG_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[WorldName] ")
							.color(ChatColor.GREEN)
							.suggest("/abl admin world [WorldName] set [Flag] [Value]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_LAND_ARG_WORLDNAME, StringsID.CLICK_TO_USE))
						.then("set ")
							.color(ChatColor.GOLD)
							.suggest("/abl admin world [WorldName] set [Flag] [Value]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_SETFLAG_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Flag] ")
							.color(ChatColor.GREEN)
							.suggest("/abl admin world [WorldName] set [Flag] [Value]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_LAND_ARG_SETFLAG_FLAG, StringsID.CLICK_TO_USE))
						.then("[Value] ")
							.color(ChatColor.GREEN)
							.suggest("/abl admin world [WorldName] set [Flag] [Value]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_LAND_ARG_SETFLAG_VALUE, StringsID.CLICK_TO_USE))
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
						
					}else{
						//Enviado desde la consola.
						//TODO Comandos
					}
					
					break;
					
				case 2:
					if(sender instanceof Player){
						
						//Mensaje de generando...
						Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
						
						//	/abl spawn <WorldName>
						BuildChatMessage help07 = new BuildChatMessage("")
						.then("#07 /abl ")
							.color(ChatColor.GRAY)
							.suggest("/abl spawn [WorldName]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_SPAWN_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("spawn ")
							.color(ChatColor.GOLD)
							.suggest("/abl spawn [WorldName]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_SPAWN_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[WorldName]")
							.color(ChatColor.GREEN)
							.suggest("/abl spawn [WorldName]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_LAND_ARG_WORLDNAME, StringsID.CLICK_TO_USE))
						;
						
						//	/abl plot help
						BuildChatMessage help08 = new BuildChatMessage("")
						.then("#08 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot help")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_GENERALCOMMAND_HELP_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("help")
							.color(ChatColor.GOLD)
							.suggest("/plot help")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_GENERALCOMMAND_HELP_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl plot claim
						BuildChatMessage help09 = new BuildChatMessage("")
						.then("#09 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot claim")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_CLAIM_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("claim")
							.color(ChatColor.GOLD)
							.suggest("/plot claim")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_CLAIM_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl plot dispose
						BuildChatMessage help10 = new BuildChatMessage("")
						.then("#10 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot dispose")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_DISPOSE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("dispose")
							.color(ChatColor.GOLD)
							.suggest("/plot dispose")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_DISPOSE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl plot add <Player>
						BuildChatMessage help11 = new BuildChatMessage("")
						.then("#11 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot add [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_ADD_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("add ")
							.color(ChatColor.GOLD)
							.suggest("/plot add [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_ADD_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Player]")
							.color(ChatColor.GREEN)
							.suggest("/plot add [Player]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PLAYER, StringsID.CLICK_TO_USE))
						;
						
						//	/abl plot add <Player>
						BuildChatMessage help12 = new BuildChatMessage("")
						.then("#12 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot remove [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_REMOVE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("remove ")
							.color(ChatColor.GOLD)
							.suggest("/plot remove [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_REMOVE_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Player]")
							.color(ChatColor.GREEN)
							.suggest("/plot remove [Player]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PLAYER, StringsID.CLICK_TO_USE))
						;
						
						BuildChatMessage nextBackButton = prepareNextBackButton(sender, final_page, maxPages);
						
						Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
						help07.send(sender);
						help08.send(sender);
						help09.send(sender);
						help10.send(sender);
						help11.send(sender);
						help12.send(sender);
						nextBackButton.send(sender);
						
					}else{
						//TODO Comandos
					}
					break;
					
				case 3:
					
					if(sender instanceof Player){
						
						//Mensaje de generando...
						Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
						
						//	/abl plot deny <Player>
						BuildChatMessage help13 = new BuildChatMessage("")
						.then("#13 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot deny [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_DENY_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("deny ")
							.color(ChatColor.GOLD)
							.suggest("/plot deny [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_DENY_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Player]")
							.color(ChatColor.GREEN)
							.suggest("/plot deny [Player]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PLAYER, StringsID.CLICK_TO_USE))
						;
						
						//	/abl plot undeny <Player>
						BuildChatMessage help14 = new BuildChatMessage("")
						.then("#14 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot undeny [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_UNDENY_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("undeny ")
							.color(ChatColor.GOLD)
							.suggest("/plot undeny [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_UNDENY_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Player]")
							.color(ChatColor.GREEN)
							.suggest("/plot undeny [Player]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PLAYER, StringsID.CLICK_TO_USE))
						;
						
						//	/abl plot set <Flag> <Value>
						BuildChatMessage help15 = new BuildChatMessage("")
						.then("#15 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot set [Flag] [Value]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_SETFLAG_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("set ")
							.color(ChatColor.GOLD)
							.suggest("/plot set [Flag] [Value]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_SETFLAG_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Flag] ")
							.color(ChatColor.GREEN)
							.suggest("/plot set [Flag] [Value]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PLOT_SETFLAG_FLAG, StringsID.CLICK_TO_USE))
						.then("[Value]")
							.color(ChatColor.GREEN)
							.suggest("/plot set [Flag] [Value]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PLOT_SETFLAG_VALUE, StringsID.CLICK_TO_USE))
						;
						
						//	/abl plot dispose
						BuildChatMessage help16 = new BuildChatMessage("")
						.then("#16 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot setHomeSpawn")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_SETHOMESPAWN_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("setHomeSpawn")
							.color(ChatColor.GOLD)
							.suggest("/plot setHomeSpawn")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_SETHOMESPAWN_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl plot dispose
						BuildChatMessage help17 = new BuildChatMessage("")
						.then("#17 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot setShopSpawn")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_SETSHOPPAWN_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("setShopSpawn")
							.color(ChatColor.GOLD)
							.suggest("/plot setShopSpawn")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_SETSHOPPAWN_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl plot info
						BuildChatMessage help18 = new BuildChatMessage("")
						.then("#18 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot info")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("info")
							.color(ChatColor.GOLD)
							.suggest("/plot info")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						BuildChatMessage nextBackButton = prepareNextBackButton(sender, final_page, maxPages);
						
						Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
						help13.send(sender);
						help14.send(sender);
						help15.send(sender);
						help16.send(sender);
						help17.send(sender);
						help18.send(sender);
						nextBackButton.send(sender);
					}else{
						//TODO Comandos.
					}
					
					break;
					
				case 4:
					
					if(sender instanceof Player){
						
						//Mensaje de generando...
						Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
						
						//	/abl plot home <#>
						BuildChatMessage help19 = new BuildChatMessage("")
						.then("#19 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot home 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_HOME_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("home ")
							.color(ChatColor.GOLD)
							.suggest("/plot home 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_HOME_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[#]")
							.color(ChatColor.GREEN)
							.suggest("/plot home 1")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_LAND_HOMENUMBER_DESC, StringsID.CLICK_TO_USE))
						;
						
						//	/abl plot auto
						BuildChatMessage help20 = new BuildChatMessage("")
						.then("#20 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot auto")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_AUTOCLAIM_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("auto")
							.color(ChatColor.GOLD)
							.suggest("/plot auto")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_AUTOCLAIM_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						;
						
						//	/abl plot list <page>
						BuildChatMessage help21 = new BuildChatMessage("")
						.then("#21 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot list 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_LIST_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("list ")
							.color(ChatColor.GOLD)
							.suggest("/plot list 1")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_LIST_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Page]")
							.color(ChatColor.GREEN)
							.suggest("/plot list 1")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_PAGE, StringsID.CLICK_TO_USE))
						;
						
						//	/abl plot teleport <Player>
						BuildChatMessage help22 = new BuildChatMessage("")
						.then("#22 /plot ")
							.color(ChatColor.GRAY)
							.suggest("/plot teleport [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_TELEPORT_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("teleport ")
							.color(ChatColor.GOLD)
							.suggest("/plot teleport [Player]")
							.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_TELEPORT_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
						.then("[Player]")
							.color(ChatColor.GREEN)
							.suggest("/plot teleport [Player]")
							.tooltip(Translation.getMultiple(false, StringsID.COMMAND_HELP_TOOLTIP_ARG_TELEPORT_PLAYER, StringsID.CLICK_TO_USE))
						;
						
						BuildChatMessage nextBackButton = prepareNextBackButton(sender, final_page, maxPages);
						
						Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
						help19.send(sender);
						help20.send(sender);
						help21.send(sender);
						help22.send(sender);
						nextBackButton.send(sender);
					}else{
						//TODO Comandos
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
				.command("/abl ? " + (page - 1))
			;
		else if(supportBackPage && supportNextPage)
			return new BuildChatMessage("")
			.then("<< " + Translation.get(StringsID.BACK, false)[0])
				.color(ChatColor.YELLOW)
				.command("/abl ? " + (page - 1))
			.then(" | " )
				.color(ChatColor.WHITE)
			.then(Translation.get(StringsID.NEXT, false)[0] + " >>")
				.color(ChatColor.YELLOW)
				.command("/abl ? " + (page + 1))
			;
		else
			return new BuildChatMessage("")
			.then(Translation.get(StringsID.NEXT, false)[0] + " >>")
				.color(ChatColor.YELLOW)
				.command("/abl ? " + (page + 1))
			;
	}

}
