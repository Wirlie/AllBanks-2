package me.wirlie.allbanks.land.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import me.wirlie.allbanks.StringsID;
import me.wirlie.allbanks.Translation;
import me.wirlie.allbanks.land.AllBanksPlot;
import me.wirlie.allbanks.land.AllBanksWorld;

public class PlotPotionListener implements Listener {

	@EventHandler
    public void PotionsSplash(PotionSplashEvent e){
        if(e.getEntity().getShooter() instanceof Player){
            Location loc = e.getEntity().getLocation();
            Player p = (Player) e.getEntity().getShooter();
            
            if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
            	AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
            	
            	if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
            		Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
            		e.setCancelled(true);
            		return;
            	}
            	
            	AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
            	
            	if(!plot.hasOwner() || !plot.havePermissions(p)){
            		e.setCancelled(true);
            		Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
            	}
            }
        }
    }
	
	@EventHandler
    public void PotionsSplash(LingeringPotionSplashEvent e){
        if(e.getEntity().getShooter() instanceof Player){
            Location loc = e.getEntity().getLocation();
            Player p = (Player) e.getEntity().getShooter();
            
            if(AllBanksWorld.worldIsAllBanksWorld(loc.getWorld().getName())){
            	AllBanksWorld abw = AllBanksWorld.getInstance(loc.getWorld().getName());
            	
            	if(!abw.locationIsPlot(loc.getBlockX(), loc.getBlockZ())){
            		Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
            		e.setCancelled(true);
            		return;
            	}
            	
            	AllBanksPlot plot = abw.getPlot(loc.getBlockX(), loc.getBlockZ());
            	
            	if(!plot.hasOwner() || !plot.havePermissions(p)){
            		e.setCancelled(true);
            		Translation.getAndSendMessage(p, StringsID.PLOT_NOT_IS_YOUR_OWN_PLOT, true);
            	}
            }
        }
    }
	
}
