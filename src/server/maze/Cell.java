package server.maze;

import server.maze.util.Coordinates;
import server.maze.util.Direction;

public class Cell extends Coordinates {
	
	private int value;
	
	public Cell(int x, int y) {
		super(x, y);
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public Cell add(int x, int y) {
		return new Cell(this.getX() + x, this.getY() + y);
	}
	
	public Cell add(Coordinates c) {
		return add(c.getX(), c.getY());
	}
	
	public Cell add(Direction d) {
		return add(d.getCoordinates());
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && this.value == ((Cell)o).value;
	}
	
	@Override
	public String toString() {
		return "Cell" + super.toString();
	}

}
