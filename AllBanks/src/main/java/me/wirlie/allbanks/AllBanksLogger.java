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
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class AllBanksLogger {
	
	private static boolean initializedLogger = false;
	
	public static Logger getLogger(){
		initializeLogger();
		
		return Logger.getLogger("AllBanks");  
	}
	
	protected static void initializeLogger(){
		if(initializedLogger) return;
		
		Logger logger = Logger.getLogger("AllBanks");  
	    FileHandler fh;  
	    
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());

	    try {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler(AllBanks.getInstance() + File.separator + "logs" + File.separator + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-"+ calendar.get(Calendar.YEAR) + "-" + calendar.getTimeInMillis() + ".log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

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
