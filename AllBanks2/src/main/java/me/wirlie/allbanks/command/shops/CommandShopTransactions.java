/*
 * AllBanks2 - AllBanks2 is a plugin for CraftBukkit and Spigot.
 * Copyright (C) 2016 Josue Israel Acevedo Peña (Wirlie)
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

/**
 * 
 */
package me.wirlie.allbanks.command.shops;

import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.command.Command;

/**
 * @author Wirlie
 *
 */
public class CommandShopTransactions extends Command {

	/**
	 * Constructor del comando.
	 * @param permission Permiso necesario para ejecutar este comando.
	 */
	public CommandShopTransactions(String permission) {
		super(permission);
	}
	
	@Override
	public CommandExecuteResult execute(CommandSender sender, String label, String[] args) {
		return CommandExecuteResult.INVALID_ARGUMENTS;
	}
	
}
