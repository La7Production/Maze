package fr.la7prod.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import fr.la7prod.maze.MazeServer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LobbyResourceTest extends JerseyTest {
	
	/**
	* Il est obligatoire de redéfinir cette méthode qui permet de configurer le contexte de Jersey
	*/
	@Override
	protected Application configure() {
		return new App();
	}

	/**
	* Vérifie qu'initialement on a une liste de serveurs de jeu vide
	*/
	@Test
	public void test_A_GetEmptyListofServer() {
		Lobby lobby = target("/servers").request().get(new GenericType<Lobby>(){});
		assertTrue(lobby.isEmpty());
	}

	/**
	* Test de création d'un serveur de jeu (retour HTTP et envoi de l'URI de la nouvelle instance)
	*/
	@Test
	public void test_B_CreateServer() {
		MazeServer server = new MazeServer("FirstServer", 7);
		
		// Conversion de l'instance de User au format JSON pour l'envoi
		Entity<MazeServer> userEntity = Entity.entity(server, MediaType.APPLICATION_JSON);

		// Envoi de la requête HTTP POST pour la création de l'serveur de jeu
		Response response = target("/servers").request().post(userEntity);

		// Vérification du code de retour HTTP
		assertEquals(201, response.getStatus());

		// Vérification que la création renvoie bien l'URI de la nouvelle instance dans le header HTTP 'Location'
		// ici : http://localhost:8080/servers/jsteed
		URI uriAttendue = target("/servers").path(server.getTitle()).getUri();
		assertTrue(uriAttendue.equals(response.getLocation()));
	}

	/**
	* Test de création en double d'un serveur de jeu. Doit renvoyer 409
	* ! Cela fonctionne car le test précédent à déjà créé le serveur de jeu et que le container est conservé !
	*/
	@Test
	public void test_C_CreateSameServer() {
		MazeServer server = new MazeServer("FirstServer", 7);
		Entity<MazeServer> userEntity = Entity.entity(server, MediaType.APPLICATION_JSON);
		int same = target("/servers").request().post(userEntity).getStatus();
		assertEquals(409, same);
	}
	
	/**
	* Vérifie que je renvoie bien une liste contenant tous les serveur de jeu (ici 2)
	*/
	@Test
	public void test_D_GetTwoServers() {
		MazeServer server = new MazeServer("SecondServer", 10);
		Entity<MazeServer> userEntity = Entity.entity(server, MediaType.APPLICATION_JSON);
		target("/servers").request().post(userEntity);
		Lobby lobby = target("/servers").request().get(new GenericType<Lobby>(){});
		assertEquals(2, lobby.size());
	}

	/**
	* Vérifie la récupération d'un serveur de jeu spécifique
	*/
	@Test
	public void test_E_GetOneServer() {
		MazeServer server = new MazeServer("FirstServer", 7);
		MazeServer result = target("/servers").path("FirstServer").request().get(MazeServer.class);
		assertEquals(server, result);
	}

	/**
	* Vérifie que la récupération d'un serveur de jeu inexistant renvoie 404
	*/
	@Test
	public void test_F_GetInexistantServer() {
		int notFound = target("/servers").path("ThirdServer").request().get().getStatus();
		assertEquals(404, notFound);
	}

	/**
	*
	* Vérifie que la suppression d'une ressource est effective
	*/
	@Test
	public void test_G_DeleteOneServer() {
		int code = target("/servers").path("FirstServer").request().delete().getStatus();
		assertEquals(204, code);
		int notFound = target("/servers").path("FirstServer").request().get().getStatus();
		assertEquals(404, notFound);    
	}
 
	/**
	*
	* Vérifie que la suppression d'un serveur de jeu inexistant renvoie 404
	*/
	@Test
	public void test_H_DeleteInexistantServer() {
		int notFound = target("/servers").path("FirstServer").request().delete().getStatus();
		assertEquals(404, notFound);
	}


	@Test
	public void test_K_CreateServerFromForm() {
		Form form = new Form();
		form.param("title", "L7PServ");
		form.param("slots", "7");

		Entity<Form> formEntity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
		int code = target("/servers").request().post(formEntity).getStatus(); 
		assertEquals(201, code);
	}


	/**
	* Vérifie qu'on récupère bien un serveur de jeu avec le type MIME application/xml
	*/
	@Test
	public void test_L_GetServerAsXml() { 
		int code = target("/servers").path("L7PServ").request(MediaType.APPLICATION_XML).get().getStatus();
		assertEquals(200, code);
	}
	
}
