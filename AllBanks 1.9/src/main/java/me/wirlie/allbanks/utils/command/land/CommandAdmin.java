package me.wirlie.allbanks.utils.command.land;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksWorld;
import me.wirlie.allbanks.land.AllBanksWorld.WorldGenerationResult;
import me.wirlie.allbanks.utils.command.Command;

public class CommandAdmin extends Command {

	@Override
	public boolean execute(CommandSender sender, String[] args){
		
		boolean displayHelp = false;
		
		if(args.length <= 1){
			//	/abland plot
			displayHelp = true;
		}else if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
			displayHelp = true;
		}
		
		if(displayHelp){
			
			return true;
		}

		if(args[1].equalsIgnoreCase("teleport")){
			if(args.length > 3){
				String player = args[2];
				String world = args[3];
				
				Bukkit.getPlayer(player).teleport(Bukkit.getWorld(world).getSpawnLocation());
			}else{
				//No cumple con los requisitos: /ab database <arg>
				Translation.getAndSendMessage(sender, 
						StringsID.COMMAND_SUGGEST_HELP, 
						Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab teleport ?"),
						true);
				return true;
			}
		}else if(args[1].equalsIgnoreCase("generate")){
			if(args.length > 2){
				if(args[2].equalsIgnoreCase("world")){
					if(args.length > 3){
						String worldName = args[3];
						AllBanksWorld world = AllBanksWorld.getPlotWorld(worldName);
						
						if(world != null){
							//Este mundo ya existe.
							Translation.getAndSendMessage(sender, 
									StringsID.COMMAND_LAND_ADMIN_GENERATE_WORLD_ERROR_WORLD_ALREADY_EXISTS, 
									Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName),
									true);
							return true;
						}
						
						//Generar mundo
						WorldGenerationResult result = AllBanksWorld.generatePlotWorld(worldName, sender);
						
						if(result == WorldGenerationResult.SUCCESS){
							Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_GENERATING, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
						}else{	
							Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_GENERATE_WORLD_ERROR_GENERATE, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + result), true);
						}
					}
				}
			}else{
				//No cumple con los requisitos: /ab database <arg>
				Translation.getAndSendMessage(sender, 
						StringsID.COMMAND_SUGGEST_HELP, 
						Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab world ?"),
						true);
				return true;
			}
		}else if(args[1].equalsIgnoreCase("unload")){
			if(args.length > 2){
				if(args[2].equalsIgnoreCase("world")){
					if(args.length > 3){
						String worldName = args[3];
						World world = Bukkit.getWorld(worldName);
						
						if(world != null){
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
					}else{
						Translation.getAndSendMessage(sender, 
								StringsID.COMMAND_SUGGEST_HELP, 
								Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab world ?"),
								true);
						return true;
					}
				}
			}else{
				Translation.getAndSendMessage(sender, 
						StringsID.COMMAND_SUGGEST_HELP, 
						Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab world ?"),
						true);
				return true;
			}
		}else if(args[1].equalsIgnoreCase("remove")){
			if(args.length > 2){
				if(args[2].equalsIgnoreCase("world")){
					if(args.length > 3){
						String worldName = args[3];
						World world = Bukkit.getWorld(worldName);
						
						if(world != null){
							//Descargar mundo
							if(!AllBanksWorld.unloadPlotWorld(worldName, false)){
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_UNLOAD_ERROR_UNKNOW, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
								return true;
							}
							
							//Remover carpeta
							int removeResult = AllBanksWorld.removePlotWorldFolder(worldName);
							System.out.println(removeResult);
							if(removeResult == 1){
								//Exito
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_REMOVE_WORLD_SUCCESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
							}else if(removeResult == 0){
								//delete() == false
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_REMOVE_WORLD_ERROR_UNABLE_DELETE_DIR, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
							}else{
								//Otro (SQLException)
								Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_REMOVE_WORLD_ERROR_SQL_EXCEPTION, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + worldName), true);
							}
						}else{
							
							//Carpeta del mundo
							int check = AllBanksWorld.removePlotWorldFolder(worldName);
							
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
					}else{
						Translation.getAndSendMessage(sender, 
								StringsID.COMMAND_SUGGEST_HELP, 
								Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab world ?"),
								true);
						return true;
					}
				}else{
					//No cumple con los requisitos: /ab database <arg>
					Translation.getAndSendMessage(sender, 
							StringsID.COMMAND_SUGGEST_HELP, 
							Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab world ?"),
							true);
					return true;
				}
			}
		}
		
		return true;
	}
}
