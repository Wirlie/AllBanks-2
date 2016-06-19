package me.wirlie.allbanks.utils;

import org.bukkit.ChatColor;
import net.minecraft.server.v1_9_R2.EnumChatFormat;
import net.minecraft.server.v1_9_R2.NBTTagCompound;

/**
 * Util para R2
 * @author Wirlie
 *
 */
public class Util_1_9_R2 {
	
	/**
	 * Obtener el código de nombre de un ítem
	 * @param stack Ítem
	 * @return Código
	 */
	public static String getItemCodeOrGetCustomName(net.minecraft.server.v1_9_R2.ItemStack stack) {
        NBTTagCompound tag = stack.getTag();

        if ((tag != null) && (tag.hasKeyOfType("display", 10))) {
            NBTTagCompound nbttagcompound = tag.getCompound("display");

            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return EnumChatFormat.ITALIC + nbttagcompound.getString("Name");
            }
        }

        return stack.a() + ".name";
    }
	
	/**
	 * Convertir un EnumChat a ChatColor
	 * @param e EnumChatFormat
	 * @return ChatColor
	 */
	public static ChatColor convertEnumChatFormatToChatColor(EnumChatFormat e) {
		
		switch(e){
		case AQUA:
			return ChatColor.AQUA;
		case BLACK:
			return ChatColor.BLACK;
		case BLUE:
			return ChatColor.BLUE;
		case BOLD:
			return ChatColor.BOLD;
		case DARK_AQUA:
			return ChatColor.DARK_AQUA;
		case DARK_BLUE:
			return ChatColor.DARK_BLUE;
		case DARK_GRAY:
			return ChatColor.DARK_GRAY;
		case DARK_GREEN:
			return ChatColor.DARK_GREEN;
		case DARK_PURPLE:
			return ChatColor.DARK_PURPLE;
		case DARK_RED:
			return ChatColor.DARK_RED;
		case GOLD:
			return ChatColor.GOLD;
		case GRAY:
			return ChatColor.GRAY;
		case GREEN:
			return ChatColor.GREEN;
		case ITALIC:
			return ChatColor.ITALIC;
		case LIGHT_PURPLE:
			return ChatColor.LIGHT_PURPLE;
		case RED:
			return ChatColor.RED;
		case RESET:
			return ChatColor.RESET;
		case STRIKETHROUGH:
			return ChatColor.STRIKETHROUGH;
		case UNDERLINE:
			return ChatColor.UNDERLINE;
		case WHITE:
			return ChatColor.WHITE;
		case YELLOW:
			return ChatColor.YELLOW;
		default:
			return ChatColor.WHITE;
		}
	}
}
