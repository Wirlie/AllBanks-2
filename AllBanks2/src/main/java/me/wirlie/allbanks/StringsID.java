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
 * Clase encargada de interpretar el ID de las traducciones.
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
	DATABASE_IS_LOCKED_PLEASE_RESTART_SERVER(51),
	SQL_EXCEPTION_PROBLEM(52),
	NO_VALID_NUMBER(53),
	LOTTERY_CAN_NOT_BUY_MORE_TICKETS(54),
	LOTTERY_BUY_TICKETS_SUCCESS(55), 
	LOTTERY_CHECK_ERROR_NO_LOTTERY_FILE(56),
	LOTTER_CHECK_INFO(57),
	LOTTERY_COMMAND_ENABLE(58),
	LOTTERY_COMMAND_DISABLE(59),
	LOTTERY_BROADCAST_WINNER(60),
	LOTTERY_YOU_WON_THE_LOTTERY(61),
	COMMAND_ONLY_FOR_PLAYER(62),
	LOTTERY_BROADCAST_FORCE_BY_ADMIN(63),
	NOT_YET_IMPLEMENTED(64),
	COMMAND_ONLY_AVAILABLE_ON_DATABASE(65),
	COMMAND_TOPRANK_GENERATING(66),
	COMMAND_TOPRANK_NO_STATS(67),
	COMMAND_TOPRANK_BANKMONEY_HEADER(68),
	COMMAND_TOPRANK_LATEST_UPDATE(69),
	WEEKS(70),
	DAYS(71),
	HOURS(72),
	MINUTES(73),
	SECONDS(74),
	LEVELS(75),
	COMMAND_TOPRANK_BANKXP_HEADER(76),
	COMMAND_MAIN_INFO(77),
	COMMAND_HELP_HELP_DESC(78),
	COMMAND_HELP_HEADER(79),
	COMMAND_HELP_TOPRANK_DESC(80),
	COMMAND_HELP_LOTTERY_INFO_DESC(81),
	COMMAND_HELP_LOTTERY_BUYTICKET_DESC(82),
	COMMAND_HELP_LOTTERY_ENABLE_DESC(83),
	COMMAND_HELP_LOTTERY_FORCE_DESC(84),
	COMMAND_HELP_RELOAD_DESC(85),
	COMMAND_RELOAD_SUCCESS(86),
	SHOP_NO_PERMISSIONS_FOR_ADMIN_SHOP(87),
	SHOP_PRICE_LINE_NOT_VALID(88),
	UNDEFINED(89),
	SHOP_WARNING_ITEM_NAME(90),
	SHOP_NEW_SHOP(91),
	COMMAND_HELP_ITEMINFO_DESC(92),
	NAME(93), 
	DURABILITY(94), 
	SHOP_FOR_SHOP_LINE(95),
	SHOP_ERROR_NO_CHEST_FOUND(96),
	SHOP_CONFIGURE_NEEDED(97),
	SHOP_IS_NOT_CONFIGURED(98),
	SHOP_CONFIGURATION_SUCCESS(99),
	SHOP_REMOVED(100),
	SHOP_NO_YOUR_OWN(101),
	SHOP_CANNOT_USE_YOUR_SHOP(102),
	SHOP_NOT_SUPPORT_SELL_ACTION(103),
	SHOP_NOT_SUPPORT_BUY_ACTION(104),
	SHOP_ERROR_ITEM_MAX_64(105),
	SHOP_PLAYER_NO_HAVE_THIS_ITEM(106),
	SHOP_ERROR_OWNER_OF_SHOP_CANNOT_HAVE_MONEY_FOR_BUY(107),
	SHOP_SUCCESS_BUY(108),
	SHOP_SUCCESS_SELL(109),
	SHOP_ERROR_SHOPCHEST_NOT_HAVE_SPACE(110), 
	SHOP_ERROR_PLAYER_NOT_HAVE_SPACE(111),
	SHOP_OUT_OF_STOCK(112),
	SHOP_ANOTHER_SHOP_USES_THIS_CHEST(113), 
	COMMAND_SUGGEST_HELP(114),
	COMMAND_SYNTAX_ERROR(115), 
	COMMAND_HELP_DATABASE_QUERY(116),
	COMMAND_HELP_DATABASE_UPDATE(117),
	UPDATER_PLEASE_RELOAD_ALLBANKS(118),
	COMMAND_NO_ARGUMENT_MATCH(119),
	COMMAND_POSSIBLE_COMMANDS_HEADER(120), 
	COMMAND_POSSIBLE_COMMAND_HIGH(121),
	COMMAND_LAND_ADMIN_GENERATE_WORLD_ERROR_WORLD_ALREADY_EXISTS(122), 
	COMMAND_LAND_GENERATE_WORLD_GENERATING(123), 
	COMMAND_LAND_GENERATE_WORLD_ERROR_GENERATE(124),
	COMMAND_LAND_GENERATE_WORLD_GENERATING_FINISH(125),
	ERROR_WORLD_NOT_LOADED(126),
	COMMAND_LAND_UNLOAD_SUCCESS(127),
	COMMAND_LAND_UNLOAD_ERROR_UNKNOW(128), 
	COMMAND_LAND_REMOVE_WORLD_SUCCESS(129), 
	COMMAND_LAND_REMOVE_WORLD_ERROR_UNABLE_DELETE_DIR(130),
	COMMAND_LAND_REMOVE_WORLD_ERROR_SQL_EXCEPTION(131),
	COMMAND_LAND_REMOVE_WORLD_ERROR_WORLD_NEED_UNLOAD(132),
	COMMAND_LAND_GENERATE_WORLD_PRE_SUCCESS(133),
	COMMAND_LAND_GENERATE_WORLD_ERROR_ANOTHER_JOB_IN_PROGRESS(134),
	COMMAND_LAND_GENERATE_WORLD_GENERATING_PROGRESS(135),
	COMMAND_LAND_GENERATE_WORLD_ERROR_ALREADY_LOADED(136),
	COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_IN_PROGRESS(137),
	COMMAND_LAND_WORLD_SPAWN_SUCCESS(138),
	COMMAND_LAND_WORLD_SPAWN_ERROR_BUKKIT_NULL(139),
	COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_NOT_IS_A_WORLD_OF_ALLBANKS(140),
	PLOT_NOT_IS_YOUR_OWN_PLOT(141),
	PLOT_INVALID_WORLD(142),
	PLOT_LOC_NOT_IS_PLOT(143),
	PLOT_PLOT_ALREADY_HAS_OWNER(144),
	COMMAND_LAND_PLOT_CLAIM_SUCCESS(145),
	COMMAND_LAND_PLOT_UNCLAIM_SUCCESS(146),
	PLOT_ADD_FRIEND(147),
	PLOT_REMOVE_FRIEND(148),
	PLOT_DENY_PLAYER(149),
	PLOT_UNDENY_PLAYER(150),
	PLOT_NOT_ALLOW_TO_ENTRY(151),
	PLOT_GREETING_FORMAT(152),
	PLOT_FAREWELL_FORMAT(153),
	PLOT_SET_FLAG_CHANGE_INFO(154),
	PLOT_SET_FLAG_ERROR_NOT_EXISTS(155),
	PLOT_CLAIM_MAX_REACHED(156),
	
	;
	
	int strID;
	
	StringsID(int strID){
		this.strID = strID;
	}
	
	/**
	 * Obtener la ruta de la traducción desde el archivo de lenguaje.
	 * @return Ruta en donde se encuentra la traducción buscada.
	 */
	String getPath(){
		//por el momento los strings tienen formato numérico.
		return String.valueOf(strID);
	}
	
	/**
	 * Método util para transformar cualquier valor de este enumerador a string.
	 * @return Cadena de texto conteniendo la traducción correspondiente.
	 */
	@Override
	public String toString(){
		return Translation.get(getPath(), new HashMap<String, String>(), true, false)[0];
	}
	
	/**
	 * Método util para transformar cualquier valor de este enumerador a string.
	 * @param prefix ¿Conservar prefix de AllBanks?
	 * @return Cadena de texto conteniendo la traducción correspondiente.
	 */
	public String toString(boolean prefix){
		return Translation.get(getPath(), new HashMap<String, String>(), prefix, false)[0];
	}
	
	/**
	 * Método util para transformar cualquier valor de este enumerador a string.
	 * @param replaceMap Mapa para reemplazar valores.
	 * @param prefix ¿Conservar prefix de AllBanks?
	 * @return Cadena de texto conteniendo la traducción correspondiente.
	 */
	public String toString(HashMap<String, String> replaceMap, boolean prefix){
		return Translation.get(getPath(), replaceMap, prefix, false)[0];
	}
}