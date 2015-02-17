package server.maze;

import server.maze.Cell.Type;

/**
 * 
 * @author Edouard CATTEZ
 *
 */
public class Maze {
	
	public static final int DEFAULT_WIDTH = 10;
	public static final int DEFAULT_HEIGHT = 10;
	
	private int width;
	private int height;
	private Cell[] cells;
	private Generator generator;
	
	public Maze(int width, int height, Generator generator) {
		this.width = width;
		this.height = height;
		this.cells = new Cell[width*height];
		this.generator = generator;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				cells[y * width + x] = new Cell(x, y, Type.WALL);
			}
		}
	}
	
	public Maze(int width, int height) {
		this(width, height, new WormGenerator());
	}
	
	public Maze() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 *
	 * @return the size (width*height)
	 */
	public int size() {
		return width * height;
	}

	/**
	 * @return the cells
	 */
	public Cell[] getCells() {
		return cells;
	}

	/**
	 * @param cells the cells to set
	 */
	public void setCells(Cell[] cell) {
		this.cells = cell;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Cell getCell(int x, int y) {
		return cells[y * width + x];
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void setCell(int x, int y, Type type) {
		this.cells[y * width + x].setType(type);
	}
	
	public boolean contains(Cell cell) {
		int r = cell.getY() * width + cell.getX();
		return r >= 0 && r < size();
	}

	/**
	 * @return the generator
	 */
	public Generator getGenerator() {
		return generator;
	}

	/**
	 * @param generator the generator to set
	 */
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}
	
	/**
	 * 
	 */
	public Maze generate() {
		return this.generator.generate(this);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return the display
	 */
	public String display(int x, int y, int width, int height) {
		String disp = "";
		for (int i = y; i < height; i++) {
			for (int j = x; j < width; j++) {
				disp += cells[i * width + j].toString();
			}
			disp += "\n";
		}
		return disp;
	}
	
	/**
	 * 
	 * @return the display
	 */
	@Deprecated
	public String display() {
		return this.display(0, 0, width, height);
	}
	
	/**
	 * 
	 */
	public String toString() {
		return this.display();
	}

}
