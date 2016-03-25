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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.wirlie.allbanks.data.BankAccount;
import me.wirlie.allbanks.data.BankSession;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Banks {
	
	public static enum BankType{
		BANK_LOAN("Loan"),
		BANK_XP("XP"),
		BANK_TIME("Time"),
		BANK_MONEY("Money"),
		SHOP("Shop"),
		BANK_LAND("Land"),
		ATM("ATM"), 
		BANK_CHEST("Chest"), 
		DEFAULT("");
		
		String display;
		
		BankType(String display){
			this.display = display;
		}
		
		String getDisplay(){
			return display;
		}
		
		public static BankType getByString(String string){
			for(BankType b : BankType.values()){
				if(b.getDisplay().equalsIgnoreCase(string)){
					return b;
				}
			}
			
			return null;
		}
	}
	
	public static enum AllBanksAction{
		NEW_SIGN,
		DESTROY_SIGN,
		USE_SIGN;
	}
	
	public static boolean playerHasPermissions(Player p, AllBanksAction action, BankType btype){
		
		if(btype == null){
			return true;
		}
		
		switch(action){
		case NEW_SIGN:
			switch(btype){
			case ATM:
				return p.hasPermission("allbanks.sign.atm.new");
			case SHOP:
				return p.hasPermission("allbanks.sign.shop.new");
			case BANK_LAND:
				return p.hasPermission("allbanks.sign.land.new");
			case BANK_LOAN:
				return p.hasPermission("allbanks.sign.loan.new");
			case BANK_MONEY:
				return p.hasPermission("allbanks.sign.money.new");
			case BANK_TIME:
				return p.hasPermission("allbanks.sign.time.new");
			case BANK_XP:
				return p.hasPermission("allbanks.sign.xp.new");
			case BANK_CHEST:
				return p.hasPermission("allbanks.sign.chest.new");
			case DEFAULT:
				break;
			}
		case DESTROY_SIGN:
			switch(btype){
			case ATM:
				return p.hasPermission("allbanks.sign.atm.destroy");
			case SHOP:
				return p.hasPermission("allbanks.sign.shop.destroy");
			case BANK_LAND:
				return p.hasPermission("allbanks.sign.land.destroy");
			case BANK_LOAN:
				return p.hasPermission("allbanks.sign.loan.destroy");
			case BANK_MONEY:
				return p.hasPermission("allbanks.sign.money.destroy");
			case BANK_TIME:
				return p.hasPermission("allbanks.sign.time.destroy");
			case BANK_XP:
				return p.hasPermission("allbanks.sign.xp.destroy");
			case BANK_CHEST:
				return p.hasPermission("allbanks.sign.chest.destroy");
			case DEFAULT:
				break;
			}
			
			//TODO Cierto, faltan los permisos para cuando el banco se usa.
		case USE_SIGN:
			switch(btype){
			case ATM:
				return p.hasPermission("allbanks.sign.atm.use");
			case SHOP:
				return p.hasPermission("allbanks.sign.shop.use");
			case BANK_LAND:
				return p.hasPermission("allbanks.sign.land.use");
			case BANK_LOAN:
				return p.hasPermission("allbanks.sign.loan.use");
			case BANK_MONEY:
				return p.hasPermission("allbanks.sign.money.use");
			case BANK_TIME:
				return p.hasPermission("allbanks.sign.time.use");
			case BANK_XP:
				return p.hasPermission("allbanks.sign.xp.use");
			case BANK_CHEST:
				return p.hasPermission("allbanks.sign.chest.use");
			case DEFAULT:
				break;
			}
		default:
			break;
		
		}
		
		AllBanks.getInstance().getLogger().warning("Method playerHasPermission returned with false (default), Action: " + action + ", btype: " + btype);
		return false;
	}
	
	public static void switchSignToInitialState(Sign sign, BankType btype){
		if(!sign.getBlock().getType().equals(Material.SIGN) && !sign.getBlock().getType().equals(Material.WALL_SIGN))
			//Si el letrero ya no existe ignoramos, esto puede suceder ya que la función switchSignTo puede ser llamada 1 segundo después.
			return;
		
		switchSignToStep(btype, sign, -1, true);
		
	}
	
	public static int getNextStep(BankSession bs){
		int nextStep = bs.getStep() + 1;
		
		switch(bs.getBankType()){
		case ATM:
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case SHOP:
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
	 * @param btype
	 * @param sign
	 * @param step
	 */
	public static void switchSignToStep(BankType btype, Sign sign, int step, boolean playerMessages) {
		
		sign.setLine(0, ChatColor.AQUA + "AllBanks");
		sign.setLine(1, ChatColor.WHITE + btype.getDisplay());
		
		//No en todos los bancos y pasos se requiere esta variable BS.
		//Esta variable puede ser nula en ocasiones, nunca se debe descartar esto.
		BankSession bs = BankSession.getActiveSessionBySign(sign);
		BankAccount ba = null; //Nulo, inicialmente. Puede ser nulo si bs no está especificado.
		Player p = null; //Nulo, será nulo si bs no está especificado.
		
		if(bs != null){
			ba = BankAccount.Cache.get(bs.getPlayer().getUniqueId()); //Si bs (BankSession) no es nulo, se puede obtener ba (BankAccount).
			p = bs.getPlayer();
		}
		
		switch(btype){
		case ATM:
			switch(step){
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case SHOP:
			switch(step){
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_CHEST:
			switch(step){
			case 0:
				//Encontrar el siguiente cofre
				ba.BankChest.switchToNextChest();
				
				//Abrir interfaz de cofre
				HashMap<String, String> replaceMap = new HashMap<String, String>();
				replaceMap.put("%1%", String.valueOf(ba.BankChest.getCurrentChestCursor()));
				
				sign.setLine(2, ChatColor.GREEN + StringsID.BANKCHEST_CHEST_NUMBER.toString(replaceMap, false));
				sign.setLine(3, "");
				
				if(p != null && playerMessages){
					Translation.getAndSendMessage(p, StringsID.BANKCHEST_STEP0_INFO, true);
				}
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_LAND:
			switch(step){
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_LOAN:
			switch(step){
			case 0:
				BigDecimal maxBorrow = BigDecimal.ZERO;
				boolean overrideConfiguration = false;
				
				for(PermissionAttachmentInfo pinfo : p.getEffectivePermissions()){
					if(pinfo.getPermission().startsWith("allbanks.banks.bankloan.maxloan.")){
						try{
							maxBorrow = new BigDecimal(Double.parseDouble(pinfo.getPermission().replace("allbanks.banks.bankloan.maxloan.", ""))).subtract(ba.BankLoan.getLoan());
							overrideConfiguration = true;
						}catch(NumberFormatException e2){
							overrideConfiguration = false;
						}
					}
				}
				
				if(!overrideConfiguration)
					maxBorrow = new BigDecimal(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.max-loan")).subtract(ba.BankLoan.getLoan());
				
				sign.setLine(2, ChatColor.YELLOW + StringsID.ASK.toString(false));
				sign.setLine(3, ChatColor.GREEN + AllBanks.getEconomy().format(maxBorrow.doubleValue()));
				
				//Mensaje al jugador
				if(p != null && playerMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", String.valueOf(AllBanks.getInstance().getConfig().getInt("banks.bank-loan.interest", 0)));
					replaceMap.put("%2%", String.valueOf(Util.ConfigUtil.convertTimeValueToSeconds(AllBanks.getInstance().getConfig().getString("banks.bank-loan.collect-interest-every"))));
					Translation.getAndSendMessage(p, StringsID.BANKLOAN_STEP0_INFO, replaceMap, true);
				}
				break;
			case 1:
				sign.setLine(2, ChatColor.YELLOW + StringsID.PAY.toString(false));
				sign.setLine(3, ChatColor.YELLOW + AllBanks.getEconomy().format(ba.BankLoan.getLoan().doubleValue()));
				
				//Mensaje al jugador
				if(p != null && playerMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", AllBanks.getEconomy().format(ba.BankLoan.getLoan().doubleValue()));
					Translation.getAndSendMessage(p, StringsID.BANKLOAN_STEP1_INFO, replaceMap, true);
				}
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_MONEY:
			switch(step){
			case 0:
				//Depositar
				BigDecimal moneyInBank = ba.BankMoney.getMoney();
				
				sign.setLine(2, ChatColor.YELLOW + StringsID.DEPOSIT_MONEY.toString(false));
				sign.setLine(3, ChatColor.GREEN + AllBanks.getEconomy().format(moneyInBank.doubleValue()));
				
				//Mensaje al jugador
				if(p != null && playerMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", AllBanks.getEconomy().format(ba.BankMoney.getMoney().doubleValue()));
					Translation.getAndSendMessage(p, StringsID.BANKMONEY_STEP0_INFO, replaceMap, true);
				}
				break;
			case 1:
				//Retirar
				BigDecimal moneyInBank2 = ba.BankMoney.getMoney();
				
				sign.setLine(2, ChatColor.YELLOW + StringsID.WITHDRAW_MONEY.toString(false));
				sign.setLine(3, ChatColor.GREEN + AllBanks.getEconomy().format(moneyInBank2.doubleValue()));
				
				//Mensaje al jugador
				if(p != null && playerMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", AllBanks.getEconomy().format(ba.BankMoney.getMoney().doubleValue()));
					Translation.getAndSendMessage(p, StringsID.BANKMONEY_STEP1_INFO, replaceMap, true);
				}
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_TIME:
			switch(step){
			case 0:
				//Retirar tiempo (este banco solo tiene este estatus)
				sign.setLine(2, ChatColor.YELLOW + StringsID.CHANGE_TIME.toString(false));
				sign.setLine(3, ChatColor.GREEN + String.valueOf(ba.BankTime.getTime()));
				
				//Mensaje al jugador
				if(p != null && playerMessages){
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					Translation.getAndSendMessage(p, StringsID.BANKTIME_STEP0_INFO, replaceMap, true);
				}
				
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_XP:
			switch(step){
			case 0:
				//depositar xp
				sign.setLine(2, ChatColor.YELLOW + StringsID.DEPOSIT_XP.toString(false));
				sign.setLine(3, ChatColor.GREEN + String.valueOf(ba.BankXP.getRawXP()) + " (" + ba.BankXP.getLvlForRawXP() + " lvl)");
				
				//Mensaje al jugador
				if(p != null && playerMessages){
					Translation.getAndSendMessage(p, StringsID.BANKXP_STEP0_INFO, true);
				}
				break;
			case 1:
				//retirar xp
				sign.setLine(2, ChatColor.YELLOW + StringsID.WITHDRAW_XP.toString(false));
				sign.setLine(3, ChatColor.GREEN + String.valueOf(ba.BankXP.getRawXP()) + " (" + ba.BankXP.getLvlForRawXP() + " lvl)");
				
				//Mensaje al jugador
				if(p != null && playerMessages){
					Translation.getAndSendMessage(p, StringsID.BANKXP_STEP1_INFO, true);
				}
				break;
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case DEFAULT:
			break;
		
		}
		
		sign.update();
	}
	
	public static void registerSign(Location signLoc, Player owner){
		try{
			Statement stm = AllBanks.getDBC().createStatement();
			stm.executeUpdate("INSERT INTO signs (location, owner) VALUES ('" + Util.StrLocUtil.convertLocationToString(signLoc, true) + "', '" + owner.getName() + "')");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static boolean signIsRegistered(Location signLoc){
		try{
			Statement stm = AllBanks.getDBC().createStatement();
			ResultSet res = stm.executeQuery("SELECT * FROM signs WHERE location = '" + Util.StrLocUtil.convertLocationToString(signLoc, true) + "'");
			return res.next();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void removeSign(Location signLoc){
		try{
			Statement stm = AllBanks.getDBC().createStatement();
			stm.executeUpdate("DELETE FROM signs WHERE location = '" + Util.StrLocUtil.convertLocationToString(signLoc, true) + "'");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * @param p
	 * @param currentChestCursor
	 */
	public static void openVirtualChest(Player p, int currentChestCursor) {
		if(p == null) throw new IllegalArgumentException("Player can not be null!!");
		if(currentChestCursor <= 0) throw new IllegalArgumentException("invalid ChestCursor");
		
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		replaceMap.put("%1%", String.valueOf(currentChestCursor));
		
		Inventory inv = Bukkit.getServer().createInventory(null, 9 * 6, "ab:virtualchest:" + currentChestCursor);
		
		Iterator<Entry<Integer, ItemStack>> it = getVirtualChestContents(p.getName(), currentChestCursor).entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, ItemStack> entry = it.next();
			inv.setItem(entry.getKey(), entry.getValue());
		}
		
		//StringsID.BANKCHEST_VIRTUAL_INVENTORY.toString(replaceMap, false)
		p.openInventory(inv);
	}
	
	public static HashMap<Integer, ItemStack> getVirtualChestContents(String owner, int virtualChest){
		File virtualDataFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "VirtualChestData");
		if(!virtualDataFolder.exists()) virtualDataFolder.mkdirs();
		
		File virtualChestFile = new File(virtualDataFolder + File.separator + owner + ".yml");
		if(!virtualChestFile.exists())
			try {
				virtualChestFile.createNewFile();
				return new HashMap<Integer, ItemStack>();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(virtualChestFile);
		HashMap<Integer, ItemStack> returnList = new HashMap<Integer, ItemStack>();
		
		for(String key : yaml.getConfigurationSection(String.valueOf(virtualChest)).getKeys(false)){
			ItemStack getItemStack = yaml.getItemStack(String.valueOf(virtualChest) + "." + key, null);
			if(getItemStack != null){
				returnList.put(Integer.parseInt(key), getItemStack);
			}
		}
		
		return returnList;
	}
	
	public static void setVirtualChestContents(String owner, int chestNumber, HashMap<Integer, ItemStack> contents){
		File virtualDataFolder = new File(AllBanks.getInstance().getDataFolder() + File.separator + "VirtualChestData");
		if(!virtualDataFolder.exists()) virtualDataFolder.mkdirs();
		
		File virtualChestFile = new File(virtualDataFolder + File.separator + owner + ".yml");
		if(!virtualChestFile.exists())
			try {
				virtualChestFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(virtualChestFile);
		
		Iterator<Entry<Integer, ItemStack>> it = contents.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, ItemStack> entry = it.next();
			yaml.set(String.valueOf(chestNumber) + "." + String.valueOf(entry.getKey()), entry.getValue());
		}
		
		try {
			yaml.save(virtualChestFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
