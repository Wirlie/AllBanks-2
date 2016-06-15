package me.wirlie.allbanks.commands.allbanksland;

import java.math.BigDecimal;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.allbanksland.WorldConfiguration;
import me.wirlie.allbanks.allbanksland.AllBanksWorld.WorldGenerationResult;
import me.wirlie.allbanks.allbanksland.generator.WorldGenerationCfg;
import me.wirlie.allbanks.command.Command;
import me.wirlie.allbanks.utils.WorldLoadAsync_1_9_R1;
import me.wirlie.allbanks.utils.WorldLoadAsync_1_9_R2;

/**
 * Comando administrativo de AllBanksLand.
 * @author Wirlie
 */
public class CommandAdmin extends Command {
	
	/**
	 * Comando administrativo.
	 * @param permissionNode Permiso necesario para ejecutar este comando.
	 */
	public CommandAdmin(String permissionNode){
		super(permissionNode);
	}

	@Override
	public CommandExecuteResult execute(CommandSender sender, String label, String[] args){
		
		boolean displayHelp = false;
		
		if(args.length <= 1){
			//	/abland plot
			displayHelp = true;
		}else if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
			displayHelp = true;
		}
		
		if(displayHelp){
			int page = 1;
			int maxPage = 1;
			Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + page, "%2%>>>" + maxPage), true);
			
			sender.sendMessage(ChatColor.GRAY + "/abl world " + ChatColor.GOLD + "<world> " + ChatColor.GRAY + "generate " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_GENERATE, false)[0]);
			sender.sendMessage(ChatColor.GRAY + "/abl world " + ChatColor.GOLD + "<world> " + ChatColor.GRAY + "unload " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_UNLOAD, false)[0]);
			sender.sendMessage(ChatColor.GRAY + "/abl world " + ChatColor.GOLD + "<world> " + ChatColor.GRAY + "remove " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_REMOVE, false)[0]);
			sender.sendMessage(ChatColor.GRAY + "/abl world " + ChatColor.GOLD + "<world> " + ChatColor.GRAY + "info " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_ADMIN_WORLD_INFO, false)[0]);
			sender.sendMessage(ChatColor.GRAY + "/abl world " + ChatColor.GOLD + "<world> " + ChatColor.GRAY + "set " + ChatColor.GOLD + "<flag> <value>");
			
			return CommandExecuteResult.SUCCESS;
		}

		if(args[1].equalsIgnoreCase("world")){
			if(args.length > 3){
				if(args[3].equalsIgnoreCase("generate")){
					
					if(!this.hasPermission(sender)){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						return CommandExecuteResult.NO_PERMISSIONS;
					}
					
					if(!AllBanks.getInstance().getConfig().getBoolean("modules.allbanksland.enable")){
						Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksLand"), true);
						return CommandExecuteResult.OTHER;
					}
					
					String worldName = args[2].toLowerCase();
					AllBanksWorld world = AllBanksWorld.getInstance(worldName);
					
					if(world != null){
						//Este mundo ya existe.
						Translation.getAndSendMessage(sender, 
								StringsID.COMMAND_LAND_ADMIN_GENERATE_WORLD_ERROR_WORLD_ALREADY_EXISTS, 
								Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName),
								true);
						return CommandExecuteResult.OTHER;
					}
					
					//Checar si la configuración de generación para este mundo existe
					if(WorldGenerationCfg.generatorConfigurationFileExists(worldName)){
						//Generar mundo
						try {
							//R1 Support
				    		Class.forName("org.bukkit.craftbukkit.v1_9_R1.CraftServer");
				    		if(WorldLoadAsync_1_9_R1.isBusy()){
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_ERROR_ANOTHER_JOB_IN_PROGRESS, true);
								return CommandExecuteResult.OTHER;
							}
				    	}catch (ClassNotFoundException e) {
				    		try {
								//R1 Support
					    		Class.forName("org.bukkit.craftbukkit.v1_9_R2.CraftServer");
					    		if(WorldLoadAsync_1_9_R2.isBusy()){
									Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_ERROR_ANOTHER_JOB_IN_PROGRESS, true);
									return CommandExecuteResult.OTHER;
								}
					    	}catch (ClassNotFoundException e2) {
					    		e2.printStackTrace();
					    		return CommandExecuteResult.EXCEPTION;
					    	}
				    	}
						
						
						WorldGenerationResult result = AllBanksWorld.generatePlotWorld(worldName, sender);
						
						if(result == WorldGenerationResult.SUCCESS){
							Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_GENERATING, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
						}else{	
							Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_ERROR_GENERATE, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + result), true);
						}
					}else{
						//Okey, generar nueva configuración y notificar al usuario.
						String generatedFilePath = WorldGenerationCfg.makeNewDefaultGeneratorConfigurationFile(worldName);
						
						Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_PRE_SUCCESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + generatedFilePath), true);
					}
				}else if(args[3].equalsIgnoreCase("unload")){
					
					if(!this.hasPermission(sender)){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						return CommandExecuteResult.NO_PERMISSIONS;
					}
					
					if(!AllBanks.getInstance().getConfig().getBoolean("modules.allbanksland.enable")){
						Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksLand"), true);
						return CommandExecuteResult.OTHER;
					}
					
					String worldName = args[2].toLowerCase();
					World world = Bukkit.getWorld(worldName);
					
					if(world != null){
						try {
							//R1 Support
				    		Class.forName("org.bukkit.craftbukkit.v1_9_R1.CraftServer");
				    		if(WorldLoadAsync_1_9_R1.isBusy() && WorldLoadAsync_1_9_R1.lastWorldGenerated.equalsIgnoreCase(worldName)){
								//En generación
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_IN_PROGRESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
								return CommandExecuteResult.OTHER;
							}
				    	}catch (ClassNotFoundException e) {
				    		try {
								//R1 Support
					    		Class.forName("org.bukkit.craftbukkit.v1_9_R2.CraftServer");
					    		if(WorldLoadAsync_1_9_R2.isBusy() && WorldLoadAsync_1_9_R2.lastWorldGenerated.equalsIgnoreCase(worldName)){
									//En generación
									Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_IN_PROGRESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
									return CommandExecuteResult.OTHER;
								}
					    	}catch (ClassNotFoundException e2) {
					    		e2.printStackTrace();
					    		return CommandExecuteResult.EXCEPTION;
					    	}
				    	}
						
						//Descargar mundo
						if(AllBanksWorld.unloadPlotWorld(worldName, true)){
							Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_UNLOAD_SUCCESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
						}else{								
							Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_UNLOAD_ERROR_UNKNOW, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
						}
					}else{
						//Este mundo no existe
						Translation.getAndSendMessage(sender, StringsID.ERROR_WORLD_NOT_LOADED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
					}
				}else if(args[3].equalsIgnoreCase("remove")){
					
					if(!this.hasPermission(sender)){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						return CommandExecuteResult.NO_PERMISSIONS;
					}
					
					if(!AllBanks.getInstance().getConfig().getBoolean("modules.allbanksland.enable")){
						Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksLand"), true);
						return CommandExecuteResult.OTHER;
					}
					
					String worldName = args[2].toLowerCase();
					World world = Bukkit.getWorld(worldName);
					
					if(world != null){
						Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_REMOVE_WORLD_ERROR_WORLD_NEED_UNLOAD, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
						return CommandExecuteResult.OTHER;
					}else{
						try {
							//R1 Support
				    		Class.forName("org.bukkit.craftbukkit.v1_9_R1.CraftServer");
				    		if(WorldLoadAsync_1_9_R1.isBusy() && WorldLoadAsync_1_9_R1.lastWorldGenerated.equalsIgnoreCase(worldName)){
								//En generación
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_IN_PROGRESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
								return CommandExecuteResult.OTHER;
							}
				    	}catch (ClassNotFoundException e) {
				    		try {
								//R1 Support
					    		Class.forName("org.bukkit.craftbukkit.v1_9_R2.CraftServer");
					    		if(WorldLoadAsync_1_9_R2.isBusy() && WorldLoadAsync_1_9_R2.lastWorldGenerated.equalsIgnoreCase(worldName)){
									//En generación
									Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_IN_PROGRESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
									return CommandExecuteResult.OTHER;
								}
					    	}catch (ClassNotFoundException e2) {
					    		e2.printStackTrace();
					    		return CommandExecuteResult.EXCEPTION;
					    	}
				    	}
						
						//Carpeta del mundo
						int check = AllBanksWorld.removePlotWorldFolderAndDataBase(worldName);
						
						if(check == -2){
							//Este mundo no existe
							Translation.getAndSendMessage(sender, StringsID.ERROR_WORLD_NOT_LOADED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
						}else{
							if(check == 1){
								//Exito
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_REMOVE_WORLD_SUCCESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
							}else if(check == 0){
								//delete() == false
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_REMOVE_WORLD_ERROR_UNABLE_DELETE_DIR, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
							}else{
								//Otro (SQLException)
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_REMOVE_WORLD_ERROR_SQL_EXCEPTION, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
							}
						}
					}
				}else if(args[3].equalsIgnoreCase("info")){
					
					if(!this.hasPermission(sender)){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						return CommandExecuteResult.NO_PERMISSIONS;
					}
					
					if(!AllBanks.getInstance().getConfig().getBoolean("modules.allbanksland.enable")){
						Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksLand"), true);
						return CommandExecuteResult.OTHER;
					}
					
					String worldName = args[2].toLowerCase();
					
					if(!AllBanksWorld.worldIsAllBanksWorld(worldName)){
						Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_NOT_IS_A_WORLD_OF_ALLBANKS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
						return CommandExecuteResult.OTHER;
					}
					
					AllBanksWorld abw = AllBanksWorld.getInstance(worldName);
					
					HashMap<String, String> replaceMap = new HashMap<String, String>();
					replaceMap.put("%1%", worldName);

					String flagsLine1 = "";
					WorldConfiguration wcfg = abw.getWorldConfiguration();
					
					if(wcfg.allowNetherPortal()){
						flagsLine1 += ChatColor.GREEN + "allow-nether-portal, ";
					}else{
						flagsLine1 += ChatColor.RED + "allow-nether-portal, ";
					}
					
					if(wcfg.allowTNTExplosion()){
						flagsLine1 += ChatColor.GREEN + "allow-tnt-explosion, ";
					}else{
						flagsLine1 += ChatColor.RED + "allow-tnt-explosion, ";
					}
					
					String flagsLine2 = "";
					
					if(wcfg.allowWither()){
						flagsLine2 += ChatColor.GREEN + "allow-wither, ";
					}else{
						flagsLine2 += ChatColor.RED + "allow-wither, ";
					}
					
					if(wcfg.animalSpawn()){
						flagsLine2 += ChatColor.GREEN + "animal-spawn, ";
					}else{
						flagsLine2 += ChatColor.RED + "animal-spawn, ";
					}
					
					if(wcfg.creeperExplosion()){
						flagsLine2 += ChatColor.GREEN + "creeper-explosion, ";
					}else{
						flagsLine2 += ChatColor.RED + "creeper-explosion, ";
					}
					
					String flagsLine3 = "";
					
					if(wcfg.mobSpawn()){
						flagsLine3 += ChatColor.GREEN + "mob-spawn, ";
					}else{
						flagsLine3 += ChatColor.RED + "mob-spawn, ";
					}
					
					if(wcfg.witherExplosion()){
						flagsLine3 += ChatColor.GREEN + "wither-explosion, ";
					}else{
						flagsLine3 += ChatColor.RED + "wither-explosion, ";
					}
					
					replaceMap.put("%2%", flagsLine1 + "%BREAK%" + flagsLine2 + "%BREAK%" + flagsLine3);
					
					Translation.getAndSendMessage(sender, StringsID.WORLD_PLOT_INFO, replaceMap, true);
					
				}else if(args[3].equalsIgnoreCase("set")){
					
					if(!this.hasPermission(sender)){
						Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
						return CommandExecuteResult.NO_PERMISSIONS;
					}
					
					if(!AllBanks.getInstance().getConfig().getBoolean("modules.allbanksland.enable")){
						Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksLand"), true);
						return CommandExecuteResult.OTHER;
					}
					
					if(args.length >= 6){
						String worldName = args[2].toLowerCase();
						
						if(!AllBanksWorld.worldIsAllBanksWorld(worldName)){
							Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_WORLD_SPAWN_ERROR_WORLD_NOT_IS_A_WORLD_OF_ALLBANKS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
							return CommandExecuteResult.OTHER;
						}
						
						AllBanksWorld abw = AllBanksWorld.getInstance(worldName);
						String flag = args[4];
						String value = args[5];
						
						if(flag.equalsIgnoreCase("allow-nether-portal")){
							if(value.equalsIgnoreCase("true"))
								abw.getWorldConfiguration().allowNetherPortal(true);
							else
								abw.getWorldConfiguration().allowNetherPortal(false);
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Allow-Nether-portal", 
											"%2%>>>" + abw.getWorldConfiguration().allowNetherPortal()
											), 
									true);
						}else if(flag.equalsIgnoreCase("allow-tnt-explosion")){
							if(value.equalsIgnoreCase("true"))
								abw.getWorldConfiguration().allowTNTExplosion(true);
							else
								abw.getWorldConfiguration().allowTNTExplosion(false);
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Allow-TNT-Explosion", 
											"%2%>>>" + abw.getWorldConfiguration().allowTNTExplosion()
											), 
									true);
						}else if(flag.equalsIgnoreCase("allow-wither")){
							if(value.equalsIgnoreCase("true"))
								abw.getWorldConfiguration().allowWither(true);
							else
								abw.getWorldConfiguration().allowWither(false);
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Allow-Wither", 
											"%2%>>>" + abw.getWorldConfiguration().allowWither()
											), 
									true);
						}else if(flag.equalsIgnoreCase("animal-spawn")){
							if(value.equalsIgnoreCase("true"))
								abw.getWorldConfiguration().animalSpawn(true);
							else
								abw.getWorldConfiguration().animalSpawn(false);
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Animal-Spawn", 
											"%2%>>>" + abw.getWorldConfiguration().animalSpawn()
											), 
									true);
						}else if(flag.equalsIgnoreCase("claim-cost")){
							try{
								
								String[] valueSplt = value.split("\\D");
								
								if(valueSplt.length == 2){
									if(valueSplt[1].length() > 2){
										value = valueSplt[0] + "." + valueSplt[1].substring(0, 2);
									}
								}
								
								abw.getWorldConfiguration().claimCost(new BigDecimal(value));
							}catch(NumberFormatException e){
								Translation.getAndSendMessage(sender, StringsID.NO_VALID_NUMBER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + value), true);
							}
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Claim-Cost", 
											"%2%>>>" + AllBanks.getEconomy().format(abw.getWorldConfiguration().claimCost().doubleValue())
											), 
									true);
						}else if(flag.equalsIgnoreCase("creeper-explosion")){
							if(value.equalsIgnoreCase("true"))
								abw.getWorldConfiguration().creeperExplosion(true);
							else
								abw.getWorldConfiguration().creeperExplosion(false);
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Creeper-Explosion", 
											"%2%>>>" + abw.getWorldConfiguration().creeperExplosion()
											), 
									true);
						}else if(flag.equalsIgnoreCase("mob-spawn")){
							if(value.equalsIgnoreCase("true"))
								abw.getWorldConfiguration().mobSpawn(true);
							else
								abw.getWorldConfiguration().mobSpawn(false);
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Mob-Spawn", 
											"%2%>>>" + abw.getWorldConfiguration().mobSpawn()
											), 
									true);
						}else if(flag.equalsIgnoreCase("wither-explosion")){
							if(value.equalsIgnoreCase("true"))
								abw.getWorldConfiguration().witherExplosion(true);
							else
								abw.getWorldConfiguration().witherExplosion(false);
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Wither-Explosion", 
											"%2%>>>" + abw.getWorldConfiguration().witherExplosion()
											), 
									true);
						}else if(flag.equalsIgnoreCase("plots-per-user")){
							try{
								abw.getWorldConfiguration().plotsPerUser(Integer.parseInt(value));
							}catch(NumberFormatException e){
								Translation.getAndSendMessage(sender, StringsID.NO_VALID_NUMBER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + value), true);
							}
							
							Translation.getAndSendMessage(
									sender, 
									StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
									Translation.splitStringIntoReplaceHashMap(">>>", 
											"%1%>>>Plots-Per-User", 
											"%2%>>>" + abw.getWorldConfiguration().plotsPerUser()
											), 
									true);
						}else{
							Translation.getAndSendMessage(sender, StringsID.PLOT_SET_FLAG_ERROR_NOT_EXISTS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + flag), true);
							return CommandExecuteResult.OTHER;
						}
					}
				}else{
					//No cumple con los requisitos: /ab database <arg>
					Translation.getAndSendMessage(sender, 
							StringsID.COMMAND_SUGGEST_HELP, 
							Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab admin world ?"),
							true);
					return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
				}
			}else{
				//No cumple con los requisitos: /ab database <arg>
				Translation.getAndSendMessage(sender, 
						StringsID.COMMAND_SUGGEST_HELP, 
						Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab world ?"),
						true);
				return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
			}
		}
		
		return CommandExecuteResult.SUCCESS;
	}
}
