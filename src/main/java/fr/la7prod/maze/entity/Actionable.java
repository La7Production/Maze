package fr.la7prod.maze.entity;

import fr.la7prod.maze.Cell;

public abstract class Actionable {
	
	private String name;
	private boolean enabled;
	
	public Actionable() {}
	
	public Actionable(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void enable() {
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public abstract void activate(Cell c);

}
