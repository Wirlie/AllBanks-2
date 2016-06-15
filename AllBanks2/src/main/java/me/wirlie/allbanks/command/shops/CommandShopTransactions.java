/*
 * AllBanks2 - AllBanks2 is a plugin for CraftBukkit and Spigot.
 * Copyright (C) 2016 Josue Israel Acevedo Peña (Wirlie)
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

/**
 * 
 */
package me.wirlie.allbanks.command.shops;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.command.Command;
import me.wirlie.allbanks.statistics.AllBanksShopStatistics;
import me.wirlie.allbanks.statistics.AllBanksShopStatistics.GetTransactionParameter;
import me.wirlie.allbanks.statistics.ShopTransactionEntry;
import me.wirlie.allbanks.utils.chatcomposer.BuildChatMessage;

/**
 * @author Wirlie
 *
 */
public class CommandShopTransactions extends Command {

	/**
	 * Constructor del comando.
	 * @param permission Permiso necesario para ejecutar este comando.
	 */
	public CommandShopTransactions(String permission) {
		super(permission);
	}
	
	@Override
	public CommandExecuteResult execute(final CommandSender sender, final String label, final String[] args) {
		
		if(args.length >= 1){
			if(args[0].equalsIgnoreCase("transactions")){
				if(args.length >= 2){
					if(args[1].equalsIgnoreCase("byHour")){
						Translation.getAndSendMessage(sender, StringsID.GENERATING_LIST, true);
						new Thread(){
							@Override
							public void run(){
								List<ShopTransactionEntry> list = AllBanksShopStatistics.getTransactionsBy(GetTransactionParameter.BY_HOUR, null, 0);
								sender.sendMessage(String.valueOf(list.size()));
								for(ShopTransactionEntry entry : list){
									sender.sendMessage(entry.signLoc.toString());
								}
							}
						}.start();
						return CommandExecuteResult.SUCCESS;
					}else if(args[1].equalsIgnoreCase("byDay")){
						Translation.getAndSendMessage(sender, StringsID.GENERATING_LIST, true);
						new Thread(){
							@Override
							public void run(){
								AllBanksShopStatistics.getTransactionsBy(GetTransactionParameter.BY_DAY, null, 0);
							}
						}.start();
						return CommandExecuteResult.SUCCESS;
					}else if(args[1].equalsIgnoreCase("byMonth")){
						Translation.getAndSendMessage(sender, StringsID.GENERATING_LIST, true);
						new Thread(){
							@Override
							public void run(){
								AllBanksShopStatistics.getTransactionsBy(GetTransactionParameter.BY_MONTH, null, 0);
							}
						}.start();
						return CommandExecuteResult.SUCCESS;
					}else if(args[1].equalsIgnoreCase("byYear")){
						Translation.getAndSendMessage(sender, StringsID.GENERATING_LIST, true);
						new Thread(){
							@Override
							public void run(){
								AllBanksShopStatistics.getTransactionsBy(GetTransactionParameter.BY_YEAR, null, 0);
							}
						}.start();
						return CommandExecuteResult.SUCCESS;
					}else if(args[1].equalsIgnoreCase("byTransactionPlayer")){
						if(args.length >= 3){
							final String player = args[2];
							Translation.getAndSendMessage(sender, StringsID.GENERATING_LIST, true);
							new Thread(){
								@Override
								public void run(){
									AllBanksShopStatistics.getTransactionsBy(GetTransactionParameter.BY_TRANSACTION_PLAYER, player, 60 * 60 * 24 * 31);
								}
							}.start();
							return CommandExecuteResult.SUCCESS;
						}
						
						return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
					}else if(args[1].equalsIgnoreCase("byShopOwner")){
						if(args.length >= 3){
							final String player = args[2];
							Translation.getAndSendMessage(sender, StringsID.GENERATING_LIST, true);
							new Thread(){
								@Override
								public void run(){
									AllBanksShopStatistics.getTransactionsBy(GetTransactionParameter.BY_SHOP_OWNER, player, 60 * 60 * 24 * 31);
								}
							}.start();
							return CommandExecuteResult.SUCCESS;
						}
						
						return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
					}else if(args[1].equalsIgnoreCase("byTransactionItem")){
						if(sender instanceof Player){
							Translation.getAndSendMessage(sender, StringsID.GENERATING_LIST, true);
							Player p = (Player) sender;
							final ItemStack item = p.getInventory().getItemInMainHand();
							new Thread(){
								@Override
								public void run(){
									AllBanksShopStatistics.getTransactionsBy(GetTransactionParameter.BY_TRANSACTION_ITEM, item, 60 * 60 * 24 * 31);
								}
							}.start();
							return CommandExecuteResult.SUCCESS;
						}else{
							Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
							return CommandExecuteResult.SUCCESS;
						}
					}
					
					return CommandExecuteResult.INVALID_ARGUMENTS;
				}else{
					//	/abs transactions
					Translation.getAndSendMessage(sender, StringsID.GENERATING, true);
					new Thread(){
						@Override
						public void run(){
							BuildChatMessage msgHeader = new BuildChatMessage(Translation.get(StringsID.SEE_TRANSACTIONS_BY, true)[0]);
							BuildChatMessage msg1 = new BuildChatMessage("[")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								.then(Translation.get(StringsID.TRANSACTION_BY_HOUR, false)[0])
									.color(ChatColor.YELLOW)
									.command("/abs transactions byHour")
								.then("]")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
									;
							BuildChatMessage msg2 = new BuildChatMessage("[")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								.then(Translation.get(StringsID.TRANSACTION_BY_DAY, false)[0])
									.color(ChatColor.YELLOW)
									.command("/abs transactions byDay")
								.then("]")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								;
							BuildChatMessage msg3 = new BuildChatMessage("[")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								.then(Translation.get(StringsID.TRANSACTION_BY_MONTH, false)[0])
									.color(ChatColor.YELLOW)
									.command("/abs transactions byMonth")
								.then("]")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								;
							BuildChatMessage msg4 = new BuildChatMessage("[")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								.then(Translation.get(StringsID.TRANSACTION_BY_YEAR, false)[0])
									.color(ChatColor.YELLOW)
									.command("/abs transactions byYear")
								.then("]")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								;
							BuildChatMessage msg5 = new BuildChatMessage("[")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								.then(Translation.get(StringsID.TRANSACTION_BY_SHOP_OWNER, false)[0])
									.color(ChatColor.YELLOW)
									.suggest("/abs transactions byShopOwner <Player>")
								.then("]")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								;
							BuildChatMessage msg6 = new BuildChatMessage("[")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								.then(Translation.get(StringsID.TRANSACTION_BY_TRANSACTION_PLAYER, false)[0])
									.color(ChatColor.YELLOW)
									.suggest("/abs transactions byTransactionPlayer <Player>")
								.then("]")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								;
							BuildChatMessage msg7 = new BuildChatMessage("[")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								.then(Translation.get(StringsID.TRANSACTION_BY_ITEM, false)[0])
									.color(ChatColor.YELLOW)
									.command("/abs transactions byTransactionItem")
								.then("]")
									.color(ChatColor.BLUE)
									.style(ChatColor.BOLD)
								;
							msgHeader.send(sender);
							msg1.send(sender);
							msg2.send(sender);
							msg3.send(sender);
							msg4.send(sender);
							msg5.send(sender);
							msg6.send(sender);
							msg7.send(sender);
						}
					}.start();
					
					return CommandExecuteResult.SUCCESS;
				}
			}
		}
		
		return CommandExecuteResult.INVALID_ARGUMENTS;
	}
	
}
