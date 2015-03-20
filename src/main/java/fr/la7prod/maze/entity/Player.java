package fr.la7prod.maze.entity;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.Maze;
import fr.la7prod.maze.util.Coordinates;
import fr.la7prod.maze.util.Direction;

public class Player {
	
	public static final int MAX_HASTE = 8;
	
	private String name;
	private double haste;
	private Coordinates c;
	
	public Player(String name, int x, int y) {
		this.name = name;
		this.c = new Coordinates(x,y);
		this.resetHaste();
	}
	
	public Player(String name) {
		this(name, 0,0);
	}
	
	public Player() {
		this(null);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getHaste() {
		return this.haste;
	}
	
	public void setHaste(double haste) {
		this.haste = haste;
	}
	
	public Coordinates getCoordinates() {
		return this.c;
	}
	
	public void setCoordinates(Coordinates c) {
		this.c = c;
	}
	
	public void place(int x, int y) {
		this.c.setX(x);
		this.c.setY(y);
	}
	
	public void place(Coordinates c) {
		this.c.setX(c.getX());
		this.c.setY(c.getY());
	}
	
	public void move(int x, int y) {
		this.c = this.c.add((int)(x * haste), (int)(y * haste));
	}
	
	public void move(Direction d) {
		this.c = this.c.add((int)(d.getX() * haste), (int)(d.getY() * haste));
	}
	
	public void incHaste() {
		this.haste += 0.01;
	}
	
	public void resetHaste() {
		this.haste = 1.0;
	}
	
	public Cell getLocation(Maze maze, final int PIXEL_SIZE) {
		int x = c.getX() / PIXEL_SIZE;
		int y = c.getY() / PIXEL_SIZE;
		return maze.include(x, y) ? maze.getCell(x, y) : null;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof Player) && ((Player) o).name.equals(this.name) && ((Player) o).c.equals(this.c);
	}
	
	@Override
	public String toString() {
		return String.format("Player(%s) is in (x=%d,y=%d)", name, c.getX(), c.getY());
	}

}
