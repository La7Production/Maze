package server.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import server.maze.Cell.Type;

/**
 * 
 * @author Edouard CATTEZ
 *
 */
public class WormGenerator extends Generator {
	
	private List<Cell> findNeighbors(Maze maze, Cell current) {
		Cell nb;
		List<Cell> neighbors = new ArrayList<Cell>();
		
		for (int i=-1; i<=1; i++) {
			for (int j=-1; j<=1; j++) {
				try {
					nb = maze.getCell(current.getX()-j, current.getY()-i);
					if (nb.getType().equals(Type.WALL))
						neighbors.add(nb);
				} catch(ArrayIndexOutOfBoundsException e) {
					// Do nothing
				}
			}
		}
		
		return neighbors;
	}

	@Override
	public Maze generate(Maze maze) {
		int size = maze.size();
		int width = maze.getWidth();
		int height = maze.getHeight();
		int x = (int)(Math.random() * width);
		int y = (int)(Math.random() * height);
		int visited = 1;
		
		Stack<Cell> locations = new Stack<Cell>();
		Cell current = maze.getCell(x, y);
		Cell nb;
		Cell knock;
		List<Cell> neighbors;
		
		while (visited < size) {
			neighbors = findNeighbors(maze, current);
			if (neighbors.size() > 0) {
				nb = neighbors.get((int)(Math.random()*neighbors.size()));
				knock = current.subtract(nb);
				if (!maze.contains(knock)) {
					knock = nb.subtract(current);
				}
				nb.setType(Type.VOID);
				//maze.setCell(knock.getX(), knock.getY(), knock.getType());
				locations.push(current);
				current = nb;
				visited++;
				System.out.println(maze);
				//System.out.println(nb.getX() + "," + nb.getY());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				current = locations.pop();
			}
		}
		return maze;
	}

}
