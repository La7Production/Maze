package fr.la7prod.maze.entity;

import org.eclipse.jetty.websocket.api.Session;

import fr.la7prod.maze.MazeServer;
import fr.la7prod.server.websocket.GameThread;


public class Observer extends HumanEntity {
	
	private GameThread thread = new GameThread(100);
	
	public Observer() {}

	// Représente un utilisateur sous Android qui peut observer le jeu
	public Observer(String name) {
		super(name);
	}
	
	public void openThread(Session session, MazeServer server) {
		this.thread.setSession(session);
		this.thread.setServer(server);
	}
	
	public void startListening() {
		this.thread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void closeListening() {
		this.thread.pause();
		this.thread.stop();
	}

}
