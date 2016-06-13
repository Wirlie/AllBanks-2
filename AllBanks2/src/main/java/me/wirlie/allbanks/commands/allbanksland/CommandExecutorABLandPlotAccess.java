package me.wirlie.allbanks.commands.allbanksland;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Ejecutor del comando /plot usando el mismo ejecutor para el comando /allbanksland
 * @author Wirlie
 */
public class CommandExecutorABLandPlotAccess implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		//Simple fix, para simular una ejecución tipo /abl plot
		String[] newArgs = new String[args.length + 1];
		newArgs[0] = "plot";
		for(int i = 0; i < args.length; i ++){
			newArgs[i + 1] = args[i];
		}
		
		return CommandExecutorABLand.getInstance().onCommand(sender, command, label, newArgs);
	}

}
