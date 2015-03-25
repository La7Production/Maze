package fr.la7prod.server.websocket;

import java.io.IOException;

public class GameThread extends Thread {
	
	public enum State {
		ACTIVED, PAUSED, WAITING;
	}
	
	private GameService service;
	private State state;
	private long sleep;
	
	public GameThread(GameService service, long sleep) {
		System.out.println("Création d'un thread de jeu...");
		this.service = service;
		this.state = State.PAUSED;
		this.sleep = sleep;
		super.start();
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
	 * Etre mis en arrière plan signifie que le modèle associé à le thread
	 * n'est actuellement pas observé par l'utilisateur. La rotation automatique peut
	 * donc être mis en pause en attendant que le modèle revienne au premier plan.
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
				service.sendToObservers(GameService.game.toJson());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
