/**
 * 
 */
package me.wirlie.allbanks.main;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * @author Wirlie
 * @since AnimalAttacks v1.0
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
		String translation = trYaml.getString(strPath, "{" + strPath + "}");
		
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
			//Intentar instalar
			AllBanks.getInstance().saveResource(lang.getResource(), true);
			//Mover de ruta
			Languages.ensureLanguageFolderExists();
			File moveFrom = new File(AllBanks.getInstance().getDataFolder() + File.separator + lang.getResource());
			File moveTo = new File(Languages.getFolder() + File.separator + lang.getResource());
			
			moveFrom.renameTo(moveTo);
		}
		
		return lang.getFile();
	}
}