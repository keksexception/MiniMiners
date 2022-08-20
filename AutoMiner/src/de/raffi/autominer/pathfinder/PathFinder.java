package de.raffi.autominer.pathfinder;

import org.bukkit.Location;

public class PathFinder {
	
	public static Path findPath(Location start, Location end) {
		Location temp = end.clone();
		temp.setY(start.getY());
		end = temp;
		Path path = new Path(start.getBlock().getLocation().add(0.5, 0, 0.5), end.getBlock().getLocation().add(0.5,0,0.5));
		path.init();
		path.findPath();
		return path;
	}

}
