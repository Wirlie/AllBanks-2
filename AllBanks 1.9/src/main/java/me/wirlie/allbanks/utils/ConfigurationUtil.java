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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;

/**
 * Funciones de utilidad para la configuración.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class ConfigurationUtil{

	public static String convertSecondsIntoTimeAgo(int seconds, boolean fullString) {
		return convertSecondsIntoTimeAgo(seconds, fullString, 2);
	}
	
	public static String convertSecondsIntoTimeAgo(int seconds, int maxShow) {
		return convertSecondsIntoTimeAgo(seconds, false, maxShow);
	}
	
	private static String convertSecondsIntoTimeAgo(int seconds, boolean fullString, int maxShow) {
		//Semanas
		int weeks = (int) Math.floor(seconds / (60 * 60 * 24 * 7));
		seconds -= weeks * (60 * 60 * 24 * 7);
		
		//Días
		int days = (int) Math.floor(seconds / (60 * 60 * 24)); 
		seconds -= days * (60 * 60 * 24);
		
		//Horas
		int hours = (int) Math.floor(seconds / (60 * 60)); 
		seconds -= hours * (60 * 60);
		
		//Minutos
		int minutes = (int) Math.floor(seconds / (60)); 
		seconds -= minutes * (60);
		
		String returnStr = "";
		int showed = 0;
		
		if(weeks > 0) {
			if(showed < maxShow || fullString) {
				returnStr += weeks + " " + Translation.get(StringsID.WEEKS, false)[0];
				showed++;
			}
		}
		
		if(days > 0) {
			if(showed < maxShow || fullString) {
				returnStr += ((showed == 0) ? "" : ", ") + days + " " + Translation.get(StringsID.DAYS, false)[0];
				showed++;
			}
		}
		
		if(hours > 0) {
			if(showed < maxShow || fullString) {
				returnStr += ((showed == 0) ? "" : ", ") + hours + " " + Translation.get(StringsID.HOURS, false)[0];
				showed++;
			}
		}
		
		if(minutes > 0) {
			if(showed < maxShow || fullString) {
				returnStr += ((showed == 0) ? "" : ", ") + minutes + " " + Translation.get(StringsID.MINUTES, false)[0];
				showed++;
			}
		}
		
		if(seconds > 0) {
			if(showed < maxShow || fullString) {
				returnStr += ((showed == 0) ? "" : ", ") + seconds + " " + Translation.get(StringsID.SECONDS, false)[0];
				showed++;
			}
		}
		
		if(seconds <= 0 && showed == 0) {
			returnStr += ((showed == 0) ? "" : ", ") + 0 + " " + Translation.get(StringsID.SECONDS, false)[0];
			showed++;
		}
		
		return returnStr;
	}
	
	/**
	 * Bueno, esto realmente ayuda con la configuración banks.bank-loan.collect-interest-every
	 * ya que se encuentra en formato 0 days, 0 hours, 0 minutes, 0 seconds y es necesario
	 * convertir todo eso a un mensaje que se pueda adaptar a las traducciones.
	 * @param strTimeValue
	 * @return
	 */
	public static int convertTimeValueToSeconds(String strTimeValue){
		
		if(strTimeValue == null) return -1;
		
		//pasar "0 days, 0 hours" a "0 days,0 hours"
		strTimeValue = strTimeValue.replace(", ", ",");
		
		//Separar valores con la coma.
		String[] split = strTimeValue.split(",");
		
		int totalSeconds = 0;
		
		//comprobar si es válido
		for(String s : split){
			//Usar REGEX
			Pattern r = Pattern.compile("^([0-9]{1,2})+ +(days|day|hours|hour|minutes|minute|seconds|second)$");
			Matcher m = r.matcher(s);
			
			if(!m.matches()){
				//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
				AllBanks.getInstance().getLogger().warning("Invalid timeValue: " + s);
				AllBanks.getInstance().getLogger().warning("Valid values: days | day | hours | hour | minutes | minute | seconds | second");
				try{
					throw new IllegalArgumentException("Invalid timeValue: " + s);
				}catch(IllegalArgumentException e){
					e.printStackTrace();
					return -1;
				}
			}else{
				
				if(m.groupCount() != 2){
					AllBanks.getInstance().getLogger().warning("The value " + s + " does not have a valid syntax for a timeValue.");
					AllBanks.getInstance().getLogger().warning("Valid syntax: '{INT} {StringValue}', example: '1 day', '1 minute'");
					//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
					return -1;
				}
				
				int intValue = 0;
				String timeValue = m.group(2);
				
				try{
					intValue = Integer.parseInt(m.group(1));
				}catch(NumberFormatException e){
					AllBanks.getInstance().getLogger().warning("The value " + m.group(1) + " is not a valid number.");
					AllBanks.getInstance().getLogger().warning("Matched string: " + s);
					AllBanks.getInstance().getLogger().warning("Full string: " + strTimeValue);
					e.printStackTrace();
					//invalido, el valor -1 hace que el sistema automático nunca se ejecute.
					return -1;
				}
				
				if(timeValue.equalsIgnoreCase("days")){
					totalSeconds += intValue * 24 * 60 * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("day")){
					totalSeconds += intValue * 24 * 60 * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("hours")){
					totalSeconds += intValue * 60 * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("hour")){
					totalSeconds += intValue * 60 * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("minutes")){
					totalSeconds += intValue * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("minute")){
					totalSeconds += intValue * 60;
					continue;
				}else if(timeValue.equalsIgnoreCase("seconds")){
					totalSeconds += intValue;
					continue;
				}else if(timeValue.equalsIgnoreCase("second")){
					totalSeconds += intValue;
					continue;
					
				}
			}
		}
		
		return totalSeconds;
	}
}