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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

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
		BANK_SELL("Sell"),
		BANK_BUY("Buy"),
		BANK_USER("User"),
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
			case BANK_BUY:
				return p.hasPermission("allbanks.sign.buy.new");
			case BANK_LAND:
				return p.hasPermission("allbanks.sign.land.new");
			case BANK_LOAN:
				return p.hasPermission("allbanks.sign.loan.new");
			case BANK_MONEY:
				return p.hasPermission("allbanks.sign.money.new");
			case BANK_SELL:
				return p.hasPermission("allbanks.sign.sell.new");
			case BANK_TIME:
				return p.hasPermission("allbanks.sign.time.new");
			case BANK_USER:
				return p.hasPermission("allbanks.sign.user.new");
			case BANK_XP:
				return p.hasPermission("allbanks.sign.xp.new");
			default:
				return false;
			}
		case DESTROY_SIGN:
			switch(btype){
			case ATM:
				return p.hasPermission("allbanks.sign.atm.destroy");
			case BANK_BUY:
				return p.hasPermission("allbanks.sign.buy.destroy");
			case BANK_LAND:
				return p.hasPermission("allbanks.sign.land.destroy");
			case BANK_LOAN:
				return p.hasPermission("allbanks.sign.loan.destroy");
			case BANK_MONEY:
				return p.hasPermission("allbanks.sign.money.destroy");
			case BANK_SELL:
				return p.hasPermission("allbanks.sign.sell.destroy");
			case BANK_TIME:
				return p.hasPermission("allbanks.sign.time.destroy");
			case BANK_USER:
				return p.hasPermission("allbanks.sign.user.destroy");
			case BANK_XP:
				return p.hasPermission("allbanks.sign.xp.destroy");
			default:
				return false;
			}
		case USE_SIGN:
			switch(btype){
			case ATM:
				return p.hasPermission("allbanks.sign.atm.use");
			case BANK_BUY:
				return p.hasPermission("allbanks.sign.buy.use");
			case BANK_LAND:
				return p.hasPermission("allbanks.sign.land.use");
			case BANK_LOAN:
				return p.hasPermission("allbanks.sign.loan.use");
			case BANK_MONEY:
				return p.hasPermission("allbanks.sign.money.use");
			case BANK_SELL:
				return p.hasPermission("allbanks.sign.sell.use");
			case BANK_TIME:
				return p.hasPermission("allbanks.sign.time.use");
			case BANK_USER:
				return p.hasPermission("allbanks.sign.user.use");
			case BANK_XP:
				return p.hasPermission("allbanks.sign.xp.use");
			default:
				return false;
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
		
		switchSignToStep(btype, sign, -1);
		
	}
	
	public static int getNextStep(BankSession bs){
		int nextStep = bs.getStep() + 1;
		
		switch(bs.getBankType()){
		case ATM:
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case BANK_BUY:
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case BANK_CHEST:
			if(nextStep > 2){
				nextStep = 0;
			}
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
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case BANK_SELL:
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case BANK_TIME:
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case BANK_USER:
			if(nextStep > 2){
				nextStep = 0;
			}
			break;
		case BANK_XP:
			if(nextStep > 2){
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
	public static void switchSignToStep(BankType btype, Sign sign, int step) {
		
		sign.setLine(0, ChatColor.AQUA + "AllBanks");
		sign.setLine(1, ChatColor.WHITE + btype.getDisplay());
		
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
		case BANK_BUY:
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
				sign.setLine(2, ChatColor.YELLOW + StringsID.ASK.toString(false));
				sign.setLine(3, ChatColor.GREEN + AllBanks.getEconomy().format(BankAccount.Cache.get(sign).BankLoan_getLoan()));
				break;
			case 1:
				sign.setLine(2, ChatColor.YELLOW + StringsID.PAY.toString(false));
				sign.setLine(3, ChatColor.YELLOW + AllBanks.getEconomy().format(BankAccount.Cache.get(sign).BankLoan_getLoan()));
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
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_SELL:
			switch(step){
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_TIME:
			switch(step){
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_USER:
			switch(step){
			default:
				//El estado default es el estado cuando el letrero NO está en uso (establecer "step" con -1 logra este resultado)
				sign.setLine(2, ChatColor.GREEN + StringsID.CLICK_TO_USE.toString(false));
				sign.setLine(3, "");
				break;
			}
			break;
		case BANK_XP:
			switch(step){
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
	
}
