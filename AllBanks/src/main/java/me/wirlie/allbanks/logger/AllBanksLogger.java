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
		SEVERE;
	}
	
	public static void info(String message, AllBanksLoggerInfo threadInfo){
		writeMessage(message, threadInfo, AllBanksLoggerLevel.INFO);
	}
	
	public static void warning(String message, AllBanksLoggerInfo threadInfo){
		writeMessage(message, threadInfo, AllBanksLoggerLevel.WARNING);
	}
	
	public static void severe(String message, AllBanksLoggerInfo threadInfo){
		writeMessage(message, threadInfo, AllBanksLoggerLevel.SEVERE);
	}
	
	public static void writeMessage(String message, AllBanksLoggerInfo threadInfo, AllBanksLoggerLevel level){
		if(message == null || threadInfo == null) return;
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());
	    
		String prefix = "[" + level.toString() + " " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "]";
		String subfix = "(FileSource: " + threadInfo.getClassName() + "." + threadInfo.getMethodName() + "():" + threadInfo.getLineNumber() + ")";
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
