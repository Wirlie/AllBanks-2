 /**
 * 
 */
package me.wirlie.allbanks.main;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Wirlie
 * @since AnimalAttacks v1.0
 *
 */
public class Banks {
	
	public static enum BankType{
		BANK_LOAN,
		BANK_XP,
		BANK_TIME,
		BANK_MONEY,
		BANK_SELL,
		BANK_BUY,
		BANK_USER,
		BANK_LAND,
		ATM, 
		BANK_CHEST, DEFAULT;
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
				sign.setLine(1, ChatColor.WHITE + "ATM");
				configurationRequired = true;
				break;
			case BANK_BUY:
				sign.setLine(1, ChatColor.WHITE + "Buy");
				configurationRequired = true;
				break;
			case BANK_LAND:
				//Este letrero solo aparecerá en la venta de un terreno.
				break;
			case BANK_LOAN:
				sign.setLine(1, ChatColor.WHITE + "Loan");
				break;
			case BANK_MONEY:
				sign.setLine(1, ChatColor.WHITE + "Money");
				break;
			case BANK_SELL:
				sign.setLine(1, ChatColor.WHITE + "Sell");
				configurationRequired = true;
				break;
			case BANK_TIME:
				sign.setLine(1, ChatColor.WHITE + "Time");
				break;
			case BANK_USER:
				sign.setLine(1, ChatColor.WHITE + "User");
				configurationRequired = true;
				break;
			case BANK_XP:
				sign.setLine(1, ChatColor.WHITE + "XP");
				break;
			case BANK_CHEST:
				sign.setLine(1, ChatColor.WHITE + "Chest");
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
