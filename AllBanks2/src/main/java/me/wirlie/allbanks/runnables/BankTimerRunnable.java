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
package me.wirlie.allbanks.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Banks.ABSignType;
import me.wirlie.allbanks.logger.AllBanksLogger;
import me.wirlie.allbanks.tempdata.BankAccount;
import me.wirlie.allbanks.tempdata.BankSession;
import me.wirlie.allbanks.utils.DataBaseUtil;

/**
 * Runnable para añadir 1 minuto a BankTime cada X segundos.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class BankTimerRunnable extends BukkitRunnable {

	public void run() {
		
		if(DataBaseUtil.databaseIsLocked()){
			AllBanks.getInstance().getLogger().info("[BankTimeRunnable] Database is locked! Aborting...");
			return;
		}
		
		AllBanksLogger.info("BankTimerRunnable: Executed.");
		
		int total = 0;
		
		for(Player p : Bukkit.getOnlinePlayers()){
			BankAccount ba = BankAccount.Cache.get(p.getUniqueId());
			
			if(ba == null){
				//Ops... error
				continue;
			}
			
			total ++;
			
			if(!ba.BankTime.updateTimePlusOne()){
				continue;
			}else{
				BankSession tryBs = BankSession.getSession(p);
				if(tryBs != null){
					if(tryBs.getBankType().equals(ABSignType.BANK_TIME)){
						tryBs.reloadSign();
					}
				}
			}
		}
		
		AllBanksLogger.info("BankTimerRunnable: " + total + " entries updated.");
	}

}
