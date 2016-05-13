package me.wirlie.allbanks.land.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleterABLandPlotAccess implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandTabCompleterABLand.getInstance().onTabComplete(sender, cmd, label, args);
	}

}
