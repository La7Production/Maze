package fr.la7prod.maze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import fr.la7prod.maze.algorithms.RecursiveBT;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Coordinates;
import fr.la7prod.maze.util.Direction;

public class MazeGame {
	
	public static final int MAX_SLOTS = 5;
	
	private boolean running;
	private Maze maze;
	private Player master;
	private Map<Session, Player> map;
	
	public MazeGame(int width, int height) {
		this.maze = new RecursiveBT().generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		this.map = new HashMap<Session, Player>();
	}
	
	public Maze getMaze() {
		return this.maze;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public int availableSlots() {
		return MAX_SLOTS - map.size();
	}
	
	public int countPlayers() {
		return map.size();
	}
	
	public Set<Session> getSessions() {
		return map.keySet();
	}
	
	public Collection<Player> getPlayers() {
		return map.values();
	}
	
	public Player addPlayer(Session s, Player p) {
		return map.put(s, p);
	}
	
	public Player removePlayer(Session s) {
		return map.remove(s);
	}
	
	public Player getPlayer(Session s) {
		return map.get(s);
	}
	
	public void setMazeMaster(Player master) {
		this.master = master;
	}
	
	public void setMazeMasterRandomly() {
		List<Player> ps = new ArrayList<Player>(this.map.values());
		this.master = ps.get((int)(Math.random()*ps.size()));
	}
	
	public boolean isMazeMaster(Player p) {
		return this.master.equals(p);
	}
	
	public Cell getLocation(Player p) {
		return p.getLocation(maze);
	}
	
	public boolean canMove(Player p, Direction d) {
		Cell c = getLocation(p);
		return c != null && !c.hasWall(d);
	}
	
	public void move(Player p, Direction d) {
		p.move(d);
	}
	
	public void clearAll() {
		this.maze = null;
		this.master = null;
		this.map.clear();
	}
	
	public boolean movePerformed(Player p, Direction d) {
		if (canMove(p, d)) {
			move(p, d);
			return true;
		}
		return false;
	}
	
	public boolean win(Player p) {
		return getLocation(p).equals(maze.getExit());
	}
	
	public void initPlayers() {
		Cell start = maze.getStart();
		this.setMazeMasterRandomly();
		for (Player p : map.values()) {
			p.place(new Coordinates(start.getX() + Cell.PIXEL_SIZE/2, start.getY() + Cell.PIXEL_SIZE/2));
		}
	}
	
	public void start() {
		// TODO
		this.running = true;
	}
	
	public void stop() {
		// TODO
		this.running = false;
	}
	
	/*public MazeZone getZone(Player p) {
		Coordinates c = p.getCoordinates();
		return new MazeZone(maze, c.getX()/Cell.PIXEL_SIZE, c.getY()/Cell.PIXEL_SIZE);
	}
	
	public JSONObject toJsonFromPlayer(Player p) {
		JSONObject json = new JSONObject();
		json.put("maze", getZone(p).toJson());
		json.put("master", master);
		json.put("players", map.values());
		return json;
	}*/
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("maze", maze.toJson());
		json.put("players", map.values());
		if (master != null)
			json.put("master", master.getName());
		return json;
	}

}
