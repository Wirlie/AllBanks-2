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
package me.wirlie.allbanks.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.wirlie.allbanks.AllBanks;

/**
 * @author Wirlie
 *
 */
public class DBUtil {
	
	/** AllBanksLand, Nombre de conexión **/
	public final static String ALLBANKSLAND_DATABASE_CONNECTION_NAME = "AllBanksLand";
	/** AllBanksLand, Ruta hacia la base de datos **/
	public final static String ALLBANKSLAND_DATABASE_PATH = AllBanks.getInstance().getDataFolder() + File.separator + "AllBanksLandWorldData.db";

	/** AllBanks, Nombre de conexión **/
	public final static String ALLBANKS_DATABASE_CONNECTION_NAME = "local";
	/** AllBanks, Ruta hacia la base de datos **/
	public final static String ALLBANKS_DATABASE_PATH = AllBanks.getInstance().getDataFolder() + File.separator + "LocalDataBase.db";

	/** ItemSolution, Nombre de conexión **/
	public final static String ITEMSOLUTION_DATABASE_CONNECTION_NAME = "itemSolution";
	/** ItemSolution, Ruta hacia la base de datos **/
	public final static String ITEMSOLUTION_DATABASE_PATH = AllBanks.getInstance().getDataFolder() + File.separator + "itemSolution.db";

	/** Statistics, Nombre de conexión **/
	public final static String STATISTICS_DATABASE_CONNECTION_NAME = "Statistics";
	/** Statistics, Ruta hacia la base de datos **/
	public final static String STATISTICS_DATABASE_PATH = AllBanks.getInstance().getDataFolder() + File.separator + "AllBanksStatistics.db";
	
	/**
	 * Comprobar actualizaciones para la base de datos de AllBanksLand.
	 */
	public static void checkUpdatesAllBanksLandDataBase(){
		
		List<Integer> updates = Arrays.asList(1);
		List<Integer> appliedUpdates = new ArrayList<Integer>();
		
		AllBanks.getInstance();
		Connection dbc = AllBanks.dbSQLite.getConnection(ALLBANKSLAND_DATABASE_CONNECTION_NAME);
		AssertUtil.assertNotNull(dbc);
		
		Statement stm = null;
		ResultSet res = null;
		
		try {
			stm = dbc.createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS applied_updates (id INTEGER PRIMARY KEY AUTOINCREMENT, update_number NUMBER NOT NULL)");
			res = stm.executeQuery("SELECT * FROM applied_updates");
			while(res.next()){
				appliedUpdates.add(res.getInt("update_number"));
			}
			res.close();
			
			for(int update : updates){
				if(!appliedUpdates.contains(update)){
					AllBanksLogger.info("[DATABASE] Updating AllBanksLand database, Applying update #" + update);
					switch(update){
					case 1:
						/*
						 * Actualización #1, en esta actualización se añadió la columna world_base_height a la tabla worlds_cfg
						 */
						DatabaseMetaData md = dbc.getMetaData();
						ResultSet rs = md.getColumns(null, null, "worlds_cfg", "world_base_height");
						if(!rs.next()) {
							//No existe la columna
							stm.executeUpdate("ALTER TABLE worlds_cfg ADD world_base_height NUMBER DEFAULT -1 NOT NULL");
						}
						break;
					}
					//Aplicar actualización
					stm.executeUpdate("INSERT INTO applied_updates (update_number) VALUES (" + update + ")");
					AllBanksLogger.info("[DATABASE] AllBanksLand database, Update #" + update + " applied.");
				}else{
					AllBanksLogger.debug("[DATABASE] AllBanksLand database | Update #" + update + " already applied.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				if(res != null) res.close();
				if(stm != null) stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
