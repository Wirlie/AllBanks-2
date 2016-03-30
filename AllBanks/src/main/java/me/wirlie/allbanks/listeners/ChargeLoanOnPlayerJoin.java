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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.Util.DatabaseUtil;
import me.wirlie.allbanks.data.BankAccount;
import me.wirlie.allbanks.logger.AllBanksLogger;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ChargeLoanOnPlayerJoin implements Listener {
	
	public ChargeLoanOnPlayerJoin(){
		AllBanksLogger.info("ChargeLoanOnPlayerJoin");
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent e){
		
		if(DatabaseUtil.databaseIsLocked()) return;
		
		Statement stm = null;
		ResultSet res = null;
		
		try {
			stm = AllBanks.getDBC().createStatement();
			res = stm.executeQuery("SELECT * FROM bankloan_pending_charges WHERE owner = '" + e.getPlayer().getName() + "'");
		
			BigDecimal totalCharge = BigDecimal.ZERO;
			
			while(res.next()){
				BigDecimal amount = new BigDecimal(res.getString("amount"));
				totalCharge = totalCharge.add(amount);
			}
			
			stm.executeUpdate("DELETE FROM bankloan_pending_charges WHERE owner = '" + e.getPlayer().getName() + "'");
			
			BigDecimal minPlayerBalance = new BigDecimal(AllBanks.getInstance().getConfig().getDouble("banks.bank-loan.stop-collect-if-player-balance-is-minor-than", -500));
			BigDecimal currentPlayerBalance = new BigDecimal(AllBanks.getEconomy().getBalance(e.getPlayer()));
			
			if(currentPlayerBalance.compareTo(minPlayerBalance) == -1){
				//es menor...
				return;
			}
			
			if(currentPlayerBalance.subtract(totalCharge).compareTo(minPlayerBalance) == -1){
				totalCharge = currentPlayerBalance.subtract(minPlayerBalance);
			}
			
			if(totalCharge.intValueExact() == 0){
				return;
			}
			
			final BigDecimal totalChargeFinal = totalCharge;
			
			new BukkitRunnable(){

				public void run() {
					BankAccount ba = BankAccount.Cache.get(e.getPlayer().getUniqueId());
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", AllBanks.getEconomy().format(totalChargeFinal.doubleValue()));
					replaceMap.put("%2%", AllBanks.getEconomy().format(ba.BankLoan.getLoan().doubleValue()));
					
					Translation.getAndSendMessage(e.getPlayer(), StringsID.BANKLOAN_INTEREST_CHARGED, replaceMap, true);
					
					AllBanks.getEconomy().withdrawPlayer(e.getPlayer(), totalChargeFinal.doubleValue());
					
					AllBanksLogger.info("BankLoan: Charged " + AllBanks.getEconomy().format(totalChargeFinal.doubleValue()) + " from " + e.getPlayer().getName() + " (" + e.getPlayer().getDisplayName() + ") (cause: has a loan at the bank).");
					
				}
				
			}.runTaskLater(AllBanks.getInstance(), 20 * 2);
			
		} catch (SQLException e1) {
			DatabaseUtil.checkDatabaseIsLocked(e1);
		} finally {
			try {
				stm.close();
				res.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
