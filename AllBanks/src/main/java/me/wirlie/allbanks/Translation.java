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
	
	public static String[] get(String strPath, boolean prefix){
		File trFile = ensureLanguageFileExists(getLangByConfig());
		YamlConfiguration trYaml = YamlConfiguration.loadConfiguration(trFile);
		String translation = trYaml.getString(strPath, null);
		
		if(translation == null && !getLangByConfig().equals(Languages.EN_US)){
			//Intentar obtener desde el archivo EnUS
			
			Console.sendMessage("String " + strPath + " not found in " + getLangByConfig().getResource() + ", try to get this string with EnUs.yml");
			YamlConfiguration enUsYaml = YamlConfiguration.loadConfiguration(ensureLanguageFileExists(Languages.EN_US));
			translation = enUsYaml.getString(strPath, null);
		
		}
		
		if(translation == null) translation = "{" + strPath + "}";
		
		String[] split = translation.split("%BREAK%");

		return split;
	}
	
	public static String[] get(StringsID strPath, boolean prefix){
		return get(strPath.getPath(), prefix);
	}
	
	public static void getAndSendMessage(Player p, String strPath, boolean prefix){
		p.sendMessage(get(strPath, prefix));
	}
	
	public static void getAndSendMessage(Player p, StringsID strPath, boolean prefix){
		getAndSendMessage(p, strPath.getPath(), prefix);
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
			Console.sendMessage("Language file " + lang.toString() + " not found...");
			Console.sendMessage("Saving resource...");
			//Intentar instalar
			AllBanks.getInstance().saveResource(lang.getResource(), true);
			//Mover de ruta
			Languages.ensureLanguageFolderExists();
			
			File moveFrom = new File(AllBanks.getInstance().getDataFolder() + File.separator + lang.getResource());
			File moveTo = new File(Languages.getFolder() + File.separator + lang.getResource());
			
			Console.sendMessage("Copy " + moveFrom.getName() + " to " + moveTo);
			
			moveFrom.renameTo(moveTo);
			
			Console.sendMessage("Success! Language installed!");
		}
		
		return lang.getFile();
	}
}