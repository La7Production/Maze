package fr.la7prod.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import fr.la7prod.maze.MazeServer;

@XmlRootElement
public class Lobby {
	
	private List<MazeServer> servers;
	
	public Lobby() {
		this.servers = new ArrayList<MazeServer>();
	}
	
	public List<MazeServer> getServers() {
		return this.servers;
	}
	
	public void setServers(List<MazeServer> servers) {
		this.servers = servers;
	}
	
	public int size() {
		return servers.size();
	}
	
	public boolean isEmpty() {
		return servers.isEmpty();
	}
	
	public boolean contains(MazeServer server) {
		return servers.contains(server);
	}
	
	public boolean contains(String title) {
		Iterator<MazeServer> i = servers.iterator();
		MazeServer s;
		while (i.hasNext()) {
			s = i.next();
			if (s.getTitle().equals(title))
				return true;
		}
		return false;
	}
	
	public MazeServer get(int i) {
		return servers.get(i);
	}
	
	public MazeServer get(String title) {
		Iterator<MazeServer> i = servers.iterator();
		MazeServer s;
		while (i.hasNext()) {
			s = i.next();
			if (s.getTitle().equals(title))
				return s;
		}
		return null;
	}
	
	public boolean add(MazeServer server) {
		return servers.add(server);
	}
	
	public boolean add(String title, int slots) {
		return servers.add(new MazeServer(title, slots));
	}
	
	public MazeServer remove(int i) {
		return servers.remove(i);
	}
	
	public boolean remove(String title) {
		Iterator<MazeServer> i = servers.iterator();
		MazeServer s;
		while (i.hasNext()) {
			s = i.next();
			if (s.getTitle().equals(title))
				return servers.remove(s);
		}
		return false;
	}
	
	public boolean remove(MazeServer server) {
		return servers.remove(server);
	}
	
	@Override
	public String toString() {
		return "Lobby: " + servers.toString();
	}
	
}
