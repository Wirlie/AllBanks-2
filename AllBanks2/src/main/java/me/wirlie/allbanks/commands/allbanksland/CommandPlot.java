package me.wirlie.allbanks.commands.allbanksland;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlayer;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.allbanksland.PlotConfiguration;
import me.wirlie.allbanks.allbanksland.PlotID;
import me.wirlie.allbanks.command.Command;
import me.wirlie.allbanks.utils.InteractiveUtil;
import me.wirlie.allbanks.utils.InteractiveUtil.SoundType;
import me.wirlie.allbanks.utils.chatcomposer.BuildChatMessage;

public class CommandPlot extends Command {
	
	private static HashMap<String, PlotClearTokenStruct> plotClearTokens = new HashMap<String, PlotClearTokenStruct>();
	
	class PlotClearTokenStruct{
		int plot_X;
		int plot_Z;
		AllBanksWorld plot_world;
		String token;
	}
	
	public CommandPlot(String permissionNode){
		super(permissionNode);
	}

	@Override
	public CommandExecuteResult execute(CommandSender sender, String label, String[] args){
		
		boolean displayHelp = false;
		String labelPrefix = "/abl plot";
		
		if(label.equalsIgnoreCase("plot")){
			labelPrefix = "/plot";
		}
		
		if(args.length <= 1){ 
			//	/abland plot
			displayHelp = true;
		}else if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
			displayHelp = true;
		}
		
		if(displayHelp){
			int page = 1;
			int maxPage = 2;
			Translation.getAndSendMessage(sender, StringsID.COMMAND_HELP_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + page, "%2%>>>" + maxPage), true);
			
			if(args.length >= 3){
				try{
					page = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					page = 1;
				}
			}
			
			switch(page){
			default:
			case 1:
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " claim " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_CLAIM_DESC, false)[0]);
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " dispose " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_DISPOSE_DESC, false)[0]);
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " set " + ChatColor.GOLD + "<flag> <value> ");
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " info " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_INFO_DESC, false)[0]);
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " add " + ChatColor.GOLD + "<player> " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_ADD_DESC, false)[0]);
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " remove " + ChatColor.GOLD + "<player> " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_REMOVE_DESC, false)[0]);
			break;
			case 2:
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " deny " + ChatColor.GOLD + "<player> " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_DENY_DESC, false)[0]);
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " undeny " + ChatColor.GOLD + "<player> " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_UNDENY_DESC, false)[0]);
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " setHomeSpawn " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_SETHOMESPAWN_DESC, false)[0]);
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " setShopSpawn " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_SETSHOPPAWN_DESC, false)[0]);
				sender.sendMessage(ChatColor.GRAY + labelPrefix + " home " + ChatColor.DARK_AQUA + "[#] " + ChatColor.WHITE + "- " + Translation.get(StringsID.COMMAND_LAND_PLOT_HOME_DESC, false)[0]);
				break;
			}	
			return CommandExecuteResult.SUCCESS;
		}
		
		if(args[1].equalsIgnoreCase("claim")){
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}

			Player p = (Player) sender;
			
			if(!AllBanks.getInstance().getConfig().getBoolean("modules.allbanksland.enable")){
				Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksLand"), true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			if(!this.hasPermission(sender)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.SUCCESS;
			}
			
			Location loc = p.getLocation();
			
			if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				//No es un mundo de allbanks
				Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				//No es un plot
				Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(plot.hasOwner()){
				//Ya tiene dueño
				Translation.getAndSendMessage(sender, StringsID.PLOT_PLOT_ALREADY_HAS_OWNER, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			//Comprobar límite
			AllBanksPlayer abp = new AllBanksPlayer(p.getName());
			
			//Permiso especial
			int plotLimit = abw.getWorldConfiguration().plotsPerUser();
			
			for(PermissionAttachmentInfo pinfo : p.getEffectivePermissions()){
				if(pinfo.getPermission().startsWith("allbanks.land." + abw.getID() + ".plot-limit.")){
					try{
						plotLimit = Integer.parseInt(pinfo.getPermission().replace("allbanks.land." + abw.getID() + ".plot-limit.", ""));
					}catch(NumberFormatException e2){
						plotLimit = abw.getWorldConfiguration().plotsPerUser();
					}
				}
			}
			
			int currentPlots = abp.currentPlots(abw.getID());
			
			if((currentPlots + 1) > plotLimit){
				Translation.getAndSendMessage(sender, StringsID.PLOT_CLAIM_MAX_REACHED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + currentPlots, "%2%>>>" + abw.getWorldConfiguration().plotsPerUser()), true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.OTHER;
			}
			
			//Dinero??
			if(!AllBanks.getEconomy().has(p, abw.getWorldConfiguration().claimCost().doubleValue())){
				Translation.getAndSendMessage(sender, StringsID.PLOT_CLAIM_INSUFICIENT_MONEY, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + AllBanks.getEconomy().format(abw.getWorldConfiguration().claimCost().doubleValue())), true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.OTHER;
			}
			
			//Listo, claimear
			plot.claim(p.getName());
			
			AllBanks.getEconomy().withdrawPlayer(p, abw.getWorldConfiguration().claimCost().doubleValue());
			
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			replaceMap.put("%1%", String.valueOf(plot.getPlotX()));
			replaceMap.put("%2%", String.valueOf(plot.getPlotZ()));
			replaceMap.put("%3%", plot.getAllBanksWorld().getID());
			
			Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_PLOT_CLAIM_SUCCESS, replaceMap, true);
			InteractiveUtil.sendSound(p, SoundType.SUCCESS);
			return CommandExecuteResult.SUCCESS;
		}else if(args[1].equalsIgnoreCase("dispose")){
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			
			if(!this.hasPermission(sender)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			Location loc = p.getLocation();
			
			if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				//No es un mundo de allbanks
				Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				//No es un plot
				Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
				//No tiene dueño o no es el dueño
				Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			//Bien, remover
			plot.unclaim();
			Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_PLOT_UNCLAIM_SUCCESS, true);
			InteractiveUtil.sendSound(p, SoundType.SUCCESS);
			
			return CommandExecuteResult.SUCCESS;
		}else if(args[1].equalsIgnoreCase("set")){
			
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			
			if(!this.hasPermission(sender)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			Location loc = p.getLocation();
			
			if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				//No es un mundo de allbanks
				Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
			
			if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
				//No es un plot
				Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
			
			if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
				//No tiene dueño o no es el dueño
				Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.OTHER;
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
				}else if(args[2].equalsIgnoreCase("use-fence-gate")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("use-fence-gate", "true");
					}else{
						plot.setPlotConfiguration("use-fence-gate", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Use-FenceDoor", 
									"%2%>>>" + plot.getPlotConfiguration().useFenceGate()
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
				}else if(args[2].equalsIgnoreCase("allow-plot-teleport")){
					if(args[3].equalsIgnoreCase("true")){
						plot.setPlotConfiguration("allow-plot-teleport", "true");
					}else{
						plot.setPlotConfiguration("allow-plot-teleport", "false");
					}
					
					Translation.getAndSendMessage(
							p, 
							StringsID.PLOT_SET_FLAG_CHANGE_INFO, 
							Translation.splitStringIntoReplaceHashMap(">>>", 
									"%1%>>>Allow-Plot-Teleport", 
									"%2%>>>" + plot.getPlotConfiguration().allowTeleport()
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
				
				InteractiveUtil.sendSound(p, SoundType.SUCCESS);
				return CommandExecuteResult.SUCCESS;
			}else{
				//argumentos inválidos
				return CommandExecuteResult.INVALID_ARGUMENTS;
			}
		}else if(args[1].equalsIgnoreCase("add")){
			if(args.length >= 3){
				String playerName = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return CommandExecuteResult.OTHER;
				}
				
				Player p = (Player) sender;
				
				if(!this.hasPermission(sender)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				Location loc = p.getLocation();
				
				if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					//No es un mundo de allbanks
					Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//No es un plot
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					//No tiene dueño o no es el dueño
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.OTHER;
				}
				
				plot.getPlotConfiguration().addFriend(playerName);
				
				Translation.getAndSendMessage(sender, StringsID.PLOT_ADD_FRIEND, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerName), true);
				InteractiveUtil.sendSound(p, SoundType.SUCCESS);
				return CommandExecuteResult.SUCCESS;
			}else{
				//Argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("remove")){
			if(args.length >= 3){
				String playerName = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return CommandExecuteResult.OTHER;
				}
				
				Player p = (Player) sender;
				
				if(!this.hasPermission(sender)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				Location loc = p.getLocation();
				
				if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					//No es un mundo de allbanks
					Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//No es un plot
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					//No tiene dueño o no es el dueño
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.OTHER;
				}
				
				plot.getPlotConfiguration().removeFriend(playerName);
				
				Translation.getAndSendMessage(sender, StringsID.PLOT_REMOVE_FRIEND, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerName), true);
				InteractiveUtil.sendSound(p, SoundType.SUCCESS);
				return CommandExecuteResult.SUCCESS;
			}else{
				//Argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("deny")){
			if(args.length >= 3){
				String playerName = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return CommandExecuteResult.OTHER;
				}
				
				Player p = (Player) sender;
				
				if(!this.hasPermission(sender)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				Location loc = p.getLocation();
				
				if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					//No es un mundo de allbanks
					Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//No es un plot
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					//No tiene dueño o no es el dueño
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.OTHER;
				}
				
				plot.getPlotConfiguration().setDeny(playerName);
				
				Translation.getAndSendMessage(sender, StringsID.PLOT_DENY_PLAYER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerName), true);
				InteractiveUtil.sendSound(p, SoundType.SUCCESS);
				return CommandExecuteResult.SUCCESS;
			}else{
				//Argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("undeny")){
			if(args.length >= 3){
				String playerName = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return CommandExecuteResult.OTHER;
				}
				
				Player p = (Player) sender;
				
				if(!this.hasPermission(sender)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				Location loc = p.getLocation();
				
				if(!AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
					//No es un mundo de allbanks
					Translation.getAndSendMessage(sender, StringsID.PLOT_INVALID_WORLD, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					//No es un plot
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner() || plot.hasOwner() && !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					//No tiene dueño o no es el dueño
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.OTHER;
				}
				
				plot.getPlotConfiguration().setUndeny(playerName);
				
				Translation.getAndSendMessage(sender, StringsID.PLOT_UNDENY_PLAYER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerName), true);
				InteractiveUtil.sendSound(p, SoundType.SUCCESS);
				return CommandExecuteResult.SUCCESS;
			}else{
				//Argumentos inválidos
			}
		}else if(args[1].equalsIgnoreCase("setHomeSpawn")){
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			Location loc = p.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				
				if(!this.hasPermission(p)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				if(!plot.havePermissions(p) || !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				//Solo el dueño puede establecer esto
				plot.getPlotConfiguration().setPlotSpawnLocation(loc);
				Translation.getAndSendMessage(sender, StringsID.PLOT_SET_HOME_BLOCK_SUCCESS, true);
				InteractiveUtil.sendSound(p, SoundType.SUCCESS);
				return CommandExecuteResult.SUCCESS;
			}
		}else if(args[1].equalsIgnoreCase("home")){
			
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			
			if(!this.hasPermission(p)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			if(!AllBanks.getInstance().getConfig().getBoolean("modules.allbanksland.enable")){
				Translation.getAndSendMessage(sender, StringsID.MODULE_DISABLED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>AllBanksLand"), true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			int home = 1;
			
			if(args.length >= 3){
				home = Integer.parseInt(args[2]);
			}
			
			if(home <= 0) home = 1;
			
			AllBanksPlayer player = new AllBanksPlayer(sender.getName());
			
			List<PlotID> plots = player.getOwnedPlots();
			
			if(home > plots.size()){
				home = plots.size();
			}
			
			if(plots.isEmpty()){
				Translation.getAndSendMessage(p, StringsID.PLOT_HOME_ERROR_NOT_PLOTS, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			PlotID pid = plots.get(home - 1);
			
			String worldID = pid.getWorldID();
			int plotX = pid.getPlotX();
			int plotZ = pid.getPlotZ();
			
			if(!AllBanksWorld.worldIsAllBanksWorld(worldID)){
				//¿¿Error??
				p.sendMessage("Error, invalid World. (Reason: Unknow).");
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			AllBanksWorld abw = AllBanksWorld.getInstance(worldID);
			AllBanksPlot plot = new AllBanksPlot(plotX, plotZ, abw);
			
			if(plot.getPlotConfiguration().plotSpawnLoc() != null)
				p.teleport(plot.getPlotConfiguration().plotSpawnLoc());
			else
				p.teleport(plot.getFirstBound());
			
			return CommandExecuteResult.SUCCESS;
		}else if(args[1].equalsIgnoreCase("setShopSpawn")){
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			
			if(!this.hasPermission(p)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			Location loc = p.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(!plot.hasOwner()){
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.OTHER;
				}
				
				if(!plot.havePermissions(p) || !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				//Solo el dueño puede establecer esto
				plot.getPlotConfiguration().setShopSpawnLocation(loc);
				Translation.getAndSendMessage(sender, StringsID.PLOT_SET_HOME_BLOCK_SUCCESS, true);
				InteractiveUtil.sendSound(p, SoundType.SUCCESS);
				return CommandExecuteResult.SUCCESS;
			}
		}else if(args[1].equalsIgnoreCase("info")){
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			Location loc = p.getLocation();
			
			if(!this.hasPermission(p)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
					
					if(plot.hasOwner()){
						
						HashMap<String, String> replaceMap = new HashMap<String, String>();
						
						replaceMap.put("%1%", plot.getPlotX() + "," + plot.getPlotZ() + " (" + abw.getID() + ")");
						replaceMap.put("%2%", plot.getOwnerName());
						replaceMap.put("%3%", "Coming soon... (Not Implemented)");
						
						String friends = "";
						
						for(String s : plot.getPlotConfiguration().getFriends()){
							friends += s + ", ";
						}
						
						if(friends.equalsIgnoreCase("") || friends.equalsIgnoreCase(", ")) friends = Translation.get(StringsID.NONE, false)[0];
						replaceMap.put("%4%", friends);
						
						String flagsLine1 = "";
						
						PlotConfiguration plotc = plot.getPlotConfiguration();
						
						if(plotc.allowEntry()){
							flagsLine1 += ChatColor.GREEN + "allow-entry, ";
						}else{
							flagsLine1 += ChatColor.RED + "allow-entry, ";
						}
						
						if(plotc.dropItem()){
							flagsLine1 += ChatColor.GREEN + "drop-item, ";
						}else{
							flagsLine1 += ChatColor.RED + "drop-item, ";
						}
						
						if(plotc.explosions()){
							flagsLine1 += ChatColor.GREEN + "explosions, ";
						}else{
							flagsLine1 += ChatColor.RED + "explosions, ";
						}
						
						if(plotc.fireSpread()){
							flagsLine1 += ChatColor.GREEN + "fire-spread,";
						}else{
							flagsLine1 += ChatColor.RED + "fire-spread,";
						}
						
						String flagsLine2 = "";
						
						if(plotc.lavaFlow()){
							flagsLine2 += ChatColor.GREEN + "lava-flow, ";
						}else{
							flagsLine2 += ChatColor.RED + "lava-flow, ";
						}
						
						if(plotc.mobs()){
							flagsLine2 += ChatColor.GREEN + "mobs, ";
						}else{
							flagsLine2 += ChatColor.RED + "mobs, ";
						}
						
						if(plotc.pvp()){
							flagsLine2 += ChatColor.GREEN + "pvp, ";
						}else{
							flagsLine2 += ChatColor.RED + "pvp, ";
						}
						
						if(plotc.useAnvil()){
							flagsLine2 += ChatColor.GREEN + "use-anvil, ";
						}else{
							flagsLine2 += ChatColor.RED + "use-anvil, ";
						}
						
						if(plotc.useButton()){
							flagsLine2 += ChatColor.GREEN + "use-button, ";
						}else{
							flagsLine2 += ChatColor.RED + "use-button, ";
						}
						
						if(plotc.useDoor()){
							flagsLine2 += ChatColor.GREEN + "use-door,";
						}else{
							flagsLine2 += ChatColor.RED + "use-door,";
						}
						
						String flagsLine3 = "";
						
						if(plotc.useEnchantmentTable()){
							flagsLine3 += ChatColor.GREEN + "use-enchantment-table, ";
						}else{
							flagsLine3 += ChatColor.RED + "use-enchantment-table, ";
						}
						
						if(plotc.useFenceGate()){
							flagsLine3 += ChatColor.GREEN + "use-fence-gate, ";
						}else{
							flagsLine3 += ChatColor.RED + "use-fence-gate, ";
						}
						
						if(plotc.useLever()){
							flagsLine3 += ChatColor.GREEN + "use-lever,";
						}else{
							flagsLine3 += ChatColor.RED + "use-lever,";
						}
						
						String flagsLine4 = "";
						
						if(plotc.usePressurePlate()){
							flagsLine4 += ChatColor.GREEN + "use-pressure-plate, ";
						}else{
							flagsLine4 += ChatColor.RED + "use-pressure-plate, ";
						}
						
						if(plotc.useWorkbench()){
							flagsLine4 += ChatColor.GREEN + "use-workbench, ";
						}else{
							flagsLine4 += ChatColor.RED + "use-workbench, ";
						}
						
						if(plotc.waterFlow()){
							flagsLine4 += ChatColor.GREEN + "water-flow,";
						}else{
							flagsLine4 += ChatColor.RED + "water-flow,";
						}
						
						String flagsLine5 = "";
						
						if(plotc.allowTeleport()){
							flagsLine5 += ChatColor.GREEN + "allow-plot-teleport,";
						}else{
							flagsLine5 += ChatColor.RED + "allow-plot-teleport,";
						}
						
						replaceMap.put("%5%", flagsLine1 + "%BREAK%" + flagsLine2 + "%BREAK%" + flagsLine3 + "%BREAK%" + flagsLine4 + "%BREAK%" + flagsLine5);
						
						String deny = "";
						
						for(String s : plot.getPlotConfiguration().getDenyPlayers()){
							deny += s + ", ";
						}
						
						if(deny.equalsIgnoreCase("") || deny.equalsIgnoreCase(", ")) deny = Translation.get(StringsID.NONE, false)[0];
						replaceMap.put("%6%", deny);
						
						Translation.getAndSendMessage(sender, StringsID.PLOT_INFO, replaceMap, false);
						InteractiveUtil.sendSound(p, SoundType.SUCCESS);
						return CommandExecuteResult.SUCCESS;
					}else{
						InteractiveUtil.sendSound(p, SoundType.WARNING);
						Translation.getAndSendMessage(p, StringsID.PLOT_INFO_NO_OWNER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + plot.getPlotX() + "," + plot.getPlotZ()), true);
					}
				}else{					
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					Translation.getAndSendMessage(p, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					return CommandExecuteResult.OTHER;
				}
			}else{
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				Translation.getAndSendMessage(p, StringsID.PLOT_INVALID_WORLD, true);
				return CommandExecuteResult.OTHER;
			}
		}else if(args[1].equalsIgnoreCase("autoclaim") || args[1].equalsIgnoreCase("auto")){
			//Claimer automáticamente
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			
			if(!this.hasPermission(p)){
				Translation.getAndSendMessage(p, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			if(!AllBanksWorld.worldIsAllBanksWorld(p.getLocation().getWorld().getName())){
				Translation.getAndSendMessage(p, StringsID.PLOT_INVALID_WORLD, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			AllBanksWorld abw = AllBanksWorld.getInstance(p.getLocation().getWorld().getName());
			
			//Comprobar límite
			AllBanksPlayer abp = new AllBanksPlayer(p.getName());
			
			//Permiso especial
			int plotLimit = abw.getWorldConfiguration().plotsPerUser();
			
			for(PermissionAttachmentInfo pinfo : p.getEffectivePermissions()){
				if(pinfo.getPermission().startsWith("allbanks.land." + abw.getID() + ".plot-limit.")){
					try{
						plotLimit = Integer.parseInt(pinfo.getPermission().replace("allbanks.land." + abw.getID() + ".plot-limit.", ""));
					}catch(NumberFormatException e2){
						plotLimit = abw.getWorldConfiguration().plotsPerUser();
					}
				}
			}
			
			int currentPlots = abp.currentPlots(abw.getID());
			
			if((currentPlots + 1) > plotLimit){
				Translation.getAndSendMessage(sender, StringsID.PLOT_CLAIM_MAX_REACHED, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + currentPlots, "%2%>>>" + abw.getWorldConfiguration().plotsPerUser()), true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			//Dinero??
			if(!AllBanks.getEconomy().has(p, abw.getWorldConfiguration().claimCost().doubleValue())){
				Translation.getAndSendMessage(sender, StringsID.PLOT_CLAIM_INSUFICIENT_MONEY, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + AllBanks.getEconomy().format(abw.getWorldConfiguration().claimCost().doubleValue())), true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.OTHER;
			}
			AllBanksPlot plot = new AllBanksPlot(AllBanksPlot.PlotFinder.getNextAvailablePlot(abw));
			
			plot.claim(p.getName());
			p.teleport(plot.getFirstBound());
			AllBanks.getEconomy().withdrawPlayer(p, abw.getWorldConfiguration().claimCost().doubleValue());
			
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			replaceMap.put("%1%", String.valueOf(plot.getPlotX()));
			replaceMap.put("%2%", String.valueOf(plot.getPlotZ()));
			replaceMap.put("%3%", plot.getAllBanksWorld().getID());
			
			Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_PLOT_CLAIM_SUCCESS, replaceMap, true);
			InteractiveUtil.sendSound(p, SoundType.SUCCESS);
			return CommandExecuteResult.SUCCESS;
			
		}else if(args[1].equalsIgnoreCase("teleport")){
			if(args.length >= 3){
				String teleportArgument = args[2];
				
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return CommandExecuteResult.OTHER;
				}
				
				Player p = (Player) sender;
				
				if(!this.hasPermission(sender)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				int homeID = 1;
				String playerHome = null;
				
				//Por nombre
				Pattern pattern = Pattern.compile("^([a-zA-Z0-9]{1,}):([-0-9]{1,})$");
				Matcher matcher = pattern.matcher(teleportArgument);
				
				if(matcher.matches()){
					String homeIDSTR = matcher.group(2);
					try{
						homeID = Integer.parseInt(homeIDSTR);
					}catch(NumberFormatException e){
						homeID = 1;
					}
					
					playerHome = matcher.group(1);
				}else{
					playerHome = teleportArgument;
					homeID = 1;
				}
				
				OfflinePlayer pget = Bukkit.getPlayer(playerHome);
				
				if(pget == null){
					for(OfflinePlayer poff : Bukkit.getOfflinePlayers()){
						if(poff.getName().equalsIgnoreCase(playerHome)){
							pget = poff;
						}
					}
					
					if(pget == null){
						Translation.getAndSendMessage(p, StringsID.PLAYER_NOT_EXISTS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerHome), true);
						return CommandExecuteResult.OTHER;
					}
				}
				
				AllBanksPlayer player = new AllBanksPlayer(playerHome);
				
				List<PlotID> plots = player.getOwnedPlots();
				
				if(plots.isEmpty()){
					Translation.getAndSendMessage(p, StringsID.PLOT_PLAYER_NOT_HAVE_PLOTS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerHome), true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				if(homeID > plots.size()){
					homeID = plots.size();
				}
				
				if(homeID < 1){
					homeID = 1;
				}
				
				AllBanksPlot plot = new AllBanksPlot(plots.get(homeID - 1));
				
				if(!plot.getPlotConfiguration().allowTeleport() && !plot.havePermissions(p)){
					Translation.getAndSendMessage(p, StringsID.PLOT_NOT_ALLOW_TELEPORT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				p.teleport(plot.getFirstBound());
				Translation.getAndSendMessage(p, StringsID.PLOT_TELEPORT_SUCCESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + playerHome), true);
				InteractiveUtil.sendSound(p, SoundType.SUCCESS);
				return CommandExecuteResult.SUCCESS;
			}
		}else if(args[1].equalsIgnoreCase("list")){
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			
			if(!this.hasPermission(p)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			AllBanksPlayer abp = new AllBanksPlayer(p);
			
			List<PlotID> plots = abp.getOwnedPlots();
			
			if(plots.isEmpty()){
				Translation.getAndSendMessage(p, StringsID.PLOT_LIST_SUCCESS_NO_RESULT, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.SUCCESS;
			}
			
			int page = 1;
			
			if(args.length >= 3){
				try{
					page = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					page = 1;
				}
			}
			
			int startRes = (page - 1) * 5;
			
			if(startRes >= plots.size()){
				startRes = 0;
				page = 1;
			}
			
			Translation.getAndSendMessage(p, StringsID.PLOT_LIST_SUCCESS_LIST_HEADER, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + p.getName(), "%2%>>>" + plots.size()), true);
			
			for(int i = startRes; i < plots.size() && i < (startRes + 5); i ++){
				
				PlotID pid = plots.get(i);
				AllBanksPlot plot = new AllBanksPlot(pid);
				
				HashMap<String, String> replaceMap = new HashMap<String, String>();
				replaceMap.put("%1%", String.valueOf(i + 1));
				replaceMap.put("%2%", plot.getPlotX() + "," + plot.getPlotZ());
				replaceMap.put("%3%", String.valueOf(plot.getFirstBound().getBlockX()));
				replaceMap.put("%4%", String.valueOf(plot.getFirstBound().getBlockZ()));
				replaceMap.put("%5%", plot.getFirstBound().getWorld().getName());
				
				Translation.getAndSendMessage(p, StringsID.PLOT_LIST_SUCCESS_LIST_ENTRY, replaceMap, true);
			}
			
			if((startRes + 5) < plots.size())
				Translation.getAndSendMessage(p, StringsID.PLOT_LIST_SUCCESS_SUGGEST_PAGES, Translation.splitStringIntoReplaceHashMap(">>>", "%COMMAND_LABEL%>>>" + labelPrefix, "%1%>>>" + (page + 1)), true);
			InteractiveUtil.sendSound(p, SoundType.SUCCESS);
			return CommandExecuteResult.SUCCESS;
		}else if(args[1].equalsIgnoreCase("biomelist")){
			if(!this.hasPermission(sender)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			int page = 1;
			int biomesLenght = Biome.values().length;
			int completePage = biomesLenght / 10;
			int dif = biomesLenght - (completePage * 10);
			int maxPage = (dif == 0) ? completePage : completePage + 1;
			
			if(args.length >= 3){
				try{
					page = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					page = 1;
				}
			}
			
			if(page > maxPage) page = maxPage;
			if(page < 1) page = 1;
			
			sender.sendMessage(ChatColor.DARK_AQUA + String.valueOf(page) + "/" + maxPage + " :");
			
			for(int i = (page * 10) - 10; i < (page * 10); i++){
				if(i >= Biome.values().length) break;
				sender.sendMessage(Biome.values()[i].toString().toUpperCase());
			}
			
			return CommandExecuteResult.SUCCESS;
		}else if(args[1].equalsIgnoreCase("setbiome")){
			
			if(!(sender instanceof Player)){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
				return CommandExecuteResult.OTHER;
			}
			
			Player p = (Player) sender;
			
			if(!this.hasPermission(p)){
				Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
				InteractiveUtil.sendSound(p, SoundType.DENY);
				return CommandExecuteResult.NO_PERMISSIONS;
			}
			
			if(!AllBanksWorld.worldIsAllBanksWorld(p.getLocation().getWorld().getName())){
				Translation.getAndSendMessage(p, StringsID.PLOT_INVALID_WORLD, true);
				InteractiveUtil.sendSound(p, SoundType.WARNING);
				return CommandExecuteResult.OTHER;
			}
			
			try{
				Biome biome = Biome.valueOf(args[2]);
				AllBanksWorld abw = AllBanksWorld.getInstance(p.getLocation().getWorld().getName());
				
				if(!abw.locationIsPlot(p.getLocation())){
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksPlot plot = abw.getPlot(p.getLocation());
				
				if(!plot.getOwnerName().equalsIgnoreCase(p.getName()) && !abw.hasAdminPermissions(p)){
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				plot.setBiome(biome);
				
				Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_PLOT_SETBIOME_SUCCESS, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + biome.toString().toUpperCase()), true);
				return CommandExecuteResult.SUCCESS;
			}catch(IllegalArgumentException e){
				Translation.getAndSendMessage(sender, StringsID.COMMAND_LAND_PLOT_SETBIOME_ERROR_INVALID_BIOME, Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>" + args[2]), true);
				return CommandExecuteResult.EXCEPTION;
			}
		}else if(args[1].equalsIgnoreCase("clear")){
			if(args.length == 2){
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return CommandExecuteResult.OTHER;
				}
				
				final Player p = (Player) sender;
				
				if(!this.hasPermission(p)){
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, true);
					InteractiveUtil.sendSound(p, SoundType.DENY);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				if(!AllBanksWorld.worldIsAllBanksWorld(p.getWorld().getName())){
					Translation.getAndSendMessage(p, StringsID.PLOT_INVALID_WORLD, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				AllBanksWorld abw = AllBanksWorld.getInstance(p.getWorld().getName());
				
				if(!abw.locationIsPlot(p.getLocation())){
					Translation.getAndSendMessage(sender, StringsID.PLOT_LOC_NOT_IS_PLOT, true);
					InteractiveUtil.sendSound(p, SoundType.WARNING);
					return CommandExecuteResult.OTHER;
				}
				
				final AllBanksPlot plot = abw.getPlot(p.getLocation());
				
				if(!plot.hasOwner() || !plot.getOwnerName().equalsIgnoreCase(p.getName())){
					Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
					return CommandExecuteResult.NO_PERMISSIONS;
				}
				
				//Aviso de confirmación
				new Thread(){
					@Override
					public void run(){
						SecureRandom random = new SecureRandom();
						String token = new BigInteger(130, random).toString(32);
						
						PlotClearTokenStruct save = new PlotClearTokenStruct();
						save.plot_X = plot.getPlotX();
						save.plot_Z = plot.getPlotZ();
						save.plot_world = plot.getAllBanksWorld();
						save.token = token;
						
						plotClearTokens.put(p.getName(), save);
						
						for(String s : Translation.get(StringsID.COMMAND_PLOT_CLEAR_CONFIRM_MSG, true)){
							BuildChatMessage prepareMessage1 = new BuildChatMessage(s);
							prepareMessage1.color(ChatColor.YELLOW);
							prepareMessage1.send(p);
						}
						BuildChatMessage prepareMessage2 = new BuildChatMessage("")
						.then("[")
							.color(ChatColor.BLUE)
						.then(Translation.get(StringsID.ACCEPT, false)[0])
							.color(ChatColor.YELLOW)
							.command("/plot clear confirm " + token)
						.then("] [")
							.color(ChatColor.BLUE)
						.then(Translation.get(StringsID.CANCEL, false)[0])
							.color(ChatColor.YELLOW)
							.command("/plot clear cancel " + token)
						.then("]")
							.color(ChatColor.BLUE)
						;
						
						prepareMessage2.send(p);
						
					}
				}.start();
				
				return CommandExecuteResult.SUCCESS;
			}else if(args.length >= 4){
				if(!(sender instanceof Player)){
					Translation.getAndSendMessage(sender, StringsID.COMMAND_ONLY_FOR_PLAYER, true);
					return CommandExecuteResult.OTHER;
				}
				
				if(args[2].equalsIgnoreCase("confirm")){
					String token = args[3];
					if(!plotClearTokens.containsKey(sender.getName())){
						Translation.getAndSendMessage(sender, StringsID.COMMAND_PLOT_CLEAR_INVALID_TOKEN, true);
						return CommandExecuteResult.NO_PERMISSIONS;
					}else{
						PlotClearTokenStruct data = plotClearTokens.get(sender.getName());
						if(!data.token.equalsIgnoreCase(token)){
							Translation.getAndSendMessage(sender, StringsID.COMMAND_PLOT_CLEAR_INVALID_TOKEN, true);
							return CommandExecuteResult.NO_PERMISSIONS;
						}
						
						//Resetear parcela
						AllBanksPlot plot = new AllBanksPlot(data.plot_X, data.plot_Z, data.plot_world);
						
						//Segundo chequeo por si se trata de ejecutar este comando para intentar acciones de trolleo
						if(!plot.hasOwner() || !plot.getOwnerName().equalsIgnoreCase(sender.getName())){
							Translation.getAndSendMessage(sender, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
							return CommandExecuteResult.NO_PERMISSIONS;
						}
						
						plot.clearPlot(sender);
						plotClearTokens.remove(sender.getName());
						return CommandExecuteResult.SUCCESS;
					}
					
				}else if(args[2].equalsIgnoreCase("cancel")){
					String token = args[3];
					if(!plotClearTokens.containsKey(sender.getName())){
						Translation.getAndSendMessage(sender, StringsID.COMMAND_PLOT_CLEAR_INVALID_TOKEN, true);
						return CommandExecuteResult.NO_PERMISSIONS;
					}else{
						PlotClearTokenStruct data = plotClearTokens.get(sender.getName());
						if(!data.token.equalsIgnoreCase(token)){
							Translation.getAndSendMessage(sender, StringsID.COMMAND_PLOT_CLEAR_INVALID_TOKEN, true);
							return CommandExecuteResult.NO_PERMISSIONS;
						}
					}
					
					Translation.getAndSendMessage(sender, StringsID.COMMAND_PLOT_CLEAR_CANCEL_SUCCESS, true);
					plotClearTokens.remove(sender.getName());
					return CommandExecuteResult.SUCCESS;
				}
			}
			
		}
		
		return CommandExecuteResult.INVALID_ARGUMENTS;
	}
}
