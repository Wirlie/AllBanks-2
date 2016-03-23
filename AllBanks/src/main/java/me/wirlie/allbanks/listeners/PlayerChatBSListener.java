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
import me.wirlie.allbanks.Util;
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
			case SHOP:
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
					
					if(msgValue.compareTo(BigDecimal.ZERO) <= 0){
						//Sólo se pueden pedir valores positivos o mayores a 0
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					String[] splitDecimal = String.valueOf(msgValue.toPlainString()).split("\\D");
					
					if(splitDecimal.length >= 2 && splitDecimal[1].length() > 2){
						//Los decimales son mayores a 2
						Translation.getAndSendMessage(p, StringsID.ONLY_TWO_DECIMALS, true);
						return;
					}
					
					BigDecimal maxLoan = new BigDecimal(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.max-loan"));
					BigDecimal userLoan = ba.BankLoan.getLoan();
					BigDecimal maxBorrow = maxLoan.subtract(userLoan);
					
					if(maxBorrow.compareTo(BigDecimal.ZERO) < 0 || maxBorrow.subtract(msgValue).compareTo(BigDecimal.ZERO) < 0){
						//No puede pedir préstamo.
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", AllBanks.getEconomy().format(maxBorrow.doubleValue()));
						Translation.getAndSendMessage(p, StringsID.BANKLOAN_CAN_NOT_BORROW_MORE_LOAN, replaceMap, true);
						return;
					}
					
					//Pedir préstamo.
					if(ba.BankLoan.addLoan(msgValue)){
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
					BigDecimal userLoan2 = ba.BankLoan.getLoan();
					
					if(msgValue2.compareTo(BigDecimal.ZERO) <= 0){
						//Sólo se pueden pedir valores positivos o mayores a 0
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					String[] splitDecimal2 = String.valueOf(msgValue2.toPlainString()).split("\\D");
					
					if(splitDecimal2.length >= 2 && splitDecimal2[1].length() > 2){
						//Los decimales son mayores a 2
						Translation.getAndSendMessage(p, StringsID.ONLY_TWO_DECIMALS, true);
						return;
					}
					
					if(msgValue2.doubleValue() > userLoan2.doubleValue()){
						//si es mayor, unicamente cobraremos lo que el usuario debe
						msgValue2 = userLoan2;
					}
					
					//¿Puede pagar?
					if(AllBanks.getEconomy().has(p, msgValue2.doubleValue())){
						if(ba.BankLoan.subsLoan(msgValue2)){
							AllBanks.getEconomy().withdrawPlayer(p, msgValue2.doubleValue());
							
							HashMap<String, String> replaceMap = new HashMap<String, String>();
							replaceMap.put("%1%", AllBanks.getEconomy().format(msgValue2.doubleValue()));
							replaceMap.put("%2%", AllBanks.getEconomy().format(ba.BankLoan.getLoan().doubleValue()));
							Translation.getAndSendMessage(p, StringsID.BANKLOAN_SUCCESS_PAY, replaceMap, true);
							
							//Actualizar letrero
							bs.reloadSign();
						}
					}else{
						//no puede pagar
						Translation.getAndSendMessage(p, StringsID.YOU_DO_NOT_HAVE_MONEY, true);
					}
					break;
				}
				break;
			case BANK_MONEY:
				switch(step){
				case 0:
					//Depositar
					BigDecimal msgValue;
					
					try{
						msgValue = new BigDecimal(Double.parseDouble(e.getMessage()));
						e.setCancelled(true);
					}catch(NumberFormatException e1){
						return;
					}
					
					if(msgValue.compareTo(BigDecimal.ZERO) <= 0){
						//Sólo se pueden pedir valores positivos o mayores a 0
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					String[] splitDecimal = String.valueOf(msgValue.toPlainString()).split("\\D");
					
					if(splitDecimal.length >= 2 && splitDecimal[1].length() > 2){
						//Los decimales son mayores a 2
						Translation.getAndSendMessage(p, StringsID.ONLY_TWO_DECIMALS, true);
						return;
					}
					
					BigDecimal maxLimitSave = new BigDecimal(AllBanks.getInstance().getConfig().getDouble("banks.bank-money.max-money-player-can-save", -1));
					boolean unlimitedSave = false;
					
					if(maxLimitSave.compareTo(new BigDecimal(-1)) == 0){
						//-1, dinero ilimitado
						unlimitedSave = true;
					}
					
					if(!unlimitedSave){
						//Si el dinero no es ilimitado, tratamos de acoplarnos al valor máximo.
						BigDecimal remainingSave = maxLimitSave.subtract(ba.BankMoney.getMoney());
					
						if(remainingSave.compareTo(msgValue) < 0){
							BigDecimal msgValueClone = msgValue;
							msgValue = remainingSave;
							
							//Antes de enviar el mensaje.... ¿tiene el dinero?
							if(!AllBanks.getEconomy().has(p, msgValue.doubleValue())){
								Translation.getAndSendMessage(p, StringsID.YOU_DO_NOT_HAVE_MONEY, true);
								return;
							}
							
							HashMap<String, String> replaceMap = new HashMap<String, String>();
							replaceMap.put("%1%", AllBanks.getEconomy().format(msgValueClone.doubleValue()));
							replaceMap.put("%2%", AllBanks.getEconomy().format(msgValue.doubleValue()));
							Translation.getAndSendMessage(p, StringsID.BANKMONEY_MAX_LIMIT_REACHED_1, replaceMap, true);
						}
					}else{
						if(!AllBanks.getEconomy().has(p, msgValue.doubleValue())){
							Translation.getAndSendMessage(p, StringsID.YOU_DO_NOT_HAVE_MONEY, true);
							return;
						}
					}
					
					//Bien, intentar guardar
					if(ba.BankMoney.addMoney(msgValue)){
						AllBanks.getEconomy().withdrawPlayer(p, msgValue.doubleValue());
						
						//Mensaje
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", AllBanks.getEconomy().format(msgValue.doubleValue()));
						Translation.getAndSendMessage(p, StringsID.BANKMONEY_SUCCESS_DEPOSIT, replaceMap, true);
						bs.reloadSign();
					}
					
					break;
				case 1:
					//Retirar
					BigDecimal msgValue2;
					
					try{
						msgValue2 = new BigDecimal(Double.parseDouble(e.getMessage()));
						e.setCancelled(true);
					}catch(NumberFormatException e1){
						return;
					}
					
					if(msgValue2.compareTo(BigDecimal.ZERO) <= 0){
						//Sólo se pueden pedir valores positivos o mayores a 0
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					String[] splitDecimal2 = String.valueOf(msgValue2.toPlainString()).split("\\D");
					
					if(splitDecimal2.length >= 2 && splitDecimal2[1].length() > 2){
						//Los decimales son mayores a 2
						Translation.getAndSendMessage(p, StringsID.ONLY_TWO_DECIMALS, true);
						return;
					}
					
					//Ok, pasó las pruebas básicas ahora hay que trabajar con el banco.
					BigDecimal moneyInBank = ba.BankMoney.getMoney();
					
					if(msgValue2.compareTo(moneyInBank) > 0){
						//Mayor
						msgValue2 = moneyInBank;
					}
					
					//Retirar dinero
					if(ba.BankMoney.subsMoney(msgValue2)){
						AllBanks.getEconomy().depositPlayer(p, msgValue2.doubleValue());
						
						//Mensaje
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", AllBanks.getEconomy().format(msgValue2.doubleValue()));
						Translation.getAndSendMessage(p, StringsID.BANKMONEY_SUCCESS_WITHDRAW, replaceMap, true);
						bs.reloadSign();
					}
					
					break;
				}
				break;
			case BANK_TIME:
				switch(step){
				case 0:
					
					boolean changeAll = false;
					
					//El valor por defecto será el tiempo de BankTime, pero, si no se especifica "all" el valor tomará el valor especificado por e.getMessage()
					int changeAmount = ba.BankTime.getTime();
					
					if(e.getMessage().equalsIgnoreCase("all")){
						changeAll = true;
					}
					
					if(!changeAll){
						try{
							changeAmount = Integer.parseInt(e.getMessage());
							e.setCancelled(true);
						}catch(NumberFormatException e2){
							return;
						}
					}
					
					if(changeAmount > ba.BankTime.getTime()){
						Translation.getAndSendMessage(p, StringsID.BANKTIME_DO_YOU_DO_NOT_HAVE_TIME, true);
						return;
					}
					
					int payPerMinute = AllBanks.getInstance().getConfig().getInt("bank-time.pay-per-minute", 0);
					int pay = changeAmount * payPerMinute;
					
					if(ba.BankTime.subsTime(changeAmount)){
						AllBanks.getEconomy().depositPlayer(p, pay);
						
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", String.valueOf(pay));
						replaceMap.put("%2%", String.valueOf(changeAmount));
						Translation.getAndSendMessage(p, StringsID.BANKTIME_SUCCESS, true);
						
						bs.reloadSign();
					}
					
					break;
				}
				break;
			case BANK_XP:
				switch(step){
				case 0:
					//Depositar XP
					String rawMsg = e.getMessage().replace("lvl", "");
					
					try{
						Integer.parseInt(rawMsg);
						e.setCancelled(true);
					}catch(NumberFormatException e2){
						return;
					}
					
					int depositXP = 0;
					
					if(e.getMessage().endsWith("lvl")){
						int levels = Integer.parseInt(rawMsg);
						depositXP = Util.XPConversionUtil.getExpToLevel(levels);
					}else{
						depositXP = Integer.parseInt(e.getMessage());
					}
					
					if(depositXP <= 0){
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					if(Util.XPConversionUtil.getTotalExperience(p) < depositXP){
						Translation.getAndSendMessage(p, StringsID.BANKXP_ERROR_DEPOSIT_INS_XP, true);
						return;
					}
					
					if(ba.BankXP.addXP(depositXP)){
						Util.XPConversionUtil.setTotalExpToPlayer(p, Util.XPConversionUtil.getTotalExperience(p) - depositXP);
						Translation.getAndSendMessage(p, StringsID.BANKXP_DEPOSIT_SUCCESS, true);
						bs.reloadSign();
					}
					
					break;
				case 1:
					//Retirar XP
					String rawMsg2 = e.getMessage().replace("lvl", "");
					
					try{
						Integer.parseInt(rawMsg2);
						e.setCancelled(true);
					}catch(NumberFormatException e2){
						return;
					}
					
					int withdrawXP = 0;
					
					if(e.getMessage().endsWith("lvl")){
						int levels = Integer.parseInt(rawMsg2);
						withdrawXP = Util.XPConversionUtil.getExpToLevel(levels);
					}else{
						withdrawXP = Integer.parseInt(e.getMessage());
					}
					
					if(withdrawXP <= 0){
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					if(ba.BankXP.getRawXP() < withdrawXP){
						Translation.getAndSendMessage(p, StringsID.BANKXP_ERROR_WITHDRAW_INS_XP, true);
						return;
					}
					
					//Quitar experiencia
					if(ba.BankXP.subsXP(withdrawXP)){
						Util.XPConversionUtil.setTotalExpToPlayer(p, Util.XPConversionUtil.getTotalExperience(p) + withdrawXP);
						Translation.getAndSendMessage(p, StringsID.BANKXP_WITHDRAW_SUCCESS, true);
						bs.reloadSign();
					}
					
					break;
				}
				break;
			case DEFAULT:
				break;
			
			}
		}
	}
}
