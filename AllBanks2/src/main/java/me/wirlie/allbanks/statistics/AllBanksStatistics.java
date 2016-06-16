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
package me.wirlie.allbanks.statistics;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.utils.DBUtil;

/**
 * @author Wirlie
 *
 */
public class AllBanksStatistics {
	/** Conexión con la base de datos de estadísticas*/
	public static Connection DBC = null;

	/**
	 * Inicializar, con el fin de instalar la base de datos.
	 */
	public static void initializeSystem(){
		DBC = AllBanks.dbSQLite.setConnection(DBUtil.STATISTICS_DATABASE_PATH, DBUtil.STATISTICS_DATABASE_CONNECTION_NAME);
		Statement stm = null;
		try{
			stm = DBC.createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS shops_statistics (id INTEGER PRIMARY KEY AUTOINCREMENT, sign_loc TEXT NOT NULL, transaction_player TEXT NOT NULL, transaction_item_base64 TEXT NOT NULL, date BIGINT NOT NULL, shop_owner TEXT NOT NULL, transaction_type TEXT NOT NULL)");
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(stm != null) stm.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

}
