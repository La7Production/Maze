package server.maze;

/**
 * 
 * @author Edouard CATTEZ
 *
 */
public class Cell {
	
	public enum Type {
		
		VOID("_"), TRAP("P"), WALL("#");
		
		private String image;
		
		private Type(String image) {
			this.image = image;
		}
		
		public String toString() {
			return this.image;
		}
		
	}
	
	private int x;
	private int y;
	private Type type;
	
	public Cell(int x, int y, Type type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public Cell(int x, int y) {
		this(x, y, Type.VOID);
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Cell add(int x, int y) {
		return new Cell(this.x+x, this.y+y);
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public Cell add(Cell c) {
		return add(c.getX(), c.getY());
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Cell subtract(int x, int y) {
		return new Cell(this.x-x, this.y-y);
	}
	
	public Cell subtract(Cell c) {
		return subtract(c.getX(), c.getY());
	}
	
	public String toString() {
		return this.type.toString();
	}

}
