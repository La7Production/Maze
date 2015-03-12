package fr.la7prod.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
* Ressource User (accessible avec le chemin "/users")
*/
@Path("/users2")
public class UserResource {
	
	/*
	 * ----------
	 * IMPORTANT:
	 * ----------
	 * 
	 * Il est préférable d'utiliser @see UserDBIResource
	 * car elle met en place un système de base de données,
	 * donc de persistences des données contre les aléas du serveur
	 * comme son arrêt par exemple
	 */
	
	// Pour l'instant, on se contentera d'une variable statique pour conserver l'état
	private static Map<String, User> users = new HashMap<>();

	// L'annotation @Context permet de récupérer des informations sur le contexte d'exécution de la ressource.
	// Ici, on récupère les informations concernant l'URI de la requête HTTP, ce qui nous permettra de manipuler
	// les URI de manière générique.
	@Context
	public UriInfo uriInfo;

	/**
	* Une ressource doit avoir un contructeur (éventuellement sans arguments)
	*/
	public UserResource() {}

	/**
	* Méthode de création d'un utilisateur qui prend en charge les requêtes HTTP POST
	* La méthode renvoie l'URI de la nouvelle instance en cas de succès
	*
	* @param  user Instance d'utilisateur à créer
	* @return Response le corps de la réponse est vide, le code de retour HTTP est fixé à 201 si la création est faite
	*         L'en-tête contient un champs Location avec l'URI de la nouvelle ressource
	*/
	@POST
	public Response createUser(User user) {
		// Si l'utilisateur existe déjà, renvoyer 409
		if (users.containsKey(user.getLogin()) ) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		else {
			users.put(user.getLogin(), user);
			// On renvoie 201 et l'instance de la ressource dans le Header HTTP 'Location'
			URI instanceURI = uriInfo.getAbsolutePathBuilder().path(user.getLogin()).build();
			return Response.created(instanceURI).build();
		}
	}

	/**
	* Method prenant en charge les requêtes HTTP GET.
	*
	* @return Une liste d'utilisateurs
	*/
	@GET
	public List<User> getUsers() {
		return new ArrayList<User>(users.values());
	}

	/** 
	* Méthode prenant en charge les requêtes HTTP GET sur /users/{login}
	*
	* @return Une instance de User
	*/
	@GET
	@Path("{login}")
	@Produces("application/json,application/xml")
	public User getUser(@PathParam("login") String login) {
		// Si l'utilisateur est inconnu, on renvoie 404
		if (!users.containsKey(login)) {
			throw new NotFoundException();
		}
		else {
			return users.get(login);
		}
	}

	@DELETE
	@Path("{login}")
	public Response deleteUser(@PathParam("login") String login) {
		// Si l'utilisateur est inconnu, on renvoie 404
		if (!users.containsKey(login)) {
			throw new NotFoundException();
		}
		else {
			users.remove(login);
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}

	/** 
	* Méthode prenant en charge les requêtes HTTP DELETE sur /users/{login}
	*
	* @param login le login de l'utilisateur à modifier
	* @param user l'entité correspondant à la nouvelle instance
	* @return Un code de retour HTTP dans un objet Response
	*/
	@PUT
	@Path("{login}")
	public Response modifyUser(@PathParam("login") String login, User user) {
		// Si l'utilisateur est inconnu, on renvoie 404
		if (!users.containsKey(user.getLogin())) {
			throw new NotFoundException();
		}
		else {
			users.put(user.getLogin(), user);
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}

	/**
	* Méthode de création d'un utilisateur qui prend en charge les requêtes HTTP POST au format application/x-www-form-urlencoded
	* La méthode renvoie l'URI de la nouvelle instance en cas de succès
	*
	* @param login login de l'utilisateur
	* @param password mdp de l'utilisateur
	* @param firstname nom de l'utilisateur
	* @param lastname prenom de l'utilisateur
	* @param birthday date de naissance de l'utilisateur
	* @param email le mail de l'utilisateur
	* @return Response le corps de la réponse est vide, le code de retour HTTP est fixé à 201 si la création est faite
	*         L'en-tête contient un champs Location avec l'URI de la nouvelle ressource
	*/
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public Response createUser(@FormParam("login") String login, @FormParam("password") String password,
			@FormParam("firstname") String firstname, @FormParam("lastname") String lastname,
			@FormParam("birthday") String birthday, @FormParam("email") String email) {
		// Si l'utilisateur existe déjà, renvoyer 409
		if (users.containsKey(login)) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		else {
			users.put(login, new User(login, password, firstname, birthday, lastname, email));
			// On renvoie 201 et l'instance de la ressource dans le Header HTTP 'Location'
			URI instanceURI = uriInfo.getAbsolutePathBuilder().path(login).build();
			return Response.created(instanceURI).build();
		}
	}

}
