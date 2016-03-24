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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class Translation{
	
static File languageDir = new File(AllBanks.getInstance().getDataFolder() + File.separator + "language");
	
	public enum Languages{
		ES_MX(new File(languageDir + File.separator + "EsMx.yml"), "EsMx.yml"),
		EN_US(new File(languageDir + File.separator + "EnUs.yml"), "EnUs.yml");

		File langFile;
		String resourceFile;
		
		Languages(File langFile, String resourceFile){
			this.langFile = langFile;
			this.resourceFile = resourceFile;
		}
		
		File getFile(){
			ensureLanguageFolderExists();
			
			return langFile;
		}
		
		String getResource(){
			return resourceFile;
		}
		
		static File getFolder(){
			return languageDir;
		}
		
		static void ensureLanguageFolderExists(){
			
			File langDir = new File(AllBanks.getInstance().getDataFolder() + File.separator + "language");
			
			if(!langDir.exists()){
				langDir.mkdirs();
			}
		}
	}
	
	public static String[] get(String strPath, HashMap<String, String> replaceMap, boolean prefix){
		File trFile = ensureLanguageFileExists(getLangByConfig());
		YamlConfiguration trYaml = YamlConfiguration.loadConfiguration(trFile);
		String translation = trYaml.getString(strPath, null);
		
		if(translation == null && !getLangByConfig().equals(Languages.EN_US)){
			//Intentar obtener desde el archivo EnUS
			
			Console.sendMessage(ChatColor.YELLOW + "[Translation] String " + strPath + " not found in " + getLangByConfig().getResource() + ", try to get this string with EnUs.yml");
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
		
		translation = Util.ChatFormatUtil.replaceChatFormat(translation);
		
		String[] split = translation.split("%BREAK%");
		
		if(prefix){
			String prefixStr = ChatColor.LIGHT_PURPLE + "All" + ChatColor.DARK_PURPLE + "Banks" + ChatColor.GOLD + " >> " + ChatColor.RESET;
			
			for(int i = 0; i < split.length; i++){
				split[i] = prefixStr + split[i];
			}
		}

		return split;
	}
	
	public static String[] get(StringsID strPath, HashMap<String, String> replaceMap, boolean prefix){
		return get(strPath.getPath(), replaceMap, prefix);
	}
	
	public static String[] get(StringsID strPath, boolean prefix){
		return get(strPath.getPath(), new HashMap<String, String>(), prefix);
	}
	
	public static void getAndSendMessage(Player p, String strPath, HashMap<String, String> replaceMap, boolean prefix){
		p.sendMessage(get(strPath, replaceMap, prefix));
	}
	
	public static void getAndSendMessage(CommandSender s, String strPath, HashMap<String, String> replaceMap, boolean prefix){
		s.sendMessage(get(strPath, replaceMap, prefix));
	}
	
	public static void getAndSendMessage(Player p, StringsID strPath, HashMap<String, String> replaceMap, boolean prefix){
		getAndSendMessage(p, strPath.getPath(), replaceMap, prefix);
	}
	
	public static void getAndSendMessage(Player p, StringsID strPath, boolean prefix){
		getAndSendMessage(p, strPath.getPath(), new HashMap<String, String>(), prefix);
	}
	
	public static void getAndSendMessage(CommandSender s, StringsID strPath, boolean prefix){
		getAndSendMessage(s, strPath.getPath(), new HashMap<String, String>(), prefix);
	}
	
	public static void getAndSendMessage(CommandSender s, String strPath, boolean prefix){
		getAndSendMessage(s, strPath, new HashMap<String, String>(), prefix);
	}
	
	public static Languages getLangByConfig(){
		
		String langStr = AllBanks.getInstance().getConfig().getString("pl.language", "Undefined");
		
		if(langStr.equalsIgnoreCase("EsMx") || langStr.equalsIgnoreCase("Es")){
			return Languages.ES_MX;
		}else{
			return Languages.EN_US;
		}
		
	}
	
	private static File ensureLanguageFileExists(Languages lang){
		
		if(!lang.getFile().exists()){
			Console.sendMessage(ChatColor.YELLOW + "[Translation] Language file " + lang.toString() + " not found...");
			Console.sendMessage(ChatColor.YELLOW + "[Translation] Saving resource...");
			//Intentar instalar
			AllBanks.getInstance().saveResource(lang.getResource(), true);
			//Mover de ruta
			Languages.ensureLanguageFolderExists();
			
			File moveFrom = new File(AllBanks.getInstance().getDataFolder() + File.separator + lang.getResource());
			File moveTo = new File(Languages.getFolder() + File.separator + lang.getResource());
			
			Console.sendMessage(ChatColor.WHITE + "[Translation][Debug] Copy " + moveFrom.getName() + " to " + moveTo);
			
			moveFrom.renameTo(moveTo);
			
			Console.sendMessage(ChatColor.GREEN + "[Translation] Success! Language installed!");
		}else{
			ensureLanguageFileIsUpToDate(lang);
		}
		
		return lang.getFile();
	}

	//TODO Quitar esto ya que es solo para forzar una actualización de los idiomas.
	static boolean isupdtodate = false;
	
	private static void ensureLanguageFileIsUpToDate(Languages lang){
		YamlConfiguration readLanguage = YamlConfiguration.loadConfiguration(lang.getFile());
		
		String version = readLanguage.getString("language-version", "null");
		
		if(!version.equalsIgnoreCase(AllBanks.getInstance().getDescription().getVersion()) || !isupdtodate){
			
			isupdtodate = true;
			
			//No concuerda, intentar actualizar.
			Console.sendMessage(ChatColor.YELLOW + "[Translation] Updating " + lang.getResource() + " " + version + " to " + AllBanks.getInstance().getDescription().getVersion());
			
			AllBanks.getInstance().saveResource(lang.getResource(), true);
			File nativeLangFile = new File(AllBanks.getInstance().getDataFolder() + File.separator + lang.getResource());
			
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