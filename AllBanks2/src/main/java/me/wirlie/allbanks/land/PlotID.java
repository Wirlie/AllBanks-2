package me.wirlie.allbanks.land;

public class PlotID {
	
	private String worldID;
	private int plotZ;
	private int plotX;
	
	public PlotID(String worldID, int plotX, int plotZ){
		this.worldID = worldID;
		this.plotX = plotX;
		this.plotZ  = plotZ;
	}

	public String getWorldID() {
		return worldID;
	}
	
	public int getPlotX(){
		return plotX;
	}
	
	public int getPlotZ(){
		return plotZ;
	}

	public AllBanksWorld getWorld() {
		return AllBanksWorld.getInstance(getWorldID());
	}
	
}
