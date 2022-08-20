package de.raffi.autominer.pathfinder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import de.raffi.autominer.exception.PathfinderException;

public class Node {
	
	
	private Location self;
	private Grid grid;

	public Node(Location self, Grid grid) {
		this.self = self;
		this.grid = grid;
	}
	/**
	 * distance from the starting node
	 * @param startNode
	 * @return
	 */
	public double getgCost(Location startNode) {
		return self.distance(startNode);
	}
	/*
	 * distance from the target node
	 * @param endNode
	 * @return
	 */
	public double gethCost(Location endNode) {
		return self.distance(endNode);
	}

	public Location getSelf() {
		return self;
	}
	public double getFCost(Location startNode, Location endNode) {
		return getgCost(startNode)+gethCost(endNode);
	}
	public Block getBlock() {
		return getSelf().getBlock();
	}
	public Grid getGrid() {
		return grid;
	}
	public boolean isSame(Node other) {
		return isSame(other.getSelf());
	}
	/**
	 * checks if the node has the same block location 
	 * @param other
	 * @return true when the location of the node matches the location of the given  location
	 */
	public boolean isSame(Location other) {
		return getSelf().getBlockX()==other.getBlockX()&&getSelf().getBlockY()==other.getBlockY()&&getSelf().getBlockZ()==other.getBlockZ();
	}
	/**
	 * 
	 * @param checkedNodes
	 * @param allowDiagonal when true, it also checks nodes diagonal to it self
	 * @return
	 * @throws PathfinderException when no path can be found
	 */
	public List<Node> getNeighbors(List<Node>checkedNodes, boolean allowDiagonal) {
		List<Node> n = new ArrayList<>();
		for(int x = -1; x <= 1; x++) {
			for(int z = -1; z <= 1; z++) {
				if(!allowDiagonal)
					if(x==0||z==0) continue;
				
				if(!(x==0&&z==0)) {
					Location loc = self.clone().add(x, 0, z);				
					Node neighbor = new Node(loc,grid);
					if(!hasBeenChecked(checkedNodes, loc)&&isValid(loc)) {
						n.add(neighbor);
					} 

					checkedNodes.add(neighbor);
			
				}
			}
		}
		if(n.size()==0) throw new PathfinderException("No path found!");
		return n;
	}
	public Node getCheapestNeighbor(Location start, Location end,List<Node>checkedNodes, boolean allowDiagonal) {
		Node cheapest = null;
		for(Node n : getNeighbors(checkedNodes,allowDiagonal)) {
			if(cheapest==null) {
				cheapest=n;
				continue;
			}
			if(n.getFCost(start, end)<cheapest.getFCost(start, end)) {
				cheapest = n;
			}
			
		}
		return cheapest;
	}
	public boolean hasBeenChecked(List<Node> checkedNodes, Location check) {
		for(Node node : checkedNodes) {
			if(node.isSame(check)) return true;
		}
		return false;
	}
	public boolean isValid(Location loc) {
		Block block = loc.getWorld().getBlockAt(loc);
		return (block.isLiquid()||block.getType()==Material.AIR||block.getType()==Material.LONG_GRASS)&&grid.contains(loc)&&getBlockUnder(block).getType()!=Material.AIR;
	}
	public Block getBlockUnder(Block top) {
		return top.getLocation().getWorld().getBlockAt(top.getLocation().clone().add(0, -1, 0));
	}
	@Override
	public boolean equals(Object obj) {
		return isSame((Node) obj);
	}
	public void armor(Location loc, double x) {
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0.5, 0.0, 0.5), EntityType.ARMOR_STAND);
		stand.setCustomName("" + x);
		stand.setCustomNameVisible(true);
		stand.setBasePlate(false);
		stand.setArms(false);
		stand.setSmall(true);
		stand.setGravity(false);
		stand.setVisible(false);
	}

}
