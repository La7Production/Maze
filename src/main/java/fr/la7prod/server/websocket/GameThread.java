package fr.la7prod.server.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import fr.la7prod.maze.MazeServer;

public class GameThread extends Thread {
	
	public enum State {
		ACTIVED, PAUSED, WAITING;
	}
	
	private Session session;
	private MazeServer server;
	private State state;
	private long sleep;
	
	public GameThread(long sleep) {
		this(null, null, sleep);
	}
	
	public GameThread(Session session, MazeServer server, long sleep) {
		this.session = session;
		this.server = server;
		this.state = State.PAUSED;
		this.sleep = sleep;
		super.start();
	}
	
	public synchronized Session getSession() {
		return this.session;
	}
	
	public synchronized void setSession(Session session) {
		this.session = session;
	}
	
	public synchronized MazeServer getServer() {
		return this.server;
	}
	
	public synchronized void setServer(MazeServer server) {
		this.server = server;
	}
	
	public synchronized long getSleepTime() {
		return this.sleep;
	}
	
	public synchronized void setSleepTime(long sleep) {
		this.sleep = sleep;
	}
	
	/**
	 * Indique si le thread est activé
	 * @return vrai si le thread est à l'état ACTIVED
	 */
	public synchronized boolean isActived() {
		return this.state.equals(State.ACTIVED);
	}
	
	/**
	 * Indique si le thread est en pause
	 * @return vrai si le thread est à l'état PAUSED
	 */
	public synchronized boolean isPaused() {
		return this.state.equals(State.PAUSED);
	}
	
	/**
	 * Indique si le thread est en attente en arrière plan.
	 * @return vrai si le thread est à l'état WAITING
	 */
	public synchronized boolean isWaiting() {
		return this.state.equals(State.WAITING);
	}
	
	/**
	 * Active le thread et en informe
	 * les observers susceptibles d'avoirsleep un regard sur le thread.
	 */
	public synchronized void start() {
		this.state = State.ACTIVED;
		this.sleep();
		this.notifyAll();
	}
	
	/**
	 * Met le thread en pause
	 */
	public synchronized void pause() {
		this.state = State.PAUSED;
	}
	
	/**
	 * Met le thread en arrière plan en attendant d'être réutilisée.
	 * Etre mis en arrière plan signifie que le modèle associé au thread
	 * n'est actuellement pas observé par l'utilisateur.
	 */
	public synchronized void waiting() {
		this.state = State.WAITING;
	}
	
	/**
	 * Met le thread en attente si le thread est en pause
	 * ou bien le met à l'état sleep pendant quelques secondes.
	 */
	private synchronized void sleep() {
		if (!this.isActived()) {
			try {
				wait();
			} catch(InterruptedException e) {}
		}
		else {
			try {
				Thread.sleep(sleep);
			} catch(InterruptedException e) {}
		}
	}
	
	@Override
	public void run() {
		while (true) {
			this.sleep();
			try {
				session.getRemote().sendString(server.toJsonString().toString());
				session.getRemote().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
