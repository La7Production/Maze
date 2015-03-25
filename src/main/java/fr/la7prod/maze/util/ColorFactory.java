package fr.la7prod.maze.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColorFactory {
	
	private static final List<String> colors = new ArrayList<String>();
	
	static {
		colors.add(toHex(Color.RED));
		colors.add(toHex(Color.BLUE));
		colors.add(toHex(Color.ORANGE));
		colors.add(toHex(Color.GREEN));
		colors.add(toHex(Color.CYAN));
		colors.add(toHex(Color.GRAY));
	}
	
	public static String toHex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public static List<String> getColors() {
		return colors;
	}

}
