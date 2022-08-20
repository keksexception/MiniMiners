package de.raffi.autominer.inventory;

import org.bukkit.entity.Player;

public class BetterInventory {
	
	private org.bukkit.inventory.Inventory container;
	private String name;
	private ClickItem[] items;
	
	
	
	public BetterInventory(org.bukkit.inventory.Inventory container) {
		this.container = container;
		this.name = container.getName();
		this.items = new ClickItem[container.getSize()-1];
	}

	public String getTitle() {
		return name;
	}
	public ClickItem[] getItems() {
		return items;
	}
	public void addItem(ClickItem i) {
		container.addItem(i.getItem());
		items[items.length+1] = i;
	}
	public void setItem(int slot, ClickItem i) {
		container.setItem(slot, i.getItem());
		items[slot] = i;
	}
	public org.bukkit.inventory.Inventory getContainer() {
		return container;
	}
	public void update() {
		
	}
	public void playSound(Player p) {
		
	}
	public void close(Player p) {
		
	}
}
