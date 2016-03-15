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

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.main.Banks;
import me.wirlie.allbanks.main.Banks.BankType;

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
	
	public static BankSession getActiveSessionBySign(Sign sign){
		
		for(BankSession bs : activeSessions.values()){
			if(bs.getSign().equals(sign)){
				return bs;
			}
		}
		
		return null;
	}
	
	public static void startSession(Player p, BankSession bs){
		startSession(p.getUniqueId(), bs);
	}
	
	public static void startSession(UUID uuid, BankSession bs){
		activeSessions.put(uuid, bs);
	}
	
	public BankSession getSession(Player p){
		return getSession(p.getUniqueId());
	}
	
	public BankSession getSession(UUID uuid){
		return activeSessions.get(uuid);
	}
	
	public static void closeSession(Player p){
		closeSession(p.getUniqueId());
	}
	
	public static void closeSession(UUID uuid){
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
	
	public void updateStepAndSwitchSign(int step){
		this.step = step;
		updateSession(player.getUniqueId(), this);
		
		Banks.switchSignToStep(btype, sign, step);
	}
	
	public void updateStepAndSwitchSign(){
		updateStepAndSwitchSign(Banks.getNextStep(this));
	}
	
}
