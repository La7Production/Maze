package server.maze.algorithms;

import server.maze.Cell;
import server.maze.Maze;

public abstract class MazeAlgorithm {
	
	public abstract void carve(Cell c, Maze maze);
	
	public abstract Maze generate(int width, int height, int xstart, int ystart);
	
	public abstract String toString(Maze maze);

}
