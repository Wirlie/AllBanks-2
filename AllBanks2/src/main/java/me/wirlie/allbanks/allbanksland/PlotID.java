package me.wirlie.allbanks.allbanksland;

/**
 * Representación de la información del ID de una parcela.
 * @author josue
 */
public class PlotID {
	
	private String worldID;
	private AllBanksWorld worldInstance = null;
	private int plotZ;
	private int plotX;
	
	/**
	 * Obtener una instancia de esta clase usando valores reales del mundo.
	 * @param abw Mundo de AllBanksLand
	 * @param startX Localización real, inicial X
	 * @param startZ Localización real, inicial Z
	 * @return Instancia de {@link PlotID}
	 */
	public static PlotID getPlotIDByRealXZ(AllBanksWorld abw, int startX, int startZ){
		int totalSize = abw.plotSize + abw.roadSize + 2;
		int plotX = ((startX >= 0) ? (startX / totalSize) : (startX / totalSize) - 1);
		int plotZ = ((startZ >= 0) ? (startZ / totalSize) : (startZ / totalSize) - 1);
		return new PlotID(abw, plotX, plotZ);
	}
	
	/**
	 * Constructor de la clase.
	 * @param worldID ID del mundo.
	 * @param plotX ID X
	 * @param plotZ ID Z
	 */
	public PlotID(String worldID, int plotX, int plotZ){
		this.worldID = worldID;
		this.plotX = plotX;
		this.plotZ  = plotZ;
	}
	
	/**
	 * Constructor de la clase.
	 * @param world Mundo de AllBanksLand.
	 * @param plotX ID X
	 * @param plotZ ID Z
	 */
	public PlotID(AllBanksWorld world, int plotX, int plotZ){
		this.worldInstance = world;
		this.worldID = world.getID();
		this.plotX = plotX;
		this.plotZ  = plotZ;
	}

	/**
	 * Obtener el ID del mundo.
	 * @return ID del mundo.
	 */
	public String getWorldID() {
		return worldID;
	}
	
	/**
	 * Obtener ID en X
	 * @return ID X
	 */
	public int getPlotX(){
		return plotX;
	}
	
	/**
	 * Obtener ID en Z
	 * @return ID Z
	 */
	public int getPlotZ(){
		return plotZ;
	}

	/**
	 * Obtener el mundo de AllBanksLand
	 * @return {@link AllBanksWorld}, mundo de AllBanksLand
	 */
	public AllBanksWorld getWorld() {
		if(worldInstance == null){
			return AllBanksWorld.getInstance(getWorldID());
		}else{
			return worldInstance;
		}
	}
	
}
