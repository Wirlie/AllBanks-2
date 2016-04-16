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
package me.wirlie.allbanks.listeners;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.Banks.PlayerAction;
import me.wirlie.allbanks.Banks.BankType;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.util.InteractiveUtil;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class SignChangeListener implements Listener {
	
	public SignChangeListener(){
		AllBanksLogger.info("SignChangeListener");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSignChange(final SignChangeEvent e){
		String[] lines = e.getLines();
		final Player p = e.getPlayer();
		final Location signLoc = e.getBlock().getLocation();
		
		if(lines[0].equalsIgnoreCase("AllBanks") || lines[0].equalsIgnoreCase("[AllBanks]") || lines[0].equalsIgnoreCase("All Banks")){
			
			BankType btype = BankType.DEFAULT;
			
			if(!e.getBlock().getType().equals(Material.WALL_SIGN)){
				Translation.getAndSendMessage(p, StringsID.ONLY_WALL_SIGN, true);
				if(p.getGameMode().equals(GameMode.CREATIVE))
					e.getBlock().setType(Material.AIR);
				else
					e.getBlock().breakNaturally();
			}
			
			if(lines[1].equalsIgnoreCase("BankLoan") || lines[1].equalsIgnoreCase("loan")){
				btype = BankType.BANK_LOAN;
			}else if(lines[1].equalsIgnoreCase("BankXP") || lines[1].equalsIgnoreCase("XP")){
				btype = BankType.BANK_XP;
			}else if(lines[1].equalsIgnoreCase("BankChest") || lines[1].equalsIgnoreCase("Chest")){
				btype = BankType.BANK_CHEST;
			}else if(lines[1].equalsIgnoreCase("Shop")){
				btype = BankType.SHOP;
			}else if(lines[1].equalsIgnoreCase("BankTime") || lines[1].equalsIgnoreCase("Time")){
				btype = BankType.BANK_TIME;
			}else if(lines[1].equalsIgnoreCase("BankMoney") || lines[1].equalsIgnoreCase("Money")){
				btype = BankType.BANK_MONEY;
			}else if(lines[1].equalsIgnoreCase("BankLand") || lines[1].equalsIgnoreCase("Land")){
				btype = BankType.BANK_LAND;
			}else if(lines[1].equalsIgnoreCase("ATM")){
				btype = BankType.ATM;
			}
			
			if(btype.equals(BankType.DEFAULT)){
				//No se especificó el segundo argumento...
				Translation.getAndSendMessage(p, StringsID.SIGN_MORE_ARGUMENTS_NEEDED, true);
				if(p.getGameMode().equals(GameMode.CREATIVE))
					e.getBlock().setType(Material.AIR);
				else
					e.getBlock().breakNaturally();
				return;
			}
			
			//Checar permiso
			if(!Banks.playerHasPermissions(p, PlayerAction.NEW_SIGN, btype)){
				Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				e.setCancelled(true);
				e.getBlock().breakNaturally();
				
				AllBanksLogger.warning("NEW-BANK: Player " + p.getName() + " (" + p.getDisplayName() + ") has tried to create a bank sign. (Deny cause: permissions)(Location: world:" + signLoc.getWorld().getName() + ", x:" + signLoc.getX() + ", y:" + signLoc.getY() + ", z:" + signLoc.getZ() + ").");
				return;
			}
			
			final BankType btypefinal = btype;
			
			//Bien, pasar y formatear letrero
			new BukkitRunnable(){

				public void run() {
					if(e.getBlock().getType().equals(Material.AIR))
						return;
					
					if(btypefinal.equals(BankType.BANK_LAND) || btypefinal.equals(BankType.ATM)) {
						Translation.getAndSendMessage(p, StringsID.NOT_YET_IMPLEMENTED, true);
						e.getBlock().breakNaturally();
						return;
					}
					
					if(Banks.registerAllBanksSign(e.getBlock().getLocation(), p)){
						Banks.switchSignToInitialState((Sign) e.getBlock().getState(), btypefinal);
						AllBanksLogger.info("NEW-BANK: Player " + p.getName() + " (" + p.getDisplayName() + ") has created a new bank. (Location: world:" + signLoc.getWorld().getName() + ", x:" + signLoc.getX() + ", y:" + signLoc.getY() + ", z:" + signLoc.getZ() + ").");
						
						InteractiveUtil.playFireworkEffect(signLoc.add(0.5, 0, 0.5), 1, Type.STAR, -1, Color.LIME, Color.WHITE, Color.GREEN);
					}else{
						e.getBlock().breakNaturally();
						Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
					}
				}

			}.runTaskLater(AllBanks.getInstance(), 10);
		}
	}
	
}
