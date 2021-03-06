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
package me.wirlie.allbanks.commands.allbanksland;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.command.Command;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;
import me.wirlie.allbanks.utils.chatcomposer.FancyMessage;

/**
 * Comando de ayuda
 * @author Wirlie
 */
public class CommandHelp extends Command {
	
	/**
	 * Comando de ayuda
	 * @param permissionNode Permiso necesario para ejecutar este comando.
	 */
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
					//Mensaje de generando...
					Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
					
					//	/abl help <page>
					FancyMessage help01 = new FancyMessage("")
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
					FancyMessage help02 = new FancyMessage("")
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
					FancyMessage help03 = new FancyMessage("")
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
					FancyMessage help04 = new FancyMessage("")
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
					FancyMessage help05 = new FancyMessage("")
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
					FancyMessage help06 = new FancyMessage("")
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
					
					FancyMessage nextBackButton = prepareNextBackButton(sender, final_page, maxPages);
					
					Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
					help01.send(sender);
					help02.send(sender);
					help03.send(sender);
					help04.send(sender);
					help05.send(sender);
					help06.send(sender);
					nextBackButton.send(sender);
					
					break;
				case 2:
					//Mensaje de generando...
					Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
					
					//	/abl spawn <WorldName>
					FancyMessage help07 = new FancyMessage("")
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
					FancyMessage help08 = new FancyMessage("")
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
					FancyMessage help09 = new FancyMessage("")
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
					FancyMessage help10 = new FancyMessage("")
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
					FancyMessage help11 = new FancyMessage("")
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
					FancyMessage help12 = new FancyMessage("")
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
					
					FancyMessage nextBackButton1 = prepareNextBackButton(sender, final_page, maxPages);
					
					Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
					help07.send(sender);
					help08.send(sender);
					help09.send(sender);
					help10.send(sender);
					help11.send(sender);
					help12.send(sender);
					nextBackButton1.send(sender);
					
					break;
				case 3:
					//Mensaje de generando...
					Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
					
					//	/abl plot deny <Player>
					FancyMessage help13 = new FancyMessage("")
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
					FancyMessage help14 = new FancyMessage("")
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
					FancyMessage help15 = new FancyMessage("")
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
					FancyMessage help16 = new FancyMessage("")
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
					FancyMessage help17 = new FancyMessage("")
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
					FancyMessage help18 = new FancyMessage("")
					.then("#18 /plot ")
						.color(ChatColor.GRAY)
						.suggest("/plot info")
						.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
					.then("info")
						.color(ChatColor.GOLD)
						.suggest("/plot info")
						.tooltip(ChatColor.YELLOW + Translation.get(StringsID.COMMAND_LAND_PLOT_INFO_DESC, false)[0], Translation.get(StringsID.CLICK_TO_USE, false)[0])
					;
					
					FancyMessage nextBackButton2 = prepareNextBackButton(sender, final_page, maxPages);
					
					Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
					help13.send(sender);
					help14.send(sender);
					help15.send(sender);
					help16.send(sender);
					help17.send(sender);
					help18.send(sender);
					nextBackButton2.send(sender);
					
					break;
				case 4:
					//Mensaje de generando...
					Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
					
					//	/abl plot home <#>
					FancyMessage help19 = new FancyMessage("")
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
					FancyMessage help20 = new FancyMessage("")
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
					FancyMessage help21 = new FancyMessage("")
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
					FancyMessage help22 = new FancyMessage("")
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
					
					FancyMessage nextBackButton3 = prepareNextBackButton(sender, final_page, maxPages);
					
					Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + final_page, "%2%>>>" + maxPages), true);
					help19.send(sender);
					help20.send(sender);
					help21.send(sender);
					help22.send(sender);
					nextBackButton3.send(sender);
					
					break;
				}
		
			}
		};
		
		doBackground.start();
		
		return CommandExecuteResult.SUCCESS;
	}
	
	private FancyMessage prepareNextBackButton(CommandSender sender, int page, int maxPages){
		boolean supportBackPage = false;
		boolean supportNextPage = false;
		
		if((page - 1) > 0){
			supportBackPage = true;
		}
		if((page + 1) <= maxPages){
			supportNextPage = true;
		}
		
		if(supportBackPage && !supportNextPage)
			return new FancyMessage("")
			.then("<< " + Translation.get(StringsID.BACK, false)[0])
				.color(ChatColor.YELLOW)
				.command("/abl ? " + (page - 1))
			;
		else if(supportBackPage && supportNextPage)
			return new FancyMessage("")
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
			return new FancyMessage("")
			.then(Translation.get(StringsID.NEXT, false)[0] + " >>")
				.color(ChatColor.YELLOW)
				.command("/abl ? " + (page + 1))
			;
	}

}
