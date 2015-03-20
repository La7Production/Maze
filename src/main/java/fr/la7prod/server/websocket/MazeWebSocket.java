package fr.la7prod.server.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Direction;

@WebSocket
public class MazeWebSocket extends GameService {
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) throws IOException {
		System.out.println("Close: session=" + session.getRemoteAddress()
				+ ", statusCode=" + statusCode + ", reason=" + reason);
		System.out.println("Success: " + (removeToGame(session) != null));
		
		JSONObject data;
		
		if (game.isRunning()) {
			// La partie est en cours et un joueur se déconnecte
			// On renvoie les données du jeu à tous les autres
			data = game.toJson();	
		}
		else {
			// La partie n'est pas encore commencée
			// On renvoie les données des joueurs à tout le monde
			data = slotsToJSON();
		}
		
		// On notifie la déconnexion à tout le monde en renvoyant les données souhaitées
		for (Session s : game.getSessions()) {
			send(s, data);
		}
	}
	
	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session) throws IOException {
		System.out.println("Connection from " + session.getRemoteAddress());
		
		// On ajoute l'utilisateur à la partie via sa session
		// Un objet Player sera associé à sa session pendant toute la partie
		addToGame(session);
		JSONObject data = slotsToJSON();
		
		// Pour chaque utilisateur présent dans la partie
		// On envoit le nombre de joueurs connectés à chaque fois
		// qu'un nouveau joueur se connecte
		for (Session s : game.getSessions()) {
			send(s, data);
		}
		
		// On commence la partie lorsque tous les joueurs nécessaires sont connectés
		// On envoit donc le labyrinthe pour la première fois lorsque le dernier joueur
		// nécessaire se connecte.
		if (game.availableSlots() == 0) {
			game.initPlayers();
			game.start();
			data = game.toJson();
			for (Session s : game.getSessions()) {
				send(s, data);
			}
		}
	}
	
	@OnWebSocketMessage
	public void onMessage(Session session, String message) throws IOException {
		System.out.println("Message from " + session.getRemoteAddress() + ": " + message);
		
		// Traitement des informations uniquement si le message est en JSON
		if (isJSON(message)) {
			
			JSONObject data;
			JSONObject receive = new JSONObject(message);
			Player p = getFromGame(session);
			Direction d;
			int pixel;
			
			// L'utilisateur référence son pseudo au serveur de jeu
			// Il ne peut le référencé que s'il ne l'est pas déjà
			if (p.getName() == null) {
				if (receive.has("playername"))
					p.setName(receive.getString("playername"));
			}
			// L'utilisateur indique son intention de déplacer son personnage
			else {
				if (receive.has("direction") && receive.has("pixel")) {
					d = toDirection(receive.getString("direction"));
					pixel = receive.getInt("pixel");
					if (d != null) {
						// Si le joueur peut se déplacer
						// on effectue les instructions suivantes
						if (game.movePerformed(p, d, pixel)) {
							//p.incHaste();
						}
						
						// Si le joueur a gagné, on notifie tous les joueurs
						// et on arrête la partie
						if (game.win(p, pixel)) {
							game.stop();
							data = new JSONObject();
							data.put("winner", p.getName());
							for (Session s : game.getSessions()) {
								send(s, data);
							}
						}
					}
				}
				
				// A chaque fois qu'un message JSON a été envoyé au serveur
				// On considère que ce message est interprétable,
				// c'est à dire que le serveur peut l'utiliser pour le jeu.
				// Ainsi, on renvoie les données du jeu à la fin de toutes
				// les instructions, à tous les joueurs
				// Note: il faut que le joueur est son nom de référencé
				// pour que ses données soient traitées par le serveur
				data = game.toJson();
				for (Session s : game.getSessions()) {
					send(s, data);
				}
			}
		}
	}

}
