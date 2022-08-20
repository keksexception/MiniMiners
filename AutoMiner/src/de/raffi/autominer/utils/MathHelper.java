package de.raffi.autominer.utils;

public class MathHelper {
	
	public static double min(double value, double min) {
		return value < min ? min : value;
	}
	public static double max(double value, double max) {
		return value > max ? max : value;
	}

}
