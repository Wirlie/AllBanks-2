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
package me.wirlie.allbanks.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.wirlie.allbanks.AllBanks;

/**
 * Hacer allbanks más interactivo.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class InteractiveUtil{
	
	/**
	 * Crear un efecto de cohete.
	 * @param loc Localización
	 * @param power Poder del cohete
	 * @param type Tipo del cohete
	 * @param detonateDelay Delay de la detonación
	 * @param fireworkColors Colores del cohete
	 */
	public static void playFireworkEffect(Location loc, int power, Type type, long detonateDelay, Color... fireworkColors) {
		final Firework fw = (Firework) loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = fw.getFireworkMeta();
		FireworkEffect effect = FireworkEffect.builder()
				.flicker(false)
				.trail(true)
				.with(type)
				.withColor(fireworkColors)
				.build();
		meta.addEffect(effect);
		meta.setPower(power);
		fw.setFireworkMeta(meta);
		
		if(detonateDelay >= 0) {
			new BukkitRunnable() {

				public void run() {
					fw.detonate();
				}
				
			}.runTaskLater(AllBanks.getInstance(), detonateDelay);
		}
	}

	@SuppressWarnings("javadoc")
	public static enum SoundType{
		SUCCESS,
		DENY,
		SWITCH_BANK_STEP,
		VIRTUAL_CHEST_OPEN,
		VIRTUAL_CHEST_CLOSE,
		WARNING,
		NEW_BANK;
	}
	
	/**
	 * Enviar un sonido al jugador.
	 * @param p Jugador
	 * @param stype Tipo de sonido.
	 */
	public static void sendSound(Player p, SoundType stype) {
		
		Sound sendSound = null;
		float soundPitch = 1;
		
		switch(stype) {
		case SUCCESS:
			sendSound = Sound.ENTITY_PLAYER_LEVELUP;
			soundPitch = 1;
			break;
		case DENY:
			sendSound = Sound.BLOCK_NOTE_PLING;
			soundPitch = (float) 0.7;
			break;
		case SWITCH_BANK_STEP:
			sendSound = Sound.BLOCK_NOTE_HAT;
			soundPitch = 1;
			break;
		case VIRTUAL_CHEST_OPEN:
			sendSound = Sound.BLOCK_CHEST_OPEN;
			soundPitch = 1;
			break;
		case VIRTUAL_CHEST_CLOSE:
			sendSound = Sound.BLOCK_CHEST_CLOSE;
			soundPitch = 1;
			break;
		case NEW_BANK:
			sendSound = Sound.ENTITY_FIREWORK_BLAST;
			soundPitch = 1;
			break;
		case WARNING:
			sendSound = Sound.BLOCK_NOTE_PLING;
			soundPitch = 1;
			break;
		}
		
		p.playSound(p.getLocation(), sendSound, 5, soundPitch);
	}
}