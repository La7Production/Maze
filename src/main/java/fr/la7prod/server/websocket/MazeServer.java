package fr.la7prod.server.websocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.Maze;
import fr.la7prod.maze.algorithms.RecursiveBT;
import fr.la7prod.maze.entity.Observer;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Coordinates;
import fr.la7prod.maze.util.Direction;
import fr.la7prod.maze.util.JSONable;

public class MazeServer implements JSONable {
	
	private boolean running;
	private String title;
	private Maze maze;
	private Player master;
	private Map<Session, Player> playerMap;
	private Map<Session, Observer> observerMap;
	private int max_slots;
	
	public MazeServer(String title, int max_slots) {
		this.title = title;
		this.max_slots = max_slots;
		this.playerMap = new HashMap<Session, Player>();
		this.observerMap = new HashMap<Session, Observer>();
	}
	
	public MazeServer() {
		this(null, 1);
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Maze getMaze() {
		return this.maze;
	}
	
	/*public void setMaze(Maze maze) {
		this.maze = maze;
	}*/
	
	/*public Map<Session, Player> getPlayerMap() {
		return this.playerMap;
	}*/
	
	public void setPlayerMap(Map<Session, Player> playerMap) {
		this.playerMap = playerMap;
	}
	
	/*public Map<Session, Observer> getObserverMap() {
		return this.observerMap;
	}*/
	
	public void setObserverMap(Map<Session, Observer> observerMap) {
		this.observerMap = observerMap;
	}
	
	public int getSlots() {
		return this.max_slots;
	}
	
	public void setSlots(int slots) {
		this.max_slots = slots;
	}
	
	public int maxSlots() {
		return this.max_slots;
	}
	
	public int availableSlots() {
		return max_slots - playerMap.size();
	}
	
	public int countPlayers() {
		return playerMap.size();
	}
	
	public int countObservers() {
		return observerMap.size();
	}
	
	public Set<Session> getPlayerSessions() {
		return playerMap.keySet();
	}
	
	public Set<Session> getObserverSessions() {
		return observerMap.keySet();
	}
	
	public Set<Session> getSessions() {
		Set<Session> sessions = new HashSet<Session>(playerMap.keySet());
		if (sessions.addAll(observerMap.keySet()))
			return sessions;
		return null;
	}
	
	public Collection<Player> getPlayers() {
		return playerMap.values();
	}
	
	public Collection<Observer> getObservers() {
		return observerMap.values();
	}
	
	public Player addPlayer(Session s, Player p) {
		return playerMap.put(s, p);
	}
	
	public Observer addObserver(Session s, Observer o) {
		return observerMap.put(s, o);
	}
	
	public Player removePlayer(Session s) {
		Player.NB_INSTANCE--;
		return playerMap.remove(s);
	}
	
	public Observer removeObserver(Session s) {
		return observerMap.remove(s);
	}
	
	public Player getPlayer(Session s) {
		return playerMap.get(s);
	}
	
	public Observer getObserver(Session s) {
		return observerMap.get(s);
	}
	
	/*public Player getMaster() {
		return this.master;
	}*/
	
	public void setMaster(Player master) {
		this.master = master;
	}
	
	public void setMasterRandomly() {
		List<Player> ps = new ArrayList<Player>(this.playerMap.values());
		this.master = ps.get((int)(Math.random()*ps.size()));
	}
	
	public boolean isMaster(Player p) {
		return this.master.equals(p);
	}
	
	public Cell getLocation(Player p) {
		return p.getLocation(maze, Cell.PIXEL_SIZE);
	}
	
	public boolean canMove(Player p, Direction d) {
		Cell c = getLocation(p);
		Coordinates pc = p.getCoordinates();
		
		if (c.hasWall(d)) {
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
		this.playerMap.clear();
		this.observerMap.clear();
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
		this.setMasterRandomly();
		int i = 1;
		for (Player p : playerMap.values()) {
			p.place(start.getX()*Cell.PIXEL_SIZE+1, start.getY()*Cell.PIXEL_SIZE+i);
			i++;
		}
	}
	
	public void initPlayer(Player p) {
		p.place(maze.getStart().getX()*Cell.PIXEL_SIZE+1, maze.getStart().getY()*Cell.PIXEL_SIZE+1);
	}
	
	public void start(int width, int height) {
		this.maze = new RecursiveBT().generate(width, height, (int)(Math.random()*width), (int)(Math.random()*height));
		this.initPlayers();
		this.running = true;
	}
	
	public void stop() {
		this.running = false;
		this.clearAll();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof MazeServer) && ((MazeServer)o).title.equals(title);
	}
	
	@Override
	public String toString() {
		return String.format("%s(%d)\n\trunning:%s\n\tplayers:%s\n\tobservers:%s\n\t", title, max_slots, running, getPlayers(), getObservers());
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("maze", maze.toJson());
		json.put("players", playerMap.values());
		//if (master != null)
		//	json.put("master", master.getName());
		return json;
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

}
