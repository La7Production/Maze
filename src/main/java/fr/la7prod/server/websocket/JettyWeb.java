package fr.la7prod.server.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.Maze;
import fr.la7prod.maze.algorithms.RecursiveBT;
import fr.la7prod.server.User;

@WebSocket
public class JettyWeb {
	
	private static Maze maze = new RecursiveBT().generate(10, 10, 0, 0);
	private static Map<User, Cell> position = new HashMap<User, Cell>();
	private static Set<Session> sessions = new HashSet<Session>();
	
	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
	}
	
	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		
		if (!sessions.contains(session)) {
			
			/*
			 * Ici on associera un utilisateur à une session
			 * pour éviter le vol d'identité.
			 * Les actions d'un utilisateur n'auront de conséquences sur la partie
			 * que si c'est la bonne personne qui envoie un message
			 */
			
			sessions.add(session);
			
		}
		
		System.out.println("Connect: " + session.getRemoteAddress().getAddress());
		try {
			session.getRemote().sendString(maze.toJson().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@OnWebSocketMessage
	public void onMessage(String message) {
		System.out.println("Simple Message: " + message);
		JSONObject json = new JSONObject();
		json.put("User", message);
		for (Session s : sessions) {
			try {
				s.getRemote().sendString(json.toString());
				s.getRemote().sendString(maze.toJson().toString());
				s.getRemote().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
