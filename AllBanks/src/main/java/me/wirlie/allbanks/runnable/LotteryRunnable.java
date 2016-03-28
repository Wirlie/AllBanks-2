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
package me.wirlie.allbanks.runnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.AllBanksLogger;
import me.wirlie.allbanks.Util;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class LotteryRunnable {
	static int runEvery = 0;
	static boolean initialized = false;
	
	public static void initializeLottery(){
		if(initialized) return;
		
		runEvery = getLotteryTime();
		
		AllBanksLogger.getLogger().info("[Lottery] Initializing lottery...");
		
		if(runEvery <= 0){
			//inválido
			AllBanks.getInstance().getLogger().severe("[Lottery] Can not start runnable!");
			AllBanks.getInstance().getLogger().severe("[Lottery] Invalid configuration value: " + runEvery);
			AllBanks.getInstance().getLogger().severe("[Lottery] on: lottery.get-winer-every");
			
			AllBanksLogger.getLogger().severe("[Lottery] Can not start runnable!");
			AllBanksLogger.getLogger().severe("[Lottery] Invalid configuration value: " + runEvery);
			AllBanksLogger.getLogger().severe("[Lottery] on: lottery.get-winer-every");
			
			initialized = true;
			return;
		}
		
		AllBanksLogger.getLogger().info("[Lottery] Initialized!");
		
		initialized = true;
	}
	
	public static int getLotteryTime(){
		String readCfg = AllBanks.getInstance().getConfig().getString("lottery.get-winer-every", "24 hours");
	
		int seconds = Util.ConfigUtil.convertTimeValueToSeconds(readCfg);
		
		if(seconds <= 0){
			//Invalido
			return -1;
		}
		
		return seconds;
	}
	
	public LotteryRunnable(){
		initializeLottery();
	}
}
