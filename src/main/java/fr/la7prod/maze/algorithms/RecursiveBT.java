package fr.la7prod.maze.algorithms;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.Maze;
import fr.la7prod.maze.util.Direction;

public class RecursiveBT implements MazeAlgorithm {

	@Override
	public void carve(Cell c, Maze maze) {
		Direction[] directions = Direction.shuffles();
		Cell n;
		for (Direction d : directions) {
			n = c.add(d);
			if (maze.include(n)) {
				n = maze.getCell(n.getX(), n.getY());
				if (n.getValue() == 0) {
					c.setValue(c.getValue() | d.exponent());
					n.setValue(n.getValue() | d.getOpposite().exponent());
					carve(n, maze);
				}
			}
		}
	}

	@Override
	public Maze generate(int width, int height, int xstart, int ystart) {
		Maze maze = new Maze(width, height);
		carve(maze.getCell(xstart, ystart), maze);
		Cell[] cells = maze.getCells();
		maze.setStart(cells[(int)(Math.random()*cells.length)]);
		defineExit(maze);
		return maze;
	}
	
	private void defineExit(Maze maze) {
		Queue<Cell> q = new ArrayDeque<Cell>();
		Map<Cell, Integer> m = new HashMap<Cell, Integer>();		
		Cell c;
		Cell n;
		Cell max = maze.getStart();
		int value;
		
		for (Cell cell : maze.getCells())
			m.put(cell, 0);

		q.offer(max);
		
		while (!q.isEmpty()) {
			c = q.remove();
			for (Direction d : Direction.values()) {
				n = c.add(d);
				if (!maze.hasWall(c, d) && maze.include(n)) {
					n = maze.getCell(n.getX(), n.getY());
					if (m.get(n) == 0) {
						value = m.get(c) + 1;
						m.put(n, value);
						if (value > m.get(max)) {
							max = n;
						}
						q.offer(n);
					}
				}
			}		
		}
		maze.setExit(max);
	}

	@Override
	public String toString(Maze maze) {
		String result = " ";
		int y = 0;
		
		for (int x = 0; x < (maze.getWidth() * 2 - 1); x++) {
			result += "_";
		}
		result += "\n|";
		
		for (Cell c : maze.getCells()) {
			if (c.getY() != y) {
				y = c.getY();
				result += "\n|";
			}
			result += (c.getValue() & Direction.SOUTH.exponent()) != 0 ? " " : "_";
			if ((c.getValue() & Direction.EAST.exponent()) != 0)
				result += ((c.getValue() | maze.getCell(c.getX()+1, c.getY()).getValue()) & Direction.SOUTH.exponent()) != 0 ? " " : "_";
			else
				result += "|";
		}		
		return result;
	}

}
