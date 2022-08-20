package de.raffi.autominer.pathfinder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Path {
	
	private Location start, stop;
	private List<Node> nodes,checkedNodes;
	private Grid grid;

	public Path(Location start, Location stop) {
		this.start = start;
		this.stop = stop;
		this.grid = new Grid(start.clone(), stop.clone(), 5);
	}
	public void init() {
		this.nodes = new ArrayList<>();
		this.checkedNodes = new ArrayList<>();
	}
	public void findPath() {
		Node startNode = new Node(start, grid);
		Node endNode = new Node(stop, grid);
		nodes.add(startNode);
		checkedNodes.add(startNode);
		s(startNode, endNode);
		nodes.add(endNode);
		
	}
	public void s(Node cheapest, Node end) {
		Node nextCheapest = cheapest.getCheapestNeighbor(start, stop, checkedNodes,true);
		if(nextCheapest==null)return;
		if(nextCheapest.isSame(end)) return;
		nodes.add(nextCheapest);
		s(nextCheapest, end);
		
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public Location getStart() {
		return start;
	}
	public Location getStop() {
		return stop;
	}
	public Grid getGrid() {
		return grid;
	}

}
