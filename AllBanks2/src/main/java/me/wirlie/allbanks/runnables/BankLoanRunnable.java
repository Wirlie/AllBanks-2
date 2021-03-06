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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.AllBanks.StorageType;
import me.wirlie.allbanks.banks.bankdata.BankAccount;
import me.wirlie.allbanks.utils.AllBanksLogger;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.Util;

/**
 * Runnable encargado de procesar funciones con los intereses de BankLoan
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class BankLoanRunnable extends BukkitRunnable {

	public synchronized void run() {
		
		if(DataBaseUtil.databaseIsLocked()){
			AllBanksLogger.severe("&7[&fCollectLoanSystem&7] &cDatabase is locked! Aborting...", true);
			return;
		}

		long currentTime = new Date().getTime();
		int affectedAccounts = 0;
		
		AllBanksLogger.info("&7[&fCollectLoanSystem&7] &bReading Database...", true);
		AllBanksLogger.info("BankTimerRunnable: Executed (reading database).");
		
		if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
			if(!Util.FlatFile_bankAccountFolder.exists()) {
				Util.FlatFile_bankAccountFolder.mkdirs();
			}
			
			for(File baFile : Util.FlatFile_bankAccountFolder.listFiles()) {
				YamlConfiguration baFileYAML = YamlConfiguration.loadConfiguration(baFile);
				
				BigDecimal loan = new BigDecimal(baFileYAML.getString("loan"));
				BigDecimal taxPercent = new BigDecimal(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.interest", 1)).divide(new BigDecimal(100));
				BigDecimal chargeLoan = loan.multiply(taxPercent);
				BigDecimal minPlayerBalance = new BigDecimal(AllBanks.getInstance().getConfig().getDouble("banks.bank-loan.stop-collect-if-player-balance-is-minor-than", -500));
				
				Player player = Bukkit.getPlayer(baFileYAML.getString("owner"));
				
				if(player != null){
					
					if(AllBanks.getEconomy().getBalance(player) > minPlayerBalance.doubleValue()){
						BankAccount ba = BankAccount.Cache.get(player.getUniqueId());
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", AllBanks.getEconomy().format(chargeLoan.doubleValue()));
						replaceMap.put("%2%", AllBanks.getEconomy().format(ba.BankLoan.getLoan().doubleValue()));
						
						Translation.getAndSendMessage(player, StringsID.BANKLOAN_INTEREST_CHARGED, replaceMap, true);
						
						AllBanks.getEconomy().withdrawPlayer(player, chargeLoan.doubleValue());
					}
				}else{
					if(!Util.FlatFile_pendingCharges.exists()) {
						Util.FlatFile_pendingCharges.mkdirs();
					}
					
					File pendingChargeFile = new File(Util.FlatFile_pendingCharges + File.separator + baFileYAML.getString("owner") + ".yml");
					
					if(!pendingChargeFile.exists()) {
						try {
							pendingChargeFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					YamlConfiguration pendingChargeFileYAML = YamlConfiguration.loadConfiguration(pendingChargeFile);
					
					BigDecimal currentAmount = new BigDecimal(pendingChargeFileYAML.getString("amount", "0"));
					BigDecimal newAmount = currentAmount.add(chargeLoan);
					
					pendingChargeFileYAML.set("amount", String.valueOf(newAmount.doubleValue()));
					
					try {
						pendingChargeFileYAML.save(pendingChargeFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				affectedAccounts++;
				
			}
		} else {
			Statement stm = null;
			ResultSet res = null;
		
			try {
				stm = AllBanks.getDataBaseConnection().createStatement();
				//Seleccionar los préstamos mayores a 0 optimizará los resultados.
				res = stm.executeQuery("SELECT * FROM bankloan_accounts WHERE loan > 0");
				
				while(res.next()){
					BigDecimal loan = new BigDecimal(res.getString("loan"));
					BigDecimal taxPercent = new BigDecimal(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.interest", 1)).divide(new BigDecimal(100));
					BigDecimal chargeLoan = loan.multiply(taxPercent);
					BigDecimal minPlayerBalance = new BigDecimal(AllBanks.getInstance().getConfig().getDouble("banks.bank-loan.stop-collect-if-player-balance-is-minor-than", -500));
					
					Player player = Bukkit.getPlayer(res.getString("owner"));
					
					if(player != null){
						
						if(AllBanks.getEconomy().getBalance(player) > minPlayerBalance.doubleValue()){
							BankAccount ba = BankAccount.Cache.get(player.getUniqueId());
							HashMap<String, String> replaceMap = new HashMap<String, String>();
							replaceMap.put("%1%", AllBanks.getEconomy().format(chargeLoan.doubleValue()));
							replaceMap.put("%2%", AllBanks.getEconomy().format(ba.BankLoan.getLoan().doubleValue()));
							
							Translation.getAndSendMessage(player, StringsID.BANKLOAN_INTEREST_CHARGED, replaceMap, true);
							
							AllBanks.getEconomy().withdrawPlayer(player, chargeLoan.doubleValue());
						}
					}else{
						//El jugador se encuentra fuera de línea...
						Statement stm2 = AllBanks.getDataBaseConnection().createStatement();
						stm2.executeUpdate("INSERT INTO bankloan_pending_charges (owner, amount) VALUES ('" + res.getString("owner") + "', '" + chargeLoan.toPlainString() + "')");
						stm2.close();
					}
					
					affectedAccounts++;
					
				}
				
				AllBanksLogger.info("&7[&fCollectLoanSystem&7] &b" + affectedAccounts + " accounts affected...", true);
			} catch (SQLException e) {
				DataBaseUtil.checkDatabaseIsLocked(e);
			}finally{
				try {
					stm.close();
					res.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Actualizar datos
		File bankLoanData = new File(AllBanks.getInstance().getDataFolder() + File.separator + "BankLoanData.yml");
		
		if(!bankLoanData.exists())
			try {
				bankLoanData.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bankLoanData);
		
		//Actualizar
		yaml.set("last-system-execution", currentTime);
		try {
			yaml.save(bankLoanData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
