package me.wirlie.allbanks.listeners.allbanksland;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.yi.acru.bukkit.Lockette.Lockette;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.utils.AllBanksLogger;

public class PlotLocketteListener implements Listener {

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onPlayerTryBreakBlock(BlockBreakEvent e){
		Block b = e.getBlock();
		if(AllBanksWorld.worldIsAllBanksWorld(b.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(b.getWorld().getName());
			if(abw.locationIsPlot(b.getLocation())){
				Player p = e.getPlayer();
				AllBanksPlot plot = abw.getPlot(b.getLocation());
				
				if(e.isCancelled() && plot.getOwnerName().equalsIgnoreCase(p.getName()) && AllBanks.getInstance().getConfig().getBoolean("allbanksland.revoke-lockette-protection-only-plot-owners", true)){
					
					if(!Lockette.isProtected(b)){
						return;
					}
					
					e.setCancelled(false);
					Translation.getAndSendMessage(p, StringsID.PLOT_LOCKETTE_PROTECTION_REMOVED, true);
					AllBanksLogger.warning("[LOCKETTE REMOVED] Player " + p.getName() + " has supersede a Lockette protection of another player. " + p.getName() + " has obtained permission because " + p.getName() + " is the owner of this Plot (ID: " + plot.getPlotID() + "). [BlockLocation: " + b.getLocation() + "]");
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onPlayerTryBreakBlock(PlayerInteractEvent e){
		Block b = e.getClickedBlock();
		if(b == null) return;
		if(AllBanksWorld.worldIsAllBanksWorld(b.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(b.getWorld().getName());
			if(abw.locationIsPlot(b.getLocation())){
				Player p = e.getPlayer();
				
				AllBanksPlot plot = abw.getPlot(b.getLocation());
				
				if(e.isCancelled() && plot.getOwnerName().equalsIgnoreCase(p.getName()) && AllBanks.getInstance().getConfig().getBoolean("allbanksland.revoke-lockette-protection-only-plot-owners", true)){
					if(!Lockette.isProtected(b)){
						return;
					}
					
					e.setCancelled(false);
					Translation.getAndSendMessage(p, StringsID.PLOT_LOCKETTE_PROTECTION_REMOVED, true);
					AllBanksLogger.warning("[LOCKETTE INTERACTION] Player " + p.getName() + " has supersede a Lockette protection of another player. " + p.getName() + " has obtained permission because " + p.getName() + " is the owner of this Plot (ID: " + plot.getPlotID() + "). [BlockLocation: " + b.getLocation() + "]");
				}
			}
		}
	}
}
