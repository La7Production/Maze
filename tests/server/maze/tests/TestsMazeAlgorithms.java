package server.maze.tests;

import org.junit.Test;

import server.maze.Cell;
import server.maze.Maze;
import server.maze.algorithms.MazeAlgorithm;
import server.maze.algorithms.RecursiveBT;

public class TestsMazeAlgorithms {
	
	public static int width = 50;
	public static int height = 50;
	public static MazeAlgorithm algo;
	
	public boolean verifier_sortie(Maze maze, Cell start, Cell end) {
		
		// TODO
		
		return false;
	}

	@Test
	public void test_RecursiveBT() {
		Maze maze;
		algo = new RecursiveBT();
		maze = algo.generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		System.out.println(algo.toString(maze));
		//verifier_sortie(maze, maze.getCell(0, 0), maze.getCell(width-1, height-1));
	}

}
