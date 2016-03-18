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
package me.wirlie.allbanks.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Banks.BankType;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class BankSession {
	
	/*
	 * Funciones para las sesiones activas. (activeSessions) 
	 */
	
	private static HashMap<UUID, BankSession> activeSessions = new HashMap<UUID, BankSession>();
	
	public static Collection<BankSession> getAllActiveSessions(){
		return activeSessions.values();
	}
	
	public static BankSession getActiveSessionBySign(Sign sign){
		
		for(BankSession bs : activeSessions.values()){
			if(bs.getSign().equals(sign)){
				return bs;
			}
		}
		
		return null;
	}
	
	public static BankSession startSession(Player p, BankSession bs){
		startSession(p.getUniqueId(), bs);
		
		//Para fines prácticos, retornamos el valor bs.
		return bs;
	}
	
	public static BankSession startSession(UUID uuid, BankSession bs){
		activeSessions.put(uuid, bs);
		
		//Para fines prácticos, retornamos el valor bs.
		return bs;
	}
	
	public static BankSession getSession(Player p){
		return getSession(p.getUniqueId());
	}
	
	public static BankSession getSession(UUID uuid){
		return activeSessions.get(uuid);
	}
	
	public static void closeSession(Player p){
		closeSession(p.getUniqueId());
	}
	
	public static void closeSession(UUID uuid){
		//notificar
		Translation.getAndSendMessage(Bukkit.getPlayer(uuid), StringsID.SESSION_CLOSED, true);

		//actualizar letrero
		BankSession bs = activeSessions.get(uuid);
		if(bs.getSign().getBlock().getType().equals(Material.WALL_SIGN)) bs.updateToInitialState();
		
		activeSessions.remove(uuid);
	}

	public static boolean checkSession(Player p){
		return checkSession(p.getUniqueId());
	}
	
	public static boolean checkSession(UUID uuid){
		return activeSessions.containsKey(uuid);
	}
	
	public static void updateSession(Player p, BankSession bs){
		startSession(p, bs);
	}
	
	public static void updateSession(UUID uuid, BankSession bs){
		startSession(uuid, bs);
	}
	
	/*
	 * Datos usados para BankSession.
	 */
	
	Player player;
	BankType btype;
	int step;
	Sign sign;
	
	public BankSession(Player player, Sign sign, BankType btype, int step){
		this.btype = btype;
		this.step = step;
		this.sign = sign;
		this.player = player;
	}
	
	public BankType getBankType(){
		return btype;
	}
	
	public int getStep(){
		return step;
	}
	
	public Sign getSign(){
		return sign;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public void updateStepAndSwitchSign(int step, boolean ignoreUpdate){
		if(!ignoreUpdate){
			this.step = step;
			updateSession(player.getUniqueId(), this);
		}
		
		Banks.switchSignToStep(btype, sign, step);
	}
	
	public void updateStepAndSwitchSign(int step){
		updateStepAndSwitchSign(step, false);
	}
	
	public void updateStepAndSwitchSign(){
		updateStepAndSwitchSign(Banks.getNextStep(this), false);
	}
	
	public void closeSession(){
		closeSession(player);
	}
	
	private void updateToInitialState() {
		updateStepAndSwitchSign(-1, true);
	}
}
