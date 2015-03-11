package fr.la7prod.maze;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class MazeZone {
	
	public static int DEFAULT_RANGE = 8;
	
	private Maze maze;
	private Cell cell;
	private int range;
	
	public MazeZone(Maze maze, int x, int y) {
		this.maze = maze;
		this.cell = maze.getCell(x, y);
		this.range = DEFAULT_RANGE;
	}
	
	public Cell getCell() {
		return this.cell;
	}
	
	public void setCell(Cell cell) {
		this.cell = cell;
	}
	
	public int getRange() {
		return this.range;
	}
	
	public void setRange(int range) {
		this.range = range;
	}
	
	public JSONObject toJson() {
		List<Cell> cells = new ArrayList<Cell>(range * range);
		int r = range / 2;
		for (int y = cell.getY() - r; y <= cell.getY() + r; y++) {
			for (int x = cell.getX() - r; x <= cell.getX() + r; x++) {
				if (maze.include(x, y)) {
					cells.add(maze.getCell(x, y));
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("cells", cells);
		return json;
	}

}
