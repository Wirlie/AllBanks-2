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
package me.wirlie.allbanks.statistics;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.wirlie.allbanks.Shops;
import me.wirlie.allbanks.utils.AssertUtil;
import me.wirlie.allbanks.utils.ItemStackBase64;
import me.wirlie.allbanks.utils.ShopUtil;
import me.wirlie.allbanks.utils.Util;

/**
 * Estadísticas de AllBanksShop
 * @author Wirlie
 *
 */
public class AllBanksShopStatistics {
	
	Sign AllBanksShopSign;
	static Connection DBC = AllBanksStatistics.DBC;
	
	/**
	 * Tipo de transacción
	 * @author Wirlie
	 *
	 */
	@SuppressWarnings("javadoc")
	public enum TransactionType{
		BUY,
		SELL;
	}
	
	/**
	 * Construir una clase de estadísticas.
	 * @param AllBanksShopSign Letrero de AllBanksShop afectado.
	 */
	public AllBanksShopStatistics(Sign AllBanksShopSign){
		
		AssertUtil.assertNotNull(AllBanksShopSign);
		this.AllBanksShopSign = AllBanksShopSign;
	}
	/**
	 * Añadir estadística de compra a un letrero de AllBanksShop.
	 * @param item Objeto comprado.
	 * @param p Jugador de la transacción.
	 */
	public void addBuyStatisticsToSign(ItemStack item, Player p){
		
		AssertUtil.assertNotNull(item);
		AssertUtil.assertNotNull(p);
		
		String shop_owner = (ShopUtil.isAdminShop(AllBanksShopSign)) ? Shops.ADMIN_TAG : ShopUtil.getOwner(AllBanksShopSign).getName().toLowerCase();
		Statement stm = null;
		
		try{
			stm = DBC.createStatement();
			stm.executeUpdate("INSERT INTO shops_statistics "
					+ "(sign_loc, transaction_player, transaction_item_base64, date, shop_owner, transaction_type) VALUES "
					+ "('" + Util.convertLocationToString(AllBanksShopSign.getLocation(), true) + "', '" + p.getName().toLowerCase() + "', '" + ItemStackBase64.toBase64(item) + "', " + new Date().getTime() + ", '" + shop_owner + "', '" + TransactionType.BUY.toString() + "')");
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(stm != null) stm.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Añadir estadística de venta a un letrero de AllBanksShop.
	 * @param item Objeto vendido.
	 * @param p Jugador de la transacción.
	 */
	public void addSellStatisticsToSign(ItemStack item, Player p){
		
		AssertUtil.assertNotNull(item);
		AssertUtil.assertNotNull(p);
		
		String shop_owner = (ShopUtil.isAdminShop(AllBanksShopSign)) ? Shops.ADMIN_TAG : ShopUtil.getOwner(AllBanksShopSign).getName().toLowerCase();
		Statement stm = null;
		
		try{
			stm = DBC.createStatement();
			stm.executeUpdate("INSERT INTO shops_statistics "
					+ "(sign_loc, transaction_player, transaction_item_base64, date, shop_owner, transaction_type) VALUES "
					+ "('" + Util.convertLocationToString(AllBanksShopSign.getLocation(), true) + "', '" + p.getName().toLowerCase() + "', '" + ItemStackBase64.toBase64(item) + "', " + new Date().getTime() + ", '" + shop_owner + "', '" + TransactionType.SELL.toString() + "')");
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(stm != null) stm.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Obtener transacciones de una tienda.
	 * @param seconds Rango en segundos.
	 * @return Listado con las transacciones.
	 */
	public List<ShopTransactionEntry> getSignTransactions(int seconds){
		
		AssertUtil.assertIntegerRangeMin(seconds, 1);
		
		Statement stm = null;
		ResultSet res = null;
		
		try{
			long currentTime = new Date().getTime();
			long minTime = currentTime - (seconds * 1000);
			
			stm = DBC.createStatement();
			res = stm.executeQuery("SELECT * FROM shops_statistics WHERE sign_loc = '" + Util.convertLocationToString(AllBanksShopSign.getLocation(), true) + "' AND date > " + minTime);
			
			List<ShopTransactionEntry> transactionEntry = new ArrayList<ShopTransactionEntry>();
			while(res.next()){
				try {
					ShopTransactionEntry entry = new ShopTransactionEntry(
						Util.convertStringToLocation(res.getString("sign_loc")), 
						res.getString("transaction_player"),
						ItemStackBase64.stacksFromBase64(res.getString("transaction_item_base64"))[0],
						res.getLong("date"),
						res.getString("shop_owner"),
						TransactionType.valueOf(res.getString("transaction_type")));
					transactionEntry.add(entry);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return transactionEntry;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(stm != null) stm.close();
				if(res != null) res.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("javadoc")
	public enum GetTransactionParameter{
		BY_TRANSACTION_PLAYER,
		BY_DAY,
		BY_HOUR,
		BY_MONTH,
		BY_YEAR,
		BY_TRANSACTION_ITEM,
		BY_SHOP_OWNER,
		BY_TEST_ASSERT;
	}
	
	/**
	 * Obtener las transacciones de acuerdo al parámetro especificado.
	 * @param parameter Parámetro excluidor.
	 * @param parameterData Dato del parámetro, puede ser nulo.
	 * @param seconds (Opcional) Rango en segundos para obtener las transacciones.
	 * @return Listado con las transacciones.
	 */
	public static List<ShopTransactionEntry> getTransactionsBy(GetTransactionParameter parameter, Object parameterData, int seconds){
		
		AssertUtil.assertNotNull(parameter);
		AssertUtil.assertTrue(
				parameter == GetTransactionParameter.BY_DAY ||
				parameter == GetTransactionParameter.BY_HOUR ||
				parameter == GetTransactionParameter.BY_MONTH ||
				parameter == GetTransactionParameter.BY_SHOP_OWNER ||
				parameter == GetTransactionParameter.BY_TRANSACTION_ITEM ||
				parameter == GetTransactionParameter.BY_TRANSACTION_PLAYER ||
				parameter == GetTransactionParameter.BY_YEAR
				);
		
		if(parameter == GetTransactionParameter.BY_DAY){
			AssertUtil.assertNull(parameterData);
			long currentTime = new Date().getTime();
			long minTime = currentTime - (60 * 60 * 24 * 1000);
			
			Statement stm = null;
			ResultSet res = null;
			
			try{
				stm = DBC.createStatement();
				res = stm.executeQuery("SELECT * FROM shops_statistics WHERE date > " + minTime);
				
				List<ShopTransactionEntry> transactionEntry = new ArrayList<ShopTransactionEntry>();
				while(res.next()){
					try {
						ShopTransactionEntry entry = new ShopTransactionEntry(
							Util.convertStringToLocation(res.getString("sign_loc")), 
							res.getString("transaction_player"),
							ItemStackBase64.stacksFromBase64(res.getString("transaction_item_base64"))[0],
							res.getLong("date"),
							res.getString("shop_owner"),
							TransactionType.valueOf(res.getString("transaction_type")));
						transactionEntry.add(entry);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return transactionEntry;
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(stm != null) stm.close();
					if(res != null) res.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}else if(parameter == GetTransactionParameter.BY_HOUR){
			AssertUtil.assertNull(parameterData);
			long currentTime = new Date().getTime();
			long minTime = currentTime - (60 * 60 * 1000);
			
			Statement stm = null;
			ResultSet res = null;
			
			try{
				stm = DBC.createStatement();
				res = stm.executeQuery("SELECT * FROM shops_statistics WHERE date > " + minTime);
				
				List<ShopTransactionEntry> transactionEntry = new ArrayList<ShopTransactionEntry>();
				while(res.next()){
					try {
						ShopTransactionEntry entry = new ShopTransactionEntry(
							Util.convertStringToLocation(res.getString("sign_loc")), 
							res.getString("transaction_player"),
							ItemStackBase64.stacksFromBase64(res.getString("transaction_item_base64"))[0],
							res.getLong("date"),
							res.getString("shop_owner"),
							TransactionType.valueOf(res.getString("transaction_type")));
						transactionEntry.add(entry);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return transactionEntry;
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(stm != null) stm.close();
					if(res != null) res.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}else if(parameter == GetTransactionParameter.BY_MONTH){
			AssertUtil.assertNull(parameterData);
			long currentTime = new Date().getTime();
			long minTime = currentTime - (60 * 60 * 24 * 31 * 1000);
			
			Statement stm = null;
			ResultSet res = null;
			
			try{
				stm = DBC.createStatement();
				res = stm.executeQuery("SELECT * FROM shops_statistics WHERE date > " + minTime);
				
				List<ShopTransactionEntry> transactionEntry = new ArrayList<ShopTransactionEntry>();
				while(res.next()){
					try {
						ShopTransactionEntry entry = new ShopTransactionEntry(
							Util.convertStringToLocation(res.getString("sign_loc")), 
							res.getString("transaction_player"),
							ItemStackBase64.stacksFromBase64(res.getString("transaction_item_base64"))[0],
							res.getLong("date"),
							res.getString("shop_owner"),
							TransactionType.valueOf(res.getString("transaction_type")));
						transactionEntry.add(entry);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return transactionEntry;
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(stm != null) stm.close();
					if(res != null) res.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}else if(parameter == GetTransactionParameter.BY_SHOP_OWNER){
			AssertUtil.assertNotNull(parameterData);
			AssertUtil.assertIntegerRangeMin(seconds, 1);
			
			long currentTime = new Date().getTime();
			long minTime = currentTime - (seconds * 1000);
			
			Statement stm = null;
			ResultSet res = null;
			
			try{
				AssertUtil.assertTrue(parameterData instanceof String);
				String shopOwner = (String) parameterData;
				
				stm = DBC.createStatement();
				res = stm.executeQuery("SELECT * FROM shops_statistics WHERE shop_owner = '" + shopOwner + "' AND date > " + minTime);
				
				List<ShopTransactionEntry> transactionEntry = new ArrayList<ShopTransactionEntry>();
				while(res.next()){
					try {
						ShopTransactionEntry entry = new ShopTransactionEntry(
							Util.convertStringToLocation(res.getString("sign_loc")), 
							res.getString("transaction_player"),
							ItemStackBase64.stacksFromBase64(res.getString("transaction_item_base64"))[0],
							res.getLong("date"),
							res.getString("shop_owner"),
							TransactionType.valueOf(res.getString("transaction_type")));
						transactionEntry.add(entry);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return transactionEntry;
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(stm != null) stm.close();
					if(res != null) res.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}else if(parameter == GetTransactionParameter.BY_TRANSACTION_ITEM){
			AssertUtil.assertNotNull(parameterData);
			AssertUtil.assertIntegerRangeMin(seconds, 1);
			
			long currentTime = new Date().getTime();
			long minTime = currentTime - (seconds * 1000);
			
			Statement stm = null;
			ResultSet res = null;
			
			try{
				AssertUtil.assertTrue(parameterData instanceof ItemStack);
				ItemStack transactionItem = (ItemStack) parameterData;
				
				stm = DBC.createStatement();
				res = stm.executeQuery("SELECT * FROM shops_statistics WHERE transaction_item_base64 = '" + ItemStackBase64.toBase64(transactionItem) + "' AND date > " + minTime);
				
				List<ShopTransactionEntry> transactionEntry = new ArrayList<ShopTransactionEntry>();
				while(res.next()){
					try {
						ShopTransactionEntry entry = new ShopTransactionEntry(
							Util.convertStringToLocation(res.getString("sign_loc")), 
							res.getString("transaction_player"),
							ItemStackBase64.stacksFromBase64(res.getString("transaction_item_base64"))[0],
							res.getLong("date"),
							res.getString("shop_owner"),
							TransactionType.valueOf(res.getString("transaction_type")));
						transactionEntry.add(entry);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return transactionEntry;
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(stm != null) stm.close();
					if(res != null) res.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}else if(parameter == GetTransactionParameter.BY_TRANSACTION_PLAYER){
			AssertUtil.assertNotNull(parameterData);
			AssertUtil.assertIntegerRangeMin(seconds, 1);
			
			long currentTime = new Date().getTime();
			long minTime = currentTime - (seconds * 1000);
			
			Statement stm = null;
			ResultSet res = null;
			
			try{
				AssertUtil.assertTrue(parameterData instanceof String);
				String transactionPlayer = (String) parameterData;
				
				stm = DBC.createStatement();
				res = stm.executeQuery("SELECT * FROM shops_statistics WHERE transaction_player = '" + transactionPlayer + "' AND date > " + minTime);
				
				List<ShopTransactionEntry> transactionEntry = new ArrayList<ShopTransactionEntry>();
				while(res.next()){
					try {
						ShopTransactionEntry entry = new ShopTransactionEntry(
							Util.convertStringToLocation(res.getString("sign_loc")), 
							res.getString("transaction_player"),
							ItemStackBase64.stacksFromBase64(res.getString("transaction_item_base64"))[0],
							res.getLong("date"),
							res.getString("shop_owner"),
							TransactionType.valueOf(res.getString("transaction_type")));
						transactionEntry.add(entry);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return transactionEntry;
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(stm != null) stm.close();
					if(res != null) res.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}else if(parameter == GetTransactionParameter.BY_YEAR){
			AssertUtil.assertNull(parameterData);
			long currentTime = new Date().getTime();
			long minTime = currentTime - (60 * 60 * 24 * 365 * 1000);
			
			Statement stm = null;
			ResultSet res = null;
			
			try{
				stm = DBC.createStatement();
				res = stm.executeQuery("SELECT * FROM shops_statistics WHERE date > " + minTime);
				
				List<ShopTransactionEntry> transactionEntry = new ArrayList<ShopTransactionEntry>();
				while(res.next()){
					try {
						ShopTransactionEntry entry = new ShopTransactionEntry(
							Util.convertStringToLocation(res.getString("sign_loc")), 
							res.getString("transaction_player"),
							ItemStackBase64.stacksFromBase64(res.getString("transaction_item_base64"))[0],
							res.getLong("date"),
							res.getString("shop_owner"),
							TransactionType.valueOf(res.getString("transaction_type")));
						transactionEntry.add(entry);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return transactionEntry;
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(stm != null) stm.close();
					if(res != null) res.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
}
