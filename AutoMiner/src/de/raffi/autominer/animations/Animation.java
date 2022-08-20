package de.raffi.autominer.animations;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

import de.raffi.autominer.exception.AnimationException;
import de.raffi.autominer.main.MiniMiners;

public abstract class Animation {
	
	private int delay, taskID;
	private boolean running;
	
	
	
	public Animation(int delay) {
		this.delay = delay;
		this.running = false;
	}
	/**
	 * method for creating the animation, called every {@link Animation#delay} ticks
	 * @param stand the ArmorStand that should be animated
	 */
	public abstract void animate(ArmorStand stand);
	/**
	 * 
	 * @param stand
	 * @throws AnimationException  when animation is already running
	 */
	public void start(ArmorStand stand) {
		if(isRunning()) throw new AnimationException("Animation already running");
		running = true;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniMiners.getInstance(), ()->{
			animate(stand);
		},delay ,delay);
	}
	/**
	 * stops the animation
	 */
	public void stop() {

		if(!isRunning()) throw new AnimationException("Cannot stop Animation that is not started");
		Bukkit.getScheduler().cancelTask(taskID);
		running = false;
	}
	public void init(ArmorStand stand) {
		
	}
	public boolean isRunning() {
		return running;
	}
}
