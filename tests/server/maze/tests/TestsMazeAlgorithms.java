package server.maze.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.junit.Test;

import server.maze.Cell;
import server.maze.Maze;
import server.maze.algorithms.MazeAlgorithm;
import server.maze.algorithms.RecursiveBT;

public class TestsMazeAlgorithms {
	
	public static int width = 10;
	public static int height = 10;
	public static MazeAlgorithm algo;
	
	private void display(Maze maze, Map<Cell, Integer> marks) {
		String result = "";
		int y=0;
		for (Cell c : maze.getCells()) {
			if (y != c.getY()) {
				y = c.getY();
				result += "\n";
			}
			result += marks.get(c) + "\t";
		}
		result += "\n";
		System.out.println(result);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private boolean verifier_sortie(Maze maze, Cell start, Cell end) {
		Queue<Cell> q = new ArrayDeque<Cell>();
		Map<Cell, Integer> marks = new HashMap<Cell, Integer>();
		final int available = 0;
		final int used = 1;
		final int back = 2;
		Cell c;
		Cell n;
		
		for (Cell cell : maze.getCells())
			marks.put(cell, available);
		
		q.offer(start);
		marks.put(start, used);
		
		while (!q.isEmpty()) {
			display(maze, marks);
			System.out.println(algo.toString(maze));
			c = q.remove();
			marks.put(c, back);
			
			if (c.equals(end))
				return true;
			
			for(int y=-1+c.getY(); y<=1+c.getY(); y++) {
				for(int x=-1+c.getX(); x<=1+c.getX(); x++) {
					
					n = new Cell(x,y);
					
					if (maze.include(n)) {
						n = maze.getCell(n.getX(), n.getY());
						if (Math.abs(y) != Math.abs(x) && !maze.hasWallBetween(c, n) && marks.get(n) != available) {
							marks.put(n, used);
							q.offer(n);
							System.out.println("offer");
						}
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
		//System.out.println(algo.toString(maze));
	}
	
	@Test
	public void test_RecursiveBT_cheminValide() {
		Maze maze;
		algo = new RecursiveBT();
		maze = algo.generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		assertTrue(verifier_sortie(maze, maze.getCell(0,0), maze.getCell(width-1,height-1)));
	}

}
