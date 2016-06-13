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

import org.bukkit.entity.Player;

/**
 * Funciones de utilidad para la conversión de experiencia 1.9
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ExperienceConversionUtil{
    
	/**
	 * Establecer experiencia total al jugador.
	 * @param player
	 * @param totalExp
	 */
    public static void setTotalExpToPlayer(Player player, int totalExp) {
        if (totalExp < 0) {
            throw new IllegalArgumentException("Experience can not have a negative value!");
        }
        
        //Reset exp and level...
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        //Recalculate experience
        int amount = totalExp;
        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player);
            amount -= expToLevel;
            if (amount >= 0) {
                player.giveExp(expToLevel);
            } else {
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }

    /**
     * Obtiene la cantidad de experiencia que se necesita para el sig nivel.
     * @param player
     * @return
     */
    private static int getExpAtLevel(final Player player) {
        return getExpAtLevel(player.getLevel());
    }

    /**
     * Obtiene la cantidad de experiencia que se necesita para el sig nivel.
     * @param level
     * @return cantidad de exp necesaria para el siguiente nivel.
     */
    public static int getExpAtLevel(final int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        }
        if ((level >= 16) && (level <= 30)) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;
    }
    
    /**
     * Convertir exp en niveles.
     * @param xp Cantidad de exp.
     * @return Niveles.
     */
    public static int convertExpToLevel(final int xp){
        int currentLevel = 0;
        int remXP = xp;
        
        while(remXP >= getExpAtLevel(currentLevel)){
        	remXP -= getExpAtLevel(currentLevel);
        	currentLevel++;
        }
        
        return currentLevel;
    }
    
    /**
     * Obtiene la cantidad de experiencia total para el nivel especificado.
     * @param level
     * @return Cantidad de exp total para el nivel expecificado.
     */
    public static int getExpToLevel(final int level) {
        int currentLevel = 0;
        int exp = 0;

        while (currentLevel < level) {
            exp += getExpAtLevel(currentLevel);
            currentLevel++;
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    /**
     * Obtiene la cantidad total de experiencia quer tiene el jugador.
     * @param player
     * @return Experiencia total del jugador.
     */
    public static int getTotalExperience(final Player player) {
        int exp = (int) Math.round(getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    /**
     * Obtener la experiencia necesaria para el siguiente nivel del jugador.
     * @param player
     * @return Experiencia necesaria para el siguiente nivel del jugador.
     */
    public static int getExpUntilNextLevel(final Player player) {
        int exp = (int) Math.round(getExpAtLevel(player) * player.getExp());
        int nextLevel = player.getLevel();
        return getExpAtLevel(nextLevel) - exp;
    }
}
