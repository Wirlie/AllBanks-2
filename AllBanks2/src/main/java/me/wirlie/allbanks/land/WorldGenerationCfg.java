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

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;

import me.wirlie.allbanks.utils.FileDirectory;

/**
 * @author Wirlie
 *
 */
public class WorldGenerationCfg {
	public static int world_height_def = 60;
	public int world_height = world_height_def;
	
	public static int plot_size_def = 50;
	public int plot_size = plot_size_def;
	
	public static int road_size_def = 10;
	public int road_size = road_size_def;
	
	public static Material road_material_def = Material.WOOD;
	public Material road_material = road_material_def;
	
	public static boolean coal_ore_def = true;
	public boolean coal_ore = coal_ore_def;
	
	public static boolean iron_ore_def = true;
	public boolean iron_ore = iron_ore_def;
	
	public static boolean emerald_ore_def = true;
	public boolean emerald_ore = emerald_ore_def;
	
	public static boolean diamond_ore_def = true;
	public boolean diamond_ore = diamond_ore_def;
	
	public static boolean redstone_ore_def = true;
	public boolean redstone_ore = redstone_ore_def;
	
	public static boolean gold_ore_def = true;
	public boolean gold_ore = gold_ore_def;
	
	public static boolean lapis_ore_def = true;
	public boolean lapis_ore = lapis_ore_def;
	
	public static boolean generate_grass_def = true;
	public boolean generate_grass = generate_grass_def;
	
	public static boolean generate_tree_def = true;
	public boolean generate_tree = generate_tree_def;
	
	public static TreeType tree_type_1_def = TreeType.TREE;
	public TreeType tree_type_1 = tree_type_1_def;
	
	public static TreeType tree_type_2_def = TreeType.BIRCH;
	public TreeType tree_type_2 = tree_type_2_def;
	
	public static Biome default_biome_def = Biome.PLAINS;
	public Biome default_biome = default_biome_def;
	
	String world_id = "";
	
	public static void removeTemporalConfiguration(String worldID){
		File worldFileCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + "world-" + worldID + "-gen-config.yml");
		
		worldFileCfg.delete();
	}
	
	public WorldGenerationCfg(String worldID){
		this.world_id = worldID;
		
		File worldFileCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + "world-" + worldID + "-gen-config.yml");
		
		if(!worldFileCfg.exists())
			try {
				worldFileCfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(worldFileCfg);
		
		world_height = cfg.getInt("world-block-height", world_height);
		plot_size = cfg.getInt("plot_size", plot_size);
		road_size = cfg.getInt("road_size", road_size);
		
		Material road_material_try = Material.getMaterial(cfg.getString("road-material", "WOOD"));
		if(road_material_try  != null) road_material = road_material_try;
		
		coal_ore = cfg.getBoolean("generate-ores.coal", coal_ore);
		iron_ore = cfg.getBoolean("generate-ores.iron", iron_ore);
		emerald_ore = cfg.getBoolean("generate-ores.emerald", emerald_ore);
		diamond_ore = cfg.getBoolean("generate-ores.diamond", diamond_ore);
		redstone_ore = cfg.getBoolean("generate-ores.redstone", redstone_ore);
		gold_ore = cfg.getBoolean("generate-ores.gold", gold_ore);
		lapis_ore = cfg.getBoolean("generate-ores.lapis", lapis_ore);
		
		generate_grass = cfg.getBoolean("generate-grass", generate_grass);
		
		generate_tree = cfg.getBoolean("generate-tree-type.enable", generate_tree);
		
		TreeType tryType1 = TreeType.valueOf(cfg.getString("generate-tree-type.type-1", "TREE"));
		TreeType tryType2 = TreeType.valueOf(cfg.getString("generate-tree-type.type-2", "BIRCH"));
	
		if(tryType1 != null) tree_type_1 = tryType1;
		if(tryType2 != null) tree_type_2 = tryType2;
		
		Biome tryBiome = Biome.valueOf(cfg.getString("default-biome", "PLAINS"));

		if(tryBiome != null) default_biome = tryBiome;
		
	}
	
	public static boolean generatorConfigurationFileExists(String worldID){
		File worldFileCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + "world-" + worldID + "-gen-config.yml");
		return worldFileCfg.exists();
	}
	
	public static String makeNewDefaultGeneratorConfigurationFile(String worldID){
		File worldFileCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + "world-" + worldID + "-gen-config.yml");
		
		try {
			worldFileCfg.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		YamlConfiguration worldCfg = YamlConfiguration.loadConfiguration(worldFileCfg);
		
		worldCfg.set("world-block-height", world_height_def);
		worldCfg.set("plot_size", plot_size_def);
		worldCfg.set("road_size", road_size_def);
		worldCfg.set("road-material", road_material_def.toString());
		worldCfg.set("generate-ores.coal", coal_ore_def);
		worldCfg.set("generate-ores.iron", iron_ore_def);
		worldCfg.set("generate-ores.emerald", emerald_ore_def);
		worldCfg.set("generate-ores.diamond", diamond_ore_def);
		worldCfg.set("generate-ores.redstone", redstone_ore_def);
		worldCfg.set("generate-ores.gold", gold_ore_def);
		worldCfg.set("generate-ores.lapis", lapis_ore_def);
		worldCfg.set("generate-grass", generate_grass_def);
		worldCfg.set("generate-tree-type.enable", generate_tree_def);
		worldCfg.set("generate-tree-type.type-1", tree_type_1_def.toString());
		worldCfg.set("generate-tree-type.type-2", tree_type_2_def.toString());
		worldCfg.set("default-biome", default_biome_def.toString());
		
		try {
			worldCfg.save(worldFileCfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return worldFileCfg.getAbsolutePath();
	}
}
