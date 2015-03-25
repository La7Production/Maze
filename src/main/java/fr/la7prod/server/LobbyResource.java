package fr.la7prod.server;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import fr.la7prod.server.websocket.MazeServer;

/**
* Ressource MazeServer (accessible avec le chemin "/servers")
*/
@Path("/servers")
public class LobbyResource {
		
	private static Lobby lobby = new Lobby();
	
	public static Lobby getLobby() { return lobby; }
	
	static {
		lobby.add("Viath", 1);
		lobby.add("catteze", 4);
		//lobby.get("catteze").addPlayer(null, new Player("coucou"));
	}

	// L'annotation @Context permet de récupérer des informations sur le contexte d'exécution de la ressource.
	// Ici, on récupère les informations concernant l'URI de la requête HTTP, ce qui nous permettra de manipuler
	// les URI de manière générique.
	@Context
	public UriInfo uriInfo;

	/**
	* Une ressource doit avoir un contructeur (éventuellement sans arguments)
	*/
	public LobbyResource() {}

	/**
	* Méthode de création d'un utilisateur qui prend en charge les requêtes HTTP POST
	* La méthode renvoie l'URI de la nouvelle instance en cas de succès
	*
	* @param  user Instance d'utilisateur à créer
	* @return Response le corps de la réponse est vide, le code de retour HTTP est fixé à 201 si la création est faite
	*         L'en-tête contient un champs Location avec l'URI de la nouvelle ressource
	*/
	@POST
	public Response createServer(MazeServer server) {
		// Si le serveur de jeu existe déjà, renvoyer 409
		if (lobby.contains(server)) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		else {
			lobby.add(server);
			// On renvoie 201 et l'instance de la ressource dans le Header HTTP 'Location'
			URI instanceURI = uriInfo.getAbsolutePathBuilder().path(server.getTitle()).build();
			return Response.created(instanceURI).build();
		}
	}

	/**
	* Method prenant en charge les requêtes HTTP GET.
	*
	* @return Une liste d'utilisateurs
	*/
	@GET
	public Lobby getServers() {
		return lobby;
	}

	/** 
	* Méthode prenant en charge les requêtes HTTP GET sur /servers/{title}
	*
	* @return Une instance de MazeServer
	*/
	@GET
	@Path("{title}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public MazeServer getServer(@PathParam("title") String title) {
		// Si le serveur de jeu est inconnu, on renvoie 404
		if (!lobby.contains(title)) {
			throw new NotFoundException();
		}
		else {
			return lobby.get(title);
		}
	}

	@DELETE
	@Path("{title}")
	public Response deleteUser(@PathParam("title") String title) {
		// Si le serveur de jeu est inconnu, on renvoie 404
		if (!lobby.contains(title)) {
			throw new NotFoundException();
		}
		else {
			lobby.remove(title);
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}

	/**
	* Méthode de création d'un utilisateur qui prend en charge les requêtes HTTP POST au format application/x-www-form-urlencoded
	* La méthode renvoie l'URI de la nouvelle instance en cas de succès
	*
	* @param title titre du serveur
	* @param slots nombre de places dans le serveur
	* @return Response le corps de la réponse est vide, le code de retour HTTP est fixé à 201 si la création est faite
	*         L'en-tête contient un champs Location avec l'URI de la nouvelle ressource
	*/
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public Response createServer(@FormParam("title") String title, @FormParam("slots") String slots) {
		// Si le serveur de jeu existe déjà, renvoyer 409
		if (lobby.contains(title)) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		else {
			lobby.add(new MazeServer(title, Integer.parseInt(slots)));
			// On renvoie 201 et l'instance de la ressource dans le Header HTTP 'Location'
			URI instanceURI = uriInfo.getAbsolutePathBuilder().path(title).build();
			return Response.created(instanceURI).build();
		}
	}

}
