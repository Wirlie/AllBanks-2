/*
 * AllBanks2 - AllBanks2 is a plugin for CraftBukkit and Spigot.
 * Copyright (C) 2016 Josue Israel Acevedo Peña (Wirlie)
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

/**
 * 
 */
package me.wirlie.allbanks.utils;

/**
 * @author Wirlie
 *
 */
public class AssertUtil {
	
	/**
	 * Asertar desigualidad.
	 * @param obj1 Objeto 1
	 * @param obj2 Objeto 2
	 */
	public static void assertNotEquals(Object obj1, Object obj2){
		if(obj1.equals(obj2)){
			throw new AssertionError("Assertion failed! 'obj1 NotEquals obj2' -> false, obj1 equals to obj2");
		}
	}
	
	/**
	 * Asertar igualidad.
	 * @param obj1 Objeto 1
	 * @param obj2 Objeto 2
	 */
	public static void assertEquals(Object obj1, Object obj2){
		if(!obj1.equals(obj2)){
			throw new AssertionError("Assertion failed! 'obj1 Equals obj2' -> false, obj1 not equals to obj2");
		}
	}
	
	/**
	 * Asertar verdadero
	 * @param obj1 Objeto 1
	 */
	public static void assertTrue(boolean obj1){
		if(obj1 == false){
			throw new AssertionError("Assertion failed! 'AssertTrue obj1' -> false, obj1 is false");
		}
	}
	
	/**
	 * Asertar falsedad
	 * @param obj1 Objeto 1
	 */
	public static void assertFalse(boolean obj1){
		if(obj1 == true){
			throw new AssertionError("Assertion failed! 'AssertFalse obj1' -> false, obj1 is true");
		}
	}
	
	/**
	 * Asertar nulidad
	 * @param obj1 Objeto 1
	 */
	public static void assertNull(Object obj1){
		if(obj1 != null){
			throw new AssertionError("Assertion failed! 'AssertNull obj1' -> false, obj1 is not null");
		}
	}
	
	/**
	 * Asertar no nulo
	 * @param obj1 Objeto 1
	 */
	public static void assertNotNull(Object obj1){
		if(obj1 == null){
			throw new AssertionError("Assertion failed! 'AssertNotNull obj1' -> false, obj1 is null");
		}
	}
	
	/**
	 * Asertar rango de números.
	 * @param obj1 Número
	 * @param min Rango mínimo
	 * @param max Rango máximo
	 */
	public static void assertIntegerRange(int obj1, int min, int max){
		if(obj1 < min || obj1 > max){
			throw new AssertionError("Assertion failed! 'AssertIntegerRange obj1' -> false");
		}
	}
	
	/**
	 * Asertar rango mínimo
	 * @param obj1 Número
	 * @param min Rango mínimo
	 */
	public static void assertIntegerRangeMin(int obj1, int min){
		if(obj1 < min){
			throw new AssertionError("Assertion failed! 'AssertIntegerRangeMin obj1' -> false");
		}
	}
	
	/**
	 * Asertar rango máximo
	 * @param obj1 Número
	 * @param max Rango máximo
	 */
	public static void assertIntegerRangeMax(int obj1, int max){
		if(obj1 > max){
			throw new AssertionError("Assertion failed! 'AssertIntegerRangeMax obj1' -> false");
		}
	}
}
