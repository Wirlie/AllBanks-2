/**
 * 
 */
package me.wirlie.allbanks.listeners;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import me.wirlie.allbanks.main.Banks;
import me.wirlie.allbanks.main.StringsID;
import me.wirlie.allbanks.main.Translation;
import me.wirlie.allbanks.main.Banks.AllBanksAction;
import me.wirlie.allbanks.main.Banks.BankType;

/**
 * @author Wirlie
 * @since AnimalAttacks v1.0
 *
 */
public class SignChangeListener implements Listener {

	@EventHandler
	public void onSignChange(SignChangeEvent e){
		String[] lines = e.getLines();
		Player p = e.getPlayer();
		
		if(lines.length > 1){
			if(lines[0].equalsIgnoreCase("AllBanks") || lines[0].equalsIgnoreCase("[AllBanks]") || lines[0].equalsIgnoreCase("All Banks")){
				
				BankType btype = BankType.DEFAULT;
				
				if(lines[1].equalsIgnoreCase("BankLoan")){
					btype = BankType.BANK_LOAN;
				}else if(lines[1].equalsIgnoreCase("BankXP")){
					btype = BankType.BANK_XP;
				}else if(lines[1].equalsIgnoreCase("BankChest")){
					btype = BankType.BANK_CHEST;
				}else if(lines[1].equalsIgnoreCase("BankSell")){
					btype = BankType.BANK_SELL;
				}else if(lines[1].equalsIgnoreCase("BankBuy")){
					btype = BankType.BANK_BUY;
				}else if(lines[1].equalsIgnoreCase("BankUser")){
					btype = BankType.BANK_USER;
				}else if(lines[1].equalsIgnoreCase("BankTime")){
					btype = BankType.BANK_TIME;
				}else if(lines[1].equalsIgnoreCase("BankMoney")){
					btype = BankType.BANK_MONEY;
				}else if(lines[1].equalsIgnoreCase("BankLand")){
					btype = BankType.BANK_LAND;
				}else if(lines[1].equalsIgnoreCase("ATM")){
					btype = BankType.ATM;
				}
				
				//Checar permiso
				if(!Banks.playerHasPermissions(p, AllBanksAction.NEW_SIGN, btype)){
					Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					e.setCancelled(true);
					e.getBlock().breakNaturally();
					return;
				}
				
				//Bien, pasar y formatear letrero
				Banks.switchSignTo((Sign) e.getBlock().getState(), btype);
			}
		}else{
			if(lines.length > 0 && (lines[0].equalsIgnoreCase("AllBanks") || lines[0].equalsIgnoreCase("[AllBanks]") || lines[0].equalsIgnoreCase("All Banks"))){
				//No se especificó el segundo argumento...
				Translation.getAndSendMessage(p, StringsID.SIGN_MORE_ARGUMENTS_NEEDED, true);
			}
		}
	}
	
}
