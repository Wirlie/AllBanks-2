package me.wirlie.allbanks.utils.command.land;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksPlayer;
import me.wirlie.allbanks.land.AllBanksPlot;
import me.wirlie.allbanks.land.AllBanksWorld;
import me.wirlie.allbanks.utils.Util;
import me.wirlie.allbanks.utils.command.Command;

public class CommandPlot extends Command {

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
		
		if(args[1].equalsIgnoreCase("claim")){
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return true;
			}
			
			Player p = (Player) sender;
			
			if(!Util.hasPermission(p, "allbanks.land.commands.plot.claim")){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				return true;
			}
			
			Location loc = p.getLocation();
			
			if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				//No es un mundo de allbanks
				Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
				return true;
			}
			
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				//No es un plot
				Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
				return true;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(plot.hasOwner()){
				//Ya tiene dueño
				Translation.getAndSendMessage(sender, StringsID.PLOT_PLOT_ALREADY_HAS_OWNER, true);
				return true;
			}
			
			//Comprobar límite
			AllBanksPlayer abp = new AllBanksPlayer(abw.getID(), p.getName());
			if((abp.currentPlots() + 1) > abw.getWorldConfiguration().plotsPerUser()){
				Translation.getAndSendMessage(sender, StringsID.PLOT_CLAIM_MAX_REACHED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + abp.currentPlots(), "%2%>>>" + abw.getWorldConfiguration().plotsPerUser()), true);
				return true;
			}
			
			//Listo, claimear
			plot.claim(p.getName());
			
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			replaceMap.put("%1%", String.valueOf(plot.getPlotX()));
			replaceMap.put("%2%", String.valueOf(plot.getPlotZ()));
			replaceMap.put("%3%", plot.getAllBanksWorld().getID());
			
			Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_PLOT_CLAIM_SUCCESS, replaceMap, true);
			
		}else if(args[1].equalsIgnoreCase("unclaim")){
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return true;
			}
			
			Player p = (Player) sender;
			
			if(!Util.hasPermission(p, "allbanks.land.commands.plot.unclaim")){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				return true;
			}
			
			Location loc = p.getLocation();
			
			if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				//No es un mundo de allbanks
				Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
				return true;
			}
			
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				//No es un plot
				Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
				return true;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
				//No tiene dueño o no es el dueño
				Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return true;
			}
			
			//Bien, remover
			plot.unclaim();
			Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_PLOT_UNCLAIM_SUCCESS, true);
		}else if(args[1].equalsIgnoreCase("set")){
			
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return true;
			}
			
			Player p = (Player) sender;
			
			if(!Util.hasPermission(p, "allbanks.land.commands.plot.set.flags")){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				return true;
			}
			
			Location loc = p.getLocation();
			
			if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				//No es un mundo de allbanks
				Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
				return true;
			}
			
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				//No es un plot
				Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
				return true;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
				//No tiene dueño o no es el dueño
				Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				return true;
			}
			
			if(args.length >= 4){
				if(args[2].equalsIgnoreCase("fire-spread")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("fire-spread", "true");
					}else{
						plot.setPlotConfiguration("fire-spread", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Fire-Spread", 
									"%2%>>>" + plot.getPlotConfiguration().fireSpread()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("explosions")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("explosions", "true");
					}else{
						plot.setPlotConfiguration("explosions", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Explosions", 
									"%2%>>>" + plot.getPlotConfiguration().explosions()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("mobs")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("mobs", "true");
					}else{
						plot.setPlotConfiguration("mobs", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Mobs", 
									"%2%>>>" + plot.getPlotConfiguration().mobs()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("pvp")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("pvp", "true");
					}else{
						plot.setPlotConfiguration("pvp", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>PvP", 
									"%2%>>>" + plot.getPlotConfiguration().pvp()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("lava-flow")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("lava-flow", "true");
					}else{
						plot.setPlotConfiguration("lava-flow", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Lava-Flow", 
									"%2%>>>" + plot.getPlotConfiguration().lavaFlow()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("water-flow")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("water-flow", "true");
					}else{
						plot.setPlotConfiguration("water-flow", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Water-Flow", 
									"%2%>>>" + plot.getPlotConfiguration().waterFlow()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("use-door")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("use-door", "true");
					}else{
						plot.setPlotConfiguration("use-door", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Use-Door", 
									"%2%>>>" + plot.getPlotConfiguration().useDoor()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("use-anvil")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("use-anvil", "true");
					}else{
						plot.setPlotConfiguration("use-anvil", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Use-Anvil", 
									"%2%>>>" + plot.getPlotConfiguration().useAnvil()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("use-workbench")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("use-workbench", "true");
					}else{
						plot.setPlotConfiguration("use-workbench", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Use-Workbench", 
									"%2%>>>" + plot.getPlotConfiguration().useWorkbench()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("use-fence-door")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("use-fence-door", "true");
					}else{
						plot.setPlotConfiguration("use-fence-door", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Use-FenceDoor", 
									"%2%>>>" + plot.getPlotConfiguration().useFenceDoor()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("use-enchantment-table")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("use-enchantment-table", "true");
					}else{
						plot.setPlotConfiguration("use-enchantment-table", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Use-EnchantmentTable", 
									"%2%>>>" + plot.getPlotConfiguration().useEnchantmentTable()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("use-lever")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("use-lever", "true");
					}else{
						plot.setPlotConfiguration("use-lever", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Use-Lever", 
									"%2%>>>" + plot.getPlotConfiguration().useLever()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("use-button")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("use-button", "true");
					}else{
						plot.setPlotConfiguration("use-button", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Use-Button", 
									"%2%>>>" + plot.getPlotConfiguration().useButton()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("drop-item")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("drop-item", "true");
					}else{
						plot.setPlotConfiguration("drop-item", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Drop-Item", 
									"%2%>>>" + plot.getPlotConfiguration().dropItem()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("msg-greeting")){
					
					String message = "";
					
					for(int i = 0; i < args.length; i ++){
						if(i == 0 || i == 1 || i == 2) continue;
						
						message += args[i] + " ";
					}
					
					plot.setPlotConfiguration("msg-greeting", message);
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>MsgGreeting", 
									"%2%>>>%BREAK%" + plot.getPlotConfiguration().greetingMessage()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("msg-farewell")){
					
					String message = "";
					
					for(int i = 0; i < args.length; i ++){
						if(i == 0 || i == 1 || i == 2) continue;
						
						message += args[i] + " ";
					}
					
					plot.setPlotConfiguration("msg-farewell", message);
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>MsgFarewell", 
									"%2%>>>%BREAK%" + plot.getPlotConfiguration().farewellMessage()
									), 
							true);
				}else if(args[2].equalsIgnoreCase("allow-entry")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("allow-entry", "true");
					}else{
						plot.setPlotConfiguration("allow-entry", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Allow-Entry", 
									"%2%>>>" + plot.getPlotConfiguration().allowEntry()
									), 
							true);
				}else{
					Translation.getAndSendMessage(
							p,
							StringsID.PLOT_SET_FLAG_ERROR_NOT_EXISTS, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>" + args[2]
									), 
							true);
				}
			}else{
				//argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("add")){
			if(args.length >= 3){
				String playerName = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return true;
				}
				
				Player p = (Player) sender;
				
				if(!Util.hasPermission(p, "allbanks.land.commands.plot.add")){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					return true;
				}
				
				Location loc = p.getLocation();
				
				if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					//No es un mundo de allbanks
					Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
					return true;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//No es un plot
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					return true;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					//No tiene dueño o no es el dueño
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return true;
				}
				
				plot.getPlotConfiguration().addFriend(playerName);
				
				Translation.getAndSendMessage(sender, StringsID.PLOT_ADD_FRIEND, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerName), true);
			}else{
				//Argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("remove")){
			if(args.length >= 3){
				String playerName = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return true;
				}
				
				Player p = (Player) sender;
				
				if(!Util.hasPermission(p, "allbanks.land.commands.plot.add")){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					return true;
				}
				
				Location loc = p.getLocation();
				
				if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					//No es un mundo de allbanks
					Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
					return true;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//No es un plot
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					return true;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					//No tiene dueño o no es el dueño
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return true;
				}
				
				plot.getPlotConfiguration().removeFriend(playerName);
				
				Translation.getAndSendMessage(sender, StringsID.PLOT_REMOVE_FRIEND, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerName), true);
			}else{
				//Argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("deny")){
			if(args.length >= 3){
				String playerName = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return true;
				}
				
				Player p = (Player) sender;
				
				if(!Util.hasPermission(p, "allbanks.land.commands.plot.deny")){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					return true;
				}
				
				Location loc = p.getLocation();
				
				if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					//No es un mundo de allbanks
					Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
					return true;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//No es un plot
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					return true;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					//No tiene dueño o no es el dueño
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return true;
				}
				
				plot.getPlotConfiguration().setDeny(playerName);
				
				Translation.getAndSendMessage(sender, StringsID.PLOT_DENY_PLAYER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerName), true);
			}else{
				//Argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("undeny")){
			if(args.length >= 3){
				String playerName = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return true;
				}
				
				Player p = (Player) sender;
				
				if(!Util.hasPermission(p, "allbanks.land.commands.plot.deny")){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					return true;
				}
				
				Location loc = p.getLocation();
				
				if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					//No es un mundo de allbanks
					Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
					return true;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//No es un plot
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					return true;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					//No tiene dueño o no es el dueño
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return true;
				}
				
				plot.getPlotConfiguration().setUndeny(playerName);
				
				Translation.getAndSendMessage(sender, StringsID.PLOT_UNDENY_PLAYER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerName), true);
			}else{
				//Argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("setHomeSpawn")){
			
		}else if(args[1].equalsIgnoreCase("setShopSpawn")){
			
		}
		
		return true;
	}
}
