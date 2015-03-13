package fr.la7prod.server.websocket;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@WebServlet(name="MazeWebServlet", urlPatterns = { "/" })
public class MazeWebServlet extends WebSocketServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void service(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
		rs.getWriter().println("Accueil ma gueule !");
		RequestDispatcher disp = rq.getRequestDispatcher("/WEB-INF/index.jsp");
		disp.forward(rq, rs);
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(MazeWebSocket.class);
	}	

}