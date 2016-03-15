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
package me.wirlie.allbanks.main;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

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
	
	public static void switchSignTo(Sign sign, BankType btype){
		if(!sign.getBlock().getType().equals(Material.SIGN) && !sign.getBlock().getType().equals(Material.WALL_SIGN))
			//Si el letrero ya no existe ignoramos, esto puede suceder ya que la función switchSignTo puede ser llamada 1 segundo después.
			return;
		
		boolean configurationRequired = false;
		
		sign.setLine(0, ChatColor.AQUA + "AllBanks");
		
		switch(btype){
			case ATM:
				sign.setLine(1, ChatColor.WHITE + BankType.ATM.getDisplay());
				configurationRequired = true;
				break;
			case BANK_BUY:
				sign.setLine(1, ChatColor.WHITE + BankType.BANK_BUY.getDisplay());
				configurationRequired = true;
				break;
			case BANK_LAND:
				//Este letrero solo aparecerá en la venta de un terreno.
				sign.getBlock().breakNaturally();
				return;
			case BANK_LOAN:
				sign.setLine(1, ChatColor.WHITE + BankType.BANK_LOAN.getDisplay());
				break;
			case BANK_MONEY:
				sign.setLine(1, ChatColor.WHITE + BankType.BANK_MONEY.getDisplay());
				break;
			case BANK_SELL:
				sign.setLine(1, ChatColor.WHITE + BankType.BANK_SELL.getDisplay());
				configurationRequired = true;
				break;
			case BANK_TIME:
				sign.setLine(1, ChatColor.WHITE + BankType.BANK_TIME.getDisplay());
				break;
			case BANK_USER:
				sign.setLine(1, ChatColor.WHITE + BankType.BANK_USER.getDisplay());
				configurationRequired = true;
				break;
			case BANK_XP:
				sign.setLine(1, ChatColor.WHITE + BankType.BANK_XP.getDisplay());
				break;
			case BANK_CHEST:
				sign.setLine(1, ChatColor.WHITE + BankType.BANK_CHEST.getDisplay());
				break;
			default:
				break;
		}
		
		if(configurationRequired)
			sign.setLine(2, String.valueOf(ChatColor.AQUA) + String.valueOf(StringsID.SIGN_NOT_CONFIGURED));
		else
			sign.setLine(2, String.valueOf(ChatColor.GREEN) + String.valueOf(StringsID.CLICK_TO_USE));
		
		sign.update();
	}
}
