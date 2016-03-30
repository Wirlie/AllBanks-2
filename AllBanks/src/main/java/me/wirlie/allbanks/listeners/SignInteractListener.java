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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.wirlie.allbanks.Util;
import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Banks.AllBanksAction;
import me.wirlie.allbanks.Banks.BankType;
import me.wirlie.allbanks.Util.DatabaseUtil;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.data.BankSession;
import me.wirlie.allbanks.logger.AllBanksLogger;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class SignInteractListener implements Listener {
	
	public SignInteractListener(){
		AllBanksLogger.info("SignInteractListener");
	}
	
	@EventHandler
	public void onPlayerInteractWithAllBanksSign(PlayerInteractEvent e){
		
		if(e.getClickedBlock() == null) return;
		
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		
		if(b.getType().equals(Material.WALL_SIGN)){
			
			Sign s = (Sign) b.getState();
			
			if(Util.ChatFormatUtil.removeChatFormat(s.getLine(0)).equalsIgnoreCase("AllBanks")){
				
				//Se puede tratar de un letrero AllBanks.
				String btypeStr = Util.ChatFormatUtil.removeChatFormat(s.getLine(1));
				BankType btype = BankType.getByString(btypeStr);
				
				if(btype != null){
					
					BankSession bs;
					
					//Bien ya sabemos de que banco se trata.
					
					//Seguridad: Comprobar si el banco está registrado en AllBanks
					if(!Banks.signIsRegistered(s.getLocation())){
						if(DatabaseUtil.databaseIsLocked()){
							DatabaseUtil.sendDatabaseLockedMessage(p);
						}else{
							Translation.getAndSendMessage(p, StringsID.BANK_NOT_REGISTERED_ON_ALLBANKS, true);
							AllBanksLogger.warning("SECURITY: Player " + p.getName() + " (" + p.getDisplayName() + ") has tried to use a not registered bank at location (w:" + s.getLocation().getWorld().getName() + ", x:" + s.getLocation().getX() + ", y:" + s.getLocation().getY() + ", z:" + s.getLocation().getZ() + ")");
						}
						
						return;
					}
					
					//Comprobar si el banco es usado por otro jugador
					BankSession bs2 = BankSession.getActiveSessionBySign(s);
					if(bs2 != null){
						if(!bs2.getPlayer().equals(p)){
							Translation.getAndSendMessage(p, StringsID.BANK_USED_WITH_ANOTHER_PLAYER, true);
							return;
						}
						
					}
					
					//Comprobar si tiene permisos para usar el letrero
					if(!Banks.playerHasPermissions(p, AllBanksAction.USE_SIGN, btype)){
						Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						AllBanksLogger.warning("Player " + p.getName() + " (" + p.getDisplayName() + ") has tried to use a bank sign. (Deny cause: permissions)(Location: world:" + s.getLocation().getWorld().getName() + ", x:" + s.getLocation().getX() + ", y:" + s.getLocation().getY() + ", z:" + s.getLocation().getZ() + ").");
						return;
					}
					
					//Comprobar si el usuario está en sesión
					if(BankSession.checkSession(p)){
						//¿Ya está usando otro banco?
						bs = BankSession.getSession(p);
						if(bs != null && !bs.getSign().equals(s)){
							//Aunque esté intentando usar otro banco, esto demuestra que no está ausente.
							bs.updateLastUse();
							
							//Esta intentando usar otro banco.
							Translation.getAndSendMessage(p, StringsID.ALREADY_USING_ANOTHER_BANK, true);
							return;
						}else if(bs == null){
							//Nulo ???
							//#TG-ERR-1
							AllBanksLogger.warning("An unknown error had happens... (bs == null). SingInteractListener [TG-ERR-1]");
							return;
						}
						
						//Actualizar ultimo uso, para evitar un cierre automático por falta de actividad
						bs.updateLastUse();
						
						//Bien, cambiar paso
						bs.updateStepAndSwitchSign();
					}else{
						//Iniciar nueva sesión
						AllBanksLogger.info("BANK-INTERACT: Player " + p.getName() + " (" + p.getDisplayName() + ") has used a bank (type: " + btype.toString() + ") (Location: world:" + s.getLocation().getWorld().getName() + ", x:" + s.getLocation().getX() + ", y:" + s.getLocation().getY() + ", z:" + s.getLocation().getZ() + ").");
						bs = BankSession.startSession(p, new BankSession(p, (Sign) b.getState(), btype, 0));
						
						//Bien, establecer el paso en 0 ya que si no se establece en 0 el paso actualizado sería 1.
						bs.updateStepAndSwitchSign(0);
					}
				}
			}
		}
		
	}
	
}
