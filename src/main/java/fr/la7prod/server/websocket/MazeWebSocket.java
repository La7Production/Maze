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

import fr.la7prod.maze.MazeServer;
import fr.la7prod.maze.entity.Observer;
import fr.la7prod.maze.entity.Player;
import fr.la7prod.maze.util.Direction;
import fr.la7prod.server.LobbyResource;

@WebSocket
public class MazeWebSocket extends GameService {

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) throws IOException {
		System.out.println("Close: session=" + session.getRemoteAddress() + ", statusCode=" + statusCode + ", reason=" + reason);
		
		// Cas 0 : l'utilisateur se connecte à un MazeServer inconnu ou fait une erreur dans le nom
		if (statusCode == StatusCode.BAD_DATA)
			return;
		
		// Cas 1 : le client se déconnecte lui même sans envoyer de données au serveur
		if (statusCode == StatusCode.NO_CODE) {
			removeFromGame(session);
			checkGameStatus();
			return;
		}
		
		// Cas 2 : le serveur s'est arrêté ou s'est redémarré
		if (server.countPlayers() == 0 && (statusCode == StatusCode.SHUTDOWN || statusCode == StatusCode.SERVICE_RESTART)) {
			stopGame();
			return;
		}
		
		// Cas 3 : l'utilisateur quitte normalement
		if (statusCode == StatusCode.NORMAL) {
			
			// Cas 3.1 : l'utilisateur est un observeur
			if (getFromGame(session) instanceof Observer)
				((Observer)getFromGame(session)).closeListening();
			
			removeFromGame(session);
			
			// Cas 3.2 : la partie n'a pas encore commencée
			if (!checkGameStatus()) {
				sendToPlayers(parametersToJSON().toString());
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
		String s = session.getUpgradeRequest().getRequestURI().toString().replaceAll("%20", " ");
		if (s.lastIndexOf("/") == s.length())
			session.close(StatusCode.BAD_DATA, "Wrong name");
		else {
			s = s.substring(s.lastIndexOf("/") + 1);
			MazeServer server = LobbyResource.getLobby().get(s);
			if (server == null)
				session.close(StatusCode.BAD_DATA, "Unknown server " + s);
			else
				this.setServer(server);
		}
		
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
					send(session, parametersToJSON().toString());
					if (server.isRunning())
						send(session, server.toJsonString());
					((Observer)getFromGame(session)).openThread(session, server);
					((Observer)getFromGame(session)).startListening();
					return;
				}
				
				// Cas 1.2 : l'utilisateur se connecte au serveur alors que le nombre de slots disponible est à 0
				if (getAvailableSlots() == 0) {
					send(session, new JSONObject().put("error", "server is full :'(").toString());
					session.close(StatusCode.TRY_AGAIN_LATER, "Server is full");
					return;
				}
				
				// Cas 1.3 : on ajoute l'utilisateur
				Player p = (Player) addToGame(session, json);
				
				// Cas 1.4 : l'utilisateur rejoins une partie déjà commencée
				if (server.isRunning()) {
					send(session, parametersToJSON().toString());
					server.initPlayer(p);
					sendToPlayers(server.toJsonString());
				}
				else {
					// Cas 1.4 : tous les joueurs/observeurs reçoivent les paramètres de base du jeu actualisés
					sendToPlayers(parametersToJSON().toString());
					
					// Cas 1.5 : c'est le dernier utilisateur attendu pour pouvoir lancer la partie
					// On commence la partie et on envoie les informations complètes du jeu
					if (getAvailableSlots() == 0)
						startGame(25,25);
				}
			}
			
			// Cas 2 : l'utilisateur envoie une demande de direction
			if (server.isRunning() && getFromGame(session) instanceof Player && json.has("direction")) {
				
				// Cas 2.1 : on vérifie que la direction est correcte
				Player p = (Player) getFromGame(session);
				Direction d = toDirection(json.getString("direction"));
				
				if (d == null)
					return;
				
				// Cas 2.2 : on exécute le mouvement du joueur si il peut
				server.movePerformed(p, d);
				
				// Cas 2.3 : on regarde s'il a gagné
				if (server.win(p)) {
					sendToAll(server.toJsonString());
					sendToPlayers(new JSONObject().put("winner", p.getName()).toString());
					stopGame();
				}
				
				// Cas 2.4 : le jeu est en cours, on envoie les données du jeu
				else {
					sendToPlayers(server.toJsonString());
				}
			}
		}
	}


}
