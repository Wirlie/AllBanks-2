/*
 * Copyright (C) 2016 Wirlie
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package me.wirlie.allbanks.main;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author Wirlie
 *
 */

public class DataBase {
    private static DataBase instance = new DataBase();
    public Connection connection = null;
    public Statement statement;
    public static boolean setstatus = false;
    HashMap<String, Connection> multipleConnections = new HashMap<String, Connection>();

    public static synchronized DataBase getInstance() {
        return instance;
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