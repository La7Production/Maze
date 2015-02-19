package server.maze.tests;

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
	
	public boolean verifier_sortie(Maze maze, Cell start, Cell end) {
		Queue<Cell> q = new ArrayDeque<Cell>();
		Map<Cell, Integer> marque = new HashMap<Cell, Integer>();
		final int VIDE = 0;
		final int MARQUE = 1;
		final int RETOUR = 2;
		
		for (Cell c : maze.getCells()) {
			marque.put(c, VIDE);
		}
		
		Cell c;
		Cell n;
		
		q.offer(start);
		marque.put(start, MARQUE);
		
		while (!q.isEmpty()) {
			c = q.remove();
			marque.put(c, RETOUR);
			
			if (c.equals(end))
				return true;
			
			for(int y=-1+c.getY(); y<=1+c.getY(); y++) {
				for(int x=-1+c.getX(); x<=1+c.getX(); x++) {
					
					n = new Cell(x,y);
					
					// Si le & logique entre la cellule courante et la cellule voisine = exposant de 2
					// alors un mur les séparent.
					if (maze.include(n)) {
						n = maze.getCell(n.getX(), n.getY());
						if (Math.abs(y) != Math.abs(x) && !maze.hasWallBetween(c, n) && marque.get(n) != VIDE) {
							marque.put(n, MARQUE);
							q.offer(n);
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
		System.out.println(algo.toString(maze));
	}
	
	@Test
	public void test_RecursiveBT_cheminValide() {
		Maze maze;
		algo = new RecursiveBT();
		maze = algo.generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		verifier_sortie(maze, maze.getCell(0,0), maze.getCell(width-1,height-1));
	}

}
