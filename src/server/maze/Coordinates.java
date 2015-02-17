package server.maze;

/**
 * 
 * @author Edouard CATTEZ
 *
 */
public enum Coordinates {
	
	NORTH(0,-1), SOUTH(0,1), EAST(1,0), WEST(-1,0);
	
	private int x;
	private int y;
	
	private Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	
}
