package me.wirlie.allbanks.land;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import me.wirlie.allbanks.utils.ConfigurationUtil;

public class AllBanksLandWorld {
	
	public World bukkitWorld = null;
	public String worldName = null;
	
	public int plot_cfg_plots_per_player = 1;
	public int plot_cfg_claim_price = 0;
	public int plot_cfg_expire_time = 60 * 60 * 24 * 7; //7 días
	public boolean plot_cfg_auto_regenerate_plot_disposed = true;
	public boolean plot_cfg_prevent_tnt_explosion = true;
	public boolean plot_cfg_prevent_creeper_explosion = true;
	public boolean plot_cfg_prevent_other_explosion = true;
	public boolean plot_cfg_prevent_enderman_grief = true;
	public boolean plot_cfg_prevent_nether_portal = true;
	public boolean plot_cfg_prevent_water_flow = true;
	public boolean plot_cfg_prevent_lava_flow = true;
	public boolean plot_cfg_prevent_wither_spawn = true;
	
	public boolean plot_cfg_tpakill_prevent_tpa_kill = true;
	public int plot_cfg_tpakill_give_inmunity_time = 0;
	public boolean plot_cfg_tpakill_player_inmunity_lava = true;
	public boolean plot_cfg_tpakill_player_inmunity_fall = true;
	public boolean plot_cfg_tpakill_player_inmunity_explosion = true;
	public boolean plot_cfg_tpakill_player_inmunity_pvp = true;
	public boolean plot_cfg_tpakill_player_inmunity_drown = true;
	
	public boolean plot_cfg_user_permissions_sell_plot = false;
	public boolean plot_cfg_user_permissions_buy_plot = true;
	public boolean plot_cfg_user_permissions_dispose_plot = true;
	public boolean plot_cfg_user_permissions_claim_plot = true;
	public boolean plot_cfg_user_permissions_add_friends = true;
	public boolean plot_cfg_user_permissions_deny_users = true;
	public boolean plot_cfg_user_permissions_plot_tp = true;
	public boolean plot_cfg_user_permissions_allbanks_shops = true;
	public boolean plot_cfg_user_permissions_greeting_message = true;
	public boolean plot_cfg_user_permissions_farewell_message = true;
	
	public boolean plot_cfg_permissions_use_anvil = false;
	public boolean plot_cfg_permissions_use_enchantment_table = true;
	public boolean plot_cfg_permissions_use_work_bench = true;
	public boolean plot_cfg_permissions_use_doors = true;
	public boolean plot_cfg_permissions_use_fence_door = false;
	public boolean plot_cfg_permissions_use_redstone_button = false;
	public boolean plot_cfg_permissions_use_lever = false;
	public boolean plot_cfg_permissions_use_pressure_plate = false;
	public boolean plot_cfg_permissions_use_other = true;
	
	public boolean plot_cfg_permissions_pvp = false;
	public boolean plot_cfg_permissions_open_chest = false;
	public boolean plot_cfg_permissions_drop_items = true;
	public boolean plot_cfg_permissions_horse_riding = false;
	public boolean plot_cfg_permissions_kill_animals = false;
	
	public int world_cfg_plot_plot_size = 50;
	public int world_cfg_plot_road_size = 7;
	
	public boolean world_cfg_generation_trees = false;
	public boolean world_cfg_generation_grass = false;
	public boolean world_cfg_generation_ores = false;
	
	public boolean world_cfg_animal_spawn = true;
	public boolean world_cfg_mob_spawn = false;
	
	public boolean world_cfg_world_limitations_enable_limit = false;
	public int world_cfg_world_limitations_world_limit = 5000;
	public int world_cfg_world_max_build_height = 256;
	
	public AllBanksLandWorld(File worldFile) {
		loadConfiguration(worldFile);
	}

	private void loadConfiguration(File worldFile){
		
		worldName = worldFile.getName().replace(".yml", "");
		
		if(!worldFile.exists()){
			//No tiene caso leer toda la configuración de un archivo que no existe
			return;
		}
		
		bukkitWorld = Bukkit.getWorld(worldName);
		
		YamlConfiguration worldYaml = YamlConfiguration.loadConfiguration(worldFile);
		
		//Comenzar a leer todos los valores del archivo Yaml...
		plot_cfg_plots_per_player = worldYaml.getInt("default-plots-per-player", 1);
		plot_cfg_claim_price = worldYaml.getInt("claim-price", 0);
		plot_cfg_expire_time = ConfigurationUtil.convertTimeValueToSeconds(worldYaml.getString("expire-abandoned-plots", "14 days"));
	}
	
}
