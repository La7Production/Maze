package fr.la7prod.maze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import fr.la7prod.maze.algorithms.RecursiveBT;
import fr.la7prod.maze.entity.Observer;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Coordinates;
import fr.la7prod.maze.util.Direction;
import fr.la7prod.maze.util.JSONable;

public class MazeGame implements JSONable {
	
	private boolean running;
	private Maze maze;
	private Player master;
	private Map<Session, Player> players;
	private Map<Session, Observer> observers;
	private int slots;
	
	public MazeGame(int width, int height, int slots) {
		this.maze = new RecursiveBT().generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		this.players = new HashMap<Session, Player>();
		this.observers = new HashMap<Session, Observer>();
		this.slots = slots;
	}
	
	public Maze getMaze() {
		return this.maze;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public int availableSlots() {
		return slots - players.size();
	}
	
	public int maxSlots() {
		return this.slots;
	}
	
	public int countPlayers() {
		return players.size();
	}
	
	public int countObservers() {
		return observers.size();
	}
	
	public Set<Session> getPlayerSessions() {
		return players.keySet();
	}
	
	public Set<Session> getObserverSessions() {
		return observers.keySet();
	}
	
	public Set<Session> getSessions() {
		Set<Session> sessions = new HashSet<Session>(players.keySet());
		if (sessions.addAll(observers.keySet()))
			return sessions;
		return null;
	}
	
	public Collection<Player> getPlayers() {
		return players.values();
	}
	
	public Collection<Observer> getObservers() {
		return observers.values();
	}
	
	public Player addPlayer(Session s, Player p) {
		return players.put(s, p);
	}
	
	public Observer addObserver(Session s, Observer o) {
		return observers.put(s, o);
	}
	
	public Player removePlayer(Session s) {
		Player.NB_INSTANCE--;
		return players.remove(s);
	}
	
	public Observer removeObserver(Session s) {
		return observers.remove(s);
	}
	
	public Player getPlayer(Session s) {
		return players.get(s);
	}
	
	public Observer getObserver(Session s) {
		return observers.get(s);
	}
	
	public void setMazeMaster(Player master) {
		this.master = master;
	}
	
	public void setMazeMasterRandomly() {
		List<Player> ps = new ArrayList<Player>(this.players.values());
		this.master = ps.get((int)(Math.random()*ps.size()));
	}
	
	public boolean isMazeMaster(Player p) {
		return this.master.equals(p);
	}
	
	public Cell getLocation(Player p) {
		return p.getLocation(maze, Cell.PIXEL_SIZE);
	}
	
	public boolean canMove(Player p, Direction d) {
		Cell c = getLocation(p);
		Coordinates pc = p.getCoordinates();
		boolean hasWall = c.hasWall(d);
		
		if (hasWall) {
			if (d.equals(Direction.NORTH) && pc.getY() > c.getY()*Cell.PIXEL_SIZE)
				return true;
			if (d.equals(Direction.EAST) && pc.getX()+1 != (c.getX()+1)*Cell.PIXEL_SIZE)
				return true;
			if (d.equals(Direction.SOUTH) && pc.getY()+1 != (c.getY()+1)*Cell.PIXEL_SIZE)
				return true;
			if (d.equals(Direction.WEST) && pc.getX() > c.getX()*Cell.PIXEL_SIZE)
				return true;
			return false;
		}
		return true;
	}
	
	public void move(Player p, Direction d) {
		p.move(d);
	}
	
	public void clearAll() {
		this.maze = null;
		this.master = null;
		this.players.clear();
		this.observers.clear();
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
		int i = 1;
		for (Player p : players.values()) {
			p.place(start.getX()*Cell.PIXEL_SIZE+1, start.getY()*Cell.PIXEL_SIZE+i);
			i++;
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
		json.put("players", map.values());
		if (master != null)
			json.put("master", master.getName());
		return json;
	}*/
	
	@Override
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("maze", maze.toJson());
		json.put("players", players.values());
		if (master != null)
			json.put("master", master.getName());
		return json;
	}

}
