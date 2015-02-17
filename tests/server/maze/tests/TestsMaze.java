package server.maze.tests;

import org.junit.Test;

import server.maze.Maze;

public class TestsMaze {

	@Test
	public void test() {
		Maze maze = new Maze(10,10);
		maze.generate();
		System.out.println(maze);
	}

}
