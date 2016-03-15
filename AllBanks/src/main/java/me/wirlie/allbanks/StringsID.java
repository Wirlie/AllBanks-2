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

/**
 * @author Wirlie
 * @since AllBanks v1.0
 *
 */
public enum StringsID {

	ENABLING(1),
	DISABLING(2), 
	SIGN_MORE_ARGUMENTS_NEEDED(3), 
	SIGN_NOT_CONFIGURED(4),
	CLICK_TO_USE(5), 
	NO_PERMISSIONS_FOR_THIS(6), 
	ONLY_WALL_SIGN(7),
	
	;
	
	int strID;
	
	StringsID(int strID){
		this.strID = strID;
	}
	
	String getPath(){
		
		//por el momento los strings tienen formato numérico.
		return String.valueOf(strID);
	}
	
	@Override
	public String toString(){
		return Translation.get(getPath(), true)[0];
	}
}
