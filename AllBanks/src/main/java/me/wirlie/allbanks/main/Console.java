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
	public static void sendMessage(StringsID strID){
		Bukkit.getConsoleSender().sendMessage(Translation.get(strID, true));
	}
	
	public static void sendMessage(String str){
		Bukkit.getConsoleSender().sendMessage(str);
	}
}
