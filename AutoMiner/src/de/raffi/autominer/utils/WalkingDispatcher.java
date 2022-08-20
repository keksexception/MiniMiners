package de.raffi.autominer.utils;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.raffi.autominer.callback.CallbackWalkingDispatcher;
import de.raffi.autominer.exception.PathfinderException;
import de.raffi.autominer.main.MiniMiners;
import de.raffi.autominer.miner.Miner;
import de.raffi.autominer.pathfinder.Node;
import de.raffi.autominer.pathfinder.Path;

public class WalkingDispatcher {
	
	private Path path;
	private Miner miner;
	private int delay, taskID, index,tried;
	private Iterator<Node> nodeIterator;
	private Node currentNode, lastNode;
	private CallbackWalkingDispatcher callback;
	
	public WalkingDispatcher(Path path, Miner miner, int delay, int tried, CallbackWalkingDispatcher callback) {
		this.path = path;
		this.miner = miner;
		this.delay = delay;
		this.nodeIterator = path.getNodes().iterator();
		this.lastNode = new Node(path.getStart(), path.getGrid());
		this.currentNode = nodeIterator.next();
		this.index = 1;
		this.tried = tried;
		this.callback = callback;
	}
	public void start() {
		if(!miner.walkAnimation.isRunning())
			miner.walkAnimation.start(miner.getArmorStand());
		else
			System.out.println("[WARNING] Double Walkingdispatcher on Minion " + miner.getArmorStand().getName());
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniMiners.getInstance(), ()->{
			dispatch();
		}, delay, delay);
	}
	public void stop() {
		Bukkit.getScheduler().cancelTask(taskID);
		miner.walkAnimation.stop();
		callback.onStop();
	}
	public void dispatch() {

		if(miner.getLocation().distance(currentNode.getSelf())<=0.5) {
			if(nodeIterator.hasNext()) {
				this.lastNode = currentNode;
				this.currentNode=nodeIterator.next();
				this.index++;
			}
			else {
				stop();
				if(currentNode.getSelf().distance(path.getStop())>1) {
					if(tried>=3) throw new PathfinderException("Cannot find valid path");
					Path path2 = new Path(currentNode.getSelf(), path.getStop());
					path2.init();
					path2.findPath();
					new WalkingDispatcher(path2, miner, delay,tried+1,callback).start();
				}
		
			}
		} 
		Location target = currentNode.getSelf();
		Location from = lastNode.getSelf();
		Vector v = new Vector(target.getX()-from.getX(),0,target.getZ()-from.getZ());
		v.multiply(0.4F);
		if(v.getX()==0&&v.getY()==0&&v.getZ()==0) //minion stuck
			miner.setLocation(target);
		else
			miner.setLocation(miner.getLocation().clone().add(v));
		
		
		if(index<path.getNodes().size())
			miner.rotateHeadTo(path.getNodes().get(index).getSelf());
		
	}
	public Path getPath() {
		return path;
	}


}
