/**
 * 
 */
package me.wirlie.allbanks.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.main.AllBanks;
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
	public void onSignChange(final SignChangeEvent e){
		String[] lines = e.getLines();
		Player p = e.getPlayer();
		
		if(lines[0].equalsIgnoreCase("AllBanks") || lines[0].equalsIgnoreCase("[AllBanks]") || lines[0].equalsIgnoreCase("All Banks")){
			
			BankType btype = BankType.DEFAULT;
			
			if(!e.getBlock().getType().equals(Material.WALL_SIGN)){
				Translation.getAndSendMessage(p, StringsID.ONLY_WALL_SIGN, true);
				if(p.getGameMode().equals(GameMode.CREATIVE))
					e.getBlock().setType(Material.AIR);
				else
					e.getBlock().breakNaturally();
			}
			
			if(lines[1].equalsIgnoreCase("BankLoan")){
				btype = BankType.BANK_LOAN;
			}else if(lines[1].equalsIgnoreCase("BankXP") || lines[1].equalsIgnoreCase("XP")){
				btype = BankType.BANK_XP;
			}else if(lines[1].equalsIgnoreCase("BankChest") || lines[1].equalsIgnoreCase("Chest")){
				btype = BankType.BANK_CHEST;
			}else if(lines[1].equalsIgnoreCase("BankSell") || lines[1].equalsIgnoreCase("Sell")){
				btype = BankType.BANK_SELL;
			}else if(lines[1].equalsIgnoreCase("BankBuy") || lines[1].equalsIgnoreCase("Buy")){
				btype = BankType.BANK_BUY;
			}else if(lines[1].equalsIgnoreCase("BankUser") || lines[1].equalsIgnoreCase("User")){
				btype = BankType.BANK_USER;
			}else if(lines[1].equalsIgnoreCase("BankTime") || lines[1].equalsIgnoreCase("Time")){
				btype = BankType.BANK_TIME;
			}else if(lines[1].equalsIgnoreCase("BankMoney") || lines[1].equalsIgnoreCase("Money")){
				btype = BankType.BANK_MONEY;
			}else if(lines[1].equalsIgnoreCase("BankLand") || lines[1].equalsIgnoreCase("Land")){
				btype = BankType.BANK_LAND;
			}else if(lines[1].equalsIgnoreCase("ATM")){
				btype = BankType.ATM;
			}
			
			if(btype.equals(BankType.DEFAULT)){
				//No se especificó el segundo argumento...
				Translation.getAndSendMessage(p, StringsID.SIGN_MORE_ARGUMENTS_NEEDED, true);
				if(p.getGameMode().equals(GameMode.CREATIVE))
					e.getBlock().setType(Material.AIR);
				else
					e.getBlock().breakNaturally();
				return;
			}
			
			//Checar permiso
			if(!Banks.playerHasPermissions(p, AllBanksAction.NEW_SIGN, btype)){
				Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				e.setCancelled(true);
				e.getBlock().breakNaturally();
				return;
			}
			
			final BankType btypefinal = btype;
			
			//Bien, pasar y formatear letrero
			new BukkitRunnable(){

				public void run() {
					Banks.switchSignTo((Sign) e.getBlock().getState(), btypefinal);
				}

			}.runTaskLater(AllBanks.getInstance(), 10);
		}
	}
	
}
