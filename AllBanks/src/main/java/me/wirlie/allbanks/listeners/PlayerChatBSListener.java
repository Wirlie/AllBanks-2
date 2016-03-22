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
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.Banks.BankType;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.data.BankAccount;
import me.wirlie.allbanks.data.BankSession;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class PlayerChatBSListener implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		
		BankSession bs = BankSession.getSession(p);
		
		if(bs != null){
			//Ok, el jugador se encuentra en una sesión del banco.

			BankAccount ba = BankAccount.Cache.get(p.getUniqueId());
			
			BankType btype = bs.getBankType();
			int step = bs.getStep();
			
			switch(btype){
			case ATM:
				break;
			case BANK_BUY:
				break;
			case BANK_CHEST:
				break;
			case BANK_LAND:
				break;
			case BANK_LOAN:
				switch(step){
				case 0:
					//¿Es un valor numérico válido?
					try{
						Double.parseDouble(e.getMessage());
						e.setCancelled(true);
					}catch(NumberFormatException e1){
						return;
					}
					
					BigDecimal msgValue = new BigDecimal(Double.parseDouble(e.getMessage()));
					
					if(msgValue.doubleValue() <= 0.00){
						//Sólo se pueden pedir valores positivos o mayores a 0
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					if(String.valueOf(msgValue.doubleValue()).split("\\D")[1].length() > 2){
						//Los decimales son mayores a 2
						Translation.getAndSendMessage(p, StringsID.ONLY_TWO_DECIMALS, true);
						return;
					}
					
					BigDecimal maxLoan = new BigDecimal(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.max-loan"));
					BigDecimal userLoan = ba.BankLoan_getLoan();
					BigDecimal maxBorrow = maxLoan.subtract(userLoan);
					
					if(maxBorrow.doubleValue() < 0.00 || maxBorrow.subtract(msgValue).doubleValue() < 0.00){
						//No puede pedir préstamo.
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", AllBanks.getEconomy().format(maxBorrow.doubleValue()));
						Translation.getAndSendMessage(p, StringsID.BANKLOAN_CAN_NOT_BORROW_MORE_LOAN, replaceMap, true);
						return;
					}
					
					//Pedir préstamo.
					if(ba.BankLoan_addLoan(msgValue)){
						AllBanks.getEconomy().depositPlayer(p, msgValue.doubleValue());
						
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", AllBanks.getEconomy().format(msgValue.doubleValue()));
						Translation.getAndSendMessage(p, StringsID.BANKLOAN_SUCCESS_BORROW, replaceMap, true);
						
						//Actualizar letrero
						bs.reloadSign();
					}
					
					break;
				case 1:
					//¿Es un valor numérico válido?
					try{
						Double.parseDouble(e.getMessage());
						e.setCancelled(true);
					}catch(NumberFormatException e1){
						return;
					}
					
					BigDecimal msgValue2 = new BigDecimal(Double.parseDouble(e.getMessage()));
					BigDecimal userLoan2 = ba.BankLoan_getLoan();
					
					if(msgValue2.doubleValue() > userLoan2.doubleValue()){
						//si es mayor, unicamente cobraremos lo que el usuario debe
						msgValue2 = userLoan2;
					}
					
					//¿Puede pagar?
					if(AllBanks.getEconomy().has(p, msgValue2.doubleValue())){
						if(ba.BankLoan_subsLoan(msgValue2)){
							AllBanks.getEconomy().withdrawPlayer(p, msgValue2.doubleValue());
							
							HashMap<String, String> replaceMap = new HashMap<String, String>();
							replaceMap.put("%1%", AllBanks.getEconomy().format(msgValue2.doubleValue()));
							replaceMap.put("%2%", AllBanks.getEconomy().format(ba.BankLoan_getLoan().doubleValue()));
							Translation.getAndSendMessage(p, StringsID.BANKLOAN_SUCCESS_PAY, replaceMap, true);
							
							//Actualizar letrero
							bs.reloadSign();
						}
					}else{
						//no puede pagar
					}
					break;
				}
				break;
			case BANK_MONEY:
				break;
			case BANK_SELL:
				break;
			case BANK_TIME:
				break;
			case BANK_USER:
				break;
			case BANK_XP:
				break;
			case DEFAULT:
				break;
			
			}
		}
	}
}
