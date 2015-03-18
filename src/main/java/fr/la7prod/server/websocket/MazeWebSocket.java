package fr.la7prod.server.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import fr.la7prod.maze.MazeGame;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Direction;

@WebSocket
public class MazeWebSocket {
	
	private static MazeGame game = new MazeGame(10,10);
	private static List<Session> sessions = new ArrayList<Session>();
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
		System.out.println("Success: " + sessions.remove(session));
	}
	
	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		sessions.add(session);
		JSONObject json = new JSONObject();
		json.put("motd", "Welcome in Maze");
		json.put("slots", sessions.size() + "/" + MazeGame.MAX_SLOTS);
		try {
			session.getRemote().sendString(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnWebSocketMessage
	public void onMessage(Session session, String message) throws IOException {
		JSONObject json = new JSONObject(message);
		JSONObject toSend = game.toJson();
		boolean success = false;
		
		if (json.has("playername")) {
			String name = json.getString("playername");
			Player p = game.getPlayer(name);
			
			if (p == null) {
				game.addPlayer(name, new Player(name));
				if (game.availableSlots() == 0) {
					game.start();
				}
			}
			else if (json.has("direction")) {
				success = game.movePerformed(p, Direction.valueOf(json.getString("direction")));
				if (success)
					p.incHaste();
				if (game.win(p)) {
					toSend.put("winner", p);
				}
			}
			//Player p = game.getPlayer(name);
			//if (game.isMazeMaster(p))
			session.getRemote().sendString(toSend.toString());
			//else
			//session.getRemote().sendString(game.toJsonFromPlayer(p).toString());
		}
		
		System.out.println("Message from " + session.getRemoteAddress() + ": " + json + " ; success: " + success + " ; exist :" + sessions.contains(session));
	}

}
