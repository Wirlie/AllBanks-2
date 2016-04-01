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
package me.wirlie.allbanks.data;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Util;
import me.wirlie.allbanks.AllBanks.StorageType;
import me.wirlie.allbanks.Util.DatabaseUtil;
import me.wirlie.allbanks.logger.AllBanksLogger;

/**
 * Esta clase permitirá almacenar los datos recopilados de la base de datos, con el fin de
 * evitar acceder a la base de datos de manera innecesaria (y a la vez ayudando al servidor
 * a consumir menos recursos ya que una clase almacenada tiene menor consumo que leer una
 * base de datos repetidamente en un corto periodo de tiempo).
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class BankAccount {

	/**
	 * Puede llamarse un caché, ya que esto conservará una instancia BankAccount mientras sea requerido
	 * y cuando no sea requerido se conservará para futuras peticiones.
	 */
	private static HashMap<UUID, BankAccount> cache = new HashMap<UUID, BankAccount>();
	
	public BankLoan BankLoan = new BankLoan();
	public BankMoney BankMoney = new BankMoney();
	public BankXP BankXP = new BankXP();
	public BankTime BankTime = new BankTime();
	public BankChest BankChest = new BankChest();
	
	public static class Cache{
		
		public static void clearCache(){
			cache.clear();
		}
		
		public static BankAccount get(UUID uuid){
			if(!cache.containsKey(uuid)){
				cache.put(uuid, getAccountFromDataBase(Bukkit.getPlayer(uuid)));
			}
			
			return cache.get(uuid);
		}
		
		public static BankAccount get(Sign sign){
			
			BankSession bs = BankSession.getActiveSessionBySign(sign);
			
			assert(bs != null);
			
			if(!cache.containsKey(bs.getPlayer().getUniqueId())){
				AllBanksLogger.info("BankAccount : Cache : Reading database, try to get the bank account for " + bs.getPlayer().getName());
				cache.put(bs.getPlayer().getUniqueId(), getAccountFromDataBase(bs.getPlayer()));
			}
			
			return cache.get(bs.getPlayer().getUniqueId());
		}
		
		private static BankAccount getAccountFromDataBase(Player p){
			
			if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
				
				if(!Util.FlaFile_bankAccountFolder.exists()) {
					Util.FlaFile_bankAccountFolder.mkdirs();
				}
				
				BankAccount ba = new BankAccount(p);
				
				File baFile = new File(Util.FlaFile_bankAccountFolder + File.separator + p.getName() + ".yml");
				if(baFile.exists()) {
					YamlConfiguration baFileYAML = YamlConfiguration.loadConfiguration(baFile);
					
					ba.BankLoan.updateLoan(new BigDecimal(baFileYAML.getString("loan", "0")), false);
					ba.BankMoney.updateMoney(new BigDecimal(baFileYAML.getString("money", "0")), false);
					ba.BankXP.updateXP(baFileYAML.getInt("xp", 0), false);
					ba.BankTime.updateTime(baFileYAML.getInt("time", 0), false);
				}else {
					registerNewAccountInDataBase(p);
					
					ba.BankLoan.updateLoan(BigDecimal.ZERO, false);
					ba.BankMoney.updateMoney(BigDecimal.ZERO, false);
					ba.BankXP.updateXP(0, false);
					ba.BankTime.updateTime(0, false);
				}
				
				return ba;
			}else {
				
				if(DatabaseUtil.databaseIsLocked()) return null;
				
				Statement stm = null;
				ResultSet result = null;
				
				try{
					stm = AllBanks.getDataBaseConnection().createStatement();
					
					//nueva instancia de bank account
					BankAccount ba = new BankAccount(p);
					
					//BankLoan
					result = stm.executeQuery("SELECT bankloan.*, bankmoney.*, bankxp.*, banktime.* FROM bankloan_accounts AS bankloan LEFT JOIN bankmoney_accounts AS bankmoney ON bankmoney.owner = bankloan.owner LEFT JOIN bankxp_accounts AS bankxp ON bankxp.owner = bankloan.owner LEFT JOIN banktime_accounts AS banktime ON bankloan.owner = banktime.owner WHERE bankloan.owner = '" + p.getName() + "'");
					
					if(result.next()){
						ba.BankLoan.updateLoan(new BigDecimal(result.getString("loan")), false);
						ba.BankMoney.updateMoney(new BigDecimal(result.getString("money")), false);
						ba.BankXP.updateXP(result.getInt("xp"), false);
						ba.BankTime.updateTime(result.getInt("time"), false);
					}else{
						//ingresar nuevos valores
						registerNewAccountInDataBase(p);
						
						ba.BankLoan.updateLoan(BigDecimal.ZERO, false);
						ba.BankMoney.updateMoney(BigDecimal.ZERO, false);
						ba.BankXP.updateXP(0, false);
						ba.BankTime.updateTime(0, false);
					}
					
					return ba;
					
				}catch(SQLException e){
					AllBanksLogger.info("failed! SQLException:");
					DatabaseUtil.checkDatabaseIsLocked(e);
				}finally{
					try {
						stm.close();
						result.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			return null;
		}

		/**
		 * @param p
		 */
		private static void registerNewAccountInDataBase(Player p) {
			if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
				if(!Util.FlaFile_bankAccountFolder.exists()) {
					Util.FlaFile_bankAccountFolder.mkdirs();
				}
				
				File baFile = new File(Util.FlaFile_bankAccountFolder + File.separator + p.getName() + ".yml");
				if(!baFile.exists()) {
					try {
						baFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration baFileYAML = YamlConfiguration.loadConfiguration(baFile);
				baFileYAML.set("loan", "0");
				baFileYAML.set("money", "0");
				baFileYAML.set("xp", 0);
				baFileYAML.set("time", 0);
				baFileYAML.set("owner", p.getName());
				
				try {
					baFileYAML.save(baFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Statement stm = null;
				
				try {
					stm = AllBanks.getDataBaseConnection().createStatement();
					stm.executeUpdate("INSERT INTO bankloan_accounts (owner, loan) VALUES ('" + p.getName() + "', 0)");
					stm.executeUpdate("INSERT INTO bankmoney_accounts (owner, money) VALUES ('" + p.getName() + "', 0)");
					stm.executeUpdate("INSERT INTO bankxp_accounts (owner, xp) VALUES ('" + p.getName() + "', 0)");
					stm.executeUpdate("INSERT INTO banktime_accounts (owner, time) VALUES ('" + p.getName() + "', 0)");
				} catch (SQLException e) {
					DatabaseUtil.checkDatabaseIsLocked(e);
				} finally {
					try {
						stm.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/*
	 * FUNCIONES DE BANKACCOUNT.
	 */
	
	Player player;
	
	BigDecimal bankloan_loan = BigDecimal.ZERO;
	BigDecimal bankmoney_money = BigDecimal.ZERO;
	int bankxp_xp = 0;
	int banktime_time = 0;
	//los cofres comienzan a contar desde 1 (1, 2, 3, 4, 5, 6) pero se establece en 0 por que al inicio se usa switchToNextChestCursor() y este cambia el valor a 1 (valor inicial).
	int bankchest_chest = 0;
	
	/**
	 * @param uniqueId
	 */
	public BankAccount(Player p) {
		this.player = p;
	}
	
	public class BankChest{
		//BankChest no trabajará con SQLite por razones de seguridad (ej, una falla de la base de datos podría hacer perder los datos, ademas que, ItemStack es serializable en Yaml)
		public int getCurrentChestCursor(){
			return bankchest_chest;
		}
		
		public void setChestCursor(int newCursor){
			bankchest_chest = newCursor;
		}
		
		public int getNextChestCursor(){
			
			int max_virtuals_chests = 0;
			boolean overrideConfiguration = false;
			
			for(PermissionAttachmentInfo pinfo : player.getEffectivePermissions()){
				if(pinfo.getPermission().startsWith("allbanks.banks.bankchest.virtualchests.")){
					try{
						max_virtuals_chests = Integer.parseInt(pinfo.getPermission().replace("allbanks.banks.bankchest.virtualchests.", ""));
						overrideConfiguration = true;
					}catch(NumberFormatException e){
						overrideConfiguration = false;
					}
				}
			}
			
			if(!overrideConfiguration)
				max_virtuals_chests = AllBanks.getInstance().getConfig().getInt("banks.bank-chest.max-virtual-chests-per-player", 1);
			
			//Mínimo un cofre virtual por jugador.
			if(max_virtuals_chests <= 0) max_virtuals_chests = 1;
			
			int nextChest = getCurrentChestCursor() + 1;
			if(nextChest > max_virtuals_chests){
				//Regresar cursor al cofre 1.
				return 1;
			}else{
				//Progresar en 1 el cursor.
				return nextChest;
			}
		}
		
		public void switchToNextChest(){
			setChestCursor(getNextChestCursor());
		}
	}
	
	public class BankLoan{
		public boolean addLoan(BigDecimal add){
			BigDecimal total = bankloan_loan.add(add);
			return updateLoan(total, true);
		}
		
		public boolean subsLoan(BigDecimal substract){

			BigDecimal total = bankloan_loan.subtract(substract);
			return updateLoan(total, true);
		}
		
		public synchronized boolean updateLoan(BigDecimal newLoan, boolean updateFromDatabase){
			if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
				if(!Util.FlaFile_bankAccountFolder.exists()) {
					Util.FlaFile_bankAccountFolder.mkdirs();
				}
				
				File baFile = new File(Util.FlaFile_bankAccountFolder + File.separator + player.getName() + ".yml");
				if(!baFile.exists()) {
					try {
						baFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration baFileYAML = YamlConfiguration.loadConfiguration(baFile);
				baFileYAML.set("loan", String.valueOf(newLoan.doubleValue()));
				
				try {
					baFileYAML.save(baFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return true;
			} else {
				if(DatabaseUtil.databaseIsLocked()) return false;
				
				//Actualizar la base de datos
				if(updateFromDatabase){
					Statement stm = null;
				
					try {
						stm = AllBanks.getDataBaseConnection().createStatement();
						stm.executeUpdate("UPDATE bankloan_accounts SET loan = '" + newLoan.doubleValue() + "' WHERE owner = '" + player.getName() + "'");
	
						bankloan_loan = newLoan;
						
						return true;
					} catch (SQLException e) {
						DatabaseUtil.checkDatabaseIsLocked(e);
					} finally {
						try {
							stm.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return false;
		}
		
		public BigDecimal getLoan(){
			return bankloan_loan;
		}
	}

	public class BankMoney{
		public boolean addMoney(BigDecimal add){
			BigDecimal total = bankmoney_money.add(add);
			return updateMoney(total, true);
		}
		
		public boolean subsMoney(BigDecimal substract){
			BigDecimal total = bankmoney_money.subtract(substract);
			return updateMoney(total, true);
		}
		
		public synchronized boolean updateMoney(BigDecimal newMoney, boolean updateFromDatabase){
			if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
				if(!Util.FlaFile_bankAccountFolder.exists()) {
					Util.FlaFile_bankAccountFolder.mkdirs();
				}
				
				File baFile = new File(Util.FlaFile_bankAccountFolder + File.separator + player.getName() + ".yml");
				if(!baFile.exists()) {
					try {
						baFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration baFileYAML = YamlConfiguration.loadConfiguration(baFile);
				baFileYAML.set("money", String.valueOf(newMoney.doubleValue()));
				
				try {
					baFileYAML.save(baFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return true;
			} else {
				if(DatabaseUtil.databaseIsLocked()) return false;
				
				//Actualizar la base de datos
				if(updateFromDatabase){
					Statement stm = null;
				
					try {
						stm = AllBanks.getDataBaseConnection().createStatement();
						stm.executeUpdate("UPDATE bankmoney_accounts SET money = '" + newMoney + "' WHERE owner = '" + player.getName() + "'");
	
						bankmoney_money = newMoney;
						return true;
					} catch (SQLException e) {
						DatabaseUtil.checkDatabaseIsLocked(e);
					} finally {
						try {
							stm.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			return false;
		}
		
		public BigDecimal getMoney(){
			return bankmoney_money;
		}
	}
	
	public class BankXP{
		public synchronized boolean updateXP(int newXP, boolean updateFromDatabase){
			if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
				if(!Util.FlaFile_bankAccountFolder.exists()) {
					Util.FlaFile_bankAccountFolder.mkdirs();
				}
				
				File baFile = new File(Util.FlaFile_bankAccountFolder + File.separator + player.getName() + ".yml");
				if(!baFile.exists()) {
					try {
						baFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration baFileYAML = YamlConfiguration.loadConfiguration(baFile);
				baFileYAML.set("xp", newXP);
				
				try {
					baFileYAML.save(baFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return true;
			} else {
				if(DatabaseUtil.databaseIsLocked()) return false;
				
				//Actualizar la base de datos
				if(updateFromDatabase){
	
					Statement stm = null;
				
					try {
						stm = AllBanks.getDataBaseConnection().createStatement();
						stm.executeUpdate("UPDATE bankxp_accounts SET xp = '" + newXP + "' WHERE owner = '" + player.getName() + "'");
	
						bankxp_xp = newXP;
						
						return true;
					} catch (SQLException e) {
						DatabaseUtil.checkDatabaseIsLocked(e);
					} finally {
						try {
							stm.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			return false;
		}
		
		public boolean addXP(int addXP){
			int total = bankxp_xp + addXP;
			return updateXP(total, true);
		}
		
		public boolean subsXP(int subsXP){
			int total = bankxp_xp - subsXP;
			return updateXP(total, true);
		}
		
		public int getRawXP(){
			return bankxp_xp;
		}
		
		public int getLvlForRawXP(){
			return Util.XPConversionUtil.convertExpToLevel(bankxp_xp);
		}
	}
	
	public class BankTime{
		public synchronized boolean updateTime(int newTime, boolean updateFromDatabase){
			if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
				if(!Util.FlaFile_bankAccountFolder.exists()) {
					Util.FlaFile_bankAccountFolder.mkdirs();
				}
				
				File baFile = new File(Util.FlaFile_bankAccountFolder + File.separator + player.getName() + ".yml");
				if(!baFile.exists()) {
					try {
						baFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration baFileYAML = YamlConfiguration.loadConfiguration(baFile);
				baFileYAML.set("time", newTime);
				
				try {
					baFileYAML.save(baFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return true;
			} else {
				//Actualizar la base de datos
				if(updateFromDatabase){
					
					if(DatabaseUtil.databaseIsLocked()) return false;
					
					Statement stm = null;
					
					try {
						stm = AllBanks.getDataBaseConnection().createStatement();
						stm.executeUpdate("UPDATE banktime_accounts SET time = '" + newTime + "' WHERE owner = '" + player.getName() + "'");
	
						banktime_time = newTime;
						
						return true;
					} catch (SQLException e) {
						DatabaseUtil.checkDatabaseIsLocked(e);
					} finally {
						try {
							stm.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			return false;
		}
		
		public boolean updateTimePlusOne(){
			return updateTime(banktime_time + 1, true);
		}
		
		public int getTime(){
			return banktime_time;
		}
		
		public boolean subsTime(int subsTime){
			int total = banktime_time - subsTime;
			return updateTime(total, true);
		}
		
		public boolean addTime(int addTime){
			int total = banktime_time + addTime;
			return updateTime(total, true);
		}
	}
}
