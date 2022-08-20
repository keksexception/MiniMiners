package de.raffi.autominer.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;

public class ColorManager {
	
	private List<Integer> rgbValues = new ArrayList<>();
	
	public ColorManager() {
		rgbValues.add(Color.YELLOW.asRGB());
		rgbValues.add(Color.ORANGE.asRGB());
		rgbValues.add(Color.RED.asRGB());
		rgbValues.add(Color.GRAY.asRGB());
		rgbValues.add(Color.BLACK.asRGB());
		rgbValues.add(Color.BLUE.asRGB());
		rgbValues.add(Color.MAROON.asRGB());
		rgbValues.add(Color.BLACK.asRGB());
		rgbValues.add(Color.FUCHSIA.asRGB());
		
	}
	
	
	private int index;
	
	public int getNextRGB() {
		int next = rgbValues.get(index);
		index++;
		index%=rgbValues.size();
		return next;
	}
	public Color getNextColor() {
		return Color.fromRGB(getNextRGB());
	}
	public int getIndex() {
		return index;
	}
	public List<Integer> getRgbValues() {
		return rgbValues;
	}
	public void reset() {
		index=0;
	}
	
}
