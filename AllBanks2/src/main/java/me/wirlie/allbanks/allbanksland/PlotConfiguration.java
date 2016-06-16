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
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.utils.ChatUtil;
import me.wirlie.allbanks.utils.DBUtil;
import me.wirlie.allbanks.utils.FileDirectory;
import me.wirlie.allbanks.utils.Util;

/**
 * @author josue
 *
 */
public class PlotConfiguration {
	
	private HashMap<String, String> plot_cfg = null;
	private AllBanksPlot plot;
	
	/**
	 * Representación de la configuración de una parcela.
	 * @param plot Parcela
	 * @param JSONConfiguration Configuración de la parcela en formato JSON.
	 */
	public PlotConfiguration(AllBanksPlot plot, String JSONConfiguration){
		Type type = new TypeToken<HashMap<String, String>>(){}.getType();
		plot_cfg = new Gson().fromJson(JSONConfiguration, type);
		if(plot_cfg == null) plot_cfg = new HashMap<String, String>();
		this.plot = plot;
		
		loadWorldPlotConfiguration();
	}
	
	/**
	 * Cargar el arhico de configuración del mundo y almacenarla en la instancia.
	 */
	public void loadWorldPlotConfiguration(){
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		File worldCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + plot.getAllBanksWorld().getID() + "-cfg.yml");
		
		if(!worldCfg.exists()){
			try {
				worldCfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * Crear una configuración por defecto.
	 * @param worldID El ID del mundo.
	 * @return devuelve una cadena formateada en JSON conteniendo la representación de un {@code HashMap<String, String>} con todas las configuraciones de una parcela.
	 */
	public static String defaultConfiguration(String worldID){
		
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		File worldCfg = new File(FileDirectory.WORLDS_DATA_FOLDER + File.separator + worldID + "-cfg.yml");
		
		if(!worldCfg.exists()){
			try {
				worldCfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		YamlConfiguration worldCfgYaml = YamlConfiguration.loadConfiguration(worldCfg);
		
		HashMap<String, String> plot_cfg = new HashMap<String, String>();
		plot_cfg.put("fire-spread", worldCfgYaml.getString("plot-default-cfg.fire-spread", "false"));
		plot_cfg.put("explosions", worldCfgYaml.getString("plot-default-cfg.explosions", "false"));
		plot_cfg.put("mobs", worldCfgYaml.getString("plot-default-cfg.mobs", "false"));
		plot_cfg.put("pvp", worldCfgYaml.getString("plot-default-cfg.pvp", "false"));
		plot_cfg.put("lava-flow", worldCfgYaml.getString("plot-default-cfg.lava-flow", "false"));
		plot_cfg.put("water-flow", worldCfgYaml.getString("plot-default-cfg.water-flow", "true"));
		plot_cfg.put("use-door", worldCfgYaml.getString("plot-default-cfg.use-door", "true"));
		plot_cfg.put("use-anvil", worldCfgYaml.getString("plot-default-cfg.use-anvil", "true"));
		plot_cfg.put("use-workbench", worldCfgYaml.getString("plot-default-cfg.use-workbench", "true"));
		plot_cfg.put("use-fence-gate", worldCfgYaml.getString("plot-default-cfg.use-fence-gate", "true"));
		plot_cfg.put("use-enchantment-table", worldCfgYaml.getString("plot-default-cfg.use-enchantment-table", "true"));
		plot_cfg.put("use-lever", worldCfgYaml.getString("plot-default-cfg.use-lever", "false"));
		plot_cfg.put("use-button", worldCfgYaml.getString("plot-default-cfg.use-button", "false"));
		plot_cfg.put("use-pressure-plate", worldCfgYaml.getString("plot-default-cfg.use-button", "false"));
		plot_cfg.put("drop-item", worldCfgYaml.getString("plot-default-cfg.drop-item", "true"));
		plot_cfg.put("msg-greeting", worldCfgYaml.getString("plot-default-cfg.msg-greeting", Translation.get(StringsID.PLOT_GREETING_FORMAT, false)[0]));
		plot_cfg.put("msg-farewell", worldCfgYaml.getString("plot-default-cfg.msg-farewell", Translation.get(StringsID.PLOT_FAREWELL_FORMAT, false)[0]));
		plot_cfg.put("plot-spawn-loc", "");
		plot_cfg.put("shop-spawn-loc", "");
		plot_cfg.put("shop-spawn-alias", "");
		plot_cfg.put("shop-spawn-visibility", worldCfgYaml.getString("plot-default-cfg.shop-spawn-visibility", "PUBLIC"));
		plot_cfg.put("plot-friends", "");
		plot_cfg.put("plot-player-deny", "");
		plot_cfg.put("allow-entry", worldCfgYaml.getString("plot-default-cfg.allow-entry", "true"));
		plot_cfg.put("allow-plot-teleport", worldCfgYaml.getString("plot-default-cfg.allow-plot-teleport", "true"));
		
		return new Gson().toJson(plot_cfg);
	}
	
	/**
	 * Establecer o actualizar una configuración de esta parcela.
	 * @param key Configuración
	 * @param value Nuevo valor
	 */
	public void setPlotConfiguration(String key, String value){
		plot_cfg.put(key, value);
		
		//Actualizar configuración en la base de datos
				Statement stm = null;
				
				try{
					stm = AllBanks.getSQLConnection(DBUtil.ALLBANKSLAND_DATABASE_CONNECTION_NAME).createStatement();
					stm.executeUpdate("UPDATE world_plots SET plot_config = '" + toJSON() + "' WHERE world_id = '" + plot.getAllBanksWorld().getID() + "' AND plot_coord_X = '" + plot.getPlotX() + "' AND plot_coord_Z = '" + plot.getPlotZ() + "'");
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
	
	/**
	 * Añadir a un amigo a esta parcela.
	 * @param friend Nombre del jugador a añadir.
	 */
	public void addFriend(String friend){
		
		friend = friend.toLowerCase();
		
		List<String> currentFriends = getFriends();
		if(!currentFriends.contains(friend)){
			currentFriends.add(friend);
			
			String listStr = "";
			
			for(String s: currentFriends){
				listStr += s + ",|>,";
			}
			
			setPlotConfiguration("plot-friends", listStr);
		}
	}
	
	/**
	 * Remover a un amigo de esta parcela.
	 * @param friend Nombre del jugador a remover.
	 */
	public void removeFriend(String friend){
		
		friend = friend.toLowerCase();
		
		List<String> currentFriends = getFriends();
		if(currentFriends.contains(friend)){
			currentFriends.remove(friend);
			
			String listStr = "";
			
			for(String s: currentFriends){
				listStr += s + ",|>,";
			}
			
			setPlotConfiguration("plot-friends", listStr);
		}
	}
	
	/**
	 * Establecer a un jugador como denegado, impidiendo que este pueda entrar.
	 * @param playerName Jugador a denegar.
	 */
	public void setDeny(String playerName){
		
		playerName = playerName.toLowerCase();
		
		List<String> currentDeny = getDenyPlayers();
		if(!currentDeny.contains(playerName)){
			currentDeny.add(playerName);
			
			String listStr = "";
			
			for(String s: currentDeny){
				listStr += s + ",|>,";
			}
			
			setPlotConfiguration("plot-player-deny", listStr);
		}
	}
	
	/**
	 * Quitar a un jugador de la lista de denegados.
	 * @param playerName Jugador a quitar del listado.
	 */
	public void setUndeny(String playerName){
		
		playerName = playerName.toLowerCase();
		
		List<String> currentDeny = getDenyPlayers();
		if(currentDeny.contains(playerName)){
			currentDeny.remove(playerName);
			
			String listStr = "";
			
			for(String s: currentDeny){
				listStr += s + ",|>,";
			}
			
			setPlotConfiguration("plot-player-deny", listStr);
		}
	}
	
	/**
	 * Obtiene todos los jugadores que están en la lista de denegados.
	 * @return Listado con todos los jugadores denegados en esta parcela.
	 */
	public List<String> getDenyPlayers(){
		if(plot_cfg.containsKey("plot-player-deny")){
			String listStr = plot_cfg.get("plot-player-deny");
			List<String> returnList = new ArrayList<String>();
			
			for(String s : listStr.split(",|>,")){
				if(s.equalsIgnoreCase("|") || s.equalsIgnoreCase("")) continue;
				returnList.add(s);
			}
			
			return returnList;
		}else{
			return new ArrayList<String>();
		}
	}
	
	/**
	 * Convertir la configuración de la parcela en una cadena con formato JSON
	 * @return Cadena de texto en formato JSON.
	 */
	public String toJSON(){
		return new Gson().toJson(plot_cfg);
	}
	
	/**
	 * Configuración: fire-spread | esparción del fuego.
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean fireSpread(){
		if(plot_cfg.containsKey("fire-spread")){
			if(plot_cfg.get("fire-spread").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: explosions | explosiones en la parcela
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean explosions(){
		if(plot_cfg.containsKey("explosions")){
			if(plot_cfg.get("explosions").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: mobs | mounstros
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean mobs(){
		if(plot_cfg.containsKey("mobs")){
			if(plot_cfg.get("mobs").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: pvp
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean pvp(){
		if(plot_cfg.containsKey("pvp")){
			if(plot_cfg.get("pvp").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: lava-flow | flujo de lava
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean lavaFlow(){
		if(plot_cfg.containsKey("lava-flow")){
			if(plot_cfg.get("lava-flow").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: water-flow | flujo de agua
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean waterFlow(){
		if(plot_cfg.containsKey("water-flow")){
			if(plot_cfg.get("water-flow").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * Configuración: use-door | usar puertas
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean useDoor(){
		if(plot_cfg.containsKey("use-door")){
			if(plot_cfg.get("use-door").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * Configuración: use-pressure-plate | usar placas de presión
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean usePressurePlate(){
		if(plot_cfg.containsKey("use-pressure-plate")){
			if(plot_cfg.get("use-pressure-plate").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * Configuración: use-anvil | usar el yunque
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean useAnvil(){
		if(plot_cfg.containsKey("use-anvil")){
			if(plot_cfg.get("use-anvil").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: use-workbench | usar la mesa de trabajo
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean useWorkbench(){
		if(plot_cfg.containsKey("use-workbench")){
			if(plot_cfg.get("use-workbench").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * Configuración: use-fence-gate | usar puertas de corral
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean useFenceGate(){
		if(plot_cfg.containsKey("use-fence-gate")){
			if(plot_cfg.get("use-fence-gate").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: use-enchantment-table | usar tabla de encantamiento
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean useEnchantmentTable(){
		if(plot_cfg.containsKey("use-enchantment-table")){
			if(plot_cfg.get("use-enchantment-table").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * Configuración: use-lever | usar palanca
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean useLever(){
		if(plot_cfg.containsKey("use-lever")){
			if(plot_cfg.get("use-lever").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: use-button | usar botones
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean useButton(){
		if(plot_cfg.containsKey("use-button")){
			if(plot_cfg.get("use-button").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Configuración: drop-item | permitir soltar objetos
	 * @return {@code true} si la configuración tiene un valor true.
	 */
	public boolean dropItem(){
		if(plot_cfg.containsKey("drop-item")){
			if(plot_cfg.get("drop-item").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * Configuración: msg-greeting | mensaje de entrada
	 * @return Cadena de texto conteniendo el mensaje de entrada
	 */
	public String greetingMessage(){
		if(plot_cfg.containsKey("msg-greeting")){
			return ChatUtil.replaceChatFormat(plot_cfg.get("msg-greeting").replace("§", "&"));
		}else{
			return null;
		}
	}
	
	/**
	 * Configuración: msg-farewell | mensaje de salida
	 * @return Cadena de texto conteniendo el mensaje de salida
	 */
	public String farewellMessage(){
		if(plot_cfg.containsKey("msg-farewell")){
			return ChatUtil.replaceChatFormat(plot_cfg.get("msg-farewell").replace("§", "&"));
		}else{
			return null;
		}
	}
	
	/**
	 * Configuración: plot-spawn-loc | la localización de aparición de la parcela
	 * @return {@link Location} localización.
	 */
	public Location plotSpawnLoc(){
		if(plot_cfg.containsKey("plot-spawn-loc")){
			String strLoc = plot_cfg.get("plot-spawn-loc");
			if(strLoc == null || strLoc.equalsIgnoreCase("")){
				//Primer punto
				return plot.getFirstBound();
			}
			
			return Util.convertStringToLocation(strLoc);
		}else{
			return null;
		}
	}
	
	/**
	 * Establecer la localización de aparición de la parcela.
	 * @param loc {@link Location} localización.
	 */
	public void setPlotSpawnLocation(Location loc) {
		setPlotConfiguration("plot-spawn-loc", Util.convertLocationToString(loc, false));
	}
	
	/**
	 * No implementado
	 * @return {@link Location} localización del spawn de la tienda.
	 */
	public Location shopSpawnLoc(){
		if(plot_cfg.containsKey("shop-spawn-loc")){
			String strLoc = plot_cfg.get("shop-spawn-loc");
			if(strLoc == null || strLoc.equalsIgnoreCase("")){
				//Primer punto
				return plot.getFirstBound();
			}
			
			return Util.convertStringToLocation(strLoc);
		}else{
			return null;
		}
	}
	
	/**
	 * No implementado, establecer la localización del spawn de la tienda.
	 * @param loc {@link Location} localización
	 */
	public void setShopSpawnLocation(Location loc) {
		setPlotConfiguration("shop-spawn-loc", Util.convertLocationToString(loc, false));
	}
	
	
	/**
	 * @return Alias del spawn de la tienda.
	 */
	public String shopSpawnAlias(){
		if(plot_cfg.containsKey("shop-spawn-alias")){
			return plot_cfg.get("shop-spawn-alias");
		}else{
			return null;
		}
	}
	
	/**
	 * No implementado, 
	 * Visibilidad del spawn de la tienda.
	 * @author Wirlie
	 */
	@SuppressWarnings("javadoc")
	public enum ShopSpawnVisibility{ PUBLIC, FRIENDS }
	
	/**
	 * Visibilidad del spawn de la tienda, no implementado.
	 * @return {@link ShopSpawnVisibility}
	 */
	public ShopSpawnVisibility shopSpawnVisibility(){
		if(plot_cfg.containsKey("shop-spawn-visibility")){
			String visStr = plot_cfg.get("shop-spawn-visibility");
			
			try{
				return ShopSpawnVisibility.valueOf(visStr);
			}catch(IllegalArgumentException e){
				return ShopSpawnVisibility.PUBLIC;
			}
		}else{
			return ShopSpawnVisibility.PUBLIC;
		}
	}
	
	/**
	 * Obtener los amigos de la parcela.
	 * @return Listado conteniendo los nombres de los amigos de esta parcela.
	 */
	public List<String> getFriends(){
		if(plot_cfg.containsKey("plot-friends")){
			String listStr = plot_cfg.get("plot-friends");
			List<String> returnList = new ArrayList<String>();
			
			for(String s : listStr.split(",|>,")){
				if(s.equalsIgnoreCase("|") || s.equalsIgnoreCase("")) continue;
				returnList.add(s.toLowerCase());
			}
			
			return returnList;
		}else{
			return new ArrayList<String>();
		}
	}
	
	/**
	 * Configuración: allow-entry | permitir entrar a otros jugadores
	 * @return {@code true} si la configuración es true.
	 */
	public boolean allowEntry(){
		if(plot_cfg.containsKey("allow-entry")){
			if(plot_cfg.get("allow-entry").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	/**
	 * Generar configuración por default para un mundo.
	 * @param worldCfg Archivo de la configuración del mundo.
	 */
	public static void defaultConfigurationForWorldCfg(File worldCfg) {
		if(!FileDirectory.WORLDS_DATA_FOLDER.exists()) FileDirectory.WORLDS_DATA_FOLDER.mkdirs();
		
		if(!worldCfg.exists()){
			try {
				worldCfg.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(worldCfg);
		yaml.set("plot-default-cfg.fire-spread", "false");
		yaml.set("plot-default-cfg.explosions", "false");
		yaml.set("plot-default-cfg.mobs", "true");
		yaml.set("plot-default-cfg.pvp", "false");
		yaml.set("plot-default-cfg.lava-flow", "false");
		yaml.set("plot-default-cfg.water-flow", "true");
		yaml.set("plot-default-cfg.use-door", "true");
		yaml.set("plot-default-cfg.use-anvil", "false");
		yaml.set("plot-default-cfg.use-workbench", "true");
		yaml.set("plot-default-cfg.use-fence-gate", "false");
		yaml.set("plot-default-cfg.use-enchantment-table", "true");
		yaml.set("plot-default-cfg.use-lever", "false");
		yaml.set("plot-default-cfg.use-button", "false");
		yaml.set("plot-default-cfg.use-pressure-plate", "false");
		yaml.set("plot-default-cfg.drop-item", "true");
		yaml.set("plot-default-cfg.msg-greeting", "" + Translation.get(StringsID.PLOT_GREETING_FORMAT, false)[0]);
		yaml.set("plot-default-cfg.msg-farewell", "" + Translation.get(StringsID.PLOT_FAREWELL_FORMAT, false)[0]);
		yaml.set("plot-default-cfg.shop-spawn-visibility", "PUBLIC");
		yaml.set("plot-default-cfg.allow-entry", "true");
		yaml.set("plot-default-cfg.allow-plot-teleport", "true");
		
		try {
			yaml.save(worldCfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permitir que otros jugadores puedan hacer tp en tu parcela.
	 * @return {@code true} si la configuración es true.
	 */
	public boolean allowTeleport() {
		if(plot_cfg.containsKey("allow-plot-teleport")){
			if(plot_cfg.get("allow-plot-teleport").equalsIgnoreCase("true")){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
}
