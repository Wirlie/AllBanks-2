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
package me.wirlie.allbanks.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ShopUtil {

	/**
	 * @param string
	 * @return
	 */
	public static boolean validatePriceLine(String s) {

		Pattern r = Pattern.compile("^((S:[0-9]{1,}(|(\\.[0-9]{1,})))|(S:[0-9]{1,}(|(\\.[0-9]{1,}))) (B:[0-9]{1,}(|(\\.[0-9]{1,})))|(B:[0-9]{1,}(|(\\.[0-9]{1,}))) (S:[0-9]{1,}(|(\\.[0-9]{1,})))|(B:[0-9]{1,}(|(\\.[0-9]{1,}))))$");
		
		return r.matcher(s).matches();
	}
	
	public static BigDecimal getBuyPrice(String priceLine) {
		String[] str = priceLine.split(" ");
		for(String s : str) {
			if(s.startsWith("B:")) {
				return new BigDecimal(s.replace("B:", ""));
			}
		}
		
		return null;
	}
	
	public static BigDecimal getSellPrice(String priceLine) {
		String[] str = priceLine.split(" ");
		for(String s : str) {
			if(s.startsWith("S:")) {
				return new BigDecimal(s.replace("S:", ""));
			}
		}
		
		return null;
	}

}
