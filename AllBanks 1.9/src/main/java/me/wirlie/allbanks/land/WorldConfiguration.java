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
package me.wirlie.allbanks.land;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import me.wirlie.allbanks.utils.FileDirectory;

/**
 * @author josue
 *
 */
public class WorldConfiguration {
	
	String id;
	
	boolean mob_spawn = true;
	boolean animal_spawn = true;
	boolean creeper_explosion = false;
	boolean wither_explosion = false;
	boolean allow_wither = false;
	boolean allow_nether_portal = true;
	boolean allow_tnt = true;
	
	public WorldConfiguration(String worldID){
		id = worldID;
		
		generateDefaultConfiguration();
		loadConfiguration();
	}
	
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
			worldCfgYaml.set("world.allow-tnt", "true");
			
			try {
				worldCfgYaml.save(worldCfg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
	public void loadConfiguration(){
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		File worldCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + id + "-cfg.yml");
		
		if(!worldCfg.exists()){
			try {
				worldCfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		YamlConfiguration worldCfgYaml = YamlConfiguration.loadConfiguration(worldCfg);
		
		mob_spawn = worldCfgYaml.getBoolean("world.mob-spawn", mob_spawn);
		animal_spawn = worldCfgYaml.getBoolean("world.animal-spawn", animal_spawn);
		creeper_explosion = worldCfgYaml.getBoolean("world.creeper-explosion", creeper_explosion);
		wither_explosion = worldCfgYaml.getBoolean("world.wither-explosion", wither_explosion);
		allow_wither = worldCfgYaml.getBoolean("world.allow-wither", allow_wither);
		allow_nether_portal = worldCfgYaml.getBoolean("world.allow-nether-portal", allow_nether_portal);
		allow_tnt = worldCfgYaml.getBoolean("world.allow-tnt", allow_tnt);
	}
	
	public void updateConfiguration(String key, Object value){
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		File worldCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + id + "-cfg.yml");
		
		if(!worldCfg.exists()){
			try {
				worldCfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		YamlConfiguration worldCfgYaml = YamlConfiguration.loadConfiguration(worldCfg);
		
		worldCfgYaml.set(key, value);
		
		try {
			worldCfgYaml.save(worldCfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean mobSpawn(){
		return mob_spawn;
	}
	
	public void mobSpawn(boolean newVal){
		updateConfiguration("world.mob-spawn", newVal);
		mob_spawn = newVal;
	}
	
	public boolean animalSpawn(){
		return animal_spawn;
	}
	
	public void animalSpawn(boolean newVal){
		updateConfiguration("world.animal-spawn", newVal);
		animal_spawn = newVal;
	}
	
	public boolean creeperExplosion(){
		return creeper_explosion;
	}
	
	public void creeperExplosion(boolean newVal){
		updateConfiguration("world.creeper-explosion", newVal);
		creeper_explosion = newVal;
	}
	
	public boolean witherExplosion(){
		return wither_explosion;
	}
	
	public void witherExplosion(boolean newVal){
		updateConfiguration("world.wither-explosion", newVal);
		wither_explosion = newVal;
	}
	
	public boolean allowWither(){
		return allow_wither;
	}
	
	public void allowWither(boolean newVal){
		updateConfiguration("world.allow-wither", newVal);
		allow_wither = newVal;
	}
	
	public boolean allowNetherPortal(){
		return allow_nether_portal;
	}
	
	public void allowNetherPortal(boolean newVal){
		updateConfiguration("world.allow-nether-portal", newVal);
		allow_nether_portal = newVal;
	}
	
	public boolean allowTNT(){
		return allow_tnt;
	}
	
	public void allowTNT(boolean newVal){
		updateConfiguration("world.allow-tnt", newVal);
		allow_tnt = newVal;
	}
	
}
