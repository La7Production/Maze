package fr.la7prod.maze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.jetty.websocket.api.Session;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.la7prod.maze.algorithms.RecursiveBT;
import fr.la7prod.maze.entity.Observer;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Coordinates;
import fr.la7prod.maze.util.Direction;
import fr.la7prod.maze.util.JSONable;

@XmlRootElement
public class MazeServer extends JSONable {
	
	private boolean running;
	private String title;
	private int max_slots;	
	private Player master;
	private Maze maze;
	
	@JsonIgnore
	private Map<Session, Player> playerMap;
	@JsonIgnore
	private Map<Session, Observer> observerMap;
	
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
	
	public Player getMaster() {
		return this.master;
	}
	
	public void setMaster(Player master) {
		this.master = master;
	}
	
	public Maze getMaze() {
		return this.maze;
	}
	
	public void setMaze(Maze maze) {
		this.maze = maze;
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
	
	@JsonIgnore
	public Set<Session> getPlayerSessions() {
		return playerMap.keySet();
	}
	
	@JsonIgnore
	public Set<Session> getObserverSessions() {
		return observerMap.keySet();
	}
	
	@JsonIgnore
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
		this.setMasterRandomly();
		for (Player p : playerMap.values()) {
			initPlayer(p);
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
		this.master = null;
		this.maze = null;
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

}
