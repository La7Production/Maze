package fr.la7prod.maze.algorithms;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.Maze;

public abstract class MazeAlgorithm {
	
	public abstract void carve(Cell c, Maze maze);
	
	public abstract Maze generate(int width, int height, int xstart, int ystart);
	
	public abstract String toString(Maze maze);

}
