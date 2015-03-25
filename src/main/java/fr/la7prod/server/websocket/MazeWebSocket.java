package fr.la7prod.server.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
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
		System.out.println("Close: session=" + session.getRemoteAddress() + ", statusCode=" + statusCode + ", reason=" + reason);
		
		// Cas 1 : on supprime l'utilisateur du jeu
		removeFromGame(session);
		
		// Cas 2 : le serveur s'est arrêté ou s'est redémarré
		if (statusCode == StatusCode.SHUTDOWN || statusCode == StatusCode.SERVICE_RESTART || reason == null) {
			stopGame();
		}
		
		// Cas 3 : la déconnexion client/socket est normale
		else if (statusCode == StatusCode.NORMAL) {
			
			// Cas 3.1 : le jeu est en cours
			if (game.isRunning()) {
				if (game.countPlayers() == 0)
					stopGame();
				else
					sendToPlayers(game.toJson());	
			}
			// Cas 3.2 : la partie n'a pas encore commencée
			else {
				sendToPlayers(parametersToJSON());
			}
			
		}
	}
	
	@OnWebSocketError
	public void onError(Throwable t) {
		System.out.println("Error: " + t.getMessage());
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session) throws IOException {
		System.out.println("Connection from " + session.getRemoteAddress());
	}
	
	@OnWebSocketMessage
	public void onMessage(Session session, String message) throws IOException {
		System.out.println("Message from " + session.getRemoteAddress() + ": " + message);
		if (isJSON(message)) {
			
			// On récupère le message de l'utilisateur au format JSON
			JSONObject json = new JSONObject(message);
			
			// Cas 1 : l'utilisateur n'est pas encore reconnu par le jeu mais il l'informe de son identité
			if (getFromGame(session) == null && (json.has("playername") || json.has("observer"))) {
				
				// Cas 1.1 : l'utilisateur est un observer, on l'ajoute
				// Il fait automatiquement parti d'un thread qui envoie les données de jeu
				// à intervalle régulier
				if (json.has("observer")) {
					addToGame(session, json);
					send(session, parametersToJSON());
					return;
				}
				
				// Cas 1.2 : l'utilisateur se connecte au serveur alors que le nombre de slots disponible est à 0
				if (getAvailableSlots() == 0) {
					send(session, new JSONObject().put("error", "server is full :'("));
					return;
				}
				
				// Cas 1.3 : on ajoute l'utilisateur
				addToGame(session, json);
				
				// Cas 1.4 : tous les joueurs/observeurs reçoivent les paramètres de base du jeu actualisés
				sendToPlayers(parametersToJSON());
				
				// Cas 1.5 : c'est le dernier utilisateur attendu pour pouvoir lancer la partie
				// On commence la partie et on envoie les informations complètes du jeu
				if (getAvailableSlots() == 0)
					startGame(20,20);
			}
			
			// Cas 2 : l'utilisateur envoie une demande de direction
			if (getFromGame(session) instanceof Player && json.has("direction")) {
				
				// Cas 2.1 : on vérifie que la direction est correcte
				Player p = (Player) getFromGame(session);
				Direction d = toDirection(json.getString("direction"));
				
				if (d == null)
					return;
				
				// Cas 2.2 : on exécute le mouvement du joueur si il peut
				game.movePerformed(p, d);
				
				// Cas 2.3 : on regarde s'il a gagné
				if (game.win(p)) {
					sendToAll(game.toJson());
					game.stop();
					sendToPlayers(new JSONObject().put("winner", p.getName()));
				}
				
				// Cas 2.4 : le jeu est en cours, on envoie les données du jeu
				else if (game.isRunning()) {
					sendToPlayers(game.toJson());
				}
			}
		}
	}


}
