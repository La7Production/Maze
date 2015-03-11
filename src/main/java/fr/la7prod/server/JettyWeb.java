package fr.la7prod.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import fr.la7prod.maze.Cell;
import fr.la7prod.maze.Maze;
import fr.la7prod.maze.algorithms.RecursiveBT;

@WebSocket
public class JettyWeb {
	
	private static Maze maze = new RecursiveBT().generate(10, 10, 0, 0);
	private static Map<User, Cell> position = new HashMap<User, Cell>();
	
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
	}

}
