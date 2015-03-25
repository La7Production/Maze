package fr.la7prod.server.websocket;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import fr.la7prod.maze.MazeWebSocket;

@WebServlet(name="MazeWebServlet", urlPatterns = { "/maze/websocket/*" })
public class MazeWebServlet extends WebSocketServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(MazeWebSocket.class);
	}	

}
