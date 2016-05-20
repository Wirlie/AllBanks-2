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
package me.wirlie.allbanks.land.generator;

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
	private static int world_height_def = 60;
	public int world_height = world_height_def;
	
	private static int plot_size_def = 50;
	public int plot_size = plot_size_def;
	
	private static int road_size_def = 10;
	public int road_size = road_size_def;
	
	private static Material road_material_def = Material.WOOD;
	public Material road_material = road_material_def;
	
	private static boolean coal_ore_def = true;
	public boolean coal_ore = coal_ore_def;
	
	private static boolean iron_ore_def = true;
	public boolean iron_ore = iron_ore_def;
	
	private static boolean emerald_ore_def = true;
	public boolean emerald_ore = emerald_ore_def;
	
	private static boolean diamond_ore_def = true;
	public boolean diamond_ore = diamond_ore_def;
	
	private static boolean redstone_ore_def = true;
	public boolean redstone_ore = redstone_ore_def;
	
	private static boolean gold_ore_def = true;
	public boolean gold_ore = gold_ore_def;
	
	private static boolean lapis_ore_def = true;
	public boolean lapis_ore = lapis_ore_def;
	
	private static boolean generate_grass_def = true;
	public boolean generate_grass = generate_grass_def;
	
	private static boolean generate_tree_def = true;
	public boolean generate_tree = generate_tree_def;
	
	private static TreeType tree_type_1_def = TreeType.TREE;
	public TreeType tree_type_1 = tree_type_1_def;
	
	private static TreeType tree_type_2_def = TreeType.BIRCH;
	public TreeType tree_type_2 = tree_type_2_def;
	
	private static Biome default_biome_def = Biome.PLAINS;
	public Biome default_biome = default_biome_def;
	
	String world_id = "";
	
	//Descriptores de altura máxima para la generacion de un mineral
	private static int coal_ore_max_y_def = 128;
	public int coal_ore_max_y = coal_ore_max_y_def;

	private static int iron_ore_max_y_def = 64;
	public int iron_ore_max_y = iron_ore_max_y_def;
	
	private static int lapis_ore_max_y_def = 23;
	public int lapis_ore_max_y = lapis_ore_max_y_def;
	
	private static int gold_ore_max_y_def = 60;
	public int gold_ore_max_y = gold_ore_max_y_def;
	
	private static int redstone_ore_max_y_def = 15;
	public int redstone_ore_max_y = redstone_ore_max_y_def;
	
	private static int diamond_ore_max_y_def = 15;
	public int diamond_ore_max_y = diamond_ore_max_y_def;
	
	private static int emerald_ore_max_y_def = 30;
	public int emerald_ore_max_y = emerald_ore_max_y_def;
	
	//Descriptores de minimo y máximo de un mineral
	private static int coal_rand_min_def = 6;
	private static int coal_rand_max_def = 10;
	public int coal_rand_min = coal_rand_min_def;
	public int coal_rand_max = coal_rand_max_def;
	
	private static int iron_rand_min_def = 5;
	private static int iron_rand_max_def = 8;
	public int iron_rand_min = iron_rand_min_def;
	public int iron_rand_max = iron_rand_max_def;

	private static int lapis_rand_min_def = 0;
	private static int lapis_rand_max_def = 4;
	public int lapis_rand_min = lapis_rand_min_def;
	public int lapis_rand_max = lapis_rand_max_def;

	private static int gold_rand_min_def = 1;
	private static int gold_rand_max_def = 3;
	public int gold_rand_min = gold_rand_min_def;
	public int gold_rand_max = gold_rand_max_def;
	
	private static int redstone_rand_min_def = 2;
	private static int redstone_rand_max_def = 4;
	public int redstone_rand_min = redstone_rand_min_def;
	public int redstone_rand_max = redstone_rand_max_def;
	
	private static int diamond_rand_min_def = 0;
	private static int diamond_rand_max_def = 2;
	public int diamond_rand_min = diamond_rand_min_def;
	public int diamond_rand_max = diamond_rand_max_def;
	
	private static int emerald_rand_min_def = 0;
	private static int emerald_rand_max_def = 2;
	public int emerald_rand_min = emerald_rand_min_def;
	public int emerald_rand_max = emerald_rand_max_def;
	
	public static void removeTemporalConfiguration(String worldID){
		File worldFileCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + "world-" + worldID + "-gen-config.yml");
		
		worldFileCfg.delete();
	}
	
	public WorldGenerationCfg(String worldID){
		this.world_id = worldID;
		
		File worldFileCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + "world-" + worldID + "-gen-config.yml");
		
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(worldFileCfg);
		
		world_height = cfg.getInt("world-block-height", world_height);
		plot_size = cfg.getInt("plot_size", plot_size);
		road_size = cfg.getInt("road_size", road_size);
		
		Material road_material_try = Material.getMaterial(cfg.getString("road-material", "WOOD"));
		if(road_material_try  != null) road_material = road_material_try;
		
		//Generar o no generar minerales.
		coal_ore = cfg.getBoolean("generate-ores.coal.generate", coal_ore);
		iron_ore = cfg.getBoolean("generate-ores.iron.generate", iron_ore);
		emerald_ore = cfg.getBoolean("generate-ores.emerald.generate", emerald_ore);
		diamond_ore = cfg.getBoolean("generate-ores.diamond.generate", diamond_ore);
		redstone_ore = cfg.getBoolean("generate-ores.redstone.generate", redstone_ore);
		gold_ore = cfg.getBoolean("generate-ores.gold.generate", gold_ore);
		lapis_ore = cfg.getBoolean("generate-ores.lapis.generate", lapis_ore);
		
		//altura (y) de los minerales
		coal_ore_max_y = cfg.getInt("generate-ores.coal.max-y", coal_ore_max_y);
		iron_ore_max_y = cfg.getInt("generate-ores.iron.max-y", iron_ore_max_y);
		emerald_ore_max_y = cfg.getInt("generate-ores.emerald.max-y", emerald_ore_max_y);
		diamond_ore_max_y = cfg.getInt("generate-ores.diamond.max-y", diamond_ore_max_y);
		redstone_ore_max_y = cfg.getInt("generate-ores.redstone.max-y", redstone_ore_max_y);
		gold_ore_max_y = cfg.getInt("generate-ores.gold.max-y", gold_ore_max_y);
		lapis_ore_max_y = cfg.getInt("generate-ores.lapis.max-y", lapis_ore_max_y);
		
		//aleatoriedad en los minerales
		coal_rand_min = cfg.getInt("generate-ores.coal.rand_min", coal_rand_min);
		coal_rand_max = cfg.getInt("generate-ores.coal.rand_max", coal_rand_max);
		iron_rand_min = cfg.getInt("generate-ores.iron.rand_min", iron_rand_min);
		iron_rand_max = cfg.getInt("generate-ores.iron.rand_max", iron_rand_max);
		emerald_rand_min = cfg.getInt("generate-ores.emerald.rand_min", emerald_rand_min);
		emerald_rand_max = cfg.getInt("generate-ores.emerald.rand_max", emerald_rand_max);
		diamond_rand_min = cfg.getInt("generate-ores.diamond.rand_min", diamond_rand_min);
		diamond_rand_max = cfg.getInt("generate-ores.diamond.rand_max", diamond_rand_max);
		redstone_rand_min = cfg.getInt("generate-ores.redstone.rand_min", redstone_rand_min);
		redstone_rand_max = cfg.getInt("generate-ores.redstone.rand_max", redstone_rand_max);
		gold_rand_min = cfg.getInt("generate-ores.gold.rand_min", gold_rand_min);
		gold_rand_max = cfg.getInt("generate-ores.gold.rand_max", gold_rand_max);
		lapis_rand_min = cfg.getInt("generate-ores.lapis.rand_min", lapis_rand_min);
		lapis_rand_max = cfg.getInt("generate-ores.lapis.rand_max", lapis_rand_max);
		
		//Arboles y hierba
		generate_grass = cfg.getBoolean("generate-grass", generate_grass);
		generate_tree = cfg.getBoolean("generate-tree-type.enable", generate_tree);
		//Tipo de árboles
		//TODO Aqui debería ser mas flexible a un listado
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
		
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
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
		//Generación de minerales
		worldCfg.set("generate-ores.coal.generate", coal_ore_def);
		worldCfg.set("generate-ores.iron.generate", iron_ore_def);
		worldCfg.set("generate-ores.emerald.generate", emerald_ore_def);
		worldCfg.set("generate-ores.diamond.generate", diamond_ore_def);
		worldCfg.set("generate-ores.redstone.generate", redstone_ore_def);
		worldCfg.set("generate-ores.gold.generate", gold_ore_def);
		worldCfg.set("generate-ores.lapis.generate", lapis_ore_def);
		//Altura de minerales
		worldCfg.set("generate-ores.coal.max-y", coal_ore_max_y_def);
		worldCfg.set("generate-ores.iron.max-y", iron_ore_max_y_def);
		worldCfg.set("generate-ores.emerald.max-y", emerald_ore_max_y_def);
		worldCfg.set("generate-ores.diamond.max-y", diamond_ore_max_y_def);
		worldCfg.set("generate-ores.redstone.max-y", redstone_ore_max_y_def);
		worldCfg.set("generate-ores.gold.max-y", gold_ore_max_y_def);
		worldCfg.set("generate-ores.lapis.max-y", lapis_ore_max_y_def);
		//Minerales aleatorios, parámetros
		worldCfg.set("generate-ores.coal.rand_min", coal_rand_min_def);
		worldCfg.set("generate-ores.coal.rand_max", coal_rand_max_def);
		worldCfg.set("generate-ores.iron.rand_min", iron_rand_min_def);
		worldCfg.set("generate-ores.iron.rand_max", iron_rand_max_def);
		worldCfg.set("generate-ores.emerald.rand_min", emerald_rand_min_def);
		worldCfg.set("generate-ores.emerald.rand_max", emerald_rand_max_def);
		worldCfg.set("generate-ores.diamond.rand_min", diamond_rand_min_def);
		worldCfg.set("generate-ores.diamond.rand_max", diamond_rand_max_def);
		worldCfg.set("generate-ores.redstone.rand_min", redstone_rand_min_def);
		worldCfg.set("generate-ores.redstone.rand_max", redstone_rand_max_def);
		worldCfg.set("generate-ores.gold.rand_min", gold_rand_min_def);
		worldCfg.set("generate-ores.gold.rand_max", gold_rand_max_def);
		worldCfg.set("generate-ores.lapis.rand_min", lapis_rand_min_def);
		worldCfg.set("generate-ores.lapis.rand_max", lapis_rand_max_def);
		//Otros
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
