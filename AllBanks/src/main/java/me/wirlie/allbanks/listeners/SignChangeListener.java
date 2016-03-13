/**
 * 
 */
package me.wirlie.allbanks.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import me.wirlie.allbanks.main.StringsID;
import me.wirlie.allbanks.main.Translation;

/**
 * @author Wirlie
 * @since AnimalAttacks v1.0
 *
 */
public class SignChangeListener implements Listener {

	@EventHandler
	public void onSignChange(SignChangeEvent e){
		String[] lines = e.getLines();
		Player ep = e.getPlayer();
		try___mssad
		if(lines.length > 1){
			if(lines[0].equalsIgnoreCase("AllBanks") || lines[0].equalsIgnoreCase("[AllBanks]") || lines[0].equalsIgnoreCase("All Banks")){
				if(lines[1].equalsIgnoreCase("BankLoan")){
					
				}else if(lines[1].equalsIgnoreCase("BankXP")){
					
				}else if(lines[1].equalsIgnoreCase("BankChest")){
					
				}else if(lines[1].equalsIgnoreCase("BankSell")){
					
				}else if(lines[1].equalsIgnoreCase("BankBuy")){
					
				}else if(lines[1].equalsIgnoreCase("BankUser")){
					
				}else if(lines[1].equalsIgnoreCase("BankTime")){
					
				}else if(lines[1].equalsIgnoreCase("BankMoney")){
					
				}else if(lines[1].equalsIgnoreCase("BankLand")){
					
				}else if(lines[1].equalsIgnoreCase("ATM")){
					
				}else if(lines[1].equalsIgnoreCase("Portable")){
					
				}
			}
		}else{
			if(lines.length > 0 && (lines[0].equalsIgnoreCase("AllBanks") || lines[0].equalsIgnoreCase("[AllBanks]") || lines[0].equalsIgnoreCase("All Banks"))){
				//No se especificó el segundo argumento...
				Translation.getAndSendMessage(ep, StringsID.SIGN_MORE_ARGUMENTS_NEEDED, true);
			}
		}
	}
	
}
