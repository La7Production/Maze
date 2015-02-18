package server.maze.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Direction {
	
	NORTH(0,-1), EAST(1,0), SOUTH(0,1), WEST(-1,0);
	
	private Coordinates c;

	private Direction(int x, int y) {
		this.c = new Coordinates(x, y);
	}
	
	public Coordinates getCoordinates() {
		return this.c;
	}

	public int getX() {
		return c.getX();
	}
	public int getY() {
		return c.getY();
	}

	public Direction getOpposite() {
		if (this.equals(NORTH))
			return SOUTH;
		if (this.equals(EAST))
			return WEST;
		if (this.equals(SOUTH))
			return NORTH;
		return EAST;
	}
	
	public int exponent() {
		return (int) Math.pow(2, this.ordinal());
	}
	
	public static Direction[] shuffles() {
		List<Direction> directions = Arrays.asList(Direction.values());
		Collections.shuffle(directions);
		return directions.toArray(new Direction[directions.size()]);
	}

}
