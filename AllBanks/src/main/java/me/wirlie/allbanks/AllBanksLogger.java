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
package me.wirlie.allbanks;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class AllBanksLogger {
	
	private static boolean initializedLogger = false;
	
	private static Logger getLogger(){
		initializeLogger();
		
		return Logger.getLogger("AllBanksInternal");  
	}
	
	public static void info(String message, int lineNumber){
		getLogger().info(lineNumber + ">>>" + message);
	}
	
	public static void warning(String message, int lineNumber){
		getLogger().warning(lineNumber + ">>>" + message);
	}
	
	public static void severe(String message, int lineNumber){
		getLogger().severe(lineNumber + ">>>" + message);
	}
	
	protected static void initializeLogger(){
		if(initializedLogger) return;
		
		Logger logger = Logger.getLogger("AllBanksInternal");
		logger.setUseParentHandlers(false);
		
		for(Handler h : logger.getHandlers()){
			if(h instanceof FileHandler)
				logger.removeHandler(h);
		}
		
		//limpiar archivos .lck si es que hay
		File dirLog = new File(AllBanks.getInstance().getDataFolder() + File.separator + "logs");
    	
		for(File f : dirLog.listFiles()){
			if(f.getName().endsWith("lck")){
				//añadir a la lista para remover al cerrar el servidor
				f.delete();
			}
		}
		
	    FileHandler fh = null;  
	    
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());

	    try {  

	        // This block configure the logger with handler and formatter  
	    	
	    	if(!dirLog.exists()){
	    		dirLog.mkdirs();
	    	}
	    	
	    	fh = new FileHandler(AllBanks.getInstance().getDataFolder() + File.separator + "logs" + File.separator + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-"+ calendar.get(Calendar.YEAR) + "-" + calendar.getTimeInMillis() + ".log");  

	    	fh.setFormatter(new SimpleFormatter());
	    	
	    	logger.addHandler(fh);
	        
	    	fh.setFormatter(new Formatter() {
	            @Override
	            public String format(LogRecord record) {
	                SimpleDateFormat logTime = new SimpleDateFormat("HH:mm:ss");
	                Calendar cal = new GregorianCalendar();
	                cal.setTimeInMillis(record.getMillis());
	                
	                String msg = record.getMessage();
	                String lineNumber = "";
	                
	                String[] split = msg.split(">>>");
	                if(split.length == 2){
	                	msg = split[1];
	                	lineNumber = " : Line " + split[0];
	                }
	                
	                return "[" + logTime.format(cal.getTime())
	                		+ " " + record.getLevel() + "] "
	                        + record.getMessage()
	                        + " (source: "
	                        + record.getSourceClassName().substring(
	                                record.getSourceClassName().lastIndexOf(".")+1,
	                                record.getSourceClassName().length())
	                        
	                        + "."
	                        + record.getSourceMethodName()
	                        + "()" + lineNumber + ")" + "\n";
	            }
	        });  

	        // the following statement is used to log any messages 
	        String pm_am_prefix = "";
	        
	        if(calendar.get(Calendar.AM_PM) == Calendar.AM){
	        	pm_am_prefix = "AM";
	        }else{
	        	pm_am_prefix = "PM";
	        }
	        
	        logger.info("AllBanks logger initialized (" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR) + ":" +  calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " " + pm_am_prefix + ").");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    
	    logger.setUseParentHandlers(false);
		
		initializedLogger = true;
	}
}
