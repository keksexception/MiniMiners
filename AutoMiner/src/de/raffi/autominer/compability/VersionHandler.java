package de.raffi.autominer.compability;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public interface VersionHandler {
	
	public ItemStack writeNBT(ItemStack original, String key, JSONObject write);
	public JSONObject getNBT(ItemStack getFrom, String key);
	public boolean hasNBTTag(ItemStack check, String tag);
	public void spawnDestroyParticle(Location at);

}
