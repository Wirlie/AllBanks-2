package me.wirlie.allbanks.land.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleterABLandPlotAccess implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		//Simple fix, para simular una ejecución tipo /abl plot
		String[] newArgs = new String[args.length + 1];
		newArgs[0] = "plot";
		for(int i = 0; i < args.length; i ++){
			newArgs[i + 1] = args[i];
		}
		
		return CommandTabCompleterABLand.getInstance().onTabComplete(sender, cmd, label, newArgs);
	}

}
