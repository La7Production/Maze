package fr.la7prod.maze.entity;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.Maze;
import fr.la7prod.maze.util.Coordinates;
import fr.la7prod.maze.util.Direction;

public class Player extends HumanEntity {
	
	public static final int MAX_HASTE = 8;
	public static int NB_INSTANCE = 0;
	
	private double haste;
	private Coordinates c;
	private String color;
	
	public Player(String name, int x, int y) {
		super(name);
		this.c = new Coordinates(x,y);
		this.color = String.format("#%02x%02x%02x", Math.random()*255, Math.random()*255, Math.random()*255);
		this.resetHaste();
	}
	
	public Player(String name) {
		this(name, 0,0);
	}
	
	public Player() {
		this(null);
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
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
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
		this.c = this.c.add((int)(x*haste), (int)(y*haste));
	}
	
	public void move(Direction d) {
		this.c = this.c.add((int)(d.getX()*haste), (int)(d.getY()*haste));
	}
	
	public void incHaste() {
		this.haste += 0.01;
	}
	
	public void resetHaste() {
		this.haste = 1.0;
	}
	
	public Cell getLocation(Maze maze, final int pixel_size) {
		int x = c.getX() / pixel_size;
		int y = c.getY() / pixel_size;
		return maze.include(x, y) ? maze.getCell(x, y) : null;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && (o instanceof Player) && ((Player) o).c.equals(this.c);
	}
	
	@Override
	public String toString() {
		return String.format("%s is in (x=%d,y=%d)", super.toString(), c.getX(), c.getY());
	}

}
