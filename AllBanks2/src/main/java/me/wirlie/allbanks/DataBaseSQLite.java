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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import me.wirlie.allbanks.utils.AllBanksLogger;

/**
 * Clase recopilada desde AllBanks (antiguo AllBanks)
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */

public class DataBaseSQLite {
    private static DataBaseSQLite instance = new DataBaseSQLite();
    public Connection connection = null;
    public Statement statement;
    public static boolean setstatus = false;
    HashMap<String, Connection> multipleConnections = new HashMap<String, Connection>();

    public static synchronized DataBaseSQLite getInstance() {
        return instance;
    }
    
    public static boolean tryForClass() {
    	try {
    		AllBanksLogger.info("Try for class: org.sqlite.JDBC");
    		Class.forName("org.sqlite.JDBC");
    		AllBanksLogger.info("Success!");
    		return true;
    	}catch (ClassNotFoundException e) {
    		AllBanksLogger.severe("Error!");
            e.printStackTrace();
            return false;
        }
    }
    
    public Connection setConnection(String path, String NameConnection) {
        try {
            Class.forName("org.sqlite.JDBC");
            File f = new File(path);
            if(!f.getParentFile().exists()){
            	f.getParentFile().mkdirs();
            }
            
            if(!f.exists()){
            	try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            this.multipleConnections.put(NameConnection, DriverManager.getConnection("jdbc:sqlite:" + path));
            return getConnection(NameConnection);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public Connection getConnection(String NameConnection) {
        return this.multipleConnections.get(NameConnection);
    }

    public boolean checkConnection(String NameConnection) {
        if (this.multipleConnections.containsKey(NameConnection)) {
            return true;
        }
        return false;
    }
}