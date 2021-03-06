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
package me.wirlie.allbanks.listeners.banks;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.Banks.ABSignType;
import me.wirlie.allbanks.banks.bankdata.BankAccount;
import me.wirlie.allbanks.banks.bankdata.BankSession;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.utils.AllBanksLogger;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.ExperienceConversionUtil;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;

/**
 * Procesar mensajes que tengan que ver con la sesión de un banco.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
@SuppressWarnings("javadoc")
public class PlayerChatBankSessionListener implements Listener {

	public PlayerChatBankSessionListener(){
		AllBanksLogger.info("PlayerChatBSListener");
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		
		BankSession bs = BankSession.getSession(p);
		
		if(bs != null){
			//Ok, el jugador se encuentra en una sesión del banco.
			
			//Actualizar ultimo uso, para evitar un cierre automático por falta de actividad
			bs.updateLastUse();

			BankAccount ba = BankAccount.Cache.get(p.getUniqueId());
			
			ABSignType btype = bs.getBankType();
			int step = bs.getStep();
			
			switch(btype){
			case ATM:
				break;
			case BANK_CHEST:
				switch(step){
				case 0:
					
					if(e.getMessage().equalsIgnoreCase("open")){
						//Abrir cofre virtual
						e.setCancelled(true);
					}else{
						return;
					}
					
					int currentChestCursor = ba.BankChest.getCurrentChestCursor();
					
					Banks.openVirtualChestForPlayer(p, currentChestCursor);
					
					InteractiveUtil.sendSound(p, SoundType.VIRTUAL_CHEST_OPEN);
					
					break;
				}
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
					
					String[] splitDecimal = e.getMessage().split("\\D");
					
					if(splitDecimal.length >= 2 && splitDecimal[1].length() > 2){
						//Los decimales son mayores a 2
						Translation.getAndSendMessage(p, StringsID.ONLY_TWO_DECIMALS, true);
						return;
					}
					
					BigDecimal maxLoan = BigDecimal.ZERO; 
					boolean overrideConfiguration = false;
					
					for(PermissionAttachmentInfo pinfo : p.getEffectivePermissions()){
						if(pinfo.getPermission().startsWith("allbanks.banks.bankloan.maxloan.")){
							try{
								maxLoan = new BigDecimal(Double.parseDouble(pinfo.getPermission().replace("allbanks.banks.bankloan.maxloan.", "")));
								overrideConfiguration = true;
							}catch(NumberFormatException e2){
								overrideConfiguration = false;
							}
							break;
						}
					}
					
					if(!overrideConfiguration)
						maxLoan = new BigDecimal(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.max-loan"));
					
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
						
					}else{
						if(DataBaseUtil.databaseIsLocked()){
							DataBaseUtil.sendDatabaseLockedMessage(p);
						}else{
							Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
						}
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
					
					String[] splitDecimal2 = e.getMessage().split("\\D");
					
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
						}else{
							if(DataBaseUtil.databaseIsLocked()){
								DataBaseUtil.sendDatabaseLockedMessage(p);
							}else{
								Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
							}
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
					
					String[] splitDecimal = e.getMessage().split("\\D");
					
					if(splitDecimal.length >= 2 && splitDecimal[1].length() > 2){
						//Los decimales son mayores a 2
						Translation.getAndSendMessage(p, StringsID.ONLY_TWO_DECIMALS, true);
						return;
					}
					
					
					BigDecimal maxLimitSave = BigDecimal.ZERO;
					boolean overrideConfiguration = false;
					
					for(PermissionAttachmentInfo pinfo : p.getEffectivePermissions()){
						if(pinfo.getPermission().startsWith("allbanks.banks.bankmoney.maxmoney.")){
							try{
								maxLimitSave = new BigDecimal(Double.parseDouble(pinfo.getPermission().replace("allbanks.banks.bankmoney.maxmoney.", "")));
								overrideConfiguration = true;
							}catch(NumberFormatException e2){
								overrideConfiguration = false;
							}
							break;
						}
					}
					
					if(!overrideConfiguration)
						maxLimitSave = new BigDecimal(AllBanks.getInstance().getConfig().getDouble("banks.bank-money.max-money-player-can-save", -1));
					
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
						}else if(!AllBanks.getEconomy().has(p, msgValue.doubleValue())){
							Translation.getAndSendMessage(p, StringsID.YOU_DO_NOT_HAVE_MONEY, true);
							return;
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
					}else{
						if(DataBaseUtil.databaseIsLocked()){
							DataBaseUtil.sendDatabaseLockedMessage(p);
						}else{
							Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
						}
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
					
					String[] splitDecimal2 = e.getMessage().split("\\D");
					
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
					}else{
						if(DataBaseUtil.databaseIsLocked()){
							DataBaseUtil.sendDatabaseLockedMessage(p);
						}else{
							Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
						}
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
						e.setCancelled(true);
					}
					
					if(!changeAll){
						try{
							changeAmount = Integer.parseInt(e.getMessage());
							e.setCancelled(true);
						}catch(NumberFormatException e2){
							return;
						}
					}
					
					if(changeAmount <= 0){
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					if(changeAmount > ba.BankTime.getTime()){
						Translation.getAndSendMessage(p, StringsID.BANKTIME_DO_YOU_DO_NOT_HAVE_TIME, true);
						return;
					}
					
					BigDecimal payPerMinute = BigDecimal.ZERO;
					boolean overrideConfiguration = false;
					
					for(PermissionAttachmentInfo pinfo : p.getEffectivePermissions()){
						if(pinfo.getPermission().startsWith("allbanks.banks.banktime.payperminute.")){
							try{
								payPerMinute = new BigDecimal(Double.parseDouble(pinfo.getPermission().replace("allbanks.banks.banktime.payperminute.", "")));
								overrideConfiguration = true;
							}catch(NumberFormatException e2){
								overrideConfiguration = false;
							}
							break;
						}
					}
					
					if(!overrideConfiguration)
						payPerMinute = new BigDecimal(AllBanks.getInstance().getConfig().getDouble("banks.bank-time.pay-per-minute", 0));
					
					if(payPerMinute.compareTo(BigDecimal.ZERO) != 1){
						p.sendMessage("Error: 'payPerMinute' equal to 0 or is less than 0.");
						p.sendMessage("Probably the value of 'banks.bank-time.pay-per-minute' not exists, please check the configuration file.");
						return;
					}
					
					BigDecimal pay = payPerMinute.multiply(new BigDecimal(changeAmount));
					
					if(ba.BankTime.subsTime(changeAmount)){
						AllBanks.getEconomy().depositPlayer(p, pay.doubleValue());
						
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						replaceMap.put("%1%", AllBanks.getEconomy().format(pay.doubleValue()));
						replaceMap.put("%2%", String.valueOf(changeAmount));
						Translation.getAndSendMessage(p, StringsID.BANKTIME_SUCCESS, replaceMap, true);
						
						bs.reloadSign();
					}else{
						if(DataBaseUtil.databaseIsLocked()){
							DataBaseUtil.sendDatabaseLockedMessage(p);
						}else{
							Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
						}
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
						depositXP = ExperienceConversionUtil.getExpToLevel(levels);
					}else{
						depositXP = Integer.parseInt(e.getMessage());
					}
					
					if(depositXP <= 0){
						Translation.getAndSendMessage(p, StringsID.ONLY_VALID_NUMBER_MORE_THAN_0, true);
						return;
					}
					
					if(ExperienceConversionUtil.getTotalExperience(p) < depositXP){
						Translation.getAndSendMessage(p, StringsID.BANKXP_ERROR_DEPOSIT_INS_XP, true);
						return;
					}
					
					//Limite de experiencia
					int experienceLimit = 0;
					boolean overrideConfiguration = false;
					
					for(PermissionAttachmentInfo pinfo : p.getEffectivePermissions()){
						if(pinfo.getPermission().startsWith("allbanks.banks.bankxp.maxxp.")){
							
							String experienceLimitSTR = pinfo.getPermission().replace("allbanks.banks.bankxp.maxxp.", "");
								
							Pattern pattern = Pattern.compile("^([0-9]{1,})(L|Lvl|Levels)$", Pattern.CASE_INSENSITIVE);
							Matcher matcher = pattern.matcher(experienceLimitSTR);
								
							if(matcher.matches()){
								int levels = Integer.parseInt(matcher.group(1));
								experienceLimit = ExperienceConversionUtil.getExpToLevel(levels);
								overrideConfiguration = true;
							}else{
								overrideConfiguration = false;
							}
							
							break;
						}
					}
					
					if(!overrideConfiguration){
						String experienceLimitSTR = AllBanks.getInstance().getConfig().getString("banks.bank-xp.max-xp-player-can-save");
						
						Pattern pattern = Pattern.compile("^([0-9]{1,})(L|Lvl|Levels)$", Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(experienceLimitSTR);
						
						if(matcher.matches()){
							int levels = Integer.parseInt(matcher.group(1));
							experienceLimit = ExperienceConversionUtil.getExpToLevel(levels);
						}else{
							//Intentar el método de número
							try{
								experienceLimit = Integer.parseInt(experienceLimitSTR);
							}catch(NumberFormatException e2){
								AllBanksLogger.warning("[CONFIG] banks.bank-xp.max-xp-player-can-save invalid configuration: " + experienceLimitSTR + ", a number or a valid string '10L' has expected. Using default value: '100L'");
								experienceLimit = ExperienceConversionUtil.getExpToLevel(100);
							}
						}
					}
					
					if(experienceLimit < 0){
						experienceLimit = -1;
					}
					
					if(experienceLimit >= 0 && (ba.BankXP.getRawXP() + depositXP) > experienceLimit){
						//Se excede
						int calculateRemainingExp = experienceLimit - ba.BankXP.getRawXP();
						if(calculateRemainingExp < 0) calculateRemainingExp = 0;
						
						Translation.getAndSendMessage(p, StringsID.BANKXP_ERROR_DEPOSIT_MAX_REACHED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + calculateRemainingExp, "%2%>>>" + ExperienceConversionUtil.convertExpToLevel(calculateRemainingExp)), true);
						
						return;
					}
						
					if(ba.BankXP.addXP(depositXP)){
						ExperienceConversionUtil.setTotalExpToPlayer(p, ExperienceConversionUtil.getTotalExperience(p) - depositXP);
						Translation.getAndSendMessage(p, StringsID.BANKXP_DEPOSIT_SUCCESS, true);
						bs.reloadSign();
					}else{
						if(DataBaseUtil.databaseIsLocked()){
							DataBaseUtil.sendDatabaseLockedMessage(p);
						}else{
							Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
						}
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
						withdrawXP = ExperienceConversionUtil.getExpToLevel(levels);
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
						ExperienceConversionUtil.setTotalExpToPlayer(p, ExperienceConversionUtil.getTotalExperience(p) + withdrawXP);
						Translation.getAndSendMessage(p, StringsID.BANKXP_WITHDRAW_SUCCESS, true);
						bs.reloadSign();
					}else{
						if(DataBaseUtil.databaseIsLocked()){
							DataBaseUtil.sendDatabaseLockedMessage(p);
						}else{
							Translation.getAndSendMessage(p, StringsID.SQL_EXCEPTION_PROBLEM, true);
						}
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
