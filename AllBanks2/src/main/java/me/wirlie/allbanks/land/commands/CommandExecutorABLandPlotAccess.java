package me.wirlie.allbanks.land.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandExecutorABLandPlotAccess implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return CommandExecutorABLand.getInstance().onCommand(sender, command, label, args);
	}

}
