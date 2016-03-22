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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.AllBanks;

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
	
	public static class Cache{
		
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
				cache.put(bs.getPlayer().getUniqueId(), getAccountFromDataBase(bs.getPlayer()));
			}
			
			return cache.get(bs.getPlayer().getUniqueId());
		}
		
		private static BankAccount getAccountFromDataBase(Player p){
			try{
				Statement stm = AllBanks.getDBC().createStatement();
				
				//nueva instancia de bank account
				BankAccount ba = new BankAccount(p);
				
				//BankLoan
				ResultSet result = stm.executeQuery("SELECT bankloan.*, bankmoney.*, bankxp.* FROM bankloan_accounts AS bankloan LEFT JOIN bankmoney_accounts AS bankmoney ON bankmoney.owner = bankloan.owner LEFT JOIN bankxp_accounts AS bankxp ON bankxp.owner = bankloan.owner WHERE bankloan.owner = '" + p.getName() + "'");
				
				if(result.next()){
					ba.BankLoan_updateLoan(new BigDecimal(result.getString("loan")), false);
					ba.BankMoney_updateMoney(new BigDecimal(result.getString("money")), false);
					ba.BankXP_updateXP(result.getInt("xp"), false);
				}else{
					//ingresar nuevos valores
					registerNewAccountInDataBase(p);
					
					ba.BankLoan_updateLoan(BigDecimal.ZERO, false);
					ba.BankMoney_updateMoney(BigDecimal.ZERO, false);
					ba.BankXP_updateXP(0, false);
				}
				
				return ba;
				
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			return null;
		}

		/**
		 * @param p
		 */
		private static void registerNewAccountInDataBase(Player p) {
			try {
				Statement stm = AllBanks.getDBC().createStatement();
				stm.executeUpdate("INSERT INTO bankloan_accounts (owner, loan) VALUES ('" + p.getName() + "', 0)");
				stm.executeUpdate("INSERT INTO bankmoney_accounts (owner, money) VALUES ('" + p.getName() + "', 0)");
				stm.executeUpdate("INSERT INTO bankxp_accounts (owner, xp) VALUES ('" + p.getName() + "', 0)");
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
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
	
	/**
	 * @param uniqueId
	 */
	public BankAccount(Player p) {
		this.player = p;
	}

	public boolean BankLoan_addLoan(BigDecimal add){
		BigDecimal total = bankloan_loan.add(add);
		return BankLoan_updateLoan(total, true);
	}
	
	public boolean BankLoan_subsLoan(BigDecimal substract){

		BigDecimal total = bankloan_loan.subtract(substract);
		return BankLoan_updateLoan(total, true);
	}
	
	public synchronized boolean BankLoan_updateLoan(BigDecimal newLoan, boolean updateFromDatabase){
		bankloan_loan = newLoan;
		
		//Actualizar la base de datos
		if(updateFromDatabase)
			try {
				Statement stm = AllBanks.getDBC().createStatement();
				stm.executeUpdate("UPDATE bankloan_accounts SET loan = '" + newLoan.doubleValue() + "' WHERE owner = '" + player.getName() + "'");
				stm.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		return false;
	}
	
	public BigDecimal BankLoan_getLoan(){
		return bankloan_loan;
	}

	public void BankMoney_addMoney(BigDecimal add){
		BigDecimal total = bankmoney_money.add(add);
		BankMoney_updateMoney(total, true);
	}
	
	public void BankMoney_subsMoney(BigDecimal substract){
		BigDecimal total = bankmoney_money.subtract(substract);
		BankMoney_updateMoney(total, true);
	}
	
	public synchronized void BankMoney_updateMoney(BigDecimal newMoney, boolean updateFromDatabase){
		bankmoney_money = newMoney;
		
		//Actualizar la base de datos
		if(updateFromDatabase)
			try {
				Statement stm = AllBanks.getDBC().createStatement();
				stm.executeUpdate("UPDATE bankmoney_accounts SET money = '" + newMoney + "' WHERE owner = '" + player.getName() + "'");
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public BigDecimal BankMoney_getMoney(){
		return bankmoney_money;
	}
	
	public synchronized void BankXP_updateXP(int newXP, boolean updateFromDatabase){
		bankxp_xp = newXP;
		
		//Actualizar la base de datos
		if(updateFromDatabase)
			try {
				Statement stm = AllBanks.getDBC().createStatement();
				stm.executeUpdate("UPDATE bankxp_accounts SET xp = '" + newXP + "' WHERE owner = '" + player.getName() + "'");
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
