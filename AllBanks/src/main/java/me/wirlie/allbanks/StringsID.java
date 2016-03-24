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

import java.util.HashMap;

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
	YOU_ARE_RUNNING_A_COMPATIBLE_VERSION_OF_CB(8),
	YOU_ARENT_RUNNING_A_COMPATIBLE_VERSION_OF_CB(9), 
	ASK(10), 
	PAY(11), 
	ALREADY_USING_ANOTHER_BANK(12),
	SESSION_CLOSED(13), 
	BANK_USED_WITH_ANOTHER_PLAYER(14),
	BANK_NOT_REGISTERED_ON_ALLBANKS(15), 
	BANK_REMOVED(16),
	COMMANDS_DATABASE_INVALID_QUERY(17), 
	COMMANDS_DATABASE_QUERY_SUCCESS(18), 
	COMMAND_ONLY_FOR_CONSOLE(19),
	BANKLOAN_STEP0_INFO(20),
	BANKLOAN_STEP1_INFO(21), 
	BANKLOAN_CAN_NOT_BORROW_MORE_LOAN(22),
	ONLY_VALID_NUMBER_MORE_THAN_0(23),
	BANKLOAN_SUCCESS_BORROW(24),
	ONLY_TWO_DECIMALS(25),
	BANKLOAN_SUCCESS_PAY(26),
	DEPOSIT_MONEY(27),
	WITHDRAW_MONEY(28),
	BANKMONEY_MAX_LIMIT_REACHED_1(29),
	YOU_DO_NOT_HAVE_MONEY(30),
	BANKMONEY_SUCCESS_DEPOSIT(31),
	BANKMONEY_SUCCESS_WITHDRAW(32),
	BANKMONEY_STEP0_INFO(33),
	BANKMONEY_STEP1_INFO(34),
	BANKXP_STEP0_INFO(35),
	BANKXP_STEP1_INFO(36),
	DEPOSIT_XP(37),
	WITHDRAW_XP(38),
	BANKXP_ERROR_DEPOSIT_INS_XP(39),
	BANKXP_ERROR_WITHDRAW_INS_XP(40),
	BANKXP_DEPOSIT_SUCCESS(41),
	BANKXP_WITHDRAW_SUCCESS(42),
	CHANGE_TIME(43),
	BANKTIME_STEP0_INFO(44),
	BANKTIME_DO_YOU_DO_NOT_HAVE_TIME(45),
	BANKTIME_SUCCESS(46),
	BANKCHEST_CHEST_NUMBER(47),
	BANKCHEST_STEP0_INFO(48),
	BANKCHEST_VIRTUAL_INVENTORY(49),
	BANKLOAN_INTEREST_CHARGED(50),
	
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
		return Translation.get(getPath(), new HashMap<String, String>(), true)[0];
	}
	
	public String toString(boolean prefix){
		return Translation.get(getPath(), new HashMap<String, String>(), prefix)[0];
	}
	
	public String toString(HashMap<String, String> replaceMap, boolean prefix){
		return Translation.get(getPath(), replaceMap, prefix)[0];
	}
}
