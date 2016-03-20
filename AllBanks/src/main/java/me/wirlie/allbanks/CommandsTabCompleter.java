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

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public class CommandsTabCompleter implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		return null;
	}

}
