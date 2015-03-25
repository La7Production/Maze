package fr.la7prod.server.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.MazeGame;
import fr.la7prod.maze.entity.HumanEntity;
import fr.la7prod.maze.entity.Observer;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Direction;

public class GameService {
	
	public static MazeGame game = new MazeGame(20,20,1);
	
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
	
	public void sendByTime(Session session, JSONObject json) throws IOException {
		
	}
	
	public void sendToAll(JSONObject json) throws IOException {
		for (Session s : game.getPlayerSessions()) {
			send(s, json);
		}
		for (Session s : game.getObserverSessions()) {
			sendByTime(s, json);
		}
	}
	
	public JSONObject parametersToJSON() {
		JSONObject json = new JSONObject();
		json.put("slots", game.countPlayers() + "/" + game.maxSlots());
		json.put("cellsize", Cell.PIXEL_SIZE);
		return json;
	}
	
	public HumanEntity addToGame(Session session, JSONObject json) {
		if (json.has("playername"))
			return game.addPlayer(session, new Player(json.getString("playername")));
		if (json.has("observer"))
			return game.addObserver(session, new Observer(json.getString("observer")));
		return null;
	}
	
	public HumanEntity removeFromGame(Session session) {
		HumanEntity e = game.removePlayer(session);
		return e != null ? e : game.removeObserver(session);
	}
	
	public HumanEntity getFromGame(Session session) {
		HumanEntity e = game.getPlayer(session);
		return e != null ? e : game.getObserver(session);
	}
	
	public int getAvailableSlots() {
		return game.availableSlots();
	}
	
	public void startGame() throws IOException {
		if (!game.isRunning()) {
			game.initPlayers();
			game.start();
			sendToAll(game.toJson());
		}
	}

}
