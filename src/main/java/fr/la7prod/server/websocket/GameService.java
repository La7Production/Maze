package fr.la7prod.server.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.entity.HumanEntity;
import fr.la7prod.maze.entity.Observer;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Direction;

public class GameService {
	
	protected MazeServer server;
	
	public GameService() {}
	
	public MazeServer getServer() {
		return this.server;
	}
	
	public void setServer(MazeServer server) {
		this.server = server;
	}
	
	public boolean isJSON(String message) {
		try {
			new JSONObject(message);
		} catch (JSONException e) {
			try {
				new JSONArray(message);
			} catch (JSONException e1) {
				return false;
			}
		}
		return true;
	}
	
	public Direction toDirection(String direction) {
		Direction d;
		try {
			d = Direction.valueOf(direction);
		} catch (Exception e) {
			return null;
		}
		return d;
	}
	
	public void send(Session session, JSONObject json) throws IOException {
		session.getRemote().sendStringByFuture(json.toString());
		session.getRemote().flush();
	}
	
	public void sendToPlayers(JSONObject json) throws IOException {
		for (Session s : server.getPlayerSessions()) {
			send(s, json);
		}
	}
	
	public void sendToObservers(JSONObject json) throws IOException {
		for (Session s : server.getObserverSessions()) {
			send(s, json);
		}
	}
	
	public void sendToAll(JSONObject json) throws IOException {
		for (Session s : server.getPlayerSessions()) {
			send(s, json);
		}
		for (Session s : server.getObserverSessions()) {
			send(s, json);
		}
	}
	
	public JSONObject parametersToJSON() {
		JSONObject json = new JSONObject();
		json.put("slots", server.countPlayers() + "/" + server.maxSlots());
		json.put("cellsize", Cell.PIXEL_SIZE);
		return json;
	}
	
	public HumanEntity addToGame(Session session, JSONObject json) {
		if (json.has("playername"))
			return server.addPlayer(session, new Player(json.getString("playername")));
		if (json.has("observer"))
			return server.addObserver(session, new Observer(json.getString("observer")));
		return null;
	}
	
	public HumanEntity removeFromGame(Session session) {
		HumanEntity e = server.removePlayer(session);
		return e != null ? e : server.removeObserver(session);
	}
	
	public HumanEntity getFromGame(Session session) {
		HumanEntity e = server.getPlayer(session);
		return e != null ? e : server.getObserver(session);
	}
	
	public int getAvailableSlots() {
		return server.availableSlots();
	}
	
	public void startGame(int width, int height) throws IOException {
		if (!server.isRunning()) {
			server.start(width, height);
			sendToPlayers(server.toJson());
		}
	}
	
	public void stopGame() {
		server.stop();
		for (Observer o : server.getObservers()) {
			o.closeListening();
		}
	}

}
