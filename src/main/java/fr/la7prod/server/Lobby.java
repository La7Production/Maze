package fr.la7prod.server;

import fr.la7prod.maze.MazeGame;

public class Lobby {
	
	private String title;
	private MazeGame game;
	private int slots;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public MazeGame getGame() {
		return game;
	}
	
	public void setGame(MazeGame game) {
		this.game = game;
	}
	
	public int getSlots() {
		return slots;
	}
	
	public void setSlots(int slots) {
		this.slots = slots;
	}
	
}
