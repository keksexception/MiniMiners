package de.raffi.autominer.pathfinder;

import org.bukkit.Location;

public class Grid {
	
	private double minX,maxX,minZ,maxZ;
	
	
	
	public Grid(double minX, double maxX, double minZ, double maxZ) {
		this.minX = minX;
		this.maxX = maxX;
		this.minZ = minZ;
		this.maxZ = maxZ;
	}
	public Grid(Location start, Location end, int offset) {		
		this.minX=(start.getX()<end.getX()?start.getX():end.getX())-offset;
		this.maxX=(start.getX()>end.getX()?start.getX():end.getX())+offset;
		this.minZ=(start.getZ()<end.getZ()?start.getZ():end.getZ())-offset;
		this.maxZ=(start.getZ()>end.getZ()?start.getZ():end.getZ())+offset;
	}
	public boolean contains(Location loc) {
		return loc.getX()>=minX&&loc.getX()<=maxX&&loc.getZ()>=minZ&&loc.getZ()<=maxZ;
	}
	public boolean contains(Node node) {
		return contains(node.getSelf());
	}

}
