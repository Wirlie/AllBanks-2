package me.wirlie.allbanks.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.Updater;
import me.wirlie.allbanks.Updater.UpdateType;

/**
 * Comando para comprobar actualizaciones de AllBanks2, un método alterno para la comprobación de actualizaciones.
 * @author Wirlie
 */
public class CommandUpdate extends Command {

	/**
	 * Constructor principal de la clase.
	 * @param permission Permiso necesario para ejecutar el comando.
	 */
	public CommandUpdate(String permission) {
		super(permission);
	}
	
	@Override
	public CommandExecuteResult execute(final CommandSender sender, String label, String[] args){
		if(args.length >= 2){
			
			if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
				//Ayuda: /ab toprank ?
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab updater " + ChatColor.AQUA + "check-updates" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_UPDATER_CHECK_UPDATES_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab updater " + ChatColor.AQUA + "download-update" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_UPDATER_DOWNLOAD_UPDATE_DESC.toString(false));
				sender.sendMessage(Translation.getPluginPrefix() + ChatColor.GRAY + "/ab updater " + ChatColor.AQUA + "current-version" + ChatColor.GOLD + " - " + ChatColor.WHITE + StringsID.COMMAND_HELP_UPDATER_CURRENT_VERSION.toString(false));
				return CommandExecuteResult.OTHER;
			}else if(args[1].equalsIgnoreCase("check-updates")){
				if(hasPermission(sender)){
					Updater updater = new Updater(AllBanks.getInstance(), 98949, AllBanks.getInstance().getPluginFile(), UpdateType.NO_DOWNLOAD, true);
					switch(updater.getResult()){
					case DISABLED:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_UPDATER_DISABLED, true);
						break;
					case FAIL_APIKEY:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_CHECK, true);
						break;
					case FAIL_BADID:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_CHECK, true);
						break;
					case FAIL_DBO:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_CHECK, true);
						break;
					case FAIL_DOWNLOAD:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_DOWNLOAD, true);
						break;
					case FAIL_NOVERSION:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_CHECK, true);
						break;
					case NO_UPDATE:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_NO_UPDATES, true);
						break;
					case SUCCESS:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_SUCCESS_DOWNLOAD, true);
						break;
					case UPDATE_AVAILABLE:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_UPDATE_AVAILABLE, true);
						break;
					default:
						break;
					}
				}else{
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
					return CommandExecuteResult.NO_PERMISSIONS;
				}
			}else if(args[1].equalsIgnoreCase("download-update")){
				if(hasPermission(sender)){
					Updater updater = new Updater(AllBanks.getInstance(), 98949, AllBanks.getInstance().getPluginFile(), UpdateType.DEFAULT, true);
					switch(updater.getResult()){
					case DISABLED:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_UPDATER_DISABLED, true);
						break;
					case FAIL_APIKEY:
					case FAIL_BADID:
					case FAIL_NOVERSION:
					case FAIL_DBO:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_CHECK, true);
						break;
					case FAIL_DOWNLOAD:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_DOWNLOAD, true);
						break;
					case NO_UPDATE:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_NO_UPDATES, true);
						break;
					case SUCCESS:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_SUCCESS_DOWNLOAD, true);
						break;
					case UPDATE_AVAILABLE:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_UPDATE_AVAILABLE, true);
						break;
					default:
						break;
					}
				}else{
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
					return CommandExecuteResult.NO_PERMISSIONS;
				}
			}else if(args[1].equalsIgnoreCase("force-download")){
				if(hasPermission(sender)){
					Updater updater = new Updater(AllBanks.getInstance(), 98949, AllBanks.getInstance().getPluginFile(), UpdateType.NO_VERSION_CHECK, true);
					switch(updater.getResult()){
					case DISABLED:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_UPDATER_DISABLED, true);
						break;
					case FAIL_APIKEY:
					case FAIL_BADID:
					case FAIL_DBO:
					case FAIL_NOVERSION:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_CHECK, true);
						break;
					case FAIL_DOWNLOAD:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_DOWNLOAD, true);
						break;
					case NO_UPDATE:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_NO_UPDATES, true);
						break;
					case SUCCESS:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_SUCCESS_DOWNLOAD, true);
						break;
					case UPDATE_AVAILABLE:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_UPDATE_AVAILABLE, true);
						break;
					default:
						break;
					}
				}else{
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
					return CommandExecuteResult.NO_PERMISSIONS;
				}
			}else if(args[1].equalsIgnoreCase("current-version")){
				if(hasPermission(sender)){
					Updater updater = new Updater(AllBanks.getInstance(), 98949, AllBanks.getInstance().getPluginFile(), UpdateType.NO_DOWNLOAD, true);
					switch(updater.getResult()){
					case DISABLED:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_UPDATER_DISABLED, true);
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_CHECK_VERSION_LABEL, true, AllBanks.getInstance().getDescription().getVersion(), Translation.get(StringsID.UNKNOW, false)[0], Translation.get(StringsID.UNKNOW, false)[0]);
						break;
					case FAIL_APIKEY:
					case FAIL_BADID:
					case FAIL_NOVERSION:
					case FAIL_DBO:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_CHECK, true);
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_CHECK_VERSION_LABEL, true, AllBanks.getInstance().getDescription().getVersion(), Translation.get(StringsID.UNKNOW, false)[0], Translation.get(StringsID.UNKNOW, false)[0]);
						break;
					case FAIL_DOWNLOAD:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_FAIL_DOWNLOAD, true);
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_CHECK_VERSION_LABEL, true, AllBanks.getInstance().getDescription().getVersion(), updater.getLatestName(), Translation.get(StringsID.UNKNOW, false)[0]);
						break;
					case NO_UPDATE:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_NO_UPDATES, true);
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_CHECK_VERSION_LABEL, true, AllBanks.getInstance().getDescription().getVersion(), updater.getLatestName(), Translation.get(StringsID.NO_UPDATES, false)[0]);
						break;
					case SUCCESS:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_SUCCESS_DOWNLOAD, true);
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_CHECK_VERSION_LABEL, true, AllBanks.getInstance().getDescription().getVersion(), updater.getLatestName(), Translation.get(StringsID.UNKNOW, false)[0]);
						break;
					case UPDATE_AVAILABLE:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_UPDATE_AVAILABLE, true);
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_CHECK_VERSION_LABEL, true, AllBanks.getInstance().getDescription().getVersion(), updater.getLatestName(), Translation.get(StringsID.UPDATE_AVAILABLE, false)[0]);
						break;
					default:
						Translation.getAndSendMessage(sender, StringsID.COMMAND_UPDATER_CHECK_VERSION_LABEL, true, AllBanks.getInstance().getDescription().getVersion(), Translation.get(StringsID.UNKNOW, false)[0], Translation.get(StringsID.UNKNOW, false)[0]);
						break;
					}
				}else{
					Translation.getAndSendMessage(sender, StringsID.NO_PERMISSIONS_FOR_THIS, (sender instanceof Player));
					return CommandExecuteResult.NO_PERMISSIONS;
				}
			}
		}else{
			Translation.getAndSendMessage(sender, 
					StringsID.COMMAND_SUGGEST_HELP, 
					Translation.splitStringIntoReplaceHashMap(">>>", "%1%>>>/ab updater ?"),
					true);
			return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
		}
		
		return CommandExecuteResult.INSUFICIENT_ARGUMENTS;
	}

}
