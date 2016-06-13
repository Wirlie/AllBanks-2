package me.wirlie.allbanks.listeners.allbanksland;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;

import me.wirlie.allbanks.AllBanks;
import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;
import me.wirlie.allbanks.hooks.LWCFunctions;
import me.wirlie.allbanks.utils.AllBanksLogger;

@SuppressWarnings("javadoc")
public class PlotLWCListener implements Listener {

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onPlayerTryBreakBlock(BlockBreakEvent e){
		Block b = e.getBlock();
		if(AllBanksWorld.worldIsAllBanksWorld(b.getWorld().getName())){
			AllBanksWorld abw = AllBanksWorld.getInstance(b.getWorld().getName());
			if(abw.locationIsPlot(b.getLocation())){
				Player p = e.getPlayer();
				AllBanksPlot plot = abw.getPlot(b.getLocation());
				
				if(e.isCancelled() && plot.getOwnerName().equalsIgnoreCase(p.getName()) && AllBanks.getInstance().getConfig().getBoolean("allbanksland.revoke-lwc-protection-only-plot-owners", true)){
					LWCPlugin plugin = LWCFunctions.pluginInstance;
					
					Protection pr = plugin.getLWC().findProtection(b);
					
					if(pr != null){
						String protectionOwner = pr.getOwner();
						OfflinePlayer pOwner = Bukkit.getPlayer(UUID.fromString(protectionOwner));
						if(pOwner == null){
							pOwner = Bukkit.getOfflinePlayer(UUID.fromString(protectionOwner));
						}
						if(pOwner == null){
							return;
						}
						
						if(!plot.getPlotConfiguration().getFriends().contains(pOwner.getName().toLowerCase())){
							//El dueño de la protección no es amigo de esta parcela.
							pr.remove();
							e.setCancelled(false);
							AllBanksLogger.warning("[LWC REMOVED] Player " + p.getName() + " has supersede a LWC protection of another player. " + p.getName() + " has obtained permission because the owner of this protection (" + pOwner.getName() + ") no longer form part of the list of friends and " + p.getName() + " is the owner of this Plot (ID: " + plot.getPlotID() + "). [BlockLocation: " + b.getLocation() + "]");
						}
					}
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
				
				if(e.isCancelled() && plot.getOwnerName().equalsIgnoreCase(p.getName()) && AllBanks.getInstance().getConfig().getBoolean("allbanksland.revoke-lwc-protection-only-plot-owners", true)){
					LWCPlugin plugin = LWCFunctions.pluginInstance;
					
					Protection pr = plugin.getLWC().findProtection(b);
					
					if(pr != null){
						String protectionOwner = pr.getOwner();
						OfflinePlayer pOwner = Bukkit.getPlayer(UUID.fromString(protectionOwner));
						if(pOwner == null){
							pOwner = Bukkit.getOfflinePlayer(UUID.fromString(protectionOwner));
						}
						if(pOwner == null){
							return;
						}
						
						if(!plot.getPlotConfiguration().getFriends().contains(pOwner.getName().toLowerCase())){
							//El dueño de la protección no es amigo de esta parcela.
							e.setCancelled(false);
							Translation.getAndSendMessage(p, StringsID.PLOT_LWC_PROTECTION_REMOVED, true);
							AllBanksLogger.warning("[LWC ITERACTION] Player " + p.getName() + " has supersede a LWC protection of another player. " + p.getName() + " has obtained permission because the owner of this protection (" + pOwner.getName() + ") no longer form part of the list of friends and " + p.getName() + " is the owner of this Plot (ID: " + plot.getPlotID() + "). [BlockLocation: " + b.getLocation() + "]");
						}
					}
				}
			}
		}
	}
}
