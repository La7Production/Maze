package fr.la7prod.server;

import java.util.ArrayList;
import java.util.List;

import fr.la7prod.maze.MazeServer;

public class Lobby {
	
	private List<MazeServer> servers;
	
	public Lobby() {
		this.servers = new ArrayList<MazeServer>();
	}
	
	public boolean contains(MazeServer server) {
		return servers.contains(server);
	}
	
	public MazeServer getServer(int i) {
		return servers.get(i);
	}
	
	public boolean addServer(MazeServer server) {
		return servers.add(server);
	}
	
	public MazeServer removeServer(int i) {
		return servers.remove(i);
	}
	
	public boolean removeServer(MazeServer server) {
		return servers.remove(server);
	}
	
}
