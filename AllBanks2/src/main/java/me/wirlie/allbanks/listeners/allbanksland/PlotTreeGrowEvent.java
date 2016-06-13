package me.wirlie.allbanks.listeners.allbanksland;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.allbanksland.AllBanksPlot;
import me.wirlie.allbanks.allbanksland.AllBanksWorld;

@SuppressWarnings("javadoc")
public class PlotTreeGrowEvent implements Listener {

	@EventHandler
	public void onTreeGrow(StructureGrowEvent e){
		if(e.getSpecies() != null){
			Location loc = e.getLocation();
			
			if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
				AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
				
				if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
					e.setCancelled(true);
					return;
				}
				
				AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
				
				if(e.getPlayer() != null){
					if(!plot.havePermissions(e.getPlayer())){
						e.setCancelled(true);
						Translation.getAndSendMessage(e.getPlayer(), StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
						return;
					}
				}
				
				List<BlockState> removeFromList = new ArrayList<BlockState>();
				
				for(BlockState b : e.getBlocks()){
					if(!abw.locationIsPlot(b.getLocation().getBlockX(), b.getLocation().getBlockZ())){
						removeFromList.add(b);
						continue;
					}
					
					AllBanksPlot plotBlock = abw.getPlot(b.getLocation().getBlockX(), b.getLocation().getBlockZ());
					
					if(plotBlock.getPlotX() != plot.getPlotX() || plotBlock.getPlotZ() != plot.getPlotZ()){
						removeFromList.add(b);
						continue;
					}
				}
				
				e.getBlocks().removeAll(removeFromList);
			}
		}
	}
	
}
