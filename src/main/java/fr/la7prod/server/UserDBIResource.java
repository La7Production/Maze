package fr.la7prod.server;

import java.net.URI;
import java.util.List;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
* Ressource User (accessible avec le chemin "/usersdb")
*/
@Path("/usersdb")
public class UserDBIResource {
	
	// L'annotation @Context permet de récupérer des informations sur le contexte d'exécution de la ressource.
	// Ici, on récupère les informations concernant l'URI de la requête HTTP, ce qui nous permettra de manipuler
	// les URI de manière générique.
	@Context
	public UriInfo uriInfo;

	/**
	* Une ressource doit avoir un contructeur (éventuellement sans arguments)
	*/
	public UserDBIResource() {}
	
	/**
	 * Ouvre l'accès vers la table des utilisateurs et offre sa manipulation
	 * au travers de méthodes au sens java
	 * @return un objet java permettant de manipuler la table des users
	 */
	private UserDAO openUserDAO() {
		return App.dbi.open(UserDAO.class);
	}

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
		UserDAO dao = openUserDAO();
		// Si l'utilisateur existe déjà, renvoyer 409
		if (dao.findByLogin(user.getLogin()) != null) {
			dao.close();
			return Response.status(Response.Status.CONFLICT).build();
		}
		else {
			dao.addUser(user);
			dao.close();
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
		UserDAO dao = openUserDAO();
		List<User> users = dao.getAllUsers();
		dao.close();
		return users;
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
		UserDAO dao = openUserDAO();
		User user = dao.findByLogin(login);
		dao.close();
		// Si l'utilisateur est inconnu, on renvoie 404
		if (user == null) {
			throw new NotFoundException();
		}
		else {
			return user;
		}
	}

	@DELETE
	@Path("{login}")
	public Response deleteUser(@PathParam("login") String login) {
		UserDAO dao = openUserDAO();
		User user = dao.findByLogin(login);
		// Si l'utilisateur est inconnu, on renvoie 404
		if (user == null) {
			dao.close();
			throw new NotFoundException();
		}
		else {
			dao.removeUser(user);
			dao.close();
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
		UserDAO dao = openUserDAO();
		// Si l'utilisateur est inconnu, on renvoie 404
		if (dao.findByLogin(login) == null) {
			dao.close();
			throw new NotFoundException();
		}
		else {
			dao.updateUser(user);
			dao.close();
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
		UserDAO dao = openUserDAO();
		// Si l'utilisateur existe déjà, renvoyer 409
		if (dao.findByLogin(login) != null) {
			dao.close();
			return Response.status(Response.Status.CONFLICT).build();
		}
		else {
			dao.addUser(new User(login, password, firstname, birthday, lastname, email));
			dao.close();
			// On renvoie 201 et l'instance de la ressource dans le Header HTTP 'Location'
			URI instanceURI = uriInfo.getAbsolutePathBuilder().path(login).build();
			return Response.created(instanceURI).build();
		}
	}

}
