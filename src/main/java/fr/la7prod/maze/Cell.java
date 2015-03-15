package fr.la7prod.maze;

import fr.la7prod.maze.entity.Actionable;
import fr.la7prod.maze.util.Coordinates;
import fr.la7prod.maze.util.Direction;

public class Cell extends Coordinates {
	
	public static final int PIXEL_SIZE = 32;
	
	private int value;
	private Actionable actionable;
	
	public Cell(int x, int y) {
		super(x, y);
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public Actionable getActionable() {
		return this.actionable;
	}
	
	public void setActionable(Actionable actionable) {
		this.actionable = actionable;
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
	
	public boolean hasWall(Direction d) {
		return (value & d.exponent()) == 0;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && this.value == ((Cell)o).value;
	}
	
	@Override
	public String toString() {
		return "Cell:" + super.toString() + " Actionable: " + actionable.toString();
	}

}
