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
package me.wirlie.allbanks.runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.AllBanksLogger;
import me.wirlie.allbanks.Banks.BankType;
import me.wirlie.allbanks.Util.DatabaseUtil;
import me.wirlie.allbanks.data.BankAccount;
import me.wirlie.allbanks.data.BankSession;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class BankTimerRunnable extends BukkitRunnable {

	public void run() {
		
		if(DatabaseUtil.databaseIsLocked()){
			AllBanks.getInstance().getLogger().info("[BankTimeRunnable] Database is locked! Aborting...");
			return;
		}
		
		AllBanksLogger.getLogger().info("BankTimerRunnable: Executed.");
		
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
					if(tryBs.getBankType().equals(BankType.BANK_TIME)){
						tryBs.reloadSign();
					}
				}
			}
		}
		
		AllBanksLogger.getLogger().info("BankTimerRunnable: " + total + " entries updated.");
	}

}
