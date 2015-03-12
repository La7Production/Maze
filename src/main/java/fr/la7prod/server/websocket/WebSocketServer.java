package fr.la7prod.server.websocket;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import fr.la7prod.server.UserDBIResource;
import fr.la7prod.server.UserResource;


public class WebSocketServer {
	
	public static void main(String[] args) throws Exception {
		//Serveur rest
		URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
		ResourceConfig config = new ResourceConfig(UserDBIResource.class, UserResource.class);
		JdkHttpServerFactory.createHttpServer(baseUri, config, true);
		System.out.println("Server started");
		
		//Serveur websocket
		Server server = new Server(9876);
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(JettyWeb.class);
			}
		};
		server.setHandler(wsHandler);
		server.start();
		server.join();
		
		
	}

}
