package de.raffi.autominer.configuration;

import de.raffi.pluginlib.configuration.Configurable;

public class Settings {
	
	@Configurable
	public static int INVENTORY_UPDATE_TICKS = 20;
	@Configurable
	public static double MINER_SWORD_TRACKINGDISTANCE_MAX = 10;

}
