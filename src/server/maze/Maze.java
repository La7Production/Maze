package server.maze;

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
	private Cell[] cell;
	private Generator generator;
	
	public Maze(int width, int height, Generator generator) {
		this.width = width;
		this.height = height;
		this.cell = new Cell[width*height];
		this.generator = generator;
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
	 * @return the cell
	 */
	public Cell[] getCell() {
		return cell;
	}

	/**
	 * @param cell the cell to set
	 */
	public void setCell(Cell[] cell) {
		this.cell = cell;
	}
	
	public Cell getCell(int x, int y) {
		return cell[y * width + x];
	}
	
	public void setCell(int x, int y) {
		this.cell[y*width+x] = new Cell(x,y);
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

}
