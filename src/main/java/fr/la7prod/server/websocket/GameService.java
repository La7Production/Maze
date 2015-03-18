package fr.la7prod.server.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.la7prod.maze.MazeGame;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Direction;

public class GameService {
	
	public static MazeGame game = new MazeGame(10,10);
	
	public void addToGame(Session session) {
		game.addPlayer(session, new Player());
	}
	
	public Player removeToGame(Session session) {
		return game.removePlayer(session);
	}
	
	public Player getFromGame(Session session) {
		return game.getPlayer(session);
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
		session.getRemote().sendString(json.toString());
	}
	 
	public JSONObject slotsToJSON() {
		JSONObject json = new JSONObject();
		json.put("slots", game.countPlayers() + "/" + MazeGame.MAX_SLOTS);
		return json;
	}

}
