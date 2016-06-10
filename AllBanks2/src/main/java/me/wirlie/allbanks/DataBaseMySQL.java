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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Clase recopilada desde AllBanks (antiguo AllBanks) y adaptada para el funcionamiento con MySQL
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class DataBaseMySQL {
    private static DataBaseSQLite instance = new DataBaseSQLite();
    HashMap<String, Connection> multipleConnections = new HashMap<String, Connection>();
    
    /**
     * Obtener la instancia de esta clase.
     * @return Instancia de la clase.
     */
    public static synchronized DataBaseSQLite getInstance() {
        return instance;
    }
    
    /**
     * Intentar obtener el driver para MySQL.
     * @return true si el driver pudo ser encontrado.
     */
    public static boolean tryFindClassForName() {
    	try {
    		
    		Class.forName("com.mysql.jdbc.Driver");
    		return true;
    	}catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    	
    }
    
    /**
     * Establecer la conexión MySQL.
     * @param NameConnection
     * @return
     */
    public Connection setConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            String host = AllBanks.getInstance().getConfig().getString("pl.mysql-host", null);
            String user = AllBanks.getInstance().getConfig().getString("pl.mysql-user", null);
            String pass = AllBanks.getInstance().getConfig().getString("pl.mysql-pass", null);
            String port = String.valueOf(AllBanks.getInstance().getConfig().getInt("pl.mysql-pass", 3306));
            String database = AllBanks.getInstance().getConfig().getString("pl.mysql-database", null);
            
            this.multipleConnections.put("global", DriverManager.getConnection("jdbc:mysql://" + host + ":"+ port +"/" + database + "",user, pass));
            return getConnection();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
            //No se puede ejecutar AllBanks.
            
        }
        
        return null;
    }
    
    /**
     * Obtener la conexión establecida con MySQL.
     * @return Conexión establecida, o null si no se ha especificado una conexión.
     */
    public Connection getConnection() {
        return this.multipleConnections.get("global");
    }
    
    /**
     * Comprobar si ya hay una conexión activa MySQL.
     * @return true si hay una conexión activa.
     */
    public boolean checkConnection() {
        if (this.multipleConnections.containsKey("global")) {
            return true;
        }
        return false;
    }
}
