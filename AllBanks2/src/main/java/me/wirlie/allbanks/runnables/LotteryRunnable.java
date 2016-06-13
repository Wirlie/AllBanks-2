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
package me.wirlie.allbanks.runnables;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.utils.AllBanksLogger;
import me.wirlie.allbanks.utils.ConfigurationUtil;

/**
 * Runnable para conseguir un ganador en la lotería.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class LotteryRunnable extends BukkitRunnable {
	/** Ejecutar comprobación cada X tiempo */
	public static int runEvery = 0;
	/** Saber si esta clase ya se ha inicializado */
	public static boolean initialized = false;
	/** Task del runnable de esta clase */
	public static BukkitTask runnable = null;
	/** Saber si la lotería está activada */
	public static boolean enable = false;
	/** Instancia de esta clase */
	public static LotteryRunnable instance = null;
	/** Carpeta de los tickets */
	public static File dirTickets = new File(AllBanks.getInstance().getDataFolder() + File.separator + "lot-tickets");
	/** Archivo de la lotería */
	public static File lotteryFile = new File(AllBanks.getInstance().getDataFolder() + File.separator + "lottery.yml");
	
	/**
	 * El constructor de la lotería es privado. 
	 * Para iniciar el Runnable es necesario usar el método<br>
	 * {@link #startRunnable()}
	 */
	private LotteryRunnable(){
		instance = this;
		initializeLottery();
	}
	
	/**
	 * Inicializar lotería.
	 */
	public static void initializeLottery(){
		if(initialized) return;
		
		runEvery = getLotteryTime();
		
		AllBanksLogger.info("[Lottery] Initializing lottery...");
		
		if(runEvery <= 0){
			//inválido
			AllBanksLogger.severe("&7[&fLottery&7] &cCan not start runnable!", true);
			AllBanksLogger.severe("&7[&fLottery&7] &cInvalid configuration value: " + runEvery, true);
			AllBanksLogger.severe("&7[&fLottery&7] &con: lottery.get-winer-every", true);
			
			initialized = true;
			return;
		}
		
		enable = AllBanks.getInstance().getConfig().getBoolean("lottery.enable", true);
		
		AllBanksLogger.info("[Lottery] Initialized!");
		
		initialized = true;
	}
	
	/**
	 * Detener runnable.
	 * @throws Exception
	 */
	public static void stopRunnable() throws Exception{
		
		AllBanksLogger.info("[Lottery] Trying to stop BukkitTask (Runnable)...");
		
		if(!initialized){
			AllBanksLogger.severe("[Lottery] Can not stop LotteryRunnable! Lottery is not initialized...");
			throw new Exception("Can not stop LotteryRunnable! Lottery is not initialized...");
		}
		
		if(runnable == null){
			AllBanksLogger.info("[Lottery] Error: 'runnable' is null, skip.");
			return;
		}
		
		runnable.cancel();
		
		runnable = null;
		
		AllBanksLogger.info("[Lottery] Success: BukkitTask cancelled.");
	}
	
	/**
	 * Iniciar runnable.
	 * @throws Exception
	 */
	public static void startRunnable() throws Exception{
		
		AllBanksLogger.info("[Lottery] Trying to start BukkitTask (Runnable)...");
		
		if(runEvery <= 0){
			AllBanksLogger.severe("[Lottery] Error: Can not start LotteryRunnable! 'runEvery' is not more than 0...");
			throw new IllegalArgumentException("Can not start LotteryRunnable! 'runEvery' is not more than 0...");
		}
		
		if(!initialized){
			AllBanksLogger.severe("[Lottery] Can not start LotteryRunnable! Lottery is not initialized...");
			throw new Exception("Can not start LotteryRunnable! Lottery is not initialized...");
		}
		
		if(runnable != null){
			AllBanksLogger.info("[Lottery] Error: 'runnable' not null, skip");
			return;
		}
		
		if(!enable){
			AllBanksLogger.info("[Lottery] Skip: Lottery is disabled.");
			return;
		}
		
		if(!lotteryFile.exists()){
			lotteryFile.createNewFile();
		}
		
		YamlConfiguration lotteryYaml = YamlConfiguration.loadConfiguration(lotteryFile);
		long lastExec = lotteryYaml.getLong("last-execution", -1);
		
		if(lastExec < 0){
			lastExec = new Date().getTime();
			lotteryYaml.set("last-execution", lastExec);
			lotteryYaml.save(lotteryFile);
		}
		
		long currentTime = new Date().getTime();
		long difTime = currentTime - lastExec;
		long remainingTime = runEvery - (difTime / 1000);
		
		runnable = new LotteryRunnable().runTaskTimer(AllBanks.getInstance(), remainingTime * 20, runEvery * 20);
		
		AllBanksLogger.info("[Lottery] Success: New BukkitTask created (TaskId: " + runnable.getTaskId() + ", delay: " + remainingTime + " seconds, runEvery: " + runEvery + " seconds)");
	}
	
	/**
	 * Obtener el tiempo total de la lotería para ejecutarse cada X segundos.
	 * @return tiempo de ejecución en segundos.
	 */
	public static int getLotteryTime(){
		String readCfg = AllBanks.getInstance().getConfig().getString("lottery.get-winer-every", "24 hours");
		
		int seconds = ConfigurationUtil.convertTimeValueToSeconds(readCfg);

		if(seconds <= 0){
			//Invalido
			return -1;
		}
		
		return seconds;
	}

	public synchronized void run() {
		//Ejecutar
		AllBanksLogger.debug("[Lottery] Runnable executed! Reading files...");

		if(!dirTickets.exists()) 
			dirTickets.mkdirs();
		
		int entries = 0;
		HashMap<Integer, String> tickets = new HashMap<Integer, String>();
		
		for(File f : dirTickets.listFiles()){
			entries++;
			
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
			tickets.put(entries, yaml.getString("owner"));
			
			f.delete();
		}

		//Sin tickets
		if(entries == 0){
			YamlConfiguration lotteryYaml = YamlConfiguration.loadConfiguration(lotteryFile);
			lotteryYaml.set("last-execution", new Date().getTime());
			try {
				lotteryYaml.save(lotteryFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		//Obtener un ticker aleatorio
		AllBanksLogger.info("[Lottery] " + (entries) + " entries readed!");
		AllBanksLogger.info("[Lottery] Getting winner...");

		int randTicketId = (int) Math.floor(entries * Math.random()) + 1;
		
		Player p = Bukkit.getPlayer(tickets.get(randTicketId));
		
		if(p == null){
			AllBanksLogger.warning("[Lottery] Variable Player is null! Probably the player does not exists.");
			return;
		}
		
		AllBanksLogger.info("[Lottery] Ticket win: " + randTicketId);
		AllBanksLogger.info("[Lottery] Ticket Owner: " + tickets.get(randTicketId));
		
		if(AllBanks.getInstance().getConfig().getBoolean("lottery.broadcast-message", true)){
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			replaceMap.put("%1%", tickets.get(randTicketId));
			Bukkit.getServer().broadcastMessage(Translation.get(StringsID.LOTTERY_BROADCAST_WINNER, replaceMap, true)[0]);
		}
		
		int ticketCost = AllBanks.getInstance().getConfig().getInt("lottery.ticket-cost", 50);
		int totalPay = ticketCost * entries;
		
		AllBanks.getEconomy().depositPlayer(p, totalPay);
		
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		replaceMap.put("%1%", AllBanks.getEconomy().format(totalPay));
		Translation.getAndSendMessage(p, StringsID.LOTTERY_YOU_WON_THE_LOTTERY, replaceMap, true);
		
		YamlConfiguration lotteryYaml = YamlConfiguration.loadConfiguration(lotteryFile);
		lotteryYaml.set("last-execution", new Date().getTime());
		
		//Stats
		int currentwinners = lotteryYaml.getInt("total-winners", 0);
		lotteryYaml.set("total-winners", currentwinners + 1);
		lotteryYaml.set("last-winner", p.getName());
		
		try {
			lotteryYaml.save(lotteryFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
