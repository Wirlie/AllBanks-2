/**
 * 
 */
package me.wirlie.allbanks.main;

import org.bukkit.Bukkit;

/**
 * @author Wirlie
 * @since AnimalAttacks v1.0
 *
 */
public class Console {
	
	static String simplePrefix = "[" + AllBanks.getInstance().getDescription().getName() + "] ";
	
	public static void sendMessage(StringsID strID){
		for(String s : Translation.get(strID, false))
			Bukkit.getConsoleSender().sendMessage(simplePrefix + s);
	}
	
	public static void sendMessage(String str){
		Bukkit.getConsoleSender().sendMessage(simplePrefix + str);
	}
}
