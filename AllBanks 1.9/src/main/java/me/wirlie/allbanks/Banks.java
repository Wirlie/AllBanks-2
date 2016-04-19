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
package me.wirlie.allbanks;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.wirlie.allbanks.AllBanks.StorageType;
import me.wirlie.allbanks.tempdata.BankAccount;
import me.wirlie.allbanks.tempdata.BankSession;
import me.wirlie.allbanks.utils.ChatUtil;
import me.wirlie.allbanks.utils.ConfigurationUtil;
import me.wirlie.allbanks.utils.DataBaseUtil;
import me.wirlie.allbanks.utils.ItemStackBase64;
import me.wirlie.allbanks.utils.StringLocationUtil;
import me.wirlie.allbanks.utils.Util;

/**
 * Esta clase se encarga de procesar acciones relacionado con los letreros de AllBanks
 * en especial, aquellos que pertenecen a BANK_LOAN, BANK_XP, BANK_TIME, BANK_MONEY
 * ATM, BANK_CHEST.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Banks {
	
	/**
	 * Tipo de banco
	 * @author Wirlie
	 */
	public static enum ABSignType{
		BANK_LOAN("Loan"),
		BANK_XP("XP"),
		BANK_TIME("Time"),
		BANK_MONEY("Money"),
		BANK_LAND("Land"),
		ATM("ATM"), 
		BANK_CHEST("Chest"), 
		DEFAULT("");
		
		String display;
		
		ABSignType(String display){
			this.display = display;
		}
		
		String getShortName(){
			return display;
		}
		
		
		public static ABSignType getSignTypeByShortName(String string){
			
			for(ABSignType b : ABSignType.values()){
				if(b.getShortName().equalsIgnoreCase(string)){
					return b;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * Acciones de un jugador
	 * @author Wirlie
	 *
	 */
	public static enum ABSignAction{
		NEW_SIGN,
		DESTROY_SIGN,
		USE_SIGN;
	}
	
	/**
	 * Comprobar si un jugador tiene permisos para efectuar una acción.
	 * @param p Jugador
	 * @param signAction Accion del jugador
	 * @param signType Tipo de banco
	 * @return true si el jugador tiene permiso.
	 */
	public static boolean playerHasPermissionForPerformAction(Player p, ABSignAction signAction, ABSignType signType){
		
		if(signType == null){
			return true;
		}
		
		if(DataBaseUtil.databaseIsLocked()){
			if(!signType.equals(ABSignType.BANK_CHEST)){
				//Actualmente BankChest es el único banco que no necesita a la base de datos.
				DataBaseUtil.sendDatabaseLockedMessage(p);
			}
		}
		
		switch(signAction){
		case NEW_SIGN:
			switch(signType){
			case ATM:
				return Util.hasPermission(p, "allbanks.sign.atm.new");
			case BANK_LAND:
				return Util.hasPermission(p, "allbanks.sign.land.new");
			case BANK_LOAN:
				return Util.hasPermission(p, "allbanks.sign.loan.new");
			case BANK_MONEY:
				return Util.hasPermission(p, "allbanks.sign.money.new");
			case BANK_TIME:
				return Util.hasPermission(p, "allbanks.sign.time.new");
			case BANK_XP:
				return Util.hasPermission(p, "allbanks.sign.xp.new");
			case BANK_CHEST:
				return Util.hasPermission(p, "allbanks.sign.chest.new");
			case DEFAULT:
				break;
			}
		case DESTROY_SIGN:
			switch(signType){
			case ATM:
				return Util.hasPermission(p, "allbanks.sign.atm.destroy");
			case BANK_LAND:
				return Util.hasPermission(p, "allbanks.sign.land.destroy");
			case BANK_LOAN:
				return Util.hasPermission(p, "allbanks.sign.loan.destroy");
			case BANK_MONEY:
				return Util.hasPermission(p, "allbanks.sign.money.destroy");
			case BANK_TIME:
				return Util.hasPermission(p, "allbanks.sign.time.destroy");
			case BANK_XP:
				return Util.hasPermission(p, "allbanks.sign.xp.destroy");
			case BANK_CHEST:
				return Util.hasPermission(p, "allbanks.sign.chest.destroy");
			case DEFAULT:
				break;
			}
			
		case USE_SIGN:
			switch(signType){
			case ATM:
				return Util.hasPermission(p, "allbanks.sign.atm.use");
			case BANK_LAND:
				return Util.hasPermission(p, "allbanks.sign.land.use");
			case BANK_LOAN:
				return Util.hasPermission(p, "allbanks.sign.loan.use");
			case BANK_MONEY:
				return Util.hasPermission(p, "allbanks.sign.money.use");
			case BANK_TIME:
				return Util.hasPermission(p, "allbanks.sign.time.use");
			case BANK_XP:
				return Util.hasPermission(p, "allbanks.sign.xp.use");
			case BANK_CHEST:
				return Util.hasPermission(p, "allbanks.sign.chest.use");
			case DEFAULT:
				break;
			}
		default:
			break;
		
		}
		
		AllBanks.getInstance().getLogger().warning("Method playerHasPermission returned with false (default), Action: " + signAction + ", btype: " + signType);
		return false;
	}
	
	/**
	 * Cambiar un letrero a su estado inicial.
	 * @param allbanksSign Letrero
	 * @param signType Tipo de banco
	 */
	public static void switchSignToInitialState(Sign allbanksSign, ABSignType signType){
		if(!allbanksSign.getBlock().getType().equals(Material.SIGN) && !allbanksSign.getBlock().getType().equals(Material.WALL_SIGN))
			//Si el letrero ya no existe ignoramos, esto puede suceder ya que la función switchSignTo puede ser llamada 1 segundo después.
			return;
		
		switchABSignToStep(signType, allbanksSign, -1, true);
		
	}
	
	/**
	 * Obtener el siguiente paso de un banco.
	 * @param bankSession Sesión de banco
	 * @return Siguiente paso del banco.
	 */
	public static int getNextABSignStep(BankSession bankSession){
		int nextStep = bankSession.getStep() + 1;
		
		switch(bankSession.getBankType()){
		case ATM:
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case BANK_CHEST:
			nextStep = 0;
			break;
		case BANK_LAND:
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case BANK_LOAN:
			if(nextStep >= 2){
				nextStep = 0;
			}
			break;
		case BANK_MONEY:
			if(nextStep >= 2){
				nextStep = 0;
			}
			break;
		case BANK_TIME:
			//Por el momento, BankTime solo tiene el paso 0.
			nextStep = 0;
			break;
		case BANK_XP:
			if(nextStep >= 2){
				nextStep = 0;
			}
			break;
		case DEFAULT:
			nextStep = 0;
			break;
		
		}
		
		return nextStep;
	}

	/**
	 * Cambiar un letrero a un paso en específico.
	 * @param signType Tipo de banco.
	 * @param allbanksSign Letrero de AllBanks.
	 * @param signStep Paso al que se desea cambiar.
	 */
	public static void switchABSignToStep(ABSignType signType, Sign allbanksSign, int signStep, boolean sendInfoMessages) {
		
		allbanksSign.setLine(0, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "AllBanks");
		allbanksSign.setLine(1, ChatColor.DARK_GRAY + signType.getShortName());
		
		//No en todos los bancos y pasos se requiere esta variable BS.
		//Esta variable puede ser nula en ocasiones, nunca se debe descartar esto.
		BankSession bankSession = BankSession.getActiveSessionBySign(allbanksSign);
		BankAccount bankAccount = null; //Nulo, inicialmente. Puede ser nulo si bs no está especificado.
		Player player = null; //Nulo, será nulo si bs no está especificado.
		
		if(bankSession != null){
			bankAccount = BankAccount.Cache.get(bankSession.getPlayer().getUniqueId()); //Si bs (BankSession) no es nulo, se puede obtener ba (BankAccount).
			player = bankSession.getPlayer();
		}
		
		switch(signType){
		case ATM:
			switch(signStep){
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				allbanksSign.setLine(2, ChatColor.DARK_GREEN + StringsID.CLICK_TO_USE.toString(false));
				allbanksSign.setLine(3, "");
				break;
			}
			break;
		case BANK_CHEST:
			switch(signStep){
			case 0:
				//Encontrar el siguiente cofre
				bankAccount.BankChest.switchToNextChest();
				
				//Abrir interfaz de cofre
				HashMap<String, String> replaceMap = new HashMap<String, String>();
				replaceMap.put("%1%", String.valueOf(bankAccount.BankChest.getCurrentChestCursor()));
				
				allbanksSign.setLine(2, ChatColor.DARK_GREEN + StringsID.BANKCHEST_CHEST_NUMBER.toString(replaceMap, false));
				allbanksSign.setLine(3, "");
				
				if(player != null && sendInfoMessages){
					Translation.getAndSendMessage(player, StringsID.BANKCHEST_STEP0_INFO, true);
				}
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				allbanksSign.setLine(2, ChatColor.DARK_GREEN + StringsID.CLICK_TO_USE.toString(false));
				allbanksSign.setLine(3, "");
				break;
			}
			break;
		case BANK_LAND:
			switch(signStep){
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				allbanksSign.setLine(2, ChatColor.DARK_GREEN + StringsID.CLICK_TO_USE.toString(false));
				allbanksSign.setLine(3, "");
				break;
			}
			break;
		case BANK_LOAN:
			switch(signStep){
			case 0:
				//Valor por defecto: 0
				BigDecimal maxBorrow = BigDecimal.ZERO;
				//¿Anular configuración?
				boolean overrideConfiguration = false;
				
				//Comprobar si el jugador posee un permiso especial.
				for(PermissionAttachmentInfo playerPermission : player.getEffectivePermissions()){
					if(playerPermission.getPermission().startsWith("allbanks.banks.bankloan.maxloan.")){
						//Posee un permiso especial, intentar parsear el número
						try{
							maxBorrow = new BigDecimal(Double.parseDouble(playerPermission.getPermission().replace("allbanks.banks.bankloan.maxloan.", ""))).subtract(bankAccount.BankLoan.getLoan());
							overrideConfiguration = true;
						}catch(NumberFormatException e2){
							overrideConfiguration = false;
						}
					}
				}
				
				//Si la configuración no se anulará definimos el valor que contiene Config.yml
				if(!overrideConfiguration)
					maxBorrow = new BigDecimal(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.max-loan")).subtract(bankAccount.BankLoan.getLoan());
				
				//Establecer nuevas líneas
				allbanksSign.setLine(2, ChatColor.DARK_BLUE + StringsID.ASK.toString(false));
				allbanksSign.setLine(3, ChatColor.DARK_GREEN + AllBanks.getEconomy().format(maxBorrow.doubleValue()));
				
				//Mensaje al jugador
				if(player != null && sendInfoMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", String.valueOf(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.interest", 0)));
					replaceMap.put("%2%", String.valueOf(ConfigurationUtil.convertTimeValueToSeconds(AllBanks.getInstance().getConfig().getString("banks.bank-loan.collect-interest-every"))));
					Translation.getAndSendMessage(player, StringsID.BANKLOAN_STEP0_INFO, replaceMap, true);
				}
				break;
			case 1:
				allbanksSign.setLine(2, ChatColor.DARK_BLUE + StringsID.PAY.toString(false));
				allbanksSign.setLine(3, ChatColor.DARK_BLUE + AllBanks.getEconomy().format(bankAccount.BankLoan.getLoan().doubleValue()));
				
				//Mensaje al jugador
				if(player != null && sendInfoMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", AllBanks.getEconomy().format(bankAccount.BankLoan.getLoan().doubleValue()));
					Translation.getAndSendMessage(player, StringsID.BANKLOAN_STEP1_INFO, replaceMap, true);
				}
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				allbanksSign.setLine(2, ChatColor.DARK_GREEN + StringsID.CLICK_TO_USE.toString(false));
				allbanksSign.setLine(3, "");
				break;
			}
			break;
		case BANK_MONEY:
			switch(signStep){
			case 0:
				//Depositar
				BigDecimal moneyInBank = bankAccount.BankMoney.getMoney();
				
				allbanksSign.setLine(2, ChatColor.DARK_BLUE + StringsID.DEPOSIT_MONEY.toString(false));
				allbanksSign.setLine(3, ChatColor.DARK_GREEN + AllBanks.getEconomy().format(moneyInBank.doubleValue()));
				
				//Mensaje al jugador
				if(player != null && sendInfoMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", AllBanks.getEconomy().format(bankAccount.BankMoney.getMoney().doubleValue()));
					Translation.getAndSendMessage(player, StringsID.BANKMONEY_STEP0_INFO, replaceMap, true);
				}
				break;
			case 1:
				//Retirar
				BigDecimal moneyInBank2 = bankAccount.BankMoney.getMoney();
				
				allbanksSign.setLine(2, ChatColor.DARK_BLUE + StringsID.WITHDRAW_MONEY.toString(false));
				allbanksSign.setLine(3, ChatColor.DARK_GREEN + AllBanks.getEconomy().format(moneyInBank2.doubleValue()));
				
				//Mensaje al jugador
				if(player != null && sendInfoMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", AllBanks.getEconomy().format(bankAccount.BankMoney.getMoney().doubleValue()));
					Translation.getAndSendMessage(player, StringsID.BANKMONEY_STEP1_INFO, replaceMap, true);
				}
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				allbanksSign.setLine(2, ChatColor.DARK_GREEN + StringsID.CLICK_TO_USE.toString(false));
				allbanksSign.setLine(3, "");
				break;
			}
			break;
		case BANK_TIME:
			switch(signStep){
			case 0:
				//Retirar tiempo (este banco solo tiene este estatus)
				allbanksSign.setLine(2, ChatColor.DARK_BLUE + StringsID.CHANGE_TIME.toString(false));
				allbanksSign.setLine(3, ChatColor.DARK_GREEN + String.valueOf(bankAccount.BankTime.getTime()));
				
				//Mensaje al jugador
				if(player != null && sendInfoMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					Translation.getAndSendMessage(player, StringsID.BANKTIME_STEP0_INFO, replaceMap, true);
				}
				
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				allbanksSign.setLine(2, ChatColor.DARK_GREEN + StringsID.CLICK_TO_USE.toString(false));
				allbanksSign.setLine(3, "");
				break;
			}
			break;
		case BANK_XP:
			switch(signStep){
			case 0:
				//depositar xp
				allbanksSign.setLine(2, ChatColor.DARK_BLUE + StringsID.DEPOSIT_XP.toString(false));
				allbanksSign.setLine(3, ChatColor.DARK_GREEN + String.valueOf(bankAccount.BankXP.getRawXP()) + " (" + bankAccount.BankXP.getLvlForRawXP() + " lvl)");
				
				//Mensaje al jugador
				if(player != null && sendInfoMessages){
					Translation.getAndSendMessage(player, StringsID.BANKXP_STEP0_INFO, true);
				}
				break;
			case 1:
				//retirar xp
				allbanksSign.setLine(2, ChatColor.DARK_BLUE + StringsID.WITHDRAW_XP.toString(false));
				allbanksSign.setLine(3, ChatColor.DARK_GREEN + String.valueOf(bankAccount.BankXP.getRawXP()) + " (" + bankAccount.BankXP.getLvlForRawXP() + " lvl)");
				
				//Mensaje al jugador
				if(player != null && sendInfoMessages){
					Translation.getAndSendMessage(player, StringsID.BANKXP_STEP1_INFO, true);
				}
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				allbanksSign.setLine(2, ChatColor.DARK_GREEN + StringsID.CLICK_TO_USE.toString(false));
				allbanksSign.setLine(3, "");
				break;
			}
			break;
		case DEFAULT:
			break;
		
		}
		
		allbanksSign.update();
	}
	
	/**
	 * Registrar un letrero de AllBanks.
	 * @param signLocation Localización del letrero a registrar.
	 * @param signOwner Dueño del letrero.
	 * @return true si el registro fue exitoso, false si ocurrió un error importante
	 */
	public static boolean registerNewABSign(Location signLocation, Player signOwner){
		
		if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
			
			if(!Util.FlatFile_signFolder.exists()) {
				Util.FlatFile_signFolder.mkdirs();
			}
			
			File signFile = new File(Util.FlatFile_signFolder + File.separator + "sign-" + signLocation.getWorld().getName() + "-" + signLocation.getBlockX() + "-" + signLocation.getBlockY() + "-" + signLocation.getBlockZ() + ".yml");
			if(!signFile.exists()) {
				try {
					signFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(signFile);
			yaml.set("location", StringLocationUtil.convertLocationToString(signLocation, true));
			yaml.set("owner", signOwner.getName());
			
			try {
				yaml.save(signFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return true;
		}else{
			if(DataBaseUtil.databaseIsLocked()) return false;
			
			Statement stm = null;
			
			try{
				stm = AllBanks.getDataBaseConnection().createStatement();
				stm.executeUpdate("INSERT INTO signs (location, owner) VALUES ('" + StringLocationUtil.convertLocationToString(signLocation, true) + "', '" + signOwner.getName() + "')");
				return true;
			}catch(SQLException e){
				DataBaseUtil.checkDatabaseIsLocked(e);
			}finally{
				if(stm != null)
					try {
						stm.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			
		}

		return false;
	}
	
	/**
	 * Comprobar si un letrero está registrado en AllBanks.
	 * @param signLocation Localización del letrero a comprobar.
	 * @return true si el letrero pertenece a AllBanks y se encuentra registrado.
	 */
	public static boolean signIsRegistered(Location signLocation){
		if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
			if(!Util.FlatFile_signFolder.exists()) {
				Util.FlatFile_signFolder.mkdirs();
			}
			
			File signFile = new File(Util.FlatFile_signFolder + File.separator + "sign-" + signLocation.getWorld().getName() + "-" + signLocation.getBlockX() + "-" + signLocation.getBlockY() + "-" + signLocation.getBlockZ() + ".yml");
			return signFile.exists();
		}else{
			if(DataBaseUtil.databaseIsLocked()) return false;
			
			Statement stm = null;
			try{
				stm = AllBanks.getDataBaseConnection().createStatement();
				ResultSet res = stm.executeQuery("SELECT * FROM signs WHERE location = '" + StringLocationUtil.convertLocationToString(signLocation, true) + "'");
				return res.next();
			}catch(SQLException e){
				DataBaseUtil.checkDatabaseIsLocked(e);
			}finally{
				if(stm != null)
					try {
						stm.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
		
		return false;
	}
	
	/**
	 * Remover un letrero de AllBanks previamente registrado.
	 * @param signLocation Localización del letrero.
	 * @return true si la operación fue exitosa y no han ocurrido problemas importantes.
	 */
	public static boolean removeSignFromAllBanks(Location signLocation){
		if(AllBanks.getStorageMethod().equals(StorageType.FLAT_FILE)) {
			if(!Util.FlatFile_signFolder.exists()) {
				Util.FlatFile_signFolder.mkdirs();
			}
			
			File signFile = new File(Util.FlatFile_signFolder + File.separator + "sign-" + signLocation.getWorld().getName() + "-" + signLocation.getBlockX() + "-" + signLocation.getBlockY() + "-" + signLocation.getBlockZ() + ".yml");
			if(!signFile.exists()) return false;
			
			signFile.delete();
			return true;
		} else {
			if(DataBaseUtil.databaseIsLocked()) return false;
			
			Statement stm = null;
			
			try{
				stm = AllBanks.getDataBaseConnection().createStatement();
				stm.executeUpdate("DELETE FROM signs WHERE location = '" + StringLocationUtil.convertLocationToString(signLocation, true) + "'");
				return true;
			}catch(SQLException e){
				DataBaseUtil.checkDatabaseIsLocked(e);
			}finally{
				if(stm != null)
					try {
						stm.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
		
		return false;
	}

	/**
	 * Abrir un cofre virtual de AllBanks a un jugador.
	 * @param player Jugador al que se desea mostrar el cofre virtual.
	 * @param chestNumber Obtener el cofre # 
	 */
	public static void openVirtualChestForPlayer(Player player, int chestNumber) {
		if(player == null) throw new IllegalArgumentException("Player can not be null!!");
		if(chestNumber <= 0) throw new IllegalArgumentException("invalid ChestCursor");
		
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		replaceMap.put("%1%", String.valueOf(chestNumber));
		
		Inventory virtualChestInventory = Bukkit.getServer().createInventory(null, 9 * 6, "ab:virtualchest:" + chestNumber);
		
		Iterator<Entry<Integer, ItemStack>> it = getVirtualChestInventoryContents(player.getName(), chestNumber).entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, ItemStack> entry = it.next();
			virtualChestInventory.setItem(entry.getKey(), entry.getValue());
		}
		
		//StringsID.BANKCHEST_VIRTUAL_INVENTORY.toString(replaceMap, false)
		player.openInventory(virtualChestInventory);
	}
	
	/**
	 * Obtiene el contenido de un cofre virtual de AllBanks.
	 * @param chestOwner Dueño del cofre virtual.
	 * @param chestNumber Número de cofre.
	 * @return se devuelve todo el contenido del cofre en un {@code HashMap<Integer, ItemStack>} que 
	 * interpretado en términos simples es igual que: {@code HashMap<Slot, Item>}
	 */
	public static HashMap<Integer, ItemStack> getVirtualChestInventoryContents(String chestOwner, int chestNumber){
		
		File virtualChestDataFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "VirtualChestData");
		if(!virtualChestDataFolder.exists()) virtualChestDataFolder.mkdirs();
		
		File virtualChestFile = new File(virtualChestDataFolder + File.separator + chestOwner + "-B64.yml");
		if(!virtualChestFile.exists())
			try {
				virtualChestFile.createNewFile();
				return new HashMap<Integer, ItemStack>();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		YamlConfiguration virtualChestYaml = YamlConfiguration.loadConfiguration(virtualChestFile);
		HashMap<Integer, ItemStack> returnContents = new HashMap<Integer, ItemStack>();
		
		ConfigurationSection configurationSection = virtualChestYaml.getConfigurationSection(String.valueOf(chestNumber));
		if(configurationSection == null) return new HashMap<Integer, ItemStack>();
		
		for(String key : configurationSection.getKeys(false)){
			ItemStack getItemStack = null;
			
			try {
				getItemStack = ItemStackBase64.stacksFromBase64(virtualChestYaml.getString(String.valueOf(chestNumber) + "." + key, null))[0];
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(getItemStack != null){
				returnContents.put(Integer.parseInt(key), getItemStack);
			}
		}
		
		return returnContents;
	}
	
	/**
	 * Establece el contenido de un cofre virtual.
	 * @param chestOwner Dueño
	 * @param chestNumber Número de cofre
	 * @param newInventoryContent Nuevo contenido, formateado en un {@code HashMap<Integer, ItemStack>}<br> 
	 * (que es lo mismo que decir: {@code HashMap<Slot, Item>}).
	 */
	public static void setVirtualChestInventoryContents(String chestOwner, int chestNumber, HashMap<Integer, ItemStack> newInventoryContent){
		File virtualChestDataFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "VirtualChestData");
		if(!virtualChestDataFolder.exists()) virtualChestDataFolder.mkdirs();
		
		File virtualChestFile = new File(virtualChestDataFolder + File.separator + chestOwner + "-B64.yml");
		if(!virtualChestFile.exists())
			try {
				virtualChestFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		YamlConfiguration virtualChestYaml = YamlConfiguration.loadConfiguration(virtualChestFile);
		
		Iterator<Entry<Integer, ItemStack>> it = newInventoryContent.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, ItemStack> entry = it.next();
			
			ItemStack slotItem = entry.getValue();
			
			virtualChestYaml.set(String.valueOf(chestNumber) + "." + String.valueOf(entry.getKey()), ItemStackBase64.toBase64(slotItem));
		}
		
		try {
			virtualChestYaml.save(virtualChestFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Verificar si un bloque contiene uno o más letreros de AllBanks.
	 * @param block El bloque a verificar.
	 */
	
	public static boolean blockIsSupportForABSigns(Block block) {
		//TODO Se necesita agregar un método que compruebe la dirección del letrero para verificar si el letrero realmente está siendo sujetado por el bloque.
		for(int i = 0; i < 4; i++){
			Block relativeBlock = (i == 0) ? block.getRelative(BlockFace.NORTH) : ((i == 1) ? block.getRelative(BlockFace.SOUTH) : ((i == 2) ? block.getRelative(BlockFace.WEST) : block.getRelative(BlockFace.EAST)));
		
			if(relativeBlock.getType().equals(Material.WALL_SIGN)){
				if(signIsABSign((Sign) relativeBlock.getState())){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Obtiene los letreros de AllBanks que están siendo sostenidos por un bloque en específico.
	 * @param block Bloque a checar.
	 * @return Devuelve un listado con todos los letreros que fueron encontrados adjuntados al bloque especificado.
	 */
	
	public static List<Sign> getABSignsBySupportBlock(Block block){
		//TODO Se necesita agregar un método que compruebe la dirección del letrero para verificar si el letrero realmente está siendo sujetado por el bloque.
		List<Sign> returnList = new ArrayList<Sign>();
		
		for(int i = 0; i < 4; i++){
			Block relativeBlock = (i == 0) ? block.getRelative(BlockFace.NORTH) : ((i == 1) ? block.getRelative(BlockFace.SOUTH) : ((i == 2) ? block.getRelative(BlockFace.WEST) : block.getRelative(BlockFace.EAST)));
		
			if(relativeBlock.getType().equals(Material.WALL_SIGN)){
				if(signIsABSign((Sign) relativeBlock.getState())){
					returnList.add((Sign) relativeBlock.getState());
				}
			}
		}
		
		return returnList;
	}
	
	/**
	 * Verificar de manera rápida si un letrero pertenece a un letrero de AllBanks
	 * @param checkSign Letrero a verificar.
	 * @return true si el letrero pertenece a AllBanks.
	 */
	public static boolean signIsABSign(Sign checkSign){
		if(ChatUtil.removeChatFormat(checkSign.getLine(0)).equalsIgnoreCase("AllBanks Shop") && signIsRegistered(checkSign.getLocation())
				|| ChatUtil.removeChatFormat(checkSign.getLine(0)).equalsIgnoreCase("AllBanks") && signIsRegistered(checkSign.getLocation())){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Obtiene el tipo de letrero de AllBanks.<br> <br>
	 * <u>Importante:</u> <i>Este método no funciona para verificar letreros que pertenezcan
	 * a tiendas de AllBanks, en lugar de usar éste método se puede usar: </i> {@linkplain me.wirlie.allbanks.utils.ShopUtil#isShopSign(Sign) ShopUtil.isShopSign()}
	 * @param allbanksSign
	 * @return Devuelve el tipo de letrero si es que se ha podido resolver, o {@linkplain ABSignType#DEFAULT DEFAULT} si no se pudo resolver.
	 */
	public static ABSignType getABSignTypeBySign(Sign allbanksSign){
		String line_header = ChatUtil.removeChatFormat(allbanksSign.getLine(0));
		
		if(line_header.equalsIgnoreCase("AllBanks")){
			return ABSignType.getSignTypeByShortName(ChatUtil.removeChatFormat(allbanksSign.getLine(1)));
		}else{
			return ABSignType.DEFAULT;
		}
	}
	
	/**
	 * Este es un método que se usa para convertir información referente a los inventarios de VirtualChest
	 * al método nuevo basado en el encriptado Base64.
	 */
	public static void convertOldVirtualChestDataToNewDataMethod(){
		File dataFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "VirtualChestData");
		if(!dataFolder.exists()){ dataFolder.mkdirs(); return; }
		
		File[] files = dataFolder.listFiles();
		
		int totalUpdated = 0;
		
		for(File file : files){
			String fileName = file.getName();
			System.out.println(fileName);
			if(fileName.contains("-B64.yml") || fileName.contains("-Backup.yml")) continue;
			
			fileName = fileName.replace(".yml", "");
			
			File backupFile = new File(dataFolder + File.separator + fileName + "-Backup.yml");
			
			YamlConfiguration oldYaml = YamlConfiguration.loadConfiguration(file);
			
			File newFile = new File(dataFolder + File.separator + fileName + "-B64.yml");
			
			try {
				newFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			YamlConfiguration newYaml = YamlConfiguration.loadConfiguration(newFile);
			
			for(String mainKey : oldYaml.getKeys(false)){
				for(String itemKey : oldYaml.getConfigurationSection(mainKey).getKeys(false)){
					ItemStack item = oldYaml.getItemStack(mainKey + "." + itemKey);
					
					if(item != null){
						String base64 = ItemStackBase64.toBase64(item);
						newYaml.set(mainKey + "." + itemKey, base64);
					}
				}
			}
			
			try {
				newYaml.save(newFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			file.renameTo(backupFile);
			totalUpdated++;
		}
		
		if(totalUpdated > 0){
			AllBanks.getInstance().getLogger().info(totalUpdated + " files changed from AllBanks/VirtualChest/");
		}
	}
	
}
