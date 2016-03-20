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
					ba.BankLoan_updateLoan(result.getInt("loan"), false);
					ba.BankMoney_updateMoney(result.getInt("money"), false);
					ba.BankXP_updateXP(result.getInt("xp"), false);
				}else{
					ba.BankLoan_updateLoan(0, false);
					ba.BankMoney_updateMoney(0, false);
					ba.BankXP_updateXP(0, false);
				}
				
				return ba;
				
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	/*
	 * FUNCIONES DE BANKACCOUNT.
	 */
	
	Player player;
	
	int bankloan_loan = 0;
	int bankmoney_money = 0;
	int bankxp_xp = 0;
	
	/**
	 * @param uniqueId
	 */
	public BankAccount(Player p) {
		this.player = p;
	}

	public void BankLoan_addLoan(int add){
		int total = bankloan_loan + add;
		BankLoan_updateLoan(total, true);
	}
	
	public void BankLoan_subsLoan(int substract){

		int total = bankloan_loan - substract;
		BankLoan_updateLoan(total, true);
	}
	
	public void BankLoan_updateLoan(int newLoan, boolean updateFromDatabase){
		bankloan_loan = newLoan;
		
		//Actualizar la base de datos
		if(updateFromDatabase)
			try {
				Statement stm = AllBanks.getDBC().createStatement();
				stm.executeUpdate("UPDATE bankloan_accounts SET loan = '" + newLoan + "' WHERE owner = '" + player.getName() + "'");
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public int BankLoan_getLoan(){
		return bankloan_loan;
	}

	public void BankMoney_addMoney(int add){
		int total = bankmoney_money + add;
		BankMoney_updateMoney(total, true);
	}
	
	public void BankMoney_subsMoney(int substract){
		int total = bankmoney_money - substract;
		BankMoney_updateMoney(total, true);
	}
	
	public void BankMoney_updateMoney(int newMoney, boolean updateFromDatabase){
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
	
	public int BankMoney_getMoney(){
		return bankmoney_money;
	}
	
	public void BankXP_updateXP(int newXP, boolean updateFromDatabase){
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
