package me.wirlie.allbanks.allbanksland;

public class PlotID {
	
	private String worldID;
	private AllBanksWorld worldInstance = null;
	private int plotZ;
	private int plotX;
	
	public PlotID(String worldID, int plotX, int plotZ){
		this.worldID = worldID;
		this.plotX = plotX;
		this.plotZ  = plotZ;
	}
	
	public PlotID(AllBanksWorld world, int plotX, int plotZ){
		this.worldInstance = world;
		this.worldID = world.getID();
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
		if(worldInstance == null){
			return AllBanksWorld.getInstance(getWorldID());
		}else{
			return worldInstance;
		}
	}
	
}
