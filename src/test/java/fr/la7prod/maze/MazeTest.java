package fr.la7prod.maze;

import static org.junit.Assert.assertTrue;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.junit.Test;

import fr.la7prod.maze.algorithms.MazeAlgorithm;
import fr.la7prod.maze.algorithms.RecursiveBT;
import fr.la7prod.maze.util.Direction;

public class MazeTest {
	
	public static int width = 10;
	public static int height = 10;
	public static MazeAlgorithm algo;
	public static Map<Cell, Boolean> visited;
	
	private Cell randomCell(Maze maze) {
		return maze.getCell((int)(Math.random()*width), (int)(Math.random()*height));
	}
	
	/*private void displayVisited(Maze maze, Cell start, Cell end) {
		String result = "";
		boolean bool;
		int y=0;
		for(Cell c : maze.getCells()) {
			if (y != c.getY()) {
				y = c.getY();
				result += "\n";
			}
			bool = visited.get(c);
			if (c.equals(start))
				result += "D ";
			else if (c.equals(end))
				result += "F ";
			else if (bool)
				result += "* ";
			else
				result += "  ";
		}
		result += "\n";
		System.out.println(result);
	}*/
	
	private boolean checkPath(Maze maze, Cell start, Cell end) {
		Queue<Cell> q = new ArrayDeque<Cell>();
		Cell c;
		Cell n;
		
		for (Cell cell : maze.getCells())
			visited.put(cell, false);

		q.offer(start);
		visited.put(start, true);
		
		while (!q.isEmpty()) {
			c = q.remove();
			visited.put(c, true);
			
			if (c.equals(end))
				return true;
			
			for (Direction d : Direction.values()) {
				n = c.add(d);
				if (maze.include(n)) {
					n = maze.getCell(n.getX(), n.getY());
					if (!maze.hasWall(c, d) && !visited.get(n)) {
						visited.put(n, true);
						q.offer(n);
					}
				}
				
			}
		}
		
		return false;
	}

	@Test
	public void test_RecursiveBT() {
		Maze maze;
		algo = new RecursiveBT();
		maze = algo.generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		System.out.println(algo.toString(maze));
	}
	
	@Test
	public void test_RecursiveBT_cheminValide() {
		Maze maze;
		visited = new HashMap<Cell, Boolean>();
		algo = new RecursiveBT();
		maze = algo.generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		assertTrue(checkPath(maze, maze.getCell(0,0), maze.getCell(width-1,height-1)));
	}
	
	@Test
	public void test_RecursiveBT_cheminValide_aleatoire() {
		Maze maze;
		visited = new HashMap<Cell, Boolean>();
		algo = new RecursiveBT();
		maze = algo.generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		assertTrue(checkPath(maze, randomCell(maze), randomCell(maze)));
	}
	
	@Test
	public void test_JSON() {
		Maze m = new RecursiveBT().generate(10, 10, 0, 0);
		MazeZone zone = new MazeZone(m, 3, 3);
		System.out.println(m.toJson());
		System.out.println(zone.toJson());
	}

}
