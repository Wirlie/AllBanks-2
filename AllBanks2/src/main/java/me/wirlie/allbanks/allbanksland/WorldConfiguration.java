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
package me.wirlie.allbanks.allbanksland;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import org.bukkit.configuration.file.YamlConfiguration;

import me.wirlie.allbanks.utils.FileDirectory;

/**
 * @author josue
 *
 */
public class WorldConfiguration {
	
	String id;
	
	String mob_spawn = "true";
	String animal_spawn = "true";
	String creeper_explosion = "false";
	String wither_explosion = "false";
	String allow_wither = "false";
	String allow_nether_portal = "true";
	String allow_tnt_explosion = "true";
	String plot_per_user = "1";
	String cost_claim = "0";
	
	int worldHeight = 0;
	
	/**
	 * Constructor principal de la clase.
	 * @param worldID ID del mundo.
	 */
	public WorldConfiguration(String worldID){
		id = worldID;
		
		generateDefaultConfiguration();
		loadConfiguration();
	}
	
	/**
	 * Generar una configuración por defecto para cualquier mundo.
	 */
	public void generateDefaultConfiguration(){
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		File worldCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + id + "-cfg.yml");
		
		if(!worldCfg.exists()){
			try {
				worldCfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			YamlConfiguration worldCfgYaml = YamlConfiguration.loadConfiguration(worldCfg);
			
			worldCfgYaml.set("world.mob-spawn", "true");
			worldCfgYaml.set("world.animal-spawn", "true");
			worldCfgYaml.set("world.creeper-explosion", "false");
			worldCfgYaml.set("world.wither-explosion", "false");
			worldCfgYaml.set("world.allow-wither", "false");
			worldCfgYaml.set("world.allow-nether-portal", "true");
			worldCfgYaml.set("world.allow-tnt-explosion", "true");
			worldCfgYaml.set("general.plots-per-user", "1");
			worldCfgYaml.set("general.cost-claim", "0.00");
			
			try {
				worldCfgYaml.save(worldCfg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			PlotConfiguration.defaultConfigurationForWorldCfg(worldCfg);
		}
	}
		
	/**
	 * Cargar la configuración desde el archivo de configuración.
	 */
	public void loadConfiguration(){
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		File worldCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + id + "-cfg.yml");
		
		if(!worldCfg.exists()){
			generateDefaultConfiguration();
		}
		
		YamlConfiguration worldCfgYaml = YamlConfiguration.loadConfiguration(worldCfg);
		
		mob_spawn = worldCfgYaml.getString("world.mob-spawn", mob_spawn);
		animal_spawn = worldCfgYaml.getString("world.animal-spawn", animal_spawn);
		creeper_explosion = worldCfgYaml.getString("world.creeper-explosion", creeper_explosion);
		wither_explosion = worldCfgYaml.getString("world.wither-explosion", wither_explosion);
		allow_wither = worldCfgYaml.getString("world.allow-wither", allow_wither);
		allow_nether_portal = worldCfgYaml.getString("world.allow-nether-portal", allow_nether_portal);
		allow_tnt_explosion = worldCfgYaml.getString("world.allow-tnt-explosion", allow_tnt_explosion);
		plot_per_user = worldCfgYaml.getString("general.plots-per-user", plot_per_user);
		cost_claim = worldCfgYaml.getString("general.cost-claim", cost_claim);
	}
	
	/**
	 * Actualizar un parámetro de la configuración.
	 * @param key Configuración.
	 * @param value Valor.
	 */
	public void updateConfiguration(String key, Object value){
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		File worldCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + id + "-cfg.yml");
		
		if(!worldCfg.exists()){
			generateDefaultConfiguration();
		}
		
		YamlConfiguration worldCfgYaml = YamlConfiguration.loadConfiguration(worldCfg);
		
		worldCfgYaml.set(key, value);
		
		try {
			worldCfgYaml.save(worldCfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Configuración: mob-spawn<br>
	 * Ruta: world.mob-spawn<br>
	 * @return {@code true} si la configuración está activada (true).
	 */
	public boolean mobSpawn(){
		return (mob_spawn.equalsIgnoreCase("true")) ? true : false;
	}
	
	/**
	 * Configuración: mob-spawn<br>
	 * Ruta: world.mob-spawn<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void mobSpawn(boolean newVal){
		updateConfiguration("world.mob-spawn", newVal);
		mob_spawn = (newVal) ? "true" : "false";
	}
	
	/**
	 * Configuración: animal-spawn<br>
	 * Ruta: world.animal-spawn<br>
	 * @return {@code true} si la configuración está activada (true).
	 */
	public boolean animalSpawn(){
		return (animal_spawn.equalsIgnoreCase("true")) ? true : false;
	}
	
	/**
	 * Configuración: animal-spawn<br>
	 * Ruta: world.animal-spawn<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void animalSpawn(boolean newVal){
		updateConfiguration("world.animal-spawn", newVal);
		animal_spawn = (newVal) ? "true" : "false";
	}
	
	/**
	 * Configuración: creeper-explosion<br>
	 * Ruta: world.creeper-explosion<br>
	 * @return {@code true} si la configuración está activada (true).
	 */
	public boolean creeperExplosion(){
		return (creeper_explosion.equalsIgnoreCase("true")) ? true : false;
	}
	
	/**
	 * Configuración: creeper-explosion<br>
	 * Ruta: world.creeper-explosion<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void creeperExplosion(boolean newVal){
		updateConfiguration("world.creeper-explosion", newVal);
		creeper_explosion = (newVal) ? "true" : "false";
	}
	
	/**
	 * Configuración: wither-explosion<br>
	 * Ruta: world.wither-explosion<br>
	 * @return {@code true} si la configuración está activada (true).
	 */
	public boolean witherExplosion(){
		return (wither_explosion.equalsIgnoreCase("true")) ? true : false;
	}
	
	/**
	 * Configuración: wither-explosion<br>
	 * Ruta: world.wither-explosion<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void witherExplosion(boolean newVal){
		updateConfiguration("world.wither-explosion", newVal);
		wither_explosion = (newVal) ? "true" : "false";
	}
	
	/**
	 * Configuración: allow-wither<br>
	 * Ruta: world.allow-wither<br>
	 * @return {@code true} si la configuración está activada (true).
	 */
	public boolean allowWither(){
		return (allow_wither.equalsIgnoreCase("true")) ? true : false;
	}
	
	/**
	 * Configuración: allow-wither<br>
	 * Ruta: world.allow-wither<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void allowWither(boolean newVal){
		updateConfiguration("world.allow-wither", newVal);
		allow_wither = (newVal) ? "true" : "false";
	}
	
	/**
	 * Configuración: allow-nether-portal<br>
	 * Ruta: world.allow-nether-portal<br>
	 * @return {@code true} si la configuración está activada (true).
	 */
	public boolean allowNetherPortal(){
		return (allow_nether_portal.equalsIgnoreCase("true")) ? true : false;
	}
	
	/**
	 * Configuración: allow-nether-portal<br>
	 * Ruta: world.allow-nether-portal<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void allowNetherPortal(boolean newVal){
		updateConfiguration("world.allow-nether-portal", newVal);
		allow_nether_portal = (newVal) ? "true" : "false";
	}
	
	/**
	 * Configuración: allow-tnt<br>
	 * Ruta: world.allow-tnt<br>
	 * @return {@code true} si la configuración está activada (true).
	 */
	public boolean allowTNTExplosion(){
		return (allow_tnt_explosion.equalsIgnoreCase("true")) ? true : false;
	}
	
	/**
	 * Configuración: allow-tnt<br>
	 * Ruta: world.allow-tnt<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void allowTNTExplosion(boolean newVal){
		updateConfiguration("world.allow-tnt", newVal);
		allow_tnt_explosion = (newVal) ? "true" : "false";
	}
	
	/**
	 * Configuración: plots-per-user<br>
	 * Ruta: general.plots-per-user<br>
	 * @return cantidad de parcelas permitidas por usuario.
	 */
	public int plotsPerUser(){
		try{
			return Integer.parseInt(plot_per_user);
		}catch(NumberFormatException e){
			return 1;
		}
	}
	
	/**
	 * Configuración: plots-per-user<br>
	 * Ruta: general.plots-per-user<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void plotsPerUser(int newVal){
		updateConfiguration("general.plots-per-user", newVal);
		plot_per_user = String.valueOf(newVal);
	}
	
	/**
	 * Configuración: cost-claim<br>
	 * Ruta: general.cost-claim<br>
	 * @return costo para claimear una parcela.
	 */
	public BigDecimal claimCost(){
		try{
			return new BigDecimal(cost_claim);
		}catch(NumberFormatException e){
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * Configuración: cost-claim<br>
	 * Ruta: general.cost-claim<br>
	 * @param newVal Nuevo valor para esta configuración.
	 */
	public void claimCost(BigDecimal newVal){
		updateConfiguration("general.cost-claim", newVal.toPlainString());
		cost_claim = String.valueOf(newVal);
	}
	
}
