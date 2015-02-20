package fr.la7prod.maze.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.la7prod.maze.util.Direction;

public class TestsDirection {

	@Test
	public void test_directions() {
		assertEquals(Direction.NORTH.getX(), 0);
		assertEquals(Direction.NORTH.getY(), -1);
		assertEquals(Direction.EAST.getX(), 1);
		assertEquals(Direction.EAST.getY(), 0);
		assertEquals(Direction.SOUTH.getX(), 0);
		assertEquals(Direction.SOUTH.getY(), 1);
		assertEquals(Direction.WEST.getX(), -1);
		assertEquals(Direction.WEST.getY(), 0);
	}
	
	@Test
	public void test_exponent() {
		assertEquals(Direction.NORTH.exponent(), 1);
		assertEquals(Direction.EAST.exponent(), 2);
		assertEquals(Direction.SOUTH.exponent(), 4);
		assertEquals(Direction.WEST.exponent(), 8);
	}

}
