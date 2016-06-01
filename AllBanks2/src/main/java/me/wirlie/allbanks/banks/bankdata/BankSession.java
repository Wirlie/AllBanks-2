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
package me.wirlie.allbanks.banks.bankdata;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.Banks;
import me.wirlie.allbanks.Banks.ABSignType;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;

/**
 * Esta clase se encarga de almacenar las sesiones de AllBanks cuando un jugador usa un banco (letrero),
 * de esta manera esta clase siempre estará accesible desde sus métodos estáticos para acceder a los valores
 * almacenados en el {@link BankSession#activeSessions HashMap activeSessions}
 * 
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class BankSession {
	
	/*
	 * Funciones para las sesiones activas. (activeSessions) 
	 */
	
	public static int expireInactiveSessionBeforeSeconds = 120;
	static boolean started = false;
	
	public static void StartExpireSessionRunnable(){
		//Auto ejecutar un runnable que cheque las sesiones expiradas.
		if(!started) {
			started = true;
			new BukkitRunnable(){
	
				public void run() {
					for(BankSession bs : activeSessions.values()){
						if(bs.sessionExpired()){
							bs.closeSession();
						}
					}
				}
				
			}.runTaskTimer(AllBanks.getInstance(), 20, 20);
		}
	}
	
	/**
	 * Este HashMap contiene las sesiones activas en una especie de "caché".
	 */
	private static HashMap<UUID, BankSession> activeSessions = new HashMap<UUID, BankSession>();
	
	/**
	 * Obtener todas las sesiones activas.
	 * @return Colección de todas las sesiones.
	 */
	public static Collection<BankSession> getAllActiveSessions(){
		return activeSessions.values();
	}
	
	/**
	 * Recuperar una sesión por medio de la instancia Sign (letrero).
	 * @param sign - Instancia Sign (letrero).
	 * @return Devuelve la sesión que pertenece a este letrero.
	 */
	public static BankSession getActiveSessionBySign(Sign sign){
		
		for(BankSession bs : activeSessions.values()){
			if(bs.getSign().equals(sign)){
				return bs;
			}
		}
		
		return null;
	}
	
	/**
	 * Iniciar una nueva sesión ({@link me.wirlie.allbanks.banks.bankdata.BankSession#startSession(UUID, BankSession) Método similar})".
	 * @param p - Jugador al que pertenece la sesión (Si ya existe una sessión con este jugador la anterior sessión será reemplazada).
	 * @param bs - Datos de la sesión {@link me.wirlie.allbanks.banks.bankdata.BankSession BankSession}
	 * @return Para fines prácticos se devuelve la variable <b>bs</b> sin modificar.
	 */
	public static BankSession startSession(Player p, BankSession bs){
		startSession(p.getUniqueId(), bs);
		
		//Para fines prácticos, retornamos el valor bs.
		return bs;
	}
	
	/**
	 * Iniciar una nueva sesión.
	 * @param uuid - UUID del jugador al que pertenece la sesión (Si ya existe una sesión con este jugador la anterior sesión será reemplazada).
	 * @param bs - Datos de la sesión {@link me.wirlie.allbanks.banks.bankdata.BankSession BankSession}
	 * @return Para fines prácticos se devuelve la variable <b>bs</b> sin modificar.
	 */
	public static BankSession startSession(UUID uuid, BankSession bs){
		activeSessions.put(uuid, bs);
		
		//Para fines prácticos, retornamos el valor bs.
		return bs;
	}
	
	/**
	 * Obtener la sesión si es que existe ({@link me.wirlie.allbanks.banks.bankdata.BankSession#getSession(UUID) Método similar}).
	 * @param p - Jugador al que pertenece la sesión.
	 * @return Devuelve la instancia de la sesión almacenada ({@link BankSession}) o {@code null} si no existe una sesión para este jugador.
	 */
	public static BankSession getSession(Player p){
		return getSession(p.getUniqueId());
	}
	
	/**
	 * Obtener la sesión si es que existe.
	 * @param uuid - Jugador al que pertenece la sesión (UUID).
	 * @return Devuelve la instancia de la sesión almacenada ({@link BankSession}) o {@code null} si no existe una sesión para este jugador.
	 */
	public static BankSession getSession(UUID uuid){
		return activeSessions.get(uuid);
	}
	
	/**
	 * Cerrar una sesión.
	 * @param p - Jugador al que pertenece la sesión.
	 */
	public static void closeSession(Player p){
		if(p != null){
			closeSession(p.getUniqueId());
			
			//Sonido
			if(p.isOnline())
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 9, 1);
		}
	}
	
	/**
	 * Cerrar una sesión.
	 * @param uuid - Jugador al que pertenece la sesión (UUID).
	 */
	public static void closeSession(UUID uuid){
		//notificar
		if(Bukkit.getPlayer(uuid).isOnline())
			Translation.getAndSendMessage(Bukkit.getPlayer(uuid), StringsID.SESSION_CLOSED, true);
		
		//actualizar letrero
		BankSession bs = activeSessions.get(uuid);
		if(bs.getSign().getBlock().getType().equals(Material.WALL_SIGN)) bs.updateToInitialState();
		
		activeSessions.remove(uuid);
	}

	/**
	 * <ul>
	 * Checar si una sesión ya existe, esto es útil para procesar métodos como {@link BankSession#getSession(UUID)} o {@link BankSession#closeSession(UUID)}.
	 * Sin embargo no es necesario, ya que estos métodos pueden trabajar a pesar de que una sesión no exista.
	 * </ul>
	 * @param p - Jugador al que pertenece la sesión.
	 * @return {@code true} si la sesión existe, o {@code false} si no.
	 */
	public static boolean checkSession(Player p){
		return checkSession(p.getUniqueId());
	}
	
	/**
	 * <ul>
	 * Checar si una sesión ya existe, esto es útil para procesar métodos como {@link BankSession#getSession(UUID)} o {@link BankSession#closeSession(UUID)}.
	 * Sin embargo no es necesario, ya que estos métodos pueden trabajar a pesar de que una sesión no exista.
	 * </ul>
	 * @param uuid - Jugador al que pertenece la sesión (UUID).
	 * @return {@code true} si la sesión existe, o {@code false} si no.
	 */
	public static boolean checkSession(UUID uuid){
		return activeSessions.containsKey(uuid);
	}
	
	/**
	 * Actualizar una sesión con una instancia nueva de {@link BankSession}
	 * @param p - Jugador al que se desea actualizar la sesión.
	 * @param bs - Nueva instancia de {@link BankSession} (se reemplazará la anterior).
	 */
	public static void updateSession(Player p, BankSession bs){
		startSession(p, bs);
	}
	
	/**
	 * Actualizar una sesión con una instancia nueva de {@link BankSession}
	 * @param uuid - Jugador al que se desea actualizar la sesión (UUID).
	 * @param bs - Nueva instancia de {@link BankSession} (se reemplazará la anterior).
	 */
	public static void updateSession(UUID uuid, BankSession bs){
		startSession(uuid, bs);
	}
	
	/*
	 * Datos usados para BankSession.
	 */
	
	/** Jugador, dueño de esta sesión. */
	Player player;
	
	/** variable del tipo {@code Enum} que conserva el tipo del banco de esta sesión. */
	ABSignType btype;
	
	/** Paso (step) actual del banco de esta sesión. */
	int step;
	
	/** Letrero con el cual se está trabajando en esta sesión. */
	Sign sign;
	
	/** Último uso de esta sesión, formato Time **/
	long lastUse = new Date().getTime();
	
	/**
	 * Inicializar una nueva instancia de {@link BankSession}
	 * @param player - Jugador.
	 * @param sign - Letrero con el que se va a trabajar.
	 * @param btype - Tipo de banco al que pertenece el letrero con el que se va a trabajar.
	 * @param step - Paso actual del letrero (banco).
	 */
	public BankSession(Player player, Sign sign, ABSignType btype, int step){
		this.btype = btype;
		this.step = step;
		this.sign = sign;
		this.player = player;
	}
	
	/**
	 * @return Devuelve el tipo de banco de esta sesión ({@link ABSignType}).
	 */
	public ABSignType getBankType(){
		return btype;
	}
	
	/**
	 * @return Devuelve el paso actual de esta sesión.
	 */
	public int getStep(){
		return step;
	}
	
	/**
	 * @return Devuelve el letrero con el que se está trabajando ({@link Sign})
	 */
	public Sign getSign(){
		return sign;
	}
	
	/**
	 * @return Devuelve el jugador al que pertenece esta sesión.
	 */
	public Player getPlayer(){
		return player;
	}
	
	/**
	 * Actualizar paso y cambiar el letrero a este nuevo paso.
	 * @param step - Paso a actualizar.
	 * @param ignoreUpdate - Ignorar actualización de sesión.
	 */
	public void updateStepAndSwitchSign(int step, boolean ignoreUpdate){
		this.step = step;
		
		if(!ignoreUpdate){
			updateSession(player.getUniqueId(), this);
		}
		
		Banks.switchABSignToStep(btype, sign, step, true);
	}
	
	/**
	 * Actualizar paso y cambiar el letrero a este nuevo paso.
	 * @param step - Paso a actualizar.
	 */
	public void updateStepAndSwitchSign(int step){
		updateStepAndSwitchSign(step, false);
	}
	
	/**
	 * Actualizar paso al siguiente, ejemplo, si el paso actual está definido en 1 con este método
	 * se buscará avanzar al paso 2, y si está en el paso 2 se buscará avanzar al paso 3.
	 */
	public void updateStepAndSwitchSign(){
		updateStepAndSwitchSign(Banks.getNextABSignStep(this), false);
	}
	
	/**
	 * Cerrar sesión de esta instancia.
	 */
	public void closeSession(){
		closeSession(player);
	}
	
	/**
	 * Actualizar sesión al paso inicial.
	 */
	private void updateToInitialState() {
		updateStepAndSwitchSign(-1, true);
	}
	
	public void reloadSign(){
		Banks.switchABSignToStep(btype, sign, step, false);
	}
	
	public void updateLastUse(){
		this.lastUse = new Date().getTime();
	}
	
	public boolean sessionExpired(){
		long currentTime = new Date().getTime();
		int diferenceSeconds = (int) ((currentTime - lastUse) / 1000);
		
		if(diferenceSeconds >= expireInactiveSessionBeforeSeconds){
			return true;
		}
		
		return false;
	}
}
