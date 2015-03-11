package fr.la7prod.maze.algorithms;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.Maze;

public interface MazeAlgorithm {
	
	public void carve(Cell c, Maze maze);
	
	public Maze generate(int width, int height, int xstart, int ystart);
	
	public String toString(Maze maze);

}
