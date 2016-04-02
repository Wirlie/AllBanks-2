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
package me.wirlie.allbanks.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;

import me.wirlie.allbanks.AllBanks;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class AllBanksLogger {
	
	private static boolean initializedLogger = false;
	private static File dirLog = new File(AllBanks.getInstance().getDataFolder() + File.separator + "logs");
	private static File fileLog = null;
	
	public enum AllBanksLoggerLevel{
		INFO,
		WARNING,
		SEVERE,
		DEBUG;
	}
	
	public static void info(String message){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		writeMessage(message, stackTraceElements[2], AllBanksLoggerLevel.INFO, false);
	}
	
	public static void info(String message, boolean showOnMainConsole){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		writeMessage(message, stackTraceElements[2], AllBanksLoggerLevel.INFO, showOnMainConsole);
	}
	
	public static void debug(String message){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		writeMessage(message, stackTraceElements[2], AllBanksLoggerLevel.DEBUG, false);
	}
	
	public static void debug(String message, boolean showOnMainConsole){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		writeMessage(message, stackTraceElements[2], AllBanksLoggerLevel.DEBUG, showOnMainConsole);
	}
	
	public static void warning(String message, boolean showOnMainConsole){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		writeMessage(message, stackTraceElements[2], AllBanksLoggerLevel.WARNING, showOnMainConsole);
	}
	
	public static void warning(String message){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		writeMessage(message, stackTraceElements[2], AllBanksLoggerLevel.WARNING, false);
	}
	
	public static void severe(String message, boolean showOnMainConsole){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		writeMessage(message, stackTraceElements[2], AllBanksLoggerLevel.SEVERE, showOnMainConsole);
	}
	
	public static void severe(String message){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		writeMessage(message, stackTraceElements[2], AllBanksLoggerLevel.SEVERE, false);
	}
	
	public static void writeMessage(String message, StackTraceElement threadInfo, AllBanksLoggerLevel level, boolean showOnMainConsole){
		if(message == null || threadInfo == null) return;
		
		if(showOnMainConsole){
			switch(level){
			case INFO:
				AllBanks.getInstance().getLogger().info(message);
				break;
			case SEVERE:
				AllBanks.getInstance().getLogger().severe(message);
				break;
			case WARNING:
				AllBanks.getInstance().getLogger().warning(message);
				break;
			case DEBUG:
				AllBanks.getInstance().getLogger().info(message);
				break;
			
			}
		}
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());
	    
	    String hours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
	    if(calendar.get(Calendar.HOUR_OF_DAY) < 10) hours = "0" + hours;

	    String minutes = String.valueOf(calendar.get(Calendar.MINUTE));
	    if(calendar.get(Calendar.MINUTE) < 10) minutes = "0" + minutes;
	    
	    String seconds = String.valueOf(calendar.get(Calendar.SECOND));
	    if(calendar.get(Calendar.SECOND) < 10) seconds = "0" + seconds;
	    
		String prefix = "[" + level.toString() + " " + hours + ":" + minutes + ":" + seconds + "]";
		String subfix = "(FileSource: " + threadInfo.getClassName().substring(threadInfo.getClassName().lastIndexOf(".") + 1) + "." + threadInfo.getMethodName() + "():" + threadInfo.getLineNumber() + ")";
		String writeMessage = prefix + " " + message + " " + subfix;
		
		try{
			FileOutputStream outputStream = new FileOutputStream(fileLog, true);
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
	        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
	         
	        bufferedWriter.write(writeMessage);
	        bufferedWriter.newLine();
	         
	        bufferedWriter.close();
		} catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void initializeLogger(){
		if(initializedLogger) return;
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());
	    
	    if(!dirLog.exists()){
	    	dirLog.mkdirs();
	    }
	    
		fileLog = new File(AllBanks.getInstance().getDataFolder() + File.separator + "logs" + File.separator + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-"+ calendar.get(Calendar.YEAR) + "-" + calendar.getTimeInMillis() + ".log");
 
		initializedLogger = true;
	}
}