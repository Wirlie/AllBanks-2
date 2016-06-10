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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.utils.AllBanksLogger;
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
		/**
		 * Lenguaje Español.
		 */
		ES_MX(new File(languageDir + File.separator + "EsMx.yml"), "EsMx.yml"),
		/**
		 * Lenguaje Inglés.
		 */
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
	 * @param stringPath Ruta de la traducción.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Incluir prefijo de AllBanks?
	 * @param consoleSender ¿Se necesita enviar con formato de Consola? (Sin ChatColor)
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(String stringPath, HashMap<String, String> replaceMap, boolean prefix, boolean consoleSender){
		File trFile = ensureLanguageFileExists(getLanguageSpecifiedInConfiguration());
		YamlConfiguration trYaml = YamlConfiguration.loadConfiguration(trFile);
		String translation = trYaml.getString(stringPath, null);
		
		if(translation == null && !getLanguageSpecifiedInConfiguration().equals(Languages.EN_US)){
			//Intentar obtener desde el archivo EnUS
			
			Console.sendMessage(ChatColor.YELLOW + "[Translation] String " + stringPath + " not found in " + getLanguageSpecifiedInConfiguration().getResourceName() + ", try to get this string with EnUs.yml");
			YamlConfiguration enUsYaml = YamlConfiguration.loadConfiguration(ensureLanguageFileExists(Languages.EN_US));
			translation = enUsYaml.getString(stringPath, null);
		
		}
		
		if(translation == null) translation = "{" + stringPath + "}";
		
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
	 * @param stringID ID de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 * @param commandSender ¿Enviar en formato de consola? (Sin ChatColor)
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(StringsID stringID, HashMap<String, String> replaceMap, boolean prefix, boolean commandSender){
		return get(stringID.getPath(), replaceMap, prefix, commandSender);
	}
	
	/**
	 * Obtener una traducción.
	 * @param stringID ID de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(StringsID stringID, HashMap<String, String> replaceMap, boolean prefix){
		return get(stringID.getPath(), replaceMap, prefix, false);
	}
	
	/**
	 * Obtener una traducción.
	 * @param stringID ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 * @param commandSender ¿Se necesita enviar con formato de Consola? (Sin ChatColor)
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(StringsID stringID, boolean prefix, boolean commandSender){
		return get(stringID.getPath(), new HashMap<String, String>(), prefix, commandSender);
	}
	
	/**
	 * Obtener una traducción.
	 * @param stringID ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static String[] get(StringsID stringID, boolean prefix){
		return get(stringID.getPath(), new HashMap<String, String>(), prefix, false);
	}
	
	/**
	 * Obtener una traducción múltiple.
	 * @param prefix ¿Conservar prefix?
	 * @param stringIDS Multiples ID de traducciones a obtener.
	 * @return Array conteniendo las lineas de traducción correspondientes a lo solicitado.
	 */
	public static List<String> getMultiple(boolean prefix, StringsID... stringIDS){
		
		List<String> returnList = new ArrayList<String>();
		for(StringsID id : stringIDS){
			returnList.addAll(Arrays.asList(get(id.getPath(), new HashMap<String, String>(), prefix, false)));
		}
		
		return returnList;
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param stringPath Ruta de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(Player p, String stringPath, HashMap<String, String> replaceMap, boolean prefix){
		p.sendMessage(get(stringPath, replaceMap, prefix, false));
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param stringPath Ruta de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 * @param commandSender ¿Se necesita enviar con formato de Consola? (Sin ChatColor)
	 */
	public static void getAndSendMessage(Player p, String stringPath, HashMap<String, String> replaceMap, boolean prefix, boolean commandSender){
		p.sendMessage(get(stringPath, replaceMap, prefix, commandSender));
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al ejecutor del comando especificado.
	 * @param s Ejecutor del comando.
	 * @param stringPath Ruta de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(CommandSender s, String stringPath, HashMap<String, String> replaceMap, boolean prefix){
		if(s instanceof ConsoleCommandSender){
			s.sendMessage(get(stringPath, replaceMap, prefix, true));
		}else{
			s.sendMessage(get(stringPath, replaceMap, prefix, false));
		}
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al ejecutor del comando especificado.
	 * @param s Ejecutor del comando.
	 * @param stringID ID de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(CommandSender s, StringsID stringID, HashMap<String, String> replaceMap, boolean prefix){
		if(s instanceof ConsoleCommandSender){
			s.sendMessage(get(stringID, replaceMap, prefix, true));
		}else{
			s.sendMessage(get(stringID, replaceMap, prefix, false));
		}
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param stringID ID de la traducción a enviar.
	 * @param replaceMap Mapa de reemplazo.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(Player p, StringsID stringID, HashMap<String, String> replaceMap, boolean prefix){
		getAndSendMessage(p, stringID.getPath(), replaceMap, prefix);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param stringID ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 * @param replaceArray Array de cadenas de texto, lo mismo que replaceMap pero sin necesidad de especificar un {@code HashMap<String, String>}
	 */
	public static void getAndSendMessage(Player p, StringsID stringID, boolean prefix, String... replaceArray){
		
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		for(int i = 0; i < replaceArray.length; i++){
			replaceMap.put("%" + i + "%", replaceArray[i]);
		}
		
		getAndSendMessage(p, stringID.getPath(), replaceMap, prefix);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param sender Jugador/CommandSender a enviar el mensaje.
	 * @param stringID ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 * @param replaceArray Array de cadenas de texto, lo mismo que replaceMap pero sin necesidad de especificar un {@code HashMap<String, String>}
	 */
	public static void getAndSendMessage(CommandSender sender, StringsID stringID, boolean prefix, String... replaceArray){
		
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		for(int i = 0; i < replaceArray.length; i++){
			replaceMap.put("%" + i + "%", replaceArray[i]);
		}
		
		getAndSendMessage(sender, stringID.getPath(), replaceMap, prefix);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param stringID ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 * @param replaceFormat Prefijo y sufijo a usar al momento de leer y colocar los valores de <b>replaceArray</b> en el Mapa de reemplazo (HashMap).
	 * <br><br>El formato por defecto (si este valor es especificado como null) es el siguiente: <b>%{i}%</b> en donde <b>i</b> es el índice del array especificado <i>replaceArray</i>.
	 * <br><br>Si no se especifica <b>{i}</b> en la cadena entonces se usará el formato por defecto <b>%{i}%</b>
	 * <br><br>Ejemplos de formatos y su resultado:
	 * <br>
	 * <table>
	 * 	<tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">Formato</td>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">Resultado<br>Array de longitud 5</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">%{i}%</td>
	 * 		<td style="padding: 15px; text-align: left; border: 1px solid black;">
	 * 			replaceArray[1] -> replaceMap.put("%1%", String)<br>
	 * 			replaceArray[2] -> replaceMap.put("%2%", String)<br>
	 * 			replaceArray[3] -> replaceMap.put("%3%", String)<br>
	 * 			replaceArray[4] -> replaceMap.put("%4%", String)<br>
	 * 			replaceArray[5] -> replaceMap.put("%5%", String)<br>
	 * 		</td>
	 * 	</tr>
	 *  <tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">&{i}=</td>
	 * 		<td style="padding: 15px; text-align: left; border: 1px solid black;">
	 * 			replaceArray[1] -> replaceMap.put("&1=", String)<br>
	 * 			replaceArray[2] -> replaceMap.put("&2=", String)<br>
	 * 			replaceArray[3] -> replaceMap.put("&3=", String)<br>
	 * 			replaceArray[4] -> replaceMap.put("&4=", String)<br>
	 * 			replaceArray[5] -> replaceMap.put("&5=", String)<br>
	 * 		</td>
	 * 	</tr>
	 *  <tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">{i}</td>
	 * 		<td style="padding: 15px; text-align: left; border: 1px solid black;">
	 * 			replaceArray[1] -> replaceMap.put("1", String)<br>
	 * 			replaceArray[2] -> replaceMap.put("2", String)<br>
	 * 			replaceArray[3] -> replaceMap.put("3", String)<br>
	 * 			replaceArray[4] -> replaceMap.put("4", String)<br>
	 * 			replaceArray[5] -> replaceMap.put("5", String)<br>
	 * 		</td>
	 * 	</tr>
	 *  <tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">$%?{i}</td>
	 * 		<td style="padding: 15px; text-align: left; border: 1px solid black;">
	 * 			replaceArray[1] -> replaceMap.put("$%?1", String)<br>
	 * 			replaceArray[2] -> replaceMap.put("$%?2", String)<br>
	 * 			replaceArray[3] -> replaceMap.put("$%?3", String)<br>
	 * 			replaceArray[4] -> replaceMap.put("$%?4", String)<br>
	 * 			replaceArray[5] -> replaceMap.put("$%?5", String)<br>
	 * 		</td>
	 * 	</tr>
	 * </table>
	 * @param replaceArray Argumentos de tipo String que especifica las cadenas a reemplazar usando el sufijo y prefijo especificado por <i>replaceFormat</i>.<br>
	 * <br>Estos se ordenan en el array según la especificación, es decir<br>
	 * ("hola", "que tal", "duh", ..., "n") se ordena como:<br><br>
	 * replaceArray[1] = "hola"<br>
	 * replaceArray[2] = "que tal"<br>
	 * replaceArray[3] = "duh"<br>
	 * ...<br>
	 * replaceArray[n] = "n";<br>
	 */
	public static void getAndSendMessage(Player p, StringsID stringID, boolean prefix, String replaceFormat, String... replaceArray){
		
		Pattern pattern = Pattern.compile("(.|){1,}{i}(.|){1,}");
		Matcher matcher = pattern.matcher(replaceFormat);
		
		if(matcher.matches()){
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			
			String prefixRpl = matcher.group(1);
			String subfixRpl = matcher.group(2);
			
			if(prefixRpl == null){
				prefixRpl = "";
				AllBanksLogger.warning("[REGEX] Fail to get group 1 of regex | pattern: " + pattern.pattern() + " | charSequence: " + replaceFormat + " | matcher.group(1) result: null | resolution: switch to an empty String ('') instead of a null value.");
			}
			if(subfixRpl == null){
				subfixRpl = "";
				AllBanksLogger.warning("[REGEX] Fail to get group 2 of regex | pattern: " + pattern.pattern() + " | charSequence: " + replaceFormat + " | matcher.group(2) result: null | resolution: switch to an empty String ('') instead of a null value.");
			}
			
			for(int i = 0; i < replaceArray.length; i++){
				replaceMap.put(prefixRpl + i + subfixRpl, replaceArray[i]);
			}
			
			getAndSendMessage(p, stringID.getPath(), replaceMap, prefix);
		}else{
			//default replacement
			getAndSendMessage(p, stringID, prefix, replaceArray);
		}
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param sender El que ha ejecutado un comando.
	 * @param stringID ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 * @param replaceFormat Prefijo y sufijo a usar al momento de leer y colocar los valores de <b>replaceArray</b> en el Mapa de reemplazo (HashMap).
	 * <br><br>El formato por defecto (si este valor es especificado como null) es el siguiente: <b>%{i}%</b> en donde <b>i</b> es el índice del array especificado <i>replaceArray</i>.
	 * <br><br>Si no se especifica <b>{i}</b> en la cadena entonces se usará el formato por defecto <b>%{i}%</b>
	 * <br><br>Ejemplos de formatos y su resultado:
	 * <br>
	 * <table>
	 * 	<tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">Formato</td>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">Resultado<br>Array de longitud 5</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">%{i}%</td>
	 * 		<td style="padding: 15px; text-align: left; border: 1px solid black;">
	 * 			replaceArray[1] -> replaceMap.put("%1%", String)<br>
	 * 			replaceArray[2] -> replaceMap.put("%2%", String)<br>
	 * 			replaceArray[3] -> replaceMap.put("%3%", String)<br>
	 * 			replaceArray[4] -> replaceMap.put("%4%", String)<br>
	 * 			replaceArray[5] -> replaceMap.put("%5%", String)<br>
	 * 		</td>
	 * 	</tr>
	 *  <tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">&{i}=</td>
	 * 		<td style="padding: 15px; text-align: left; border: 1px solid black;">
	 * 			replaceArray[1] -> replaceMap.put("&1=", String)<br>
	 * 			replaceArray[2] -> replaceMap.put("&2=", String)<br>
	 * 			replaceArray[3] -> replaceMap.put("&3=", String)<br>
	 * 			replaceArray[4] -> replaceMap.put("&4=", String)<br>
	 * 			replaceArray[5] -> replaceMap.put("&5=", String)<br>
	 * 		</td>
	 * 	</tr>
	 *  <tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">{i}</td>
	 * 		<td style="padding: 15px; text-align: left; border: 1px solid black;">
	 * 			replaceArray[1] -> replaceMap.put("1", String)<br>
	 * 			replaceArray[2] -> replaceMap.put("2", String)<br>
	 * 			replaceArray[3] -> replaceMap.put("3", String)<br>
	 * 			replaceArray[4] -> replaceMap.put("4", String)<br>
	 * 			replaceArray[5] -> replaceMap.put("5", String)<br>
	 * 		</td>
	 * 	</tr>
	 *  <tr>
	 * 		<td style="padding: 15px; text-align: center; border: 1px solid black;">$%?{i}</td>
	 * 		<td style="padding: 15px; text-align: left; border: 1px solid black;">
	 * 			replaceArray[1] -> replaceMap.put("$%?1", String)<br>
	 * 			replaceArray[2] -> replaceMap.put("$%?2", String)<br>
	 * 			replaceArray[3] -> replaceMap.put("$%?3", String)<br>
	 * 			replaceArray[4] -> replaceMap.put("$%?4", String)<br>
	 * 			replaceArray[5] -> replaceMap.put("$%?5", String)<br>
	 * 		</td>
	 * 	</tr>
	 * </table>
	 * @param replaceArray Argumentos de tipo String que especifica las cadenas a reemplazar usando el sufijo y prefijo especificado por <i>replaceFormat</i>.<br>
	 * <br>Estos se ordenan en el array según la especificación, es decir<br>
	 * ("hola", "que tal", "duh", ..., "n") se ordena como:<br><br>
	 * replaceArray[1] = "hola"<br>
	 * replaceArray[2] = "que tal"<br>
	 * replaceArray[3] = "duh"<br>
	 * ...<br>
	 * replaceArray[n] = "n";<br>
	 */
	public static void getAndSendMessage(CommandSender sender, StringsID stringID, boolean prefix, String replaceFormat, String... replaceArray){
		
		Pattern pattern = Pattern.compile("(.|){1,}{i}(.|){1,}");
		Matcher matcher = pattern.matcher(replaceFormat);
		
		if(matcher.matches()){
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			
			String prefixRpl = matcher.group(1);
			String subfixRpl = matcher.group(2);
			
			if(prefixRpl == null){
				prefixRpl = "";
				AllBanksLogger.warning("[REGEX] Fail to get group 1 of regex | pattern: " + pattern.pattern() + " | charSequence: " + replaceFormat + " | matcher.group(1) result: null | resolution: switch to an empty String ('') instead of a null value.");
			}
			if(subfixRpl == null){
				subfixRpl = "";
				AllBanksLogger.warning("[REGEX] Fail to get group 2 of regex | pattern: " + pattern.pattern() + " | charSequence: " + replaceFormat + " | matcher.group(2) result: null | resolution: switch to an empty String ('') instead of a null value.");
			}
			
			for(int i = 0; i < replaceArray.length; i++){
				replaceMap.put(prefixRpl + i + subfixRpl, replaceArray[i]);
			}
			
			getAndSendMessage(sender, stringID.getPath(), replaceMap, prefix);
		}else{
			//default replacement
			getAndSendMessage(sender, stringID, prefix, replaceArray);
		}
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al jugador especificado.
	 * @param p Jugador a enviar el mensaje.
	 * @param stringID ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(Player p, StringsID stringID, boolean prefix){
		getAndSendMessage(p, stringID.getPath(), new HashMap<String, String>(), prefix);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al ejecutor del comando especificado.
	 * @param s Ejecutor del comando.
	 * @param stringID ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(CommandSender s, StringsID stringID, boolean prefix){
		getAndSendMessage(s, stringID.getPath(), new HashMap<String, String>(), prefix);
	}
	
	/**
	 * Obtener una traducción y enviar a la vez al ejecutor del comando especificado.
	 * @param s Ejecutor del comando.
	 * @param stringPath ID de la traducción a enviar.
	 * @param prefix ¿Conservar prefix?
	 */
	public static void getAndSendMessage(CommandSender s, String stringPath, boolean prefix){
		getAndSendMessage(s, stringPath, new HashMap<String, String>(), prefix);
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
	static boolean debugTranslationFile = AllBanks.getInstance().getConfig().getBoolean("debug-mode", false);
	
	/**
	 * Nos aseguramos de que un archivo de lenguaje se encuentra actualizado.
	 * @param lang Lenguaje a comprobar.
	 */
	private static void ensureLanguageFileIsUpToDate(Languages lang){
		YamlConfiguration readLanguage = YamlConfiguration.loadConfiguration(lang.getFile());
		
		String version = readLanguage.getString("language-version", "null");
		
		if(!version.equalsIgnoreCase(AllBanks.getInstance().getDescription().getVersion()) || !firstExecution && debugTranslationFile){
			
			firstExecution = true;
			
			//No concuerda, intentar actualizar.
			if(firstExecution)
				Console.sendMessage(ChatColor.YELLOW + "[Translation] Updating " + lang.getResourceName() + " v" + version + " to v" + AllBanks.getInstance().getDescription().getVersion());
			
			AllBanks.getInstance().saveResource(lang.getResourceName(), true);
			File nativeLangFile = new File(AllBanks.getInstance().getDataFolder() + File.separator + lang.getResourceName());
			
			if(!nativeLangFile.exists()){
				//error!! no existe a pesar de haber sido guardado
				Console.sendMessage(ChatColor.RED + "[Translation][Error] Ops! Language file can not be updated because an unknow error (NativeFileNotExists)");
				return;
			}
			
			YamlConfiguration nativeYaml = YamlConfiguration.loadConfiguration(nativeLangFile);
			
			for(String key : nativeYaml.getKeys(true)){
				//Object obj = readLanguage.get(key, null);
				
				/*if(obj == null){
					readLanguage.set(key, nativeYaml.get(key));
					count++;
					continue;
				}*/
				
				readLanguage.set(key, nativeYaml.get(key));
			}
			
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