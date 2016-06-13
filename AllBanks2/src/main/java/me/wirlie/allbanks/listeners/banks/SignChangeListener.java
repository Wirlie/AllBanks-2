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
package me.wirlie.allbanks.listeners.banks;

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
import me.wirlie.allbanks.Banks.ABSignAction;
import me.wirlie.allbanks.Banks.ABSignType;
import me.wirlie.allbanks.utils.AllBanksLogger;
import me.wirlie.allbanks.utils.InteractiveUtil;

/**
 * Detecta cuando un letrero de AllBanks está siendo intentado crearse.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
@SuppressWarnings("javadoc")
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
			
			ABSignType btype = ABSignType.DEFAULT;
			
			if(!e.getBlock().getType().equals(Material.WALL_SIGN)){
				Translation.getAndSendMessage(p, StringsID.ONLY_WALL_SIGN, true);
				if(p.getGameMode().equals(GameMode.CREATIVE))
					e.getBlock().setType(Material.AIR);
				else
					e.getBlock().breakNaturally();
			}
			
			if(lines[1].equalsIgnoreCase("BankLoan") || lines[1].equalsIgnoreCase("loan")){
				
				if(!AllBanks.getInstance().getConfig().getBoolean("modules.banks.bank-loan.enable")){
					Translation.getAndSendMessage(p, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>BankLoan"), true);
					e.setCancelled(true);
					return;
				}
				
				btype = ABSignType.BANK_LOAN;
			}else if(lines[1].equalsIgnoreCase("BankXP") || lines[1].equalsIgnoreCase("XP")){
				
				if(!AllBanks.getInstance().getConfig().getBoolean("modules.banks.bank-xp.enable")){
					Translation.getAndSendMessage(p, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>BankXP"), true);
					e.setCancelled(true);
					return;
				}
				
				btype = ABSignType.BANK_XP;
			}else if(lines[1].equalsIgnoreCase("BankChest") || lines[1].equalsIgnoreCase("Chest")){
				
				if(!AllBanks.getInstance().getConfig().getBoolean("modules.banks.bank-chest.enable")){
					Translation.getAndSendMessage(p, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>BankChest"), true);
					e.setCancelled(true);
					return;
				}
				
				btype = ABSignType.BANK_CHEST;
			}else if(lines[1].equalsIgnoreCase("BankTime") || lines[1].equalsIgnoreCase("Time")){
				
				if(!AllBanks.getInstance().getConfig().getBoolean("modules.banks.bank-time.enable")){
					Translation.getAndSendMessage(p, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>BankTime"), true);
					e.setCancelled(true);
					return;
				}
				
				btype = ABSignType.BANK_TIME;
			}else if(lines[1].equalsIgnoreCase("BankMoney") || lines[1].equalsIgnoreCase("Money")){
				
				if(!AllBanks.getInstance().getConfig().getBoolean("modules.banks.bank-money.enable")){
					Translation.getAndSendMessage(p, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>BankMoney"), true);
					e.setCancelled(true);
					return;
				}
				
				btype = ABSignType.BANK_MONEY;
			}else if(lines[1].equalsIgnoreCase("ATM")){
				btype = ABSignType.ATM;
			}
			
			if(btype.equals(ABSignType.DEFAULT)){
				//No se especificó el segundo argumento...
				Translation.getAndSendMessage(p, StringsID.SIGN_MORE_ARGUMENTS_NEEDED, true);
				if(p.getGameMode().equals(GameMode.CREATIVE))
					e.getBlock().setType(Material.AIR);
				else
					e.getBlock().breakNaturally();
				return;
			}
			
			//Checar permiso
			if(!Banks.playerHasPermissionForPerformAction(p, ABSignAction.NEW_SIGN, btype)){
				Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				e.setCancelled(true);
				e.getBlock().breakNaturally();
				
				AllBanksLogger.warning("NEW-BANK: Player " + p.getName() + " (" + p.getDisplayName() + ") has tried to create a bank sign. (Deny cause: permissions)(Location: world:" + signLoc.getWorld().getName() + ", x:" + signLoc.getX() + ", y:" + signLoc.getY() + ", z:" + signLoc.getZ() + ").");
				return;
			}
			
			final ABSignType btypefinal = btype;
			
			//Bien, pasar y formatear letrero
			new BukkitRunnable(){

				public void run() {
					if(e.getBlock().getType().equals(Material.AIR))
						return;
					
					if(Banks.registerNewABSign(e.getBlock().getLocation(), p)){
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
