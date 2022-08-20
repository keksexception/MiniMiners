package de.raffi.autominer.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.raffi.autominer.callback.CallbackClick;
import de.raffi.pluginlib.builder.ItemBuilder;

public class ClickItem {
	
	private ItemStack item;
	private CallbackClick onClick;
	public ClickItem(ItemStack item, CallbackClick onClick) {
		this.item = item;
		this.onClick = onClick;
	}
	public ClickItem(ItemBuilder item, CallbackClick onClick) {
		this(item.build(), onClick);
	}
	
	public void click(Player p) {
		onClick.onClick(p);
	}
	public ItemStack getItem() {
		return item;
	}
	public void update() {
		
	}
	public void updateType(Material newType) {
		item.setType(newType);
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}

}
