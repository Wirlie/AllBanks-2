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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.utils.ChatUtil;

/**
 * Clase encargada de obtener la traducción correspondiente a un string.
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Translation{
	
	private static File languageDir = new File(AllBanks.getInstance().getDataFolder() + File.separator + "language");
	private static String prefixStr = ChatUtil.replaceChatFormat(AllBanks.getInstance().getConfig().getString("pl.prefix", ChatColor.DARK_AQUA + "All" + ChatColor.AQUA + "Banks" + ChatColor.GOLD + ChatColor.BOLD + " >> " + ChatColor.RESET));

	/**
	 * Lenguajes soportados por AllBanks
	 * @author Wirlie
	 *
	 */
	public enum Languages{
		ES_MX(new File(languageDir + File.separator + "EsMx.yml"), "EsMx.yml"),
		EN_US(new File(languageDir + File.separator + "EnUs.yml"), "EnUs.yml");

		File langFile;
		String resourceFile;
		
		Languages(File langFile, String resourceFile){
			this.langFile = langFile;
			this.resourceFile = resourceFile;
		}
		
		/**
		 * Obtener el archivo del lenguaje
		 * @return
		 */
		File getFile(){
			ensureLanguageFolderExists();
			
			return langFile;
		}
		
		/**
		 * Obtener el nombre del recurso.
		 * @return
		 */
		String getResourceName(){
			return resourceFile;
		}
		
		/**
		 * Obtener la carpeta en donde se almacenan las traducciones.
		 * @return
		 */
		static File getFolder(){
			return languageDir;
		}
		
		/**
		 * Asegurarnos de que la carpeta de lenguaje existe.
		 */
		static void ensureLanguageFolderExists(){
			
			File langDir = new File(AllBanks.getInstance().getDataFolder() + File.separator + "language");
			
			if(!langDir.exists()){
				langDir.mkdirs();
			}
		}
	}
	
	/**
	 * Util, que ayuda a transformar un String en un HashMap<String, String>
	 * @param splitRegex Palabra o secuencia de caracteres que servirán de ayuda para hacer un split de argumentos. Ejemplo si se establece
	 * con >>> y nuestro string tiene %1%>>>Hola se considerará lo siguiente:<br>
	 * Reemplazar %1% por "Hola".
	 * @param args Multiples argumentos.
	 * @return HashMap conteniendo la interpretación de lo anterior.
	 */
	public static HashMap<String, String> splitStringIntoReplaceHashMap(String splitRegex, String... args){
		HashMap<String, String> returnMap = new HashMap<String, String>();
		
		for(String s : args) {
			String[] parts = s.split(splitRegex);
			if(parts.length >= 2) {
				returnMap.put(parts[0], parts[1]);
			}
		}
		
		return returnMap;
	}
	
	/**
	 * Obtener el Prefix de AllBanks
	 * @return Prefix de AllBanks
	 */
	public static String getPluginPrefix() {
		return prefixStr;
	}
	
	/**
	 * Obtener una traducción.
	 * @param strPath Ruta de la traducción.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Incluir prefijo de AllBanks?
	 * @param consoleSender ¿Se necesita enviar con formato de Consola? (Sin ChatColor)
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(String strPath, HashMap<String, String> replaceMap, boolean prefix, boolean consoleSender){
		File trFile = ensureLanguageFileExists(getLanguageSpecifiedInConfiguration());
		YamlConfiguration trYaml = YamlConfiguration.loadConfiguration(trFile);
		String translation = trYaml.getString(strPath, null);
		
		if(translation == null && !getLanguageSpecifiedInConfiguration().equals(Languages.EN_US)){
			//Intentar obtener desde el archivo EnUS
			
			Console.sendMessage(ChatColor.YELLOW + "[Translation] String " + strPath + " not found in " + getLanguageSpecifiedInConfiguration().getResourceName() + ", try to get this string with EnUs.yml");
			YamlConfiguration enUsYaml = YamlConfiguration.loadConfiguration(ensureLanguageFileExists(Languages.EN_US));
			translation = enUsYaml.getString(strPath, null);
		
		}
		
		if(translation == null) translation = "{" + strPath + "}";
		
		if(!replaceMap.isEmpty()){
			Iterator<Entry<String, String>> it = replaceMap.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, String> ent = it.next();
				translation = translation.replace(ent.getKey(), ent.getValue());
			}
		}
		
		translation = ChatUtil.replaceChatFormat(translation);
		
		//Especial
		translation = translation.replace("%LOTTERY_PREFIX%", ChatUtil.replaceChatFormat(trYaml.getString("LOTTERY_PREFIX", "")));
		
		if(consoleSender){
			translation = ChatUtil.supressChatFormat(translation);
		}
		
		String[] split = translation.split("%BREAK%");
		
		if(prefix){
			for(int i = 0; i < split.length; i++){
				split[i] = prefixStr + split[i];
			}
		}

		return split;
	}
	
	/**
	 * Obtener una traducción.
	 * @param strPath ID de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 * @param commandSender ¿Enviar en formato de consola? (Sin ChatColor)
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(StringsID strPath, HashMap<String, String> replaceMap, boolean prefix, boolean commandSender){
		return get(strPath.getPath(), replaceMap, prefix, commandSender);
	}
	
	/**
	 * Obtener una traducción.
	 * @param strPath ID de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(StringsID strPath, HashMap<String, String> replaceMap, boolean prefix){
		return get(strPath.getPath(), replaceMap, prefix, false);
	}
	
	/**
	 * Obtener una traducción.
	 * @param strPath ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 * @param commandSender ¿Se necesita enviar con formato de Consola? (Sin ChatColor)
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(StringsID strPath, boolean prefix, boolean commandSender){
		return get(strPath.getPath(), new HashMap<String, String>(), prefix, commandSender);
	}
	
	/**
	 * Obtener una traducción.
	 * @param strPath ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(StringsID strPath, boolean prefix){
		return get(strPath.getPath(), new HashMap<String, String>(), prefix, false);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param strPath Ruta de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(Player p, String strPath, HashMap<String, String> replaceMap, boolean prefix){
		p.sendMessage(get(strPath, replaceMap, prefix, false));
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param strPath Ruta de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 * @param commandSender ¿Se necesita enviar con formato de Consola? (Sin ChatColor)
	 */
	public static void getAndSendMessage(Player p, String strPath, HashMap<String, String> replaceMap, boolean prefix, boolean commandSender){
		p.sendMessage(get(strPath, replaceMap, prefix, commandSender));
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al ejecutor del comando especificado.
	 * @param s Ejecutor del comando.
	 * @param strPath Ruta de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(CommandSender s, String strPath, HashMap<String, String> replaceMap, boolean prefix){
		if(s instanceof ConsoleCommandSender){
			s.sendMessage(get(strPath, replaceMap, prefix, true));
		}else{
			s.sendMessage(get(strPath, replaceMap, prefix, false));
		}
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al ejecutor del comando especificado.
	 * @param s Ejecutor del comando.
	 * @param strPath ID de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(CommandSender s, StringsID strPath, HashMap<String, String> replaceMap, boolean prefix){
		if(s instanceof ConsoleCommandSender){
			s.sendMessage(get(strPath, replaceMap, prefix, true));
		}else{
			s.sendMessage(get(strPath, replaceMap, prefix, false));
		}
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param strPath ID de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(Player p, StringsID strPath, HashMap<String, String> replaceMap, boolean prefix){
		getAndSendMessage(p, strPath.getPath(), replaceMap, prefix);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param strPath ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(Player p, StringsID strPath, boolean prefix){
		getAndSendMessage(p, strPath.getPath(), new HashMap<String, String>(), prefix);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al ejecutor del comando especificado.
	 * @param s Ejecutor del comando.
	 * @param strPath ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(CommandSender s, StringsID strPath, boolean prefix){
		getAndSendMessage(s, strPath.getPath(), new HashMap<String, String>(), prefix);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al ejecutor del comando especificado.
	 * @param s Ejecutor del comando.
	 * @param strPath ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(CommandSender s, String strPath, boolean prefix){
		getAndSendMessage(s, strPath, new HashMap<String, String>(), prefix);
	}
	
	/**
	 * Obtener el lenguaje especificado en el archivo de configuración
	 * @return Lenguaje.
	 */
	public static Languages getLanguageSpecifiedInConfiguration(){
		
		String langStr = AllBanks.getInstance().getConfig().getString("pl.language", "Undefined");
		
		if(langStr.equalsIgnoreCase("EsMx") || langStr.equalsIgnoreCase("Es")){
			return Languages.ES_MX;
		}else{
			return Languages.EN_US;
		}
		
	}
	
	/**
	 * Asegurarnos de que el archivo de lenguaje existe.
	 * @param lang Lenguaje
	 * @return Archivo del lenguaje.
	 */
	private static File ensureLanguageFileExists(Languages lang){
		
		if(!lang.getFile().exists()){
			Console.sendMessage(ChatColor.YELLOW + "[Translation] Language file " + lang.toString() + " not found...");
			Console.sendMessage(ChatColor.YELLOW + "[Translation] Saving resource...");
			//Intentar instalar
			AllBanks.getInstance().saveResource(lang.getResourceName(), true);
			//Mover de ruta
			Languages.ensureLanguageFolderExists();
			
			File moveFrom = new File(AllBanks.getInstance().getDataFolder() + File.separator + lang.getResourceName());
			File moveTo = new File(Languages.getFolder() + File.separator + lang.getResourceName());
			
			Console.sendMessage(ChatColor.WHITE + "[Translation][Debug] Copy " + moveFrom.getName() + " to " + moveTo);
			
			moveFrom.renameTo(moveTo);
			
			Console.sendMessage(ChatColor.GREEN + "[Translation] Success! Language installed!");
		}else{
			ensureLanguageFileIsUpToDate(lang);
		}
		
		return lang.getFile();
	}
	
	static boolean firstExecution = false;
	
	/**
	 * Nos aseguramos de que un archivo de lenguaje se encuentra actualizado.
	 * @param lang Lenguaje a comprobar.
	 */
	private static void ensureLanguageFileIsUpToDate(Languages lang){
		YamlConfiguration readLanguage = YamlConfiguration.loadConfiguration(lang.getFile());
		
		String version = readLanguage.getString("language-version", "null");
		
		if(!version.equalsIgnoreCase(AllBanks.getInstance().getDescription().getVersion()) || !firstExecution){
			
			firstExecution = true;
			
			//No concuerda, intentar actualizar.
			if(firstExecution)
				Console.sendMessage(ChatColor.YELLOW + "[Translation] Updating " + lang.getResourceName() + " " + version + " to " + AllBanks.getInstance().getDescription().getVersion());
			
			AllBanks.getInstance().saveResource(lang.getResourceName(), true);
			File nativeLangFile = new File(AllBanks.getInstance().getDataFolder() + File.separator + lang.getResourceName());
			
			if(!nativeLangFile.exists()){
				//error!! no existe a pesar de haber sido guardado
				Console.sendMessage(ChatColor.RED + "[Translation][Error] Ops! Language file can not be updated because an unknow error (NativeFileNotExists)");
				return;
			}
			
			YamlConfiguration nativeYaml = YamlConfiguration.loadConfiguration(nativeLangFile);
			
			int count = 0;
			
			for(String key : nativeYaml.getKeys(true)){
				Object obj = readLanguage.get(key, null);
				
				if(obj == null){
					readLanguage.set(key, nativeYaml.get(key));
					count++;
				}
				
			}
			
			if(count > 0)
				Console.sendMessage(ChatColor.GREEN + "[Translation] " + count + " translations updated!");
			else
				Console.sendMessage(ChatColor.YELLOW + "[Translation] 0 translations updated.");
			
			//Actualizar version
			readLanguage.set("language-version", AllBanks.getInstance().getDescription().getVersion());
			
			try {
				readLanguage.save(lang.getFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			nativeLangFile.delete();
		}
	}
}