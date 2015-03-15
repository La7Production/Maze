package fr.la7prod.maze;

import org.json.JSONObject;

import fr.la7prod.maze.util.Direction;

public class Maze {
	
	public static final int DEFAULT_WIDTH = 10;
	public static final int DEFAULT_HEIGHT = 10;
	
	private int width;
	private int height;
	private Cell[] cells;
	
	public Maze(int width, int height) {
		this.width = width;
		this.height = height;
		this.cells = new Cell[width*height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				cells[y * width + x] = new Cell(x,y);
			}
		}
	}
	
	public Maze() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int size() {
		return this.width * this.height;
	}

	public Cell[] getCells() {
		return this.cells;
	}

	public void setCells(Cell[] cells) {
		this.cells = cells;
	}
	
	public boolean include(int x, int y) {
		return y >= 0 && y < height && x >= 0 && x < width;
	}
	
	public boolean include(Cell c) {
		return include(c.getX(), c.getY());
	}
	
	public boolean contains(Cell c) {
		return include(c) && cells[c.getY() * width + c.getX()].equals(c);
	}
	
	public Cell getCell(int x, int y) {
		return this.cells[y * width + x];
	}
	
	public int getValue(int x, int y) {
		return this.cells[y * width + x].getValue();
	}
	
	public void setCell(int x, int y, int value) {
		cells[y * width + x].setValue(value);
	}
	
	public void setCell(Cell c, int value) {
		c.setValue(value);
	}
	
	public boolean hasWall(Cell c, Direction d) {
		return c.hasWall(d);
	}
	
	public Cell getStart() {
		return cells[0];
	}
	
	public Cell getExit() {
		return cells[size()-1];
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("width", width);
		json.put("height", height);
		json.put("pixel", Cell.PIXEL_SIZE);
		json.put("cells", this.getCells());
		return json;
	}

}
