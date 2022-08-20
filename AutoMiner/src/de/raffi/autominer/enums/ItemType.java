package de.raffi.autominer.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemType {
	
	
	ITEM, 
	SKULL,
	COLORED_ARMOR;
	
	
	public static ItemType getType(ItemStack i) {
		if(i.getType() == Material.SKULL_ITEM) return SKULL;
		else if(i.getType().name().startsWith("LEATHER_")) return COLORED_ARMOR;
		return ITEM;
	}

}
