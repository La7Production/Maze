package fr.la7prod.maze.util;

public class Coordinates {
	
	private int x;
	private int y;
	
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public Coordinates add(int x, int y) {
		return new Coordinates(this.x + x, this.y + y);
	}
	
	public Coordinates add(Coordinates c) {
		return add(c.getX(), c.getY());
	}
	
	public Coordinates add(Direction d) {
		return add(d.getCoordinates());
	}
	
	public boolean equals(Coordinates c) {
		return this.x == c.x && this.y == c.y;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Coordinates && equals((Coordinates)o);
	}
	
	@Override
	public String toString() {
		return String.format("(x:%d,y:%d)", x, y);
	}

}
